package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Event;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.platform.Application;

public class InputSystem extends System {

    private List<Event> eventQueue = new ArrayList<>();

    public InputSystem(World world) {
        super(world);
    }

    Entity camera = null;

    private Event previousEvent = null;

    public void update(long dt) {

        if (camera == null) {
            camera = world.entities.get().filterWithComponent(Camera.class).get(0);
        }

        if (camera != null) {
            while (eventQueue.size() > 0) {
                world.getSystem(EventSystem.class).queue(process(dequeue()));
            }
        }
    }

    public void queue(Event event) {
        eventQueue.add(event);
    }

    private Event dequeue() {
        return eventQueue.remove(0);
    }

    private Event process(Event event) {

        // Set world position
        for (int i = 0; i < Event.MAXIMUM_POINT_COUNT; i++) {
            // TODO: Update equations so cameraScale is always the correct scale, the current scale, and computed as needed.
            Transform origin = Application.getInstance().platformRenderSurface.originTransform;
            event.pointerCoordinates[i].x = (event.surfaceCoordinates[i].x - (origin.x + camera.getComponent(Transform.class).x)) / camera.getComponent(Transform.class).scale;
            event.pointerCoordinates[i].y = (event.surfaceCoordinates[i].y - (origin.y + camera.getComponent(Transform.class).y)) / camera.getComponent(Transform.class).scale;
        }

        switch (event.getType()) {
            case SELECT: {
                // Set previous Event
                previousEvent = event;

                break;
            }

            case HOLD: {

                // Set previous Event
                if (previousEvent != null) {
                    event.setPreviousEvent(previousEvent);
                } else {
                    event.setPreviousEvent(null);
                }
                previousEvent = event;

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

                // Set previous Event
                event.setPreviousEvent(previousEvent);
                previousEvent = event;

                break;
            }

            case UNSELECT: {

                // Set previous Event
                event.setPreviousEvent(previousEvent);
                previousEvent = event;

                break;
            }
        }

        setTargets(event);

        return event;
    }

    public Entity previousPrimaryTarget = null;

    private void setTargets(Event event) {

        Entity primaryTarget = null;

        // Handle special cases for SELECT and non-SELECT events
        if (event.getType() != Event.Type.SELECT) {
            event.setTarget(event.getFirstEvent().getTarget());
            event.setSecondaryTarget(event.getFirstEvent().getSecondaryTarget());
        } else {

            // Assign target Entities
            Group<Entity> primaryBoundaries = world.entities.get().filterVisibility(true).filterWithComponents(Model.class, Boundary.class).sortByLayer().filterContains(event.getPosition());
            Group<Entity> secondaryBoundaries = world.entities.get().filterVisibility(true).filterWithComponents(Primitive.class, Boundary.class).filterContains(event.getPosition());

            if (primaryBoundaries.size() > 0) {
                primaryTarget = primaryBoundaries.get(primaryBoundaries.size() - 1); // Get primary target from the top layer (will be last in the list of targets)
            } else {
                Group<Entity> cameras = world.entities.get().filterWithComponent(Camera.class);
                primaryTarget = cameras.get(0);
            }
            event.setTarget(primaryTarget);

            if (primaryTarget.hasComponent(Model.class)) { // Needed because entities like Camera without Model component are also processed here.
                for (int i = 0; i < secondaryBoundaries.size(); i++) {
                    if (Model.getShapes(primaryTarget).contains(secondaryBoundaries.get(i))) {
                        event.setSecondaryTarget(secondaryBoundaries.get(i));
                    }
                }
            }
        }

        // Handle special bookkeeping storing previous target Entity
        if (event.getType() == Event.Type.UNSELECT) {
            previousPrimaryTarget = event.getTarget();
        }
    }
}
