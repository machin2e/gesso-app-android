package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Rectangle<T extends Entity> extends Shape<T> {
    private double width = 1.0;

    private double height = 1.0;

    private double cornerRadius = 0.0;

    public Rectangle(T entity) {
        this.entity = entity;
    }

    public Rectangle(double width, double height) {
        super();
        this.width = width;
        this.height = height;
    }

    public Rectangle(Point position, double width, double height) {
        super(position);
        this.width = width;
        this.height = height;
    }

    public Rectangle(double left, double top, double right, double bottom) {
        super(new Point((right + left) / 2.0, (top + bottom) / 2.0));
        this.width = (right - left);
        this.height = (bottom - top);
    }

    public List<Point> getVertices() {
        List<Point> vertices = new ArrayList<>();
        vertices.add(getTopLeft());
        vertices.add(getTopRight());
        vertices.add(getBottomRight());
        vertices.add(getBottomLeft());
        return vertices;
    }

    public List<Line> getSegments() {
        ArrayList<Line> segments = new ArrayList<>();
        segments.add(new Line(getTopLeft(), getTopRight()));
        segments.add(new Line(getTopRight(), getBottomRight()));
        segments.add(new Line(getBottomRight(), getBottomLeft()));
        segments.add(new Line(getBottomLeft(), getTopLeft()));
        return segments;
    }

    public Line getNearestSegment(Point point) {
        double nearestDistance = Double.MAX_VALUE;
        Line nearestSegment = null;

        List<Line> segments = getSegments();
        for (int i = 0; i < segments.size(); i++) {
            double distanceToSegment = 0;
            if (i < (segments.size() - 1)) {
                distanceToSegment = Point.calculateDistance(point, segments.get(i).getSource()) + Point.calculateDistance(point, segments.get(i + 1).getTarget());
            } else {
                distanceToSegment = Point.calculateDistance(point, segments.get(i).getSource()) + Point.calculateDistance(point, segments.get(0).getTarget());
            }
            if (distanceToSegment < nearestDistance) {
                nearestDistance = distanceToSegment;
                nearestSegment = segments.get(i);
            }
        }

        return nearestSegment;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            Display.drawRectangle(this, display);
        }
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public Line getTop() {
        return new Line(getTopLeft(), getTopRight());
    }

    public Line getRight() {
        return new Line(getTopRight(), getBottomRight());
    }

    public Line getBottom() {
        return new Line(getBottomRight(), getBottomLeft());
    }

    public Line getLeft() {
        return new Line(getBottomLeft(), getTopLeft());
    }

    // TODO: Return a Number that updates when the Point coordinates update
    public double getRelativeLeft() {
        return 0 - (width / 2.0f);
    }

    // TODO: Return a Number that updates when the Point coordinates update
    public double getRelativeTop() {
        return 0 - (height / 2.0f);
    }

    public double getRelativeRight() {
        // TODO: Return a Number that updates when the Point coordinates update
//        return this.position.getRelativeX() + (width / 2.0f);
        return 0 + (width / 2.0f);
    }

    public double getRelativeBottom() {
        // TODO: Return a Number that updates when the Point coordinates update
//        return this.position.getRelativeY() + (height / 2.0f);
        return 0 + (height / 2.0f);
    }

    public Point getRelativeTopLeft() {
        return new Point(getRelativeLeft(), getRelativeTop());
    }

    public Point getRelativeTopRight() {
        return new Point(getRelativeRight(), getRelativeTop());
    }

    public Point getRelativeBottomRight() {
        return new Point(getRelativeRight(), getRelativeBottom());
    }

    public Point getRelativeBottomLeft() {
        return new Point(getRelativeLeft(), getRelativeBottom());
    }

    public Point getTopLeft() {
        return new Point(getRelativeLeft(), getRelativeTop(), position);
    }

    public Point getTopRight() {
        return new Point(getRelativeRight(), getRelativeTop(), position);
    }

    public Point getBottomRight() {
        return new Point(getRelativeRight(), getRelativeBottom(), position);
    }

    public Point getBottomLeft() {
        return new Point(getRelativeLeft(), getRelativeBottom(), position);
    }

    public double getArea() {
        return this.width * this.height;
    }

    public double getPerimeter() {
        return 2 * (this.width + this.height);
    }

    public double getDiagonalLength() {
        return (double) Math.sqrt(Math.pow(this.width, 2) + Math.pow(this.height, 2));
    }
}