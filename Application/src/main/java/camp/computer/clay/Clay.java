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
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Camera;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.PortableEntity;
import camp.computer.clay.host.DisplayHostInterface;
import camp.computer.clay.host.InternetInterface;
import camp.computer.clay.host.MessengerInterface;
import camp.computer.clay.engine.component.Actor;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.old_model.Cache;
import camp.computer.clay.old_model.Internet;
import camp.computer.clay.old_model.Messenger;
import camp.computer.clay.old_model.PhoneHost;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

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

        // Create actor and setAbsolute perspective
        Actor actor = new Actor(null);
        this.space.addActor(actor);

        // Camera
        actor.getCamera().setSpace(space);

        // Add actor to model
        space.addActor(actor);

        Application.getView().getDisplay().setSpace(space);

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

    public static UUID createEntity(Class<?> entityType) {
        if (entityType == Host.class) { // HACK (because Host is a Component)
            return createHostEntity();
        } else if (entityType == Extension.class) { // HACK (because Extension is a Component)
            return createExtensionEntity();
        } else if (entityType == Path.class) {
            return createPathEntity();
        } else {
            return null;
        }
    }

    /**
     * Adds a <em>virtual</em> {@code HostEntity} that can be configured and later assigned to a physical
     * host.
     */
    private static UUID createHostEntity() {

        // Create Entity
        Entity hostEntity = new Entity();

        // Add Extension Component (for type identification)
        hostEntity.setComponent(new Host(hostEntity));

        // <HACK>
        hostEntity.getComponent(Host.class).setupHeaderExtensions();
        // </HACK>

        // Add Portable Component (so can add Ports)
        hostEntity.setComponent(new Portable(hostEntity));

        // PortableEntity Component (Image Component depends on this)
        final int PORT_COUNT = 12;
        for (int j = 0; j < PORT_COUNT; j++) {
            Port port = new Port();
            port.setLabel("Port " + (j + 1));
            port.setIndex(j);
            hostEntity.getComponent(Portable.class).addPort(port);
        }

        // Add Transform Component
        hostEntity.setComponent(new Transform());

        // Add Image Component
        hostEntity.setComponent(new Image(hostEntity));

        // Load geometry from file into Image Component
        // TODO: Application.getView().restoreGeometry(this, "Geometry.json");
        Application.getView().restoreGeometry(hostEntity.getComponent(Image.class), "Geometry.json");

        // <HACK>
        Group<Shape> shapes = hostEntity.getComponent(Image.class).getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getLabel().startsWith("Port")) {
                String label = shapes.get(i).getLabel();
                Port port = hostEntity.getComponent(Portable.class).getPort(label);
                shapes.get(i).setEntity(port);
            }
        }
        // </HACK>

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        hostEntity.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent(hostEntity);
        actionListener.setOnActionListener(getHostActionListener(hostEntity));
        hostEntity.setComponent(actionListener);
        // </HACK>

        return hostEntity.getUuid();
    }

    private static UUID createExtensionEntity() {

        // Create Entity
        Entity extensionEntity = new Entity();

        // Add Extension Component (for type identification)
        extensionEntity.setComponent(new Extension(extensionEntity));

        // Add Portable Component (so can add Ports)
        extensionEntity.setComponent(new Portable(extensionEntity));

        // <PORTABLE_COMPONENT>
        // Create Ports and add them to the ExtensionEntity
        int defaultPortCount = 1;
        for (int j = 0; j < defaultPortCount; j++) {
            Port port = new Port();
            port.setIndex(j);
            extensionEntity.getComponent(Portable.class).addPort(port);
        }
        // </PORTABLE_COMPONENT>

        // Add Components
        extensionEntity.setComponent(new Transform());
        extensionEntity.setComponent(new Image(extensionEntity));

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

        ActionListenerComponent actionListener = new ActionListenerComponent(extensionEntity);
        actionListener.setOnActionListener(getExtensionActionListener(extensionEntity));
        extensionEntity.setComponent(actionListener);
        // </HACK>

        return extensionEntity.getUuid();
    }

    private static UUID createPathEntity() {
        Path path = new Path();

        Image pathImage = new Image(path); // Create Path Image

        // <SETUP_PATH_IMAGE_GEOMETRY>
        Segment segment;

        // Board
        segment = new Segment<>();
        segment.setOutlineThickness(2.0);
        segment.setLabel("Path");
        segment.setColor("#1f1f1e"); // #f7f7f7
        segment.setOutlineThickness(1);
        pathImage.addShape(segment);
        // </SETUP_PATH_IMAGE_GEOMETRY>

        path.setComponent(new Transform());
        path.setComponent(pathImage); // Assign Image to Entity

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        path.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent(path);
        actionListener.setOnActionListener(getPathActionListener(path));
        path.setComponent(actionListener);
        // </HACK>

        return path.getUuid();
    }

    public static ActionListener getHostActionListener(final Entity hostEntity) {

        final Image hostImage = hostEntity.getComponent(Image.class);

        return new ActionListener() {
            @Override
            public void onAction(Action action) {

                final Event event = action.getLastEvent();

                final Camera camera = event.getActor().getCamera();

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

                            hostEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
                            hostEntity.getComponent(Portable.class).setPathVisibility(Visibility.INVISIBLE);

                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.VISIBLE);

                        } else if (action.isHolding()) {

                            // Update position of HostEntity image
                            hostEntity.getComponent(Transform.class).set(event.getPosition());

                            // Camera
                            camera.setFocus(hostEntity);

                        }

                    } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.isDragging()) {

                            // Prototype Path Visibility
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

                                if (distanceToHostImage < 375) { // 500
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
                            Port sourcePort = (Port) action.getFirstEvent().getTargetShape().getEntity();
                            Event lastEvent = action.getLastEvent();

                            // Show Ports of nearby Hosts and Extensions
                            double nearbyRadiusThreshold = 200 + 60;
                            Group<Image> nearbyPortableImages = imageGroup.filterArea(lastEvent.getPosition(), nearbyRadiusThreshold);

                            for (int i = 0; i < imageGroup.size(); i++) {
                                Image portableImage = imageGroup.get(i);

                                if (portableImage.getEntity() == sourcePort.getPortable() || nearbyPortableImages.contains(portableImage)) {

//                                                        // <HACK>
                                    Image nearbyImage = portableImage;
                                    Entity nearbyPortableEntity = nearbyImage.getEntity();
                                    nearbyImage.setTransparency(1.0f);
                                    nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);

                                    // Add additional Port to ExtensionEntity if it has no more available Ports
                                    Entity portableEntity = portableImage.getEntity();

                                    if (portableEntity.hasComponent(Extension.class)) { // HACK
                                        if (portableEntity.getComponent(Extension.class).getProfile() == null) {
//                                            if (portableImage.getEntity() instanceof ExtensionEntity) {
                                            Entity extensionPortableEntity = portableImage.getEntity();

                                            boolean addPrototypePort = true;
                                            for (int j = 0; j < extensionPortableEntity.getComponent(Portable.class).getPorts().size(); j++) {
                                                Port existingPort = extensionPortableEntity.getComponent(Portable.class).getPorts().get(j);
                                                if (existingPort.getType() == Port.Type.NONE) {
                                                    addPrototypePort = false;
                                                    break;
                                                }
                                            }

                                            if (addPrototypePort) {
                                                Port port = new Port();
                                                port.setIndex(extensionPortableEntity.getComponent(Portable.class).getPorts().size());
                                                extensionPortableEntity.getComponent(Portable.class).addPort(port);
                                            }
//                                            }
                                        }
                                    }

                                    // </HACK>

                                } else {

                                    Image nearbyImage = portableImage;
                                    Entity nearbyPortableEntity = portableImage.getEntity();
                                    nearbyImage.setTransparency(0.1f);
                                    nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);

                                }
                            }

                            // Camera
                            camera.setFocus(sourcePort, event.getPosition());

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
                            hostEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);

                            hostImage.setTransparency(1.0);

                            // Show ports and paths of touched form
                            for (int i = 0; i < hostEntity.getComponent(Portable.class).getPorts().size(); i++) {
                                Group<Path> paths = hostEntity.getComponent(Portable.class).getPort(i).getPaths();

                                for (int j = 0; j < paths.size(); j++) {
                                    Path path = paths.get(j);

                                    // Show source and target ports in path
                                    Space.getSpace().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                    Space.getSpace().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                    // Show Path connection
                                    path.getComponent(Image.class).setVisibility(Visibility.VISIBLE);
                                }
                            }

                            // Camera
                            camera.setFocus(hostEntity);

                            if (hostEntity.getComponent(Portable.class).getExtensions().size() > 0) {
//                                                    Space.getSpace().getImages(getHost().getExtensions()).setTransparency(1.0);
                                hostEntity.getComponent(Portable.class).getExtensions().setTransparency(0.1);

                                // <HACK>
                                // TODO: Replace ASAP. This is shit.
                                // TODO: Use "rectangle" or "circular" extension layout algorithms
                                hostEntity.getComponent(Host.class).setExtensionDistance(500);
                                // </HACK>
                            }

                            // Title
                            Space.getSpace().setTitleText("HostEntity");
                            Space.getSpace().setTitleVisibility(Visibility.VISIBLE);

                        } else {

                            // TODO: Release longer than tap!

                            //if (event.getTargetImage().getEntity() instanceof HostEntity) {
                            if (event.getTargetImage().getEntity().hasComponent(Host.class)) {

                                // If getFirstEvent queueEvent was on the same form, then respond
//                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage().getEntity() instanceof HostEntity) {
                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage().getEntity().hasComponent(Host.class)) {

                                    // HostEntity
//                                                        event.getTargetImage().queueEvent(action);

                                    // Camera
//                                                        camera.setFocus();
                                }

                            } else if (event.getTargetImage() instanceof Space) {

                                // HostEntity
//                                                        action.getFirstEvent().getTargetImage().queueEvent(action);

                            }

                        }

                        // Check if connecting to a extension
                        if (Space.getSpace().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

                            // Get cached extension profiles (and retrieve additional from Internet store)
                            List<Profile> profiles = Application.getView().getClay().getProfiles();


                            if (profiles.size() == 0) {

                                // Show "default" DIY extension builder (or info about there being no headerExtensions)

                            } else if (profiles.size() > 0) {

                                // Prompt User to select an ExtensionEntity from the Store
                                // i.e., Prompt to select extension to use! Then use that profile to create and configure ports for the extension.
                                Application.getView().getActionPrompts().promptSelection(profiles, new Prompt.OnActionListener<Profile>() {
                                    @Override
                                    public void onComplete(Profile profile) {

                                        // Add ExtensionEntity from Profile
                                        Entity extensionEntity = hostEntity.getComponent(Host.class).restoreExtension(profile, event.getPosition());

                                        // Update Camera
                                        camera.setFocus(extensionEntity);
                                    }
                                });
                                // Application.getView().promptTasks();
                            }
                        }

                    } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.getLastEvent().getTargetShape() != null && action.getLastEvent().getTargetShape().getLabel().startsWith("Port")) {

                            // (HostEntity.Port, ..., HostEntity.Port) Action Pattern

                            if (action.getFirstEvent().getTargetShape() == action.getLastEvent().getTargetShape() && action.isTap()) { // if (action.isTap()) {

                                // (HostEntity.Port A, ..., HostEntity.Port A) Action Pattern
                                // i.e., The action's first and last events address the same port. Therefore, it must be either a tap or a hold.

                                // Get port associated with the touched port shape
                                Port port = (Port) action.getFirstEvent().getTargetShape().getEntity();
                                int portIndex = hostEntity.getComponent(Portable.class).getPorts().indexOf(port);

                                if (port.getExtension() == null || port.getExtension().getComponent(Extension.class).getProfile() == null) {

                                    if (port.getType() == Port.Type.NONE) {

                                        // Set initial Port Type

                                        Log.v("TouchPort", "-A");

                                        port.setDirection(Port.Direction.INPUT);
                                        port.setType(Port.Type.next(port.getType()));

                                    } else if (!port.hasPath()) {

                                        // Change Port Type

                                        Log.v("TouchPort", "-B");

                                        Port.Type nextType = port.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }
                                        port.setType(nextType);

                                    } else if (hostEntity.getComponent(Portable.class).hasVisiblePaths(portIndex)) {

                                        // Change Path Type. Updates each Port in the Path.

                                        Log.v("TouchPort", "-D");

                                        // Paths are being shown. Touching a port changes the port type. This will also
                                        // updates the corresponding path requirement.

                                        Port.Type nextType = port.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }

                                        // <FILTER>
                                        // TODO: Make Filter/Editor to pass to Group.filter(Filter) or Group.filter(Editor)
                                        Group<Path> paths = port.getPaths();
                                        for (int i = 0; i < paths.size(); i++) {
                                            Path path = paths.get(i);

                                            // <FILTER>
                                            // TODO: Make Filter/Editor
                                            Group<Port> ports = path.getPorts();
                                            for (int j = 0; j < ports.size(); j++) {
                                                ports.get(j).setType(nextType);
                                            }
                                            // </FILTER>
                                        }
                                        // </FILTER>

                                    }

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);
                                }

                            } else if (action.getFirstEvent().getTargetShape() != action.getLastEvent().getTargetShape()) {

                                // (HostEntity.Port A, ..., HostEntity.Port B) Action Pattern
                                // i.e., The Action's first and last Events address different Ports.

                                Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                if (action.isDragging()) {

                                    Log.v("Events", "B.1");

                                    Port sourcePort = (Port) sourcePortShape.getEntity();
                                    Port targetPort = null;

                                    Shape targetPortShape = Space.getSpace().getShapes(Port.class).remove(sourcePortShape).filterContains(event.getPosition()).get(0);
                                    targetPort = (Port) targetPortShape.getEntity();

                                    Log.v("Events", "D.1");

                                    // Create and configure new Path
                                    UUID pathUuid = Clay.createEntity(Path.class);
                                    Path path = (Path) Entity.getEntity(pathUuid);
                                    path.set(sourcePort, targetPort);

                                    event.getActor().getCamera().setFocus(path.getExtension());

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);

                                }

                            }

                        } else if (action.getLastEvent().getTargetShape() == null
                                // TODO: && action.getLastEvent().getTargetImage().getLabel().startsWith("Space")) {
                                && action.getLastEvent().getTargetImage() == Space.getSpace()) {

                            // (HostEntity.Port, ..., Space) Action Pattern

                            if (Space.getSpace().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                                Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();
                                Port hostPort = (Port) hostPortShape.getEntity();

                                // Create new ExtensionEntity from scratch (for manual configuration/construction)
                                Entity extensionEntity = hostEntity.getComponent(Host.class).createExtension(hostPort, event.getPosition());

                                // Update Camera
                                camera.setFocus(extensionEntity);
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
                            extensionEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);
                            extensionImage.setTransparency(1.0);

                            // Show ports and paths of touched form
                            ShapeGroup portShapes = extensionEntity.getComponent(Portable.class).getPortShapes();
                            for (int i = 0; i < portShapes.size(); i++) {
                                Shape portShape = portShapes.get(i);
                                Port port = (Port) portShape.getEntity();

                                Group<Path> paths = port.getPaths();
                                for (int j = 0; j < paths.size(); j++) {
                                    Path path = paths.get(j);

                                    // Show ports
                                    Space.getSpace().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                    Space.getSpace().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                    // Show path
                                    path.getComponent(Image.class).setVisibility(Visibility.VISIBLE);
                                }
                            }

                            // Camera
                            event.getActor().getCamera().setFocus(extensionImage.getEntity());

                            // Title
                            Space.getSpace().setTitleText("ExtensionEntity");
                            Space.getSpace().setTitleVisibility(Visibility.VISIBLE);
                        }
                    }
                }
            }
        };
    }

    public static ActionListener getPathActionListener(Path path) {
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

//    private static UUID createPathEntity(Port sourcePort, Port targetPort) {
//        Path path = new Path(sourcePort, targetPort);
//        PathImage pathImage = new PathImage(path); // Create Path Image
//        path.setComponent(pathImage); // Assign Image to Entity
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
