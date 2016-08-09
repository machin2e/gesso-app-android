package camp.computer.clay.visualization.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Model;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;
import camp.computer.clay.visualization.util.geometry.Shape;
import camp.computer.clay.visualization.util.Visibility;

public abstract class Image {

    protected List<Shape> shapes = new LinkedList<>();

    protected Point position = new Point(); // Image position

    protected double scale = 1.0f; // Image scale factor

    protected double rotation = 0.0f; // Image heading rotation

    protected Visibility visibility = Visibility.VISIBLE;

    protected double targetTransparency = 1.0;

    protected double currentTransparency = targetTransparency;

    protected Model model;

    protected Visualization visualization;

    // TODO: Make this an interface? Move interface out of class.
    protected ActionListener actionListener;

    public Image(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public Image getParentImage() {
        if (getModel().hasParent()) {
            Model parentModel = getModel().getParent();
            return getVisualization().getImage(parentModel);
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
        this.position.setAngle(angle);
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

    public interface ActionListener {
    }

    public abstract void onAction(Action action);

    // TODO: change this to addOnTouchListener (since have abstract onAction)... and call at end of that
    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void processAction(Action action) {
        onAction(action);
    }

    public void setTransparency(final double transparency) {
        this.targetTransparency = transparency;
        this.currentTransparency = transparency;
    }

    public Rectangle getBoundingRectangle() {

        List<Point> pointList = new LinkedList<>();

        for (Shape shape : shapes) {
            pointList.addAll(shape.getVertices());
        }

//        Log.v("Bounds", "vertex #: " + pointList.size());

//        return Geometry.calculateBoundingBox(pointList);

        return new Rectangle(250, 250);

    }

}
