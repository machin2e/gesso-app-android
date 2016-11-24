package camp.computer.clay.engine.manager;

public abstract class EventHandler<T> {
    public abstract void execute(Event event);
}
