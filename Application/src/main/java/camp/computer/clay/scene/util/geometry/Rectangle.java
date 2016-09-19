package camp.computer.clay.scene.util.geometry;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Feature;

public class Rectangle<T extends Feature> extends Shape<T> {

    private double width = 1.0;

    private double height = 1.0;

    public Rectangle(T feature) {
        this.feature = feature;
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

    // TODO: Delete?
    public List<Point> getRelativeVertices() {
        ArrayList<Point> vertices = new ArrayList<>();
        vertices.add(getRelativeTopLeft());
        vertices.add(getRelativeTopRight());
        vertices.add(getRelativeBottomRight());
        vertices.add(getRelativeBottomLeft());
        return vertices;
    }

    // TODO: Delete?
    public List<Line> getRelativeSegments() {
        ArrayList<Line> segments = new ArrayList<>();
        segments.add(new Line(getRelativeTopLeft(), getRelativeTopRight()));
        segments.add(new Line(getRelativeTopRight(), getRelativeBottomRight()));
        segments.add(new Line(getRelativeBottomRight(), getRelativeBottomLeft()));
        segments.add(new Line(getRelativeBottomLeft(), getRelativeTopLeft()));
        return segments;
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

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            Display.drawRectangle(this, display);

            display.getPaint().setColor(Color.GREEN);
//            Surface.drawRectangle(position, getRotation(), width, height, surface);
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

    public double getRelativeLeft() {
        return 0 - (width / 2.0f);
    }

    public double getRelativeTop() {
        return 0 - (height / 2.0f);
    }

    public double getRelativeRight() {
//        return this.position.getRelativeX() + (width / 2.0f);
        return 0 + (width / 2.0f);
    }

    public double getRelativeBottom() {
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