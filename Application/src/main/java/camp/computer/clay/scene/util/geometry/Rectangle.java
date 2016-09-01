package camp.computer.clay.scene.util.geometry;

import java.util.ArrayList;
import java.util.List;

public class Rectangle extends Shape {

    private double width = 1.0;
    private double height = 1.0;

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
        ArrayList<Point> vertices = new ArrayList<>();
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

    public double getLeft() {
        return this.position.getX() - (width / 2.0f);
    }

    public double getTop() {
        return this.position.getY() - (height / 2.0f);
    }

    public double getRight() {
        return this.position.getX() + (width / 2.0f);
    }

    public double getBottom() {
        return this.position.getY() + (height / 2.0f);
    }

    public Point getTopLeft() {
        return new Point(getLeft(), getTop());
    }

    public Point getTopRight() {
        return new Point(getRight(), getTop());
    }

    public Point getBottomRight() {
        return new Point(getRight(), getBottom());
    }

    public Point getBottomLeft() {
        return new Point(getLeft(), getBottom());
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