package camp.computer.clay.system;

import java.util.UUID;

public class BehaviorState {

    /** The UUID that uniquely identifies this behavior state, making it addressable. */
    private UUID uuid;

    /** The behavior for which this is a state. */
    // TODO: Remove this from the class so it is patterned like BehaviorScript
    private Behavior behavior;

    // Together, these are the state of the complete behavior
    private String state;

    private String description;

    public BehaviorState (UUID uuid, Behavior behavior, String state) {

        this.uuid = uuid;

        this.behavior = behavior;

        this.state = state;

        this.description = "";
    }

    public BehaviorState (Behavior behavior, String state) {

        this (UUID.randomUUID(), behavior, state);
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public Behavior getBehavior () { return this.behavior; }

    public String getState() {
        return this.state;
    }
}
