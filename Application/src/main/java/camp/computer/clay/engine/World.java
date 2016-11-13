package camp.computer.clay.engine;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Manager;
import camp.computer.clay.engine.system.BoundarySystem;
import camp.computer.clay.engine.system.CameraSystem;
import camp.computer.clay.engine.system.EventHandlerSystem;
import camp.computer.clay.engine.system.ImageSystem;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.model.Repository;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.model.player.Player;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.controls.NativeUi;
import camp.computer.clay.util.ImageBuilder.Circle;
import camp.computer.clay.util.ImageBuilder.ImageBuilder;
import camp.computer.clay.util.ImageBuilder.Point;
import camp.computer.clay.util.ImageBuilder.Rectangle;
import camp.computer.clay.util.ImageBuilder.Segment;
import camp.computer.clay.util.ImageBuilder.Shape;
import camp.computer.clay.util.time.Clock;

public class World {

    public static final double HOST_TO_EXTENSION_SHORT_DISTANCE = 325;
    public static final double HOST_TO_EXTENSION_LONG_DISTANCE = 550;

    public static double PIXEL_PER_MILLIMETER = 6.0;

    public static double NEARBY_RADIUS_THRESHOLD = 200 + 60;

    // <SETTINGS>
    public static boolean ENABLE_DRAW_OVERLAY = true;
    // </SETTINGS>

    // <TEMPORARY>
    public Repository repository = new Repository();
    // </TEMPORARY>

    // <MANAGERS>
    public camp.computer.clay.engine.manager.Manager Manager;
    // </MANAGERS>

    // <WORLD_SYSTEMS>
    public CameraSystem cameraSystem = new CameraSystem(this);
    public ImageSystem imageSystem = new ImageSystem(this);
    public RenderSystem renderSystem = new RenderSystem(this);
    public BoundarySystem boundarySystem = new BoundarySystem(this);
    public InputSystem inputSystem = new InputSystem(this);
    public PortableLayoutSystem portableLayoutSystem = new PortableLayoutSystem(this);
    public EventHandlerSystem eventHandlerSystem = new EventHandlerSystem(this);
    // </WORLD_SYSTEMS>

    public World() {
        super();
        setup();
    }

    private void setup() {
        // <TODO: DELETE>
        World.world = this;
        // </TODO: DELETE>

        Manager = new Manager();

        createPrototypePathEntity();
        createPrototypeExtensionEntity();

        // <TEMPORARY>
        repository.populateTestData();
        // </TEMPORARY>
    }

    // <TODO: DELETE>
    private static World world = null;

    public static World getWorld() {
        return World.world;
    }
    // </TODO: DELETE>

//    public boolean addSystem(System system) {
//
//    }
//
//    public System getSystem(Class<?> systemType) {
//
//    }

    public Entity createEntity(Class<?> entityType) {

        Entity entity = null;

        if (entityType == Host.class) { // HACK (because Host is a Component)
            entity = createHostEntity();
        } else if (entityType == Extension.class) { // HACK (because Extension is a Component)
            entity = createExtensionEntity();
        } else if (entityType == Path.class) {
            entity = createPathEntity();
        } else if (entityType == Port.class) { // HACK (because Extension is a Component)
            entity = createPortEntity();
        } else if (entityType == Camera.class) {
            entity = createCameraEntity();
        } else if (entityType == Player.class) {
            entity = createPlayerEntity();
        }

        // Add Entity to Manager
        Manager.add(entity);

        return entity;
    }

    private Entity createPlayerEntity() {

        // Create Entity
        Entity player = new Entity();

        // Add Components
        player.addComponent(new Player()); // Unique to Player
        player.addComponent(new Transform());

        return player;
    }

    /**
     * Adds a <em>virtual</em> {@code HostEntity} that can be configured and later assigned to a physical
     * host.
     */
    private Entity createHostEntity() {

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

            Entity port = createEntity(Port.class);

            port.getComponent(Label.class).setLabel("Port " + (j + 1));
            Port.setIndex(port, j);

            // <HACK>
            // TODO: Set default visibility of Ports some other way?
            port.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);
            // </HACK>

            Portable.addPort(host, port);
        }

        // Load geometry from file into Image Component
        // TODO: Application.getPlatform().openFile(this, "Host.json");
