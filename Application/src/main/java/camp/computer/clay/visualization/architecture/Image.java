package camp.computer.clay.visualization.architecture;

import android.graphics.Rect;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.architecture.Model;
import camp.computer.clay.model.interactivity.Impression;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;
import camp.computer.clay.visualization.util.geometry.Shape;
import camp.computer.clay.visualization.util.Visibility;

public abstract class Image {

    protected List<Shape> shapes = new LinkedList<>();

    protected Point position = new Point(); // Image position

    // TODO: Move scale into list of points defining shape. Draw on "unit canvas (scale 1.0)", and set scale. Recomputing happens automatically!
    protected double scale = 1.0f; // Image scale factor

    protected double angle = 0.0f; // Image heading rotation

    protected double targetTransparency = 1.0;
    protected double currentTransparency = targetTransparency;

    protected Visibility visibility = Visibility.VISIBLE;

    // TODO: Replace with Body (touching it) or Body's Finger (or whatever Pointer, whatever).
    public boolean isTouched = false;

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
        return angle;
    }

    public double getAbsoluteRotation() {
        double absoluteRotation = 0;
        Image parentImage = getParentImage();
        if (parentImage != null) {
            absoluteRotation = parentImage.getAbsoluteRotation() + getRotation();
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
    public void setRelativePosition(Point position) {
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
        this.angle = angle;
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

    public abstract void draw(VisualizationSurface visualizationSurface);

    public abstract boolean containsPoint(Point point);

    public abstract boolean containsPoint(Point point, double padding);

    public interface ActionListener {
    }

    public abstract void onImpression(Impression impression);

    // TODO: change this to addOnTouchListener (since have abstract onImpression)... and call at end of that
    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void apply(Impression impression) {
        onImpression(impression);
    }

    public void setTransparency(final double transparency) {
        targetTransparency = transparency;
        currentTransparency = targetTransparency;
    }

    public Rectangle getBoundingRectangle() {

        List<Point> pointList = new LinkedList<>();

        for (Shape shape : shapes) {
            pointList.addAll(shape.getVertices());
        }

        Log.v("Bounds", "vertex #: " + pointList.size());

//        return Geometry.calculateBoundingBox(pointList);

        return new Rectangle(250, 250);

    }

}
