package camp.computer.clay.model.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.model.interaction.*;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.architecture.Space;
import camp.computer.clay.space.architecture.Shape;

/**
 * {@code Actor} models a user of Clay and performs actions in the simulated world on user's behalf,
 * based on the actions recognized on one of the {@code Host} objects associated with the
 * {@code Actor}.
 */
public class Actor {

    private Camera camera = null;

    // Action (Smart querying interface)
    public List<Action> actions = new LinkedList<>();

    public Actor() {
        setup();
    }

    private void setup() {
        // Camera
        Camera camera = new Camera();
        setCamera(camera);
    }

    /**
     * Sets the {@code Camera} that defines the {@code Actor}'s viewing area onto the {@code Space}.
     *
     * @param camera The {@code Camera} to use to define the viewing area onto the {@code Space}.
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
    public Space getSpace() {
        if (camera != null) {
            return camera.getSpace();
        }
        return null;
    }

    /**
     * Returns the most recent interaction.
     *
     * @return The most recent interaction.
     */
    private Action getAction() {
        if (actions.size() > 0) {
            return actions.get(actions.size() - 1);
        } else {
            return null;
        }
    }

    public void processAction(Event event) { // TODO: Rename to processAction()

        // <HACK>
        Launcher.getView().publish(event.getType().toString());
        // </HACK>

        event.setActor(this);

        switch (event.getType()) {

            case SELECT: {

                // Having an idea is just accumulating intention. It's a suggestion from your existential
                // controller.

                // Start a new action
                Action action = new Action();
                actions.add(action);

                // Add event to action
                action.addEvent(event);

                // Record actions on timeline
                // TODO: Cache and store the processAction actions before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (actions.size() > 3) {
                    actions.remove(0);
                }

                // Set the target image
                Image targetImage = getCamera().getSpace().getImageByPosition(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShapeByPosition(event.getPosition());
                event.setTargetShape(targetShape);

                // Action the event
                event.getTargetImage().processAction(action);

                break;
            }

            case HOLD: {

                // Start a new action
                Action action = getAction();
                actions.add(action);

                // Add event to action
                action.addEvent(event);

                // Set the target image
                Image targetImage = getCamera().getSpace().getImageByPosition(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShapeByPosition(event.getPosition());
                event.setTargetShape(targetShape);

                // Action the event
                event.getTargetImage().processAction(action);

                break;
            }

            case MOVE: {

                Action action = getAction();
                action.addEvent(event);

                // Current
                event.isPointing[event.pointerIndex] = true;

                // Classify/Callback
                if (action.getDragDistance() > Event.MINIMUM_DRAG_DISTANCE) {

                    // Set the target image
                    Image targetImage = getCamera().getSpace().getImageByPosition(event.getPosition());
                    event.setTargetImage(targetImage);

                    // Set the target shape
                    Shape targetShape = targetImage.getShapeByPosition(event.getPosition());
                    event.setTargetShape(targetShape);

                    action.getFirstEvent().getTargetImage().processAction(action);
                }

                break;
            }

            case UNSELECT: {

                Action action = getAction();
                action.addEvent(event);

                // Current
                event.isPointing[event.pointerIndex] = false;

                // Stop listening for a hold event
                action.timerHandler.removeCallbacks(action.timerRunnable);

                // Set the target image
                Image targetImage = getCamera().getSpace().getImageByPosition(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShapeByPosition(event.getPosition());
                event.setTargetShape(targetShape);

                //event.getTargetImage().processAction(action);
                action.getFirstEvent().getTargetImage().processAction(action);

//                getCamera().getSpace().onReleaseListener(event);

                break;
            }
        }
    }
}
