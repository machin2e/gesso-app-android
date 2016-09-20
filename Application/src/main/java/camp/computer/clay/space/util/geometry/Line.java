package camp.computer.clay.space.util.geometry;

import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Entity;

public class Line<T extends Entity> extends Shape<T> {
    private Point source = new Point(0, 0);
    private Point target = new Point(0, 0);

    public Line () {}

    public Line(T entity) {
        this.entity = entity;
    }

    @Override
    public List<Point> getVertices() {
        return null;
    }

    @Override
    public List<Line> getSegments() {
        return null;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            Display.drawLine(this, display);
        }
    }

    public Line (Point source, Point target) {
        this.source = source;
        this.target = target;
    }

    public Point getSource() {
        return this.source;
    }

    public Point getTarget() {
        return this.target;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public double getLength() {
        return Geometry.calculateDistance(source, target);
    }
}
