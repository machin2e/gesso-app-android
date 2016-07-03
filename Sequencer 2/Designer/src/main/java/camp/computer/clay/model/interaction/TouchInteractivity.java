package camp.computer.clay.model.interaction;

import android.os.Handler;

import java.util.ArrayList;

import camp.computer.clay.sprite.Sprite;

// An interactivity is a temporal sequence of one or more interactions.
//
// Model this with a "touch interaction envelope" or "interaction envelope".

public class TouchInteractivity {
    private ArrayList<TouchInteraction> touchInteractions = new ArrayList<TouchInteraction>();

    // TODO: Classify these! Every time an Interaction is added!
    public boolean[] isTouchingSprite = new boolean[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public Sprite[] touchedSprite = new Sprite[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
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
    }

    public ArrayList<TouchInteraction> getInteractions() {
        return this.touchInteractions;
    }

    public void addInteraction (TouchInteraction touchInteraction) {
        this.touchInteractions.add(touchInteraction);

        if (touchInteractions.size() == 1) {

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, TouchInteraction.MINIMUM_HOLD_DURATION);
        }
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

    public long getDuration() {
        return getLatestInteraction().getTimestamp() - getFirstInteraction().getTimestamp();
    }

    // TODO: getStartTime()
    // TODO: getStopTime()
    // TODO: getDuration()
    // TODO: getTouchPath()

    // <CLASSIFIER>

    // </CLASSIFIER>
}
