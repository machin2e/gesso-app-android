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

    // List of touchscreen viewing devices
    private ArrayList<ViewManagerInterface> views;

    public void addView (ViewManagerInterface view) {
        this.views.add (view);
    }

    public ViewManagerInterface getView (int i) {
        return this.views.get (i);
    }

    private ArrayList<Unit> units = new ArrayList<Unit>();

    private BehaviorCacheManager behaviorRepository = null;

    // Physical systems
    private ContentManager database = null;
    private MessageManager messageManager = null;
    private NetworkManager networkManager = null;

    static private Context context;

    private Calendar calendar = Calendar.getInstance (TimeZone.getTimeZone("GMT"));

    public Clay() {

        // Create list to store views.
        this.views = new ArrayList<ViewManagerInterface>();

        // TODO: throw exception if context is not defined!

        // Start the communications systems
        this.messageManager = new MessageManager (this);

        // Start the networking systems
        this.networkManager = new NetworkManager(this);

        // Start the database system
        this.database = new ContentManager(this);

        // Set up behavior repository
        this.behaviorRepository = new BehaviorCacheManager(this);

        // TODO: Discover units!
    }

    // TODO: Move this into Clay, then just reference Clay's platform context.
    public static void setContext (Context context) {
        Clay.context = context;
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

    public static Context getContext() {
        return Clay.context;
    }

    public BehaviorCacheManager getBehaviorRepository () {
        return this.behaviorRepository;
    }

//    public Communication getCommunication () {
//
//        if (this.communication == null) {
//            this.communication = new Communication(this);
//        }
//
//        return this.communication;
//    }

    public ContentManager getDatabase() {
        return this.database;
    }

//    public System getSystem () {
//        return this.system;
//    }

//    public Perspective getPerspective () {
//        return this.perspective;
//    }

//    public Person getPerson () {
//        return this.person;
//    }

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

            // <TEST>
            // Add a random number of random behaviors to the unit.
            // This will be replaced with the actual behaviors on the device's timeline.
            Random r = new Random ();
            int behaviorCount = r.nextInt(20);
            for (int i = 0; i < behaviorCount; i++) {

                // Get list of the available behavior types
                ArrayList<String> behaviorTypes = new ArrayList<String>();
                behaviorTypes.add ("lights");
                behaviorTypes.add ("io");
                behaviorTypes.add ("message");
                behaviorTypes.add ("wait");
                behaviorTypes.add ("say");

                // Select random behavior type
                int behaviorSelection = r.nextInt(behaviorTypes.size());

                // Generate a behavior of the selected type
                unit.getTimeline().addBehavior(new Behavior(behaviorTypes.get(behaviorSelection)));
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

            // Retrieve unit from memory (i.e., database).
            //getDatabase().addUnit(unit);
            //getDatabase ().getUnit (unit.getUuid ());
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

    public void discoverUnits () {

    }

    public void addMessageManager(MessageManagerInterface messageManager) {
        this.messageManager.addManager(messageManager);
    }

    public void addNetworkResource(NetworkResourceInterface networkResource) {
        this.networkManager.addResource(networkResource);
    }

    public void sendMessage (Unit unit, String content) {

        // Prepare message
        String source = this.networkManager.getInternetAddress ();
        String destination = DatagramManager.BROADCAST_ADDRESS; // unit.getInternetAddress();
        Message message = new Message("udp", source, destination, content);

        // Queue message
        messageManager.queueOutgoingMessage(message);

//        // <HACK>
//        // Process the outgoing messages
//        messageManager.processOutgoingMessages();
//        // </HACK>
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

    // TODO: discoverUnits() : Discover devices via UDP (maybe TCP).
    // TODO: discoverNetwork() : Discover and model the communications network between the units.
    // TODO: requestModuleBehaviors(module) : Request the available behaviors that Clay modules can do. These are the basic behaviors.
    // TODO: requestModuleBehavior(module) : Request the currently programmed behavior of a specific Clay module.

    // TODO: Implement incoming and outgoing message queues for communicating with Clay modules.
}
