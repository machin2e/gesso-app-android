package camp.computer.clay.model.action;

import camp.computer.clay.engine.entity.Actor;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.time.Clock;

public class Event {

    // TODO: Rename "Type" to "Stage" or "Phase". Type should be "Touch", "Sound", "Motion", etc.
    // TODO: Increase MAXIMUM_POINT_COUNT to 10
    // TODO: Associate with broader context (e.g., sensor data, including 3D rotation, brightness.

    public enum Type {

        NONE,
        SELECT,
        HOLD,
        MOVE,
        UNSELECT,

        ANY,
        ANY_REPEAT;

        Type() {
        }
    }

    public static int MAXIMUM_POINT_COUNT = 1;

    public static int MAXIMUM_TAP_DURATION = 200;

    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    private Action parentAction = null;

    /**
     * The pointerCoordinates at which actions were performed (e.g., the touch pointerCoordinates on a touchscreen).
     */
    public Transform[] pointerCoordinates = new Transform[MAXIMUM_POINT_COUNT];

    // TODO: Delete this!
    public boolean[] isPointing = new boolean[MAXIMUM_POINT_COUNT];

    private Image[] targetImages = new Image[MAXIMUM_POINT_COUNT];

    private Shape[] targetShapes = new Shape[MAXIMUM_POINT_COUNT];

    private Type type = null;

    private Actor actor = null;

    private long timestamp = DEFAULT_TIMESTAMP;

    public int pointerIndex = -1;

    public Event() {
        this.timestamp = Clock.getCurrentTime();
        setup();
    }

    private void setup() {
        for (int i = 0; i < MAXIMUM_POINT_COUNT; i++) {
            pointerCoordinates[i] = new Transform(0, 0);
            targetImages[i] = null;
            targetShapes[i] = null;
            isPointing[i] = false;
        }
    }

    public boolean hasPoints() { // was hasTouches
        for (int i = 0; i < MAXIMUM_POINT_COUNT; i++) {
            if (isPointing[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAction() {
        return parentAction != null;
    }

    public void setAction(Action action) {
        this.parentAction = action;
    }

    public Action getAction() {
        return this.parentAction;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return this.actor;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Transform getPosition() {
        return this.pointerCoordinates[0];
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean isPointing(int pointerIndex) { // was isTouching
        return this.targetImages[pointerIndex] != null;
    }

    public boolean isPointing() { // was isTouching
        return isPointing(0);
    }

    public void setTargetImage(int pointerIndex, Image image) {
        this.targetImages[pointerIndex] = image;
    }

    public void setTargetImage(Image image) {
        setTargetImage(0, image);
        if (image != null) {
            isPointing[0] = true;
        }
    }

    public Image getTargetImage(int pointerIndex) {
        return this.targetImages[pointerIndex];
    }

    public Image getTargetImage() {
        return getTargetImage(0);
    }

    public void setTargetShape(int pointerIndex, Shape shape) {
        this.targetShapes[pointerIndex] = shape;
    }

    public void setTargetShape(Shape shape) {
        setTargetShape(0, shape);
        if (shape != null) {
            isPointing[0] = true;
        }
    }

    public Shape getTargetShape(int pointerIndex) {
        return this.targetShapes[pointerIndex];
    }

    public Shape getTargetShape() {
        return getTargetShape(0);
    }
}