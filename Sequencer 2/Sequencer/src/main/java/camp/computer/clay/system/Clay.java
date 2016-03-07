package camp.computer.clay.system;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

public class Clay {

    // <HACK>
    // TODO: Move this out of the Clay object model.
    static private Context context;
    // </HACK>

    // Resource management systems (e.g., networking, messaging, content)
    private ContentManagerInterface contentManager = null;
    private MessageManager messageManager = null;
    private NetworkManager networkManager = null;

    // List of discovered touchscreen devices
    private ArrayList<ViewManagerInterface> views;

    // List of discovered units
    private ArrayList<Unit> units = new ArrayList<Unit>();

    // List of behaviors cached on this device
    private CacheManager cacheManager = null;

    // The calendar used by Clay
    private Calendar calendar = Calendar.getInstance (TimeZone.getTimeZone("GMT"));

    public Clay() {

        this.views = new ArrayList<ViewManagerInterface>(); // Create list to store views.
        this.messageManager = new MessageManager(this); // Start the communications systems
        this.networkManager = new NetworkManager (this); // Start the networking systems

        this.cacheManager = new CacheManager(this); // Set up behavior repository
    }

    // <HACK>
    // TODO: Move this into Clay, then just reference Clay's platform context.
    public static void setContext (Context context) {
        Clay.context = context;
    }
    // </HACK>

