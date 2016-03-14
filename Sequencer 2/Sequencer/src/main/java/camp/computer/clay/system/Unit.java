package camp.computer.clay.system;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

//import com.fasterxml.jackson.annotation.JsonIgnore;

public class Unit {

    private Clay clay = null;

    private UUID uuid = null; // The unit's static, unchanging, UUID

    private String internetAddress = null; // The unit's IP address

    private String meshAddress = null; // The unit's IP address

    private UUID timelineUuid;
    // TODO: Replace timeline with timelineUuid so it can be easily moved between multiple devices or simulators.
    private Timeline timeline = null;

    private Date timeOfLastContact = null;

    // TODO: Cache/model the unit's state and behavior

    public Unit() {
        // This empty default constructor is necessary for Firebase to be able to deserialize objects.
    }

    Unit(Clay clay, UUID uuid) {
        this.clay = clay;

        this.uuid = uuid;

        this.timeline = new Timeline(this);
        this.timelineUuid = this.timeline.getUuid();
    }

    public Clay getClay () {
        return this.clay;
    }

    public UUID getTimelineUuid () {
        return timelineUuid;
    }

    public void setTimelineUuid (UUID uuid) {
        this.timelineUuid = uuid;
        this.timeline = null;
    }

    public long getTimeSinceLastMessage () {
//        Log.v ("Clay_Time", "Time since last message: " + this.timeOfLastContact);
        Date currentTime = Calendar.getInstance().getTime ();

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
        Log.v("Clay_Time", "Changing time from " + this.timeOfLastContact.getTime() + " to " + time.getTime());
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

    public Timeline getTimeline() {
        return this.timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
        this.timelineUuid = timeline.getUuid();

        this.timeline.setUnit(this);
    }

//    public void notifyChange (Event event) {
//        getClay().notifyChange (event);
//
//        this.send("create behavior " + event.getUuid() + " \"" + event.getBehavior().getTag() + " " + event.getBehavior().getState().getState() + "\"");
//        this.send("addUnit behavior " + event.getUuid());
//    }

    /*
    // TODO:
    public void notifyRemove (Event event) {
        getClay().notifyChange (event);

        // Remove the previous behavior from the timeline
        this.send("remove behavior " + event.getBehavior().getUuid());
    }
    */

    public void send(String content) {
        getClay().sendMessage(this, content);
    }

//    public void cacheBehavior(UUID behaviorUuid) {
//
//        // Get the behavior with the specified UUID
//        Behavior behavior = getClay().getBehavior(behaviorUuid);
//
//        // Generate the behavior's initial state
//        BehaviorState behaviorState = new BehaviorState(behavior, behavior.getDefaultState());
//
//        // Create an event for the behavior so it can be added to the timeline
//        Event event = new Event(timeline, behavior, behaviorState);
//
//        // Add the event to the timeline
//        this.getTimeline().addEvent(event);
//    }

//    public void cacheBehavior(Behavior behavior) {
//
//        // Create an event for the behavior so it can be added to the timeline
//        Event event = new Event(timeline, behavior);
//
//        // Add the event to the timeline
//        this.getTimeline().addEvent(event);
//    }
}
