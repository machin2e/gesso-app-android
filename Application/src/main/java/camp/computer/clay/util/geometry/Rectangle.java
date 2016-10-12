package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Rectangle<T extends Entity> extends Shape<T> {

    public double width = 1.0;

    public double height = 1.0;

    public double cornerRadius = 0.0;

    private ArrayList<Line> segments = new ArrayList<>();

    public Rectangle(T entity) {
        this.entity = entity;
        setup();
    }

    public Rectangle(double width, double height) {
        super();
        this.width = width;
        this.height = height;

        setup();
    }

    public Rectangle(double left, double top, double right, double bottom) {
        position.x = (right + left) / 2.0;
        position.y = (top + bottom) / 2.0;
        width = (right - left);
        height = (bottom - top);

        setup();
    }

    protected void setup() {
        setupGeometry();
    }

    private void setupGeometry() {

        // Create vertex Points (relative to the Shape)
        Point topLeft = new Point(0 - (width / 2.0), 0 - (height / 2.0));
        Point topRight = new Point(0 + (width / 2.0), 0 - (height / 2.0));
        Point bottomRight = new Point(0 + (width / 2.0), 0 + (height / 2.0));
        Point bottomLeft = new Point(0 - (width / 2.0), 0 + (height / 2.0));

        boundary.add(topLeft);
        boundary.add(topRight);
        boundary.add(bottomRight);
        boundary.add(bottomLeft);

        // Create segment Lines (relative to the Shape)
        Line top = new Line(topLeft, topRight);
        Line right = new Line(topRight, bottomRight);
        Line bottom = new Line(bottomRight, bottomLeft);
        Line left = new Line(bottomLeft, topLeft);

        segments.add(top);
        segments.add(right);
        segments.add(bottom);
        segments.add(left);
    }

    public List<Point> getBoundary() {
        return boundary;
    }

    @Override
    public List<Point> getVertices() {

        List<Point> vertices = new LinkedList<>();

        vertices.add(new Point(
                0 - (width / 2.0),
                0 - (height / 2.0)
        ));
        vertices.add(new Point(
                0 + (width / 2.0),
                0 - (height / 2.0)
        ));
        vertices.add(new Point(
                0 + (width / 2.0),
                0 + (height / 2.0)
        ));
        vertices.add(new Point(
                0 - (width / 2.0),
                0 + (height / 2.0)
        ));

        return vertices;
    }

    public List<Line> getSegments() {
        return segments;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;
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

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawRectangle(this);

            /*
            // Draw bounding box!
            display.paint.setColor(Color.GREEN);
            display.paint.setStyle(Paint.Style.STROKE);
            display.paint.setStrokeWidth(2.0f);
            display.drawPolygon(getBoundary());
            */
        }
    }
}