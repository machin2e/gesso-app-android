package camp.computer.clay.system;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import camp.computer.clay.sequencer.ApplicationView;

public class Clay {

    private ContentEntry state;

    // Resource management systems (e.g., networking, messaging, content)
    private SQLiteContentManager store = null;
    private MessageManager messageManager = null;
    private NetworkManager networkManager = null;

    // List of discovered touchscreen devices
    private ArrayList<ViewManagerInterface> views;

    // List of discovered devices
    private ArrayList<Device> devices = new ArrayList<Device>();

    // List of actions cached on this device
    private CacheManager cache = null;

    // The calendar used by Clay
    private Calendar calendar = Calendar.getInstance (TimeZone.getTimeZone("GMT"));

    public Clay() {

        this.views = new ArrayList<ViewManagerInterface>(); // Create choose to store views.
        this.messageManager = new MessageManager(this); // Start the communications systems
        this.networkManager = new NetworkManager (this); // Start the networking systems

        this.cache = new CacheManager(this); // Set up behavior repository

        // Content
        // TODO: Stream this in from the Internet and devices.
        state = new ContentEntry ("clay", "");
        state.choose ("devices");
    }

    public ContentEntry getContent () {
        return this.state;
    }

    /*
     * Clay's essential operating system functions.
     */

    public void addManager (MessageManagerInterface messageManager) {
        this.messageManager.addManager(messageManager);
    }

    public void addResource (NetworkResourceInterface networkResource) {
        this.networkManager.addResource(networkResource);
    }

    /**
     * Adds a content manager for use by Clay. Retrieves the basic actions provided by the
     * content manager and makes them available in Clay.
     */
    public void setStore(SQLiteContentManager contentManager) {
        this.store = contentManager;
    }

    /*
     * Clay's infrastructure management functions.
     */

    /**
     * Adds a view to Clay. This makes the view available for use in systems built with Clay.
     * @param view The view to make available to Clay.
     */
    public void addView (ViewManagerInterface view) {
        this.views.add (view);
    }

    /**
     * Returns the view manager the specified index.
     * @param i The index of the view to return.
     * @return The view at the specified index.
     */
    public ViewManagerInterface getView (int i) {
        return this.views.get (i);
    }

    public CacheManager getCache() {
        return this.cache;
    }

    public SQLiteContentManager getStore() {
        return this.store;
    }

    public ArrayList<Device> getDevices() {
        return this.devices;
    }

    public boolean hasNetworkManager () {
        return this.networkManager != null;
    }

    // TODO: Create device profile. Add this to device profile. Change to getClay().getProfile().getInternetAddress()
    public String getInternetAddress () {
        Context context = ApplicationView.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v ("Clay", "Internet address: " + ip);
        return ip;
    }

    public String getInternetBroadcastAddress () {
        String broadcastAddressString = getInternetAddress();
        Log.v ("Clay", "Broadcast: " + broadcastAddressString);
        broadcastAddressString = broadcastAddressString.substring(0, broadcastAddressString.lastIndexOf("."));
        broadcastAddressString += ".255";
        return broadcastAddressString;
    }

    public Device getDeviceByAddress(String address) {
        for (Device device : getDevices()) {
            if (device.getInternetAddress ().compareTo (address) == 0) {
                return device;
            }
        }
        return null;
    }

    private Clay getClay () {
        return this;
    }

