package camp.computer.clay.system;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Event {

    /** The timeline on which this event was scheduled. */
    private Timeline timeline;

    private UUID uuid;

    private Behavior behavior;

    /** The behavior represented by this event. This can change. */
    private UUID behaviorUuid;

    private BehaviorState behaviorState;

    /** The state of the behavior */
    private UUID behaviorStateUuid;

    Event() {

    }

    public Event(Behavior behavior, BehaviorState behaviorState) {
        this.behavior = behavior;
        this.behaviorUuid = behavior.getUuid();

        this.behaviorState = behaviorState;
        this.behaviorStateUuid = behaviorState.getUuid();
    }

    @JsonIgnore
    public void setBehavior (Behavior behavior, BehaviorState behaviorState) {
        this.behavior = behavior;
        this.behaviorUuid = behavior.getUuid();

        this.behaviorState = behaviorState;
        this.behaviorStateUuid = behaviorState.getUuid();
    }

    @JsonIgnore
    public Behavior getBehavior() { return this.behavior; }

    public UUID getBehaviorUuid () {
        return this.behaviorUuid;
    }

    @JsonIgnore
    public BehaviorState getBehaviorState() { return this.behaviorState; }

    public UUID getBehaviorStateUuid () {
        return this.behaviorStateUuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
