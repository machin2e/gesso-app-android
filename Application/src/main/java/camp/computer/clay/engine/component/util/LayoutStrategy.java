package camp.computer.clay.engine.component.util;

import java.util.List;

public interface LayoutStrategy<T> {
    void execute(List<T> elements);
}
