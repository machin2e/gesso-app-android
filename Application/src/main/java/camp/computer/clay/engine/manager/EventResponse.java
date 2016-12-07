package camp.computer.clay.engine.manager;

import camp.computer.clay.engine.event.Event;

public abstract class EventResponse<T> {
    public abstract void execute(Event event);
}
