package camp.computer.clay.sprite;

import android.graphics.PointF;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Model;
import camp.computer.clay.model.TouchInteraction;

public abstract class Sprite {

    protected PointF position = new PointF(); // Sprite position
    protected float scale = 1.0f; // Sprite scale factor
    protected float rotation = 0.0f; // Sprite heading rotation

    private TouchActionListener touchActionListener;

    private Model model;

    // TODO: Model model;
    // TODO: Sprite(Model model) --- Constructor

    private Sprite parentSprite;

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

    // TODO: Account for rotation of (parent) Sprites!
    public PointF getAbsolutePosition() {
        PointF absolutePosition = new PointF();
        if (parentSprite != null) {
            absolutePosition.x
                    = this.parentSprite.getAbsolutePosition().x
                    + (this.getPosition().x * this.getScale()); // TODO: this.machineSprite.getAbsolutePosition()
            absolutePosition.y
                    = this.parentSprite.getAbsolutePosition().y
                    + (this.getPosition().y * this.getScale());
        } else {
            // TODO: This should get the absolute position of the root sprite relative to the origin point on the coordinate system/canvas
            absolutePosition.x = this.getPosition().x * this.getScale();
            absolutePosition.y = this.getPosition().y * this.getScale();
        }
        return absolutePosition;
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
//        this.updatePortPositions();
    }

    public void setRotation(float angle) {
        this.rotation = angle;
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
