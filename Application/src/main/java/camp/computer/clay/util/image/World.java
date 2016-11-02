package camp.computer.clay.util.image;

import android.graphics.Canvas;

import camp.computer.clay.application.Application;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.system.EventHandlerSystem;
import camp.computer.clay.engine.system.BoundarySystem;
import camp.computer.clay.engine.system.CameraSystem;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Segment;

public class World {

    public static final double HOST_TO_EXTENSION_SHORT_DISTANCE = 400;
    public static final double HOST_TO_EXTENSION_LONG_DISTANCE = 550;

    public static double PIXEL_PER_MILLIMETER = 6.0;

    public static double NEARBY_RADIUS_THRESHOLD = 200 + 60;

    public Entity extensionPrototype = null;

    // Serves as a "prop" for user to define new Extensions
    public Entity createExtensionPrototype() {

        Entity prototypeExtension = new Entity();

        // extensionPrototype.addComponent(new Extension());
        prototypeExtension.addComponent(new Transform());

        Image image = new Image();
        Rectangle rectangle = new Rectangle(200, 200);
        rectangle.setColor("#fff7f7f7");
        image.addShape(rectangle);
        prototypeExtension.addComponent(image);
        image.invalidate();

        prototypeExtension.addComponent(new Label());
        prototypeExtension.getComponent(Label.class).setLabel("prototypeExtension");

        prototypeExtension.addComponent(new Visibility());
        prototypeExtension.addComponent(new Boundary());

        return prototypeExtension;
    }

//    public Visibility2 extensionPrototypeVisibility2 = Visibility2.INVISIBLE;
//    public Transform extensionPrototypePosition = new Transform();

    public Visibility2 pathPrototypeVisibility2 = Visibility2.INVISIBLE;
    public Transform pathPrototypeSourcePosition = new Transform(0, 0);
    public Transform pathPrototypeDestinationCoordinate = new Transform(0, 0);

    // <WORLD_SYSTEMS>
    public CameraSystem cameraSystem = new CameraSystem();
    public RenderSystem renderSystem = new RenderSystem();
    public BoundarySystem boundarySystem = new BoundarySystem();
    public InputSystem inputSystem = new InputSystem();
    public EventHandlerSystem eventHandlerSystem = new EventHandlerSystem();
    // </WORLD_SYSTEMS>

    public World() {
        super();
        setup();
    }

    private void setup() {
        // <TODO: DELETE>
        World.world = this;
        // </TODO: DELETE>

        this.extensionPrototype = createExtensionPrototype();
    }

    // <TODO: DELETE>
    private static World world = null;

    public static World getWorld() {
        return World.world;
    }
    // </TODO: DELETE>


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

        // Add Components
        host.addComponent(new Host()); // Unique to Host
        host.addComponent(new Portable()); // Add Portable Component (so can add Ports)
        host.addComponent(new Transform());
        host.addComponent(new Image());
        host.addComponent(new Boundary());
        host.addComponent(new Visibility());

        // Portable Component (Image Component depends on this)
        final int PORT_COUNT = 12;
        for (int j = 0; j < PORT_COUNT; j++) {

            Entity port = World.createEntity(Port.class);

            port.getComponent(Label.class).setLabel("Port " + (j + 1));
            port.getComponent(Port.class).setIndex(j);

            // <HACK>
            // TODO: Set default visibility of Ports some other way?
            port.getComponent(Visibility.class).isVisible = false;
            // </HACK>

            host.getComponent(Portable.class).addPort(port);
        }

        // Load geometry from file into Image Component
        // TODO: Application.getPlatform().restoreGeometry(this, "Geometry.json");
        Application.getView().restoreGeometry(host.getComponent(Image.class), "Geometry.json");

        // <HACK>
//        Group<Shape> shapes = host.getComponent(Image.class).getShapes();
//        for (int i = 0; i < shapes.size(); i++) {
//            if (shapes.get(i).getLabel().startsWith("Port")) {
//                String label = shapes.get(i).getLabel();
//                Entity portEntity = host.getComponent(Portable.class).getPort(label);
//                shapes.get(i).setEntity(portEntity);
//            }
//        }
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

