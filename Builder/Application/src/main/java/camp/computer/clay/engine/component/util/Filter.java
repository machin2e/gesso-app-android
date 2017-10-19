package camp.computer.clay.engine.component.util;

public interface Filter<V, D> {
    boolean filter(V entity, D data);
}
