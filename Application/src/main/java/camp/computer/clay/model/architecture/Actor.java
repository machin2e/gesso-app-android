package camp.computer.clay.model.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.interaction.*;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.scene.architecture.Scene;

public class Actor { // Controller

    private Camera camera = null;

    // PatternSet (Smart querying interface)
    public List<ActionSequence> actionSequences = new LinkedList<>();

    public Actor() {
        setup();
    }

    private void setup() {
        // Camera
        Camera camera = new Camera();
        setCamera(camera);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public boolean hasView() {
        return camera != null;
    }

    public Camera getCamera() {
        return this.camera;
    }

    /**
     * Conveninece function.
     *
     * @return
     */
    public Scene getScene() {
        if (camera != null) {
            return camera.getScene();
        }
        return null;
    }

    /**
     * Returns the most recent interaction.
     *
     * @return The most recent interaction.
     */
    private ActionSequence getPattern() {
        if (actionSequences.size() > 0) {
            return actionSequences.get(actionSequences.size() - 1);
        } else {
            return null;
        }
    }

    public void onAction(Action action) {

        action.setActor(this);

        switch (action.getType()) {

            case TOUCH: {

                // Having an idea is just accumulating intention. It's a suggestion from your existential
                // controller.

                // Start a new actionSequence
                ActionSequence actionSequence = new ActionSequence();
                actionSequences.add(actionSequence);

                // Add action to actionSequence
                actionSequence.add(action);

                // Record actionSequences on timeline
                // TODO: Cache and store the processAction actionSequences before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (actionSequences.size() > 3) {
                    actionSequences.remove(0);
                }

                // Process the action
                getCamera().getScene().onTouchListener(action);

                break;
            }

            case MOVE: {

                ActionSequence actionSequence = getPattern();
                actionSequence.add(action);

                // Current
                action.isPointing[action.pointerIndex] = true;

                // Classify/Callback
                if (actionSequence.getDragDistance() > Action.MINIMUM_DRAG_DISTANCE) {
                    action.setType(Action.Type.MOVE);
                    getCamera().getScene().onMoveListener(action);
                }

                break;
            }

            case RELEASE: {

                ActionSequence actionSequence = getPattern();
                actionSequence.add(action);

                // Current
                action.isPointing[action.pointerIndex] = false;

                // Stop listening for a hold action
                actionSequence.timerHandler.removeCallbacks(actionSequence.timerRunnable);

//                if (actionSequence.getDuration() < Action.MAXIMUM_TAP_DURATION) {
//                    action.setType(Action.Type.TOUCH);
//                    getCamera().getScene().onTapListener(action);
//                } else {
//                    action.setType(Action.Type.RELEASE);
//                    getCamera().getScene().onReleaseListener(action);
//                }

                getCamera().getScene().onReleaseListener(action);

                break;
            }
        }
    }
}
