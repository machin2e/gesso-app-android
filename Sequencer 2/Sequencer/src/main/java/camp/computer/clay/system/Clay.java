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

    // Resource management systems (e.g., networking, messaging, content)
    private SQLiteContentManager contentManager = null;
    private MessageManager messageManager = null;
    private NetworkManager networkManager = null;

    // List of discovered touchscreen devices
    private ArrayList<ViewManagerInterface> views;

    // List of discovered devices
    private ArrayList<Device> devices = new ArrayList<Device>();

    // List of actions cached on this device
    private CacheManager cacheManager = null;

    // The calendar used by Clay
    private Calendar calendar = Calendar.getInstance (TimeZone.getTimeZone("GMT"));

    public Clay() {

        this.views = new ArrayList<ViewManagerInterface>(); // Create list to store views.
        this.messageManager = new MessageManager(this); // Start the communications systems
        this.networkManager = new NetworkManager (this); // Start the networking systems

        this.cacheManager = new CacheManager(this); // Set up behavior repository
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
    public void addContentManager (SQLiteContentManager contentManager) {
        // <HACK>
        // this.contentManager = new FileContentManager(this, "file"); // Start the content management system
        this.contentManager = contentManager;
        // </HACK>
    }

    /*
     * Clay's infrastructure management functions.
     */

    /**
     * Sends a message to the specified device.
     * @param device
     * @param content
     */
    public void sendMessage (Device device, String content) {

        // Get source address
        String source = this.networkManager.getInternetAddress ();

        // Get destination address
        // String destination = device.getInternetAddress();
        String destination = device.getInternetAddress();
//        String[] destinationOctets = destination.split("\\.");
//        destinationOctets[3] = "255";
//        destination = TextUtils.join(".", destinationOctets);

        // Create message
        Message message = new Message("udp", source, destination, content);
        message.setDeliveryGuaranteed(true);

        // Queue message for sending
        messageManager.queueOutgoingMessage(message);
    }

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
        return this.cacheManager;
    }

    public SQLiteContentManager getStore() {
        return this.contentManager;
    }

    public ArrayList<Device> getDevices() {
        return this.devices;
    }

    public boolean hasNetworkManager () {
        return this.networkManager != null;
    }

    // TODO: Create device profile. Add this to device profile. Change to getClay().getProfile().getInternetAddress()
    public String getInternetAddress () {
//        if (hasNetworkManager()) {
//            return this.networkManager.getInternetAddress();
//        } else {
//            return null;
//        }

        Context context = ApplicationView.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v ("Clay", "Internet address: " + ip);
        return ip;
    }

    // TODO: Move this to a "network interface"
    public String getInternetBroadcastAddress () {
//        if (hasNetworkManager()) {
//            String broadcastAddressString = this.networkManager.getInternetAddress();
            String broadcastAddressString = getInternetAddress();
        Log.v ("Clay", "Broadcast: " + broadcastAddressString);
        broadcastAddressString = broadcastAddressString.substring(0, broadcastAddressString.lastIndexOf("."));
        broadcastAddressString += ".255";
//            String[] broadcastAddressOctetStrings = broadcastAddressString.split(".");
//            broadcastAddressOctetStrings[3] = "255"; // Replace the fourth octet with broadcast flag
//            broadcastAddressString = TextUtils.join(".", broadcastAddressOctetStrings);
            return broadcastAddressString;
//        } else {
//            return null;
//        }
    }

    public Device getUnitByAddress (String address) {
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
    public Device addUnit (final UUID unitUuid, final String internetAddress) {

        // Search for the device in the store
        if (hasUnitByUuid(unitUuid)) {
            return null;
        }

        // Try to restore the device profile from the store.
        Device device = getStore().restoreDevice(unitUuid);

        // If unable to restore the device's profile, then create a profile for the device.
        if (device == null) {
            device = new Device(getClay (), unitUuid);
        }

        // Update the device's profile based on information received from device itself.
        if (device != null) {
            // Update restored device with information from device
            device.setInternetAddress(internetAddress);

            // Add device to Clay
            addUnit2(device);

            // Establish TCP connection
            device.connectTcp();

//            // Show the action button
//            ApplicationView.getApplicationView().getCursorView().show(true);

            // Populate the timeline
            // TODO: Populate from scratch only if no timeline has been programmed for the device
            for (Event event : device.getTimeline().getEvents()) {
                // <HACK>
                device.enqueueMessage("start event " + event.getUuid());
                device.enqueueMessage("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                device.enqueueMessage("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
                // </HACK>
            }

            // Store the updated device profile.
            getStore ().storeDevice (device);
            getStore ().storeTimeline (device.getTimeline ());
        }

        return device;
    }

    private void addUnit2 (Device device) {
        Log.v ("Content_Manager", "addUnit2");

        if (!this.devices.contains (device)) {

            // Add device to present (i.e., local cache).
            this.devices.add(device);
            Log.v("Content_Manager", "Successfully added timeline.");

            // Add timelines to attached views
            for (ViewManagerInterface view : this.views) {
                // TODO: (1) addUnit a page to the ViewPager
                // TODO: (2) Add a tab to the action bar to support navigation to the specified page.
                view.addUnitView(device);
            }
        }
    }

    public boolean hasUnits () {
        return this.devices.size () > 0;
    }

    public boolean hasUnit (Device device) {
        return this.devices.contains (device);
    }

    public boolean hasUnitByUuid (UUID unitUuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(unitUuid) == 0) {
                return true;
            }
        }
        return false;
    }

    public Device getUnitByUuid (UUID unitUuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(unitUuid) == 0) {
                return device;
            }
        }
        return null;
    }

    public boolean hasUnitByAddress (String address) {
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
//        getClay().addUnit(unitUuidA, "10.1.10.29");
//        Device foundUnit = getUnitByUuid(unitUuidA);
//
//        // Discover second device
//        UUID unitUuidB = UUID.fromString("903d4bd4-71b0-4c6b-acab-bd30c6548c78");
//        getClay().addUnit(unitUuidB, "192.168.1.123");
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
//                getClay().getUnitByUuid(unitUuidA).getTimeline().addEvent(event);
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
     * Adds the action, caches it, and stores it.
     */
    public void cacheAction(Action action) {

        // Create action (and state) for the action script
//        Script behaviorScript = new Script (UUID.randomUUID(), tag, defaultState);
//        Action action = new Action (behaviorScript);

        // Cache the action
        this.cache(action);

//        // Store the action
//        if (hasStore()) {
//            getStore().storeAction(action);
//        }

    }

    public void cacheScript (Script script) {

        if (hasCache ()) {
            this.cache (script);
        }

    }

    /**
     * Returns true if Clay has a content manager.
     * @return True if Clay has a content manager. False otherwise.
     */
    public boolean hasStore() {
        return this.contentManager != null;
    }

    public Action getBehavior (UUID behaviorUuid) {
        if (hasCache()) {
            if (getCache().hasAction(behaviorUuid.toString())) {
                return getCache().getAction(behaviorUuid);
            } else {
                // TODO: Cache the behavior and callback to the object requesting the behavior.
            }
        }
        // TODO: throw NoCacheManagerException
        return null;
    }

    private boolean hasCache() {
        if (getCache() != null) {
            return true;
        }
        return false;
    }

    /**
     * Cycle through routine operations.
     */
    public void cycle () {

        // Process messages
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

    /**
     * Caches a Action in memory.
     * @param action The Action to cache.
     */
    public void cache (Action action) {
        this.getCache().cache (action);
    }

    /**
     * Caches a behavior interface to the cache.
     * @param script The behavior interface to cache.
     */
    public void cache (Script script) {
        this.getCache().cache (script);
    }

    /**
     * Adds a Device to Clay's object model.
     * @param device The Device to addUnit to Clay's object model.
     */
    public void addUnit(Device device) {
        this.devices.add(device);
    }

    /**
     * Push updated device object to views so they can show the updated information
     * @param device
     */
    public void updateUnitView (Device device) {

        for (ViewManagerInterface view : this.views) {
            view.refreshListViewFromData(device);
        }
    }

    /**
     * Requests a view for a device.
     * @param device The device for which a view is requested.
     */
    public void addUnitView (Device device) {

        // TODO: (?) Add DeviceViewFragment to a list here?

        // <HACK>
        // Make sure no devices are in an invalid state (null reference)
        boolean addView = true;
        for (Event event : device.getTimeline().getEvents()) {
            if (event.getAction() == null) {
                addView = false;
            }
        }
        if (addView) {
            addUnitView2(device);
        }
        // </HACK>
    }

    private void addUnitView2(Device device) {
        // Add timelines to attached views
        for (ViewManagerInterface view : this.views) {
            // TODO: (1) addUnit a page to the ViewPager

            // (2) Add a tab to the action bar to support navigation to the specified page.
            Log.v ("CM_Log", "addUnitView2");
            Log.v ("CM_Log", "\tdevice: " + device);
            Log.v ("CM_Log", "\tdevice/timeline: " + device.getTimeline());
            view.addUnitView(device);
        }
    }

    /**
     * Notify Clay of a change to an Event in the object model. Clay will propagate the change
     * to the cache, store, and repository.
     * @param event
     */
    public void notifyChange(Event event) {

        if (hasStore()) {

            // Add events if they don't already exist
//            if (!contentManager.hasEvent(event)) {

                // Store event, behavior, state
                getStore().storeEvent(event);

                // Store behavior for event
                getStore().storeAction(event.getAction());

                // Store behavior state for behavior
//                getStore().storeState(event.getAction().getState());

//            }
        }

    }
}
