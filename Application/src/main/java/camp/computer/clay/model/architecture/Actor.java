package camp.computer.clay.model.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.interaction.*;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.scene.architecture.Scene;

public class Actor { // Controller

    private Camera camera = null;

    // PatternSet (Smart querying interface)
    public List<Pattern> patterns = new LinkedList<>();

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
    private Pattern getPattern() {
        if (patterns.size() > 0) {
            return patterns.get(patterns.size() - 1);
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

                // Start a new pattern
                Pattern pattern = new Pattern();
                patterns.add(pattern);

                // Add action to pattern
                pattern.add(action);

                // Record patterns on timeline
                // TODO: Cache and store the processAction patterns before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (patterns.size() > 3) {
                    patterns.remove(0);
                }

                // Process the action
                getCamera().getScene().onTouchListener(action);

                break;
            }

            case MOVE: {

                Pattern pattern = getPattern();
                pattern.add(action);

                // Current
                action.isPointing[action.pointerIndex] = true;

                // Classify/Callback
                if (pattern.getDragDistance() > Action.MINIMUM_DRAG_DISTANCE) {
                    action.setType(Action.Type.MOVE);
                    getCamera().getScene().onMoveListener(action);
                }

                break;
            }

            case RELEASE: {

                Pattern pattern = getPattern();
                pattern.add(action);

                // Current
                action.isPointing[action.pointerIndex] = false;

                // Stop listening for a hold action
                pattern.timerHandler.removeCallbacks(pattern.timerRunnable);

//                if (pattern.getDuration() < Action.MAXIMUM_TAP_DURATION) {
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
