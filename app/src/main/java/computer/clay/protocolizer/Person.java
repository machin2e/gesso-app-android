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

}