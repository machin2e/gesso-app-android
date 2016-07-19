package camp.computer.clay.model.interaction;

import android.os.Handler;

import java.util.ArrayList;

import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Point;

/**
 * An interactivity is a temporal sequence of one or more interactions.
 */
public class TouchInteractivity {

    // TODO: Model this with a "touchPositions interaction envelope" or "interaction envelope".
    // TODO: Model voice interaction in the same way. Generify to Interactivity<T> or subclass.
    // TODO: (?) Model data transmissions as interactions in the same way?

    private ArrayList<TouchInteraction> interactions = new ArrayList<>();

    // TODO: Classify these! Every time an Interaction is added!
    // TODO: (cont'd) Note can have multiple sequences per finger in an interactivity,
    // TODO: (cont'd) so consider remodeling as per-finger interactivity and treat each finger
    // TODO: (cont'd) as an individual actor.
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

    public boolean isTouchingImage () {
        return isTouchingImage(0);
    }

    public void setTouchedImage (Image image) {
        setTouchedImage(0, image);
    }

    public Image getTouchedImage () {
        return getTouchedImage(0);
    }

    public Handler timerHandler = new Handler();
    TouchInteractivity touchInteractivity = this;
    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            // Do what you need to do.
            // e.g., foobar();
            int pointerId = 0;
            if (getFirst().isTouching[pointerId])
                if (dragDistance[pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
                    getFirst().getBody().onHoldListener(touchInteractivity, getFirst());
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

    public int getSize() {
        return this.interactions.size();
    }

    public void add(TouchInteraction touchInteraction) {
        this.interactions.add(touchInteraction);

        if (interactions.size() == 1) {

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, TouchInteraction.MINIMUM_HOLD_DURATION);
        }
    }

    public TouchInteraction get(int index) {
        return this.interactions.get(index);
    }

    public TouchInteraction getFirst() {
        if (interactions.size() > 0) {
            return interactions.get(0);
        } else {
            return null;
        }
    }

    public TouchInteraction getLatest() {
        if (interactions.size() > 0) {
            return interactions.get(interactions.size() - 1);
        } else {
            return null;
        }
    }

    public TouchInteraction getPrevious(TouchInteraction touchInteraction) {
        for (int i = 0; i < interactions.size() - 1; i++) {
            if (interactions.get(i + 1) == touchInteraction) {
                return interactions.get(i);
            }
        }
        return null;
    }

    public TouchInteraction getPrevious() {
        for (int i = 0; i < interactions.size() - 1; i++) {
            if (interactions.get(i + 1) == getLatest()) {
                return interactions.get(i);
            }
        }
        return null;
    }

    public long getStartTime() {
        return getFirst().getTimestamp();
    }

    public long getStopTime() {
        return getLatest().getTimestamp();
    }

    public long getDuration() {
        return getLatest().getTimestamp() - getFirst().getTimestamp();
    }

    public ArrayList<Point> getTouchPath() {
        ArrayList<Point> touchPositions = new ArrayList<>();
        for (int i = 0; i < interactions.size(); i++) {
            touchPositions.add(interactions.get(i).getPosition());
        }
        return touchPositions;
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>
}
