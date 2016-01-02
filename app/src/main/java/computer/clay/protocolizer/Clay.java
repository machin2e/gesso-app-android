package computer.clay.protocolizer;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Clay {

    private ArrayList<Unit> units = new ArrayList<Unit> (); // TODO: Move this to the System class!

    private BehaviorRepository behaviorRepository = new BehaviorRepository (this);

    private System system = new System (this); // i.e., like the "model"
    private Perspective perspective = new Perspective (this); // ie., like the "view"
    private Person person = new Person (this); // i.e., like the "controller"

    // Physical systems
    private Database database = null;
    private Communication communication = null;

    // Hacks for debugging!
    // TODO: Remove all of these!
//    public AppActivity Hack_appActivity = null;

    static private Context context;

    private Calendar calendar = Calendar.getInstance (TimeZone.getTimeZone ("GMT"));

    // Settings
    public boolean enablePerspectiveMovingProperty = false;

    Clay () {

        // TODO: throw exception if context is not defined!

        // Start the communications systems
        this.communication = new Communication (this);

        // Start the database system
        this.database = new Database (this);

        // TODO: Discover units!
    }

    // TODO: Move this into Clay, then just reference Clay's platform context.
    public static void setPlatformContext (Context context) {
        Clay.context = context;
    }

    private Calendar getCalendar () {
        return this.calendar;
    }

    public Date getDate () {
        return this.calendar.getTime();
    }

    public long getTime () {
        return this.calendar.getTimeInMillis ();
    }

    public static Context getPlatformContext () {
        return Clay.context;
    }

    public BehaviorRepository getBehaviorRepository () {
        return this.behaviorRepository;
    }

    public Communication getCommunication () {

        if (this.communication == null) {
            this.communication = new Communication(this);
        }

        return this.communication;
    }

    public Database getDatabase() {
        return this.database;
    }

    public System getSystem () {
        return this.system;
    }

    public Perspective getPerspective () {
        return this.perspective;
    }

    public Person getPerson () {
        return this.person;
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
            this.units.add (unit);
        }
    }

    public boolean hasUnits () {
        return this.units.size () > 0;
    }

    public boolean hasUnit (Unit unit) {
        return this.units.contains (unit);
    }

    public boolean hasUnitByAddress (String address) {
        Log.v ("Clay_Time", "Looking for unit (in set of " + getUnits ().size () + ") with address " + address + "...");
        for (Unit unit : getUnits ()) {
            Log.v ("Clay_Time", "\t...checking address " + unit.getInternetAddress ());
            if (unit.getInternetAddress ().compareTo (address) == 0) {
                Log.v ("Clay_Time", "Found matching address " + unit.getInternetAddress ());
                return true;
            }
        }
        Log.v ("Clay_Time", "Didn't find a matching address");
        return false;
    }

    public void removeUnit (Unit unit) {
        if (hasUnit (unit)) {
            this.units.remove (unit);
        }
    }

    public void discoverUnits () {

    }

    // TODO: discoverUnits() : Discover devices via UDP (maybe TCP).
    // TODO: discoverNetwork() : Discover and model the communications network between the units.
    // TODO: requestModuleBehaviors(module) : Request the available behaviors that Clay modules can do. These are the basic behaviors.
    // TODO: requestModuleBehavior(module) : Request the currently programmed behavior of a specific Clay module.

    // TODO: Implement incoming and outgoing message queues for communicating with Clay modules.
}
