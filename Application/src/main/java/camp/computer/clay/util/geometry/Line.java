package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Line<T extends Entity> extends Shape<T> {

    protected Point source = new Point();
    protected Point target = new Point();

    public Line() {
    }

    public Line(T entity) {
        this.entity = entity;
    }

    @Override
    public List<Point> getVertices() {
        List<Point> vertices = new ArrayList<>();
        vertices.add(getSource());
        vertices.add(getTarget());
        return vertices;
    }

    @Override
    public List<Line> getSegments() {
        ArrayList<Line> segments = new ArrayList<>();
        segments.add(new Line(getSource(), getTarget()));
        return segments;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawLine(this);
        }
    }

    public Line(Point source, Point target) {
        this.source = source;
        this.target = target;
    }

    public Point getSource() {
        return this.source;
    }

    public void setSource(Point source) {
        //this.source = source;
        this.source.set(source);
    }

    public Point getTarget() {
        return this.target;
    }

    public void setTarget(Point target) {
        //this.target = target;
        this.target.set(target);
    }

    public double getLength() {
        return Geometry.calculateDistance(source, target);
    }

    public Point getMidpoint() {
        return Geometry.calculateMidpoint(getSource(), getTarget());
    }
}
