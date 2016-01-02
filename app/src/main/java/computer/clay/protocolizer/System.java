package computer.clay.protocolizer;

import java.util.ArrayList;

public class System {

    private Clay clay = null;

    private ArrayList<Loop> loops = new ArrayList<Loop>();
//    ArrayList<BehaviorConstruct> behaviors = new ArrayList<BehaviorConstruct>();

    // TODO: Include Operator/People/Agents/Actors/Robots/Intelligence

    public System (Clay clay) {
        super();

        this.clay = clay;
    }

    public Clay getClay () {
        return this.clay;
    }

    // TODO: Replace with createLoop and return the Loop
    public void addLoop (Loop loop) {
        this.loops.add(loop);
    }

    public ArrayList<Loop> getLoops () {
        return this.loops;
    }

    // TODO: Replace with createBehavior and return the Behavior
//    public void addBehavior (BehaviorConstruct behaviorConstruct) {
//        this.behaviors.add (behaviorConstruct);
//    }

//    public ArrayList<BehaviorConstruct> getBehaviors() {
//        return this.behaviors;
//    }

    // TODO: attachBehaviorToLoop

    // TODO: detachBehaviorToLoop

    // TODO: deleteLoop

    // TODO: deleteBehavior
}
