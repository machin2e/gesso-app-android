package camp.computer.clay.model.architecture;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.interaction.*;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Process;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.util.geometry.Shape;

/**
 * {@code Actor} models a user of Clay and performs actions in the simulated world on user's behalf,
 * based on the actions recognized on one of the {@code Device} objects associated with the
 * {@code Actor}.
 */
public class Actor { // Controller

    private Camera camera = null;

    // Process (Smart querying interface)
    public List<Process> processes = new LinkedList<>();

    public Actor() {
        setup();
    }

    private void setup() {
        // Camera
        Camera camera = new Camera();
        setCamera(camera);
    }

    /**
     * Sets the {@code Camera} that defines the {@code Actor}'s viewing area onto the {@code Scene}.
     *
     * @param camera The {@code Camera} to use to define the viewing area onto the {@code Scene}.
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public boolean hasCamera() {
        return camera != null;
    }

    public Camera getCamera() {
        return this.camera;
    }

    /**
     * Convenience method.
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
    private Process getProcess() {
        if (processes.size() > 0) {
            return processes.get(processes.size() - 1);
        } else {
            return null;
        }
    }

    public void processAction(Action action) { // TODO: Rename to processAction()

        action.setActor(this);

        switch (action.getType()) {

            case SELECT: {

                // Having an idea is just accumulating intention. It's a suggestion from your existential
                // controller.

                // Start a new process
                Process process = new Process();
                processes.add(process);

                // Add action to process
                process.add(action);

                // Record processes on timeline
                // TODO: Cache and store the processAction processes before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (processes.size() > 3) {
                    processes.remove(0);
                }

                // Set the target image
                Image targetImage = getCamera().getScene().getImageByCoordinate(action.getCoordinate());
                action.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShapeByCoordinate(action.getCoordinate());
                action.setTargetShape(targetShape);

                // Process the action
                action.getTargetImage().processAction(process);

                //getCamera().getScene().onTouchListener(action);

                break;
            }

            case MOVE: {

                Process process = getProcess();
                process.add(action);

                // Current
                action.isPointing[action.pointerIndex] = true;

                // Classify/Callback
                if (process.getDragDistance() > Action.MINIMUM_DRAG_DISTANCE) {

                    // Set the target image
                    Image targetImage = getCamera().getScene().getImageByCoordinate(action.getCoordinate());
                    action.setTargetImage(targetImage);

                    // Set the target shape
                    Shape targetShape = targetImage.getShapeByCoordinate(action.getCoordinate());
                    action.setTargetShape(targetShape);

//                    // <HACK>
//                    // TODO: Update handlers and Delete!
//                    //Process process = action.getProcess();
//                    if (process.getSize() > 1) {
//                        action.setTargetImage(process.getStartAction().getTargetImage());
//                        action.setTargetShape(process.getStartAction().getTargetShape());
//                    }
//                    // </HACK>

                    //action.getTargetImage().processAction(process);
                    process.getStartAction().getTargetImage().processAction(process);
                }

                break;
            }

            case UNSELECT: {

                Process process = getProcess();
                process.add(action);

                // Current
                action.isPointing[action.pointerIndex] = false;

                // Stop listening for a hold action
                process.timerHandler.removeCallbacks(process.timerRunnable);

//                if (process.getDuration() < Action.MAXIMUM_TAP_DURATION) {
//                    action.setType(Action.Type.SELECT);
//                    getCamera().getScene().onTapListener(action);
//                } else {
//                    action.setType(Action.Type.UNSELECT);
//                    getCamera().getScene().onReleaseListener(action);
//                }

                // Set the target image
                Image targetImage = getCamera().getScene().getImageByCoordinate(action.getCoordinate());
                action.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShapeByCoordinate(action.getCoordinate());
                action.setTargetShape(targetShape);

                //action.getTargetImage().processAction(process);
                process.getStartAction().getTargetImage().processAction(process);

//                getCamera().getScene().onReleaseListener(action);

                break;
            }
        }
    }
}
