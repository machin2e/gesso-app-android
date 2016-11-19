package camp.computer.clay.engine.manager;

import camp.computer.clay.engine.Event;

public abstract class EventHandler<T> {
    public abstract void execute(Event event);
}
