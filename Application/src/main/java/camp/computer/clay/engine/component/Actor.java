package camp.computer.clay.engine.component;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.ActionHandlerSystem;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.World;

/**
 * {@code Actor} models a user of Clay and performs actions in the simulated world on user's behalf,
 * based on the actions recognized on one of the {@code PhoneHost} objects associated with the
 * {@code Actor}.
 */
public class Actor extends Component {

    private List<Event> incomingEvents = new LinkedList<>();

    private List<Action> actions = new LinkedList<>();

    public Actor() {
        super();
        setup();
    }

    private void setup() {
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

                // TODO: Add Boundary component.
                // TODO: Remove targetImage.
                // TODO: Replace targetImage with targetBoundary (or better, just use Entity)

                Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

                // Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());

                // Set the target image
                Group<Image> targetImages = Entity.Manager.filterVisibility(true).filterContains(event.getPosition()).getImages();
                Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : camera.getComponent(Camera.class).getWorld(); // getImage(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target Entity
                event.setTargetEntity(targetImage.getEntity());
                Entity targetEntity = targetImage.getEntity();

//                // Set the target shape
//                Shape targetShape = targetImage.getShape(event.getPosition());
//                event.setTargetShape(targetShape);

                // Action the event
                // <HACK>
//                if (targetImage.getClass() == World.class) {
//                    World world = ((World) targetImage);
//                    ActionHandlerSystem.handleWorldAction(world, action);
//                } else if (targetImage.getEntity().hasComponent(Host.class)) {
//                    Entity hostEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handleHostAction(hostEntity, action);
//                } else if (targetImage.getEntity().hasComponent(Extension.class)) {
//                    Entity extensionEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handleExtensionAction(extensionEntity, action);
//                } else if (targetImage.getEntity().hasComponent(Port.class)) {
//                    Entity portEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handlePortAction(portEntity, action);
//                } else if (targetImage.getEntity().hasComponent(Path.class)) {
//                    Entity pathEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handlePathAction(pathEntity, action);
//                } else {
//                    targetImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
//                }
                if (targetImage.getClass() == World.class) {
                    World world = ((World) targetImage);
                    ActionHandlerSystem.handleWorldAction(world, action);
                } else if (targetEntity.hasComponent(Host.class)) {
                    ActionHandlerSystem.handleHostAction(targetEntity, action);
                } else if (targetEntity.hasComponent(Extension.class)) {
                    ActionHandlerSystem.handleExtensionAction(targetEntity, action);
                } else if (targetEntity.hasComponent(Port.class)) {
                    ActionHandlerSystem.handlePortAction(targetEntity, action);
                } else if (targetEntity.hasComponent(Path.class)) {
                    ActionHandlerSystem.handlePathAction(targetEntity, action);
                }
                // </HACK>

                break;
            }

            case HOLD: {

                Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

                // Set the target image
//                Group<Image> targetImages = Entity.Manager.getImages().filterVisibility(true).filterContains(event.getPosition());
                Group<Image> targetImages = Entity.Manager.filterVisibility(true).filterContains(event.getPosition()).getImages();
                Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : cameraEntity.getComponent(Camera.class).getWorld(); // Image targetImage = getCameraEntity().getWorld().getImage(event.getPosition());
                event.setTargetImage(targetImage);

                // Set the target Entity
                event.setTargetEntity(targetImage.getEntity());
                Entity targetEntity = targetImage.getEntity();

//                // Set the target shape
//                Shape targetShape = targetImage.getShape(event.getPosition());
//                event.setTargetShape(targetShape);

                // Action the event
//                event.getTargetImage().getEntity().processAction(action);
                // Action the event
                // <HACK>
//                if (targetImage.getClass() == World.class) {
//                    World world = ((World) targetImage);
//                    ActionHandlerSystem.handleWorldAction(world, action);
//                } else if (targetImage.getEntity().hasComponent(Host.class)) {
//                    Entity hostEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handleHostAction(hostEntity, action);
//                } else if (targetImage.getEntity().hasComponent(Extension.class)) {
//                    Entity extensionEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handleExtensionAction(extensionEntity, action);
//                } else if (targetImage.getEntity().hasComponent(Port.class)) {
//                    Entity portEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handlePortAction(portEntity, action);
//                } else if (targetImage.getEntity().hasComponent(Path.class)) {
//                    Entity pathEntity = targetImage.getEntity();
//                    ActionHandlerSystem.handlePathAction(pathEntity, action);
//                } else {
//                    targetImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
//                }
                if (targetImage.getClass() == World.class) {
                    World world = ((World) targetImage);
                    ActionHandlerSystem.handleWorldAction(world, action);
                } else if (targetEntity.hasComponent(Host.class)) {
                    ActionHandlerSystem.handleHostAction(targetEntity, action);
                } else if (targetEntity.hasComponent(Extension.class)) {
                    ActionHandlerSystem.handleExtensionAction(targetEntity, action);
                } else if (targetEntity.hasComponent(Port.class)) {
                    ActionHandlerSystem.handlePortAction(targetEntity, action);
                } else if (targetEntity.hasComponent(Path.class)) {
                    ActionHandlerSystem.handlePathAction(targetEntity, action);
                }
                // </HACK>

                break;
            }

