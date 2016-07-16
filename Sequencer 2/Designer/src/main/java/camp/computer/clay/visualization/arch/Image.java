package camp.computer.clay.visualization.arch;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Model;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Geometry;

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

    private PointF position = new PointF(); // Image position
    private float scale = 1.0f; // Image scale factor
    private float rotation = 0.0f; // Image heading rotation

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

    public PointF getPosition() {
        return this.position;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getAbsoluteRotation() {
        float absoluteRotation = 0.0f;
        Image parentImage = getParentImage();
        if (parentImage != null) {
            absoluteRotation = parentImage.getAbsoluteRotation() + getRotation();
        } else {
            return getRotation();
        }
        return absoluteRotation;
    }

    public float getScale() {
        return this.scale;
    }

    public void setPosition(PointF position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    /**
     * Absolute position calculated from relative position.
     */
    public void setRelativePosition(PointF position) {
        PointF absolutePosition = new PointF();
        Image parentImage = getParentImage();
        if (parentImage != null) {
            PointF relativePositionFromRelativePosition = Geometry.calculatePoint(
                    parentImage.getPosition(),
                    Geometry.calculateRotationAngle(parentImage.getPosition(), position),
                    (float) Geometry.calculateDistance(parentImage.getPosition(), position)
            );
            absolutePosition.x = parentImage.getPosition().x + relativePositionFromRelativePosition.x;
            absolutePosition.y = parentImage.getPosition().y + relativePositionFromRelativePosition.y;
        } else {
            // TODO: This should get the absolute position of the root sprite relative to the origin point on the coordinate system/canvas
            absolutePosition.x = position.x;
            absolutePosition.y = position.y;
        }
        this.position.x = absolutePosition.x;
        this.position.y = absolutePosition.y;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
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

    public abstract boolean isTouching(PointF point);

    public abstract boolean isTouching(PointF point, float padding);

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