package camp.computer.clay.model.action;

import android.util.Log;

import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.geometry.Geometry;
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
        UNSELECT;
    }

    public static int MAXIMUM_POINT_COUNT = 1;

    public static int MAXIMUM_TAP_DURATION = 200;

    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    private boolean[] isHolding = new boolean[Event.MAXIMUM_POINT_COUNT];
    private boolean[] isDragging = new boolean[Event.MAXIMUM_POINT_COUNT];
    private double[] dragDistance = new double[Event.MAXIMUM_POINT_COUNT];

    /**
     * The pointerCoordinates at which actions were performed (e.g., the touch pointerCoordinates on a touchscreen).
     */
    public Transform[] pointerCoordinates = new Transform[MAXIMUM_POINT_COUNT];

    // TODO: Delete this!
    public boolean[] isPointing = new boolean[MAXIMUM_POINT_COUNT];

    private Entity[] targets = new Entity[MAXIMUM_POINT_COUNT];

    private Type type = null;

    private long timestamp = DEFAULT_TIMESTAMP;

    public int pointerIndex = -1;

    public Event() {
        this.timestamp = Clock.getCurrentTime();
        setup();
        setupAction();
    }

    private void setup() {
        for (int i = 0; i < MAXIMUM_POINT_COUNT; i++) {
            pointerCoordinates[i] = new Transform(0, 0);
            isPointing[i] = false;
        }
    }

    private void setupAction() {
        for (int i = 0; i < Event.MAXIMUM_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
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

//    public boolean hasAction() {
//        return parentAction != null;
//    }
//
//    public void setAction(Action action) {
//        this.parentAction = action;
//    }
//
//    public Action getAction() {
//        return this.parentAction;
//    }

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
        return this.targets[pointerIndex] != null;
    }

    public boolean isPointing() { // was isTouching
        return isPointing(0);
    }

    public void setTarget(Entity entity) {
        this.targets[0] = entity;
    }

    public Entity getTarget() {
        return this.targets[0];
    }



    // <INTEGRATE_WITH_ACTION>
    public Event previousEvent = null;

    private Transform offset = new Transform();

    public Event getFirstEvent() {
        Event firstEvent = this;
        while (firstEvent.previousEvent != null) {
            firstEvent = firstEvent.previousEvent;
        }
        return firstEvent;
    }

    public long getDuration() {
//        return getLastEvent().getTimestamp() - getFirstEvent().getTimestamp();
        return getTimestamp() - getFirstEvent().getTimestamp();
    }

    public boolean isHolding() {
        return isHolding[0];
    }

    public boolean isDragging() {
//        return isDragging[getLastEvent().pointerIndex];
        return isDragging[pointerIndex];
    }

    public boolean isTap() {
        return getDuration() < Event.MAXIMUM_TAP_DURATION;
    }

    public double getDragDistance() {
//        return dragDistance[getLastEvent().pointerIndex];
        return dragDistance[pointerIndex];
    }

    public double getDistance() {
        Event firstEvent = getFirstEvent();
//        Event lastEvent = getLastEvent();
        Event lastEvent = this;
        double distance = Geometry.distance(
                firstEvent.getPosition(),
                lastEvent.getPosition()
        );
        return distance;
    }

    public Transform getOffset() {
        this.offset.set(
                getPosition().x - getFirstEvent().getPosition().x,
                getPosition().y - getFirstEvent().getPosition().y
        );
        return offset;
    }
    // </INTEGRATE_WITH_ACTION>
}