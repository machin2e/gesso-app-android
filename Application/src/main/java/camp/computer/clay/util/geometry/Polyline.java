package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.Shape;

public class Polyline<T extends Entity> extends Shape<T> {

    private List<Transform> vertices = new ArrayList<>();

    public Polyline() {
        super();
        setup();
    }

    public Polyline(T entity) {
        this.entity = entity;
        setup();
    }

    public Polyline(Transform position, List<Transform> vertices) {
        super(position);
        setup();
        setVertices(vertices);
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        this.vertices.addAll(vertices);
    }

    public void setVertices(List<Transform> vertices) {
        this.vertices.addAll(vertices);
    }

    public void addVertex(Transform point) {
        this.vertices.add(point);
    }

    @Override
    protected List<Transform> getVertices() {
        return vertices;
    }

    public List<Transform> getPoints() {
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawPolyline(this);
        }
    }

}
