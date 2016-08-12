package camp.computer.clay.visualization.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Construct;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.model.interactivity.ActionListener;
import camp.computer.clay.visualization.util.Color;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;
import camp.computer.clay.visualization.util.geometry.Shape;
import camp.computer.clay.visualization.util.Visibility;

public abstract class Image {

    protected List<Shape> shapes = new LinkedList<>();

    protected Point position = new Point(0, 0); // Image position

    protected double scale = 1.0; // Image scale factor

    protected double rotation = 0.0; // Image heading rotation

    protected Visibility visibility = Visibility.VISIBLE;

    protected double targetTransparency = 1.0;

    protected double transparency = targetTransparency;

    protected Construct construct = null;

    protected Visualization visualization = null;

    // TODO: Make this an interface? Move interface out of class.
    protected ActionListener actionListener;

    public Image(Construct construct) {
        this.construct = construct;
    }

    public Construct getConstruct() {
        return this.construct;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public Image getParentImage() {
        if (getConstruct().hasParent()) {
            Construct parentConstruct = getConstruct().getParent();
            return getVisualization().getImage(parentConstruct);
        }
        return null;
    }

    public Point getPosition() {
        return this.position;
    }

    public double getRotation() {
        return rotation;
    }

    public double old_getAbsoluteRotation() {
        double absoluteRotation = 0;
        Image parentImage = getParentImage();
        if (parentImage != null) {
            absoluteRotation = parentImage.old_getAbsoluteRotation() + getRotation();
        } else {
            return getRotation();
        }
        return absoluteRotation;
    }

    public double getScale() {
        return this.scale;
    }

    public void setPosition(Point position) {
        this.position.set(position.getX(), position.getY());
    }

    /**
     * Absolute position calculated from relative position.
     */
    public void old_setRelativePosition(Point position) {
        Point absolutePosition = new Point();
        Image parentImage = getParentImage();
        if (parentImage != null) {
            Point relativePositionFromRelativePosition = Geometry.calculatePoint(
                    parentImage.getPosition(),
                    Geometry.calculateRotationAngle(parentImage.getPosition(), position),
                    Geometry.calculateDistance(parentImage.getPosition(), position)
            );
            absolutePosition.setX(parentImage.getPosition().getX() + relativePositionFromRelativePosition.getX());
            absolutePosition.setY(parentImage.getPosition().getY() + relativePositionFromRelativePosition.getY());
        } else {
            // TODO: This should get the absolute position of the root sprite relative to the origin point on the coordinate system/canvas
            absolutePosition.setX(position.getX());
            absolutePosition.setY(position.getY());
        }
        this.position.setX(absolutePosition.getX());
        this.position.setY(absolutePosition.getY());
    }

    public void setRotation(double angle) {
        this.rotation = angle;
        this.position.setRotation(angle);
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean isVisible() {
        return visibility == Visibility.VISIBLE;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void addShape(Shape shape) {
        shape.getPosition().setReferencePoint(getPosition());
        shapes.add(shape);
    }

    public Shape getShape(int index) {
        return shapes.get(index);
    }

    public Shape getShape(String label) {
        for (Shape shape : shapes) {
            if (shape.getLabel().equals(label)) {
                return shape;
            }
        }
        return null;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public Shape removeShape(int index) {
        return shapes.remove(index);
    }

    public abstract void update();

    public abstract void draw(Surface surface);

    public abstract boolean containsPoint(Point point);

    public abstract boolean containsPoint(Point point, double padding);

    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void processAction(Action action) {
        if (actionListener != null) {
            actionListener.onAction(action);
        }
    }

    public void setTransparency(final double transparency) {
        this.targetTransparency = transparency;

        for (int i = 0; i < shapes.size(); i++) {
            // Color
            int intColor = android.graphics.Color.parseColor(shapes.get(i).getColor());
            intColor = Color.setTransparency(intColor, this.targetTransparency);
            shapes.get(i).setColor(Color.getHexColorString(intColor));

            // Outline Color
            int outlineColorIndex = android.graphics.Color.parseColor(shapes.get(i).getOutlineColor());
            outlineColorIndex = Color.setTransparency(outlineColorIndex, this.targetTransparency);
            shapes.get(i).setOutlineColor(Color.getHexColorString(outlineColorIndex));
        }

        this.transparency = this.targetTransparency;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (Shape shape : shapes) {
            positions.addAll(shape.getVertices());
        }
        return positions;
    }

    public List<Point> getAbsoluteVertices() {
        List<Point> positions = new LinkedList<>();
        for (Shape shape : shapes) {
            // positions.addAll(shape.getVertices());
            for (Point shapeVertex : shape.getVertices()) {

                // Rotate shape about its center point
                Point absoluteVertex = Geometry.calculateRotatedPoint(shape.getPosition(), shape.getRotation(), shapeVertex);

                // Rotate shape vertices about the shape's reference point
                Point referencePoint = position;
                absoluteVertex = Geometry.calculateRotatedPoint(referencePoint, getRotation(), absoluteVertex);
                positions.add(absoluteVertex);
//                positions.add(vertex);
            }
        }
        return positions;
    }

    public Rectangle getBoundingBox() {

        List<Point> pointList = new LinkedList<>();

        for (Shape shape : shapes) {
            pointList.addAll(shape.getVertices());
        }

        return Geometry.calculateBoundingBox(pointList);
    }

}
