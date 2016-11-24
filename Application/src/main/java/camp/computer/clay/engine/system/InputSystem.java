package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.manager.Event;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Geometry;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.entity.Entity;

public class InputSystem extends System {

    private List<Event> eventQueue = new ArrayList<>();

    public InputSystem(World world) {
        super(world);
    }

    public void update() {
        while (eventQueue.size() > 0) {
            Event event = dequeueEvent();
            Event processedEvent = processEvent(event);

            processAndDispatchEvent(processedEvent);
        }
    }

    public void queueEvent(Event event) {
        eventQueue.add(event);
    }

    private Event dequeueEvent() {
        return eventQueue.remove(0);
    }

    private Event processEvent(Event event) {

        switch (event.getType()) {
            case SELECT: {
                break;
            }

            case HOLD: {

                // <REFACTOR>
                // There might be a better way to do this. How can I assign reasonable coordinates to the synthetic HOLD event?
                // TODO: Set coordinates of hold... to first event?
                Event firstEvent = event.getFirstEvent();
                for (int i = 0; i < firstEvent.pointerCoordinates.length; i++) {
                    event.pointerCoordinates[i].x = firstEvent.pointerCoordinates[i].x;
                    event.pointerCoordinates[i].y = firstEvent.pointerCoordinates[i].y;
                }
                // </REFACTOR>

                break;
            }

            case MOVE: {
                break;
            }

            case UNSELECT: {
                break;
            }
        }

        return event;
    }

    public Entity previousPrimaryTarget = null;

    private void processAndDispatchEvent(Event event) {

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
            world.notifySubscribers(event);
        }

        // Handle special bookkeeping storing previous target Entity
        if (event.getType() == Event.Type.UNSELECT) {
            previousPrimaryTarget = event.getTarget();
        }
    }
}
