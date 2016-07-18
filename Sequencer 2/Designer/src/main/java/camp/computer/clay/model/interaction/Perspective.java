package camp.computer.clay.model.interaction;

import android.util.Log;

import camp.computer.clay.application.Application;
import camp.computer.clay.visualization.arch.Image;
import camp.computer.clay.visualization.arch.Visualization;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.PointHolder;
import camp.computer.clay.visualization.util.Time;

public class Perspective {

    // TODO: Move position into Body, so can share Perspective among different bodies
    // ^ actually NO, because then a Body couldn't adopt a different Perspective

    private double width; // Width of perspective --- interactions (e.g., touches) are interpreted relative to this point
    private double height; // Height of perspective

    public void setWidth(double width) {
        this.width = width;
    }

    public double getWidth() {
        return this.width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return this.height;
    }

    // The visualization displayed from this perspective
    private Visualization visualization;

    // Focus in Perspective
    // TODO: Infer this from interaction history/perspective
    private Image focusImage = null;

    private boolean isMovable = true;

    public Perspective(Visualization visualization) {
        this.visualization = visualization;
    }

    public PointHolder getPosition() {
        return this.position;
    }

    public static final double DEFAULT_SCALE_FACTOR = 1.0f;
    public static final int DEFAULT_SCALE_DURATION = 250;

    public static final PointHolder DEFAULT_POSITION = new PointHolder(0, 0);
    public static final double DEFAULT_ADJUSTMENT_DURATION = 250;

    private double targetScale = DEFAULT_SCALE_FACTOR;
    public double scale = targetScale;
    private int scaleDuration = DEFAULT_SCALE_DURATION;

    private PointHolder targetPosition = DEFAULT_POSITION;
    private PointHolder position = new PointHolder(targetPosition.getX(), targetPosition.getY());
    private double adjustmentDuration = DEFAULT_ADJUSTMENT_DURATION;
    private double adjustmentFrameDeltaX = 0.0f;
    private double adjustmentFrameDeltaY = 0.0f;

    private double scaleFrameDelta = 0.0f;

    int frameCount = 0;
    int frameIndex = 0;

    PointHolder startPosition = new PointHolder();
    double distanceToTarget = 0.0f;
    double distanceToTargetX = 0.0f;
    double distanceToTargetY = 0.0f;
    public void setPosition (PointHolder targetPosition) {

        // this.targetPosition.setX(-targetPosition.getX() * targetScale);
        // this.targetPosition.setY(-targetPosition.getY() * targetScale);

        startPosition.set(position);

        this.targetPosition.setX(-targetPosition.getX());
        this.targetPosition.setY(-targetPosition.getY());

        distanceToTarget = Geometry.calculateDistance(position, this.targetPosition);
        distanceToTargetX = this.targetPosition.getX() - this.position.getX();
        distanceToTargetY = this.targetPosition.getY() - this.position.getY();

        // <PLAN_ANIMATION>
        frameCount = (int) (Application.getDisplay().getFramesPerSecond() * (adjustmentDuration / Time.MILLISECONDS_PER_SECOND));
        // ^ use frameCount as index into function to change animation by maing stepDistance vary with frameCount
        frameIndex = 0;

        adjustmentFrameDeltaX = Math.abs(targetPosition.getX() - position.getX()) / frameCount;
        adjustmentFrameDeltaY = Math.abs(targetPosition.getY() - position.getY()) / frameCount;;
        // </PLAN_ANIMATION>
    }

    public void setOffset(double xOffset, double yOffset) {
        this.position.offset(xOffset, yOffset);
    }

    public void setScale (double targetScale) {

        this.targetScale = targetScale;

        double frameCount = Application.getDisplay().getFramesPerSecond() * (scaleDuration / Time.MILLISECONDS_PER_SECOND);
        // ^ use frameCount as index into function to change animation by maing stepDistance vary with frameCount

        scaleFrameDelta = Math.abs(targetScale - scale) / frameCount;
    }

    public double getScale() {
        return this.scale;
    }

