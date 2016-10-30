package camp.computer.clay;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.application.Application;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.ActionListenerComponent;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.ActionListenerSystem;
import camp.computer.clay.host.DisplayHostInterface;
import camp.computer.clay.host.InternetInterface;
import camp.computer.clay.host.MessengerInterface;
import camp.computer.clay.engine.component.Actor;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.old_model.Cache;
import camp.computer.clay.old_model.Internet;
import camp.computer.clay.old_model.Messenger;
import camp.computer.clay.old_model.PhoneHost;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.World;

public class Clay {

    private Messenger messenger = null;

    private Internet internet = null;

    private Cache cache = null;

    private World world;

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

        // World
        this.world = new World();
        world.setupActionListener();

        // Create Camera
        createEntity(Camera.class);

        // Create actor and setAbsolute perspective
        Actor actor = new Actor();
        this.world.addActor(actor);

        Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

        // CameraEntity
        cameraEntity.getComponent(Camera.class).setWorld(world);

        // Add actor to model
        world.addActor(actor);

        Application.getView().getPlatformRenderSurface().setWorld(world);

        // <TEST>
        createEntity(Host.class);
        createEntity(Host.class);
        createEntity(Host.class);
        createEntity(Host.class);
        createEntity(Host.class);
        // </TEST>

        // <HACK>
        World.getWorld().adjustLayout();
        // </HACK>
    }

    private Clay getClay() {
        return this;
    }

    public World getWorld() {
        return this.world;
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
        host.addComponent(new Portable()); // Add Portable Component (so can add Ports)
        host.addComponent(new Transform());
        host.addComponent(new Image());

        // Portable Component (Image Component depends on this)
        final int PORT_COUNT = 12;
        for (int j = 0; j < PORT_COUNT; j++) {

            Entity port = Clay.createEntity(Port.class);

            port.getComponent(Label.class).setLabel("Port " + (j + 1));
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

        return host;
    }

    private static Entity createExtensionEntity() {

        // Create Entity
        Entity extensionEntity = new Entity();

        // Add Components
        extensionEntity.addComponent(new Extension()); // Unique to Extension
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

        return pathEntity;
    }

    private static Entity createPortEntity() {

        Entity port = new Entity();

        // Add Components
        port.addComponent(new Port()); // Unique to Port
        port.addComponent(new Transform());
        port.addComponent(new Image());
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
