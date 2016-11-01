package camp.computer.clay.engine.system;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import camp.computer.clay.application.Application;
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
                break;
            }

            case HOLD: {

                // <HACK?>
                // There might be a better way to do this. How can I assign reasonable coordinates to the synthetic HOLD event?
                // TODO: Set coordinates of hold... to first event?
                Event firstEvent = previousEvent.getFirstEvent();
                for (int i = 0; i < firstEvent.pointerCoordinates.length; i++) {
                    event.pointerCoordinates[i].x = firstEvent.pointerCoordinates[i].x;
                    event.pointerCoordinates[i].y = firstEvent.pointerCoordinates[i].y;
                }
                // </HACK?>

                break;
            }

            case MOVE: {
                break;
            }

            case UNSELECT: {
                break;
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
