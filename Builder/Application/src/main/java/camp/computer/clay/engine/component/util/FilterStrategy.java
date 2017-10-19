package camp.computer.clay.engine.component.util;

/**
 * Created by mokogobo on 12/11/2016.
 */

public class FilterStrategy {
    private Filter filter;
    private Object data;

    public <D> FilterStrategy(Filter filter, D... data) {
        this.filter = filter;
        this.data = data;
    }

    public Filter getFilter() {
        return filter;
    }

    public Object getData() {
        return data;
    }
}
