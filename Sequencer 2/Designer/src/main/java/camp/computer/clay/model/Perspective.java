package camp.computer.clay.model;

import android.graphics.PointF;

import camp.computer.clay.sprite.Sprite;

public class Perspective {
    // TODO: Move position into Body, so can share Perspective among different bodies
    // ^ actually NO, because then a Body couldn't adopt a different Perspective

    float width; // Width of perspective --- interactions (e.g., touches) are interpreted relative to this point
    float height; // Height of perspective

    // Focus in Perspective
//    private boolean isMapPerspective = false;
//    private boolean isMachinePerspective = false;
//    private boolean isPortPerspective = false;
//    private boolean isPathPerspective = false; // TODO: Infer this from interaction history/perspective
    public Sprite focusSprite = null;

    private boolean isPanningEnabled = true;

    private PointF currentPosition = new PointF (); // Center position --- interactions (e.g., touches) are interpreted relative to this point

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

    public void disablePanning() {
        this.isPanningEnabled = false;
    }

    public void enablePanning() {
        this.isPanningEnabled = true;
    }

    public boolean isPanningEnabled() {
        return isPanningEnabled;
    }
}
