package camp.computer.clay.system;

import java.util.ArrayList;

public class Timeline {

    private ArrayList<Behavior> behaviors = new ArrayList<Behavior>();

    private Unit unit = null;

    public Timeline(Unit unit) {
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

    /**
     * Remove the specified behavior from the loop (if it is present)
     */
    public void removeBehavior (Behavior behavior) {
        if (behavior != null) {
            if (this.behaviors.contains (behavior)) {
                this.behaviors.remove(behavior);
            }
        }
    }

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
