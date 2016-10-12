package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Polygon<T extends Entity> extends Shape<T> {

    protected List<Point> vertices = new ArrayList<>();

    public Polygon(T entity) {
        this.entity = entity;
        setup();
    }

    public Polygon(Point position, List<Point> vertices) {
        super(position);
        this.vertices.addAll(vertices);
        setup();
    }

    public Polygon(List<Point> vertices) {
        super();
        this.vertices.addAll(vertices);
        setup();
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {

    }

    @Override
    protected List<Point> getVertices() {
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawPolygon(this);
        }
    }

}
