package camp.computer.clay.model.interactivity;

import camp.computer.clay.model.architecture.Body;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Time;
import camp.computer.clay.visualization.util.geometry.Point;

public class Action {

    // TODO: Rename "Type" to "Stage" or "Phase". Type should be "Touch", "Sound", "Motion", etc.
    // TODO: Increase MAX_TOUCH_POINT_COUNT to 10
    // TODO: Associate with broader context (e.g., sensor data, including 3D rotation, brightness.

    public enum Type {

        NONE,
        TOUCH,
        HOLD,
        MOVE, // TODO: Remove this. Instead, use MOVE and Interaction.getDistance()/getCardinality()/getSum(":dragDelta").
        RELEASE;
        //TAP; // TODO: Remove this. Instead, put logic in RELEASE and use Interaction.getDuration().

        Type() {
        }

    }

    public static int MAX_TOUCH_POINT_COUNT = 1;

    public static int MAX_TAP_DURATION = 200;

    public static int MIN_HOLD_DURATION = 600;

    public static int MIN_DRAG_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    public Point[] touchPoints = new Point[MAX_TOUCH_POINT_COUNT];

    public boolean[] isTouching = new boolean[MAX_TOUCH_POINT_COUNT];

    private Interaction interaction = null;

    private Type type = null;

    private Body body = null;

    private long timestamp = DEFAULT_TIMESTAMP;

    public int pointerIndex = -1;

    public Action() {
        this.timestamp = Time.getCurrentTime();
        setup();
    }

    private void setup() {
        for (int i = 0; i < MAX_TOUCH_POINT_COUNT; i++) {
            touchPoints[i] = new Point(0, 0);
            touchedImage[i] = null;
            isTouching[i] = false;
        }
    }

    public boolean hasTouches() {
        for (int i = 0; i < MAX_TOUCH_POINT_COUNT; i++) {
            if (isTouching[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean hasInteraction() {
        return interaction != null;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public Interaction getInteraction() {
        return this.interaction;
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
        return this.touchPoints[0];
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    private Image[] touchedImage = new Image[Action.MAX_TOUCH_POINT_COUNT];

    public boolean isTouching(int fingerIndex) {
        return this.touchedImage[fingerIndex] != null;
    }

    public void setTarget(int fingerIndex, Image image) {
        this.touchedImage[fingerIndex] = image;
    }

    public Image getTarget(int fingerIndex) {
        return this.touchedImage[fingerIndex];
    }

    public boolean isTouching() {
        return isTouching(0);
    }

    public void setTarget(Image image) {
        setTarget(0, image);
        if (image != null) {
            isTouching[0] = true;
        }
    }

    public Image getTarget() {
        return getTarget(0);
    }
}