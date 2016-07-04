package camp.computer.clay.visualization;

import android.graphics.PointF;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.simulation.Model;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Geometry;

public abstract class Image {

    private Image parentImage;

    private PointF position = new PointF(); // Image position
    private float scale = 1.0f; // Image scale factor
    private float rotation = 0.0f; // Image heading rotation

    private Model model;

    private Visualization visualization;

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    private TouchActionListener touchActionListener;

    public Image(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }

    public void setParentImage(Image parentImage) {
        this.parentImage = parentImage;
    }

    public Image getParentImage() {
        return this.parentImage;
    }

    public PointF getPosition() {
        return this.position;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getAbsoluteRotation() {
        float absoluteRotation = 0.0f;
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

    public void setRelativePosition(PointF position) {
        PointF absolutePositionFromRelativePosition = new PointF();
        if (parentImage != null) {
            PointF relativePositionFromRelativePosition
                    = Geometry.calculatePoint(
                        parentImage.getPosition(),
                        Geometry.calculateRotationAngle(parentImage.getPosition(), position),
                        (float) Geometry.calculateDistance(parentImage.getPosition(), position));
            absolutePositionFromRelativePosition.x = parentImage.getPosition().x + relativePositionFromRelativePosition.x;
            absolutePositionFromRelativePosition.y = parentImage.getPosition().y + relativePositionFromRelativePosition.y;
        } else {
            // TODO: This should get the absolute position of the root sprite relative to the origin point on the coordinate system/canvas
            absolutePositionFromRelativePosition.x = position.x;
            absolutePositionFromRelativePosition.y = position.y;
        }
        this.position.x = absolutePositionFromRelativePosition.x;
        this.position.y = absolutePositionFromRelativePosition.y;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }


    private boolean isVisible = true;

    public void setVisibility (boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public abstract void update ();

    public abstract void draw (MapView mapView);

    public abstract boolean isTouching (PointF point);

    public interface TouchActionListener {
    }

    public abstract void onTouchAction(TouchInteraction touchInteraction);

    // TODO: change this to addOnTouchListener (since have abstract onTouchAction)... and call at end of that
    public void setOnTouchActionListener(TouchActionListener touchActionListener) {
        this.touchActionListener = touchActionListener;
    }

    public void touch (TouchInteraction touchInteraction) {
        onTouchAction(touchInteraction);
    }

}
