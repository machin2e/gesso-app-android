package camp.computer.clay;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.application.Application;
import camp.computer.clay.engine.component.ActionListenerComponent;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Extension;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.host.DisplayHostInterface;
import camp.computer.clay.host.InternetInterface;
import camp.computer.clay.host.MessengerInterface;
import camp.computer.clay.engine.component.Actor;
import camp.computer.clay.engine.entity.Host;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.old_model.Cache;
import camp.computer.clay.old_model.Internet;
import camp.computer.clay.old_model.Messenger;
import camp.computer.clay.old_model.PhoneHost;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PathImage;
import camp.computer.clay.util.image.Space;

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
        Actor actor = new Actor();
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
        if (entityType == Host.class) {
            return createHostEntity();
        } else if (entityType == Extension.class) {
            return createExtensionEntity();
        } else if (entityType == Path.class) {
            return createPathEntity();
        } else {
            return null;
        }
    }

    /**
     * Adds a <em>virtual</em> {@code Host} that can be configured and later assigned to a physical
     * host.
     */
    private static UUID createHostEntity() {

        // Create Entity
        Host host = new Host();

        // Portable Component (Image Component depends on this)
        final int PORT_COUNT = 12;
        for (int j = 0; j < PORT_COUNT; j++) {
            Port port = new Port();
            port.setLabel("Port " + (j + 1));
            port.setIndex(j);
            host.addPort(port);
        }

        // Add Transform Component
        host.setComponent(new Transform());

        // Add Image Component
        host.setComponent(new HostImage(host));

        // Load geometry from file into Image Component
        // TODO: Application.getView().restoreGeometry(this, "Geometry.json");

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        host.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent();
        actionListener.setOnActionListener(host.getActionListener());
        host.setComponent(actionListener);
        // </HACK>

        return host.getUuid();
    }

    private static UUID createExtensionEntity() {

        // Create Entity
        Extension extension = new Extension();

        // <PORTABLE_COMPONENT>
        // Create Ports and add them to the Extension
        int defaultPortCount = 1;
        for (int j = 0; j < defaultPortCount; j++) {
            Port port = new Port();
            port.setIndex(j);
            extension.addPort(port);
        }
        // </PORTABLE_COMPONENT>

        // Add Components
        extension.setComponent(new Transform());
        extension.setComponent(new ExtensionImage(extension));

        // Load geometry from file into Image Component
        // TODO: Application.getView().restoreGeometry(this, "Geometry.json");

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        extension.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent();
        actionListener.setOnActionListener(extension.getActionListener());
        extension.setComponent(actionListener);
        // </HACK>

        return extension.getUuid();
    }

    private static UUID createPathEntity() {
        Path path = new Path();
        PathImage pathImage = new PathImage(path); // Create Path Image
        path.setComponent(new Transform());
        path.setComponent(pathImage); // Assign Image to Entity

        // <HACK>
        // NOTE: This has to be done after adding an ImageComponent
//        path.setupActionListener();

        ActionListenerComponent actionListener = new ActionListenerComponent();
        actionListener.setOnActionListener(path.getActionListener());
        path.setComponent(actionListener);
        // </HACK>

        return path.getUuid();
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
