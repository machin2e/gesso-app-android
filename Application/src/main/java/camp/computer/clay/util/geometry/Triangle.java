package camp.computer.clay.util.geometry;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.image.Shape;

public class Triangle<T extends Entity> extends Shape<T> {

    private Point a = new Point(0, 0);
    private Point b = new Point(0, 0);
    private Point c = new Point(0, 0);

    // Cached descriptive {@code Point} geometry for the {@code Shape}.


    public Triangle(T entity) {
        this.entity = entity;
    }

    public Triangle(Point position) {
        super(position);
    }

    public void setPoints(double width, double height) {
        a = new Point(position.x + -(width / 2.0f), position.y + (height / 2.0f));
        b = new Point(position.x + 0, position.y - (height / 2.0f));
        c = new Point(position.x + (width / 2.0f), position.y + (height / 2.0f));
    }

    public void setPoints(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    protected List<Point> getVertices() {
        List<Point> vertices = new LinkedList<>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(c);
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawTriangle(this);
        }
    }
}