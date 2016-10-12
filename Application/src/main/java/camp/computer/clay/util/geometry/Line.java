package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Line<T extends Entity> extends Shape<T> {

    protected List<Point> endpoints = new ArrayList<>();

    public Line() {
        super();
    }

    public Line(T entity) {
        super(entity);
    }

    public Line(Point source, Point target) {
        endpoints.clear();
        endpoints.add(source);
        endpoints.add(target);
    }

    @Override
    protected List<Point> getVertices() {
        return endpoints;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawLine(this);
        }
    }

    public List<Point> getPoints() {
        return endpoints;
    }

    public Point getSource() {
        return this.endpoints.get(0);
    }

    public void setSource(Point source) {
        this.endpoints.get(0).set(source);
    }

    public Point getTarget() {
        return this.endpoints.get(1);
    }

    public void setTarget(Point target) {
        this.endpoints.get(1).set(target);
    }
}
