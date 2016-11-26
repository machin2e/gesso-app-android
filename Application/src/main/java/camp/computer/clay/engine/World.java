package camp.computer.clay.engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Geometry;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Notification;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Prototype;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.Style;
import camp.computer.clay.engine.component.Timer;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.util.EntityFactory;
import camp.computer.clay.engine.manager.Event;
import camp.computer.clay.engine.manager.EventHandler;
import camp.computer.clay.engine.manager.Manager;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.engine.system.System;
import camp.computer.clay.lib.ImageBuilder.Rectangle;
import camp.computer.clay.lib.ImageBuilder.Text;
import camp.computer.clay.model.Repository;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.model.player.Player;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.controls.PlatformUi;
import camp.computer.clay.util.time.Clock;

public class World {

    // <EVENT_MANAGER>
    private HashMap<Event.Type, ArrayList<EventHandler>> eventHandlers = new HashMap<>();

    public boolean subscribe(Event.Type eventType, EventHandler<?> eventHandler) {
        if (!eventHandlers.containsKey(eventType)) {
            eventHandlers.put(eventType, new ArrayList());
            eventHandlers.get(eventType).add(eventHandler);
            return true;
        } else if (eventHandlers.containsKey(eventType) && !eventHandlers.get(eventType).contains(eventHandler)) {
            eventHandlers.get(eventType).add(eventHandler);
            return true;
        } else {
            return false;
        }
    }

    public void notifySubscribers(Event event) {

        // Get subscribers to Event
        ArrayList<EventHandler> subscribedEventHandlers = eventHandlers.get(event.getType());
        if (subscribedEventHandlers != null) {
            for (int i = 0; i < subscribedEventHandlers.size(); i++) {
                subscribedEventHandlers.get(i).execute(event);
            }
        }
    }

    // TODO: public boolean unsubscribe(...)

    // </EVENT_MANAGER>

    public static final double HOST_TO_EXTENSION_SHORT_DISTANCE = 325;
    public static final double HOST_TO_EXTENSION_LONG_DISTANCE = 550;

    public static final double EXTENSION_PORT_SEPARATION_DISTANCE = 115;

    public static double PIXEL_PER_MILLIMETER = 6.0;

    public static double NEARBY_EXTENSION_DISTANCE_THRESHOLD = 375; // 375, 500
    public static double NEARBY_EXTENSION_RADIUS_THRESHOLD = 200 + 60;

    public static double PATH_OVERVIEW_THICKNESS = 10.0;
    public static double PATH_EDITVIEW_THICKNESS = 15.0;

    // <SETTINGS>
    public static boolean ENABLE_DRAW_OVERLAY = true;

    public static final String ASSET_SERVER_URI = "http://192.168.1.2:8001";
    // </SETTINGS>

    // <TEMPORARY>
    public Repository repository = new Repository();
    // </TEMPORARY>

    // <MANAGERS>
    public Manager Manager;
    // </MANAGERS>

    public World() {
        super();
        setup();
    }

    private void setup() {
        // <TODO: DELETE>
        World.world = this;
        // </TODO: DELETE>

        Manager = new Manager();

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

    private List<System> systems = new ArrayList<>();

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

    public void update() {
        long updateStartTime = Clock.getCurrentTime();
        for (int i = 0; i < systems.size(); i++) {
            // <HACK>
            if (systems.get(i).getClass() == RenderSystem.class) {
                continue;
            }
            // </HACK>
            systems.get(i).update();
        }
        updateTime = Clock.getCurrentTime() - updateStartTime;
    }

    public void draw() {
        long renderStartTime = Clock.getCurrentTime();
        getSystem(RenderSystem.class).update();
        renderTime = Clock.getCurrentTime() - renderStartTime;
    }

    public Entity createEntity(Class<?> entityType) {

        Entity entity = null;

        if (entityType == Host.class) { // HACK (because Host is a Component)
            entity = EntityFactory.createHostEntity(this);
        } else if (entityType == Extension.class) { // HACK (because Extension is a Component)
            entity = EntityFactory.createExtensionEntity(this);
        } else if (entityType == Path.class) {
            entity = EntityFactory.createPathEntity(this);
        } else if (entityType == Port.class) { // HACK (because Extension is a Component)
            entity = EntityFactory.createPortEntity(this);
        } else if (entityType == Camera.class) {
            entity = EntityFactory.createCameraEntity(this);
        } else if (entityType == Player.class) {
            entity = EntityFactory.createPlayerEntity(this);
        } else if (entityType == Notification.class) {
            entity = EntityFactory.createNotificationEntity(this);
        } else if (entityType == Geometry.class) {
            entity = EntityFactory.createGeometryEntity(this);
        }

        // Add Entity to Manager
        Manager.add(entity);

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

//        Text text2 = (Text) notification.getComponent(Image.class).getImage().getShapes().get(0);
        Text text2 = (Text) Image.getShapes(notification).get(0).getComponent(Geometry.class).shape;
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
        prototypeExtension.addComponent(new Image());
        prototypeExtension.addComponent(new Boundary());
        prototypeExtension.addComponent(new Style());
        prototypeExtension.addComponent(new Visibility());
        prototypeExtension.addComponent(new Label());

        Rectangle rectangle = new Rectangle(200, 200);
        rectangle.setColor("#fff7f7f7");
        rectangle.setOutlineThickness(0.0);
        Image.addShape(prototypeExtension, rectangle);

        Label.setLabel(prototypeExtension, "prototypeExtension");

        prototypeExtension.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

        // <HACK>
        // TODO: Add to common createEntity method.
        Manager.add(prototypeExtension);
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

            // TODO: Only call openCreateExtensionView if the extensionEntity is a draft (i.e., does not have an associated Configuration)
            Application.getApplication_().getPlatformUi().openCreateExtensionView(new PlatformUi.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {

                    // Create Extension Configuration
                    Configuration configuration = new Configuration(extension);
                    configuration.setLabel(text);

                    Log.v("Configuration", "configuration # ports: " + configuration.getPorts().size());

                    // Assign the Configuration to the ExtensionEntity
//                    configureExtensionFromProfile(extension, configuration);

                    // Cache the new ExtensionEntity Configuration
                    Application.getApplication_().getClay().getConfigurations().add(configuration);

                    // TODO: Persist the configuration in the user's private store (either local or online)

                    // TODO: Persist the configuration in the global store online
                }
            });
        } else {
            Application.getApplication_().getPlatformUi().promptAcknowledgment(new PlatformUi.OnActionListener() {
                @Override
                public void onComplete(Object result) {

                }
            });
        }
    }
    // </EXTENSION_IMAGE_HELPERS>
}
