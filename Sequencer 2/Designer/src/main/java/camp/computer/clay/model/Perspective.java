package camp.computer.clay.model;

import android.graphics.PointF;

import camp.computer.clay.sprite.Sprite;
import camp.computer.clay.sprite.Visualization;

public class Perspective {
    // TODO: Move position into Body, so can share Perspective among different bodies
    // ^ actually NO, because then a Body couldn't adopt a different Perspective

    float width; // Width of perspective --- interactions (e.g., touches) are interpreted relative to this point
    float height; // Height of perspective

    private Visualization visualization;

    // Focus in Perspective
//    private boolean isMapPerspective = false;
//    private boolean isMachinePerspective = false;
//    private boolean isPortPerspective = false;
//    private boolean isPathPerspective = false; // TODO: Infer this from interaction history/perspective
    public Sprite focusSprite = null;

    private boolean isPanningEnabled = true;

    private PointF currentPosition = new PointF (); // Center position --- interactions (e.g., touches) are interpreted relative to this point
    private float scale = 1.0f;

    public Perspective(Visualization visualization) {
        this.visualization = visualization;
    }

    public PointF getPosition() {
        return this.currentPosition;
    }

    public void setPosition(PointF position) {
        this.currentPosition.x = position.x;
        this.currentPosition.y = position.y;
    }

    public void setOffset(float xOffset, float yOffset) {
        this.currentPosition.offset(xOffset, yOffset);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }

    public void disablePanning() {
        this.isPanningEnabled = false;
    }

    public void enablePanning() {
        this.isPanningEnabled = true;
    }

    public boolean isPanningEnabled() {
        return isPanningEnabled;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }
}
