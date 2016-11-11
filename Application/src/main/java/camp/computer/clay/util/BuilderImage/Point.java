package camp.computer.clay.util.BuilderImage;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.component.Boundary;
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
//        Boundary.shapeBoundaries.get(this).boundary = new ArrayList<>();
        Boundary.shapeBoundaries.get(this).add(new Transform());
    }

    @Override
    public List<Transform> getVertices() {
        List<Transform> vertices = new ArrayList<>();
        vertices.add(new Transform(imagePosition));
        return vertices;
    }
}
