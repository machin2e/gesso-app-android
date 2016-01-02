package computer.clay.protocolizer;

import java.util.ArrayList;

/**
 * Loop
 */
public class Loop { // TODO: Possibly rename to LoopOperation

    private ArrayList<Behavior> behaviors = new ArrayList<Behavior> ();

    private Unit unit = null;

    public Loop (Unit unit) {
        super();

        this.unit = unit;
    }

    public Unit getUnit () {
        return this.unit;
    }

    public void addBehavior (Behavior behavior) {

        // Add behavior to the list of behaviors in the loop sequence
        if (!this.behaviors.contains(behavior)) {
            this.behaviors.add(behavior);
        }

        // Re-order the behaviors based on their position along the loop
//        this.reorderBehaviors();

    }

    public boolean hasBehaviors () {
        return (this.behaviors.size () > 0);
    }

    public boolean hasBehavior (Behavior behavior) {
        return this.behaviors.contains (behavior);
    }

    // TODO: Remove behavior from a sequence, not from by the specified angle. Do that in LoopConstruct.
    public void removeBehavior (Behavior behavior) {
        if (behavior != null) {

            // Remove the specified behavior from the loop (if it is present)
            if (this.behaviors.contains (behavior)) {
                this.behaviors.remove(behavior);
            }

            // Re-order the behaviors based on their position along the loop
//            this.reorderBehaviors();
        }
    }

    // TODO: Intelligently compute adjustments to behavior position on loop and update the position, showing it clearly as automatically being updated by Clay.

    public ArrayList<Behavior> getBehaviors () {
        return this.behaviors;
    }

    public Behavior getBehavior (int index) {
        if (0 < index && index < this.behaviors.size()) {
            return this.behaviors.get(index);
        } else {
            return null;
        }
    }
}
