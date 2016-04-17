package camp.computer.clay.system;

import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.sequencer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import camp.computer.clay.sequencer.ApplicationView;
import camp.computer.clay.sequencer.EventHolder;
import camp.computer.clay.sequencer.TimelineListView;

public class Clay {

    // Resource management systems (e.g., networking, messaging, content)
    private SQLiteContentManager contentManager = null;
    private MessageManager messageManager = null;
    private NetworkManager networkManager = null;

    // <FAB>
    public ArrayList<FloatingActionButton> fablets = new ArrayList<FloatingActionButton>();
    public Point fabDownPoint;
    public Point fabCurrentPoint;
    public static int FAB_STOP_DRAGGING = 0;
    public static int FAB_START_DRAGGING = 1;
    public int fabStatus = FAB_STOP_DRAGGING;
    public boolean fabDisableClick = false;
    public int fabStartDragThresholdDistance = 75;
    public int selectedEventHolderIndex = -1;
    public EventHolder selectedEventHolder = null;

    private void setUpActionButton(FloatingActionButton fab) {
        // Get screen width and height of the device
        DisplayMetrics metrics;
        int screenWidth = 0, screenHeight = 0;
        metrics = new DisplayMetrics();
        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        Log.v ("Metrics", "width: " + screenWidth);
        Log.v ("Metrics", "height: " + screenHeight);

        // Update placement of action button (default)
        int width = fab.getWidth();
        int height = fab.getHeight();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
        params.leftMargin = (int) screenWidth - (int) (width * 1.1);
        params.topMargin = (int) (screenHeight / 2.0) - (int) (height / 2.0);
    }

//    private void moveUnderTimeline(FloatingActionButton fab) {
//        // Get screen width and height of the device
//        DisplayMetrics metrics;
//        int screenWidth = 0, screenHeight = 0;
//        metrics = new DisplayMetrics();
//        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        screenHeight = metrics.heightPixels;
//        screenWidth = metrics.widthPixels;
//
//        Log.v ("Metrics", "width: " + screenWidth);
//        Log.v ("Metrics", "height: " + screenHeight);
//
//        // Update placement of action button (default)
//        int width = fab.getWidth();
//        int height = fab.getHeight();
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
//        params.leftMargin = (int) 200; // i.e., width of event label
//
//        int eventCount = (int) TimelineListView.getTimelineListView().getChildCount();
//        params.topMargin = (int) TimelineListView.getTimelineListView().getChildAt(eventCount).getScrollY();
//    }
    // </FAB>

    // List of discovered touchscreen devices
    private ArrayList<ViewManagerInterface> views;