    /**
     * Adds the specified unit to Clay's operating environment.
     */
    public Device addDevice(final UUID deviceUuid, final String internetAddress) {

        // Search for the device in the store
        if (hasDeviceByUuid(deviceUuid)) {
            return null;
        }

        // Try to restore the device profile from the store.
        Device device = getStore().restoreDevice(deviceUuid);

        // If unable to restore the device's profile, then create a profile for the device.
        if (device == null) {
            device = new Device(getClay (), deviceUuid);
        }

        // Update the device's profile based on information received from device itself.
        if (device != null) {

            // Data.
            ContentEntry deviceContent = getClay().getContent().get("devices").put(deviceUuid.toString());

            // <HACK>
            // TODO: Update this from a choose of the observables received from the boards.
            ContentEntry channelsContent = deviceContent.choose("channels");
            for (int i = 0; i < 12; i++) {

                // device/<uuid>/channels/<number>
                ContentEntry channelContent = channelsContent.put(String.valueOf(i + 1));

                // device/<uuid>/channels/<number>/number
                channelContent.put("number", String.valueOf(i + 1));

                // device/<uuid>/channels/<number>/direction
                channelContent.put("direction").from("input", "output").set("input");

                // device/<uuid>/channels/<number>/type
                channelContent.put("type").from("toggle", "waveform", "pulse").set("toggle"); // TODO: switch

                // device/<uuid>/channels/<number>/content
                ContentEntry channelContentContent = channelContent.put("content");

                // device/<uuid>/channels/<number>/content/<observable>
                // TODO: Retreive the "from" values and the "default" value from the exposed observables on the actual hardware (or the hardware profile)
                channelContentContent.put("toggle_value").from("on", "off").set("off");
                channelContentContent.put("waveform_sample_value", "none");
                channelContentContent.put("pulse_period_seconds", "0");
                channelContentContent.put("pulse_duty_cycle", "0");
            }
            // </HACK>

            // Update restored device with information from device
            device.setInternetAddress(internetAddress);

            Log.v ("TCP", "device.internetAddress: " + internetAddress);

            // Store the updated device profile.
            getStore ().storeDevice (device);
            getStore().storeTimeline(device.getTimeline());

            Log.v("TCP", "device.internetAddress (2): " + internetAddress);

            // Add device to Clay
            if (!this.devices.contains (device)) {

                // Add device to present (i.e., local cache).
                this.devices.add (device);
                Log.v("Content_Manager", "Successfully added timeline.");

                // Add timelines to attached views
                for (ViewManagerInterface view : this.views) {
                    view.addDeviceView(device);
                }
            }

            Log.v("TCP", "device.internetAddress (3): " + internetAddress);

            // Establish TCP connection
            device.connectTcp();

            Log.v("TCP", "device.internetAddress (4): " + internetAddress);

//            // Show the action button
//            ApplicationView.getApplicationView().getCursorView().show(true);

            // Populate the device's timeline
            // TODO: Populate from scratch only if no timeline has been programmed for the device
            for (Event event : device.getTimeline().getEvents()) {
                // <HACK>
                device.enqueueMessage("start event " + event.getUuid());
                device.enqueueMessage("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                device.enqueueMessage("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
                // </HACK>
            }
        }

        return device;
    }

    public boolean hasDeviceByUuid(UUID uuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(uuid) == 0) {
                return true;
            }
        }
        return false;
    }

    public Device getDeviceByUuid(UUID uuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(uuid) == 0) {
                return device;
            }
        }
        return null;
    }

    public boolean hasDeviceByAddress(String address) {
        for (Device device : getDevices()) {
            if (device.getInternetAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

//    public void simulateSession (boolean addBehaviorToTimeline, int behaviorCount, boolean addAbstractBehaviorToTimeline) {
//        Log.v("Content_Manager", "simulateSession");
//
//        // Discover first device
//        UUID unitUuidA = UUID.fromString("403d4bd4-71b0-4c6b-acab-bd30c6548c71");
//        getClay().addDevice(unitUuidA, "10.1.10.29");
//        Device foundUnit = getDeviceByUuid(unitUuidA);
//
//        // Discover second device
//        UUID unitUuidB = UUID.fromString("903d4bd4-71b0-4c6b-acab-bd30c6548c78");
//        getClay().addDevice(unitUuidB, "192.168.1.123");
//
//        if (addBehaviorToTimeline) {
//            for (int i = 0; i < behaviorCount; i++) {
//                // Create action based on action script
//                Log.v("Content_Manager", "> Creating action");
//                Random r = new Random();
//                int selectedBehaviorIndex = r.nextInt(getClay().getCache().getActions().size());
////                Script selectedBehaviorScript = getClay().getCache().getScripts().get(selectedBehaviorIndex);
////                Action action = new Action(selectedBehaviorScript);
//                Action action = getClay().getCache().getActions().get(selectedBehaviorIndex);
//                getClay().getStore().storeAction(action);
//
//                // Create event for the action and add it to the unit's timeline
//                Log.v("Content_Manager", "> Device (UUID: " + foundUnit.getUuid() + ")");
//                Event event = new Event(foundUnit.getTimeline(), action);
//                getClay().getDeviceByUuid(unitUuidA).getTimeline().addEvent(event);
//                getClay().getStore().storeEvent(event);
//                // TODO: Update unit
//            }
//        }
//
//        if (addAbstractBehaviorToTimeline) {
//            // Create action based on action script
//            Log.v("Content_Manager", "> Creating action");
////            Action action = new Action("so high");
////            action.setDescription("oh yeah!");
////            action.addAction(foundUnit.getTimeline().getEvents().get(0).getAction());
////            action.addAction(foundUnit.getTimeline().getEvents().get(1).getAction());
////            getClay().getStore().storeAction(action);
//            ArrayList<Action> children = new ArrayList<Action>();
//            ArrayList<State> states = new ArrayList<State>();
//            children.add(foundUnit.getTimeline().getEvents().get(0).getAction());
//            states.addAll(foundUnit.getTimeline().getEvents().get(0).getState());
//            children.add(foundUnit.getTimeline().getEvents().get(1).getAction());
//            states.addAll(foundUnit.getTimeline().getEvents().get(1).getState());
//            Action action = getClay().getStore().getActionComposition(children);
//
//            // remove events for abstracted actions
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0));
//            foundUnit.getTimeline().getEvents().remove(0); // if store action successful
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1));
//            foundUnit.getTimeline().getEvents().remove(1); // if store action successful
//
//            // Create event for the action and add it to the unit's timeline
//            Log.v("Content_Manager", "> Device (UUID: " + foundUnit.getUuid() + ")");
//            Event event = new Event(foundUnit.getTimeline(), action);
//            // insert new event for abstract action
//            //            foundUnit.getTimeline().addEvent(event);
//            event.getState().erase();
//            event.getState().addAll(states);
//            Log.v("New_Behavior_Parent", "Added " + states.size() + " states to new event.");
//            for (State state : event.getState()) {
//                Log.v("New_Behavior_Parent", "\t" + state.getState());
//            }
//            foundUnit.getTimeline().getEvents().add(0, event); // if store event was successful
//            getClay().getStore().storeEvent(event);
//            // TODO: Update unit
//        }
//
////        if (addAbstractBehaviorToTimeline) {
////            // Create behavior based on behavior script
////            Log.v("Content_Manager", "> Creating behavior");
////            Action behavior = new Action("so so high");
////            behavior.setDescription("oh yeah!");
////            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0), null);
////            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(0).getAction());
////            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1), null);
////            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(1).getAction());
////            getClay().getStore().storeAction(behavior);
////            // remove events for abstracted actions
////            foundUnit.getTimeline().getEvents().remove(0); // if store behavior successful
////            foundUnit.getTimeline().getEvents().remove(1); // if store behavior successful
////
////            // Create event for the behavior and add it to the unit's timeline
////            Log.v("Content_Manager", "> Device (UUID: " + foundUnit.getUuid() + ")");
////            Event event = new Event(foundUnit.getTimeline(), behavior);
////            // insert new event for abstract behavior
////            //            foundUnit.getTimeline().addEvent(event);
////            foundUnit.getTimeline().getEvents().add(0, event); // if store event was successful
////            getClay().getStore().storeEvent(event);
////            // TODO: Update unit
////        }
//
////        getClay().notifyChange(event);
//
//        getClay().getStore().writeDatabase();
//
//        for (Device unit : getClay().getDevices()) {
//            Log.v ("Content_Manager", "Device (UUID: " + unit.getUuid() + ")");
//            Log.v ("Content_Manager", "\tTimeline (UUID: " + unit.getTimeline().getUuid() + ")");
//
//            int tabCount = 3;
//            for (Event e : unit.getTimeline().getEvents()) {
//                Log.v ("Content_Manager", "\t\tEvent (UUID: " + e.getUuid() + ")");
//                // TODO: Recursively print out the behavior tree
//                printBehavior (e.getAction(), tabCount);
//            }
//        }
//    }

    /**
     * Returns true if Clay has a content manager.
     * @return True if Clay has a content manager. False otherwise.
     */
    public boolean hasStore() {
        return this.store != null;
    }

    private boolean hasCache() {
        return this.cache != null;
    }

    /**
     * Cycle through routine operations.
     */
    public void step () {
        messageManager.processMessage();
    }

    private Calendar getCalendar () {
        return this.calendar;
    }

    public Date getDate () {
        return this.calendar.getTime();
    }

    public long getTime () {
        return this.calendar.getTimeInMillis();
    }
}