        return host;
    }

    private static Entity createExtensionEntity() {

        // Create Entity
        Entity extension = new Entity();

        // Add Components
        extension.addComponent(new Extension()); // Unique to Extension
        extension.addComponent(new Portable());

        // <PORTABLE_COMPONENT>
        // Create Ports and add them to the ExtensionEntity
        int defaultPortCount = 1;
        for (int j = 0; j < defaultPortCount; j++) {

            Entity portEntity = World.createEntity(Port.class);

            portEntity.getComponent(Port.class).setIndex(j);
            extension.getComponent(Portable.class).addPort(portEntity);
        }
        // </PORTABLE_COMPONENT>

        // Add Components
        extension.addComponent(new Transform());
        extension.addComponent(new Image());
        extension.addComponent(new Boundary());
        extension.addComponent(new Visibility());

        // <LOAD_GEOMETRY_FROM_FILE>
        Rectangle rectangle;

        // Create Shapes for Image
        rectangle = new Rectangle(extension);
        rectangle.setWidth(200);
        rectangle.setHeight(200);
        rectangle.setLabel("Board");
        rectangle.setColor("#ff53BA5D"); // Gray: #f7f7f7, Greens: #32CD32
        rectangle.setOutlineThickness(0);
        extension.getComponent(Image.class).addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header");
        rectangle.setPosition(0, 107);
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        extension.getComponent(Image.class).addShape(rectangle);
        // </LOAD_GEOMETRY_FROM_FILE>

        // Load geometry from file into Image Component
        // TODO: Application.getPlatform().restoreGeometry(this, "Geometry.json");

        return extension;
    }

    private static Entity createPathEntity() {
        Entity path = new Entity();

        // Add Path Component (for type identification)
        path.addComponent(new Path());

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

        path.addComponent(new Transform());
        path.addComponent(pathImage); // Assign Image to Entity
        path.addComponent(new Boundary());
        path.addComponent(new Visibility());

        return path;
    }

    private static Entity createPortEntity() {

        Entity port = new Entity();

        // Add Components
        port.addComponent(new Port()); // Unique to Port
        port.addComponent(new Transform());
        port.addComponent(new Image());
        port.addComponent(new Boundary());
        port.addComponent(new Visibility());
        port.addComponent(new Label());

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

        return port;

    }

    private static Entity createCameraEntity() {

        Entity cameraEntity = new Entity();

        // Add Path Component (for type identification)
        cameraEntity.addComponent(new Camera());

        // Add Transform Component
        cameraEntity.addComponent(new Transform());

        return cameraEntity;
    }

    public void updateSystems(Canvas canvas) {
        world.inputSystem.update(world);
        world.eventHandlerSystem.update(world);
        world.boundarySystem.update(world);
        world.renderSystem.update(world, canvas); // TODO: Remove canvas!
        world.cameraSystem.update(world);
    }

    /**
     * Sorts {@code Image}s by layer.
     */
    public void updateLayers() {

        Group<Image> images = Entity.Manager.getImages();

        for (int i = 0; i < images.size() - 1; i++) {
            for (int j = i + 1; j < images.size(); j++) {
                // Check for out-of-order pairs, and swap them
                if (images.get(i).layerIndex > images.get(j).layerIndex) {
                    Image image = images.get(i);
                    images.set(i, images.get(j));
                    images.set(j, image);
                }
            }
        }

        /*
        // TODO: Sort using this after making Group implement List
        Collections.sort(Database.arrayList, new Comparator<MyObject>() {
            @Override
            public int compare(MyObject o1, MyObject o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        */
    }

    // TODO: Use base class's addImage() so Shapes are added to super.shapes. Then add an index instead of layers?

    /**
     * Automatically determines and assigns a valid position for all {@code HostEntity} {@code Image}s.
     */
    public void adjustLayout() {

//        Group<Image> hostImages = Entity.Manager.filterType2(HostEntity.class).getImages();
        Group<Image> hostImages = Entity.Manager.filterWithComponent(Host.class).getImages();

        // Set position on grid layout
        if (hostImages.size() == 1) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(0, 0);
        } else if (hostImages.size() == 2) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(-300, 0);
            hostImages.get(1).getEntity().getComponent(Transform.class).set(300, 0);
        } else if (hostImages.size() == 5) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(-300, -600);
            hostImages.get(0).getEntity().getComponent(Transform.class).setRotation(0);
            hostImages.get(1).getEntity().getComponent(Transform.class).set(300, -600);
            hostImages.get(1).getEntity().getComponent(Transform.class).setRotation(20);
            hostImages.get(2).getEntity().getComponent(Transform.class).set(-300, 0);
            hostImages.get(2).getEntity().getComponent(Transform.class).setRotation(40);
            hostImages.get(3).getEntity().getComponent(Transform.class).set(300, 0);
            hostImages.get(3).getEntity().getComponent(Transform.class).setRotation(60);
            hostImages.get(4).getEntity().getComponent(Transform.class).set(-300, 600);
            hostImages.get(4).getEntity().getComponent(Transform.class).setRotation(80);
        }

        // TODO: Set position on "scatter" layout

        // Set rotation
        // image.setRotation(Probability.getRandomGenerator().nextInt(360));
    }

    // TODO: Remove this! First don't extend Image on Shape (this class)? Make TouchableComponent?
    public Group<Shape> getShapes() {
        Group<Shape> shapes = new Group<>();
        Group<Image> images = Entity.Manager.getImages();
        for (int i = 0; i < images.size(); i++) {
            shapes.addAll(images.get(i).getShapes());
        }
        return shapes;
    }

    public Shape getShape(Entity entity) {
        Group<Image> images = Entity.Manager.getImages();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            Shape shape = image.getShape(entity);
            if (shape != null) {
                return shape;
            }
        }
        return null;
    }


    // <EXTENSION_PROTOTYPE>
    public void setPathPrototypeVisibility2(Visibility2 visibility2) {
        pathPrototypeVisibility2 = visibility2;
    }

    public Visibility2 getPathPrototypeVisibility2() {
        return pathPrototypeVisibility2;
    }

    public void setPathPrototypeSourcePosition(Transform position) {
        this.pathPrototypeSourcePosition.set(position);
    }

    public void setPathPrototypeDestinationPosition(Transform position) {
        this.pathPrototypeDestinationCoordinate.set(position);
    }

    public void setExtensionPrototypePosition(Transform position) {
//        this.extensionPrototypePosition.set(position);
        this.extensionPrototype.getComponent(Transform.class).set(position);

        // <REFACTOR>
        double pathRotationAngle = Geometry.getAngle(
                pathPrototypeSourcePosition,
                extensionPrototype.getComponent(Transform.class)
        );
        this.extensionPrototype.getComponent(Transform.class).setRotation(pathRotationAngle);
        // <REFACTOR>

        // <HACK>
        // TODO: Move! Needed, but should be in a better place so it doesn't have to be explicitly called!
        extensionPrototype.getComponent(Image.class).invalidate();
        this.boundarySystem.updateImage(extensionPrototype);
        // </HACK>
    }

    public void setExtensionPrototypeVisibility2(Visibility2 visibility2) {
//        extensionPrototypeVisibility2 = visibility2;
        if (visibility2 == Visibility2.INVISIBLE) {
            this.extensionPrototype.getComponent(Visibility.class).isVisible = false;
        } else if (visibility2 == Visibility2.VISIBLE) {
            this.extensionPrototype.getComponent(Visibility.class).isVisible = true;
        }
    }

    public Visibility2 getExtensionPrototypeVisibility2() {
//        return extensionPrototypeVisibility2;
        return extensionPrototype.getComponent(Visibility.class).isVisible == true ? Visibility2.VISIBLE : Visibility2.INVISIBLE;
    }
    // </EXTENSION_PROTOTYPE>


    public void hideAllPorts() {
        // TODO: getEntities().filterType2(PortEntity.class).getShapes().setVisibility(Visibility2.INVISIBLE);

//        Group<Image> portableImages = Entity.Manager.filterType2(HostEntity.class, ExtensionEntity.class).getImages();
//        Group<Image> portableImages = Entity.Manager.filterType2(HostEntity.class).getImages();
        Group<Image> portableImages = Entity.Manager.filterWithComponent(Host.class, Extension.class).getImages(); // HACK

//        ImageGroup portableImages = getImages(HostEntity.class, ExtensionEntity.class);
        for (int i = 0; i < portableImages.size(); i++) {
            Image portableImage = portableImages.get(i);
            Entity portableEntity = portableImage.getEntity();
//            portableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility2.INVISIBLE);
//            portableEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility2.INVISIBLE);
            portableEntity.getComponent(Portable.class).getPorts().setVisibility(false);
            portableEntity.getComponent(Portable.class).getPaths().setVisibility(false);
//            portableImage.setDockVisibility(Visibility2.VISIBLE);
            portableImage.setTransparency(1.0);
        }
    }


    // <TITLE>
    // TODO: Allow user to setAbsolute and change a goal. Track it in relation to the actions taken and things built.
    protected Visibility2 titleVisibility2 = Visibility2.INVISIBLE;
    protected String titleText = "Project";

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleVisibility2(Visibility2 visibility2) {
        if (titleVisibility2 == Visibility2.INVISIBLE && visibility2 == Visibility2.VISIBLE) {
//            Application.getPlatform().openTitleEditor(getTitleText());
            this.titleVisibility2 = visibility2;
        } else if (titleVisibility2 == Visibility2.VISIBLE && visibility2 == Visibility2.VISIBLE) {
//            Application.getPlatform().setTitleEditor(getTitleText());
        } else if (titleVisibility2 == Visibility2.VISIBLE && visibility2 == Visibility2.INVISIBLE) {
//            Application.getPlatform().closeTitleEditor();
            this.titleVisibility2 = visibility2;
        }
    }

    public Visibility2 getTitleVisibility2() {
        return this.titleVisibility2;
    }
    // </TITLE>
}
