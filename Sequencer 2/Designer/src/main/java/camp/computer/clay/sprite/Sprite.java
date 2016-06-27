package camp.computer.clay.sprite;

import android.graphics.PointF;
import android.util.Log;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Model;
import camp.computer.clay.model.TouchInteraction;
import camp.computer.clay.sprite.util.Geometry;

public abstract class Sprite {

    private Sprite parentSprite;

    private PointF position = new PointF(); // Sprite position
    private float scale = 1.0f; // Sprite scale factor
    private float rotation = 0.0f; // Sprite heading rotation

    private Model model;

    private TouchActionListener touchActionListener;

    public Sprite(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }

    public void setParentSprite(Sprite parentSprite) {
        this.parentSprite = parentSprite;
    }

    public Sprite getParentSprite() {
        return this.parentSprite;
    }

    public PointF getPosition() {
        return this.position;
    }

    public float getRotation() {
        return this.rotation;
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
        if (parentSprite != null) {
            PointF relativePositionFromRelativePosition
                    = Geometry.calculatePoint(
                        parentSprite.getPosition(),
                        Geometry.calculateRotationAngle(parentSprite.getPosition(), position),
                        (float) Geometry.calculateDistance(parentSprite.getPosition(), position));
            absolutePositionFromRelativePosition.x = parentSprite.getPosition().x + relativePositionFromRelativePosition.x;
            absolutePositionFromRelativePosition.y = parentSprite.getPosition().y + relativePositionFromRelativePosition.y;
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

    public boolean getVisibility () {
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
