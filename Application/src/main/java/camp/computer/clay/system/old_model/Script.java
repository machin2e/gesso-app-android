package camp.computer.clay.system.old_model;

import java.util.UUID;
import java.util.regex.Pattern;

public class Script {

    private UUID uuid;

    private String tag;

    // Regular expression pattern that defines the script's state space
    private String stateSpacePatternString; // e.g., for 12 lights with binary state "((T|F) ){11}(T|F)";

    // Compile the pattern string for use
    private Pattern stateSpacePattern;

    /**
     * The behavior's default state string. This is used to generate the initial state for the
     * behavior. It is also used to recover from emergency situations in which no state is
     * available for the behavior (e.g., Internet connection lost).
     */
    private String defaultState;

    public Script(UUID uuid, String tag, String stateSpacePattern, String defaultState) {
    //public Script(UUID uuid, String tag, String defaultState) {

        this.uuid = uuid;

        this.stateSpacePatternString = stateSpacePattern;
        this.stateSpacePattern = Pattern.compile (stateSpacePatternString);

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

    public String getStatePattern () {
        return stateSpacePatternString;
    }
}
