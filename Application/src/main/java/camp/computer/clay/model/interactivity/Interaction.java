package camp.computer.clay.model.interactivity;

import android.os.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.visualization.util.Point;

/**
 * An interaction is a temporal sequence of one or more impressions.
 */
public class Interaction {

    // TODO: Model this with a "touchPositions interaction envelope" or "interaction envelope".
    // TODO: Model voice interaction in the same way. Generify to Interaction<T> or subclass.
    // TODO: (?) Model data transmissions as impressions in the same way?

    private List<Impression> impressions = new LinkedList<>();

    // TODO: Classify these! Every time an Impression is added!
    // TODO: (cont'd) Note can have multiple sequences per finger in an interaction,
    // TODO: (cont'd) so consider remodeling as per-finger interaction and treat each finger
    // TODO: (cont'd) as an individual actor.
    public boolean[] isHolding = new boolean[Impression.MAXIMUM_TOUCH_POINT_COUNT];
    public boolean[] isDragging = new boolean[Impression.MAXIMUM_TOUCH_POINT_COUNT];
    public double[] dragDistance = new double[Impression.MAXIMUM_TOUCH_POINT_COUNT];
    public double offsetX = 0;
    public double offsetY = 0;

    public Handler timerHandler = new Handler();
    Interaction interaction = this;
    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            // Do what you need to do.
            // e.g., foobar();
            int pointerId = 0;
            if (getFirst().isTouching[pointerId])
                if (dragDistance[pointerId] < Impression.MINIMUM_DRAG_DISTANCE) {
                    getFirst().getBody().onHoldListener(interaction);
                }

            // Uncomment this for periodic callback
            // timerHandler.postDelayed(this, 100);
        }
    };

    public Interaction() {
        setup();
    }

    private void setup() {
        for (int i = 0; i < Impression.MAXIMUM_TOUCH_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
        }
    }

    public int getSize() {
        return this.impressions.size();
    }

    public void add(Impression impression) {
        this.impressions.add(impression);

        offsetX += impression.getPosition().getX();
        offsetY += impression.getPosition().getY();

        if (impressions.size() == 1) {
            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, Impression.MINIMUM_HOLD_DURATION);
        }
    }

    public Impression get(int index) {
        return this.impressions.get(index);
    }

    public Impression getFirst() {
        if (impressions.size() > 0) {
            return impressions.get(0);
        } else {
            return null;
        }
    }

    public Impression getLast() {
        if (impressions.size() > 0) {
            return impressions.get(impressions.size() - 1);
        } else {
            return null;
        }
    }

    public Impression getPrevious(Impression impression) {
        for (int i = 0; i < impressions.size() - 1; i++) {
            if (impressions.get(i + 1) == impression) {
                return impressions.get(i);
            }
        }
        return null;
    }

    public Impression getPrevious() {
//        for (int i = 0; i < impressions.size() - 1; i++) {
        if (impressions.size() > 1) {
//            if (impressions.get(i + 1) == getLast()) {
            return impressions.get(impressions.size() - 1);
//            }
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

    public ArrayList<Point> getTouchPath() {
        ArrayList<Point> touchPositions = new ArrayList<>();
        for (int i = 0; i < impressions.size(); i++) {
            touchPositions.add(impressions.get(i).getPosition());
        }
        return touchPositions;
    }

    public boolean isDragging() {
        return isDragging[getLast().pointerIndex];
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>
}
