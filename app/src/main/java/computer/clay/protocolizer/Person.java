package computer.clay.protocolizer;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class Person {

    // public enum loopGesture = { };

    /* Touch Interaction Dynamics for Clay */

//    public static int DEFAULT_TOUCH_COUNT = 5;
    public final int MAXIMUM_TOUCH_COUNT = 5;

//    public static int DEFAULT_DRAG_DISTANCE = 15;
    public final int MINIMUM_DRAG_DISTANCE = 35;

    private boolean hasTouches = false; // i.e., a touch is detected
    private int touchCount = 0; // i.e., the number of touch points detected

//    private Point[] touch = new Point[MAXIMUM_TOUCH_COUNT];
    private double[] xTouch = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouch = new double[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouch = new boolean[MAXIMUM_TOUCH_COUNT];
    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_COUNT];
    private double[] dragDistance = new double[MAXIMUM_TOUCH_COUNT];

    private double[] xTouchPrevious = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouchPrevious = new double[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouchPrevious = new boolean[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouchingActionPrevious = new boolean[MAXIMUM_TOUCH_COUNT];

    // Point where the touch started.
    private double[] xTouchStart = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouchStart = new double[MAXIMUM_TOUCH_COUNT];

    // Point where the touch ended.
    private double[] xTouchStop = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouchStop = new double[MAXIMUM_TOUCH_COUNT];

    private boolean[] isTouchingBehavior = new boolean[MAXIMUM_TOUCH_COUNT];

    private boolean isPerformingLoopGesture = false;
    private LoopConstruct selectedLoop = null; // TODO: Implement this for each finger.

    private boolean isPerformingLoopPerspectiveGesture = false;
    private LoopPerspective selectedLoopPerspective = null; // TODO: Implement this for each finger.
    private boolean selectedLoopPerspectiveStartBoundary = false;

    private boolean isPerformingConditionGesture = false;

    private boolean isPerformingSubstrateGesture = false;

    double previousDistanceToSelectedLoopCenter = 0.0;
    double distanceToSelectedLoopCenter = 0.0;

    BehaviorConstruct touchedBehaviorConstruct = null;

    boolean isPerformingBehaviorGesture = false; // True if touching _any_ action.
    boolean isMovingBehavior = false;

    BehaviorTrigger touchedCondition = null;

    boolean isPerformingPerspectiveGesture = false;
    boolean isMovingPerspective = false; // True if not touching an action, but dragging (not just touching) the canvas.
    double maxDragDistance = 0;
    boolean isCreatingLoopPerspective = false;

    private Clay clay = null;

    Person (Clay clay) {
        this.clay = clay;
    }

    public Clay getClay () {
        return this.clay;
    }

//    public void setTouching (int finger, boolean touching) {
//        this.isTouch[finger] = touching;
//    }

    public void touch (int finger, double x, double y) {

        // Set the previous touch point to the current touch point before updating the current one.
        this.updatePreviousTouch (finger);

        // Update the current touch point.
        this.isTouch[finger] = true;
        this.xTouch[finger] = x;
        this.yTouch[finger] = y;

//        // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
//        if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == false) {
//            this.xTouchStart[finger] = this.xTouch[finger];
//            this.yTouchStart[finger] = this.yTouch[finger];
//
//            isTouchingBehavior[pointerId] = false;
//
//            isDragging[pointerId] = false;
//            dragDistance[pointerId] = 0;
//        }

        // TODO: Classify ongoing gesture.
        this.classify (finger);
    }

    public boolean isTouching (int finger) {
        return this.isTouch[finger];
    }

    public Point getTouch (int finger) {
        if (this.isTouching(finger)) {
            return new Point((int) this.xTouch[finger], (int) this.yTouch[finger]);
        } else {
            return null;
        }
    }

    public void untouch (int finger, double x, double y) {

//        // Set the previous touch state to the current touch state before updating the current one.
//        this.isTouchPrevious[finger] = this.isTouch[finger];
//
//        // Update the current touch state.
//        this.isTouch[finger] = false;

        // Set the previous touch point to the current touch point before updating the current one.
        this.updatePreviousTouch (finger);

        // Update the current touch point.
        this.isTouch[finger] = false;
        this.xTouch[finger] = x;
        this.yTouch[finger] = y;

        // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
        if (this.isTouch[finger] == false && this.isTouchPrevious[finger] == true) {
            this.xTouchStop[finger] = this.xTouch[finger];
            this.yTouchStop[finger] = this.yTouch[finger];
        }

        // TODO: Classify completed gesture.
    }

    private void updatePreviousTouch (int finger) {

        // TODO: Store the current value of previousTouch in a local timeline/database.

        this.isTouchPrevious[finger] = this.isTouch[finger];
        this.xTouchPrevious[finger] = this.xTouch[finger];
        this.yTouchPrevious[finger] = this.yTouch[finger];
    }

    public void unsetPreviousTouch (int finger) {
        this.isTouchPrevious[finger] = false;
    }

    public void classify (int finger) {

        // isDragging
        // dragDistance
        // isPerformingPerspectiveGesture
        // isMovingPerspective
        // selectedLoop
        // isPerformingLoopGesture
        // isCreatingLoopPerspective
        // isPerformingBehaviorGesture
        // touchedBehaviors
        // TODO: touchedBehaviorConstruct[]

        // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)

        if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == false) { // touch...

            Log.v ("Clay", "touch");

            // TODO: Determine what was touched: behavior? condition? system?

            // Set the first point of touch.
            this.xTouchStart[finger] = this.xTouch[finger];
            this.yTouchStart[finger] = this.yTouch[finger];

            this.isTouchingBehavior[finger] = false;
            this.isDragging[finger] = false;
            this.dragDistance[finger] = 0;

            // Check if performing behavior construct gesture.
            // i.e., Check if touching _any_ behaviors (or loops, or canvas, or perspective). If so, keep the canvas locked, and find the action that's being touched.
            for (BehaviorConstruct behaviorConstruct : this.getClay ().getPerspective ().getBehaviorConstructs ()) {
                double distanceToTouch = behaviorConstruct.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                if (distanceToTouch < behaviorConstruct.getRadius () + 20) {

                    // A behavior gesture is being performed.
                    if (isPerformingBehaviorGesture != true) {
                        Log.v("Clay", "starting behavior gesture");
                        isPerformingBehaviorGesture = true;
                    }

                    // Update the state of the touched behavior.
                    isTouchingBehavior[finger] = true; // TODO: Set state of finger
                    behaviorConstruct.state = BehaviorConstruct.State.MOVING;
//                    getClay().getPerspective().setScaleFactor(0.8f);

                    // Add the touched behavior to the list of touched behaviors.
                    touchedBehaviorConstruct = behaviorConstruct;
                }
            }

            // Check if performing loop construct gesture.
            if (!isPerformingBehaviorGesture) {

                // Check if a loop is being touched.
                for (LoopConstruct loopConstruct : this.getClay ().getPerspective ().getLoopConstructs ()) {

                    double distanceToTouch = loopConstruct.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                    Log.v("Clay", "distanceToTouch = " + distanceToTouch);
                    if (distanceToTouch < 0.50 * loopConstruct.getRadius()) {

                        Log.v("Clay", "starting loop gesture");
                        isPerformingLoopGesture = true;
                        selectedLoop = loopConstruct;

                    }
                }
            }

            // Check if performing loop perspective gesture.
            if (!isPerformingBehaviorGesture && !isPerformingLoopGesture) {

                // TODO: Determine the nearest perspective on the selected loop for the finger (if any).

                if (getClay ().hasUnits ()) {
                    // Declare the distance around arc that will respond to touch
                    float conditionTouchProximity = 100;

                    // Get the nearest loop to the touch
                    // TODO: Move this into a new function Perspective.getNearestLoopConstruct().
                    LoopConstruct nearestLoopConstruct = null;
                    float nearestLoopDistance = Float.MAX_VALUE;
                    for (LoopConstruct loopConstruct : this.getClay ().getPerspective ().getLoopConstructs ()) {

                        double distanceToTouch = loopConstruct.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                        if (distanceToTouch < nearestLoopDistance) {
                            nearestLoopDistance = (float) distanceToTouch;
                            nearestLoopConstruct = loopConstruct;
                        }
                    }

                    // Calculate the angle of the touch point relative to the nearest loop.
                    double touchAngle = nearestLoopConstruct.getAngle ((int) xTouch[finger], (int) yTouch[finger]);
                    Log.v ("Clay_Loop_Perspective", "touchAngle = " + touchAngle);

                    // Get the start angle and span of the perspective

                    LoopPerspective nearestLoopPerspectiveBoundary = null;
                    double distanceToNearestLoopPerspectiveBoundary = Double.MAX_VALUE;
                    ArrayList<LoopPerspective> nearestLoopPerspectives = nearestLoopConstruct.getPerspectives ();
                    for (LoopPerspective loopPerspective : nearestLoopPerspectives) {
                        double startAngle = loopPerspective.startAngle;
                        double stopAngle = loopPerspective.startAngle + loopPerspective.span;
                        Log.v ("Clay_Loop_Perspective", "startAngle = " + startAngle);
                        Log.v ("Clay_Loop_Perspective", "stopAngle = " + stopAngle);

                        double distanceToStart = Math.max (touchAngle, startAngle) - Math.min (touchAngle, startAngle);
                        double distanceToStop = Math.max (touchAngle, stopAngle) - Math.min (touchAngle, stopAngle);
                        if (distanceToStart < distanceToNearestLoopPerspectiveBoundary) {
                            distanceToNearestLoopPerspectiveBoundary = distanceToStart;
                            nearestLoopPerspectiveBoundary = loopPerspective;
                            selectedLoopPerspectiveStartBoundary = true;
                            Log.v ("Clay_Loop_Perspective", "START boundary is nearest");
                        }

                        if (distanceToStop < distanceToNearestLoopPerspectiveBoundary) {
                            distanceToNearestLoopPerspectiveBoundary = distanceToStop;
                            nearestLoopPerspectiveBoundary = loopPerspective;
                            selectedLoopPerspectiveStartBoundary = false; // "false" flag means the stop boundary is selected, not the start boundary
                            Log.v ("Clay_Loop_Perspective", "STOP boundary is nearest");
                        }

                    }

                    int loopPerspectiveBoundaryTouchThreshold = 5;
                    if (distanceToNearestLoopPerspectiveBoundary < loopPerspectiveBoundaryTouchThreshold) {
                        Log.v ("Clay_Loop_Perspective", "PERFORMING loop perspective gesture");
                        isPerformingLoopPerspectiveGesture = true;
                        selectedLoopPerspective = nearestLoopPerspectiveBoundary;
                    }

                    // TODO: Calculate positions of endpoints of a perspective, which will be the target points for touch gestures.
                    // TODO: Get the position of a touch.
                    // TODO: Calculate the distance between the endpoints of the perspective and the touch.
                    // TODO: If the distance is small enough, consider the endpoint touched and update the position of the touched endpoint to the position of the touch.
                }
            }

            // Check if performing behavior condition construct gesture.
            if (!isPerformingBehaviorGesture && !isPerformingLoopGesture && !isPerformingLoopPerspectiveGesture) {

                if (getClay ().hasUnits ()) {
                    // Declare the distance around arc that will respond to touch
                    float conditionTouchProximity = 100;

                    // Get the nearest loop to the touch
                    // TODO: Move this into a new function Perspective.getNearestLoopConstruct().
                    LoopConstruct nearestLoopConstruct = null;
                    float nearestLoopDistance = Float.MAX_VALUE;
//                for (Loop loop : this.getClay ().getSystem ().getLoops()) {
//                    LoopConstruct loopConstruct = this.getClay ().getPerspective ().getLoopConstruct (loop);
                    for (LoopConstruct loopConstruct : this.getClay ().getPerspective ().getLoopConstructs ()) {

                        double distanceToTouch = loopConstruct.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                        if (distanceToTouch < nearestLoopDistance) {
                            nearestLoopDistance = (float) distanceToTouch;
                            nearestLoopConstruct = loopConstruct;
                        }
                    }

                    Point touchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                    LoopPerspective nearestLoopPerspective = nearestLoopConstruct.getPerspective (touchPoint);

                    if (nearestLoopDistance < (nearestLoopPerspective.getRadius () + conditionTouchProximity)) {
                        double touchAngle = nearestLoopConstruct.getAngle ((int) xTouch[finger], (int) yTouch[finger]); // i.e., the touch angle relative to the nearest loop

                        BehaviorTrigger behaviorTrigger = nearestLoopConstruct.getBehaviorConditionAtAngle (touchAngle);
                        if (behaviorTrigger != null) {
                            isPerformingConditionGesture = true;
                            Log.v ("Condition", "starting condition gesture");
                            this.touchedCondition = behaviorTrigger;
                        }

                        // TODO: Loop.getBehaviorBeforeAngle(float angle)
                        // TODO: Loop.getBehaviorAfterAngle(float angle)
                        // TODO: Loop.getConditionAtAngle(float angle)
                    }
                }
            }

            // Check if performing a perspective construct gesture.
            if (!isPerformingBehaviorGesture && !isPerformingLoopGesture && !isPerformingLoopPerspectiveGesture && !isPerformingConditionGesture) {
                Log.v("Condition", "starting perspective gesture");
                isPerformingPerspectiveGesture = true;
            }

        } else if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == true) { // ...continue touching...

//            Log.v ("Clay", "continuing touch");

            // Calculate the drag distance
            double dragDistanceSquare = Math.pow(xTouch[finger] - xTouchStart[finger], 2) + Math.pow(yTouch[finger] - yTouchStart[finger], 2);
            dragDistance[finger] = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);

//            Log.v ("Clay", "dragDistance = " + dragDistance[finger]);

            // Check if a drag is occurring (defined by continuously touching the screen while deviating from the initail point of touch by more than 15 pixels)
            if (dragDistance[finger] > this.MINIMUM_DRAG_DISTANCE) {
                // TODO: Get distance between down and current touch point. Set isMovingPerspective to true if the drag distance is greater than a specified threshold.
                isDragging[finger] = true;

                // TODO: Move this into a separate processTouchInteraction() function, and in this event listener, only update the touch interaction state.

                // Check if a loop gesture is being performed.
                if (isPerformingBehaviorGesture) {

                    Log.v("Condition", "continuing behavior gesture (response)");
                    isMovingBehavior = true;

                } else if (isPerformingConditionGesture) {

                    Log.v("Condition", "continuing condition gesture (response)");

                } else if (isPerformingLoopGesture) {

                    Log.v("Condition", "continuing loop gesture (response)");

                    // Get the distance from the center of the loop "selected" with the gesture.
//                    LoopConstruct selectedLoopConstruct = this.getClay ().getPerspective ().getLoopConstruct (selectedLoop);
                    previousDistanceToSelectedLoopCenter = selectedLoop.getDistance ((int) xTouchPrevious[finger], (int) yTouchPrevious[finger]);
                    distanceToSelectedLoopCenter = selectedLoop.getDistance ((int) xTouch[finger], (int) yTouch[finger]);

                    Point touchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                    LoopPerspective selectedLoopPerspective = selectedLoop.getPerspective (touchPoint);

//                            Log.v ("Clay", "distanceToLoopCenter = " + distanceToLoopCenter);
                    if (previousDistanceToSelectedLoopCenter < selectedLoopPerspective.getRadius() && distanceToSelectedLoopCenter > selectedLoopPerspective.getRadius ()) {
                        Log.v("Clay", "Cut the loop.");

                        isCreatingLoopPerspective = true;

                        // TODO: Get the angle and (x,y) coordinate at which the loop was crossed (exited).
                        //if (!this.getClay ().getPerspective ().getLoopConstruct (selectedLoop).hasCandidatePerspective (selectedLoop)) {
                        if (!selectedLoop.hasCandidatePerspective (selectedLoop.getLoop ())) {
//                        if (this.currentLoopPerspective == null) {
                        // TODO: Replace the above conditional with a check for proximity (so can adjust existing loop)?
                            Point startAnglePoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                            int startAngle = (int) selectedLoop.getAngle (startAnglePoint);
                            LoopPerspective candidateLoopPerspective = new LoopPerspective (selectedLoop, startAngle, 0);
                            candidateLoopPerspective.startAnglePoint = startAnglePoint;
                            candidateLoopPerspective.startAngle = startAngle;
                            Log.v ("Clay", "startAngle = " + candidateLoopPerspective.startAngle);
                            //this.getClay ().getPerspective ().getLoopConstruct (selectedLoop).setCandidatePerspective(candidateLoopPerspective);
                            selectedLoop.setCandidatePerspective (candidateLoopPerspective);
                        }

//                        if (this.perspective.startAnglePoint == null) {
//                            this.perspective.startAnglePoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                            this.perspective.startAngle = (int) selectedLoop.getAngle((int) xTouch[finger], (int) yTouch[finger]);
//                            Log.v ("Clay", "startAngle = " + this.perspective.startAngle);
//                        }

                        // TODO: Calculate startAngle

                    } else if (previousDistanceToSelectedLoopCenter > selectedLoopPerspective.getRadius () && distanceToSelectedLoopCenter < selectedLoopPerspective.getRadius ()) {
                        Log.v ("Clay", "Uncut the loop.");

                        // A finger was dragged from the outside of a loop back to the inside of a
                        // loop (after starting, originally, in inside the loop and going outside
                        // the loop).
                        //
                        // The effect of doing this is "reversing" the gesture, thereby "undoing"
                        // the result of the gesture (i.e., the result of crossing to the outside
                        // of the loop from the inside of the loop in a single drag gesture).

                        if (selectedLoop.hasCandidatePerspective (selectedLoop.getLoop ())) {
                            selectedLoop.removeCandidatePerspective (selectedLoop.getLoop ());
                        }

//                        if (this.perspective.hasPerspectives(selectedLoop)) {
//                            this.perspective.removePerspective(selectedLoop);
//                        }

//                        // Clear the angle and (x,y) coordinate at which the loop was crossed (entered).
//                        if (this.perspective.startAnglePoint != null) {
//                            this.perspective.startAnglePoint = null;
//                            this.perspective.spanPoint = null;
//                            this.perspective.startAngle = 0;
//                            this.perspective.span = 0;
//                        }

                    }

                    // If started cutting the loop, then calculate the angle offset of the cut in degrees.
                    if (selectedLoop.hasCandidatePerspective (selectedLoop.getLoop ())) {
//                    if (this.currentLoopPerspective != null) {

                        LoopPerspective candidateLoopPerspective = selectedLoop.getCandidatePerspective (selectedLoop.getLoop ());

                        Point currentTouchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                        // TODO: Calculate the end angle between the three points (loop center, startAnglePoint, and the current touch point)
                        candidateLoopPerspective.span = (int) selectedLoop.getAngle(candidateLoopPerspective.startAnglePoint, currentTouchPoint);
                        // double loopCutSpanPselectedLoop.getAngle (currentTouchPoint.x, currentTouchPoint.y);
                        Log.v ("Clay", "startAngle = " + candidateLoopPerspective.startAngle);
                        candidateLoopPerspective.spanPoint = selectedLoop.getPoint (candidateLoopPerspective.span); // (startAngle + span);
                        Log.v ("Clay", "angle = " + candidateLoopPerspective.span);

                    }

//                    // If started cutting the loop, then calculate the angle offset of the cut in degrees.
//                    if (this.perspective.startAnglePoint != null) {
//
//                        Point currentTouchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                        // TODO: Calculate the end angle between the three points (loop center, startAnglePoint, and the current touch point)
//                        this.perspective.span = (int) selectedLoop.getAngle(this.perspective.startAnglePoint, currentTouchPoint);
//                        // double loopCutSpanPselectedLoop.getAngle (currentTouchPoint.x, currentTouchPoint.y);
//                        Log.v ("Clay", "startAngle = " + this.perspective.startAngle);
//                        this.perspective.spanPoint = selectedLoop.getPoint (this.perspective.span); // (startAngle + span);
//                        Log.v ("Clay", "angle = " + this.perspective.span);
//                    }

                } else if (isPerformingLoopPerspectiveGesture) {

                    Log.v("Clay_Loop_Perspective", "long continuing loop perspective gesture (response)");

                } else if (isPerformingPerspectiveGesture) {

                    // Log.v("Condition", "continuing perspective gesture (response)");

                    // If a loop gesture is not being performed, then it must be the case that the perspective is being moved.
                    if (isTouchingBehavior[finger] == false) {

                        // Move the perspective if it an be moved.
                        if (getClay().enablePerspectiveMovingProperty) {
                            isMovingPerspective = true;
                        }

                    }
                }
            }


            // Move the perspective over the canvas if this is a drag event!
            if (isPerformingPerspectiveGesture) {

                if (isMovingPerspective) {
                    getClay ().getPerspective ().moveBy((int) (xTouch[finger] - xTouchStart[finger]), (int) (yTouch[finger] - yTouchStart[finger]));
                }

            } else if (isPerformingLoopGesture) {

                // TODO: Start constructing a viewing angle model to use to construct a viewing angle.

                // TODO: Look for the point on the loop at which the finger crosses the line (i.e., the distance is greater than the loop's radius).

            } else if (isPerformingLoopPerspectiveGesture) {

                Log.v("Clay_Loop_Perspective", "continuing loop perspective gesture (response)");

                if (selectedLoopPerspective != null) {

                    // TODO: Replace the above conditional with a check for proximity (so can adjust existing loop)?
//                    LoopPerspective candidateLoopPerspective = new LoopPerspective(selectedLoop);
//                    candidateLoopPerspective = new LoopPerspective(selectedLoop);
                    if (selectedLoopPerspectiveStartBoundary == true) {
                        selectedLoopPerspective.startAnglePoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                        selectedLoopPerspective.startAngle = (int) selectedLoopPerspective.getLoopConstruct ().getAngle ((int) xTouch[finger], (int) yTouch[finger]);
                        selectedLoopPerspective.setStartAngle((int) selectedLoopPerspective.getLoopConstruct ().getAngle ((int) xTouch[finger], (int) yTouch[finger]));
                    } else if (selectedLoopPerspectiveStartBoundary == false) {
//                        selectedLoopPerspective.startAnglePoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                        selectedLoopPerspective.startAngle = (int) selectedLoop.getAngle ((int) xTouch[finger], (int) yTouch[finger]);

                        selectedLoopPerspective.spanPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                        int loopCutAngle = (int) selectedLoopPerspective.getLoopConstruct ().getAngle ((int) xTouch[finger], (int) yTouch[finger]);

                        int loopSpanStartAngle = selectedLoopPerspective.getLoopConstruct ().getStartAngle () + selectedLoopPerspective.getLoopConstruct ().getAngleSpan ();

//                        selectedLoopPerspective.span = loopCutAngle - selectedLoopPerspective.startAngle;
                        selectedLoopPerspective.setSpan(loopCutAngle - selectedLoopPerspective.startAngle);
                    }

                    selectedLoopPerspective.updatePerspectives();
//                    Log.v("Clay", "startAngle = " + selectedLoopPerspective.startAngle);
//                    this.perspective.setCandidatePerspective(selectedLoopPerspective);

                }

            } else if (isPerformingBehaviorGesture) {

                Log.v ("Clay_Loop_Construct", "performing behavior construct gesture");

                if (isMovingBehavior) {

                    // Get the nearest loop construct to the touch
                    // TODO: Move this into a new function Perspective.getNearestLoopConstruct().
                    LoopConstruct nearestLoopConstruct = null;
                    float nearestLoopDistance = Float.MAX_VALUE;
                    for (LoopConstruct loopConstruct : this.getClay().getPerspective().getLoopConstructs()) {
                        double distanceToTouch = loopConstruct.getDistance((int) xTouch[finger], (int) yTouch[finger]);
                        if (distanceToTouch < nearestLoopDistance) {
                            nearestLoopDistance = (float) distanceToTouch;
                            nearestLoopConstruct = loopConstruct;
                        }
                    }

                    // Declare the distance around arc that will respond to touch
                    float conditionTouchProximity = nearestLoopConstruct.getRadius() + 200;

                    Log.v("Clay_Loop_Construct", "nearestLoopDistance = " + nearestLoopDistance);
                    Log.v("Clay_Loop_Construct", "conditionTouchProximity = " + conditionTouchProximity);

                    if (nearestLoopDistance > conditionTouchProximity) {
                        Log.v("Clay_Loop_Construct", "removing from loop: " + nearestLoopDistance);
                        touchedBehaviorConstruct.state = BehaviorConstruct.State.MOVING;
//                        getClay().getPerspective().setScaleFactor(0.8f);
//                    if (behaviorConstruct.hasLoopConstruct ()) {
//                        behaviorConstruct.getLoopConstruct ().removeBehaviorConstruct (behaviorConstruct);
//                    }
                    } else {
                        Log.v("Clay_Loop_Construct", "adding to loop: " + nearestLoopDistance);
                        touchedBehaviorConstruct.state = BehaviorConstruct.State.COUPLED;
//                        getClay().getPerspective().setScaleFactor(0.8f);
//                    nearestLoopConstruct.addBehaviorConstruct (behaviorConstruct);
                    }

                    // TODO: Get nearest loop to the touch point (where the behavior is being dragged)
                    // TODO: Get nearest loop perspective
                    // TODO: Get the loop placeholder associated with the nearest perspective
                    // TODO: Update the behavior's loop (based on the loop in the placeholder). (TODO: Shift removed behaviors into place in perspectives observing the segment where the removed behavior was.)
                    // TODO: Update position based on inferred position relative to the nearest loop in snapping range.
                    // TODO: Get the segment of the loop placeholder exposed by the perspective
                    // TODO: Calculate the relative angle of the touched behavior on the loop segment
                    // TODO: Calculate the relative point of the touched behavior on the loop segment

                    // Update the position of the behavior construct to the touched point.
                    touchedBehaviorConstruct.setPosition((int) xTouch[finger], (int) yTouch[finger]);

//                for (BehaviorConstruct behaviorConstruct : touchedBehaviors) {
//                    behaviorConstruct.setPosition ((int) xTouch[finger], (int) yTouch[finger]);
//
//                    if (nearestLoopDistance > conditionTouchProximity) {
//                        Log.v ("Clay_Loop_Construct", "removing from loop: " + nearestLoopDistance);
//                        if (behaviorConstruct.hasLoopConstruct ()) {
//                            behaviorConstruct.getLoopConstruct ().removeBehaviorConstruct (behaviorConstruct);
//                        }
//                    } else {
//                        Log.v ("Clay_Loop_Construct", "adding to loop: " + nearestLoopDistance);
//                        nearestLoopConstruct.addBehaviorConstruct (behaviorConstruct);
//                    }
//
////                    if (behaviorConstruct.hasLoopConstruct ()) {
////                        behaviorConstruct.getLoopConstruct ().removeBehaviorConstruct (behaviorConstruct);
////                    }
//
////                    behaviorConstruct.removeLoopConstruct ();/*
////                    behaviorConstruct.setLoopConstruct (nearestLoopConstruct);*/
//                }
                }
            }

        } else if (this.isTouch[finger] == false && this.isTouchPrevious[finger] == true) { // ...untouch.

            Log.v ("Clay", "untouch");

            // Move the canvas if this is a drag event!
            if (isPerformingBehaviorGesture) {

                if (isMovingBehavior) {
                    Log.v("Condition", "stopping behavior gesture");

                    // TODO: If moving an action, upon release, call "searchForPosition()" to check the "logical state" of the action in the system WRT the other loops, and find it's final position and update its state (e.g., if it's near enough to snap to a loop, to be deleted, etc.).

                    Log.v("Clay", "before isTouchingBehavior[pointerId]");
//                if (touchedBehaviors.size() > 0) {
                    if (touchedBehaviorConstruct != null) {
//                    Log.v("Clay", "touchedBehaviors.size() = " + touchedBehaviors.size());

                        Log.v("Clay_Loop_Construct", "performing behavior construct gesture");

                        // Get the nearest loop construct to the touch
                        // TODO: Move this into a new function Perspective.getNearestLoopConstruct().
                        LoopConstruct nearestLoopConstruct = null;
                        float nearestLoopDistance = Float.MAX_VALUE;
                        for (LoopConstruct loopConstruct : this.getClay().getPerspective().getLoopConstructs()) {
                            double distanceToTouch = loopConstruct.getDistance((int) xTouch[finger], (int) yTouch[finger]);
                            if (distanceToTouch < nearestLoopDistance) {
                                nearestLoopDistance = (float) distanceToTouch;
                                nearestLoopConstruct = loopConstruct;
                            }
                        }

                        // Declare the distance around arc that will respond to touch
                        float conditionTouchProximity = nearestLoopConstruct.getRadius() + 200;

                        Log.v("Clay_Loop_Construct", "nearestLoopDistance = " + nearestLoopDistance);
                        Log.v("Clay_Loop_Construct", "conditionTouchProximity = " + conditionTouchProximity);

                        // TODO: Move this into LoopConstruct.settlePosition()
                        if (nearestLoopDistance > conditionTouchProximity) {
                            Log.v("Clay_Loop_Construct", "removing from loop: " + nearestLoopDistance);
                            touchedBehaviorConstruct.state = BehaviorConstruct.State.FREE; // TODO: Move this into LoopConstruct.settlePosition()
//                        getClay().getPerspective().setScaleFactor(1.0f);
                            // TODO: send UDP message "remove behavior <uuid> from loop <uuid>"
                            if (touchedBehaviorConstruct.hasLoopConstruct()) {
                                touchedBehaviorConstruct.getLoopConstruct().removeBehaviorConstruct(touchedBehaviorConstruct);
                            }
                        } else {
                            Log.v("Clay_Loop_Construct", "adding to loop: " + nearestLoopDistance);
                            touchedBehaviorConstruct.state = BehaviorConstruct.State.SEQUENCED; // TODO: Move this into LoopConstruct.settlePosition()
//                        getClay().getPerspective().setScaleFactor(1.0f);
//                        nearestLoopConstruct.addBehaviorConstruct (touchedBehaviorConstruct);
                            // TODO: send UDP message "add behavior <uuid> to loop <uuid>"
                            // <HACK>
                            // Queue behavior transformation in the outgoing message queue.
                            // e.g., create behavior <uuid> "turn light <number> on" --> Response: got <message>
                            // e.g., (shorthand) "add behavior <uuid> to loop (<uuid>)"
                            // e.g., "focus perspective on behavior <uuid>" (Changes perspective so implicit language refers to it.)
                            String behaviorUuid = touchedBehaviorConstruct.getUuid().toString(); // HACK: BehaviorConstruct and Behavior should have separate UUIDs.
                            //String behaviorConstructUuid = behaviorConstruct.getUuid ().toString (); // HACK: BehaviorConstruct and Behavior should have separate UUIDs.
//                        getClay ().getCommunication ().sendMessage (nearestLoopConstruct.getUnit ().getInternetAddress (), "create behavior " + behaviorUuid + " \"" + touchedBehaviorConstruct.getBehavior ().getTitle () + "\"");
//                        getClay ().getCommunication ().sendMessage (nearestLoopConstruct.getUnit ().getInternetAddress (), "add behavior " + behaviorUuid + " to loop");
//                        // </HACK>
                        }

                        // Settle position of action.
//                    for (BehaviorConstruct behaviorConstruct : touchedBehaviors) {
//                        behaviorConstruct.settlePosition();
//                    }
                        touchedBehaviorConstruct.settlePosition();

                        // HACK: This hack removes _all_ touched behaviors when _any_ finger is lifted up.
//                    touchedBehaviors.clear();
//                    touchedBehaviorConstruct = null;
                        // TODO: Remove specific finger from the list of fingers touching down.

                        // HACK: This hack updates the touch flag that indicates if _any_ finger is touching to false.
                        isPerformingBehaviorGesture = false;
                        // TODO: Set state of finger

//                            // Update the gesture state
//                            isPerformingLoopGesture = false;
//                            selectedLoop = null;

                        // Delete behavior (if it's in the right position!)
                        if (touchedBehaviorConstruct.getPosition().y > 750) {
                            // Remove behavior construct from perspective!
                            // NOTE: This shouldn't delete it from the behavior repository!
                            LoopConstruct loopConstruct = touchedBehaviorConstruct.getLoopConstruct();
                            getClay().getPerspective().getBehaviorConstructs().remove(touchedBehaviorConstruct);
                        }
                    }

                } else {
                    Log.v ("Clay_Update_Behavior", "Updating behavior");

                    // <HACK>
                    ((MainActivity) getClay ().getPlatformContext()).Hack_PromptForBehaviorTransform(touchedBehaviorConstruct);
                    // </HACK>

                    // TODO: Populate the behavior transform editor with the present behavior.
                }

                isMovingBehavior = false;

            } else if (isPerformingConditionGesture) {

                Log.v ("Condition", "stopping condition gesture");
                Log.v ("Condition", "touchedCondition = " + touchedCondition);

                if (this.touchedCondition != null) {

                    Log.v ("Condition", "touchedCondition.type = " + this.touchedCondition.getType());

                    if (touchedCondition.getType() == BehaviorTrigger.Type.NONE) {
                        touchedCondition.setType(BehaviorTrigger.Type.SWITCH);
                    } else if (touchedCondition.getType() == BehaviorTrigger.Type.SWITCH) {
                        touchedCondition.setType(BehaviorTrigger.Type.THRESHOLD);
                    } else if (touchedCondition.getType() == BehaviorTrigger.Type.THRESHOLD) {
                        touchedCondition.setType(BehaviorTrigger.Type.GESTURE);
                    } else if (touchedCondition.getType() == BehaviorTrigger.Type.GESTURE) {
                        touchedCondition.setType(BehaviorTrigger.Type.MESSAGE);
                    } else if (touchedCondition.getType() == BehaviorTrigger.Type.MESSAGE) {
                        touchedCondition.setType(BehaviorTrigger.Type.NONE);
                    }

                    touchedCondition = null;
                }

                // TODO:
                // - Get the condition associated with this gesture
                // - Change the condition to the next condition type in the list
                // - TODO: (in continuing condition gesture) As dragging toward and away from the loop center, cycle through the condition types!

            } else if (isPerformingLoopGesture) {

                Log.v ("Condition", "stopping loop gesture");

                if (isCreatingLoopPerspective) {

                    // Create the loop perspective and associate with the system perspective.
                    LoopPerspective candidateLoopPerspective = selectedLoop.getCandidatePerspective (this.selectedLoop.getLoop ());
                    selectedLoop.addPerspective (candidateLoopPerspective);

                    // Reset the "live action" state in the current perspective to prepare for
                    // future "live action" gestures.
//                    this.currentLoopPerspective = null;
                    selectedLoop.removeCandidatePerspective (this.selectedLoop.getLoop ());
                    candidateLoopPerspective = this.selectedLoop.getCandidatePerspective(this.selectedLoop.getLoop ());
                    Log.v("Clay", "candidatePerspective = " + candidateLoopPerspective);

//                    this.perspective.startAnglePoint = null;
//                    this.perspective.spanPoint = null;
//
//                    this.perspective.startAngle = 0;
//                    this.perspective.span = 0;

                    // Update the gesture state
                    isPerformingLoopGesture = false;
                    selectedLoop = null;
                }

            } else if (isPerformingLoopPerspectiveGesture) {

                Log.v ("Clay_Loop_Perspective", "stopping loop perspective gesture");

                // Check if the perspective is small enough to delete.
                if (selectedLoopPerspective != null) {
                    if (selectedLoopPerspective.span < 20) {
                        this.selectedLoop.removePerspective (selectedLoopPerspective);
                    }
                }

                isPerformingLoopPerspectiveGesture = false;
                selectedLoopPerspective = null;
                selectedLoopPerspectiveStartBoundary = false;

            } else if (isPerformingPerspectiveGesture) {

                Log.v ("Condition", "stopping perspecting gesture");

                if (isMovingPerspective) {

                    this.getClay().getPerspective().moveBy((int) (xTouch[finger] - xTouchStart[finger]), (int) (yTouch[finger] - yTouchStart[finger]));

                } else {

                    // Add a behavior construct from the perspective.
                    if (getClay ().hasUnits ()) {
                        // TODO: this.getClay ().getPerspective ().createBehaviorConstruct (loopConstruct)
//                    BehaviorConstruct behaviorConstruct = new BehaviorConstruct (this.getClay ().getPerspective (), (int) xTouch[finger], (int) yTouch[finger]);
//                    this.getClay ().getPerspective ().addBehaviorConstruct (behaviorConstruct);
                        Point touchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                        BehaviorConstruct behaviorConstruct = getClay ().getPerspective ().createBehaviorConstruct (touchPoint);
                        // nearestLoopConstruct.reorderBehaviors ();
                        behaviorConstruct.settlePosition ();
                    }

                }
            }

            /* Reset touch state for the finger. */

            isTouchingBehavior[finger] = false;

            isDragging[finger] = false;
            dragDistance[finger] = 0;

            // TODO: In processTouchInteractions, compute isMovingPerspective and if it is true, move the perspective.
            isPerformingBehaviorGesture = false;
            isMovingBehavior = false;
            isPerformingLoopGesture = false;
            isCreatingLoopPerspective = false;
            isPerformingLoopPerspectiveGesture = false;
            isPerformingConditionGesture = false;
            isPerformingPerspectiveGesture = false;
            isMovingPerspective = false;

        }

    }

//    public void classifyUntouch (int finger) {
//
//
//
//    }

}