package camp.computer.clay.model.interactivity;

import android.os.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.visualization.util.Point;

/**
 * An interplay is a temporal sequence of one or more interactions.
 */
public class Interplay {

    // TODO: Model this with a "touchPositions interaction envelope" or "interaction envelope".
    // TODO: Model voice interaction in the same way. Generify to Interplay<T> or subclass.
    // TODO: (?) Model data transmissions as interactions in the same way?

    private List<Interaction> interactions = new LinkedList<>();

    // TODO: Classify these! Every time an Interaction is added!
    // TODO: (cont'd) Note can have multiple sequences per finger in an interplay,
    // TODO: (cont'd) so consider remodeling as per-finger interplay and treat each finger
    // TODO: (cont'd) as an individual actor.
    public boolean[] isHolding = new boolean[Interaction.MAXIMUM_TOUCH_POINT_COUNT];
    public boolean[] isDragging = new boolean[Interaction.MAXIMUM_TOUCH_POINT_COUNT];
    public double[] dragDistance = new double[Interaction.MAXIMUM_TOUCH_POINT_COUNT];
    public double offsetX = 0;
    public double offsetY = 0;

    public Handler timerHandler = new Handler();
    Interplay interplay = this;
    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            // Do what you need to do.
            // e.g., foobar();
            int pointerId = 0;
            if (getFirst().isTouching[pointerId])
                if (dragDistance[pointerId] < Interaction.MINIMUM_DRAG_DISTANCE) {
                    getFirst().getBody().onHoldListener(interplay, getFirst());
                }

            // Uncomment this for periodic callback
            // timerHandler.postDelayed(this, 100);
        }
    };

    public Interplay() {
        setup();
    }

    private void setup() {
        for (int i = 0; i < Interaction.MAXIMUM_TOUCH_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
        }
    }

    public int getSize() {
        return this.interactions.size();
    }

    public void add(Interaction interaction) {
        this.interactions.add(interaction);

        offsetX += interaction.getPosition().getX();
        offsetY += interaction.getPosition().getY();

        if (interactions.size() == 1) {
            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, Interaction.MINIMUM_HOLD_DURATION);
        }
    }

    public Interaction get(int index) {
        return this.interactions.get(index);
    }

    public Interaction getFirst() {
        if (interactions.size() > 0) {
            return interactions.get(0);
        } else {
            return null;
        }
    }

    public Interaction getLatest() {
        if (interactions.size() > 0) {
            return interactions.get(interactions.size() - 1);
        } else {
            return null;
        }
    }

    public Interaction getPrevious(Interaction interaction) {
        for (int i = 0; i < interactions.size() - 1; i++) {
            if (interactions.get(i + 1) == interaction) {
                return interactions.get(i);
            }
        }
        return null;
    }

    public Interaction getPrevious() {
//        for (int i = 0; i < interactions.size() - 1; i++) {
        if (interactions.size() > 1) {
//            if (interactions.get(i + 1) == getLatest()) {
                return interactions.get(interactions.size() - 1);
//            }
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

    public boolean isDragging() {
        return isDragging[getLatest().pointerIndex];
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>
}
