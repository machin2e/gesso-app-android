package camp.computer.clay.engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Notification;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Player;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Prototype;
import camp.computer.clay.engine.component.Style;
import camp.computer.clay.engine.component.Timer;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.TransformConstraint;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.Workspace;
import camp.computer.clay.engine.component.util.HostLayoutStrategy;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.util.EntityFactory;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.event.EventResponse;
import camp.computer.clay.engine.manager.EntityManager;
import camp.computer.clay.engine.manager.EventManager;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.engine.system.System;
import camp.computer.clay.lib.Geometry.Rectangle;
import camp.computer.clay.lib.Geometry.Text;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.Cache;
import camp.computer.clay.platform.graphics.controls.Widgets;
import camp.computer.clay.structure.configuration.Configuration;

public class World {

    public static final double HOST_TO_EXTENSION_SHORT_DISTANCE = 325;
    public static final double HOST_TO_EXTENSION_LONG_DISTANCE = 550;

    public static final double EXTENSION_PORT_SEPARATION_DISTANCE = 115;

    public static double PIXEL_PER_MILLIMETER = 6.0;

    public static double NEARBY_EXTENSION_DISTANCE_THRESHOLD = 375; // 375, 500
    public static double NEARBY_EXTENSION_RADIUS_THRESHOLD = 200 + 60;

    public static double PATH_OVERVIEW_THICKNESS = 10.0;
    public static double PATH_EDITVIEW_THICKNESS = 15.0;

    // <SETTINGS>
    public static boolean ENABLE_OVERLAY = true;
    public static boolean ENABLE_GEOMETRY_ANNOTATIONS = false;
    public static boolean ENABLE_ANNOTATION_ENTITY_TRANSFORM = false;
    public static boolean ENABLE_GEOMETRY_OVERLAY = false;
    // </SETTINGS>

    // <REFACTOR_INTO_ENGINE>
    public static String NOTIFICATION_FONT = "fonts/ProggyClean.ttf";
    public static float NOTIFICATION_FONT_SIZE = 45;
    public static final long DEFAULT_NOTIFICATION_TIMEOUT = 1000;
    public static final float DEFAULT_NOTIFICATION_OFFSET_X = 0;
    public static final float DEFAULT_NOTIFICATION_OFFSET_Y = -50;

    public static int OVERLAY_TOP_MARGIN = 25;
    public static int OVERLAY_LEFT_MARGIN = 25;
    public static int OVERLAY_LINE_SPACING = 10;
    public static String OVERLAY_FONT = "fonts/ProggySquare.ttf";
    public static float OVERLAY_FONT_SIZE = 25;
    public static String OVERLAY_FONT_COLOR = "#ffff0000";

    public static String GEOMETRY_ANNOTATION_FONT = "fonts/ProggySquare.ttf";
    public static float GEOMETRY_ANNOTATION_FONT_SIZE = 35;
    public static String GEOMETRY_ANNOTATION_FONT_COLOR = "#ffff0000";
    public static int GEOMETRY_ANNOTATION_LINE_SPACING = 10;
    // </REFACTOR_INTO_ENGINE>

    public long tickCount = 0;
    public long previousTickTime = -1;
    public long tickFrequency = Clock.NANOS_PER_SECOND / 30;

    // <MANAGERS>
    public List<Event> eventQueue = new ArrayList<>();
    public int nextEventIndex = 0;

    public EventManager eventManager;

    public EntityManager entityManager;
    // </MANAGERS>

    private List<System> systems = new ArrayList<>();

    // <TEMPORARY>
//    public Repository repository;
    public Cache cache;
    // </TEMPORARY>

    public Engine engine;

    public World(Engine engine) {
        super();

        this.engine = engine;
        setup();
    }

    private void setup() {
        // <TODO: DELETE>
        World.world = this;
        // </TODO: DELETE>

        eventManager = new EventManager();
        eventManager.registerEvent("NONE"); // TODO: Delete!

        eventManager.registerEvent("CLOCK_TICK");

        eventManager.registerEvent("SELECT");
        eventManager.registerEvent("HOLD");
        eventManager.registerEvent("MOVE");
        eventManager.registerEvent("UNSELECT");

        eventManager.registerEvent("CREATE_HOST");
        eventManager.registerEvent("DESTROY_HOST");

        entityManager = new EntityManager();

        createPrototypeExtensionEntity();

        // <TEMPORARY>
//        repository = new Repository();
//        repository.populateTestData();
        cache = new Cache();
        // </TEMPORARY>

        eventManager.subscribe("CREATE_HOST", new EventResponse() {
            @Override
            public void execute(Event event) {
                Log.v("EVENT_QUEUE", "CREATE_HOST");
                Entity host = World.getInstance().createEntity(Host.class);
                World.getInstance().getSystem(PortableLayoutSystem.class).updateWorldLayout(new HostLayoutStrategy());

//                // Automatically focus on the first Host that appears in the workspace/world.
//                if (World.getInstance().entityManager.get().size() == 1) {
//                    Entity camera = World.getInstance().entityManager.get().filterWithComponent(Camera.class).get(0);
//                    camera.getComponent(Camera.class).focus = host;
//                    camera.getComponent(Camera.class).mode = Camera.Mode.FOCUS;
//                } else {
//                    Entity camera = World.getInstance().entityManager.get().filterWithComponent(Camera.class).get(0);
//                    camera.getComponent(Camera.class).focus = null;
//                    camera.getComponent(Camera.class).mode = Camera.Mode.FOCUS;
//                }
            }
        });
    }

