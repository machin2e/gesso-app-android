package camp.computer.clay;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.ActionListenerComponent;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.host.DisplayHostInterface;
import camp.computer.clay.host.InternetInterface;
import camp.computer.clay.host.MessengerInterface;
import camp.computer.clay.engine.component.Actor;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.old_model.Cache;
import camp.computer.clay.old_model.Internet;
import camp.computer.clay.old_model.Messenger;
import camp.computer.clay.old_model.PhoneHost;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

public class Clay {

    private Messenger messenger = null;

    private Internet internet = null;

    private Cache cache = null;

    private Space space;

    // Group of discovered touchscreen phoneHosts
    private List<DisplayHostInterface> displays = new ArrayList<>();

    // Group of discovered phoneHosts
    private List<PhoneHost> phoneHosts = new ArrayList<>();

    private List<Profile> profiles = new ArrayList<>();

    public List<Profile> getProfiles() {
        return this.profiles;
    }

    public Clay() {

        this.cache = new Cache(this); // Set up cache

        this.messenger = new Messenger(this); // Start the messaging systems

        this.internet = new Internet(this); // Start the networking systems

        // Space
        this.space = new Space();
        space.setupActionListener();

        // Create Camera
        createEntity(Camera.class);

        // Create actor and setAbsolute perspective
        Actor actor = new Actor();
        this.space.addActor(actor);

        Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

        // CameraEntity
        cameraEntity.getComponent(Camera.class).setSpace(space);

        // Add actor to model
        space.addActor(actor);

        Application.getView().getPlatformRenderSurface().setSpace(space);

        // <TEST>
        createEntity(Host.class);
        createEntity(Host.class);
        createEntity(Host.class);
        createEntity(Host.class);
        createEntity(Host.class);
        // </TEST>

        // <HACK>
        Space.getSpace().adjustLayout();
        // </HACK>
    }

    private Clay getClay() {
        return this;
    }

    public Space getSpace() {
        return this.space;
    }

    public static Entity createEntity(Class<?> entityType) {
        if (entityType == Host.class) { // HACK (because Host is a Component)
            return createHostEntity();
        } else if (entityType == Extension.class) { // HACK (because Extension is a Component)
            return createExtensionEntity();
        } else if (entityType == Path.class) {
            return createPathEntity();
        } else if (entityType == Port.class) { // HACK (because Extension is a Component)
            return createPortEntity();
        } else if (entityType == Camera.class) {
            return createCameraEntity();
        } else {
            return null;
        }
    }

