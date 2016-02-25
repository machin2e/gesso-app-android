package camp.computer.clay.system;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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

    @JsonIgnore
    public Clay getClay () {
        return this.clay;
    }

    public UUID getTimelineUuid () {
        return timelineUuid;
    }

    public void setTimelineUuid (UUID uuid) {
        this.timelineUuid = uuid;
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getInternetAddress () {
        return this.internetAddress;
    }

    public void setMeshAddress (String address) {
        this.meshAddress = address;
    }

    @JsonIgnore
    public String getMeshAddress () {
        return this.meshAddress;
    }

    @JsonIgnore
    public Timeline getTimeline() {
        return this.timeline;
    }

    @JsonIgnore
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;

        this.timeline.setUnit(this);
    }

    public void send(String content) {
        getClay().sendMessage(this, content);
    }

//    public void addBehavior(UUID behaviorUuid) {
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

    public void addBehavior(Behavior behavior, BehaviorState behaviorState) {

        // Create an event for the behavior so it can be added to the timeline
        Event event = new Event(timeline, behavior, behaviorState);

        // Add the event to the timeline
        this.getTimeline().addEvent(event);
    }
}
