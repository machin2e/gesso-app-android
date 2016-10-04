package camp.computer.clay.util.geometry;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Rectangle<T extends Entity> extends Shape<T> {

    private double width = 1.0;

    private double height = 1.0;

    private double cornerRadius = 0.0;

    // <CACHED_OBJECTS>
    // </CACHED_OBJECTS>

    public Rectangle(T entity) {
        this.entity = entity;

        updateCache();
    }

    public Rectangle(double width, double height) {
        super();
        this.width = width;
        this.height = height;

        updateCache();
    }

    public Rectangle(Point position, double width, double height) {
        super(position);
        this.width = width;
        this.height = height;

        updateCache();
    }

    public Rectangle(double left, double top, double right, double bottom) {
        super(new Point((right + left) / 2.0, (top + bottom) / 2.0));
        this.width = (right - left);
        this.height = (bottom - top);

        // TODO: Needed? Probably.
//        updateCache();
    }

    // Cached descriptive {@code Point} geometry for the {@code Shape}.
    protected Point topLeft = new Point(getRelativeLeft(), getRelativeTop(), position);
    protected Point topRight = new Point(getRelativeRight(), getRelativeTop(), position);
    protected Point bottomRight = new Point(getRelativeRight(), getRelativeBottom(), position);
    protected Point bottomLeft = new Point(getRelativeLeft(), getRelativeBottom(), position);

    List<Point> vertices = new ArrayList<>(4);

    Point innerTopLeft = new Point(
            topLeft.getRelativeX() + getCornerRadius(),
            topLeft.getRelativeY() + getCornerRadius(),
            topLeft.getReferencePoint()
    );

    Point innerTopRight = new Point(
            topRight.getRelativeX() - getCornerRadius(),
            topRight.getRelativeY() + getCornerRadius(),
            topRight.getReferencePoint()
    );

    Point innerBottomRight = new Point(
            bottomRight.getRelativeX() - getCornerRadius(),
            bottomRight.getRelativeY() - getCornerRadius(),
            bottomRight.getReferencePoint()
    );

    Point innerBottomLeft = new Point(
            bottomLeft.getRelativeX() + getCornerRadius(),
            bottomLeft.getRelativeY() - getCornerRadius(),
            bottomLeft.getReferencePoint()
    );

    // Top segment
    public Point topSegmentSource = new Point(
            topLeft.getRelativeX() + getCornerRadius(),
            topLeft.getRelativeY(),
            getPosition()
    );

    public Point topSegmentTarget = new Point(
            topRight.getRelativeX() - getCornerRadius(),
            topRight.getRelativeY(),
            getPosition()
    );

    public List<Point> topRightArc = null; // Geometry.getArc(innerTopRight, getCornerRadius(), 270.0, 360.0, 10);

    // Right segment
    public Point rightSegmentSource = new Point(
            topRight.getRelativeX(),
            topRight.getRelativeY() + getCornerRadius(),
            getPosition()
    );

    public Point rightSegmentTarget = new Point(
            bottomRight.getRelativeX(),
            bottomRight.getRelativeY() - getCornerRadius(),
            getPosition()
    );

    public List<Point> bottomRightArc = null; // Geometry.getArc(innerBottomRight, getCornerRadius(), 0.0, 90.0, 10);

    // Bottom segment
    public Point bottomSegmentSource = new Point(
            bottomRight.getRelativeX() - getCornerRadius(),
            bottomRight.getRelativeY(),
            getPosition()
    );

    public Point bottomSegmentTarget = new Point(
            bottomLeft.getRelativeX() + getCornerRadius(),
            bottomLeft.getRelativeY(),
            getPosition()
    );

    public List<Point> bottomLeftArc = null; // Geometry.getArc(innerBottomLeft, getCornerRadius(), 90.0, 180.0, 10);

    // Left segment
    public Point leftSegmentSource = new Point(
            bottomLeft.getRelativeX(),
            bottomLeft.getRelativeY() - getCornerRadius(),
            getPosition()
    );

    public Point leftSegmentTarget = new Point(
            topLeft.getRelativeX(),
            topLeft.getRelativeY() + getCornerRadius(),
            getPosition()
    );

    public List<Point> topLeftArc = null; // Geometry.getArc(innerTopLeft, getCornerRadius(), 180.0, 270.0, 10);

    protected void updateCache() {

        Log.v("Geometry", "updateCache");

        // Update {@code vertices}
        if (vertices.size() < 4) {
            vertices.add(getTopLeft());
            vertices.add(getTopRight());
            vertices.add(getBottomRight());
            vertices.add(getBottomLeft());
        } else {
            vertices.set(0, getTopLeft());
            vertices.set(1, getTopRight());
            vertices.set(2, getBottomRight());
            vertices.set(3, getBottomLeft());
        }

        // Rounded Corner Geometry

        innerTopLeft.setRelative(
                topLeft.relativeX + this.cornerRadius,
                topLeft.relativeY + this.cornerRadius
        );

        innerTopRight.setRelative(
                topRight.relativeX - this.cornerRadius,
                topRight.relativeY + this.cornerRadius
        );

        innerBottomRight.setRelative(
                bottomRight.relativeX - this.cornerRadius,
                bottomRight.relativeY - this.cornerRadius
        );

        innerBottomLeft.setRelative(
                bottomLeft.relativeX + this.cornerRadius,
                bottomLeft.relativeY - this.cornerRadius
        );

        // Top segment
        topSegmentSource.setRelative(
                topLeft.relativeX + this.cornerRadius,
                topLeft.relativeY
        );

        topSegmentTarget.setRelative(
                topRight.relativeX - this.cornerRadius,
                topRight.relativeY
        );

        topRightArc = Geometry.getArc(innerTopRight, this.cornerRadius, 270.0, 360.0, 10);

        // Right segment
        rightSegmentSource.setRelative(
                topRight.relativeX,
                topRight.relativeY + this.cornerRadius
        );

        rightSegmentTarget.setRelative(
                bottomRight.relativeX,
                bottomRight.relativeY - this.cornerRadius
        );

        bottomRightArc = Geometry.getArc(innerBottomRight, this.cornerRadius, 0.0, 90.0, 10);

        // Bottom segment
        bottomSegmentSource.setRelative(
                bottomRight.relativeX - this.cornerRadius,
                bottomRight.relativeY
        );

        bottomSegmentTarget.setRelative(
                bottomLeft.relativeX + this.cornerRadius,
                bottomLeft.relativeY
        );

        bottomLeftArc = Geometry.getArc(innerBottomLeft, this.cornerRadius, 90.0, 180.0, 10);

        // Left segment
        leftSegmentSource.setRelative(
                bottomLeft.relativeX,
                bottomLeft.relativeY - this.cornerRadius
        );

        leftSegmentTarget.setRelative(
                topLeft.relativeX,
                topLeft.relativeY + this.cornerRadius
        );

        topLeftArc = Geometry.getArc(innerTopLeft, this.cornerRadius, 180.0, 270.0, 10);
    }

    public List<Point> getVertices() {
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
                distanceToSegment = Geometry.calculateDistance(point, segments.get(i).getSource()) + Geometry.calculateDistance(point, segments.get(i + 1).getTarget());
            } else {
                distanceToSegment = Geometry.calculateDistance(point, segments.get(i).getSource()) + Geometry.calculateDistance(point, segments.get(0).getTarget());
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

        updateCache();
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;

        updateCache();
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;

        updateCache();
    }

//    public Line getTop() {
//        return new Line(getTopLeft(), getTopRight());
//    }
//
//    public Line getRight() {
//        return new Line(getTopRight(), getBottomRight());
//    }
//
//    public Line getBottom() {
//        return new Line(getBottomRight(), getBottomLeft());
//    }
//
//    public Line getLeft() {
//        return new Line(getBottomLeft(), getTopLeft());
//    }

    public double getLeft() {
        return position.getX() - (width / 2.0f);
    }

    public double getTop() {
        return position.getY() - (height / 2.0f);
    }

    public double getRight() {
        return position.getX() + (width / 2.0f);
    }

    public double getBottom() {
        return position.getY() + (height / 2.0f);
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

    public Point getTopLeft() {
        //return new Point(getRelativeLeft(), getRelativeTop(), position);
        topLeft.setRelative(
                0 - (width / 2.0), // getRelativeLeft(),
                0 - (height / 2.0) // getRelativeTop()
        );
        return topLeft;
    }

    public Point getTopRight() {
        //return new Point(getRelativeRight(), getRelativeTop(), position);
        topRight.setRelative(
                0 + (width / 2.0), // getRelativeRight(),
                0 - (height / 2.0) // getRelativeTop()
        );
        return topRight;
    }

    public Point getBottomRight() {
        //return new Point(getRelativeRight(), getRelativeBottom(), position);
        bottomRight.setRelative(
                0 + (width / 2.0), // getRelativeRight(),
                0 + (height / 2.0) // getRelativeBottom()
        );
        return bottomRight;
    }

    public Point getBottomLeft() {
        //return new Point(getRelativeLeft(), getRelativeBottom(), position);
        bottomLeft.setRelative(
                0 - (width / 2.0), // getRelativeLeft(),
                0 + (height / 2.0) // getRelativeBottom()
        );
        return bottomLeft;
    }
}