    // <TODO: DELETE>
    private static World world = null;

    public static World getInstance() {
        return World.world;
    }
    // </TODO: DELETE>

    public void addSystem(System system) {
        if (!systems.contains(system)) {
            systems.add(system);
        }
    }

    public <S extends System> S getSystem(Class<S> systemType) {
        for (int i = 0; i < systems.size(); i++) {
            if (systems.get(i).getClass() == systemType) {
                return systemType.cast(systems.get(i));
            }
        }
        return null;
    }

    public long updateTime = 0L;
    public long renderTime = 0L;
    public long lookupCount = 0L;

    // TODO: Timer class with .start(), .stop() and keep history of records in list with timestamp.

    public void update(long dt) {

        // <REFACTOR>
        entityManager.destroyEntities();
        // </REFACTOR>

        long updateStartTime = Clock.getTime(Clock.Unit.MILLISECONDS);
        for (int i = 0; i < systems.size(); i++) {
            systems.get(i).update(dt);
        }
        updateTime = Clock.getTime(Clock.Unit.MILLISECONDS) - updateStartTime;
    }

    public Entity createEntity(Class<?> entityType) {

        Entity entity = null;

        // HACK (because Host is a Component)
        if (entityType == Host.class) {
            entity = EntityFactory.createHostEntity(this);
        } else if (entityType == Extension.class) {
            entity = EntityFactory.createExtensionEntity(this);
        } else if (entityType == Path.class) {
            entity = EntityFactory.createPathEntity(this);
        } else if (entityType == Port.class) {
            entity = EntityFactory.createPortEntity(this);
        } else if (entityType == Camera.class) {
            entity = EntityFactory.createCameraEntity(this);
        } else if (entityType == Player.class) {
            entity = EntityFactory.createPlayerEntity(this);
        } else if (entityType == Notification.class) {
            entity = EntityFactory.createNotificationEntity(this);
        } else if (entityType == Primitive.class) {
            entity = EntityFactory.createPrimitiveEntity(this);
        } else if (entityType == Workspace.class) {
            entity = EntityFactory.createWorkspaceEntity(this);
        }

        // Add Entity to entityManager
        entityManager.add(entity);

        return entity;
    }

    // <TODO:REFACTOR>
    public void createAndConfigureNotification(String text, Transform transform, long timeout) {

        Entity notification = world.createEntity(Notification.class);

        notification.getComponent(Notification.class).message = text;
        notification.getComponent(Notification.class).timeout = timeout;
        notification.getComponent(Transform.class).set(transform);
        // <HACK>
        notification.getComponent(Transform.class).rotation = 0;
        // </HACK>

//        Text text2 = (Text) notification.getComponent(ModelBuilder.class).getModelComponent().getPrimitives().get(0);
        Text text2 = (Text) Model.getPrimitives(notification).get(0).getComponent(Primitive.class).shape;
        text2.setText(notification.getComponent(Notification.class).message);
        text2.setColor("#ff0000");

        // <HACK>
        notification.getComponent(Timer.class).onTimeout(notification);
        // </HACK>
    }
    // </TODO:REFACTOR>

    // TODO: Actually create and stage a real single-port Entity without a parent!?
    // Serves as a "prop" for user to define new Extensions
    public Entity createPrototypeExtensionEntity() {

        Entity prototypeExtension = new Entity();

//        // prototypeExtension.addComponent(new Extension()); // NOTE: Just used as a placeholder. Consider actually using the prototype, removing the Prototype component.
//        prototypeExtension.addComponent(new Portable());

        prototypeExtension.addComponent(new Prototype()); // Unique to Prototypes/Props
        prototypeExtension.addComponent(new Transform());
        prototypeExtension.addComponent(new Physics());
        prototypeExtension.addComponent(new Model());
        prototypeExtension.addComponent(new Boundary());
        prototypeExtension.addComponent(new Style());
        prototypeExtension.addComponent(new Visibility());
        prototypeExtension.addComponent(new Label());

        Rectangle rectangle = new Rectangle(200, 200);
        rectangle.setColor("#fff7f7f7");
        rectangle.setOutlineThickness(0.0);
        Model.addShape(prototypeExtension, rectangle);

        Label.setLabel(prototypeExtension, "prototypeExtension");

        prototypeExtension.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

        // <HACK>
        // TODO: Add to common createEntity method.
        entityManager.add(prototypeExtension);
        // <HACK>

        return prototypeExtension;
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
            port.addComponent(new TransformConstraint());
            port.getComponent(TransformConstraint.class).setReferenceEntity(extension);
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

            // TODO: Only call openCreateExtensionView if the extensionEntity is a draft (i.e., does not have an associated Configuration)
            Application.getInstance().getWidgets().openCreateExtensionView(new Widgets.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {

                    // Create Extension Configuration
                    Configuration configuration = new Configuration(extension);
                    configuration.setLabel(text);

                    Log.v("Configuration", "configuration # ports: " + configuration.getPorts().size());

                    // Assign the Configuration to the ExtensionEntity
//                    configureExtensionFromProfile(extension, configuration);

                    // Cache_OLD the new ExtensionEntity Configuration
                    cache.add(configuration);

                    // TODO: Persist the configuration in the user's private store (either local or online)

                    // TODO: Persist the configuration in the global store online
                }
            });
        } else {
            Application.getInstance().getWidgets().promptAcknowledgment(new Widgets.OnActionListener() {
                @Override
                public void onComplete(Object result) {

                }
            });
        }
    }
    // </EXTENSION_IMAGE_HELPERS>
}
