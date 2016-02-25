package camp.computer.clay.system;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Clay {

    // <HACK>
    // TODO: Move this out of the Clay object model.
    static private Context context;
    // </HACK>

    // Resource management systems (e.g., networking, messaging, content)
    private ContentManager contentManager = null;
    private MessageManager messageManager = null;
    private NetworkManager networkManager = null;

    // List of discovered touchscreen devices
    private ArrayList<ViewManagerInterface> views;

    // List of discovered units
    private ArrayList<Unit> units = new ArrayList<Unit>();

    // List of behaviors cached on this device
    private BehaviorCacheManager behaviorCacheManager = null;

    // The calendar used by Clay
    private Calendar calendar = Calendar.getInstance (TimeZone.getTimeZone("GMT"));

    public Clay() {

        this.views = new ArrayList<ViewManagerInterface>(); // Create list to store views.
        this.messageManager = new MessageManager (this); // Start the communications systems
        this.networkManager = new NetworkManager (this); // Start the networking systems
        this.contentManager = new ContentManager (this); // Start the content management system

        this.behaviorCacheManager = new BehaviorCacheManager(this); // Set up behavior repository
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

    public BehaviorCacheManager getBehaviorCacheManager() {
        return this.behaviorCacheManager;
    }

    public ContentManager getContentManager() {
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

    /**
     * Adds the specified unit to Clay's operating environment.
     */
    public void addUnit (UUID unitUuid, String internetAddress) {

        Unit unit = new Unit(this, unitUuid);
        unit.setInternetAddress(internetAddress);

        if (!this.units.contains (unit)) {

            // Add unit to present (i.e., local cache).
            this.units.add(unit);

            /*
            // <TEST>
            // Add a random number of random behaviors to the unit.
            // This will be replaced with the actual behaviors on the device's timeline.
            Random r = new Random ();
            int behaviorCount = r.nextInt(20);
            for (int i = 0; i < behaviorCount; i++) {

                // Get list of the available behavior types
                ArrayList<Behavior> behaviors = getBehaviorCacheManager().getCachedBehaviors();


                // Generate a random behavior
                if (behaviors.size() > 0) {

                    // Select random behavior type
                    int behaviorSelection = r.nextInt(behaviors.size());
                    UUID behaviorUuid = behaviors.get(behaviorSelection).getUuid();

                    Log.v("Behavior_DB", "BEFORE unit.storeBehavior:");
                    getContentManager().restoreBehavior(behaviorUuid.toString());

                    // Generate a behavior of the selected type
                    unit.addBehavior (behaviorUuid.toString());

                    Log.v("Behavior_DB", "AFTER unit.storeBehavior:");
                    getContentManager().restoreBehavior(behaviorUuid.toString());
                }
            }
            // </TEST>
            */

//            Log.v("Behavior_Count", "Unit behavior count: " + unit.getTimeline().getEvents().size());
//
//            // Add timelines to attached views
//            for (ViewManagerInterface view : this.views) {
//                // TODO: (1) add a page to the ViewPager
//
//                // (2) Add a tab to the action bar to support navigation to the specified page.
//                view.addUnitView(unit);
//            }

            // Restore the unit in Clay's object model
            // Request unit profile from history (i.e., the remote store).
            getContentManager().restoreUnit(unitUuid);
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

    /**
     * Creates a new behavior with the specified tag and state, caches it, and stores it.
     * @param tag
     * @param defaultStateString
     */
    public void createBehavior (String tag, String defaultStateString) {

        // Create behavior and behavior state
        Behavior behavior = new Behavior (tag, defaultStateString);
        BehaviorState behaviorState = new BehaviorState(behavior, defaultStateString);

        // Cache the behavior locally
        this.cacheBehavior(behavior);

        // Store the behavior remotely
        if (hasContentManager()) {
            getContentManager().storeBehaviorState(behaviorState);
            getContentManager().storeBehavior(behavior);
        }
    }

    /**
     * Returns true if Clay has a content manager.
     * @return True if Clay has a content manager. False otherwise.
     */
    private boolean hasContentManager() {
        return this.contentManager != null;
    }

    public Behavior getBehavior (UUID behaviorUuid) {
        if (hasBehaviorCacheManager()) {
            return getBehaviorCacheManager().getBehavior(behaviorUuid);
        }
        return null;
    }

    private boolean hasBehaviorCacheManager() {
        if (getBehaviorCacheManager() != null) {
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

    public void cacheBehavior(Behavior behavior) {
        this.getBehaviorCacheManager().cacheBehavior(behavior);
    }

    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    public void updateUnitView (Unit unit) {
//        todo
    }

    /**
     * Requests a view for a unit.
     * @param unit The unit for which a view is requested.
     */
    public void addUnitView (Unit unit) {

        // TODO: (?) Add UnitViewFragment to a list here?

        // <HACK>
        // Make sure no units are in an invalid state (null reference)
        boolean addView = true;
        for (Event event : unit.getTimeline().getEvents()) {
            if (event.getBehavior() == null || event.getBehaviorState() == null) {
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
            // TODO: (1) add a page to the ViewPager

            // (2) Add a tab to the action bar to support navigation to the specified page.
            Log.v ("CM_Log", "addUnitView2");
            Log.v ("CM_Log", "\tunit: " + unit);
            Log.v ("CM_Log", "\tunit/timeline: " + unit.getTimeline());
            view.addUnitView(unit);
        }
    }
}
