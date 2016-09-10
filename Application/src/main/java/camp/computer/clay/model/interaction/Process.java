package camp.computer.clay.model.interaction;

import android.os.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Construct;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;

/**
 * An thisProcess is a temporal sequence of one or more actions.
 */
public class Process { // TODO: Rename Activity. Previously Gesture.

    // TODO: Construct this with a "pointerCoordinates thisProcess envelope" or "thisProcess envelope".
    // TODO: Construct voice thisProcess in the same way. Generify to Process<T> or subclass.
    // TODO: (?) Construct data transmissions as actions in the same way?

    private List<Action> actions = new LinkedList<>();

    // TODO: Classify these! Every time an Action is added!
    // TODO: (cont'd) Note can have multiple sequences per finger in an thisProcess,
    // TODO: (cont'd) so consider remodeling as per-finger thisProcess and treat each finger
    // TODO: (cont'd) as an individual actor.
    private boolean[] isHolding = new boolean[Action.MAXIMUM_POINT_COUNT];
    private boolean[] isDragging = new boolean[Action.MAXIMUM_POINT_COUNT];
    private double[] dragDistance = new double[Action.MAXIMUM_POINT_COUNT];

    public double offsetX = 0;
    public double offsetY = 0;

    public Handler timerHandler = new Handler();

    Process thisProcess = this;

    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            int pointerIndex = 0;

            if (getStartAction().isPointing[pointerIndex]) {
                if (getDragDistance() < Action.MINIMUM_DRAG_DISTANCE) {

                    // <HACK>
                    // TODO: Make this less ugly! It's so ugly.
                    thisProcess.getStartAction().setType(Action.Type.HOLD);
//                    getStartAction().getActor().getCamera().getScene().onHoldListener(thisProcess.getStartAction());

                    Action action = thisProcess.getStartAction();
                    Image targetImage = getStartAction().getActor().getScene().getImageByCoordinate(action.getCoordinate());
                    action.setTargetImage(targetImage);

                    action.getTargetImage().processAction(action);
                    // </HACK>

                    thisProcess.isHolding[pointerIndex] = true;

                }
            }
        }
    };

    public Process() {
        setup();
    }

    private void setup() {
        for (int i = 0; i < Action.MAXIMUM_POINT_COUNT; i++) {
            isHolding[i] = false;
            isDragging[i] = false;
            dragDistance[i] = 0;
        }
    }

    public void add(Action action) {

        action.setProcess(this);

        actions.add(action);

        offsetX += action.getCoordinate().getX();
        offsetY += action.getCoordinate().getY();

        if (actions.size() == 1) {

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, Action.MINIMUM_HOLD_DURATION);

        } else if (actions.size() > 1) {

            // Calculate drag distance
            this.dragDistance[action.pointerIndex] = Geometry.calculateDistance(action.getCoordinate(), getStartAction().pointerCoordinates[action.pointerIndex]);

            if (getDragDistance() > Action.MINIMUM_DRAG_DISTANCE) {
                isDragging[action.pointerIndex] = true;
            }

        }
    }

    public Action getAction(int index) {
        return this.actions.get(index);
    }

    public Action getStartAction() {
        if (actions.size() > 0) {
            return actions.get(0);
        } else {
            return null;
        }
    }

    public Action getStopAction() {
        if (actions.size() > 0) {
            return actions.get(actions.size() - 1);
        } else {
            return null;
        }
    }

    // TODO: Remove this? Or make it complement the updated getTargetImage() which returns the Construct that the process targeted.
    public Construct getSource() { // getOrigin
        if (actions.size() > 0) {
            return getAction(0).getTargetImage().getConstruct();
        }
        return null;
    }

    public Construct getTarget() { // getTarget
        Action action = getStopAction();
        if (action != null) {
            return action.getTargetImage().getConstruct();
        }
        return null;
    }

    public long getStartTime() {
        return getStartAction().getTimestamp();
    }

    public long getStopTime() {
        return getStopAction().getTimestamp();
    }

    public int getSize() {
        return this.actions.size();
    }

    public long getDuration() {
        return getStopAction().getTimestamp() - getStartAction().getTimestamp();
    }

    public ArrayList<Point> getTouchPath() {
        ArrayList<Point> touchCoordinates = new ArrayList<>();
        for (int i = 0; i < actions.size(); i++) {
            touchCoordinates.add(actions.get(i).getCoordinate());
        }
        return touchCoordinates;
    }

    public boolean isHolding() {
        return isHolding[0];
    }

    public boolean isDragging() {
        return isDragging[getStopAction().pointerIndex];
    }

    public double getDragDistance() {
        return dragDistance[getStopAction().pointerIndex];
    }

    public boolean isTap() {
        return getDuration() < Action.MAXIMUM_TAP_DURATION;
    }

    /**
     * Returns point-to-point distance between getStartAction and getStopAction action positions.
     *
     * @return Point-to-point distance between the getStartAction and getStopAction actions' positions.
     */
    public double getDistance() {
        Action firstAction = getStartAction();
        Action lastAction = getStopAction();
        double distance = Geometry.calculateDistance(
                firstAction.getCoordinate(),
                lastAction.getCoordinate()
        );
        return distance;
    }

    // <CLASSIFIER>
    // TODO: Implement classifiers (inc. $1).
    // </CLASSIFIER>

    public boolean startsWith(Action action) {
        return false;
    }

    public boolean stopsWith(Action action) {
        return false;
    }
}
