package camp.computer.clay.model.interaction;

import android.graphics.PointF;
import android.util.Log;

import camp.computer.clay.visualization.Image;
import camp.computer.clay.visualization.Visualization;
import camp.computer.clay.visualization.util.Animation;

public class Perspective {
    // TODO: Move position into Body, so can share Perspective among different bodies
    // ^ actually NO, because then a Body couldn't adopt a different Perspective

    private float width; // Width of perspective --- interactions (e.g., touches) are interpreted relative to this point
    private float height; // Height of perspective

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return this.width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeight() {
        return this.height;
    }

    // The visualization displayed from this perspective
    private Visualization visualization;

    // Focus in Perspective
//    private boolean isMapPerspective = false;
//    private boolean isMachinePerspective = false;
//    private boolean isPortPerspective = false;
//    private boolean isPathPerspective = false; // TODO: Infer this from interaction history/perspective
    private Image focusImage = null;

    private boolean isPanningEnabled = true;

//    private PointF position = new PointF (); // Center position --- interactions (e.g., touches) are interpreted relative to this point

    public Perspective(Visualization visualization) {
        this.visualization = visualization;
    }

    public PointF getPosition() {
        return this.position;
    }

    public static PointF DEFAULT_POSITION = new PointF(0, 0);
    public static int DEFAULT_PANNING_DURATION = 200;

    private PointF targetPosition = DEFAULT_POSITION;
    private PointF position = new PointF(targetPosition.x, targetPosition.y);
    private int panningDuration = DEFAULT_PANNING_DURATION;

    public void setPosition (PointF targetPosition) {

        if (this.position.x != -targetPosition.x) {

            // Pan to x position
            if (this.position.x != -targetPosition.x) {
                Animation.scaleValue(position.x, -targetPosition.x, panningDuration, new Animation.OnScaleListener() {
                    @Override
                    public void onScale(float currentScale) {
                        position.x = currentScale;
                    }
                });
            }

            this.targetPosition.x = -targetPosition.x;
        }

        if (this.position.y != -targetPosition.y) {

            // Pan to y position
            if (this.position.y != -targetPosition.y) {
                Animation.scaleValue(position.y, -targetPosition.y, panningDuration, new Animation.OnScaleListener() {
                    @Override
                    public void onScale(float currentScale) {
                        position.y = currentScale;
                    }
                });
            }

            this.targetPosition.y = -targetPosition.y;
        }
    }

    public void setOffset(float xOffset, float yOffset) {
        this.position.offset(xOffset, yOffset);
    }

    public static float DEFAULT_SCALE_FACTOR = 1.0f;
    public static int DEFAULT_SCALE_DURATION = 200;

    private float targetScale = DEFAULT_SCALE_FACTOR;
    public float scale = targetScale;
    private int scaleDuration = DEFAULT_SCALE_DURATION;
    private boolean scaleInProgress = false;

    public void setScale (float targetScale) {

        Log.v("SetScale", "newScale: " + targetScale);
        Log.v("SetScale", "this.targetScale: " + this.targetScale);
        Log.v("SetScale", "this.scale: " + this.scale);

        if (this.targetScale != targetScale) {

            Log.v("SetScale", "targetScale: " + this.targetScale);

            if (this.scale != targetScale) {
                Animation.scaleValue(scale, targetScale, scaleDuration, new Animation.OnScaleListener() {
                    @Override
                    public void onScale(float currentScale) {
                        Log.v("SetScale", "targetScale: " + currentScale);
                        scale = currentScale;
                    }
                });
            }

            /*
            Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(50);
            */

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

    public Image getFocus() {
        return this.focusImage;
    }

    public void setFocus(Image image) {
        this.focusImage = image;
    }

//    public void setFocus(ArrayList<Image> sprites) {
//        // TODO: Get bounding box of sprites.
//        // TODO: Zoom to fit bounding box plus some padding.
//    }
}
