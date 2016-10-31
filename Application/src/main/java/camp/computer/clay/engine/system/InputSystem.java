package camp.computer.clay.engine.system;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Workspace;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.image.World;

/**
 * {@code InputSystem} models a user of Clay and performs actions in the simulated world on user's behalf,
 * based on the actions recognized on one of the {@code PhoneHost} objects associated with the
 * {@code InputSystem}.
 */
public class InputSystem extends System {

    private List<Event> incomingEvents = new LinkedList<>();

    private List<Action> actions = new LinkedList<>();

    public InputSystem() {
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

        event.setInputSystem(this);

        switch (event.getType()) {

            case SELECT: {

                // Create a new Action
                Action action = new Action();
                action.setInputSystem(this);
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

    // TODO: Move to ActionHandlerSystem
    public void processAction(Action action, Event event) {

        switch (event.getType()) {

            case SELECT: {

                // TODO: Add Boundary component.

                Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());
                Entity targetEntity = null;
                if (targetEntities.size() > 0) {
                    targetEntity = targetEntities.get(0);
                } else {
                    Group<Entity> workspaces = Entity.Manager.filterWithComponent(Workspace.class);
                    targetEntity = workspaces.get(0);
                }
                event.setTargetEntity(targetEntity);

                // Action the Event
                // <HACK>
                if (targetEntity.hasComponent(Workspace.class)) {
                    ActionHandlerSystem.handleWorldAction(targetEntity, action);
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

                Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());
                Entity targetEntity = null;
                if (targetEntities.size() > 0) {
                    targetEntity = targetEntities.get(0);
                } else {
                    Group<Entity> workspaces = Entity.Manager.filterWithComponent(Workspace.class);
                    targetEntity = workspaces.get(0);
                }
                event.setTargetEntity(targetEntity);

                // Action the event
                // <HACK>
                if (targetEntity.hasComponent(Workspace.class)) {
                    ActionHandlerSystem.handleWorldAction(targetEntity, action);
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

                // Classify/Callback
                if (action.getDragDistance() > Event.MINIMUM_DRAG_DISTANCE) {

                    Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());
                    Entity targetEntity = null;
                    if (targetEntities.size() > 0) {
                        targetEntity = targetEntities.get(0);
                    } else {
                        Group<Entity> workspaces = Entity.Manager.filterWithComponent(Workspace.class);
                        targetEntity = workspaces.get(0);
                    }
                    event.setTargetEntity(targetEntity);

                    targetEntity = event.getAction().getFirstEvent().getTargetEntity();

                    // Action the Event
                    // <HACK>
                    if (targetEntity.hasComponent(Workspace.class)) {
                        ActionHandlerSystem.handleWorldAction(targetEntity, action);
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
                }

                break;
            }

            case UNSELECT: {

                Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());
                Entity targetEntity = null;
                if (targetEntities.size() > 0) {
                    targetEntity = targetEntities.get(0);
                } else {
                    Group<Entity> workspaces = Entity.Manager.filterWithComponent(Workspace.class);
                    targetEntity = workspaces.get(0);
                }
                event.setTargetEntity(targetEntity);

                targetEntity = event.getAction().getFirstEvent().getTargetEntity();

                // Action the Event
                // <HACK>
                if (targetEntity.hasComponent(Workspace.class)) {
                    ActionHandlerSystem.handleWorldAction(targetEntity, action);
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

    @Override
    public boolean update(World world) {
        dequeueEvents();
        return true;
    }
}
