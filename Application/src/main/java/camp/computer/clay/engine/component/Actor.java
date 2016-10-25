package camp.computer.clay.engine.component;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Camera;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

/**
 * {@code Actor} models a user of Clay and performs actions in the simulated world on user's behalf,
 * based on the actions recognized on one of the {@code PhoneHost} objects associated with the
 * {@code Actor}.
 */
public class Actor extends Component {

    private Camera camera = null;

    private List<Event> incomingEvents = new LinkedList<>();

    private List<Action> actions = new LinkedList<>();

    public Actor(Entity entity) {
        super(entity);
        setup();
    }

    private void setup() {
        Camera camera = new Camera();
        setCamera(camera);
    }

    /**
     * Sets the {@code Camera} that defines the {@code Actor}'s viewing area onto the {@code Space}.
     *
     * @param camera The {@code Camera} to use to define the viewing area onto the {@code Space}.
     */
    // TODO: Remove?
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    // TODO: Remove?
    public boolean hasCamera() {
        return camera != null;
    }

    // TODO: Remove?
    public Camera getCamera() {
        return this.camera;
    }

    public void queueEvent(Event event) {
        incomingEvents.add(event);
    }

    private void dequeueEvents() {
        while (incomingEvents.size() > 0) {
            Event event = incomingEvents.remove(0);
            doAction(event);
        }
    }

    private void doAction(Event event) {

        event.setActor(this);

        switch (event.getType()) {

            case SELECT: {

                // Create a new Action
                Action action = new Action();
                action.setActor(this);
                actions.add(action);

                // Add Event to Action
                action.addEvent(event);

                // Record actions on timeline
                // TODO: Cache and store the queueEvent actions before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (actions.size() > 3) {
                    actions.remove(0);
                }

                processAction(action, event);

                break;
            }

            case HOLD: {

                // Start a new action
                Action action = getAction();
                actions.add(action);

                // Add event to action
                action.addEvent(event);

                processAction(action, event);

                break;
            }

            case MOVE: {

                Action action = getAction();
                action.addEvent(event);

                // Current
                event.isPointing[event.pointerIndex] = true;

                processAction(action, event);

                break;
            }

            case UNSELECT: {

                Action action = getAction();
                action.addEvent(event);

                // Current
                event.isPointing[event.pointerIndex] = false;

                processAction(action, event);

                break;
            }
        }
    }

    public void processAction(Action action, Event event) {

        switch (event.getType()) {

            case SELECT: {

                // Set the target image
                Group<Image> targetImages = Entity.Manager.getImages().filterVisibility(Visibility.VISIBLE).filterContains(event.getPosition());
                Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : getCamera().getSpace(); // getImage(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShape(event.getPosition());
                event.setTargetShape(targetShape);

                // Action the event
                // <HACK>
                if (targetImage.getClass() == Space.class) {
                    ((Space) targetImage).processAction(action);
                } else {
                    targetImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
                }
                // </HACK>

                break;
            }

            case HOLD: {

                // Set the target image
                Group<Image> targetImages = Entity.Manager.getImages().filterVisibility(Visibility.VISIBLE).filterContains(event.getPosition());
                Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : getCamera().getSpace(); // Image targetImage = getCamera().getSpace().getImage(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShape(event.getPosition());
                event.setTargetShape(targetShape);

                // Action the event
//                event.getTargetImage().getEntity().processAction(action);
                // Action the event
                // <HACK>
                if (targetImage.getClass() == Space.class) {
                    ((Space) targetImage).processAction(action);
                } else {
                    targetImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
                }
                // </HACK>

                break;
            }

            case MOVE: {

                // Classify/Callback
                if (action.getDragDistance() > Event.MINIMUM_DRAG_DISTANCE) {

                    // Set the target image
                    Group<Image> targetImages = Entity.Manager.getImages().filterVisibility(Visibility.VISIBLE).filterContains(event.getPosition());
                    Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : getCamera().getSpace(); // Image targetImage = getCamera().getSpace().getImage(event.getPosition());
                    event.setTargetImage(targetImage);

                    // Set the target shape
                    Shape targetShape = targetImage.getShape(event.getPosition());
                    event.setTargetShape(targetShape);

//                    action.getFirstEvent().getTargetImage().getEntity().processAction(action);
                    // Action the event
                    // <HACK>
                    Image firstImage = action.getFirstEvent().getTargetImage();
                    if (firstImage.getClass() == Space.class) {
                        ((Space) firstImage).processAction(action);
                    } else {
                        firstImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
                    }
                    // </HACK>
                }

                break;
            }

            case UNSELECT: {

                // Stop listening for a hold event
//                action.timerHandler.removeCallbacks(action.timerRunnable);

                // Set the target image
                Group<Image> targetImages = Entity.Manager.getImages().filterVisibility(Visibility.VISIBLE).filterContains(event.getPosition());
                Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : getCamera().getSpace(); // Image targetImage = getCamera().getSpace().getImage(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target shape
                Shape targetShape = targetImage.getShape(event.getPosition());
                event.setTargetShape(targetShape);

                //event.getTargetImage().queueEvent(action);
//                action.getFirstEvent().getTargetImage().getEntity().processAction(action);
                // Action the event
                // <HACK>
                Image firstImage = action.getFirstEvent().getTargetImage();
                if (firstImage.getClass() == Space.class) {
                    ((Space) firstImage).processAction(action);
                } else {
                    firstImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
                }
                // </HACK>

                break;
            }
        }
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

    public List<Action> getActions() {
        return this.actions;
    }

    public void update() {
        dequeueEvents();
    }

}
