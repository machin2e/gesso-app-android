package camp.computer.clay.system;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

// How to exclude certain fields:
// http://stackoverflow.com/questions/32108969/why-do-i-get-failed-to-bounce-to-type-when-i-turn-json-from-firebase-into-java

public class Behavior {

    private UUID uuid;

    // Behavior Profile
    private UUID stateUuid;

    // Behavior State
    private BehaviorState state;

    public Behavior() {
        // This empty default constructor is necessary for Firebase to be able to deserialize objects.
    }

    Behavior(String tag, String defaultState) {
        this.uuid = UUID.randomUUID();

        // Create the default state of this behavior
        BehaviorState behaviorState = new BehaviorState (this, tag, defaultState);
        this.state = behaviorState;
        this.stateUuid = behaviorState.getUuid();
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public UUID getStateUuid() { return this.stateUuid; }

    public void setStateUuid (UUID stateUuid) {
        this.stateUuid = stateUuid;
    }

    @JsonIgnore
    public void setTag(String tag) {
        BehaviorState behaviorState = new BehaviorState (this, tag, getState().getState());
        behaviorState.setDescription(getDescription());
        this.state = behaviorState;
    }

    @JsonIgnore
    public String getTag() {
        return this.state.getTag();
    }

    @JsonIgnore
    public void setDescription (String description) {
        BehaviorState behaviorState = new BehaviorState (this, getTag(), getState().getState());
        behaviorState.setDescription(description);
        this.state = behaviorState;
    }

    @JsonIgnore
    public String getDescription () {
        return this.state.getDescription();
    }

//    @JsonIgnore
//    public void setState(String state) {
//        BehaviorState behaviorState = new BehaviorState (this, getTag(), state);
//        behaviorState.setDescription(getDescription());
//        this.state = behaviorState;
//    }

    @JsonIgnore
    public void setState (BehaviorState state) {
        this.state = state;
    }

    @JsonIgnore
    public BehaviorState getState() {
        return this.state;
    }

//    /**
//     * Sets the current state of the behavior to the existing state with the specified UUID.
//     * @param uuid The UUID of the behavior state to set as the current one.
//     */
//    public void setState(UUID uuid) {
//        // TODO: Look up the behavior state with the specified UUID in the behavior's state cache.
//    }

    /*
    public void perform () {
        // TODO: Perform the action, whatever it is!

        Log.v("Clay", "Performing behavior " + this.getTag() + ".");
    }
    */
}
