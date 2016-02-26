package camp.computer.clay.system;

//import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Event {

    private UUID uuid;

    /** The UUID for the timeline on which this event was scheduled. */
    private UUID timelineUuid;

    /** The timeline on which this event was scheduled. */
    private Timeline timeline;

    /** The behavior represented by this event. This can change. */
    private UUID behaviorUuid;

    private Behavior behavior;

    private BehaviorState behaviorState;

    /** The state of the behavior */
    private UUID behaviorStateUuid;

    public Event(UUID uuid, Timeline timeline, Behavior behavior, BehaviorState behaviorState) {
        this.uuid = uuid;

        this.timeline = timeline;
        this.timelineUuid = timeline.getUuid();

        this.behavior = behavior;
        this.behaviorUuid = behavior.getUuid();

        this.behaviorState = behaviorState;
        this.behaviorStateUuid = behaviorState.getUuid();
    }

    public Event(Timeline timeline, Behavior behavior, BehaviorState behaviorState) {
        this.uuid = UUID.randomUUID();

        this.timeline = timeline;
        this.timelineUuid = timeline.getUuid();

        this.behavior = behavior;
        this.behaviorUuid = behavior.getUuid();

        this.behaviorState = behaviorState;
        this.behaviorStateUuid = behaviorState.getUuid();
    }

    public Clay getClay() {
        return getTimeline().getUnit().getClay();
    }

    public void setBehavior (Behavior behavior, BehaviorState behaviorState) {
        this.behavior = behavior;
        this.behaviorUuid = behavior.getUuid();

        this.behaviorState = behaviorState;
        this.behaviorStateUuid = behaviorState.getUuid();
    }

    public void setTimeline (Timeline timeline) {
        this.timeline = timeline;
        this.timelineUuid = timeline.getUuid();
    }

    public void setBehaviorState (BehaviorState behaviorState) {

        // Update the object model
        this.behaviorState = behaviorState;
        this.behaviorStateUuid = behaviorState.getUuid();

        // Cache the state
        // TODO:

        // Store the updated event and behavior state
        getClay().notifyChange (this);

        /* Notify the the unit of the update */

        // ...then add it to the device...
        String eventUuid = this.getUuid().toString();
        getTimeline().getUnit().send("update behavior " + eventUuid + " \"" + behavior.getTag() + " " + getBehaviorState().getState() + "\"");
        /*
        String behaviorUuid = getBehavior().getUuid().toString();
        getTimeline().getUnit().send("update behavior " + behaviorUuid + " \"" + behavior.getTag() + " " + getBehaviorState().getState() + "\"");
        */

        /* Notify Clay of the update */

        // ...and finally update the repository.
        /*
        getTimeline().getUnit().getClay ().getContentManager().storeBehaviorState(behaviorState);
//        setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
        //getClay ().getContentManager().updateBehaviorState(behaviorState);
        getTimeline().getUnit().getClay ().getContentManager().updateTimeline(getTimeline());
        */
    }

//    @JsonIgnore
//    public void updateBehaviorState (String stateString) {
//
//        BehaviorState behaviorState = new BehaviorState(getBehavior(), stateString);
////        getBehavior().setState(behaviorState);
//
//        this.behaviorState = behaviorState;
//        this.behaviorStateUuid = behaviorState.getUuid();
//    }

    public Behavior getBehavior() { return this.behavior; }

    public UUID getBehaviorUuid () {
        return this.behaviorUuid;
    }

    public BehaviorState getBehaviorState() { return this.behaviorState; }

    public UUID getBehaviorStateUuid () {
        return behaviorStateUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public UUID getTimelineUuid() {
        return timelineUuid;
    }
}
