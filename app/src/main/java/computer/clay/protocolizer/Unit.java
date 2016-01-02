package computer.clay.protocolizer;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mrgubbels on 11/12/15.
 */
public class Unit {

    private Clay clay = null;

    private UUID uuid = null; // The unit's static, unchanging, UUID

    private String internetAddress = null; // The unit's IP address

    private String meshAddress = null; // The unit's IP address

    private Loop loop = null; // The unit's IP address

    // TODO: Cache/model the unit's state and behavior

    Unit (Clay clay, UUID uuid) {
        this.clay = clay;

        this.uuid = uuid;

        this.loop = new Loop (this);
    }

    public Clay getClay () {
        return this.clay;
    }

    private Date timeOfLastContact = null;

    public long getTimeSinceLastMessage () {
//        Log.v ("Clay_Time", "Time since last message: " + this.timeOfLastContact);
        Date currentTime = Calendar.getInstance ().getTime ();

        if (timeOfLastContact != null) {
            long timeDifferenceInMilliseconds = currentTime.getTime () - timeOfLastContact.getTime ();
            // long seconds = timeDifferenceInMilliseconds / 1000;
            // long minutes = seconds / 60;
            // long hours = minutes / 60;
            // long days = hours / 24;
            return timeDifferenceInMilliseconds;
        } else {
            return Long.MAX_VALUE;
        }
    }

    public void setTimeOfLastContact (Date time) {
        this.timeOfLastContact = time;
        Log.v ("Clay_Time", "Changing time from " + this.timeOfLastContact.getTime () + " to " + time.getTime ());
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setInternetAddress (String address) {
        this.internetAddress = address;
    }

    public String getInternetAddress () {
        return this.internetAddress;
    }

    public void setMeshAddress (String address) {
        this.meshAddress = address;
    }

    public String getMeshAddress () {
        return this.meshAddress;
    }

    // TODO: Simulate the unit's state change based on its behavior

    public Loop getLoop () {
        return this.loop;
    }
}
