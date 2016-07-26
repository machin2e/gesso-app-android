package camp.computer.clay.viz.arch;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.sim.Model;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Point;

public class Image<T extends Model> {

    public static int INVISIBLE = 0;
    public static int VISIBLE = 1;

    private String type = "Image";

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isType(String... types) {
        if (types != null) {
            for (String type : types) {
                if (this.type.equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }


    private Viz viz;

    private T model;

    /**
     * Position of the image. The image's shapes are rendered relative to this point.
     */
    protected Point position = new Point();

    protected List<Shape> shapes = new ArrayList<>();

    // TODO: Move scale into list of points defining shape. Draw on "unit canvas (scale 1.0)", and set scale. Recomputing happens automatically!
    private double scale = 1.0f; // Image scale factor

    private double angle = 0.0f; // Image heading rotation

    protected Visibility visibility = Visibility.VISIBLE;

    private OnDrawListener onDrawListener;

    // TODO: Replace with Body (touching it) or Body's Finger (or whatever Pointer, whatever).
    public boolean isTouched = false;

    private OnTouchActionListener onTouchActionListener;

    public Image(T model) {
        this.model = model;
    }

    public T getModel() {
        return this.model;
    }

    public void setViz(Viz viz) {
        this.viz = viz;
    }

    public Viz getViz() {
        return this.viz;
    }

    public Image getParentImage() {
        if (getModel().hasParent()) {
            Model parentModel = getModel().getParent();
            return getViz().getImage(parentModel);
        }
        return null;
    }

    public Point getPosition() {
        return this.position;
    }

    public double getRotation() {
        return angle;
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

    // TODO: Delete this!
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
        Log.v("Touch", "\tImage.setPosition: " + position.getX() + ", " + position.getY());
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

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public boolean isVisible() {
        return visibility == Visibility.VISIBLE;
    }

    public void generate() {
    }

    public void draw(Viz viz) {
        if (onDrawListener != null) {
            onDrawListener.onUpdate(viz);
            onDrawListener.onDraw(viz);
        }
    }

    // TODO: Also rotate the point when checking touch!
    public boolean isTouching(Point point) {

        if (isVisible()) {

            //Log.v("Touching", "Image: " + point.getX() + ", " + point.getY() + " isTouching " + getPosition().getX() + ", " + getPosition().getY());

//            Point offsetPoint = new Point(
//                    point.getX() - getPosition().getX(),
//                    point.getY() - getPosition().getY()
//            );

            for (int i = 0; i < shapes.size(); i++) {
//                if (shapes.get(i).isTouching(offsetPoint)) {
                if (shapes.get(i).isTouching(point)) {
                    Log.v("Touch", "Image.isTouching: " + shapes.get(i).getPosition().getX() + ", " + shapes.get(i).getPosition().getY());
                    return true;
                }
            }
        }
        return false;
    }

    // TODO: Delete this!
    public void onTouchInteraction(TouchInteraction touchInteraction) {

    }

//    // TODO: change this to addOnTouchListener (since have abstract onTouchInteraction)... and call at end of that
//    public void setOnTouchActionListener(OnTouchActionListener onTouchActionListener) {
//        this.onTouchActionListener = onTouchActionListener;
//    }

    public void touch(TouchInteraction touchInteraction) {

//        Point offsetPoint = new Point(
//                touchInteraction.getPosition().getX() + getPosition().getX(),
//                touchInteraction.getPosition().getY() + getPosition().getY()
//        );

//        touchInteraction.getPosition().set(offsetPoint);

        Log.v("Touch", "Image.touchInteraction: " + touchInteraction.getPosition().getX() + ", " + touchInteraction.getPosition().getY());
        Log.v("Touch", "Image.touch: " + getPosition().getX() + ", " + getPosition().getY());

//        Point offsetPoint = new Point(
//                touchInteraction.getPosition().getX() - getPosition().getX(),
//                touchInteraction.getPosition().getY() - getPosition().getY()
//        );

        for (Shape shape : shapes) {
            if (shape.isTouching(touchInteraction.getPosition())) {
//            if (shape.isTouching(offsetPoint)) {
                shape.touch(touchInteraction);
            }
        }

        onTouchInteraction(touchInteraction);
    }

    public void setOnDrawListener(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }

}