    public void update() {

        /*
        // This works without per-frame adjustment. It's a starting point for that.
        scale = this.targetScale;

        position.setX(targetPosition.getX());
        position.setY(targetPosition.getY());

        position.setX(position.getX() * scale);
        position.setY(position.getY() * scale);
        */

        double previousFrameScale = scale;

        // Scale
        if (scale != targetScale) {

            if (scale > targetScale) {
                scale -= scaleFrameDelta;
            } else {
                scale += scaleFrameDelta;
            }

            if (Math.abs(scale - targetScale) < scaleFrameDelta) {
                scale = targetScale;
            }

        }

        if (frameIndex < frameCount) {

            // double currentDistanceToTarget = Geometry.calculateDistance(position, targetPosition);
            // double currentDistance = (distanceToTarget - currentDistanceToTarget) / distanceToTarget;
            double currentDistanceTarget = (((double) (frameIndex + 1) / (double) frameCount) * distanceToTarget) / distanceToTarget;
            // Log.v("Progress", "frame: " + (frameIndex + 1) + " of " + frameCount + ", done: " + currentDistance + ", target: " + currentDistanceTarget + ", left: " + (1.0 - currentDistance));

            double newX = currentDistanceTarget * distanceToTargetX + startPosition.getX();
            double newY = currentDistanceTarget * distanceToTargetY + startPosition.getY();

            position.set(
                    newX * scale,
                    newY * scale
            );

            frameIndex++;

        } else if (frameIndex == frameCount) {

            position.setX(targetPosition.getX() * scale);
            position.setY(targetPosition.getY() * scale);

        }

//        if (position.getX() != targetPosition.getX()) {
//
//            if (position.getX() > targetPosition.getX()) {
//                position.setX((position.getX() - adjustmentFrameDeltaX * (1)) * 1);
//            } else {
//                position.setX((position.getX() + adjustmentFrameDeltaX * (1)) * 1);
//            }
//
//            if (Math.abs(targetPosition.getX() - position.getX()) < adjustmentFrameDeltaX) {
//                position.setX(targetPosition.getX());
//            }
//
//        }
//
//        if (position.getY() != this.targetPosition.getY()) {
//
//            if (position.getY() > targetPosition.getY()) {
//                position.setY((position.getY() - adjustmentFrameDeltaY * (1)) * 1);
//            } else {
//                position.setY((position.getY() + adjustmentFrameDeltaY * (1)) * 1);
//            }
//
//            if (Math.abs(targetPosition.getY() - position.getY()) < adjustmentFrameDeltaY) {
//                position.setY(targetPosition.getY());
//            }
//
//            frameIndex++;
//        }

//        if (position.getX() != targetPosition.getX()) {
//
//            if (position.getX() > targetPosition.getX()) {
//                position.setX((position.getX() - frameDeltaX * (1)) * 1);
//            } else {
//                position.setX((position.getX() + frameDeltaX * (1)) * 1);
//            }
//
//            if (Math.abs(targetPosition.getX() - position.getX()) < frameDeltaX) {
//                position.setX(targetPosition.getX());
//            }
//
//        }
//
//        if (position.getY() != this.targetPosition.getY()) {
//
//            if (position.getY() > targetPosition.getY()) {
//                position.setY((position.getY() - frameDeltaY * (1)) * 1);
//            } else {
//                position.setY((position.getY() + frameDeltaY * (1)) * 1);
//            }
//
//            if (Math.abs(targetPosition.getY() - position.getY()) < frameDeltaY) {
//                position.setY(targetPosition.getY());
//            }
//
//            frameIndex++;
//        }

        // Position
        /*
        // Not bad.. but not perfect!

        double frameDeltaX = Math.abs(targetPosition.getX() - position.getX()) / frameCount;
        double frameDeltaY = Math.abs(targetPosition.getY() - position.getY()) / frameCount;

        if (position.getX() != targetPosition.getX()) {

            if (position.getX() > targetPosition.getX()) {
                position.setX((position.getX() - frameDeltaX * (1)) * 1);
            } else {
                position.setX((position.getX() + frameDeltaX * (1)) * 1);
            }

            if (Math.abs(targetPosition.getX() - position.getX()) < frameDeltaX) {
                position.setX(targetPosition.getX());
            }

        }

        if (position.getY() != this.targetPosition.getY()) {

            if (position.getY() > targetPosition.getY()) {
                position.setY((position.getY() - frameDeltaY * (1)) * 1);
            } else {
                position.setY((position.getY() + frameDeltaY * (1)) * 1);
            }

            if (Math.abs(targetPosition.getY() - position.getY()) < frameDeltaY) {
                position.setY(targetPosition.getY());
            }

            frameIndex++;
        }
        */

    }

    public void setAdjustability(boolean isAdjustable) {
        this.isMovable = isAdjustable;
    }

    public boolean isAdjustable() {
        return isMovable;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public Image getFocusImage() {
        return this.focusImage;
    }

    public boolean hasFocusImage() {
        return this.focusImage != null;
    }

    public void setFocus(Image image) {
        this.focusImage = image;
    }
}