//        Application.getView().restoreGeometry(host.getComponent(Image.class), "Host.json");
        InputStream inputStream = null;
        try {
            inputStream = Application.getContext().getAssets().open("Host.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageBuilder imageBuilder = ImageBuilder.open(inputStream);
        host.getComponent(Image.class).setImage(imageBuilder);

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
        Portable.getPort(host, 0).getComponent(Transform.class).set(-19.0, 40.0);
        Portable.getPort(host, 1).getComponent(Transform.class).set(0, 40.0);
        Portable.getPort(host, 2).getComponent(Transform.class).set(19.0, 40.0);
        Portable.getPort(host, 3).getComponent(Transform.class).set(40.0, 19.0);
        Portable.getPort(host, 4).getComponent(Transform.class).set(40.0, 0.0);
        Portable.getPort(host, 5).getComponent(Transform.class).set(40.0, -19.0);
        Portable.getPort(host, 6).getComponent(Transform.class).set(19.0, -40.0);
        Portable.getPort(host, 7).getComponent(Transform.class).set(0, -40.0);
        Portable.getPort(host, 8).getComponent(Transform.class).set(-19.0, -40.0);
        Portable.getPort(host, 9).getComponent(Transform.class).set(-40.0, -19.0);
        Portable.getPort(host, 10).getComponent(Transform.class).set(-40.0, 0.0);
        Portable.getPort(host, 11).getComponent(Transform.class).set(-40.0, 19.0);
        for (int i = 0; i < Portable.getPorts(host).size(); i++) {
            Portable.getPort(host, i).getComponent(Transform.class).set(
                    Portable.getPort(host, i).getComponent(Transform.class).x * 6.0,
                    Portable.getPort(host, i).getComponent(Transform.class).y * 6.0
            );
        }
        // Add relative layout constraints
        for (int i = 0; i < Portable.getPorts(host).size(); i++) {
            Entity port = Portable.getPort(host, i);
            port.addComponent(new RelativeLayoutConstraint());
            port.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(host);
        }

        // <HACK>
        List<Shape> pinContactPoints = host.getComponent(Image.class).getImage().getShapes();
        for (int i = 0; i < pinContactPoints.size(); i++) {
            if (pinContactPoints.get(i).getLabel().startsWith("Pin")) {
                String label = pinContactPoints.get(i).getLabel();
                Point contactPointShape = (Point) pinContactPoints.get(i);
                host.getComponent(Portable.class).headerContactPositions.add(contactPointShape);
            }
        }
        // </HACK>

        return host;
    }

    private Entity createExtensionEntity() {

        // Create Entity
        Entity extension = new Entity();

        // Add Components
        extension.addComponent(new Extension()); // Unique to Extension
        extension.addComponent(new Portable());

        // <PORTABLE_COMPONENT>
        // Create Ports and add them to the Extension
        int defaultPortCount = 1;
        for (int j = 0; j < defaultPortCount; j++) {

            Entity port = createEntity(Port.class);

            Port.setIndex(port, j);
            Portable.addPort(extension, port);
        }
        // Add relative layout constraints
        Portable portable = extension.getComponent(Portable.class);
        for (int i = 0; i < Portable.getPorts(extension).size(); i++) {
            Entity port = Portable.getPort(extension, i);
            port.addComponent(new RelativeLayoutConstraint());
            port.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(extension);
        }
        // </PORTABLE_COMPONENT>

        // Add Components
        extension.addComponent(new Transform());
        extension.addComponent(new Image());
        extension.addComponent(new Boundary());
        extension.addComponent(new Visibility());

        // <LOAD_GEOMETRY_FROM_FILE>
        ImageBuilder imageBuilder = new ImageBuilder();

        Rectangle rectangle;

        // Create Shapes for Image
        rectangle = new Rectangle();
        rectangle.setWidth(200);
        rectangle.setHeight(200);
        rectangle.setLabel("Board");
        rectangle.setColor("#ff53BA5D"); // Gray: #f7f7f7, Greens: #32CD32
        rectangle.setOutlineThickness(0);
        // TODO: Create BuilderImages with geometry when initializing entity with BuildingImage!
//        extension.getComponent(Image.class).addShape(rectangle);
        rectangle.isBoundary = true;
        imageBuilder.addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header");
        rectangle.setPosition(0, 107);
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
//        extension.getComponent(Image.class).addShape(rectangle);
        imageBuilder.addShape(rectangle);

        extension.getComponent(Image.class).setImage(imageBuilder);
        // </LOAD_GEOMETRY_FROM_FILE>

        // Load geometry from file into Image Component
        // TODO: Application.getPlatform().openFile(this, "Host.json");

        return extension;
    }

    private Entity createPathEntity() {
        Entity path = new Entity();

        // Add Path Component (for type identification)
        path.addComponent(new Path()); // Unique to Path
        path.addComponent(new Transform());
        path.addComponent(new Image());
        path.addComponent(new Boundary());
        path.addComponent(new Visibility());

        // <SETUP_PATH_IMAGE_GEOMETRY>
        ImageBuilder imageBuilder = new ImageBuilder();

        // Board
        Segment segment = new Segment();
        segment.setOutlineThickness(2.0);
        segment.setLabel("Path");
        segment.setColor("#1f1f1e"); // #f7f7f7
        segment.setOutlineThickness(1);
        imageBuilder.addShape(segment);

        Circle circle = new Circle();
        circle.setRadius(50.0);
        circle.setLabel("Source Port"); // TODO: Give proper name...
        circle.setColor("#990000"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
        circle.isBoundary = true;
        imageBuilder.addShape(circle);

        circle = new Circle();
        circle.setRadius(50.0);
        circle.setLabel("Target Port"); // TODO: Give proper name...
        circle.setColor("#990000"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
        circle.isBoundary = true;
        imageBuilder.addShape(circle);

        // TODO: 11/5/2016 Add Port circles to the Path? So moving paths around will be easier? Then Port images are always just the same color. They look different because of the Path image. Path can contain single node. Then can be stretched out to include another Port.
        // TODO: 11/5/2016 Create corresponding world state CREATING_PATH, MODIFYING_PATH/MOVING_PATH, etc.

        path.getComponent(Image.class).setImage(imageBuilder);
        path.getComponent(Image.class).layerIndex = 10;
        // </SETUP_PATH_IMAGE_GEOMETRY>

        return path;
    }

    private Entity createPortEntity() {

        Entity port = new Entity();

        // Add Components
        port.addComponent(new Port()); // Unique to Port
        port.addComponent(new Transform());
        port.addComponent(new Image());
        port.addComponent(new Boundary());
        port.addComponent(new Visibility());
        port.addComponent(new Label());

        // <LOAD_GEOMETRY_FROM_FILE>
        ImageBuilder imageBuilder = new ImageBuilder();

        // Create Shapes for Image
        Circle circle = new Circle();
        circle.setRadius(50.0);
        circle.setLabel("Port"); // TODO: Give proper name...
        circle.setColor("#f7f7f7"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
        circle.isBoundary = true;
        imageBuilder.addShape(circle);

        port.getComponent(Image.class).setImage(imageBuilder);
        // </LOAD_GEOMETRY_FROM_FILE>

        return port;

    }

    private Entity createCameraEntity() {

        Entity camera = new Entity();

        // Add Path Component (for type identification)
        camera.addComponent(new Camera());

        // Add Transform Component
        camera.addComponent(new Transform());

        return camera;
    }

    // TODO: Actually create and stage a real single-port Entity without a parent!?
    // Serves as a "prop" for user to define new Extensions
    public Entity createPrototypeExtensionEntity() {

        Entity prototypeExtension = new Entity();

        // extensionPrototype.addComponent(new Extension());
        prototypeExtension.addComponent(new Transform());
        prototypeExtension.addComponent(new Image());

        ImageBuilder imageBuilder = new ImageBuilder();

        Rectangle rectangle = new Rectangle(200, 200);
        rectangle.setColor("#fff7f7f7");
        rectangle.setOutlineThickness(0.0);
        imageBuilder.addShape(rectangle);

        prototypeExtension.getComponent(Image.class).setImage(imageBuilder);

        imageSystem.invalidate(prototypeExtension.getComponent(Image.class)); // TODO: World shouldn't call systems. System should operate on the world and interact with other systems/entities in it.

        prototypeExtension.addComponent(new Label());
        prototypeExtension.getComponent(Label.class).setLabel("prototypeExtension");

        prototypeExtension.addComponent(new Visibility());
        prototypeExtension.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

        prototypeExtension.addComponent(new Boundary());

        // <HACK>
        // TODO: Add to common createEntity method.
        Manager.add(prototypeExtension);
        // <HACK>

        return prototypeExtension;
    }

    public Entity createPrototypePathEntity() {

        Entity prototypePath = new Entity();

        // extensionPrototype.addComponent(new Extension());
        prototypePath.addComponent(new Transform());
        prototypePath.addComponent(new Image());

        ImageBuilder imageBuilder = new ImageBuilder();

        Segment segment = new Segment(new Transform(-50, -50), new Transform(50, 50));
        segment.setLabel("Path");
        segment.setOutlineColor("#ff333333");
        segment.setOutlineThickness(10.0);
        imageBuilder.addShape(segment);

        prototypePath.getComponent(Image.class).setImage(imageBuilder);

        imageSystem.invalidate(prototypePath.getComponent(Image.class)); // TODO: World shouldn't call systems. System should operate on the world and interact with other systems/entities in it.

        prototypePath.addComponent(new Label());
        prototypePath.getComponent(Label.class).setLabel("prototypePath");

        prototypePath.addComponent(new Visibility());
        prototypePath.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

        prototypePath.addComponent(new Boundary());

        // <HACK>
        // TODO: Add to common createEntity method.
        Manager.add(prototypePath);
        // <HACK>

        return prototypePath;
    }


    // <EXTENSION_IMAGE_HELPERS>
    // TODO: Come up with better way to determine if the Extension already exists in the database.
    // TODO: Make more general for all Portables.
    public void configureExtensionFromProfile(Entity extension, Configuration configuration) {

        // Create Ports to match the Configuration
        for (int i = 0; i < configuration.getPorts().size(); i++) {

            Entity port = null;
            if (i < Portable.getPorts(extension).size()) {
                port = Portable.getPort(extension, i);
            } else {
                port = createEntity(Port.class);
            }

            // <HACK>
            port.addComponent(new RelativeLayoutConstraint());
            port.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(extension);
            // </HACK>

            Port.setIndex(port, i);
            Port.setType(port, configuration.getPorts().get(i).getType());
            Port.setDirection(port, configuration.getPorts().get(i).getDirection());

            if (i >= Portable.getPorts(extension).size()) {
                Portable.addPort(extension, port);
            }
        }

        // Set persistent to indicate the Extension is stored in a remote database
        // TODO: Replace with something more useful, like the URI or UUID of stored object in database
        extension.getComponent(Extension.class).setPersistent(true);
    }

    // TODO: This is an action that Clay can perform. Place this better, maybe in Clay.
    public void createExtensionProfile(final Entity extension) {
        if (!extension.getComponent(Extension.class).isPersistent()) {

            // TODO: Only call promptInputText if the extensionEntity is a draft (i.e., does not have an associated Configuration)
            Application.getView().getNativeUi().promptInputText(new NativeUi.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {

                    // Create Extension Configuration
                    Configuration configuration = new Configuration(extension);
                    configuration.setLabel(text);

                    Log.v("Configuration", "configuration # ports: " + configuration.getPorts().size());

                    // Assign the Configuration to the ExtensionEntity
//                    configureExtensionFromProfile(extension, configuration);

                    // Cache the new ExtensionEntity Configuration
                    Application.getView().getClay().getConfigurations().add(configuration);

                    // TODO: Persist the configuration in the user's private store (either local or online)

                    // TODO: Persist the configuration in the global store online
                }
            });
        } else {
            Application.getView().getNativeUi().promptAcknowledgment(new NativeUi.OnActionListener() {
                @Override
                public void onComplete(Object result) {

                }
            });
        }
    }
    // </EXTENSION_IMAGE_HELPERS>

    public long updateTime = 0L;
    public long renderTime = 0L;
    public long lookupCount = 0L;

    // TODO: Timer class with .start(), .stop() and keep history of records in list with timestamp.

    public void update() {
//        Boundary.innerBoundaries.clear();
        long updateStartTime = Clock.getCurrentTime();
        world.inputSystem.update();
        world.eventHandlerSystem.update();
        world.boundarySystem.update();
        world.portableLayoutSystem.update();
        world.cameraSystem.update();
        updateTime = Clock.getCurrentTime() - updateStartTime;
    }

    public void draw() {
        long renderStartTime = Clock.getCurrentTime();
        world.renderSystem.update();
        renderTime = Clock.getCurrentTime() - renderStartTime;
    }

    // <TITLE_UI_COMPONENT>
    // TODO: Allow user to setAbsolute and change a goal. Track it in relation to the actions taken and things built.
    protected Visible titleVisible = Visible.INVISIBLE;
    protected String titleText = "Project";

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleVisibility(Visible visible) {
        if (titleVisible == Visible.INVISIBLE && visible == Visible.VISIBLE) {
//            Application.getPlatform().openTitleEditor(getTitleText());
            this.titleVisible = visible;
        } else if (titleVisible == Visible.VISIBLE && visible == Visible.VISIBLE) {
//            Application.getPlatform().setTitleEditor(getTitleText());
        } else if (titleVisible == Visible.VISIBLE && visible == Visible.INVISIBLE) {
//            Application.getPlatform().closeTitleEditor();
            this.titleVisible = visible;
        }
    }

    public Visible getTitleVisibility() {
        return this.titleVisible;
    }
    // </TITLE_UI_COMPONENT>
}
