package camp.computer.clay.system;

import java.util.UUID;

public class BehaviorState {

    /** The UUID that uniquely identifies this behavior state, making it addressable. */
    private UUID uuid;

    /** The UUID of the behavior for which this is a state. */
    private UUID behaviorUuid;

    // Together, these are the state of the complete behavior
    private String tag;
    private String description;
    private String state;

    BehaviorState() {
        // This empty default constructor is necessary for Firebase to be able to deserialize objects.
    }

    public BehaviorState(Behavior behavior, String tag, String state) {
        this.uuid = UUID.randomUUID();

        this.tag = tag;
        this.state = state;
        this.description = "";
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getDescription () {
        return this.description;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
}
