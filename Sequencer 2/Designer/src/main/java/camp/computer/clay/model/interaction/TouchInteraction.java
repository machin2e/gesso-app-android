package camp.computer.clay.model.interaction;

import android.graphics.PointF;

import camp.computer.clay.model.simulation.Body;
import camp.computer.clay.visualization.Image;

public class TouchInteraction {


    public static int MAXIMUM_TOUCH_POINT_COUNT = 5;

    public static int MAXIMUM_TAP_DURATION = 200;
    public static int MAXIMUM_DOUBLE_TAP_DURATION = 400;
    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    public PointF[] touch = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
    public boolean[] isTouching = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

//    public PointF[] touchPrevious = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
//    public long[] touchPreviousTime = new long[MAXIMUM_TOUCH_POINT_COUNT];
//    public boolean[] isTouchingPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
//    public boolean[] isTouchingActionPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

    // Point where the touch started.
//    public PointF[] touchStart = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
//    public long touchStartTime = java.lang.System.currentTimeMillis ();

    // Point where the touch ended.
//    public PointF[] touchStop = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
//    public long touchStopTime = java.lang.System.currentTimeMillis ();

    // Touch state
    public boolean hasTouches = false; // i.e., At least one touch is detected.
    public int touchCount = 0; // i.e., The total number of touch points detected.

    private void initialize() {

        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
            touch[i] = new PointF();
//            touchPrevious[i] = new PointF();
//            touchStart[i] = new PointF();
//            touchStop[i] = new PointF();
        }
    }

    public boolean hasTouches () {
        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
            if (isTouching[i]) {
                return true;
            }
        }
        return false;
    }




    final public static long DEFAULT_TIMESTAMP = 0L;

    public enum TouchInteractionType {

        NONE(0),
        TOUCH(1),
        HOLD(2),
        MOVE(3),
        PRE_DRAG(4),
        DRAG(5),
        RELEASE(6),
        TAP(7),
        DOUBLE_DAP(8);

        // TODO: Change the index to a UUID?
        int index;

        TouchInteractionType(int index) {
            this.index = index;
        }
    }

    private TouchInteractionType touchInteractionType;

    private Body body;
    // TODO: targetSprite? or is the state of body containing this info (e.g., hand occupied with model <M>)

    private PointF position;

    // <CONTEXT>
    private long timestamp = DEFAULT_TIMESTAMP;
    // TODO: Sensor data (inc. 3D orienetation, brightness)
    // </CONTEXT>

    //public int touches[];
    public int pointerId = -1;

    // touchedImage
    // overlappedImage (not needed, probably, because can look in history, or look at first action in interaction)

    public Image overlappedImage = null;

    //public TouchInteraction(PointF position, TouchInteractionType touchInteractionType) {
    public TouchInteraction(TouchInteractionType touchInteractionType) {
//        this.position = position;
        this.touchInteractionType = touchInteractionType;
        this.timestamp = java.lang.System.currentTimeMillis ();

        initialize();
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return this.body;
    }

    public TouchInteractionType getType() {
        return this.touchInteractionType;
    }

    public void setType(TouchInteractionType touchInteractionType) {
        this.touchInteractionType = touchInteractionType;
    }

    public PointF getPosition() {
        return this.position;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}