    /**
     * Adds a <em>virtual</em> {@code HostEntity} that can be configured and later assigned to a physical
     * host.
     */
    private static Entity createHostEntity() {

        // Create Entity
        Entity host = new Entity();

        // Add Extension Component (for type identification)
        host.addComponent(new Host());

        // <HACK>
        host.getComponent(Host.class).setupHeaderExtensions();
        // </HACK>

        // Add Components
        // Add Portable Component (so can add Ports)
        host.addComponent(new Portable());
        host.addComponent(new Transform());
        host.addComponent(new Image());

        // Portable Component (Image Component depends on this)
        final int PORT_COUNT = 12;
        for (int j = 0; j < PORT_COUNT; j++) {

            Entity port = Clay.createEntity(Port.class);

            port.setLabel("Port " + (j + 1));
            port.getComponent(Port.class).setIndex(j);

            host.getComponent(Portable.class).addPort(port);
        }

        // Load geometry from file into Image Component
        // TODO: Application.getView().restoreGeometry(this, "Geometry.json");
        Application.getView().restoreGeometry(host.getComponent(Image.class), "Geometry.json");

        // <HACK>
        Group<Shape> shapes = host.getComponent(Image.class).getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getLabel().startsWith("Port")) {
                String label = shapes.get(i).getLabel();
                Entity portEntity = host.getComponent(Portable.class).getPort(label);
                shapes.get(i).setEntity(portEntity);
            }
        }
        // </HACK>

        // Position Port Images
        Portable portable = host.getComponent(Portable.class);
        portable.getPort(0).getComponent(Transform.class).set(-19.0, 40.0);
        portable.getPort(1).getComponent(Transform.class).set(0, 40.0);
        portable.getPort(2).getComponent(Transform.class).set(19.0, 40.0);
        portable.getPort(3).getComponent(Transform.class).set(40.0, 19.0);
        portable.getPort(4).getComponent(Transform.class).set(40.0, 0.0);
        portable.getPort(5).getComponent(Transform.class).set(40.0, -19.0);
        portable.getPort(6).getComponent(Transform.class).set(19.0, -40.0);
        portable.getPort(7).getComponent(Transform.class).set(0, -40.0);
        portable.getPort(8).getComponent(Transform.class).set(-19.0, -40.0);
        portable.getPort(9).getComponent(Transform.class).set(-40.0, -19.0);
        portable.getPort(10).getComponent(Transform.class).set(-40.0, 0.0);
        portable.getPort(11).getComponent(Transform.class).set(-40.0, 19.0);
        for (int i = 0; i < portable.getPorts().size(); i++) {
            portable.getPort(i).getComponent(Transform.class).set(
                    portable.getPort(i).getComponent(Transform.class).x * 6.0,
                    portable.getPort(i).getComponent(Transform.class).y * 6.0
            );
        }

        // <HACK>
        Group<Shape> pinContactPoints = host.getComponent(Image.class).getShapes();
        for (int i = 0; i < pinContactPoints.size(); i++) {
            if (pinContactPoints.get(i).getLabel().startsWith("Pin")) {
                String label = pinContactPoints.get(i).getLabel();
//                Entity portEntity = hostEntity.getComponent(Portable.class).getPort(label);
//                pinContactPoints.get(i).setEntity(portEntity);
                Point contactPointShape = (Point) pinContactPoints.get(i);
                host.getComponent(Portable.class).headerContactPositions.add(contactPointShape);
            }
        }
        // </HACK>

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        hostEntity.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent();
        actionListener.setOnActionListener(getHostActionListener(host));
        host.addComponent(actionListener);
        // </HACK>

        return host;
    }

    private static Entity createExtensionEntity() {

        // Create Entity
        Entity extensionEntity = new Entity();

        // Add Extension Component (for type identification)
        extensionEntity.addComponent(new Extension());

        // Add Portable Component (so can add Ports)
        extensionEntity.addComponent(new Portable());

        // <PORTABLE_COMPONENT>
        // Create Ports and add them to the ExtensionEntity
        int defaultPortCount = 1;
        for (int j = 0; j < defaultPortCount; j++) {

            Entity portEntity = Clay.createEntity(Port.class);

            portEntity.getComponent(Port.class).setIndex(j);
            extensionEntity.getComponent(Portable.class).addPort(portEntity);
        }
        // </PORTABLE_COMPONENT>

        // Add Components
        extensionEntity.addComponent(new Transform());
        extensionEntity.addComponent(new Image());

        // <LOAD_GEOMETRY_FROM_FILE>
        Rectangle rectangle;

        // Create Shapes for Image
        rectangle = new Rectangle(extensionEntity);
        rectangle.setWidth(200);
        rectangle.setHeight(200);
        rectangle.setLabel("Board");
        rectangle.setColor("#ff53BA5D"); // Gray: #f7f7f7, Greens: #32CD32
        rectangle.setOutlineThickness(0);
        extensionEntity.getComponent(Image.class).addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header");
        rectangle.setPosition(0, 107);
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        extensionEntity.getComponent(Image.class).addShape(rectangle);
        // </LOAD_GEOMETRY_FROM_FILE>

        // Load geometry from file into Image Component
        // TODO: Application.getView().restoreGeometry(this, "Geometry.json");

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        extensionEntity.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent();
        actionListener.setOnActionListener(getExtensionActionListener(extensionEntity));
        extensionEntity.addComponent(actionListener);
        // </HACK>

        return extensionEntity;
    }

    private static Entity createPathEntity() {
        Entity pathEntity = new Entity();

        // Add Path Component (for type identification)
        pathEntity.addComponent(new Path());

        Image pathImage = new Image(); // Create PathEntity Image

        // <SETUP_PATH_IMAGE_GEOMETRY>
        Segment segment;

        // Board
        segment = new Segment<>();
        segment.setOutlineThickness(2.0);
        segment.setLabel("PathEntity");
        segment.setColor("#1f1f1e"); // #f7f7f7
        segment.setOutlineThickness(1);
        pathImage.addShape(segment);
        // </SETUP_PATH_IMAGE_GEOMETRY>

        pathEntity.addComponent(new Transform());
        pathEntity.addComponent(pathImage); // Assign Image to Entity

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        pathEntity.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent();
        actionListener.setOnActionListener(getPathActionListener(pathEntity));
        pathEntity.addComponent(actionListener);
        // </HACK>

        return pathEntity;
    }

    private static Entity createPortEntity() {

        Entity port = new Entity();

        // Add Components
        port.addComponent(new Port()); // Unique to Port
        port.addComponent(new Transform());
        port.addComponent(new Image());

        // <LOAD_GEOMETRY_FROM_FILE>
        Circle circle;

        // Create Shapes for Image
        circle = new Circle(port);
        circle.setRadius(50.0);
        circle.setLabel("Port"); // TODO: Give proper name...
        circle.setColor("#990000"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
        port.getComponent(Image.class).addShape(circle);
        // </LOAD_GEOMETRY_FROM_FILE>

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        hostEntity.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent();
        actionListener.setOnActionListener(getPortActionListener(port));
        port.addComponent(actionListener);
        // </HACK>

        return port;

    }

    private static Entity createCameraEntity() {

        Entity cameraEntity = new Entity();

        // Add Path Component (for type identification)
        cameraEntity.addComponent(new Camera());

        // Add Transform Component
        cameraEntity.addComponent(new Transform());

//        // <HACK>
//        // NOTE: This has to be done after adding an ImageComponent
////        pathEntity.setupActionListener();
//
//        ActionListenerComponent actionListener = new ActionListenerComponent(cameraEntity);
//        actionListener.setOnActionListener(getPathActionListener(cameraEntity));
//        cameraEntity.addComponent(actionListener);
//        // </HACK>

        return cameraEntity;
    }

    public static ActionListener getHostActionListener(final Entity hostEntity) {

        final Image hostImage = hostEntity.getComponent(Image.class);

        return new ActionListener() {
            @Override
            public void onAction(Action action) {

                final Event event = action.getLastEvent();

                final Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {

                    if (action.getFirstEvent().getTargetShape() == null) {
                        return;
                    }

                    if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                        if (action.isDragging()) {

                            // Update position of prototype ExtensionEntity
                            Space.getSpace().setExtensionPrototypePosition(event.getPosition());

//                            hostEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
                            hostEntity.getComponent(Portable.class).setPathVisibility(Visibility.INVISIBLE);

                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.VISIBLE);

                        } else if (action.isHolding()) {

                            // Update position of HostEntity image
                            hostEntity.getComponent(Transform.class).set(event.getPosition());

                            // CameraEntity
                            cameraEntity.getComponent(Camera.class).setFocus(hostEntity);

                        }

                    } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.isDragging()) {

                            // Prototype PathEntity Visibility
                            Space.getSpace().setPathPrototypeSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                            Space.getSpace().setPathPrototypeDestinationPosition(event.getPosition());
                            Space.getSpace().setPathPrototypeVisibility(Visibility.VISIBLE);

                            // Prototype ExtensionEntity Visibility
                            boolean isCreateExtensionAction = true;

                            // <HACK>
                            //Group<Image> imageGroup = Space.getSpace().getImages(HostEntity.class, ExtensionEntity.class);
//                            Group<Image> imageGroup = Entity.Manager.filterType2(HostEntity.class).getImages();
//                            imageGroup.addAll(Entity.Manager.filterWithComponent(Extension.class).getImages());
                            Group<Image> imageGroup = Entity.Manager.filterWithComponent(Host.class, Extension.class).getImages();
                            // </HACK>

                            for (int i = 0; i < imageGroup.size(); i++) {
                                Image otherImage = imageGroup.get(i);

                                // Update style of nearby Hosts
                                double distanceToHostImage = Geometry.distance(
                                        event.getPosition(),
                                        otherImage.getEntity().getComponent(Transform.class)
                                );

                                if (distanceToHostImage < 375) { // 375, 500
                                    isCreateExtensionAction = false;
                                    break;
                                }

                                // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                            }

                            if (isCreateExtensionAction) {
                                Space.getSpace().setExtensionPrototypeVisibility(Visibility.VISIBLE);
                                Space.getSpace().setPathPrototypeSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                                Space.getSpace().setExtensionPrototypePosition(event.getPosition());
                            } else {
                                Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);
                            }

                            // Show Ports of nearby Hosts and Extensions
                            Entity sourcePortEntity = action.getFirstEvent().getTargetShape().getEntity();
                            Event lastEvent = action.getLastEvent();

                            // Show Ports of nearby Hosts and Extensions
                            double nearbyRadiusThreshold = 200 + 60;
                            Group<Image> nearbyPortableImages = imageGroup.filterArea(lastEvent.getPosition(), nearbyRadiusThreshold);

                            for (int i = 0; i < imageGroup.size(); i++) {
                                Image portableImage = imageGroup.get(i);

                                //if (portableImage.getEntity() == sourcePortEntity.getComponent(Port.class).getPortable() || nearbyPortableImages.contains(portableImage)) {
                                if (portableImage.getEntity() == sourcePortEntity.getParent() || nearbyPortableImages.contains(portableImage)) {

//                                                        // <HACK>
                                    Image nearbyImage = portableImage;
                                    Entity nearbyPortableEntity = nearbyImage.getEntity();
                                    nearbyImage.setTransparency(1.0f);
//                                    nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);
                                    nearbyPortableEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.VISIBLE);

                                    // Add additional PortEntity to ExtensionEntity if it has no more available Ports
                                    Entity portableEntity = portableImage.getEntity();

                                    if (portableEntity.hasComponent(Extension.class)) { // HACK
                                        if (portableEntity.getComponent(Extension.class).getProfile() == null) {
                                            Entity extensionPortableEntity = portableImage.getEntity();

                                            boolean addPrototypePort = true;
                                            for (int j = 0; j < extensionPortableEntity.getComponent(Portable.class).getPorts().size(); j++) {
                                                Entity existingPortEntity = extensionPortableEntity.getComponent(Portable.class).getPorts().get(j);
                                                if (existingPortEntity.getComponent(Port.class).getType() == Port.Type.NONE) {
                                                    addPrototypePort = false;
                                                    break;
                                                }
                                            }

                                            if (addPrototypePort) {

                                                Entity portEntity = Clay.createEntity(Port.class);

                                                portEntity.getComponent(Port.class).setIndex(extensionPortableEntity.getComponent(Portable.class).getPorts().size());
                                                extensionPortableEntity.getComponent(Portable.class).addPort(portEntity);
                                            }
                                        }
                                    }

                                    // </HACK>

                                } else {

                                    Image nearbyImage = portableImage;
                                    Entity nearbyPortableEntity = portableImage.getEntity();
                                    nearbyImage.setTransparency(0.1f);
                                    //nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
                                    nearbyPortableEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.INVISIBLE);

                                }
                            }

                            // CameraEntity
                            cameraEntity.getComponent(Camera.class).setFocus(sourcePortEntity, event.getPosition());

                        } else if (action.isHolding()) {

//                                                // Holding and dragging

                        }

                    }

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // <HACK>
                    // TODO: Refactor so this doesn't have to be here! It's messy this way... standardize the way "null shapes" are handled
                    if (action.getFirstEvent().getTargetShape() == null) {
                        return;
                    }
                    // </HACK>

                    if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                        if (action.isTap()) {

                            // Focus on touched form
                            hostEntity.getComponent(Portable.class).setPathVisibility(Visibility.VISIBLE);

//                            hostEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);
                            hostEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.VISIBLE);
                            hostEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.VISIBLE);

                            hostImage.setTransparency(1.0);

                            // Show Ports and Paths of touched Host
                            for (int i = 0; i < hostEntity.getComponent(Portable.class).getPorts().size(); i++) {
                                Group<Entity> pathEntities = hostEntity.getComponent(Portable.class).getPort(i).getComponent(Port.class).getPaths();

                                for (int j = 0; j < pathEntities.size(); j++) {
                                    Entity pathEntity = pathEntities.get(j);

                                    // Show source and target Ports in Paths
                                    Space.getSpace().getShape(pathEntity.getComponent(Path.class).getSource()).setVisibility(Visibility.VISIBLE);
                                    Space.getSpace().getShape(pathEntity.getComponent(Path.class).getTarget()).setVisibility(Visibility.VISIBLE);

                                    // Show Path connection
                                    pathEntity.getComponent(Image.class).setVisibility(Visibility.VISIBLE);
                                }
                            }

                            // Camera
                            cameraEntity.getComponent(Camera.class).setFocus(hostEntity);

                            if (hostEntity.getComponent(Portable.class).getExtensions().size() > 0) {
//                                Space.getSpace().getImages(getHost().getExtensions()).setTransparency(1.0);
                                hostEntity.getComponent(Portable.class).getExtensions().setTransparency(0.1);

                                // <HACK>
                                // TODO: Replace ASAP. This is shit.
                                // TODO: Use "rectangle" or "circular" extension layout algorithms
                                hostEntity.getComponent(Host.class).setExtensionDistance(Space.HOST_TO_EXTENSION_LONG_DISTANCE);
                                // </HACK>
                            }

                            // Title
                            Space.getSpace().setTitleText("Host");
                            Space.getSpace().setTitleVisibility(Visibility.VISIBLE);

                        } else {

                            // TODO: Release longer than tap!

                            if (event.getTargetImage().getEntity().hasComponent(Host.class)) {

                                // If getFirstEvent queueEvent was on the same form, then respond
//                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage().getEntity() instanceof HostEntity) {
                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage().getEntity().hasComponent(Host.class)) {

                                    // HostEntity
//                                    event.getTargetImage().queueEvent(action);

                                    // CameraEntity
//                                    cameraEntity.setFocus();
                                }

                            } else if (event.getTargetImage() instanceof Space) {

                                // HostEntity
//                                action.getFirstEvent().getTargetImage().queueEvent(action);

                            }
                        }

                        // Check if connecting to a Extension
                        if (Space.getSpace().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

                            // Get cached extension profiles (and retrieve additional from Internet store)
                            List<Profile> profiles = Application.getView().getClay().getProfiles();


                            if (profiles.size() == 0) {

                                // Show "default" DIY extension builder (or info about there being no headerExtensions)

                            } else if (profiles.size() > 0) {

                                // Prompt User to select an ExtensionEntity from the Store
                                // i.e., Prompt to select extension to use! Then use that profile to create and configure portEntities for the extension.
                                Application.getView().getActionPrompts().promptSelection(profiles, new Prompt.OnActionListener<Profile>() {
                                    @Override
                                    public void onComplete(Profile profile) {

                                        // Add ExtensionEntity from Profile
                                        Entity extensionEntity = hostEntity.getComponent(Host.class).restoreExtension(profile, event.getPosition());

                                        // Update CameraEntity
                                        cameraEntity.getComponent(Camera.class).setFocus(extensionEntity);
                                    }
                                });
                                // Application.getView().promptTasks();
                            }
                        }

                    } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.getLastEvent().getTargetShape() != null && action.getLastEvent().getTargetShape().getLabel().startsWith("Port")) {

                            // (HostEntity.PortEntity, ..., HostEntity.PortEntity) Action Pattern

                            if (action.getFirstEvent().getTargetShape() == action.getLastEvent().getTargetShape() && action.isTap()) { // if (action.isTap()) {

                                // (HostEntity.PortEntity A, ..., HostEntity.PortEntity A) Action Pattern
                                // i.e., The action's first and last events address the same portEntity. Therefore, it must be either a tap or a hold.

                                // Get portEntity associated with the touched portEntity shape
                                Entity portEntity = action.getFirstEvent().getTargetShape().getEntity();
                                int portIndex = hostEntity.getComponent(Portable.class).getPorts().indexOf(portEntity);

                                Port portComponent = portEntity.getComponent(Port.class);

                                if (portComponent.getExtension() == null || portComponent.getExtension().getComponent(Extension.class).getProfile() == null) {

                                    if (portComponent.getType() == Port.Type.NONE) {

                                        // Set initial PortEntity Type

                                        Log.v("TouchPort", "-A");

                                        portComponent.setDirection(Port.Direction.INPUT);
                                        portComponent.setType(Port.Type.next(portComponent.getType()));

                                    } else if (!portComponent.hasPath()) {

                                        // Change PortEntity Type

                                        Log.v("TouchPort", "-B");

                                        Port.Type nextType = portComponent.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == portComponent.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }
                                        portComponent.setType(nextType);

                                    //} else if (hostEntity.getComponent(Portable.class).hasVisiblePaths(portIndex)) {
                                    } else if (hostEntity.getComponent(Portable.class).getPort(portIndex).getComponent(Port.class).hasVisiblePaths()) {

                                        // Change PathEntity Type. Updates each PortEntity in the PathEntity.

                                        Log.v("TouchPort", "-D");

                                        // Paths are being shown. Touching a portEntity changes the portEntity type. This will also
                                        // updates the corresponding path requirement.

                                        Port.Type nextType = portComponent.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == portComponent.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }

                                        // <FILTER>
                                        // TODO: Make Filter/Editor to pass to Group.filter(Filter) or Group.filter(Editor)
                                        Group<Entity> pathEntities = portComponent.getPaths();
                                        for (int i = 0; i < pathEntities.size(); i++) {
                                            Entity pathEntity = pathEntities.get(i);

                                            // <FILTER>
                                            // TODO: Make Filter/Editor
                                            Group<Entity> portEntities = pathEntity.getComponent(Path.class).getPorts();
                                            for (int j = 0; j < portEntities.size(); j++) {
                                                portEntities.get(j).getComponent(Port.class).setType(nextType);
                                            }
                                            // </FILTER>
                                        }
                                        // </FILTER>

                                    }

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);
                                }

                            } else if (action.getFirstEvent().getTargetShape() != action.getLastEvent().getTargetShape()) {

                                // (HostEntity.PortEntity A, ..., HostEntity.PortEntity B) Action Pattern
                                // i.e., The Action's first and last Events address different Ports.

                                Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                if (action.isDragging()) {

                                    Log.v("Events", "B.1");

                                    Entity sourcePortEntity = sourcePortShape.getEntity();
                                    Entity targetPortEntity = null;

                                    Shape targetPortShape = event.getTargetShape();
//                                            Space.getSpace()
//                                                    .getShapes(PortEntity.class)
//                                                    .remove(sourcePortShape)
//                                                    .filterContains(event.getPosition())
//                                                    .get(0);
//                                    Entity.Manager.filterWithComponent(Port.class).getImages().filter
                                    targetPortEntity = targetPortShape.getEntity();

                                    Log.v("Events", "D.1");

                                    // Create and configure new PathEntity
                                    Entity pathEntity = Clay.createEntity(Path.class);
                                    pathEntity.getComponent(Path.class).set(sourcePortEntity, targetPortEntity);

                                    cameraEntity.getComponent(Camera.class).setFocus(pathEntity.getComponent(Path.class).getExtension());

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);

                                }

                            }

                        } else if (action.getLastEvent().getTargetShape() == null
                                // TODO: && action.getLastEvent().getTargetImage().getLabel().startsWith("Space")) {
                                && action.getLastEvent().getTargetImage() == Space.getSpace()) {

                            // (HostEntity.PortEntity, ..., Space) Action Pattern

                            if (Space.getSpace().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                                Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();
                                Entity hostPortEntity = hostPortShape.getEntity();

                                // Create new ExtensionEntity from scratch (for manual configuration/construction)
                                Entity extensionEntity = hostEntity.getComponent(Host.class).createExtension(hostPortEntity, event.getPosition());

                                // Update CameraEntity
//                                cameraEntity.setFocus(extensionEntity);
                            }

                            // Update Image
                            Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);
                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

                        }
                    }
                }
            }
        };
    }

    public static ActionListener getExtensionActionListener(final Entity extensionEntity) {

        final Image extensionImage = extensionEntity.getComponent(Image.class);

        return new ActionListener() {
            @Override
            public void onAction(Action action) {

                Log.v("ExtensionImage", "onAction " + action.getLastEvent().getType());

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                    Log.v("ExtensionImage", "ExtensionImage.HOLD / createProfile()");
                    extensionEntity.getComponent(Portable.class).createProfile(extensionEntity);

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Previous Action targeted also this ExtensionEntity
                    // TODO: Refactor
                    if (action.getPrevious().getFirstEvent().getTargetImage().getEntity() == extensionImage.getEntity()) {

                        if (action.isTap()) {
                            // TODO: Replace with script editor/timeline
                            Application.getView().openActionEditor(extensionImage.getEntity());
                        }

                    } else {

                        if (action.isTap()) {

                            // Focus on touched base
                            extensionEntity.getComponent(Portable.class).setPathVisibility(Visibility.VISIBLE);
//                            extensionEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);
                            extensionEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.VISIBLE);
                            extensionImage.setTransparency(1.0);

                            // Show Ports and Paths for selected Host
//                            Group<Shape> portShapes = extensionEntity.getComponent(Portable.class).getPortShapes();
                            for (int i = 0; i < extensionEntity.getComponent(Portable.class).getPorts().size(); i++) {
//                                Shape portShape = portShapes.get(i);
                                Entity portEntity = extensionEntity.getComponent(Portable.class).getPorts().get(i);

                                Group<Entity> paths = portEntity.getComponent(Port.class).getPaths();
                                for (int j = 0; j < paths.size(); j++) {
                                    Entity path = paths.get(j);

                                    // Show Ports
                                    Space.getSpace().getShape(path.getComponent(Path.class).getSource()).setVisibility(Visibility.VISIBLE);
                                    Space.getSpace().getShape(path.getComponent(Path.class).getTarget()).setVisibility(Visibility.VISIBLE);
                                    Entity sourcePort = path.getComponent(Path.class).getSource();
                                    Entity targetPort = path.getComponent(Path.class).getTarget();
                                    sourcePort.getComponent(Image.class).setVisibility(Visibility.VISIBLE);
                                    targetPort.getComponent(Image.class).setVisibility(Visibility.VISIBLE);


                                    // Show Path
                                    path.getComponent(Image.class).setVisibility(Visibility.VISIBLE);
                                }
                            }
                            // TODO: Replace above with?: portEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.VISIBLE);

                            // CameraEntity
                            Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);
                            cameraEntity.getComponent(Camera.class).setFocus(extensionImage.getEntity());

                            // Title
                            Space.getSpace().setTitleText("ExtensionEntity");
                            Space.getSpace().setTitleVisibility(Visibility.VISIBLE);
                        }
                    }
                }
            }
        };
    }

    public static ActionListener getPortActionListener(final Entity portEntity) {

//        final Image hostImage = portEntity.getComponent(Image.class);

        return new ActionListener() {
            @Override
            public void onAction(Action action) {

                final Event event = action.getLastEvent();

                final Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {

                    if (action.getFirstEvent().getTargetShape() == null) {
                        return;
                    }

                    if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.isDragging()) {

                            // Prototype PathEntity Visibility
                            Space.getSpace().setPathPrototypeSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                            Space.getSpace().setPathPrototypeDestinationPosition(event.getPosition());
                            Space.getSpace().setPathPrototypeVisibility(Visibility.VISIBLE);

                            // Prototype ExtensionEntity Visibility
                            boolean isCreateExtensionAction = true;

                            // <HACK>
                            //Group<Image> imageGroup = Space.getSpace().getImages(HostEntity.class, ExtensionEntity.class);
//                            Group<Image> imageGroup = Entity.Manager.filterType2(HostEntity.class).getImages();
//                            imageGroup.addAll(Entity.Manager.filterWithComponent(Extension.class).getImages());
                            Group<Image> imageGroup = Entity.Manager.filterWithComponent(Host.class, Extension.class).getImages();
                            // </HACK>

                            for (int i = 0; i < imageGroup.size(); i++) {
                                Image otherImage = imageGroup.get(i);

                                // Update style of nearby Hosts
                                double distanceToHostImage = Geometry.distance(
                                        event.getPosition(),
                                        otherImage.getEntity().getComponent(Transform.class)
                                );

                                if (distanceToHostImage < 375) { // 375, 500
                                    isCreateExtensionAction = false;
                                    break;
                                }

                                // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                            }

                            if (isCreateExtensionAction) {
                                Space.getSpace().setExtensionPrototypeVisibility(Visibility.VISIBLE);
                                Space.getSpace().setPathPrototypeSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                                Space.getSpace().setExtensionPrototypePosition(event.getPosition());
                            } else {
                                Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);
                            }

                            // Show Ports of nearby Hosts and Extensions
                            Entity sourcePortEntity = action.getFirstEvent().getTargetShape().getEntity();
                            Event lastEvent = action.getLastEvent();

                            // Show Ports of nearby Hosts and Extensions
                            double nearbyRadiusThreshold = 200 + 60;
                            Group<Image> nearbyPortableImages = imageGroup.filterArea(lastEvent.getPosition(), nearbyRadiusThreshold);

                            for (int i = 0; i < imageGroup.size(); i++) {
                                Image portableImage = imageGroup.get(i);

                                //if (portableImage.getEntity() == sourcePortEntity.getComponent(Port.class).getPortable() || nearbyPortableImages.contains(portableImage)) {
                                if (portableImage.getEntity() == sourcePortEntity.getParent() || nearbyPortableImages.contains(portableImage)) {

//                                                        // <HACK>
                                    Image nearbyImage = portableImage;
                                    Entity nearbyPortableEntity = nearbyImage.getEntity();
                                    nearbyImage.setTransparency(1.0f);
//                                    nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);
                                    nearbyPortableEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.VISIBLE);

                                    // Add additional PortEntity to ExtensionEntity if it has no more available Ports
                                    Entity portableEntity = portableImage.getEntity();

                                    if (portableEntity.hasComponent(Extension.class)) { // HACK
                                        if (portableEntity.getComponent(Extension.class).getProfile() == null) {
                                            Entity extensionPortableEntity = portableImage.getEntity();

                                            boolean addPrototypePort = true;
                                            for (int j = 0; j < extensionPortableEntity.getComponent(Portable.class).getPorts().size(); j++) {
                                                Entity existingPortEntity = extensionPortableEntity.getComponent(Portable.class).getPorts().get(j);
                                                if (existingPortEntity.getComponent(Port.class).getType() == Port.Type.NONE) {
                                                    addPrototypePort = false;
                                                    break;
                                                }
                                            }

                                            if (addPrototypePort) {

                                                Entity portEntity = Clay.createEntity(Port.class);

                                                portEntity.getComponent(Port.class).setIndex(extensionPortableEntity.getComponent(Portable.class).getPorts().size());
                                                extensionPortableEntity.getComponent(Portable.class).addPort(portEntity);
                                            }
                                        }
                                    }

                                    // </HACK>

                                } else {

                                    Image nearbyImage = portableImage;
                                    Entity nearbyPortableEntity = portableImage.getEntity();
                                    nearbyImage.setTransparency(0.1f);
                                    //nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
                                    nearbyPortableEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.INVISIBLE);

                                }
                            }

                            // CameraEntity
                            cameraEntity.getComponent(Camera.class).setFocus(sourcePortEntity, event.getPosition());

                        } else if (action.isHolding()) {

//                                                // Holding and dragging

                        }

                    }

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // <HACK>
                    // TODO: Refactor so this doesn't have to be here! It's messy this way... standardize the way "null shapes" are handled
                    if (action.getFirstEvent().getTargetShape() == null) {
                        return;
                    }
                    // </HACK>

                    if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.getLastEvent().getTargetShape() != null && action.getLastEvent().getTargetShape().getLabel().startsWith("Port")) {

                            // (HostEntity.PortEntity, ..., HostEntity.PortEntity) Action Pattern

                            if (action.getFirstEvent().getTargetShape() == action.getLastEvent().getTargetShape() && action.isTap()) { // if (action.isTap()) {

                                // (HostEntity.PortEntity A, ..., HostEntity.PortEntity A) Action Pattern
                                // i.e., The action's first and last events address the same portEntity. Therefore, it must be either a tap or a hold.

                                // Get portEntity associated with the touched portEntity shape
                                Entity portEntity = action.getFirstEvent().getTargetShape().getEntity();
//                                int portIndex = portEntity.getComponent(Portable.class).getPorts().indexOf(portEntity);
                                int portIndex = portEntity.getComponent(Port.class).getIndex();

                                Port portComponent = portEntity.getComponent(Port.class);

                                if (portComponent.getExtension() == null || portComponent.getExtension().getComponent(Extension.class).getProfile() == null) {

                                    if (portComponent.getType() == Port.Type.NONE) {

                                        // Set initial PortEntity Type

                                        Log.v("TouchPort", "-A");

                                        portComponent.setDirection(Port.Direction.INPUT);
                                        portComponent.setType(Port.Type.next(portComponent.getType()));

                                    } else if (!portComponent.hasPath()) {

                                        // Change PortEntity Type

                                        Log.v("TouchPort", "-B");

                                        Port.Type nextType = portComponent.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == portComponent.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }
                                        portComponent.setType(nextType);

//                                    } else if (portEntity.getComponent(Portable.class).hasVisiblePaths(portIndex)) {
                                    } else if (portEntity.getComponent(Port.class).hasVisiblePaths()) {

                                        // Change PathEntity Type. Updates each PortEntity in the PathEntity.

                                        Log.v("TouchPort", "-D");

                                        // Paths are being shown. Touching a portEntity changes the portEntity type. This will also
                                        // updates the corresponding path requirement.

                                        Port.Type nextType = portComponent.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == portComponent.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }

                                        // <FILTER>
                                        // TODO: Make Filter/Editor to pass to Group.filter(Filter) or Group.filter(Editor)
                                        Group<Entity> pathEntities = portComponent.getPaths();
                                        for (int i = 0; i < pathEntities.size(); i++) {
                                            Entity pathEntity = pathEntities.get(i);

                                            // <FILTER>
                                            // TODO: Make Filter/Editor
                                            Group<Entity> portEntities = pathEntity.getComponent(Path.class).getPorts();
                                            for (int j = 0; j < portEntities.size(); j++) {
                                                portEntities.get(j).getComponent(Port.class).setType(nextType);
                                            }
                                            // </FILTER>
                                        }
                                        // </FILTER>

                                    }

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);
                                }

                            } else if (action.getFirstEvent().getTargetShape() != action.getLastEvent().getTargetShape()) {

                                // (HostEntity.PortEntity A, ..., HostEntity.PortEntity B) Action Pattern
                                // i.e., The Action's first and last Events address different Ports.

                                Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                if (action.isDragging()) {

                                    Log.v("Events", "B.1");

                                    Entity sourcePortEntity = sourcePortShape.getEntity();
                                    Entity targetPortEntity = null;

                                    Shape targetPortShape = event.getTargetShape();
//                                            Space.getSpace()
//                                                    .getShapes(PortEntity.class)
//                                                    .remove(sourcePortShape)
//                                                    .filterContains(event.getPosition())
//                                                    .get(0);
//                                    Entity.Manager.filterWithComponent(Port.class).getImages().filter
                                    targetPortEntity = targetPortShape.getEntity();

                                    Log.v("Events", "D.1");

                                    // Create and configure new PathEntity
                                    Entity pathEntity = Clay.createEntity(Path.class);
                                    pathEntity.getComponent(Path.class).set(sourcePortEntity, targetPortEntity);

                                    cameraEntity.getComponent(Camera.class).setFocus(pathEntity.getComponent(Path.class).getExtension());

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);

                                }

                            }

                        } else if (action.getLastEvent().getTargetShape() == null
                                // TODO: && action.getLastEvent().getTargetImage().getLabel().startsWith("Space")) {
                                && action.getLastEvent().getTargetImage() == Space.getSpace()) {

                            // (HostEntity.PortEntity, ..., Space) Action Pattern

                            if (Space.getSpace().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                                Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();
                                Entity hostPortEntity = hostPortShape.getEntity();

                                // Create new ExtensionEntity from scratch (for manual configuration/construction)
//                                Entity extensionEntity = portEntity.getComponent(Host.class).createExtension(hostPortEntity, event.getPosition());
                                Entity extensionEntity = portEntity.getParent().getComponent(Host.class).createExtension(hostPortEntity, event.getPosition());

                                // Update CameraEntity
//                                cameraEntity.setFocus(extensionEntity);
                            }

                            // Update Image
                            Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);
                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

                        }
                    }
                }
            }
        };
    }

    public static ActionListener getPathActionListener(Entity pathEntity) {
        return new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                }
            }
        };
    }

