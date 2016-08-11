package camp.computer.clay.model.interactivity;

import android.os.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;

/**
 * An thisInteraction is a temporal sequence of one or more actions.
 */
public class Interaction {

    // TODO: Construct this with a "touchPoints thisInteraction envelope" or "thisInteraction envelope".
    // TODO: Construct voice thisInteraction in the same way. Generify to Interaction<T> or subclass.
    // TODO: (?) Construct data transmissions as actions in the same way?

    private List<Action> actions = new LinkedList<>();

    // TODO: Classify these! Every time an Action is added!
    // TODO: (cont'd) Note can have multiple sequences per finger in an thisInteraction,
    // TODO: (cont'd) so consider remodeling as per-finger thisInteraction and treat each finger
    // TODO: (cont'd) as an individual actor.
    private boolean[] isHolding = new boolean[Action.MAX_TOUCH_POINT_COUNT];
    private boolean[] isDragging = new boolean[Action.MAX_TOUCH_POINT_COUNT];
    private double[] dragDistance = new double[Action.MAX_TOUCH_POINT_COUNT];

    public double offsetX = 0;
    public double offsetY = 0;

    public Handler timerHandler = new Handler();

    Interaction thisInteraction = this;

    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            int pointerIndex = 0;

            if (getFirst().isTouching[pointerIndex]) {
                if (getDragDistance() < Action.MIN_DRAG_DISTANCE) {

                    // TODO: Make this less ugly! It's so ugly.
                    // getFirst().getBody().getPerspective().getVisualization().onHoldListener(thisInteraction.getFirst());

                    thisInteraction.isHolding[pointerIndex] = true;

                }
            }
        }
    };

    public Interaction() {
        setup();
    }

    private void setup() {
        for (int i = 0; i < Action.MAX_TOUCH_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
        }
    }

    public int getSize() {
        return this.actions.size();
    }

    public void add(Action action) {

        action.setInteraction(this);

        actions.add(action);

        offsetX += action.getPosition().getX();
        offsetY += action.getPosition().getY();

        if (actions.size() == 1) {

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, Action.MIN_HOLD_DURATION);

        } else if (actions.size() > 1) {

            // Calculate drag distance
            this.dragDistance[action.pointerIndex] = Geometry.calculateDistance(action.getPosition(), getFirst().touchPoints[action.pointerIndex]);

            if (getDragDistance() > Action.MIN_DRAG_DISTANCE) {
                isDragging[action.pointerIndex] = true;
            }

        }
    }

    public Action get(int index) {
        return this.actions.get(index);
    }

    public Action getFirst() {
        if (actions.size() > 0) {
            return actions.get(0);
        } else {
            return null;
        }
    }

    public Action getLast() {
        if (actions.size() > 0) {
            return actions.get(actions.size() - 1);
        } else {
            return null;
        }
    }

    public Action getPrevious(Action action) {
        for (int i = 0; i < actions.size() - 1; i++) {
            if (actions.get(i + 1) == action) {
                return actions.get(i);
            }
        }
        return null;
    }

    public Action getPrevious() {
//        for (int i = 0; i < actions.size() - 1; i++) {
        if (actions.size() > 1) {
//            if (actions.get(i + 1) == getLast()) {
            return actions.get(actions.size() - 1);
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
        for (int i = 0; i < actions.size(); i++) {
            touchPositions.add(actions.get(i).getPosition());
        }
        return touchPositions;
    }

    public boolean isHolding() {
        return isHolding[0];
    }

    public boolean isDragging() {
        return isDragging[getLast().pointerIndex];
    }

    public double getDragDistance() {
        return dragDistance[getLast().pointerIndex];
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>
}
