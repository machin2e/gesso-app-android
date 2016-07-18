package camp.computer.clay.visualization.arch;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Model;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.PointHolder;

public abstract class Image {

    // <TYPE_INTERFACE>
    private String type = "Image";

    public String getType () {
        return this.type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public boolean isType (String... types) {
        for (String type: types) {
            if (this.type.equals(type)) {
                return true;
            }
        }
        return false;
    }
    // <TYPE_INTERFACE>

//    // <TAG_INTERFACE>
//    private List<String> tags = new ArrayList<>();
//
//    public List<String> getTags () {
//        return this.tags;
//    }
//
//    public void addTag (String tag) {
//        this.tags.add(tag);
//    }
//    // <TAG_INTERFACE>

    // TODO: Group of points to represent geometric objects, even circles. Helper functions for common shapes. Gives generality.

    private PointHolder position = new PointHolder(); // Image position

    // TODO: Move scale into list of points defining shape. Draw on "unit canvas (scale 1.0)", and set scale. Recomputing happens automatically!
    private double scale = 1.0f; // Image scale factor
//    private double rotation = 0.0f; // Image heading rotation

    private boolean isVisible = true;

    // TODO: Replace with Body (touching it) or Body's Finger (or whatever Pointer, whatever).
    public boolean isTouched = false;

    private Model model;

    private Visualization visualization;

    // TODO: Make this an interface? Move interface out of class.
    private TouchActionListener touchActionListener;

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

    public PointHolder getPosition() {
        return this.position;
    }

    public double getRotation() {
        return this.position.getAngle();
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

    public void setPosition(PointHolder position) {
        this.position.set(position.getX(), position.getY());
    }

    /**
     * Absolute position calculated from relative position.
     */
    public void setRelativePosition(PointHolder position) {
        PointHolder absolutePosition = new PointHolder();
        Image parentImage = getParentImage();
        if (parentImage != null) {
            PointHolder relativePositionFromRelativePosition = Geometry.calculatePoint(
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

    public void setRotation(double rotation) {
        this.position.setAngle(rotation);
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public abstract void update();

    public abstract void draw(VisualizationSurface visualizationSurface);

    public abstract boolean isTouching(PointHolder point);

    public abstract boolean isTouching(PointHolder point, double padding);

    public interface TouchActionListener {
    }

    public abstract void onTouchInteraction(TouchInteraction touchInteraction);

    // TODO: change this to addOnTouchListener (since have abstract onTouchInteraction)... and call at end of that
    public void setOnTouchActionListener(TouchActionListener touchActionListener) {
        this.touchActionListener = touchActionListener;
    }

    public void touch(TouchInteraction touchInteraction) {
        onTouchInteraction(touchInteraction);
    }

}
