package camp.computer.clay.system;

//import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.UUID;

public class BehaviorState {

    /** The UUID that uniquely identifies this behavior state, making it addressable. */
    private UUID uuid;

    /** The UUID of the behavior for which this is a state. */
    private UUID behaviorUuid;

    // Together, these are the state of the complete behavior
    private String state;

    BehaviorState() {
        // This empty default constructor is necessary for Firebase to be able to deserialize objects.
    }

    public BehaviorState(Behavior behavior, String state) {
        this.uuid = UUID.randomUUID();
        this.behaviorUuid = behavior.getUuid();

        this.state = state;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public UUID getBehaviorUuid () { return this.behaviorUuid; }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
}
