package camp.computer.clay.model.interaction;

import android.util.Log;

import camp.computer.clay.model.sim.Body;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.util.Point;

public class TouchInteraction {

    public static int MAXIMUM_TOUCH_POINT_COUNT = 5;

    public static int MAXIMUM_TAP_DURATION = 200;

    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    private Point[] touchPositions = new Point[MAXIMUM_TOUCH_POINT_COUNT];

    public boolean[] isTouching = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

    private OnTouchActionListener.Type type;

    private Body body;
    // TODO: targetImage? or is the state of body containing this info (e.g., hand occupied with model <M>)

    // <CONTEXT>
    private long timestamp = DEFAULT_TIMESTAMP;
    // TODO: Link to context, e.g., Sensor data (inc. 3D orienetation, brightness).
    // </CONTEXT>

    public int pointerIndex = -1;

    public TouchInteraction(OnTouchActionListener.Type type) {
        this.type = type;
        this.timestamp = java.lang.System.currentTimeMillis ();

        setup();
    }

    private void setup() {
        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
            touchPositions[i] = new Point(0, 0);
            touchedImage[i] = null;
            isTouching[i] = false;
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

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return this.body;
    }

    public OnTouchActionListener.Type getType() {
        return this.type;
    }

    public void setType(OnTouchActionListener.Type type) {
        this.type = type;
        Log.v("Touch", "TouchInteraction." + type + ": " + getPosition().getX() + ", " + getPosition().getY());
        Log.v("Touch", "     on " + getTarget());
    }

    public Point getPosition() {
        return this.touchPositions[0];
    }

    public Point getPosition(int pointerIndex) {
        return this.touchPositions[pointerIndex];
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    private Image[] touchedImage = new Image[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];

    public boolean isTouching(int fingerIndex) {
        return this.touchedImage[fingerIndex] != null;
    }

    public void setTargetImage(int fingerIndex, Image image) {
        this.touchedImage[fingerIndex] = image;
    }

    public Image getTarget(int fingerIndex) {
        return this.touchedImage[fingerIndex];
    }

    public boolean isTouching() {
        return isTouching(0);
    }

    public void setTargetImage(Image image) {
        setTargetImage(0, image);
        if (image != null) {
            isTouching[0] = true;
        }
    }

    public Image getTarget() {
        return getTarget(0);
    }
}