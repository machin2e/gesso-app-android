package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Event;

public class EventSystem extends System {

    private List<Event> eventQueue = new ArrayList<>();

    public EventSystem(World world) {
        super(world);
    }

    public void update(long dt) {
        while (eventQueue.size() > 0) {
            dispatch(dequeue());
        }
    }

    public void queue(Event event) {
        eventQueue.add(event);
    }

    /**
     * Dequeues element
     *
     * @return
     */
    private Event dequeue() {
        return eventQueue.remove(0);
    }

    /**
     * Dispatch the {@code Event} to subscribers.
     *
     * @param event
     */
    private void dispatch(Event event) {
        Entity eventTarget = event.getTarget();
        if (eventTarget != null) {
            world.events.dispatch(event);
        }
    }
}
