package camp.computer.clay.model.interactivity;

import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Point;

public class Impression {

    public enum Type {

        NONE(0),
        TOUCH(1),
        HOLD(2),
        MOVE(3),
        DRAG(4),
        RELEASE(5),
        TAP(6);

        // TODO: Change the index to a UUID?
        int index;

        Type(int index) {
            this.index = index;
        }
    }

    public static int MAXIMUM_TOUCH_POINT_COUNT = 5;

    public static int MAXIMUM_TAP_DURATION = 200;

    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    public Point[] touchPositions = new Point[MAXIMUM_TOUCH_POINT_COUNT];

    public boolean[] isTouching = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

    private Type type;

    private Body body;

    // <CONTEXT>
    private long timestamp = DEFAULT_TIMESTAMP;
    // TODO: Link to context, e.g., Sensor data (inc. 3D orienetation, brightness).
    // </CONTEXT>

    public int pointerIndex = -1;

    public Impression(Type type) {
        this.type = type;
        this.timestamp = java.lang.System.currentTimeMillis();

        setup();
    }

    private void setup() {
        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
            touchPositions[i] = new Point(0, 0);
            touchedImage[i] = null;
            isTouching[i] = false;
        }
    }

    public boolean hasTouches() {
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

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Point getPosition() {
        return this.touchPositions[0];
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    private Image[] touchedImage = new Image[Impression.MAXIMUM_TOUCH_POINT_COUNT];

    public boolean isTouching(int fingerIndex) {
        return this.touchedImage[fingerIndex] != null;
    }

    public void setTargetImage(int fingerIndex, Image image) {
        this.touchedImage[fingerIndex] = image;
    }

    public Image getTargetImage(int fingerIndex) {
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

    public Image getTargetImage() {
        return getTargetImage(0);
    }
}