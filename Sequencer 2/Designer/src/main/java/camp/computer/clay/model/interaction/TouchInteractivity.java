package camp.computer.clay.model.interaction;

import android.graphics.PointF;
import android.os.Handler;

import java.util.ArrayList;

import camp.computer.clay.visualization.arch.Image;

/**
 * An interactivity is a temporal sequence of one or more interactions.
 */
public class TouchInteractivity {

    // TODO: Model this with a "touchPositions interaction envelope" or "interaction envelope".

    private ArrayList<TouchInteraction> touchInteractions = new ArrayList<>();

    // TODO: Classify these! Every time an Interaction is added!
    private Image[] touchedImage = new Image[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public boolean[] isHolding = new boolean[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public boolean[] isDragging = new boolean[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public double[] dragDistance = new double[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];

    public boolean isTouchingImage (int fingerIndex) {
        return this.touchedImage[fingerIndex] != null;
    }

    public void setTouchedImage (int fingerIndex, Image image) {
        this.touchedImage[fingerIndex] = image;
    }

    public Image getTouchedImage (int fingerIndex) {
        return this.touchedImage[fingerIndex];
    }

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
        setup();
    }

    private void setup() {
        for (int i = 0; i < TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
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

    public long getStartTime() {
        return getFirstInteraction().getTimestamp();
    }

    public long getStopTime() {
        return getLatestInteraction().getTimestamp();
    }

    public long getDuration() {
        return getLatestInteraction().getTimestamp() - getFirstInteraction().getTimestamp();
    }

    public ArrayList<PointF> getTouchPath() {
        ArrayList<PointF> touchPositions = new ArrayList<>();
        for (int i = 0; i < touchInteractions.size(); i++) {
            touchPositions.add(touchInteractions.get(i).getPosition());
        }
        return touchPositions;
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>
}
