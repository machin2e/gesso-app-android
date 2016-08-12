package camp.computer.clay.visualization.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.visualization.util.Visibility;

public abstract class Shape {

    protected String label = null;

    protected Visibility visibility = Visibility.VISIBLE;

    protected Point position = new Point(0, 0);

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    protected double outlineThickness = 1.0;

    public Shape() {
    }

    public Shape(Point position) {
        this.position.set(position);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void setRotation(double angle) {
        this.position.setRotation(angle);
    }

    public double getRotation() {
        return this.position.getRotation();
    }

    abstract public List<Point> getVertices();

    /**
     * Returns the axis-aligned minimum bounding box for the set of vertices that define the shape.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">Minimum bounding box</a>
     *
     * @return A {@code Rectangle} representing the minimum bounding box.
     */
    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getVertices());
    }

//    public List<Point> getAbsoluteVertices() {
//
//        // Get absolute center position
//        Point rotatedPosition = null;
//        if (getPosition().getReferencePoint() != null) {
//            rotatedPosition = Geometry.calculatePoint(getPosition().getReferencePoint(), getPosition().getReferencePoint().getRotation() + Geometry.calculateRotationAngle(getPosition().getReferencePoint(), getPosition()), Geometry.calculateDistance(getPosition().getReferencePoint(), getPosition()));
//        } else {
//            rotatedPosition = new Point(getPosition());
//        }
//
////        // Calculate points before rotation
////        Rectangle boundingBox = getBoundingBox();
////        Point topLeft = new Point(rotatedPosition.getX() - (boundingBox.getWidth() / 2.0f), rotatedPosition.getY() - (boundingBox.getHeight() / 2.0f));
////        Point topRight = new Point(rotatedPosition.getX() + (boundingBox.getWidth() / 2.0f), rotatedPosition.getY() - (boundingBox.getHeight() / 2.0f));
////        Point bottomRight = new Point(rotatedPosition.getX() + (boundingBox.getWidth() / 2.0f), rotatedPosition.getY() + (boundingBox.getHeight() / 2.0f));
////        Point bottomLeft = new Point(rotatedPosition.getX() - (boundingBox.getWidth() / 2.0f), rotatedPosition.getY() + (boundingBox.getHeight() / 2.0f));
//
//        List<Point> shapeVertices = getVertices();
//        List<Point> rotatedVertices = new ArrayList<>();
//        for (int i = 0; i < shapeVertices.size(); i++) {
//            Point vertex = shapeVertices.get(i);
//            Point rotatedVertex = Geometry.calculatePoint(rotatedPosition, getRotation() + Geometry.calculateRotationAngle(rotatedPosition, vertex), Geometry.calculateDistance(rotatedPosition, vertex));
//            rotatedVertices.add(rotatedVertex);
//        }
//
////        // Calculate points after rotation
////        Point rotatedTopLeft = Geometry.calculatePoint(rotatedPosition, getRotation() + Geometry.calculateRotationAngle(rotatedPosition, topLeft), Geometry.calculateDistance(rotatedPosition, topLeft));
////        Point rotatedTopRight = Geometry.calculatePoint(rotatedPosition, getRotation() + Geometry.calculateRotationAngle(rotatedPosition, topRight), Geometry.calculateDistance(rotatedPosition, topRight));
////        Point rotatedBottomRight = Geometry.calculatePoint(rotatedPosition, getRotation() + Geometry.calculateRotationAngle(rotatedPosition, bottomRight), Geometry.calculateDistance(rotatedPosition, bottomRight));
////        Point rotatedBottomLeft = Geometry.calculatePoint(rotatedPosition, getRotation() + Geometry.calculateRotationAngle(rotatedPosition, bottomLeft), Geometry.calculateDistance(rotatedPosition, bottomLeft));
//
//        return rotatedVertices;
//    }

    abstract public List<Line> getSegments();

    public boolean containsPoint(Point point) {
        return Geometry.containsPoint(getVertices(), point);
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public boolean isVisible() {
        return visibility == Visibility.VISIBLE;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setOutlineColor(String color) {
        this.outlineColor = color;
    }

    public String getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineThickness(double thickness) {
        this.outlineThickness = thickness;
    }

    public double getOutlineThickness() {
        return outlineThickness;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean hasLabel() {
        return this.label != null && this.label.length() > 0;
    }

    public String getLabel() {
        return this.label;
    }
}
