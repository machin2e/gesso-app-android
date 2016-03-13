package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public class Event {

    private UUID uuid;

    private Timeline timeline;

    private Behavior behavior;

//    private BehaviorState behaviorState;
    private ArrayList<BehaviorState> behaviorState;

    public Event (UUID uuid, Timeline timeline) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.behaviorState = new ArrayList<BehaviorState>();
    }

    public Event (UUID uuid, Timeline timeline, Behavior behavior) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.behavior = behavior;

        this.behaviorState = new ArrayList<BehaviorState>();
        String defaultState = behavior.getScript().getDefaultState();
        this.behaviorState.add(new BehaviorState (defaultState));
    }

    public Event (Timeline timeline, Behavior behavior) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.behavior = behavior;

        this.behaviorState = new ArrayList<BehaviorState>();

        this.initializeState(behavior);
//        if (behavior.getBehaviors().size() == 0) {
//            String defaultState = behavior.getScript().getDefaultState();
//            this.behaviorState.add(new BehaviorState(defaultState));
//        } else {
//            this.initializeState(behavior);
//        }
    }

    private void initializeState(Behavior behavior) {
        if (behavior.hasScript()) {
            String defaultState = behavior.getScript().getDefaultState();
            this.behaviorState.add(new BehaviorState(defaultState));
        } else {
            for (Behavior child : behavior.getBehaviors()) {
                initializeState(child);
            }
        }
    }

    public Event (Timeline timeline) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.behaviorState = new ArrayList<BehaviorState>();
    }

//    public Event (Timeline timeline, Behavior behavior, BehaviorState behaviorState) {
//
//        this.uuid = UUID.randomUUID();
//
//        this.timeline = timeline;
//
//        this.behavior = behavior;
//
//        this.behaviorState = behaviorState;
//    }

    public UUID getUuid() {
        return uuid;
    }

    public Clay getClay() {
        return getTimeline().getUnit().getClay();
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline (Timeline timeline) {
        this.timeline = timeline;
    }

    public Behavior getBehavior() { return this.behavior; }

    public void setBehavior (Behavior behavior) {
        this.behavior = behavior;
    }

    public ArrayList<BehaviorState> getBehaviorState () { return this.behaviorState; }

    public void addBehaviorState (BehaviorState behaviorState) { this.behaviorState.add (behaviorState); };

    //public void setBehaviorState (BehaviorState behaviorState) { this.behaviorState = behaviorState; };
}
