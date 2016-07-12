package camp.computer.clay.model.interaction;

import android.os.Handler;

import java.util.ArrayList;

import camp.computer.clay.visualization.Image;

// An interactivity is a temporal sequence of one or more interactions.
//
// Model this with a "touchPositions interaction envelope" or "interaction envelope".

public class TouchInteractivity {
    private ArrayList<TouchInteraction> touchInteractions = new ArrayList<TouchInteraction>();

    // TODO: Classify these! Every time an Interaction is added!
    public boolean[] isTouchingImage = new boolean[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public Image[] touchedImage = new Image[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public boolean[] isHolding = new boolean[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public boolean[] isDragging = new boolean[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public double[] dragDistance = new double[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];

    public Handler timerHandler = new Handler();
    TouchInteractivity touchInteractivity = this;
    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            /* do what you need to do */
            //foobar();
            int pointerId = 0;
            if (getFirstInteraction().isTouching[pointerId])
                if (dragDistance[pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
                    getFirstInteraction().getBody().onHoldListener(touchInteractivity, getFirstInteraction());
                }

            // Uncomment this for periodic callback
            // timerHandler.postDelayed(this, 100);
        }
    };

    public TouchInteractivity() {
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
            isTouchingImage[i] = false;
            touchedImage[i] = null;
        }
    }

    public int getCardinality() {
        return this.touchInteractions.size();
    }

    public void addInteraction (TouchInteraction touchInteraction) {
        this.touchInteractions.add(touchInteraction);

        if (touchInteractions.size() == 1) {

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, TouchInteraction.MINIMUM_HOLD_DURATION);
        }
    }

    public TouchInteraction getInteraction(int index) {
        return this.touchInteractions.get(index);
    }

    public TouchInteraction getFirstInteraction() {
        if (touchInteractions.size() > 0) {
            return touchInteractions.get(0);
        } else {
            return null;
        }
    }

    public TouchInteraction getLatestInteraction() {
        if (touchInteractions.size() > 0) {
            return touchInteractions.get(touchInteractions.size() - 1);
        } else {
            return null;
        }
    }

    public TouchInteraction getPreviousInteraction(TouchInteraction touchInteraction) {
        for (int i = 0; i < touchInteractions.size() - 1; i++) {
            if (touchInteractions.get(i + 1) == touchInteraction) {
                return touchInteractions.get(i);
            }
        }
        return null;
    }

    public TouchInteraction getPreviousInteraction() {
        for (int i = 0; i < touchInteractions.size() - 1; i++) {
            if (touchInteractions.get(i + 1) == getLatestInteraction()) {
                return touchInteractions.get(i);
            }
        }
        return null;
    }

    public long getDuration() {
        return getLatestInteraction().getTimestamp() - getFirstInteraction().getTimestamp();
    }

    public long getStartTime() {
        return getFirstInteraction().getTimestamp();
    }

    public long getStopTime() {
        return getLatestInteraction().getTimestamp();
    }

    // TODO: getTouchPath()

    // <CLASSIFIER>

    // </CLASSIFIER>
}
