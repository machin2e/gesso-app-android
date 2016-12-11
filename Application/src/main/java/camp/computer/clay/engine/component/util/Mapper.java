package camp.computer.clay.engine.component.util;

/**
 * Interface for custom map functions.
 *
 * @param <E> "Input" group element type.
 * @param <M> "Result" group element type.
 * @param <D> Type of data to pass to the {@code Mapper}. Set to {@code Void} if there's no
 *            data.
 */
public interface Mapper<E, M, D> {
    M map(E value, D data);
}
