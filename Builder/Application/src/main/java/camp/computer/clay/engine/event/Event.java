package camp.computer.clay.engine.event;

import camp.computer.clay.engine.Clock;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.Geometry;

public class Event {

    // TODO: Calculate velocity of current event relative to previous.
    // TODO: Calculate acceleration of current event relative to previous.

//    public enum Type {
//        SELECT,
//        HOLD,
//        MOVE,
//        UNSELECT
//
//        /*
//        // Raw input to InputSystem
//        KEY_A,
//        KEY_B,
//        ...
//        KEY_LEFT,
//        KEY_UP,
//        KEY_RIGHT,
//        KEY_DOWN,
//        ...
//        */
//
//        /*
//        // Intents/Commands generated from Events
//        CAMERA_MOVE,
//        CAMERA_FOCUS,
//
//        HOST_SHOW_PORT_CONTROLS, // Host.State = EDIT
//        HOST_HIDE_PORT_CONTROLS,
//        HOST_SHOW_PATHS,
//        HOST_HIDE_PATHS,
//
//        PATH_CREATE, // PORT_CREATE_PATH
//
//        PATH_CHANGE_MODE,
//        PATH_SWAP_DIRECTION,
//        PATH_MOVE_SOURCE,
//        PATH_MOVE_TARGET,
//
//        WORLD_SHOW_OVERVIEW
//        */
//    }

    /*
    // <RECOGNIZER>
    class Sequence {

    }

    class Single extends Sequence {
        private List<Event.Type> eventManager;

        public Single(Event.Type... eventManager) {
            this.eventManager = new ArrayList<>();
            for (int i = 0; i < eventManager.length; i++) {
                this.eventManager.add(eventManager[i]);
            }
        }
    }

    class Repeat extends Sequence {
        private List<Event.Type> eventManager;

        // Repeat() means repeat anything...
        public Repeat(Event.Type... eventManager) {
            this.eventManager = new ArrayList<>();
            for (int i = 0; i < eventManager.length; i++) {
                this.eventManager.add(eventManager[i]);
            }
        }
    }

    public void matchSequence(Sequence... sequences) {
        // e.g., Single(EVENT_TOUCH), Repeat(EVENT_MOVE), Single(EVENT_UNTOUCH)

        matchSequence(
                new Single(Type.SELECT),
                new Repeat(Type.MOVE),
                new Single(Type.UNSELECT)
        );

        matchSequence(
                new Single(Type.SELECT),
                new Repeat(Type.MOVE),
                new Single(Type.HOLD)
        );

        // TODO: Double-click!
        matchSequence(
                new Single(Type.SELECT),
                new Repeat(Type.MOVE),
                new Single(Type.HOLD),
                new Single(Type.SELECT)
        );
    }

    // TODO: States for editable entityManager (states: VIEW, EDIT)
    static class RecognizerStateMachine {

        enum State {
            INVALID,
        }

        public void update() {

        }
    }
    // </RECOGNIZER>
    */

    // <CONTEXT>
    class Context {
        // TODO: Allow attachment of data in Context (e.g., dt, target parameter, type/mode/direction parameter)
    }

    public long dt;
    // </CONTEXT>

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

    private long timestamp = DEFAULT_TIMESTAMP;

    public int pointerIndex = -1;

    private long eventTypeUid;

    // TODO: eventTransactionUid

    public Event(String eventType) {
        this.eventTypeUid = World.getInstance().eventManager.getEventUid(eventType);
        this.timestamp = Clock.getTime(Clock.Unit.MILLISECONDS); // TODO: Get from the World clock!
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

    /**
     * Returns the {@code Event}'s type UID.
     */
    public long getType() {
        return this.eventTypeUid;
    }

    public void setType(String eventType) {
        // <REFACTOR>
        this.eventTypeUid = World.getInstance().eventManager.getEventUid(eventType);
        // </REFACTOR>
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

        if (previousEvent != null) {
            this.xOffset = (getPosition().x - previousEvent.getPosition().x);
            this.yOffset = (getPosition().y - previousEvent.getPosition().y);
        } else {
            this.xOffset = 0;
            this.yOffset = 0;
        }
    }

//    // TODO: DELETE
//    public Event getPreviousEvent() {
//        return previousEvent;
//    }

    //    private Transform offset = new Transform();
    public double xOffset = 0;
    public double yOffset = 0;

    // TODO: DELETE
    public Event getFirstEvent() {
        Event firstEvent = this;
        while (firstEvent.previousEvent != null) {
            firstEvent = firstEvent.previousEvent;
        }
        return firstEvent;
    }

//    // TODO: DELETE
//    public int getEventCount() {
//        int entityCounter = 1;
//        Event firstEvent = this;
//        while (firstEvent.previousEvent != null) {
//            entityCounter++;
//            firstEvent = firstEvent.previousEvent;
//        }
//        return entityCounter;
//    }

    // TODO: DELETE
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

    // TODO: DELETE
    public double getDistance() {
        Event firstEvent = getFirstEvent();
        Event lastEvent = this;
        double distance = Geometry.distance(
                firstEvent.getPosition(),
                lastEvent.getPosition()
        );
        return distance;
    }

//    public Transform getOffset() {
////        this.offset.set(
////                getPosition().x - getFirstEvent().getPosition().x,
////                getPosition().y - getFirstEvent().getPosition().y
////        );
//        return new Transform(xOffset, yOffset);
//    }
    // </INTEGRATE_WITH_ACTION>
}