    // List of discovered units
    private ArrayList<Unit> units = new ArrayList<Unit>();

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
     * Sends a message to the specified unit.
     * @param unit
     * @param content
     */
    public void sendMessage (Unit unit, String content) {

        // Get source address
        String source = this.networkManager.getInternetAddress ();

        // Get destination address
        // String destination = unit.getInternetAddress();
        String destination = unit.getInternetAddress();
//        String[] destinationOctets = destination.split("\\.");
//        destinationOctets[3] = "255";
//        destination = TextUtils.join(".", destinationOctets);

        // Create message
        Message message = new Message("udp", source, destination, content);
        message.setDeliveryGuaranteed(true);
        Log.v ("Messagez", message.getFromAddress());
        Log.v ("Messagez", message.getToAddress());
        Log.v ("Messagez", message.getContent());

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

    public ArrayList<Unit> getUnits () {
        return this.units;
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

    public Unit getUnitByAddress (String address) {
        for (Unit unit : getUnits ()) {
            if (unit.getInternetAddress ().compareTo (address) == 0) {
                return unit;
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
    public void addUnit(final UUID unitUuid, final String internetAddress) {

//        final Unit newUnit = new Unit(this, unitUuid);
//        newUnit.setInternetAddress(internetAddress);

        Log.v("Content_Manager", "Clay is searching for the unit (UUID: " + unitUuid + ").");

        if (hasUnitByUuid(unitUuid)) {
            Log.v("Content_Manager", "Clay already has the unit (UUID: " + unitUuid + ").");
            return;
        }

        Log.v("Content_Manager", "Clay couldn't find the unit (UUID: " + unitUuid + ").");

        // Restore the unit in Clay's object model
        // Request unit profile from history (i.e., the remote store).

//            getCache().setupRepository();
//            getStore().storeDevice(unit);
        Unit unit = getStore().restoreDevice(unitUuid);

        if (unit != null) {
            Log.v("Content_Manager", "Successfully restored unit (UUID: " + unit.getUuid() + ").");
            Log.v("Content_Manager", "\tIP: " + unit.getInternetAddress());

            // Update restored unit with information from device
            unit.setInternetAddress(internetAddress);

            // Add unit to Clay
            addUnit2(unit);

            // Establish TCP connection
            unit.connectTcp();

            // Show the action palette
            FloatingActionButton fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);
            setUpActionButton (fab);
            fab.show(true);

            // Populate the timeline
            // TODO: Populate from scratch only if no timeline has been programmed for the device
            for (Event event : unit.getTimeline().getEvents()) {
                // <HACK>
                unit.enqueueMessage("start event " + event.getUuid());
                unit.enqueueMessage("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                String content = "set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"";
                unit.enqueueMessage(content);
                // </HACK>
            }
        } else {

            Log.v("Content_Manager", "Failed to restore unit.");

            // Create unit in graph
            Unit newUnit = new Unit (getClay (), unitUuid);
            newUnit.setInternetAddress (internetAddress);
            Log.v("Content_Manager", "Graphed unit (UUID: " + newUnit.getUuid () + ").");
            Log.v("Content_Manager", "\tIP: " + newUnit.getInternetAddress());

            // Cache the unit
            addUnit2 (newUnit);
            Log.v("Content_Manager", "Cached unit (UUID: " + newUnit.getUuid() + ").");

            // TCP connection
            newUnit.connectTcp();

            // Show the action palette
            FloatingActionButton fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);
            setUpActionButton(fab);
            fab.show(true);

            Log.v("Content_Manager", "\tIP: " + newUnit.getInternetAddress());

            // Store the unit
            getStore ().storeDevice (newUnit);
            Log.v("Content_Manager", "Stored new unit (UUID: " + newUnit.getUuid() + ").");

            Log.v("Content_Manager", "\tIP: " + newUnit.getInternetAddress());

            getStore ().storeTimeline (newUnit.getTimeline ());
            Log.v ("Content_Manager", "Stored new timeline (UUID: " + newUnit.getTimeline().getUuid() + ").");

            Log.v("Content_Manager", "\tIP: " + newUnit.getInternetAddress());

            // Archive the unit
            // TODO:

        }
    }

    private void addUnit2 (Unit unit) {
        Log.v ("Content_Manager", "addUnit2");

        if (!this.units.contains (unit)) {

            // Add unit to present (i.e., local cache).
            this.units.add(unit);
            Log.v("Content_Manager", "Successfully added timeline.");

            // Caches actions on the device
            if (getClay().hasCache()) {
                // Only cache the basic actions
                for (Action action : getClay().getCache().getActions()) {
                    if (action.hasScript()) {

                        // Sends actions to the device's cache
                        // i.e., 'cache action <action-uuid> "<action-regex>"'
//                        unit.sendMessage ("cache action " + action.getUuid());
                        //unit.sendMessage ("cache action " + action.getUuid() + " \"" + action.getScript().getStatePattern() + "\"");

//                        UUID uuid = unit.getUuid();
//                        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
//                        bb.putLong(uuid.getMostSignificantBits());
//                        bb.putLong(uuid.getLeastSignificantBits());
//                        byte[] uuidBytes = bb.array();
////                        return bb.array();
//
//                        String byteString = "";
//                        for (int i = 0; i < uuidBytes.length; i++) {
//                            byteString = "" + uuidBytes[i];
//                        }
//                        Log.v ("New_Map", byteString);

                    }
                }
            }

            /*
            // <TEST>
            // Add a random number of random actions to the unit.
            // This will be replaced with the actual actions on the device's timeline.
            Random r = new Random ();
            int behaviorCount = r.nextInt(20);
            for (int i = 0; i < behaviorCount; i++) {

                // Get list of the available behavior types
                ArrayList<Action> actions = getCache().getActions();


                // Generate a random behavior
                if (actions.size() > 0) {

                    // Select random behavior type
                    int behaviorSelection = r.nextInt(actions.size());
                    UUID behaviorUuid = actions.get(behaviorSelection).getUuid();

                    Log.v("Behavior_DB", "BEFORE unit.storeAction:");
                    getStore().restoreAction(behaviorUuid.toString());

                    // Generate a behavior of the selected type
                    unit.cacheAction (behaviorUuid.toString());

                    Log.v("Behavior_DB", "AFTER unit.storeAction:");
                    getStore().restoreAction(behaviorUuid.toString());
                }
            }
            // </TEST>
            */

//            Log.v("Behavior_Count", "Unit behavior count: " + unit.getTimeline().getEvents().size());
//
            // Add timelines to attached views
            for (ViewManagerInterface view : this.views) {
                // TODO: (1) addUnit a page to the ViewPager
                // TODO: (2) Add a tab to the action bar to support navigation to the specified page.
                view.addUnitView(unit);
            }
        }
    }

    public boolean hasUnits () {
        return this.units.size () > 0;
    }

    public boolean hasUnit (Unit unit) {
        return this.units.contains (unit);
    }

    public boolean hasUnitByUuid (UUID unitUuid) {
        for (Unit unit : getUnits ()) {
            if (unit.getUuid().compareTo(unitUuid) == 0) {
                return true;
            }
        }
        return false;
    }

    public Unit getUnitByUuid (UUID unitUuid) {
        for (Unit unit : getUnits ()) {
            if (unit.getUuid().compareTo(unitUuid) == 0) {
                return unit;
            }
        }
        return null;
    }

    public boolean hasUnitByAddress (String address) {
        for (Unit unit : getUnits()) {
            if (unit.getInternetAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

//    public void removeUnit (Unit unit) {
//        if (hasUnit(unit)) {
//            this.units.remove (unit);
//        }
//    }

    public void generateStore () {

        if (hasStore()) {

            UUID uuid;

            // light
            uuid = UUID.fromString("1470f5c4-eaf1-43fb-8fb3-d96dc4e2bee4");
            if (!getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"light\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript(uuid, "light", "((T|F) ){11}(T|F)", "000000 000000 000000 000000 000000 000000 000000 000000 000000 000000 000000 000000");
            }

            // signal
            uuid = UUID.fromString("bdb49750-9ead-466e-96a0-3aa88e7d246c");
            if (!getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"signal\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript(uuid, "signal", "regex", "FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL");
            }

            // message
            uuid = UUID.fromString("99ff8f6d-a0e7-4b6e-8033-ee3e0dc9a78e");
            if (!getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"message\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript(uuid, "message", "regex", "UDP Other \"hello\"");
            }

            // tone
            uuid = UUID.fromString("16626b1e-cf41-413f-bdb4-0188e82803e2");
            if (!getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"tone\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript(uuid, "tone", "regex", "frequency 0 hz 0 ms");
            }

            // pause
            uuid = UUID.fromString("56d0cf7d-ede6-4529-921c-ae9307d1afbc");
            if (!getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"pause\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript(uuid, "pause", "regex", "250");
            }

            // say
            uuid = UUID.fromString("269f2e19-1fc8-40f5-99b2-6ca67e828e70");
            if (!getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"say\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript(uuid, "say", "regex", "oh, that's great");
            }
        }
    }

    /**
     * Creates a new behavior with the specified tag and state and stores it.
     * @param tag
     * @param defaultState
     */
    private void generateBehaviorScript (UUID uuid, String tag, String stateSpacePattern, String defaultState) {

        Log.v ("Content_Manager", "Creating script.");

        // Create behavior (and state) for the behavior script
        Script script = new Script (uuid, tag, stateSpacePattern, defaultState);

        // Cache the behavior
//        this.cache(script);

        // Store the behavior
        if (hasStore()) {
            getStore().storeScript(script);
        }

        generateBasicBehavior(script);

    }

    private void generateBasicBehavior (Script script) {

        Log.v ("Content_Manager", "Generating basic behavior for script.");

        // Generate basic actions for all behavior scripts
        Action basicAction = new Action(script);
        getStore ().storeAction(basicAction);
    }

    /**
     * Populates cache with all behavior scripts and actions from the available content managers.
     */
    public void populateCache () {
        Log.v("Content_Manager", "populateCache");

        if (hasStore()) {
            Log.v("Content_Manager", "populateCache");

            // Restore behavior scripts and addUnit them to the cache
            getStore().restoreScripts();
            Log.v("Content_Manager", "Restored behavior scripts:");
            for (Script script : getCache().getScripts()) {
                Log.v("Content_Manager", "\t" + script.getUuid());
            }

            // Restore actions and addUnit them to the cache
            getStore().restoreActions();
            Log.v("Content_Manager", "Restored actions:");
            for (Action action : getCache().getActions()) {
                //Log.v("Content_Manager", "\t" + action.getUuid());
                printRestoredBehavior(action, 1);
            }
        }
    }

    private void printRestoredBehavior (Action action, int tabCount) {
        String tabString = "";
        for (int i = 0; i < tabCount; i++) {
            tabString += "\t";
        }
        Log.v ("Content_Manager", tabString + "Action (UUID: " + action.getUuid() + ")");
        if (!action.hasScript()) {
            for (Action childAction : action.getActions()) {
                printBehavior(childAction, tabCount + 1);
            }
        } else {
//            Log.v("Content_Manager", tabString + "\tScript (UUID: " + action.getScript().getUuid() + ")");
//            Log.v("Content_Manager", tabString + "\tState (UUID: " + action.getState().getUuid() + ")");
        }
    }

    public void simulateSession (boolean addBehaviorToTimeline, int behaviorCount, boolean addAbstractBehaviorToTimeline) {
        Log.v("Content_Manager", "simulateSession");

        // Discover first device
        UUID unitUuidA = UUID.fromString("403d4bd4-71b0-4c6b-acab-bd30c6548c71");
        getClay().addUnit(unitUuidA, "10.1.10.29");
        Unit foundUnit = getUnitByUuid(unitUuidA);

        // Discover second device
        UUID unitUuidB = UUID.fromString("903d4bd4-71b0-4c6b-acab-bd30c6548c78");
        getClay().addUnit(unitUuidB, "192.168.1.123");

        if (addBehaviorToTimeline) {
            for (int i = 0; i < behaviorCount; i++) {
                // Create action based on action script
                Log.v("Content_Manager", "> Creating action");
                Random r = new Random();
                int selectedBehaviorIndex = r.nextInt(getClay().getCache().getActions().size());
//                Script selectedBehaviorScript = getClay().getCache().getScripts().get(selectedBehaviorIndex);
//                Action action = new Action(selectedBehaviorScript);
                Action action = getClay().getCache().getActions().get(selectedBehaviorIndex);
                getClay().getStore().storeAction(action);

                // Create event for the action and add it to the unit's timeline
                Log.v("Content_Manager", "> Unit (UUID: " + foundUnit.getUuid() + ")");
                Event event = new Event(foundUnit.getTimeline(), action);
                getClay().getUnitByUuid(unitUuidA).getTimeline().addEvent(event);
                getClay().getStore().storeEvent(event);
                // TODO: Update unit
            }
        }

        if (addAbstractBehaviorToTimeline) {
            // Create action based on action script
            Log.v("Content_Manager", "> Creating action");
//            Action action = new Action("so high");
//            action.setDescription("oh yeah!");
//            action.addAction(foundUnit.getTimeline().getEvents().get(0).getAction());
//            action.addAction(foundUnit.getTimeline().getEvents().get(1).getAction());
//            getClay().getStore().storeAction(action);
            ArrayList<Action> children = new ArrayList<Action>();
            ArrayList<State> states = new ArrayList<State>();
            children.add(foundUnit.getTimeline().getEvents().get(0).getAction());
            states.addAll(foundUnit.getTimeline().getEvents().get(0).getState());
            children.add(foundUnit.getTimeline().getEvents().get(1).getAction());
            states.addAll(foundUnit.getTimeline().getEvents().get(1).getState());
            Action action = getClay().getStore().getActionComposition(children);

            // remove events for abstracted actions
            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0));
            foundUnit.getTimeline().getEvents().remove(0); // if store action successful
            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1));
            foundUnit.getTimeline().getEvents().remove(1); // if store action successful

            // Create event for the action and add it to the unit's timeline
            Log.v("Content_Manager", "> Unit (UUID: " + foundUnit.getUuid() + ")");
            Event event = new Event(foundUnit.getTimeline(), action);
            // insert new event for abstract action
            //            foundUnit.getTimeline().addEvent(event);
            event.getState().clear();
            event.getState().addAll(states);
            Log.v("New_Behavior_Parent", "Added " + states.size() + " states to new event.");
            for (State state : event.getState()) {
                Log.v("New_Behavior_Parent", "\t" + state.getState());
            }
            foundUnit.getTimeline().getEvents().add(0, event); // if store event was successful
            getClay().getStore().storeEvent(event);
            // TODO: Update unit
        }

//        if (addAbstractBehaviorToTimeline) {
//            // Create behavior based on behavior script
//            Log.v("Content_Manager", "> Creating behavior");
//            Action behavior = new Action("so so high");
//            behavior.setDescription("oh yeah!");
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0), null);
//            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(0).getAction());
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1), null);
//            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(1).getAction());
//            getClay().getStore().storeAction(behavior);
//            // remove events for abstracted actions
//            foundUnit.getTimeline().getEvents().remove(0); // if store behavior successful
//            foundUnit.getTimeline().getEvents().remove(1); // if store behavior successful
//
//            // Create event for the behavior and add it to the unit's timeline
//            Log.v("Content_Manager", "> Unit (UUID: " + foundUnit.getUuid() + ")");
//            Event event = new Event(foundUnit.getTimeline(), behavior);
//            // insert new event for abstract behavior
//            //            foundUnit.getTimeline().addEvent(event);
//            foundUnit.getTimeline().getEvents().add(0, event); // if store event was successful
//            getClay().getStore().storeEvent(event);
//            // TODO: Update unit
//        }

