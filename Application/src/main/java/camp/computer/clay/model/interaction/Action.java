package camp.computer.clay.model.interaction;

import camp.computer.clay.model.architecture.Actor;
import camp.computer.clay.visualization.architecture.Figure;
import camp.computer.clay.visualization.util.Time;
import camp.computer.clay.visualization.util.geometry.Point;

public class Action {

    // TODO: Rename "Type" to "Stage" or "Phase". Type should be "Touch", "Sound", "Motion", etc.
    // TODO: Increase MAXIMUM_POINT_COUNT to 10
    // TODO: Associate with broader context (e.g., sensor data, including 3D rotation, brightness.

    public enum Type {

        NONE,
        TOUCH,
        HOLD,
        MOVE,
        RELEASE;

        Type() {
        }

    }

    public static int MAXIMUM_POINT_COUNT = 1;

    public static int MAXIMUM_TAP_DURATION = 200;

    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    private Gesture parentGesture = null;

    /**
     * The points at which actions were performed (e.g., the touch points on a touchscreen).
     */
    public Point[] points = new Point[MAXIMUM_POINT_COUNT];

    public boolean[] isPointing = new boolean[MAXIMUM_POINT_COUNT];

    private Figure[] targetFigure = new Figure[MAXIMUM_POINT_COUNT];

    private Type type = null;

    private Actor actor = null;

    private long timestamp = DEFAULT_TIMESTAMP;

    public int pointerIndex = -1;

    public Action() {
        this.timestamp = Time.getCurrentTime();
        setup();
    }

    private void setup() {
        for (int i = 0; i < MAXIMUM_POINT_COUNT; i++) {
            points[i] = new Point(0, 0);
            targetFigure[i] = null;
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

    public boolean hasGesture() {
        return parentGesture != null;
    }

    public void setGesture(Gesture gesture) {
        this.parentGesture = gesture;
    }

    public Gesture getGesture() {
        return this.parentGesture;
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

    public Point getPosition() {
        return this.points[0];
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean isPointing(int fingerIndex) { // was isTouching
        return this.targetFigure[fingerIndex] != null;
    }

    public void setTarget(int fingerIndex, Figure figure) {
        this.targetFigure[fingerIndex] = figure;
    }

    public Figure getTarget(int fingerIndex) {
        return this.targetFigure[fingerIndex];
    }

    public boolean isPointing() { // was isTouching
        return isPointing(0);
    }

    public void setTarget(Figure figure) {
        setTarget(0, figure);
        if (figure != null) {
            isPointing[0] = true;
        }
    }

    public Figure getTarget() {
        return getTarget(0);
    }
}