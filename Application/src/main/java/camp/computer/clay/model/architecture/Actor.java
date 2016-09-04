package camp.computer.clay.model.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.interaction.*;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Transcript;
import camp.computer.clay.scene.architecture.Figure;
import camp.computer.clay.scene.architecture.Scene;

public class Actor { // Controller

    private Camera camera = null;

    // PatternSet (Smart querying interface)
    public List<Transcript> transcripts = new LinkedList<>();

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
    private Transcript getPattern() {
        if (transcripts.size() > 0) {
            return transcripts.get(transcripts.size() - 1);
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

                // Start a new transcript
                Transcript transcript = new Transcript();
                transcripts.add(transcript);

                // Add action to transcript
                transcript.add(action);

                // Record transcripts on timeline
                // TODO: Cache and store the processAction transcripts before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (transcripts.size() > 3) {
                    transcripts.remove(0);
                }

                // Transcript the action

                // Set the target
                Figure targetFigure = getCamera().getScene().getFigureByPosition(action.getPosition());
                action.setTarget(targetFigure);

                action.getTarget().processAction(action);

                //getCamera().getScene().onTouchListener(action);

                break;
            }

            case MOVE: {

                Transcript transcript = getPattern();
                transcript.add(action);

                // Current
                action.isPointing[action.pointerIndex] = true;

                // Classify/Callback
                if (transcript.getDragDistance() > Action.MINIMUM_DRAG_DISTANCE) {
                    // action.setType(Action.Type.MOVE);

                    Figure targetFigure = getCamera().getScene().getFigureByPosition(action.getPosition());
                    action.setTarget(targetFigure);

                    // <HACK>
                    //Transcript transcript = action.getActionSequence();
                    if (transcript.getSize() > 1) {
                        action.setTarget(transcript.getFirst().getTarget());
                    }
                    // </HACK>

                    action.getTarget().processAction(action);

//                    getCamera().getScene().onMoveListener(action);
                }

                break;
            }

            case RELEASE: {

                Transcript transcript = getPattern();
                transcript.add(action);

                // Current
                action.isPointing[action.pointerIndex] = false;

                // Stop listening for a hold action
                transcript.timerHandler.removeCallbacks(transcript.timerRunnable);

//                if (transcript.getDuration() < Action.MAXIMUM_TAP_DURATION) {
//                    action.setType(Action.Type.TOUCH);
//                    getCamera().getScene().onTapListener(action);
//                } else {
//                    action.setType(Action.Type.RELEASE);
//                    getCamera().getScene().onReleaseListener(action);
//                }

                // Set the target
                Figure targetFigure = getCamera().getScene().getFigureByPosition(action.getPosition());
                action.setTarget(targetFigure);

                action.getTarget().processAction(action);

//                getCamera().getScene().onReleaseListener(action);

                break;
            }
        }
    }
}
