package camp.computer.clay;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.application.Application;
import camp.computer.clay.host.DisplayHostInterface;
import camp.computer.clay.host.InternetInterface;
import camp.computer.clay.host.MessengerInterface;
import camp.computer.clay.model.Actor;
import camp.computer.clay.model.Model;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.profile.PortableProfile;
import camp.computer.clay.old_model.Cache;
import camp.computer.clay.old_model.Internet;
import camp.computer.clay.old_model.Messenger;
import camp.computer.clay.old_model.PhoneHost;
import camp.computer.clay.util.image.Space;

public class Clay {

    private Messenger messenger = null;

    private Internet internet = null;

    private Cache cache = null;

    private Model model;

    private Space space;

    // Group of discovered touchscreen phoneHosts
    private List<DisplayHostInterface> displays = new ArrayList<>();

    // Group of discovered phoneHosts
    private List<PhoneHost> phoneHosts = new ArrayList<>();

    private List<PortableProfile> portableProfiles = new ArrayList<>();

    public List<PortableProfile> getPortableProfiles() {
        return this.portableProfiles;
    }

    public Clay() {

        this.cache = new Cache(this); // Set up cache

        this.messenger = new Messenger(this); // Start the messaging systems

        this.internet = new Internet(this); // Start the networking systems

        // Model
        this.model = new Model();

        // Space
        this.space = new Space(model);

        // Create actor and set perspective
        Actor actor = new Actor(this.space);

        // Add actor to model
        model.addActor(actor);

        Application.getView().getDisplay().setSpace(space);

        // <TEST>
        simulateHost();
        simulateHost();
        simulateHost();
        simulateHost();
        simulateHost();
        // </TEST>

    }

    public Model getModel() {
        return this.model;
    }

    public Space getSpace() {
        return this.space;
    }

    private void simulateHost() {

        // <FORM_CONFIGURATION>
        // TODO: Read this from the device (or look up from host UUID). It will be encoded on
        // TODO: (cont'd) the device.
        final int PORT_COUNT = 12;
        // </FORM_CONFIGURATION>

        camp.computer.clay.model.Host host = new camp.computer.clay.model.Host();

        for (int j = 0; j < PORT_COUNT; j++) {
            Port port = new Port();
            port.setIndex(j);
            host.addPort(port);
        }

        model.addHost(host);

        space.addEntity(host);
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

    private Clay getClay() {
        return this;
    }

    /**
     * Adds the specified unit to Clay's operating model.
     */
    public PhoneHost addDevice(final UUID deviceUuid, final String internetAddress) {

        // Search for the phoneHost in the store
        if (hasDeviceByUuid(deviceUuid)) {
            return null;
        }

        simulateHost();

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
