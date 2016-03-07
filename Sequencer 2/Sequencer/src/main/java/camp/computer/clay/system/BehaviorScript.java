package camp.computer.clay.system;

import java.util.UUID;

public class BehaviorScript {

    private UUID uuid;

    private String tag;

    /**
     * The behavior's default state string. This is used to generate the initial state for the
     * behavior. It is also used to recover from emergency situations in which no state is
     * available for the behavior (e.g., Internet connection lost).
     */
    private String defaultState;

    public BehaviorScript(UUID uuid, String tag, String defaultState) {

        this.uuid = uuid;

        this.tag = tag;

        this.defaultState = defaultState;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public String getTag () {
        return this.tag;
    }

    public String getDefaultState () {
        return this.defaultState;
    }

}
