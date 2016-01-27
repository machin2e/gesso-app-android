package computer.clay.protocolizer;

import java.util.UUID;

public class BehaviorConstruct {

    private UUID uuid = null;

    // HACK: Use either Behavior or Loop
    private Behavior behavior = null;
    private Loop loop = null;

    private BehaviorTrigger condition = null;

    private LoopConstruct loopConstruct = null; // The loop associated with this behavior placeholder, if any.

    public enum State {
        FREE, // The action is not on a loop.
        MOVING, // The action is being moved by touch.
        COUPLED, // The action is near enough to a loop to snap onto it.
        SEQUENCED // The action is in a sequence (i.e., on a loop).
    }

    public State state;

    private Perspective perspective = null;

    // Syncrhonization/flow state
    private boolean isSynchronized;

    public BehaviorConstruct (Perspective perspective) {
        super();

        this.uuid = UUID.randomUUID ();

        this.state = State.FREE;

        this.perspective = perspective;

        // Create and associate a behavior with this behavior construct.
        this.behavior = new Behavior(this); // TODO: Remove this! Assign this through the behavior selection interface.
        this.behavior.setTitle (String.valueOf(Behavior.BEHAVIOR_COUNT));
        Behavior.BEHAVIOR_COUNT++;

        // Create the behavior condition associated with this behavior construct.
        this.condition = new BehaviorTrigger(this, BehaviorTrigger.Type.NONE);
    }

    public boolean isSynchronized () {
        return this.isSynchronized;
    }

    public void setSynchronized (boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public boolean hasLoopConstruct () {
        return (this.loopConstruct != null);
    }

    public LoopConstruct getLoopConstruct () {
        return this.loopConstruct;
    }

    /**
     * Adds this behavior construct to the specified loop sequence and updates the state
     * accordingly.
     *
     * @param loopConstruct The loop construct to which the behavior construct will be added.
     */
    public void setLoopConstruct (LoopConstruct loopConstruct) {

        // Associate the specified loop construct with this behavior construct...
        this.loopConstruct = loopConstruct;

//        // ...then add this behavior construct to the loop...
//        this.loopConstruct.addBehaviorConstruct (this);
//
//        // ...and update state of this behavior construct.
//        this.state = State.SEQUENCED;

    }

    /**
     * Removes this behavior placeholder from the loop it's associated with, if any, and update
     * the state accordingly.
     */
//    public void removeLoopConstruct () {
//        this.loopConstruct = null;
//        Log.v ("Clay_Loop_Construct", "removeLoopConstruct");
//
////        if (this.hasLoopConstruct ()) {
////
////            LoopConstruct previousLoopConstruct = this.loopConstruct;
////
////            this.loopConstruct = null;
////
////            // Update state of the this placeholder
////            this.state = State.FREE;
////
//////            LoopConstruct nearestLoopConstruct = this.perspective.getNearestLoopConstruct (this);
//////            nearestLoopConstruct.reorderBehaviors();
//////            this.getLoopConstruct ().reorderBehaviors ();
////
////            // Remove this placeholder from the loop.
////            previousLoopConstruct.removeBehaviorConstruct (this);
////
////        }
//    }

    public void setBehavior (Behavior behavior) {
        this.behavior = behavior;
        this.loop = null;
    }

    public void setLoop (Loop loop) {
        this.loop = loop;
        this.behavior = null;
    }

    public Behavior getBehavior () {
        return this.behavior;
    }

    public Loop getLoop () {
        return this.loop;
    }

    public boolean hasBehavior () {
        return (this.behavior != null);
    }

    public boolean hasLoop () {
        return (this.loop != null);
    }

    public void setCondition (BehaviorTrigger condition) {
        this.condition = condition;
    }

    public BehaviorTrigger getCondition () {
        return this.condition;
    }

    public boolean hasCondition () {
        return (this.condition != null);
    }
}
