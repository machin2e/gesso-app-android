package camp.computer.clay.engine.manager;

import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.Geometry;
import camp.computer.clay.util.time.Clock;

public class Event {

    // TODO: Calculate velocity of current event relative to previous.
    // TODO: Calculate acceleration of current event relative to previous.

    public enum Type {
        NONE,
        SELECT,
        HOLD,
        MOVE,
        UNSELECT
    }

    public enum Commands {

        // Host

        // Extension

        // Portable

        // Path
        PATH_CREATE,
        PATH_MOVE_SOURCE_PORT,
        PATH_MOVE_TARGET_PORT,
        PATH_REMOVE_SOURCE_PORT,
        PATH_REMOVE_TARGET_PORT,
        PATH_REMOVE,
        PATH_CHANGE_TYPE,
        PATH_CHANGE_DIRECTION,

        // Camera
        CAMERA_SET_POSITION,
        CAMERA_SET_ZOOM,
        CAMERA_SET_FOCUS
    }

    // Clay: Entities can register events it will publish to EventManager. Others can subscribe to published messages.
    // e.g.,
    /*
    ? CREATE_PORT
    CHANGE_PORT_TYPE
    CHANGE_PORT_DIRECTION
    ? REMOVE_PORT

    CREATE_PATH
    MOVE_PATH_SOURCE_PORT -> MOVE_PORT
    MOVE_PATH_TARGET_PORT -> MOVE_PORT
    REMOVE_PATH_SOURCE_PORT -> REMOVE_PORT
    REMOVE_PATH_TARGET_PORT -> REMOVE_PORT
    REMOVE_PATH
    CHANGE_PATH_TYPE
    CHANGE_PATH_DIRECTION
    subscribe(Event.Type.CREATE_PATH, new EventProcess() {
        public void processEvent(Event event, EventData data) {
        }
    });
    */

    public static int MAXIMUM_POINT_COUNT = 5;

    public static int MAXIMUM_TAP_DURATION = 200;

    public static int MINIMUM_HOLD_DURATION = 600;

    public static int MINIMUM_MOVE_DISTANCE = 35;

    final public static long DEFAULT_TIMESTAMP = 0L;

    private boolean[] isHolding = new boolean[Event.MAXIMUM_POINT_COUNT];
    private boolean[] isDragging = new boolean[Event.MAXIMUM_POINT_COUNT];
    private double[] dragDistance = new double[Event.MAXIMUM_POINT_COUNT];

    public Transform[] surfaceCoordinates = new Transform[MAXIMUM_POINT_COUNT];

    public Transform[] pointerCoordinates = new Transform[MAXIMUM_POINT_COUNT];

    // TODO: Delete this!
    public boolean[] isPointing = new boolean[MAXIMUM_POINT_COUNT];

    private Entity[] targets = new Entity[MAXIMUM_POINT_COUNT];
    private Entity[] secondaryTarget = new Entity[MAXIMUM_POINT_COUNT];

//    private Entity[] backgroundTargets = new Entity[MAXIMUM_POINT_COUNT];

    private Type type = null;

    private long timestamp = DEFAULT_TIMESTAMP;

    public int pointerIndex = -1;

    public Event() {
        this.timestamp = Clock.getCurrentTime(); // TODO: Get from the World clock!
        setup();
    }

    private void setup() {
        for (int i = 0; i < MAXIMUM_POINT_COUNT; i++) {
            surfaceCoordinates[i] = new Transform(0, 0);
            pointerCoordinates[i] = new Transform(0, 0);
            isPointing[i] = false;

            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
        }
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

    public Transform getSurfacePosition() {
        return this.surfaceCoordinates[0];
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

    public Entity getSecondaryTarget() {
        return this.secondaryTarget[0];
    }

    public void setSecondaryTarget(Entity entity) {
        this.secondaryTarget[0] = entity;
    }

    // <INTEGRATE_WITH_ACTION>
    private Event previousEvent = null;

    public void setPreviousEvent(Event previousEvent) {
        this.previousEvent = previousEvent;

//        this.offset.set(
//                getPosition().x - getFirstEvent().getPosition().x,
//                getPosition().y - getFirstEvent().getPosition().y
//        );

        if (previousEvent != null) {
//            this.xOffset = previousEvent.xOffset + (getPosition().x - previousEvent.getPosition().x);
//            this.yOffset = previousEvent.yOffset + (getPosition().y - previousEvent.getPosition().x);
            this.xOffset = (getPosition().x - previousEvent.getPosition().x);
            this.yOffset = (getPosition().y - previousEvent.getPosition().y);
        } else {
            this.xOffset = 0;
            this.yOffset = 0;
        }
    }

    public Event getPreviousEvent() {
        return previousEvent;
    }

    //    private Transform offset = new Transform();
    public double xOffset = 0;
    public double yOffset = 0;

    public Event getFirstEvent() {
        Event firstEvent = this;
        while (firstEvent.previousEvent != null) {
            firstEvent = firstEvent.previousEvent;
        }
        return firstEvent;
    }

    public int getEventCount() {
        int count = 1;
        Event firstEvent = this;
        while (firstEvent.previousEvent != null) {
            count++;
            firstEvent = firstEvent.previousEvent;
        }
        return count;
    }

    public long getDuration() {
        return getTimestamp() - getFirstEvent().getTimestamp();
    }

    public boolean isHolding() {
        return isHolding[0];
    }

    public boolean isDragging() {
        return isDragging[pointerIndex];
    }

    public boolean isTap() {
        return getDuration() < Event.MAXIMUM_TAP_DURATION;
    }

    public double getDragDistance() {
        return dragDistance[pointerIndex];
    }

    public double getDistance() {
        Event firstEvent = getFirstEvent();
        Event lastEvent = this;
        double distance = Geometry.distance(
                firstEvent.getPosition(),
                lastEvent.getPosition()
        );
        return distance;
    }

    public Transform getOffset() {
//        this.offset.set(
//                getPosition().x - getFirstEvent().getPosition().x,
//                getPosition().y - getFirstEvent().getPosition().y
//        );
        return new Transform(xOffset, yOffset);
    }
    // </INTEGRATE_WITH_ACTION>
}