package camp.computer.clay.engine.event;

public abstract class EventResponse<T> {
    public abstract void execute(Event event);
}