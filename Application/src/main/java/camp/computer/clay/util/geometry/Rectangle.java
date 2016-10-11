package camp.computer.clay.util.geometry;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Rectangle<T extends Entity> extends Shape<T> {

    public double width = 1.0;

    public double height = 1.0;

    public double cornerRadius = 0.0;

    // <CACHED_OBJECTS>
    // TODO: Move caching framework into superclass
    // Cached descriptive {@code Point} geometry for the {@code Shape}.
//    protected Point topLeft = new Point(0, 0);
//    protected Point topRight = new Point(0, 0);
//    protected Point bottomRight = new Point(0, 0);
//    protected Point bottomLeft = new Point(0, 0);

    List<Point> vertices = new ArrayList<>();

    ArrayList<Line> segments = new ArrayList<>();
    // </CACHED_OBJECTS>

    public Rectangle(T entity) {
        this.entity = entity;

//        updateCache();
        setup();
    }

    public Rectangle(double width, double height) {
        super();
        this.width = width;
        this.height = height;

        setup();

//        updateCache();
    }

    public Rectangle(Point position, double width, double height) {
        super(position);
        this.width = width;
        this.height = height;

        setup();

//        updateCache();
    }

    public Rectangle(double left, double top, double right, double bottom) {
        super(new Point((right + left) / 2.0, (top + bottom) / 2.0));
        this.width = (right - left);
        this.height = (bottom - top);

        setup();

        // TODO: Needed? Probably.
//        updateCache();
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

        vertices.add(topLeft);
        vertices.add(topRight);
        vertices.add(bottomRight);
        vertices.add(bottomLeft);

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

//    protected void updateCache() {
//
//        // Update vertices
//        if (vertices.size() == 0) {
//            vertices.add(getTopLeft());
//            vertices.add(getTopRight());
//            vertices.add(getBottomRight());
//            vertices.add(getBottomLeft());
//        } else {
////            vertices.set(0, getTopLeft());
////            vertices.set(1, getTopRight());
////            vertices.set(2, getBottomRight());
////            vertices.set(3, getBottomLeft());
//        }
//
//        // Update segments
//        if (segments.size() == 0) {
//            segments.add(new Line(topLeft, topRight));
//            segments.add(new Line(topRight, bottomRight));
//            segments.add(new Line(bottomRight, bottomLeft));
//            segments.add(new Line(bottomLeft, topLeft));
//        }
//    }

    public List<Point> getVertices() {
        return vertices;
    }

    @Override
    public List<Point> temp_getRelativeVertices() {
        vertices.get(0).set(
                0 - (width / 2.0),
                0 - (height / 2.0)
        );
        vertices.get(1).set(
                0 + (width / 2.0),
                0 - (height / 2.0)
        );
        vertices.get(2).set(
                0 + (width / 2.0),
                0 + (height / 2.0)
        );
        vertices.get(3).set(
                0 - (width / 2.0),
                0 + (height / 2.0)
        );
        return vertices;
    }

    public List<Line> getSegments() {
        return segments;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawRectangle(this);

            // Draw bounding box!
            display.paint.setColor(Color.GREEN);
            display.paint.setStyle(Paint.Style.STROKE);
            display.paint.setStrokeWidth(2.0f);
            display.drawPolygon(getVertices());
        }
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;

//        updateCache();
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;

//        updateCache();
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;

//        updateCache();
    }

//    // TODO: Return a Number that updates when the Point coordinates update
//    public double getRelativeLeft() {
//        return 0 - (width / 2.0f);
//    }
//
//    // TODO: Return a Number that updates when the Point coordinates update
//    public double getRelativeTop() {
//        return 0 - (height / 2.0f);
//    }
//
//    public double getRelativeRight() {
//        // TODO: Return a Number that updates when the Point coordinates update
////        return this.position.getX() + (width / 2.0f);
//        return 0 + (width / 2.0f);
//    }
//
//    public double getRelativeBottom() {
//        // TODO: Return a Number that updates when the Point coordinates update
////        return this.position.getY() + (height / 2.0f);
//        return 0 + (height / 2.0f);
//    }

//    public Point getTopLeft() {
//        topLeft.set(
//                0 - (width / 2.0), // getRelativeLeft(),
//                0 - (height / 2.0) // getRelativeTop()
//        );
//        return topLeft;
//    }
//
//    public Point getTopRight() {
//        topRight.set(
//                0 + (width / 2.0), // getRelativeRight(),
//                0 - (height / 2.0) // getRelativeTop()
//        );
//        return topRight;
//    }
//
//    public Point getBottomRight() {
//        bottomRight.set(
//                0 + (width / 2.0), // getRelativeRight(),
//                0 + (height / 2.0) // getRelativeBottom()
//        );
//        return bottomRight;
//    }
//
//    public Point getBottomLeft() {
//        bottomLeft.set(
//                0 - (width / 2.0), // getRelativeLeft(),
//                0 + (height / 2.0) // getRelativeBottom()
//        );
//        return bottomLeft;
//    }
}