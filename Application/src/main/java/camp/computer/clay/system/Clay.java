package camp.computer.clay.system;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private BehaviorManager behaviorRepository = null;

    private System system = new System (this);

    // Physical systems
    private ContentManager database = null;
    private MessageManager messageManager = null;

    static private Context context;

    private Calendar calendar = Calendar.getInstance (TimeZone.getTimeZone("GMT"));

    public Clay() {

        // Create list to store views.
        this.views = new ArrayList<ViewManagerInterface>();

        // TODO: throw exception if context is not defined!

        // Start the communications systems
        this.messageManager = new MessageManager (this);

        // Start the database system
        this.database = new ContentManager(this);

        // Set up behavior repository
        this.behaviorRepository = new BehaviorManager(this);

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

    public BehaviorManager getBehaviorRepository () {
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

    public System getSystem () {
        return this.system;
    }

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

            // Add timelines to attached views
            for (ViewManagerInterface view : this.views) {
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
        this.messageManager.addMessageManager(messageManager);
    }

    // TODO: discoverUnits() : Discover devices via UDP (maybe TCP).
    // TODO: discoverNetwork() : Discover and model the communications network between the units.
    // TODO: requestModuleBehaviors(module) : Request the available behaviors that Clay modules can do. These are the basic behaviors.
    // TODO: requestModuleBehavior(module) : Request the currently programmed behavior of a specific Clay module.

    // TODO: Implement incoming and outgoing message queues for communicating with Clay modules.
}
