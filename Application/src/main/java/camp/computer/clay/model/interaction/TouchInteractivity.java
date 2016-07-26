package camp.computer.clay.model.interaction;

import android.os.Handler;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Point;

/**
 * An interactivity is a temporal sequence of one or more touchInteractions.
 */
public class TouchInteractivity {

    // TODO: Model this with a "touchPositions interaction envelope" or "interaction envelope".
    // TODO: Model voice interaction in the same way. Generify to Interactivity<T> or subclass.
    // TODO: (?) Model data transmissions as touchInteractions in the same way?

    private List<TouchInteraction> touchInteractions = new LinkedList<>();

    private double[] dragDistance = new double[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];

    public Handler timerHandler = new Handler();

    TouchInteractivity touchInteractivity = this;

    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            int pointerId = 0;
            if (getFirst().isTouching[pointerId]) {
                if (dragDistance[pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
                    getFirst().getBody().onHoldListener(touchInteractivity, getFirst());
                }
            }
        }
    };

    public TouchInteractivity() {
        setup();
    }

    private void setup() {
        for (int i = 0; i < TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT; i++) {
            dragDistance[i] = 0;
        }
    }

    public int getSize() {
        return this.touchInteractions.size();
    }

    public void add(TouchInteraction touchInteraction) {

        if (touchInteractions.size() > 1) {
            dragDistance[touchInteraction.pointerIndex] += Geometry.calculateDistance(touchInteraction.getPosition(), getLast().getPosition());
//            Log.v("Touch", "dragDistance: " + dragDistance[touchInteraction.pointerIndex]);
        }

        this.touchInteractions.add(touchInteraction);

//        offsetX += touchInteraction.getPosition().getX();
//        offsetY += touchInteraction.getPosition().getY();

        if (touchInteractions.size() == 1) {
            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, TouchInteraction.MINIMUM_HOLD_DURATION);
        }
    }

    public TouchInteraction get(int index) {
        return this.touchInteractions.get(index);
    }

    public TouchInteraction getFirst() {
        if (touchInteractions.size() > 0) {
            return touchInteractions.get(0);
        } else {
            return null;
        }
    }

    public TouchInteraction getLast() {
        if (touchInteractions.size() > 0) {
            return touchInteractions.get(touchInteractions.size() - 1);
        } else {
            return null;
        }
    }

    public TouchInteraction getPrevious(TouchInteraction touchInteraction) {
        for (int i = 0; i < touchInteractions.size() - 1; i++) {
            if (touchInteractions.get(i + 1) == touchInteraction) {
                return touchInteractions.get(i);
            }
        }
        return null;
    }

    public TouchInteraction getPrevious() {
        if (touchInteractions.size() > 1) {
            return touchInteractions.get(touchInteractions.size() - 1);
        }
        return null;
    }

    public long getStartTime() {
        return getFirst().getTimestamp();
    }

    public long getStopTime() {
        return getLast().getTimestamp();
    }

    public long getDuration() {
        return getLast().getTimestamp() - getFirst().getTimestamp();
    }

    public List<Point> getTouchPath() {
        List<Point> touchPositions = new LinkedList<>();
        for (int i = 0; i < touchInteractions.size(); i++) {
            touchPositions.add(touchInteractions.get(i).getPosition());
        }
        return touchPositions;
    }

    public boolean isHolding(int pointerIndex) {
        for (TouchInteraction touchInteraction : touchInteractions) {
            //if (touchInteractions.get(pointerIndex).getType() == OnTouchActionListener.Type.HOLD) {
            if (touchInteraction.getType() == OnTouchActionListener.Type.HOLD) {
                return true;
            }
        }
        return false;
    }

    public boolean isHolding() {
        return isHolding(0);
    }

    public boolean isDragging(int pointerIndex) {
        if (dragDistance[pointerIndex] > TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            return true;
        }
        return false;
    }

    public boolean isDragging() {
        return isDragging(0);
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>
}
