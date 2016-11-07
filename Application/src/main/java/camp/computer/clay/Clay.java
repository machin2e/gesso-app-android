package camp.computer.clay;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.controls.NativeUi;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.platform.PlatformInterface;
import camp.computer.clay.platform.communication.InternetInterface;
import camp.computer.clay.platform.communication.MessengerInterface;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.platform.Cache;
import camp.computer.clay.platform.Internet;
import camp.computer.clay.platform.Messenger;
import camp.computer.clay.platform.PhoneHost;
import camp.computer.clay.engine.World;

public class Clay {

    private Messenger messenger = null;

    private Internet internet = null;

    private Cache cache = null;

    // Group of discovered touchscreen PhoneHosts
    private List<PlatformInterface> platforms = new ArrayList<>();

    // Group of discovered PhoneHosts
    private List<PhoneHost> phoneHosts = new ArrayList<>();

    private List<Profile> profiles = new ArrayList<>();

    private World world;

    public List<Profile> getProfiles() {
        return this.profiles;
    }

    public Clay() {

        this.cache = new Cache(this); // Set up cache

        this.messenger = new Messenger(this); // Start the messaging systems

        this.internet = new Internet(this); // Start the networking systems

        // Create World
        this.world = new World();

        // Create Camera
        world.createEntity(Camera.class);

        Application.getView().getPlatformRenderSurface().setWorld(world);

        // <TEST>
        world.createEntity(Host.class);
        world.createEntity(Host.class);
        world.createEntity(Host.class);
        world.createEntity(Host.class);
        world.createEntity(Host.class);
        // </TEST>

        // <HACK>
        // TODO: Place in a LayoutSystem
        this.world.portableLayoutSystem.adjustLayout();
        // </HACK>
    }

    // <EXTENSION_IMAGE_HELPERS>
    // TODO: Come up with better way to determine if the Extension already exists in the database.
    // TODO: Make more general for all Portables.
    public static void configureFromProfile(Entity extension, Profile profile) {

        // Create Ports to match the Profile
        for (int i = 0; i < profile.getPorts().size(); i++) {

            Entity port = World.createEntity(Port.class);

            port.getComponent(Port.class).setIndex(i);
            port.getComponent(Port.class).setType(profile.getPorts().get(i).getType());
            port.getComponent(Port.class).setDirection(profile.getPorts().get(i).getDirection());

            extension.getComponent(Portable.class).addPort(port);
        }

        // Set persistent to indicate the Extension is stored in a remote database
        // TODO: Replace with something more useful, like the URI or UUID of stored object in database
        extension.getComponent(Extension.class).setPersistent(true);
    }

    // TODO: This is an action that Clay can perform. Place this better, maybe in Clay.
    public static void createExtensionProfile(final Entity extension) {
        if (!extension.getComponent(Extension.class).isPersistent()) {

            // TODO: Only call promptInputText if the extensionEntity is a draft (i.e., does not have an associated Profile)
            Application.getView().getNativeUi().promptInputText(new NativeUi.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {

                    // Create Extension Profile
                    Profile profile = new Profile(extension);
                    profile.setLabel(text);

                    // Assign the Profile to the ExtensionEntity
                    Clay.configureFromProfile(extension, profile);

                    // Cache the new ExtensionEntity Profile
                    Application.getView().getClay().getProfiles().add(profile);

                    // TODO: Persist the profile in the user's private store (either local or online)

                    // TODO: Persist the profile in the global store online
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

    public void addHost(MessengerInterface messageManager) {
        this.messenger.addHost(messageManager);
    }

    public void addResource(InternetInterface networkResource) {
        this.internet.addHost(networkResource);
    }

    /**
     * Adds a view to Clay. This makes the view available for use in systems built with Clay.
     *
     * @param view The view to make available to Clay.
     */
    public void addPlatform(PlatformInterface view) {
        this.platforms.add(view);
    }

    /**
     * Returns the view manager the specified index.
     *
     * @param i The index of the view to return.
     * @return The view at the specified index.
     */
    public PlatformInterface getPlatform(int i) {
        return this.platforms.get(i);
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
    public PhoneHost addDevice(final UUID deviceUuid, final String internetAddress) {

        // Search for the phoneHost in the store
        if (hasDeviceByUuid(deviceUuid)) {
            return null;
        }

        World.createEntity(Host.class);

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
