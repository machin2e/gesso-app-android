package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.component.Transform;

public class Point extends Shape {

    public Point() {
        setup();
    }

    public Point(Transform position) {
        super(position);
        setup();
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        boundary = new ArrayList<>();
        boundary.add(new Transform());
    }

    @Override
    public List<Transform> getVertices() {
        List<Transform> vertices = new ArrayList<>();
        vertices.add(new Transform(imagePosition));
        return vertices;
    }
}
