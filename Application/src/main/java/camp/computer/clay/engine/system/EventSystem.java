package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.event.Event;

public class EventSystem extends System {

//    private List<Event> eventQueue = new ArrayList<>();

    public EventSystem(World world) {
        super(world);
    }

    public void update(long dt) {
//        while (world.eventQueue.size() > 0) {
//            dispatch(dequeue());
//        }

        while (world.nextEventIndex < world.eventQueue.size()) {
            dispatch(world.eventQueue.get(world.nextEventIndex));
            world.nextEventIndex++;
        }
    }

    public void queue(Event event) {
        world.eventQueue.add(event);
    }

    public void execute(Event event) {
        world.eventManager.dispatch(event);
    }

    /**
     * Dequeues element
     *
     * @return
     */
    private Event dequeue() {
        return world.eventQueue.remove(0);
    }

    /**
     * Dispatch the {@code Event} to subscribers.
     *
     * @param event
     */
    private void dispatch(Event event) {
//        Entity eventTarget = event.getTarget();
//        if (eventTarget != null) {
        world.eventManager.dispatch(event);
//        }
    }
}