//        getClay().notifyChange(event);

        getClay().getStore().writeDatabase();

        for (Unit unit : getClay().getUnits()) {
            Log.v ("Content_Manager", "Unit (UUID: " + unit.getUuid() + ")");
            Log.v ("Content_Manager", "\tTimeline (UUID: " + unit.getTimeline().getUuid() + ")");

            int tabCount = 3;
            for (Event e : unit.getTimeline().getEvents()) {
                Log.v ("Content_Manager", "\t\tEvent (UUID: " + e.getUuid() + ")");
                // TODO: Recursively print out the behavior tree
                printBehavior (e.getAction(), tabCount);
            }
        }
    }

    private void printBehavior (Action action, int tabCount) {
        String tabString = "";
        for (int i = 0; i < tabCount; i++) {
            tabString += "\t";
        }
        Log.v ("Content_Manager", tabString + "Action (UUID: " + action.getUuid() + ")");
        if (!action.hasScript()) {
            for (Action childAction : action.getActions()) {
                printBehavior(childAction, tabCount + 1);
            }
        } else {
            Log.v("Content_Manager", tabString + "\tScript (UUID: " + action.getScript().getUuid() + ")");
//            Log.v("Content_Manager", tabString + "\tState (UUID: " + action.getState().getUuid() + ")");
        }
    }

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
    private boolean hasStore() {
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
     * Adds a Unit to Clay's object model.
     * @param unit The Unit to addUnit to Clay's object model.
     */
    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    /**
     * Push updated unit object to views so they can show the updated information
     * @param unit
     */
    public void updateUnitView (Unit unit) {

        for (ViewManagerInterface view : this.views) {
            view.refreshListViewFromData(unit);
        }
    }

    /**
     * Requests a view for a unit.
     * @param unit The unit for which a view is requested.
     */
    public void addUnitView (Unit unit) {

        // TODO: (?) Add DeviceViewFragment to a list here?

        // <HACK>
        // Make sure no units are in an invalid state (null reference)
        boolean addView = true;
        for (Event event : unit.getTimeline().getEvents()) {
            if (event.getAction() == null) {
                addView = false;
            }
        }
        if (addView) {
            addUnitView2(unit);
        }
        // </HACK>
    }

    private void addUnitView2(Unit unit) {
        // Add timelines to attached views
        for (ViewManagerInterface view : this.views) {
            // TODO: (1) addUnit a page to the ViewPager

            // (2) Add a tab to the action bar to support navigation to the specified page.
            Log.v ("CM_Log", "addUnitView2");
            Log.v ("CM_Log", "\tunit: " + unit);
            Log.v ("CM_Log", "\tunit/timeline: " + unit.getTimeline());
            view.addUnitView(unit);
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
