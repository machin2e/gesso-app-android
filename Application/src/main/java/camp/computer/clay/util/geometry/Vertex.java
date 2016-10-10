package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Vertex<T extends Entity> extends Shape<T> {

    public Vertex() {
    }

    public Vertex(T entity) {
        this.entity = entity;
    }

    @Override
    public List<Point> getVertices() {
        List<Point> vertices = new ArrayList<>();
        vertices.add(getPosition());
        return vertices;
    }

    @Override
    public List<Line> getSegments() {
        return null;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            // display.drawVertex(this);
        }
    }

    public Vertex(Point position) {
        super(position);
    }

    public void setX(double x) {
        this.position.x = x;
    }

    public void setY(double y) {
        this.position.y = y;
    }
}