    // <HACK>
    // TODO: Returns the context.
    public static Context getContext() {
        return Clay.context;
    }
    // </HACK>

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
     * Adds a content manager for use by Clay. Retrieves the basic behaviors provided by the
     * content manager and makes them available in Clay.
     */
    public void addContentManager (ContentManagerInterface contentManager) {
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
        String[] destinationOctets = destination.split("\\.");
        destinationOctets[3] = "255";
        destination = TextUtils.join(".", destinationOctets);

        // Create message
        Message message = new Message("udp", source, destination, content);

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

    public ContentManagerInterface getStore() {
        return this.contentManager;
    }

    public ArrayList<Unit> getUnits () {
        return this.units;
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
//            getStore().storeUnit(unit);
        getStore().restoreUnit(unitUuid, new ContentManagerInterface.Callback() {
            @Override
            public void onSuccess(Object object) {

                final Unit restoredUnit = (Unit) object;
                Log.v("Content_Manager", "Successfully restored unit (UUID: " + restoredUnit.getUuid() + ").");

                // Update restored unit with information from device
                restoredUnit.setInternetAddress(internetAddress);

                addUnit2(restoredUnit);

//                Log.v("Content_Manager", "Clay is searching for the timeline (UUID: " + restoredUnit.getTimelineUuid() + ").");

                /*
                // Restore timeline
                getStore().restoreTimeline(restoredUnit, restoredUnit.getTimelineUuid(), new ContentManagerInterface.Callback() {
                    @Override
                    public void onSuccess(Object object) {
                        Log.v("Content_Manager", "Successfully restored timeline.");
                        Timeline timeline = (Timeline) object;
                        restoredUnit.setTimeline(timeline);

                        // Restore events
                        getStore().restoreEvents(timeline);

                        // Add unit to cache
                        addUnit2(restoredUnit);

//                        // Update view
//                        updateUnitView(restoredUnit);
                    }

                    @Override
                    public void onFailure() {
                        Log.v("Content_Manager", "Failed to restore timeline.");

                        // Graph timeline locally since it was not found
                        Timeline newTimeline = new Timeline (restoredUnit.getTimelineUuid());
                        newTimeline.setUnit(restoredUnit);
                        restoredUnit.setTimeline(newTimeline);
                        Log.v("Content_Manager", "Cached new timeline (UUID: " + newTimeline.getUuid() + ").");

                        // Store timeline
                        getStore().storeTimeline(restoredUnit.getTimeline());
                        Log.v("Content_Manager", "Saved new timeline (UUID: " + newTimeline.getUuid() + ").");

                        // Add unit to cache
                        addUnit2(restoredUnit);
                    }
                });
                */

                // Restore events
//                    getStore().restoreEvents(timeline);

                // Update view
//                    updateUnitView(newUnit);
            }

            @Override
            public void onFailure() {
                Log.v("Content_Manager", "Failed to restore unit.");

                // Create unit in graph
                Unit newUnit = new Unit(getClay(), unitUuid);
                newUnit.setInternetAddress(internetAddress);
                Log.v("Content_Manager", "Graphed unit (UUID: " + newUnit.getUuid() + ").");

                // Cache the unit
                addUnit2(newUnit);
                Log.v("Content_Manager", "Cached unit (UUID: " + newUnit.getUuid() + ").");

                // Store the unit
                getStore().storeUnit(newUnit);
                Log.v("Content_Manager", "Stored new unit (UUID: " + newUnit.getUuid() + ").");

                getStore().storeTimeline(newUnit.getTimeline());
                Log.v("Content_Manager", "Stored new timeline (UUID: " + newUnit.getTimeline().getUuid() + ").");

                // Archive the unit
                // TODO:
            }
        });
//            getStore().storeTimeline(unit.getTimeline());

//            // Add events if they don't already exist
//            for (Event event : unit.getTimeline().getEvents()) {
//                if (!getStore().hasEvent(event)) {
//                    getStore().restoreEvents(unit.getTimeline());
//                }
//            }




    }

    private void addUnit2 (Unit unit) {
        Log.v ("Content_Manager", "addUnit2");

        if (!this.units.contains (unit)) {

            // Add unit to present (i.e., local cache).
            this.units.add(unit);
            Log.v("Content_Manager", "Successfully added timeline.");

            /*
            // <TEST>
            // Add a random number of random behaviors to the unit.
            // This will be replaced with the actual behaviors on the device's timeline.
            Random r = new Random ();
            int behaviorCount = r.nextInt(20);
            for (int i = 0; i < behaviorCount; i++) {

                // Get list of the available behavior types
                ArrayList<Behavior> behaviors = getCache().getBehaviors();


                // Generate a random behavior
                if (behaviors.size() > 0) {

                    // Select random behavior type
                    int behaviorSelection = r.nextInt(behaviors.size());
                    UUID behaviorUuid = behaviors.get(behaviorSelection).getUuid();

                    Log.v("Behavior_DB", "BEFORE unit.storeBehavior:");
                    getStore().restoreBehavior(behaviorUuid.toString());

                    // Generate a behavior of the selected type
                    unit.addBehavior (behaviorUuid.toString());

                    Log.v("Behavior_DB", "AFTER unit.storeBehavior:");
                    getStore().restoreBehavior(behaviorUuid.toString());
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
            if (!getCache().hasBehaviorScript ("lights")) {
                Log.v("Clay_Behavior_Repo", "\"lights\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript("lights", "F F F F F F F F F F F F");
            }

            if (!getCache().hasBehaviorScript ("io")) {
                Log.v("Clay_Behavior_Repo", "\"lights\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript("io", "FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL");
            }

            if (!getCache().hasBehaviorScript ("message")) {
                Log.v("Clay_Behavior_Repo", "\"message\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript("message", "hello");
            }

            if (!getCache().hasBehaviorScript ("wait")) {
                Log.v("Clay_Behavior_Repo", "\"wait\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript("wait", "250");
            }

            if (!getCache().hasBehaviorScript ("say")) {
                Log.v("Clay_Behavior_Repo", "\"say\" behavior not found in the repository. Adding it.");
                getClay().generateBehaviorScript("say", "oh, that's great");
            }
        }
    }

    /**
     * Creates a new behavior with the specified tag and state and stores it.
     * @param tag
     * @param defaultState
     */
    private void generateBehaviorScript (String tag, String defaultState) {

        Log.v ("Content_Manager", "Creating basic behavior.");

        // Create behavior (and state) for the behavior script
        BehaviorScript behaviorScript = new BehaviorScript (UUID.randomUUID(), tag, defaultState);

        // Cache the behavior
//        this.cache(behaviorScript);

        // Store the behavior
        if (hasStore()) {
            getStore().storeBehaviorScript(behaviorScript);
        }

    }

    /**
     * Populates cache with all behavior scripts and behaviors from the available content managers.
     */
    public void populateCache () {
        Log.v("Content_Manager", "populateCache");

         //getStore().resetDatabase();
         //generateStore();

        if (hasStore()) {
            Log.v ("Content_Manager", "populateCache");

            // Restore behavior scripts and addUnit them to the cache
            getStore().restoreBehaviorScripts();
            Log.v("Content_Manager", "Restored behavior scripts:");
            for (BehaviorScript behaviorScript : getCache().getBehaviorScripts()) {
                Log.v("Content_Manager", "\t" + behaviorScript.getUuid());
            }

            // Restore behaviors and addUnit them to the cache
            getStore().restoreBehaviors();
            Log.v("Content_Manager", "Restored behaviors:");
            for (Behavior behavior : getCache().getBehaviors()) {
                //Log.v("Content_Manager", "\t" + behavior.getUuid());
                printRestoredBehavior(behavior, 1);
            }
        }
    }

    private void printRestoredBehavior (Behavior behavior, int tabCount) {
        String tabString = "";
        for (int i = 0; i < tabCount; i++) {
            tabString += "\t";
        }
        Log.v ("Content_Manager", tabString + "Behavior (UUID: " + behavior.getUuid() + ")");
        if (!behavior.hasScript()) {
            for (Behavior childBehavior : behavior.getBehaviors()) {
                printBehavior(childBehavior, tabCount + 1);
            }
        } else {
//            Log.v("Content_Manager", tabString + "\tScript (UUID: " + behavior.getScript().getUuid() + ")");
//            Log.v("Content_Manager", tabString + "\tState (UUID: " + behavior.getState().getUuid() + ")");
        }
    }

    public void simulateSession (boolean addBehaviorToTimeline, int behaviorCount, boolean addAbstractBehaviorToTimeline) {
        Log.v("Content_Manager", "simulateSession");

        // Discover unit
//        UUID unitUuid = UUID.randomUUID();
        UUID unitUuid = UUID.fromString("403d4bd4-71b0-4c6b-acab-bd30c6548c71");
        getClay().addUnit(unitUuid, "192.168.1.122");
        Unit foundUnit = getUnitByUuid(unitUuid);

        if (addBehaviorToTimeline) {
            for (int i = 0; i < behaviorCount; i++) {
                // Create behavior based on behavior script
                Log.v("Content_Manager", "> Creating behavior");
                Random r = new Random();
                BehaviorScript selectedBehaviorScript = getClay().getCache().getBehaviorScripts().get(r.nextInt(getClay().getCache().getBehaviorScripts().size()));
                Behavior behavior = new Behavior(selectedBehaviorScript);
                getClay().getStore().storeBehavior(behavior);

                // Create event for the behavior and add it to the unit's timeline
                Log.v("Content_Manager", "> Unit (UUID: " + foundUnit.getUuid() + ")");
                Event event = new Event(foundUnit.getTimeline(), behavior);
                getClay().getUnitByUuid(unitUuid).getTimeline().addEvent(event);
                getClay().getStore().storeEvent(event);
                // TODO: Update unit
            }
        }

        if (addAbstractBehaviorToTimeline) {
            // Create behavior based on behavior script
            Log.v("Content_Manager", "> Creating behavior");
            Behavior behavior = new Behavior("so high");
            behavior.setDescription("oh yeah!");
            behavior.addBehavior(foundUnit.getTimeline().getEvents().get(0).getBehavior());
            behavior.addBehavior(foundUnit.getTimeline().getEvents().get(1).getBehavior());
            getClay().getStore().storeBehavior(behavior);
            // remove events for abstracted behaviors
            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0), null);
            foundUnit.getTimeline().getEvents().remove(0); // if store behavior successful
            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1), null);
            foundUnit.getTimeline().getEvents().remove(1); // if store behavior successful

            // Create event for the behavior and add it to the unit's timeline
            Log.v("Content_Manager", "> Unit (UUID: " + foundUnit.getUuid() + ")");
            Event event = new Event(foundUnit.getTimeline(), behavior);
            // insert new event for abstract behavior
            //            foundUnit.getTimeline().addEvent(event);
            foundUnit.getTimeline().getEvents().add(0, event); // if store event was successful
            getClay().getStore().storeEvent(event);
            // TODO: Update unit
        }

