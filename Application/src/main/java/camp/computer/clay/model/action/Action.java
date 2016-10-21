package camp.computer.clay.model.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.engine.entity.Actor;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.engine.component.Transform;

/**
 * An Action is a sequence of one or more events.
 */
public class Action {

    private Actor actor = null;

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return this.actor;
    }

    private List<Event> events = new LinkedList<>();

    // "Event Future"
//    private List<Event> eventQueue = new LinkedList<>();

    // TODO: Classify these! Every time an Event is added!
    // TODO: (cont'd) Note can have multiple sequences per finger in an thisAction,
    // TODO: (cont'd) so consider remodeling as per-finger thisAction and treat each finger
    // TODO: (cont'd) as an individual actor.
    private boolean[] isHolding = new boolean[Event.MAXIMUM_POINT_COUNT];
    private boolean[] isDragging = new boolean[Event.MAXIMUM_POINT_COUNT];
    private double[] dragDistance = new double[Event.MAXIMUM_POINT_COUNT];
    // TODO: private double[] touchPressure = new double[Event.MAXIMUM_POINT_COUNT]; // Reference: http://stackoverflow.com/questions/17540058/android-detect-touch-pressure-on-capacitive-touch-screen

//    public Handler timerHandler = new Handler();
//
//    public Runnable timerRunnable = new Runnable() {
//        @Override
//        public void run() {
//
//            int pointerIndex = 0;
//
//            if (getFirstEvent().isPointing[pointerIndex]) {
//                if (getDragDistance() < Event.MINIMUM_DRAG_DISTANCE) {
//
//                    Event event = new Event();
//                    event.setType(Event.Type.HOLD);
//                    event.pointerIndex = getFirstEvent().pointerIndex;
//                    event.pointerCoordinates[0] = new Transform(getFirstEvent().getPosition()); // HACK. This should contain the state of ALL pointers (just set the previous event's since this is a synthetic event?)
//                    getFirstEvent().getActor().queueEvent(event);
//
//                    isHolding[pointerIndex] = true;
//
//                }
//            }
//        }
//    };

    public Action() {
        setup();
    }

    private void setup() {
        for (int i = 0; i < Event.MAXIMUM_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
        }
    }

//    // TODO: getPreviousAction()
//    Action previousAction = null;
//    if (.actions.size() > 1) {
//        previousAction = actor.actions.get(actor.actions.size() - 2);
//        Log.v("PreviousTouch", "Previous: " + previousAction.getFirstEvent().getTargetImage());
//        Log.v("PreviousTouch", "Current: " + event.getTargetImage());
//    }

    public Action getPrevious() {
        if (actor != null) {
            if (actor.getActions().size() > 1) {
                int previousActionIndex = actor.getActions().size() - 2;
                return actor.getActions().get(previousActionIndex);
            }
        }
        return null;
    }

    public void addEvent(Event event) {

        event.setAction(this);

        if (events.size() == 0) {

            events.add(event);

            // Start timer to check for hold
//            timerHandler.removeCallbacks(timerRunnable);
//            timerHandler.postDelayed(timerRunnable, Event.MINIMUM_HOLD_DURATION);

        } else if (events.size() > 0) {

            events.add(event);

            // Calculate drag distance
            this.dragDistance[event.pointerIndex] = Geometry.distance(event.getPosition(), getFirstEvent().pointerCoordinates[event.pointerIndex]);

            if (getDragDistance() > Event.MINIMUM_DRAG_DISTANCE) {
                isDragging[event.pointerIndex] = true;
            }

        }
    }

    public Event getEvent(int index) {
        return this.events.get(index);
    }

    public Event getFirstEvent() {
        if (events.size() > 0) {
            return events.get(0);
        } else {
            return null;
        }
    }

    public Event getLastEvent() {
        if (events.size() > 0) {
            return events.get(events.size() - 1);
        } else {
            return null;
        }
    }

    // TODO: Remove this? Or make it complement the updated getTargetImage() which returns the Entity that the process targeted.
    public Entity getSourceEntity() {
        if (events.size() > 0) {
            if (getEvent(0).getTargetShape() != null && getEvent(0).getTargetShape().getEntity() != null) {
                return getEvent(0).getTargetShape().getEntity();
            } else {
                return getEvent(0).getTargetImage().getEntity();
            }
        }
        return null;
    }

    public Entity getTargetEntity() { // getTargetEntity
//        Event event = getLastEvent();
//        if (event != null) {
//            return event.getTargetImage().getEntity();
//        }
//        return null;
        if (events.size() > 0) {
            Event lastEvent = getLastEvent();
            if (lastEvent.getTargetShape() != null && lastEvent.getTargetShape().getEntity() != null) {
                return lastEvent.getTargetShape().getEntity();
            } else {
                return lastEvent.getTargetImage().getEntity();
            }
        }
        return null;
    }

    public long getStartTime() {
        return getFirstEvent().getTimestamp();
    }

    public long getStopTime() {
        return getLastEvent().getTimestamp();
    }

    public int getSize() {
        return this.events.size();
    }

    public long getDuration() {
        return getLastEvent().getTimestamp() - getFirstEvent().getTimestamp();
    }

    public ArrayList<Transform> getTouchPath() {
        ArrayList<Transform> touchCoordinates = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            touchCoordinates.add(events.get(i).getPosition());
        }
        return touchCoordinates;
    }

    public boolean isHolding() {
        return isHolding[0];
    }

    public boolean isDragging() {
        return isDragging[getLastEvent().pointerIndex];
    }

    public boolean isTap() {
        return getDuration() < Event.MAXIMUM_TAP_DURATION;
    }

    public double getDragDistance() {
        return dragDistance[getLastEvent().pointerIndex];
    }

    /**
     * Returns point-to-point distance between getFirstEvent and getLastEvent action positions.
     *
     * @return Transform-to-point distance between the getFirstEvent and getLastEvent events' positions.
     */
    public double getDistance() {
        Event firstEvent = getFirstEvent();
        Event lastEvent = getLastEvent();
        double distance = Geometry.distance(
                firstEvent.getPosition(),
                lastEvent.getPosition()
        );
        return distance;
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>

    public boolean startsWith(Event event) {
        return (getFirstEvent() == event);
    }

    public boolean stopsWith(Event event) {
        return (getLastEvent() == event);
    }

    protected Transform offset = new Transform();

    public Transform getOffset() {
        this.offset.set(
                getLastEvent().getPosition().x - getFirstEvent().getPosition().x,
                getLastEvent().getPosition().y - getFirstEvent().getPosition().y
        );
        return offset;
    }

//    // in handlers (e.g., in Images), use this to check for match, if match, then use/get/setValue on the action's events to get inputs for the routine operation
//    public boolean matches(Event... events) {
//        for (int i = 0, j = 0; i < this.events.size(); i++) {
//
//            Event event = this.events.get(i);
//            Event otherEvent = events[i];
//
//            if (event != otherEvent && otherEvent !=) {
//
//            }
//        }
//    }

    // TODO: public boolean matches(Action action) { return false; }
}
