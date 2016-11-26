package camp.computer.clay;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.util.ProjectLayoutStrategy;
import camp.computer.clay.engine.system.BoundarySystem;
import camp.computer.clay.engine.system.CameraSystem;
import camp.computer.clay.engine.system.ImageSystem;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.engine.system.PhysicsSystem;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.engine.system.StyleSystem;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.Cache;
import camp.computer.clay.platform.Internet;
import camp.computer.clay.platform.Messenger;
import camp.computer.clay.platform.PhoneHost;
import camp.computer.clay.platform.PlatformInterface;
import camp.computer.clay.platform.communication.InternetInterface;
import camp.computer.clay.platform.communication.MessengerInterface;
import camp.computer.clay.util.Random;

public class Clay {

    private Cache cache = null;

    private Messenger messenger = null;

    private Internet internet = null;

    // Group of discovered touchscreen PhoneHosts
    private PlatformInterface platform;

    // Group of discovered PhoneHosts
    private List<PhoneHost> phoneHosts = new ArrayList<>(); // TODO: Convert to Entity

    private List<Configuration> configurations = new ArrayList<>();

    private World world;

    public List<Configuration> getConfigurations() {
        return this.configurations;
    }

    public Clay() {

        this.cache = new Cache(this); // Set up cache

        this.messenger = new Messenger(this); // Start the messaging systems

        this.internet = new Internet(this); // Start the networking systems

        // Create World
        world = new World();
        world.addSystem(new InputSystem(world));
        world.addSystem(new ImageSystem(world));
        world.addSystem(new StyleSystem(world));
        world.addSystem(new PhysicsSystem(world));
        world.addSystem(new BoundarySystem(world));
        world.addSystem(new PortableLayoutSystem(world));
        world.addSystem(new CameraSystem(world));
        world.addSystem(new RenderSystem(world));

        // Create Camera
        world.createEntity(Camera.class);

        Application.getApplication_().getPlatformRenderSurface().setWorld(world);

        // <TEST>
        int minHostCount = 5;
        int maxHostCount = 6;
        int hostCount = Random.generateRandomInteger(minHostCount, maxHostCount);
        for (int i = 0; i < hostCount; i++) {
            world.createEntity(Host.class);
        }
        // </TEST>

        // <HACK>
        // TODO: Place in a LayoutSystem
        world.getSystem(PortableLayoutSystem.class).adjustLayout(new ProjectLayoutStrategy());
        // </HACK>
    }

    // TODO: Convert to Event Handler
    public void addHost(MessengerInterface messageManager) {
        this.messenger.addHost(messageManager);
    }

    // TODO: Convert to Event Handler
    public void addResource(InternetInterface networkResource) {
        this.internet.addHost(networkResource);
    }

    /**
     * Adds a view to Clay. This makes the view available for use in systems built with Clay.
     *
     * @param view The view to make available to Clay.
     */
    public void addPlatform(PlatformInterface view) {
        this.platform = (view);
    }

    /**
     * Returns the view manager the specified index.
     *
     * @param i The index of the view to return.
     * @return The view at the specified index.
     */
    public PlatformInterface getPlatform() {
        return platform;
    }

    public Cache getCache() {
        return this.cache;
    }

    public List<PhoneHost> getPhoneHosts() {
        return this.phoneHosts;
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
    public PhoneHost addDevice(final UUID deviceUuid, String internetAddress) {

        // Search for the phoneHost in the store
        if (hasDeviceByUuid(deviceUuid)) {
            return null;
        }

        world.createEntity(Host.class);

        return null;
    }

    // <TODO: MOVE_TO_MANAGER>
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
        // TODO:
        return false;
    }
    // </TODO: MOVE_TO_MANAGER>

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
