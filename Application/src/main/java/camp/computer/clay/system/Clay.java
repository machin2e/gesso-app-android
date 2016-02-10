package camp.computer.clay.system;

import android.content.Context;
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

        // Prepare message
        String source = this.networkManager.getInternetAddress ();
        String destination = unit.getInternetAddress();
        Message message = new Message("udp", source, destination, content);

        // Queue message
        messageManager.queueOutgoingMessage(message);

//        // <HACK>
//        // The destination should be the unit's address, not the broadcast address.
//        destination = DatagramManager.BROADCAST_ADDRESS; // unit.getInternetAddress();
//        Log.v ("UDP_Destination", "destination: " + unit.getInternetAddress());
//        Message broadcast = new Message("udp", source, destination, content);
//        messageManager.queueOutgoingMessage(broadcast);
//        // </HACK>
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

    public void addUnit (Unit unit) {
        if (!this.units.contains (unit)) {

            // Add unit to present (i.e., local cache).
            this.units.add(unit);
            // TODO: Move this into a unit manager?
//            this.getContentManager().addUnit(unit);

            // <TEST>
            // Add a random number of random behaviors to the unit.
            // This will be replaced with the actual behaviors on the device's timeline.
            Random r = new Random ();
            int behaviorCount = r.nextInt(20);
            for (int i = 0; i < behaviorCount; i++) {

                // Get list of the available behavior types
//                ArrayList<String> behaviorTypes = new ArrayList<String>();
//                behaviorTypes.add ("lights");
//                behaviorTypes.add ("io");
//                behaviorTypes.add ("message");
//                behaviorTypes.add ("wait");
//                behaviorTypes.add ("say");

                ArrayList<Behavior> behaviors = getBehaviorCacheManager().getCachedBehaviors();

                // Select random behavior type
                int behaviorSelection = r.nextInt(behaviors.size());
                UUID behaviorUuid = behaviors.get(behaviorSelection).getUuid();

                // Generate a behavior of the selected type
                unit.addBehavior (behaviorUuid.toString());
            }
            // </TEST>

            Log.v("Behavior_Count", "unit behavior count: " + unit.getTimeline().getBehaviors().size());

            // Add timelines to attached views
            for (ViewManagerInterface view : this.views) {
                // TODO: (1) add a page to the ViewPager

                // (2) Add a tab to the action bar to support navigation to the specified page.
                view.addUnitView(unit);
            }

            Log.v ("Add_Unit", "Adding unit");

            // Retrieve unit from memory (i.e., contentManager).
            //getContentManager().addUnit(unit);
            //getContentManager ().getUnit (unit.getUuid ());
        }
    }

    public boolean hasUnits () {
        return this.units.size () > 0;
    }

    public boolean hasUnit (Unit unit) {
        return this.units.contains (unit);
    }

    public boolean hasUnitByUuid (UUID unitUuid) {
        Log.v("Clay_Time", "Looking for unit (in set of " + getUnits().size() + ") with UUID " + unitUuid + "...");
        for (Unit unit : getUnits ()) {
            Log.v("Clay_Time", "\t...checking address " + unit.getInternetAddress());
            if (unit.getUuid().compareTo(unitUuid) == 0) {
                Log.v("Clay_Time", "Found matching address " + unit.getInternetAddress());
                return true;
            }
        }
        Log.v("Clay_Time", "Didn't find a matching address");
        return false;
    }

    public Unit getUnitByUuid (UUID unitUuid) {
        Log.v("Clay_Time", "Looking for unit (in set of " + getUnits().size() + ") with UUID " + unitUuid + "...");
        for (Unit unit : getUnits ()) {
            Log.v("Clay_Time", "\t...checking address " + unit.getInternetAddress());
            if (unit.getUuid().compareTo(unitUuid) == 0) {
                Log.v("Clay_Time", "Found matching address " + unit.getInternetAddress());
                return unit;
            }
        }
        Log.v("Clay_Time", "Didn't find a matching address");
        return null;
    }

    public boolean hasUnitByAddress (String address) {
        Log.v("Clay_Time", "Looking for unit (in set of " + getUnits().size() + ") with address " + address + "...");
        for (Unit unit : getUnits ()) {
            Log.v("Clay_Time", "\t...checking address " + unit.getInternetAddress());
            if (unit.getInternetAddress().compareTo (address) == 0) {
                Log.v("Clay_Time", "Found matching address " + unit.getInternetAddress());
                return true;
            }
        }
        Log.v("Clay_Time", "Didn't find a matching address");
        return false;
    }

    public void removeUnit (Unit unit) {
        if (hasUnit (unit)) {
            this.units.remove (unit);
        }
    }

    public Behavior getBehavior (String behaviorUuid) {
        return getBehaviorCacheManager().getBehavior(behaviorUuid);
    }

    /**
     * Cycle through routine operations.
     */
    public void cycle () {

        // Process incoming messages
        messageManager.processIncomingMessages();

        // Process outgoing messages
        messageManager.processOutgoingMessages();
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

    // TODO: discoverUnits() : Discover devices via UDP (maybe TCP).
    // TODO: discoverNetwork() : Discover and model the communications network between the units.
    // TODO: requestModuleBehaviors(module) : Request the available behaviors that Clay modules can do. These are the basic behaviors.
    // TODO: requestModuleBehavior(module) : Request the currently programmed behavior of a specific Clay module.

    // TODO: Implement incoming and outgoing message queues for communicating with Clay modules.
}
