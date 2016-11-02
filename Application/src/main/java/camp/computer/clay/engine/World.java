package camp.computer.clay.engine;

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
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.util.geometry.Shape;
import camp.computer.clay.util.image.Visibility2;

public class World {

    public static final double HOST_TO_EXTENSION_SHORT_DISTANCE = 400;
    public static final double HOST_TO_EXTENSION_LONG_DISTANCE = 550;

    public static double PIXEL_PER_MILLIMETER = 6.0;

    public static double NEARBY_RADIUS_THRESHOLD = 200 + 60;

    // <WORLD_SYSTEMS>
    public CameraSystem cameraSystem = new CameraSystem();
    public RenderSystem renderSystem = new RenderSystem();
    public BoundarySystem boundarySystem = new BoundarySystem();
    public InputSystem inputSystem = new InputSystem();
    public PortableLayoutSystem portableLayoutSystem = new PortableLayoutSystem();
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

//        this.pathPrototype = createPrototypePathEntity();
//        this.extensionPrototype = createPrototypeExtensionEntity();
        createPrototypePathEntity();
        createPrototypeExtensionEntity();
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
        //rectangle = new Rectangle(extension);
        rectangle = new Rectangle();
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
        // circle = new Circle(port);
        circle = new Circle();
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

    // TODO: Actually create and stage a real single-port Entity without a parent!?
    // Serves as a "prop" for user to define new Extensions
    public Entity createPrototypeExtensionEntity() {

        Entity prototypeExtension = new Entity();

        // extensionPrototype.addComponent(new Extension());
        prototypeExtension.addComponent(new Transform());

        Image image = new Image();
        Rectangle rectangle = new Rectangle(200, 200);
        rectangle.setColor("#fff7f7f7");
        rectangle.setOutlineThickness(0.0);
        image.addShape(rectangle);
        image.invalidate();
        prototypeExtension.addComponent(image);

        prototypeExtension.addComponent(new Label());
        prototypeExtension.getComponent(Label.class).setLabel("prototypeExtension");

        prototypeExtension.addComponent(new Visibility());
        prototypeExtension.getComponent(Visibility.class).isVisible = false;

        prototypeExtension.addComponent(new Boundary());

        return prototypeExtension;
    }

    public Entity createPrototypePathEntity() {

        Entity prototypePath = new Entity();

        // extensionPrototype.addComponent(new Extension());
        prototypePath.addComponent(new Transform());

        Image image = new Image();
        Segment segment = new Segment(new Transform(-50, -50), new Transform(50, 50));
        segment.setLabel("Path");
        segment.setOutlineColor("#ff333333");
        segment.setOutlineThickness(10.0);
        image.addShape(segment);
        image.invalidate();
        prototypePath.addComponent(image);

        prototypePath.addComponent(new Label());
        prototypePath.getComponent(Label.class).setLabel("prototypePath");

        prototypePath.addComponent(new Visibility());
        prototypePath.getComponent(Visibility.class).isVisible = false;

        prototypePath.addComponent(new Boundary());

        return prototypePath;
    }

    public void updateSystems(Canvas canvas) {
        world.inputSystem.update(world);
        world.eventHandlerSystem.update(world);
        world.boundarySystem.update(world);
        world.portableLayoutSystem.update(world);
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

    // <TITLE_UI_COMPONENT>
    // TODO: Allow user to setAbsolute and change a goal. Track it in relation to the actions taken and things built.
    protected Visibility2 titleVisibility2 = Visibility2.INVISIBLE;
    protected String titleText = "Project";

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleVisibility(Visibility2 visibility2) {
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

    public Visibility2 getTitleVisibility() {
        return this.titleVisibility2;
    }
    // </TITLE_UI_COMPONENT>
}
