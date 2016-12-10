package camp.computer.clay.lib.Geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.component.Transform;

public class Polygon extends Shape {

    protected List<Transform> vertices = new ArrayList<>();

    public Polygon() {
    }

    public Polygon(Transform position, List<Transform> vertices) {
        super(position);
        this.vertices.addAll(vertices);
    }

    public Polygon(List<Transform> vertices) {
        super();
        this.vertices.addAll(vertices);
    }

    @Override
    public List<Transform> getVertices() {
        return vertices;
    }

}
