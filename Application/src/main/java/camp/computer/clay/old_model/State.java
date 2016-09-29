package camp.computer.clay.old_model;

import java.util.UUID;

public class State {

    /** The UUID that uniquely identifies this behavior state, making it addressable. */
    private UUID uuid;

    // Together, these are the state of the complete behavior
    private String state;

    private String description;

    public State(UUID uuid, String state) {

        this.uuid = uuid;

        this.state = state;

        this.description = "";
    }

    public State(String state) {

        this (UUID.randomUUID(), state);
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public String getState() {
        return this.state;
    }
}
