package computer.clay.protocolizer;

import android.graphics.Point;
import android.util.Log;

import java.util.UUID;

public class BehaviorConstruct {

    public static int DEFAULT_RADIUS = 80;

    private UUID uuid = null;

    private Behavior behavior = null;

    private BehaviorTrigger condition = null;

    private Point position = new Point ();
    private int radius;

    private LoopConstruct loopConstruct = null; // The loop associated with this behavior placeholder, if any.

    public enum State {
        FREE, // The action is not on a loop.
        MOVING, // The action is being moved by touch.
        COUPLED, // The action is near enough to a loop to snap onto it.
        SEQUENCED // The action is in a sequence (i.e., on a loop).
    }

    // Touch state
    // TODO: isTouched
    // TODO: touchStartTime
    // TODO: touchStopTime
    // TODO: startPoint
    // TODO: currentPoint
    // TODO: stopPoint

    public State state;

    private Perspective perspective = null;

    // TODO: Title
    // TODO: Graphical representation and layout
    // TODO: Associate with a particular Clay by address
    // TODO: Associate with command (action's behavior tree/graph structure)
    // TODO: Associate with cloud object

    // Syncrhonization/flow state
    private boolean isSynchronized;

    public BehaviorConstruct (Perspective perspective, int xPosition, int yPosition) {
        super();

        this.uuid = UUID.randomUUID ();

        this.state = State.FREE;

        this.perspective = perspective;

        position.set (xPosition, yPosition);

        radius = DEFAULT_RADIUS;

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
    }

    public Behavior getBehavior () {
        return this.behavior;
    }

    public boolean hasBehavior () {
        return (this.behavior != null);
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

    public void setPosition (int x, int y) {
        position.set (x, y);

        // TODO: Update the state based on the position (i.e., settleState).
    }

    /**
     * "Settling" the position means computing the position based on the state of the action.
     */
    public Point settlePosition () {
        Log.v("Clay", "settlePosition");

        Point resolvedPoint = new Point (this.getPosition ());

        /* Check if the action is entangled. */

        // Search for the nearest loop and snap to that one (ongoing).
        LoopConstruct nearestLoopConstruct = this.perspective.getNearestLoopConstruct (this);
        double behaviorConstructAngle = nearestLoopConstruct.getAngle (this.getPosition ());

        // Get the perspective at the behavior's angle
        LoopPerspective nearestLoopConstructPerspective = nearestLoopConstruct.getPerspective (behaviorConstructAngle);
        if (nearestLoopConstructPerspective != null) {
            double nearestLoopConstructPerspectiveDistance = this.getDistanceToLoopPerspective(nearestLoopConstructPerspective);

            // Snap to the loop if within snapping range
            if (nearestLoopConstruct != null) {
                if (nearestLoopConstructPerspectiveDistance < nearestLoopConstructPerspective.getSnapDistance()) { // TODO: Replace magic number with a static class variable.

                    Point nearestPoint = this.getNearestPoint(nearestLoopConstructPerspective);
                    this.setPosition(nearestPoint.x, nearestPoint.y);

                    nearestLoopConstruct.addBehaviorConstruct(this);

                } else { // The behavior was positioned outside the snapping boundary of the loop.

                    if (this.hasLoopConstruct()) { // Check if this behavior placeholder is in a loop sequence.
                        Log.v("Clay", "REMOVING BEHAVIOR CONSTRUCT FROM LOOP CONSTRUCT.");
                        this.getLoopConstruct().removeBehaviorConstruct(this);
//                    this.removeLoopConstruct ();
                    } else {
                        // NOTE: This happens when a free behavior is moved, but not onto a loop (it remains free after being moved).
                        Log.v("Clay", "UNHANGLED CONDITION MET. HANDLE THIS CONDITION!");
                    }
                }
            }
        }

        return resolvedPoint;
    }

//    public void updateState () {
//
//    }

//    public void snapToLoop (Loop loop) {
//        this.getNearestPoint (loop);
//    }

    public void moveBy (int xOffset, int yOffset) {
        position.offset(xOffset, yOffset);
    }

    public boolean touches (int x, int y) {

        double distanceSquare = Math.pow (x - this.position.x, 2) + Math.pow (y - this.position.y, 2);
        double distance = Math.sqrt(distanceSquare);

        // Check if the screen was touched within the action's radius.
        if (distance <= this.radius) {
            return true;
        } else {
            return false;
        }
    }

    public Point getPosition () {
        return this.position;
    }

    public int getRadius () {
        return this.radius;
    }

    public double getDistance (int x, int y) {
        double distanceSquare = Math.pow (x - this.position.x, 2) + Math.pow (y - this.position.y, 2);
        double distance = Math.sqrt (distanceSquare);
        return distance;
    }

    public Point getNearestPoint (LoopPerspective loopPerspective) {

        double deltaX = this.position.x - loopPerspective.getLoopConstruct ().getPosition ().x;
        double deltaY = this.position.y - loopPerspective.getLoopConstruct ().getPosition ().y;
        double angleInDegrees = Math.atan2 (deltaY, deltaX);

        int nearestX = (int) ((0) + (loopPerspective.getRadius ()) * Math.cos(angleInDegrees));
        int nearestY = (int) ((0) + (loopPerspective.getRadius ()) * Math.sin (angleInDegrees));

        return new Point (nearestX, nearestY);
    }

    public double getDistanceToLoopPerspective (LoopPerspective loopPerspective) {

        Point nearestPoint = this.getNearestPoint (loopPerspective);

        double distance = this.getDistance (nearestPoint.x, nearestPoint.y);

        return distance;
    }

    public double getDistanceToLoop (LoopConstruct loopConstruct) {
        return this.getDistance (this.position.x, this.position.y);
    }
}
