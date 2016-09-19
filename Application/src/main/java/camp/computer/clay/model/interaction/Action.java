package camp.computer.clay.model.interaction;

import android.os.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Feature;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;

/**
 * An thisAction is a temporal sequence of one or more events.
 */
public class Action { // TODO: Rename Activity. Previously Gesture.

    // TODO: Feature this with a "pointerCoordinates thisAction envelope" or "thisAction envelope".
    // TODO: Feature voice thisAction in the same way. Generify to Action<T> or subclass.
    // TODO: (?) Feature data transmissions as events in the same way?

    private List<Event> events = new LinkedList<>();

    // TODO: Classify these! Every time an Event is added!
    // TODO: (cont'd) Note can have multiple sequences per finger in an thisAction,
    // TODO: (cont'd) so consider remodeling as per-finger thisAction and treat each finger
    // TODO: (cont'd) as an individual actor.
    private boolean[] isHolding = new boolean[Event.MAXIMUM_POINT_COUNT];
    private boolean[] isDragging = new boolean[Event.MAXIMUM_POINT_COUNT];
    private double[] dragDistance = new double[Event.MAXIMUM_POINT_COUNT];

    public double offsetX = 0;
    public double offsetY = 0;

    public Handler timerHandler = new Handler();

    Action thisAction = this;

    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            int pointerIndex = 0;

            if (getFirstEvent().isPointing[pointerIndex]) {
                if (getDragDistance() < Event.MINIMUM_DRAG_DISTANCE) {

                    // <HACK>
                    // TODO: Make this less ugly! It's so ugly.
                    thisAction.getFirstEvent().setType(Event.Type.HOLD);
//                    getFirstEvent().getActor().getCamera().getScene().onHoldListener(thisAction.getFirstEvent());

                    Event event = thisAction.getFirstEvent();
                    Image targetImage = getFirstEvent().getActor().getScene().getImageByCoordinate(event.getCoordinate());
                    event.setTargetImage(targetImage);

                    event.getTargetImage().processAction(thisAction);
                    // </HACK>

                    thisAction.isHolding[pointerIndex] = true;

                }
            }
        }
    };

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

    public void addEvent(Event event) {

        event.setAction(this);

        events.add(event);

        offsetX += event.getCoordinate().getX();
        offsetY += event.getCoordinate().getY();

        if (events.size() == 1) {

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, Event.MINIMUM_HOLD_DURATION);

        } else if (events.size() > 1) {

            // Calculate drag distance
            this.dragDistance[event.pointerIndex] = Geometry.calculateDistance(event.getCoordinate(), getFirstEvent().pointerCoordinates[event.pointerIndex]);

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

    // TODO: Remove this? Or make it complement the updated getTargetImage() which returns the Feature that the process targeted.
    public Feature getSourceFeature() { // getOrigin
        if (events.size() > 0) {
            if (getEvent(0).getTargetShape() != null && getEvent(0).getTargetShape().getFeature() != null) {
                return getEvent(0).getTargetShape().getFeature();
            } else {
                return getEvent(0).getTargetImage().getFeature();
            }
        }
        return null;
    }

    public Feature getTargetFeature() { // getTargetFeature
//        Event event = getLastEvent();
//        if (event != null) {
//            return event.getTargetImage().getFeature();
//        }
//        return null;
        if (events.size() > 0) {
            Event lastEvent = getLastEvent();
            if (lastEvent.getTargetShape() != null && lastEvent.getTargetShape().getFeature() != null) {
                return lastEvent.getTargetShape().getFeature();
            } else {
                return lastEvent.getTargetImage().getFeature();
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

    public ArrayList<Point> getTouchPath() {
        ArrayList<Point> touchCoordinates = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            touchCoordinates.add(events.get(i).getCoordinate());
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
     * @return Point-to-point distance between the getFirstEvent and getLastEvent events' positions.
     */
    public double getDistance() {
        Event firstEvent = getFirstEvent();
        Event lastEvent = getLastEvent();
        double distance = Geometry.calculateDistance(
                firstEvent.getCoordinate(),
                lastEvent.getCoordinate()
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

//    // in handlers (e.g., in Images), use this to check for match, if match, then use/get/set on the action's events to get inputs for the routine operation
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
