package camp.computer.clay.model;

import android.content.Context;
import android.graphics.PointF;
import android.os.Vibrator;

import java.util.ArrayList;

import camp.computer.clay.designer.ApplicationView;
import camp.computer.clay.sprite.Sprite;
import camp.computer.clay.sprite.Visualization;
import camp.computer.clay.sprite.util.Animation;

public class Perspective {
    // TODO: Move position into Body, so can share Perspective among different bodies
    // ^ actually NO, because then a Body couldn't adopt a different Perspective

    float width; // Width of perspective --- interactions (e.g., touches) are interpreted relative to this point
    float height; // Height of perspective

    // The visualization displayed from this perspective
    private Visualization visualization;

    // Focus in Perspective
//    private boolean isMapPerspective = false;
//    private boolean isMachinePerspective = false;
//    private boolean isPortPerspective = false;
//    private boolean isPathPerspective = false; // TODO: Infer this from interaction history/perspective
    private Sprite focusSprite = null;

    private boolean isPanningEnabled = true;

    private PointF currentPosition = new PointF (); // Center position --- interactions (e.g., touches) are interpreted relative to this point

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

    public static float DEFAULT_SCALE_FACTOR = 1.0f;
    public static int DEFAULT_SCALE_DURATION = 200;

    private float targetScale = DEFAULT_SCALE_FACTOR;
    public float scale = targetScale;
    private int scaleDuration = DEFAULT_SCALE_DURATION;

    public void setScale (float targetScale) {

        if (this.targetScale != targetScale) {

            if (this.scale != targetScale) {
                Animation.scaleValue(scale, targetScale, scaleDuration, new Animation.OnScaleListener() {
                    @Override
                    public void onScale(float currentScale) {
                        scale = currentScale;
                    }
                });
            }

            Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(50);

            this.targetScale = targetScale;
        }
    }

    public float getScale() {
        return this.scale;
    }

    // TODO: setMovability(boolean isMovable)
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

    public Sprite getFocus() {
        return this.focusSprite;
    }

    public void setFocus(Sprite sprite) {
        this.focusSprite = sprite;
    }

//    public void setFocus(ArrayList<Sprite> sprites) {
//        // TODO: Get bounding box of sprites.
//        // TODO: Zoom to fit bounding box plus some padding.
//    }
}
