package camp.computer.clay.model.interaction;

import camp.computer.clay.model.architecture.Actor;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.util.Time;
import camp.computer.clay.scene.util.geometry.Point;

public class Action {

    // TODO: Rename "Type" to "Stage" or "Phase". Type should be "Touch", "Sound", "Motion", etc.
    // TODO: Increase MAXIMUM_POINT_COUNT to 10
    // TODO: Associate with broader context (e.g., sensor data, including 3D rotation, brightness.

    public enum Type {

        NONE,
        SELECT, // Consider renaming to CONNECT or ATTACH or ENGAGE
        HOLD,
        MOVE,
        UNSELECT; // Consider renaming to DISCONNECT or DETACH or DISENGAGE

        Type() {
        }

    }

    public static int MAXIMUM_POINT_COUNT = 1;

    public static int MAXIMUM_TAP_DURATION = 200;

    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    private Process parentProcess = null;

    /**
     * The points at which actions were performed (e.g., the touch points on a touchscreen).
     */
    public Point[] points = new Point[MAXIMUM_POINT_COUNT];

    public boolean[] isPointing = new boolean[MAXIMUM_POINT_COUNT];

    private Image[] targetImage = new Image[MAXIMUM_POINT_COUNT];

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
            targetImage[i] = null;
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

    public boolean hasPattern() {
        return parentProcess != null;
    }

    public void setProcess(Process process) {
        this.parentProcess = process;
    }

    public Process getActionSequence() {
        return this.parentProcess;
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
        return this.targetImage[fingerIndex] != null;
    }

    public void setTarget(int fingerIndex, Image image) {
        this.targetImage[fingerIndex] = image;
    }

    public Image getTarget(int fingerIndex) {
        return this.targetImage[fingerIndex];
    }

    public boolean isPointing() { // was isTouching
        return isPointing(0);
    }

    public void setTarget(Image image) {
        setTarget(0, image);
        if (image != null) {
            isPointing[0] = true;
        }
    }

    public Image getTarget() {
        return getTarget(0);
    }
}