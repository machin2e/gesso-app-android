package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.engine.Event;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Geometry;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.EventHandler;

public class EventHandlerSystem extends System {

    // <EVENT_MANAGER>
    private HashMap<Event.Type, ArrayList<EventHandler>> eventHandlers = new HashMap<>();

    public boolean subscribe(Event.Type eventType, EventHandler<?> eventHandler) {
        if (!eventHandlers.containsKey(eventType)) {
            eventHandlers.put(eventType, new ArrayList());
            eventHandlers.get(eventType).add(eventHandler);
            return true;
        } else if (eventHandlers.containsKey(eventType) && !eventHandlers.get(eventType).contains(eventHandler)) {
            eventHandlers.get(eventType).add(eventHandler);
            return true;
        } else {
            return false;
        }
    }

    private void notifySubscribers(Event event) {

        // Get subscribers to Event
        ArrayList<EventHandler> subscribedEventHandlers = eventHandlers.get(event.getType());
        if (subscribedEventHandlers != null) {
            for (int i = 0; i < subscribedEventHandlers.size(); i++) {
                subscribedEventHandlers.get(i).execute(event);
            }
        }
    }

    // TODO: public boolean unsubscribe(...)

    // </EVENT_MANAGER>

    // TODO: Rename to EventManager, ActionManager, or something like that.
    // TODO: Allow Entities to register for specific Events/Actions.

    private List<Event> incomingEvents = new ArrayList<>();

    public Entity previousPrimaryTarget = null;

    public EventHandlerSystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        // Dequeue actions and apply them to the targeted Entity
        while (incomingEvents.size() > 0) {
            dispatchEvent(dequeueEvent());
        }
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

    private void dispatchEvent(Event event) {

        Entity primaryTarget = null;

        // Handle special cases for SELECT and non-SELECT events
        if (event.getType() != Event.Type.SELECT) {
            event.setTarget(event.getFirstEvent().getTarget());
            event.setSecondaryTarget(event.getFirstEvent().getSecondaryTarget());
        } else {

            // Annotate the Event
            Group<Entity> primaryTargets = world.Manager.getEntities().filterVisibility(true).filterWithComponents(Image.class, Boundary.class).sortByLayer().filterContains(event.getPosition());
            Group<Entity> secondaryTargets = world.Manager.getEntities().filterVisibility(true).filterWithComponents(Geometry.class, Boundary.class).filterContains(event.getPosition());

            if (primaryTargets.size() > 0) {
                primaryTarget = primaryTargets.get(primaryTargets.size() - 1); // Get primary target from the top layer (will be last in the list of targets)
            } else {
                Group<Entity> cameras = world.Manager.getEntities().filterWithComponent(Camera.class);
                primaryTarget = cameras.get(0);
            }
            event.setTarget(primaryTarget);

            if (primaryTarget.hasComponent(Image.class)) { // Needed because entities like Camera without Image component are also processed here.
                for (int i = 0; i < secondaryTargets.size(); i++) {
                    if (Image.getShapes(primaryTarget).contains(secondaryTargets.get(i))) {
                        event.setSecondaryTarget(secondaryTargets.get(i));
                    }
                }
            }
        }

        // Dispatch the Event to subscribers
        Entity eventTarget = event.getTarget();
        if (eventTarget != null) {
            notifySubscribers(event);
        }

        // Handle special bookkeeping storing previous target Entity
        if (event.getType() == Event.Type.UNSELECT) {
            previousPrimaryTarget = event.getTarget();
        }
    }
}
