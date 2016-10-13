package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Polyline<T extends Entity> extends Shape<T> {

    private List<Point> vertices = new ArrayList<>();

    public Polyline() {
        super();
        setup();
    }

    public Polyline(T entity) {
        this.entity = entity;
        setup();
    }

    public Polyline(Point position, List<Point> vertices) {
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

    public void setVertices(List<Point> vertices) {
        this.vertices.addAll(vertices);
    }

    public void addVertex(Point point) {
        this.vertices.add(point);
    }

    @Override
    protected List<Point> getVertices() {
        return vertices;
    }

    public List<Point> getPoints() {
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawPolyline(this);
        }
    }

}
