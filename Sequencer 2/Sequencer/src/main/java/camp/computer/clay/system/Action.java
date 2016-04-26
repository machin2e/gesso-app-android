package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public class Action {

    private UUID uuid;

    private String tag;

    private String description;

    /**
     * The script represented by this behavior (if any). Only basic actions are associated with
     * a behavior script.
     */
    private Script script;

    /**
     * The state to apply to the behavior script (if any).
     */
//    private State behaviorState;

    /**
     * The list of actions that constitute this behavior, if any. This list is erase for basic
     * actions. Behaviors for which this list is not erase are compositions of basic actions
     * and other behavior compositions (ultimately, of basic actions).
     */
    ArrayList<Action> actions;


    public Action(UUID uuid, String tag, Script script, State state) {

        this.uuid = uuid;

        this.tag = tag;

        this.description = "";

        this.script = script;

//        this.state = state;

        this.actions = null;
    }

//    public Action(Script script, State behaviorState) {
//
//        this.uuid = UUID.randomUUID();
//
//        this.tag = script.getTag();
//
//        this.description = "";
//
//        this.script = script;
//
////        this.behaviorState = behaviorState;
//
//        this.actions = null;
//    }

    /**
     * Constructor for a basic behavior initialized with the default tag and state of the
     * behavior script.
     * @param script A script associated with a basic behavior.
     */
    public Action(UUID uuid, Script script) {

        this.uuid = uuid;

        this.tag = script.getTag();

        this.description = "";

        this.script = script;

//        this.behaviorState = new State(script.getDefaultState());

        this.actions = null;
    }

    /**
     * Convenience constructor that generates a random UUID for the behavior.
     * @param script A script associated with a basic behavior.
     */
    public Action(Script script) {

        this(UUID.randomUUID(), script);
    }

    /**
     * Constructor for behavior composition (non-leaf in the behavior tree graph).
     * @param uuid UUID to associate with the behavior.
     * @param tag Tag to associate with the behavior.
     */
    public Action(UUID uuid, String tag) {

        this.uuid = uuid;

        this.tag = tag;

        this.description = "";

        this.script = null;

//        this.behaviorState = null;

        this.actions = new ArrayList<Action>();
    }

    /**
     * Convenience that generates a random UUID for a behavior.
     * @param tag Tag to associate with the behavior.
     */
    public Action(String tag) {
        this(UUID.randomUUID(), tag);
    }

    public void setUuid (UUID uuid) {
        this.uuid = uuid;
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

    public Script getScript () {
        return this.script;
    }

    public void setScript (Script script) {
        this.script = script;
    }

//    public void setScript (Script script, State behaviorState) {
//        this.script = script;
//        this.behaviorState = behaviorState;
//    }

//    public State getState () {
//        return this.behaviorState;
//    }
//
//    public void setState (State behaviorState) {
//        this.behaviorState = behaviorState;
//    }

    public ArrayList<Action> getActions() {
        return this.actions;
    }

    public void addAction(Action action) {

        // Add actions. This changes the action so it is no longer a basic action.
        this.actions.add(action);

        // Remove the script and state since this action is no longer a basic action.
        this.script = null;
//        this.behaviorState = null;
    }

    public void addBehaviors (ArrayList<Action> actions) {

        // Add actions. This changes the behavior so it is no longer a basic behavior.
        this.actions.addAll(actions);

        // Remove the script and state since this behavior is no longer a basic behavior.
        this.script = null;
//        this.behaviorState = null;
    }

    /**
     * Returns true if the behavior is associated with a behavior script. This will only return
     * true for basic actions. This will return false for behavior compositions.
     * @return True if the behavior has a script. Only true for basic actions.
     */
    public boolean hasScript() {
        return (this.script != null);
    }

    public int getLeafCount () {

        int count = 0;

        if (hasScript()) {
            count = 1;
        }

        return count + getLeafCount(this);
    }

    private int getLeafCount (Action action) {

        int count = 0;

        for (Action childAction : action.getActions()) {

            if (childAction.hasScript()) {
                count++;
            } else {
                count += getLeafCount(childAction);
            }
        }

        return count;
    }
}
