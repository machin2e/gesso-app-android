package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Workspace;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.image.World;

/**
 * {@code InputSystem} models a user of Clay and performs actions in the simulated world on user's behalf,
 * based on the actions recognized on one of the {@code PhoneHost} objects associated with the
 * {@code InputSystem}.
 */
public class InputSystem extends System {

    private List<Event> incomingEvents = new ArrayList<>();

//    private List<Action> actions = new ArrayList<>();

    private Event previousEvent = null;

    public InputSystem() {
    }

    public boolean update(World world) {

        while (incomingEvents.size() > 0) {
            Event event = dequeueEvent();
            Event processedEvent = processEvent(event);

            processAction(processedEvent);
        }

        return true;
    }

    public void queueEvent(Event event) {
        incomingEvents.add(event);
    }

    private Event dequeueEvent() {
        if (incomingEvents.size() > 0) {
            return incomingEvents.remove(0);
        }
        return null;
    }

    private Event processEvent(Event event) {

        switch (event.getType()) {

            case SELECT: {

                // Create a new Action
//                Action action = new Action();
//                action.setInputSystem(this);
//                actions.add(action);
//
//                // Add Event to Action
//                action.addEvent(event);
//
//                // Record actions on timeline
//                // TODO: Cache and store the queueEvent actions before deleting them completely! Do it in
//                // TODO: (cont'd) a background thread.
//                if (actions.size() > 3) {
//                    actions.remove(0);
//                }

                previousEvent = null;
                if (previousEvent != null) {
                    event.previousEvent = previousEvent;
                } else {
                    event.previousEvent = null;
                }
                previousEvent = event;

                return event;
            }

            case HOLD: {

                // Start a new action
//                Action action = getAction();
//                actions.add(action);
//
//                // Add event to action
//                action.addEvent(event);

                if (previousEvent != null) {
                    event.previousEvent = previousEvent;
                } else {
                    event.previousEvent = null;
                }
                previousEvent = event;

                return event;
            }

            case MOVE: {

//                Action action = getAction();
//                action.addEvent(event);
//
//                // Current
//                event.isPointing[event.pointerIndex] = true;

                if (previousEvent != null) {
                    event.previousEvent = previousEvent;
                } else {
                    event.previousEvent = null;
                }
                previousEvent = event;

                return event;
            }

            case UNSELECT: {

//                Action action = getAction();
//                action.addEvent(event);
//
//                // Current
//                event.isPointing[event.pointerIndex] = false;

                if (previousEvent != null) {
                    event.previousEvent = previousEvent;
                } else {
                    event.previousEvent = null;
                }
                previousEvent = event;

                return event;
            }
        }

        return null;
    }

    // TODO: Move to ActionHandlerSystem
    public void processAction(Event event) {

//        Event event = action.getLastEvent();

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
                event.setTarget(targetEntity);

                // Action the Event
                // <HACK>
                if (targetEntity.hasComponent(Workspace.class)) {
                    ActionHandlerSystem.handleWorldAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Host.class)) {
                    ActionHandlerSystem.handleHostAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Extension.class)) {
                    ActionHandlerSystem.handleExtensionAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Port.class)) {
                    ActionHandlerSystem.handlePortAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Path.class)) {
                    ActionHandlerSystem.handlePathAction(targetEntity, event);
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
                event.setTarget(targetEntity);

                // Action the event
                // <HACK>
                if (targetEntity.hasComponent(Workspace.class)) {
                    ActionHandlerSystem.handleWorldAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Host.class)) {
                    ActionHandlerSystem.handleHostAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Extension.class)) {
                    ActionHandlerSystem.handleExtensionAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Port.class)) {
                    ActionHandlerSystem.handlePortAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Path.class)) {
                    ActionHandlerSystem.handlePathAction(targetEntity, event);
                }
                // </HACK>

                break;
            }

            case MOVE: {

                // Classify/Callback
//                if (action.getDragDistance() > Event.MINIMUM_DRAG_DISTANCE) {

                    Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());
                    Entity targetEntity = null;
                    if (targetEntities.size() > 0) {
                        targetEntity = targetEntities.get(0);
                    } else {
                        Group<Entity> workspaces = Entity.Manager.filterWithComponent(Workspace.class);
                        targetEntity = workspaces.get(0);
                    }
                    event.setTarget(targetEntity);

//                    targetEntity = event.getAction().getFirstEvent().getTarget();
                    targetEntity = event.getFirstEvent().getTarget();

                    // Action the Event
                    // <HACK>
                    if (targetEntity.hasComponent(Workspace.class)) {
                        ActionHandlerSystem.handleWorldAction(targetEntity, event);
                    } else if (targetEntity.hasComponent(Host.class)) {
                        ActionHandlerSystem.handleHostAction(targetEntity, event);
                    } else if (targetEntity.hasComponent(Extension.class)) {
                        ActionHandlerSystem.handleExtensionAction(targetEntity, event);
                    } else if (targetEntity.hasComponent(Port.class)) {
                        ActionHandlerSystem.handlePortAction(targetEntity, event);
                    } else if (targetEntity.hasComponent(Path.class)) {
                        ActionHandlerSystem.handlePathAction(targetEntity, event);
                    }
                    // </HACK>
//                }

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
                event.setTarget(targetEntity);

//                targetEntity = event.getAction().getFirstEvent().getTarget();
                targetEntity = event.getFirstEvent().getTarget();

                // Action the Event
                // <HACK>
                if (targetEntity.hasComponent(Workspace.class)) {
                    ActionHandlerSystem.handleWorldAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Host.class)) {
                    ActionHandlerSystem.handleHostAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Extension.class)) {
                    ActionHandlerSystem.handleExtensionAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Port.class)) {
                    ActionHandlerSystem.handlePortAction(targetEntity, event);
                } else if (targetEntity.hasComponent(Path.class)) {
                    ActionHandlerSystem.handlePathAction(targetEntity, event);
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
//    private Action getAction() {
//        if (actions.size() > 0) {
//            return actions.get(actions.size() - 1);
//        } else {
//            return null;
//        }
//    }

//    public List<Action> getActions() {
//        return this.actions;
//    }
}
