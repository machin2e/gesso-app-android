package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public class Behavior {

    private UUID uuid;

    private String tag;

    private String description;

    /**
     * The script represented by this behavior (if any). Only basic behaviors are associated with
     * a behavior script.
     */
    private BehaviorScript behaviorScript;

    /**
     * The state to apply to the behavior script (if any).
     */
    private BehaviorState behaviorState;

    /**
     * The list of behaviors that constitute this behavior, if any. This list is empty for basic
     * behaviors. Behaviors for which this list is not empty are compositions of basic behaviors
     * and other behavior compositions (ultimately, of basic behaviors).
     */
    ArrayList<Behavior> behaviors;


    public Behavior(UUID uuid, String tag, BehaviorScript behaviorScript, BehaviorState behaviorState) {

        this.uuid = uuid;

        this.tag = tag;

        this.description = "";

        this.behaviorScript = behaviorScript;

        this.behaviorState = behaviorState;

        this.behaviors = null;
    }

    public Behavior(BehaviorScript behaviorScript, BehaviorState behaviorState) {

        this.uuid = UUID.randomUUID();

        this.tag = behaviorScript.getTag();

        this.description = "";

        this.behaviorScript = behaviorScript;

        this.behaviorState = behaviorState;

        this.behaviors = null;
    }

    /**
     * Constructor for a basic behavior initialized with the default tag and state of the
     * behavior script.
     * @param behaviorScript A script associated with a basic behavior.
     */
    public Behavior(UUID uuid, BehaviorScript behaviorScript) {

        this.uuid = uuid;

        this.tag = behaviorScript.getTag();

        this.description = "";

        this.behaviorScript = behaviorScript;

        this.behaviorState = new BehaviorState(behaviorScript.getDefaultState());

        this.behaviors = null;
    }

    /**
     * Convenience constructor that generates a random UUID for the behavior.
     * @param behaviorScript A script associated with a basic behavior.
     */
    public Behavior (BehaviorScript behaviorScript) {

        this(UUID.randomUUID(), behaviorScript);
    }

    /**
     * Constructor for behavior composition (non-leaf in the behavior tree graph).
     * @param uuid UUID to associate with the behavior.
     * @param tag Tag to associate with the behavior.
     */
    public Behavior (UUID uuid, String tag) {

        this.uuid = uuid;

        this.tag = tag;

        this.description = "";

        this.behaviorScript = null;

        this.behaviorState = null;

        this.behaviors = new ArrayList<Behavior>();
    }

    /**
     * Convenience that generates a random UUID for a behavior.
     * @param tag Tag to associate with the behavior.
     */
    public Behavior (String tag) {
        this(UUID.randomUUID(), tag);
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

    public BehaviorScript getScript () {
        return this.behaviorScript;
    }

    public void setScript (BehaviorScript behaviorScript) {
        this.behaviorScript = behaviorScript;
    }

//    public void setScript (BehaviorScript behaviorScript, BehaviorState behaviorState) {
//        this.behaviorScript = behaviorScript;
//        this.behaviorState = behaviorState;
//    }

    public BehaviorState getState () {
        return this.behaviorState;
    }

    public void setState (BehaviorState behaviorState) {
        this.behaviorState = behaviorState;
    }

    public ArrayList<Behavior> getBehaviors() {
        return this.behaviors;
    }

    public void addBehavior (Behavior behavior) {

        // Add behaviors. This changes the behavior so it is no longer a basic behavior.
        this.behaviors.add(behavior);

        // Remove the script and state since this behavior is no longer a basic behavior.
        this.behaviorScript = null;
        this.behaviorState = null;
    }

    public void addBehaviors (ArrayList<Behavior> behaviors) {

        // Add behaviors. This changes the behavior so it is no longer a basic behavior.
        this.behaviors.addAll(behaviors);

        // Remove the script and state since this behavior is no longer a basic behavior.
        this.behaviorScript = null;
        this.behaviorState = null;
    }

    /**
     * Returns true if the behavior is associated with a behavior script. This will only return
     * true for basic behaviors. This will return false for behavior compositions.
     * @return True if the behavior has a script. Only true for basic behaviors.
     */
    public boolean hasScript() {
        return (this.behaviorScript != null);
    }
}