//    private static UUID createPathEntity(PortEntity sourcePort, PortEntity targetPort) {
//        PathEntity path = new PathEntity(sourcePort, targetPort);
//        PathImage pathImage = new PathImage(path); // Create PathEntity Image
//        path.addComponent(pathImage); // Assign Image to Entity
//
//        return path.getUuid();
//    }

    /*
     * Clay's essential operating system functions.
     */

    public void addHost(MessengerInterface messageManager) {
        this.messenger.addHost(messageManager);
    }

    public void addResource(InternetInterface networkResource) {
        this.internet.addHost(networkResource);
    }

    /*
     * Clay's infrastructure management functions.
     */

    /**
     * Adds a view to Clay. This makes the view available for use in systems built with Clay.
     *
     * @param view The view to make available to Clay.
     */
    public void addDisplay(DisplayHostInterface view) {
        this.displays.add(view);
    }

    /**
     * Returns the view manager the specified index.
     *
     * @param i The index of the view to return.
     * @return The view at the specified index.
     */
    public DisplayHostInterface getView(int i) {
        return this.displays.get(i);
    }

    public Cache getCache() {
        return this.cache;
    }

    public List<PhoneHost> getPhoneHosts() {
        return this.phoneHosts;
    }

    public boolean hasNetworkHost() {
        return this.internet != null;
    }

    // TODO: Create device profile. Add this to device profile. Change to getClay().getProfile().getInternetAddress()
    public String getInternetAddress() {
        Context context = Application.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v("Clay", "Internet address: " + ip);
        return ip;
    }

    public String getInternetBroadcastAddress() {
        String broadcastAddressString = getInternetAddress();
        Log.v("Clay", "Broadcast: " + broadcastAddressString);
        broadcastAddressString = broadcastAddressString.substring(0, broadcastAddressString.lastIndexOf("."));
        broadcastAddressString += ".255";
        return broadcastAddressString;
    }

    public PhoneHost getDeviceByAddress(String address) {
        for (PhoneHost phoneHost : getPhoneHosts()) {
            if (phoneHost.getInternetAddress().compareTo(address) == 0) {
                return phoneHost;
            }
        }
        return null;
    }

    /**
     * Adds the specified unit to Clay's operating model.
     */
    public PhoneHost addDevice(final UUID deviceUuid, final String internetAddress) {

        // Search for the phoneHost in the store
        if (hasDeviceByUuid(deviceUuid)) {
            return null;
        }

        createEntity(Host.class);

        return null;
    }

    public boolean hasDeviceByUuid(UUID uuid) {
        for (PhoneHost phoneHost : getPhoneHosts()) {
            if (phoneHost.getUuid().compareTo(uuid) == 0) {
                return true;
            }
        }
        return false;
    }

    public PhoneHost getDeviceByUuid(UUID uuid) {
        for (PhoneHost phoneHost : getPhoneHosts()) {
            if (phoneHost.getUuid().compareTo(uuid) == 0) {
                return phoneHost;
            }
        }
        return null;
    }

    public boolean hasDeviceByAddress(String address) {
        return false;
    }

    private boolean hasCache() {
        return this.cache != null;
    }

    /**
     * Cycle through routine operations.
     */
    public void update() {
        messenger.update();
    }
}