            case MOVE: {

                Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

                // Classify/Callback
                if (action.getDragDistance() > Event.MINIMUM_DRAG_DISTANCE) {

                    // Set the target image
//                    Group<Image> targetImages = Entity.Manager.getImages().filterVisibility(true).filterContains(event.getPosition());
                    Group<Image> targetImages = Entity.Manager.filterVisibility(true).filterContains(event.getPosition()).getImages();
                    Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : cameraEntity.getComponent(Camera.class).getWorld(); // Image targetImage = getCameraEntity().getWorld().getImage(event.getPosition());
                    event.setTargetImage(targetImage);

//                    // Set the target shape
//                    Shape targetShape = targetImage.getShape(event.getPosition());
//                    event.setTargetShape(targetShape);

//                    action.getFirstEvent().getTargetImage().getEntity().processAction(action);
                    // Action the event
                    // <HACK>
                    Image firstImage = action.getFirstEvent().getTargetImage();

                    // Set the target Entity
//                    event.setTargetEntity(firstImage.getEntity());
                    event.setTargetEntity(targetImage.getEntity());
                    Entity targetEntity = firstImage.getEntity();


                    if (firstImage.getClass() == World.class) {
                        World world = ((World) targetImage);
                        ActionHandlerSystem.handleWorldAction(world, action);
                    } else if (firstImage.getEntity().hasComponent(Host.class)) {
                        Entity hostEntity = firstImage.getEntity();
                        ActionHandlerSystem.handleHostAction(hostEntity, action);
                    } else if (firstImage.getEntity().hasComponent(Extension.class)) {
                        Entity extensionEntity = firstImage.getEntity();
                        ActionHandlerSystem.handleExtensionAction(extensionEntity, action);
                    } else if (firstImage.getEntity().hasComponent(Port.class)) {
                        Entity portEntity = firstImage.getEntity();
                        ActionHandlerSystem.handlePortAction(portEntity, action);
                    } else if (firstImage.getEntity().hasComponent(Port.class)) {
                        Entity pathEntity = firstImage.getEntity();
                        ActionHandlerSystem.handlePathAction(pathEntity, action);
                    } else {
                        firstImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
                    }
//                    if (targetImage.getClass() == World.class) {
//                        World world = ((World) targetImage);
//                        ActionHandlerSystem.handleWorldAction(world, action);
//                    } else if (targetEntity.hasComponent(Host.class)) {
//                        ActionHandlerSystem.handleHostAction(targetEntity, action);
//                    } else if (targetEntity.hasComponent(Extension.class)) {
//                        ActionHandlerSystem.handleExtensionAction(targetEntity, action);
//                    } else if (targetEntity.hasComponent(Port.class)) {
//                        ActionHandlerSystem.handlePortAction(targetEntity, action);
//                    } else if (targetEntity.hasComponent(Path.class)) {
//                        ActionHandlerSystem.handlePathAction(targetEntity, action);
//                    }
                    // </HACK>
                }

                break;
            }

            case UNSELECT: {

                Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

                // Stop listening for a hold event
//                action.timerHandler.removeCallbacks(action.timerRunnable);

                // Set the target image
//                Group<Image> targetImages = Entity.Manager.getImages().filterVisibility(true).filterContains(event.getPosition());
                Group<Image> targetImages = Entity.Manager.filterVisibility(true).filterContains(event.getPosition()).getImages();
                Image targetImage = targetImages.size() > 0 ? targetImages.get(0) : cameraEntity.getComponent(Camera.class).getWorld(); // Image targetImage = getCameraEntity().getWorld().getImage(event.getPosition());
                event.setTargetImage(targetImage);

//                // Set the target shape
//                Shape targetShape = targetImage.getShape(event.getPosition());
//                event.setTargetShape(targetShape);

                //event.getTargetImage().queueEvent(action);
//                action.getFirstEvent().getTargetImage().getEntity().processAction(action);
                // Action the event
                // <HACK>
                Image firstImage = action.getFirstEvent().getTargetImage();

                // Set the target Entity
                event.setTargetEntity(targetImage.getEntity());
                Entity targetEntity = firstImage.getEntity();

                if (firstImage.getClass() == World.class) {
                    World world = ((World) targetImage);
                    ActionHandlerSystem.handleWorldAction(world, action);
                } else if (firstImage.getEntity().hasComponent(Host.class)) {
                    Entity hostEntity = firstImage.getEntity();
                    ActionHandlerSystem.handleHostAction(hostEntity, action);
                } else if (firstImage.getEntity().hasComponent(Extension.class)) {
                    Entity extensionEntity = firstImage.getEntity();
                    ActionHandlerSystem.handleExtensionAction(extensionEntity, action);
                } else if (firstImage.getEntity().hasComponent(Port.class)) {
                    Entity portEntity = firstImage.getEntity();
                    ActionHandlerSystem.handlePortAction(portEntity, action);
                } else if (firstImage.getEntity().hasComponent(Path.class)) {
                    Entity pathEntity = firstImage.getEntity();
                    ActionHandlerSystem.handlePathAction(pathEntity, action);
                } else {
                    firstImage.getEntity().getComponent(ActionListenerComponent.class).processAction(action);
                }

//                if (targetImage.getClass() == World.class) {
//                    World world = ((World) targetImage);
//                    ActionHandlerSystem.handleWorldAction(world, action);
//                } else if (targetEntity.hasComponent(Host.class)) {
//                    ActionHandlerSystem.handleHostAction(targetEntity, action);
//                } else if (targetEntity.hasComponent(Extension.class)) {
//                    ActionHandlerSystem.handleExtensionAction(targetEntity, action);
//                } else if (targetEntity.hasComponent(Port.class)) {
//                    ActionHandlerSystem.handlePortAction(targetEntity, action);
//                } else if (targetEntity.hasComponent(Path.class)) {
//                    ActionHandlerSystem.handlePathAction(targetEntity, action);
//                }
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