        if (addAbstractBehaviorToTimeline) {
            // Create behavior based on behavior script
            Log.v("Content_Manager", "> Creating behavior");
            Behavior behavior = new Behavior("so so high");
            behavior.setDescription("oh yeah!");
            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0), null);
            behavior.addBehavior(foundUnit.getTimeline().getEvents().get(0).getBehavior());
            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1), null);
            behavior.addBehavior(foundUnit.getTimeline().getEvents().get(1).getBehavior());
            getClay().getStore().storeBehavior(behavior);
            // remove events for abstracted behaviors
            foundUnit.getTimeline().getEvents().remove(0); // if store behavior successful
            foundUnit.getTimeline().getEvents().remove(1); // if store behavior successful

            // Create event for the behavior and add it to the unit's timeline
            Log.v("Content_Manager", "> Unit (UUID: " + foundUnit.getUuid() + ")");
            Event event = new Event(foundUnit.getTimeline(), behavior);
            // insert new event for abstract behavior
            //            foundUnit.getTimeline().addEvent(event);
            foundUnit.getTimeline().getEvents().add(0, event); // if store event was successful
            getClay().getStore().storeEvent(event);
            // TODO: Update unit
        }

//        getClay().notifyChange(event);


        for (Unit unit : getClay().getUnits()) {
            Log.v ("Content_Manager", "Unit (UUID: " + unit.getUuid() + ")");
            Log.v ("Content_Manager", "\tTimeline (UUID: " + unit.getTimeline().getUuid() + ")");

            int tabCount = 3;
            for (Event e : unit.getTimeline().getEvents()) {
                Log.v ("Content_Manager", "\t\tEvent (UUID: " + e.getUuid() + ")");
                // TODO: Recursively print out the behavior tree
                printBehavior (e.getBehavior(), tabCount);
            }
        }
    }

    private void printBehavior (Behavior behavior, int tabCount) {
        String tabString = "";
        for (int i = 0; i < tabCount; i++) {
            tabString += "\t";
        }
        Log.v ("Content_Manager", tabString + "Behavior (UUID: " + behavior.getUuid() + ")");
        if (!behavior.hasScript()) {
            for (Behavior childBehavior : behavior.getBehaviors()) {
                printBehavior(childBehavior, tabCount + 1);
            }
        } else {
            Log.v("Content_Manager", tabString + "\tScript (UUID: " + behavior.getScript().getUuid() + ")");
            Log.v("Content_Manager", tabString + "\tState (UUID: " + behavior.getState().getUuid() + ")");
        }
    }

    /**
     * Adds the behavior, caches it, and stores it.
     */
    public void addBehavior (Behavior behavior) {

        // Create behavior (and state) for the behavior script
//        BehaviorScript behaviorScript = new BehaviorScript (UUID.randomUUID(), tag, defaultState);
//        Behavior behavior = new Behavior (behaviorScript);

        // Cache the behavior
        this.cache(behavior);

//        // Store the behavior
//        if (hasStore()) {
//            getStore().storeBehavior(behavior);
//        }

    }

    public void addBehaviorScript(BehaviorScript behaviorScript) {

        if (hasCache ()) {
            this.cache (behaviorScript);
        }

    }

    /**
     * Returns true if Clay has a content manager.
     * @return True if Clay has a content manager. False otherwise.
     */
    private boolean hasStore() {
        return this.contentManager != null;
    }

    public Behavior getBehavior (UUID behaviorUuid) {
        if (hasCache()) {
            if (getCache().hasBehavior(behaviorUuid.toString())) {
                return getCache().getBehavior(behaviorUuid);
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
     * Caches a Behavior in memory.
     * @param behavior The Behavior to cache.
     */
    public void cache (Behavior behavior) {
        this.getCache().cache(behavior);
    }

    /**
     * Caches a behavior interface to the cache.
     * @param behaviorScript The behavior interface to cache.
     */
    public void cache (BehaviorScript behaviorScript) {
        this.getCache().cache (behaviorScript);
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
            if (event.getBehavior() == null) {
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
                getStore().storeBehavior(event.getBehavior());

                // Store behavior state for behavior
//                getStore().storeBehaviorState(event.getBehavior().getState());

//            }
        }

    }
}
