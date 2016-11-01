package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.image.World;

public class InputSystem extends System {

    private Event previousEvent = null;

    private List<Event> incomingEvents = new ArrayList<>();

    public InputSystem() {
    }

    public boolean update(World world) {

        while (incomingEvents.size() > 0) {
            Event event = dequeueEvent();
            Event processedEvent = processEvent(event);

            world.eventHandlerSystem.queueEvent(processedEvent);
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
                previousEvent = null;
            }

            case HOLD: {
            }

            case MOVE: {
            }

            case UNSELECT: {
            }
        }

        // Set previous Event
        if (previousEvent != null) {
            event.previousEvent = previousEvent;
        } else {
            event.previousEvent = null;
        }
        previousEvent = event;

        return event;
    }
}
