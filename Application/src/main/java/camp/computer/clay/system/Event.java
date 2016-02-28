package camp.computer.clay.system;

import java.util.UUID;

public class Event {

    private UUID uuid;

    /** The timeline on which this event was scheduled. */
    private Timeline timeline;

    private Behavior behavior;

    public Event (UUID uuid, Timeline timeline, Behavior behavior) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.behavior = behavior;
    }

    public Event (Timeline timeline, Behavior behavior) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.behavior = behavior;
    }

    public Clay getClay() {
        return getTimeline().getUnit().getClay();
    }

    public void setBehavior (Behavior behavior) {
        this.behavior = behavior;
    }

    public void setTimeline (Timeline timeline) {
        this.timeline = timeline;
    }

//    public void setBehaviorState (BehaviorState behaviorState) {
//
//        // Update the object model
//        this.behaviorState = behaviorState;
//        this.behaviorStateUuid = behaviorState.getUuid();
//
//        // Cache the state
//        // TODO:
//
//        // Store the updated event and behavior state
//        getClay().notifyChange (this);
//
//        /* Notify the the unit of the update */
//
//        // ...then add it to the device...
//        String eventUuid = this.getUuid().toString();
//        getTimeline().getUnit().send("update behavior " + eventUuid + " \"" + behavior.getTag() + " " + getBehaviorState().getState() + "\"");
//        /*
//        String behaviorUuid = getBehavior().getUuid().toString();
//        getTimeline().getUnit().send("update behavior " + behaviorUuid + " \"" + behavior.getTag() + " " + getBehaviorState().getState() + "\"");
//        */
//
//        /* Notify Clay of the update */
//
//        // ...and finally update the repository.
//        /*
//        getTimeline().getUnit().getClay ().getContentManager().storeBehaviorState(behaviorState);
////        setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//        //getClay ().getContentManager().updateBehaviorState(behaviorState);
//        getTimeline().getUnit().getClay ().getContentManager().updateTimeline(getTimeline());
//        */
//    }

    public Behavior getBehavior() { return this.behavior; }

    public UUID getUuid() {
        return uuid;
    }

    public Timeline getTimeline() {
        return timeline;
    }
}
