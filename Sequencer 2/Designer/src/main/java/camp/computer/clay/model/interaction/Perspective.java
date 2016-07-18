package camp.computer.clay.model.interaction;

import camp.computer.clay.application.Application;
import camp.computer.clay.visualization.arch.Image;
import camp.computer.clay.visualization.arch.Visualization;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.PointHolder;

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

    public static PointHolder DEFAULT_POSITION = new PointHolder(0, 0);
    public static double DEFAULT_ADJUSTMENT_DURATION = 250;

    private PointHolder targetPosition = DEFAULT_POSITION;
    private PointHolder position = new PointHolder(targetPosition.getX(), targetPosition.getY());
    private double adjustmentDuration = DEFAULT_ADJUSTMENT_DURATION;
    private double adjustmentPerFrameDistanceX = 0.0f;
    private double adjustmentPerFrameDistanceY = 0.0f;

    private double scalePerFrameDistance = 0.0f;

    public void setPosition (PointHolder targetPosition) {

        this.targetPosition.setX(-targetPosition.getX());
        this.targetPosition.setY(-targetPosition.getY());

        double MILLISECONDS_PER_SECOND = 1000.0f;
        double frameCount = (double) Application.getDisplay().getFramesPerSecond() * (adjustmentDuration / MILLISECONDS_PER_SECOND);
        // ^ use frameCount as index into function to change animation by maing stepDistance vary with frameCount
        double stepDistanceX = Math.abs(this.position.getX() - targetPosition.getX()) / frameCount;
        double stepDistanceY = Math.abs(this.position.getY() - targetPosition.getY()) / frameCount;

        adjustmentPerFrameDistanceX = stepDistanceX;
        adjustmentPerFrameDistanceY = stepDistanceY;

//        if (this.position.x != -targetPosition.x) {
//
//            // Pan to x position
//            if (this.position.x != -targetPosition.x) {
//                Animation.scaleValue(position.x, -targetPosition.x, adjustmentDuration, new Animation.OnScaleListener() {
//                    @Override
//                    public void onScale(double currentScale) {
//                        position.x = currentScale;
//                    }
//                });
//            }
//
//            this.targetPosition.x = -targetPosition.x;
//        }
//
//        if (this.position.y != -targetPosition.y) {
//
//            // Pan to y position
//            if (this.position.y != -targetPosition.y) {
//                Animation.scaleValue(position.y, -targetPosition.y, adjustmentDuration, new Animation.OnScaleListener() {
//                    @Override
//                    public void onScale(double currentScale) {
//                        position.y = currentScale;
//                    }
//                });
//            }
//
//            this.targetPosition.y = -targetPosition.y;
//        }
    }

    public void setOffset(double xOffset, double yOffset) {
        this.position.offset(xOffset, yOffset);
    }

    public static double DEFAULT_SCALE_FACTOR = 1.0f;
    public static int DEFAULT_SCALE_DURATION = 250;

    private double targetScale = DEFAULT_SCALE_FACTOR;
    public double scale = targetScale;
    private int scaleDuration = DEFAULT_SCALE_DURATION;

    public void setScale (double targetScale) {

        this.targetScale = targetScale;

        double MILLISECONDS_PER_SECOND = 1000.0f;
        double frameCount = (double) Application.getDisplay().getFramesPerSecond() * (scaleDuration / MILLISECONDS_PER_SECOND);
        // ^ use frameCount as index into function to change animation by maing stepDistance vary with frameCount
        scalePerFrameDistance = Math.abs(this.scale - targetScale) / frameCount;;
    }

    public double getScale() {
        return this.scale;
    }

    public void update() {

        // Position

        if (this.position.getX() != targetPosition.getX()) {

//            double stepDistance = Geometry.calculateDistance(this.position, targetPosition) / adjustmentDuration;

            if (position.getX() > targetPosition.getX()) {
                position.setX(position.getX() - adjustmentPerFrameDistanceX * scale);
            } else {
                position.setX(position.getX() + adjustmentPerFrameDistanceX * scale);
            }

            if (Math.abs(position.getX() * scale - targetPosition.getX() * scale) < adjustmentPerFrameDistanceX * scale) {
                position.setX(targetPosition.getX());
            }

        }

        if (this.position.getY() != this.targetPosition.getY()) {

//            double stepDistance = Geometry.calculateDistance(this.position, targetPosition) / adjustmentDuration;

            if (position.getY() > targetPosition.getY()) {
                position.setY(position.getY() - adjustmentPerFrameDistanceY * scale);
            } else {
                position.setY(position.getY() + adjustmentPerFrameDistanceY * scale);
            }

            if (Math.abs(position.getY() - targetPosition.getY()) < adjustmentPerFrameDistanceY * scale) {
                position.setY(targetPosition.getY());
            }

        }

        // Scale
        if (this.scale != this.targetScale) {

            if (scale > targetScale) {
                scale -= scalePerFrameDistance;
            } else {
                scale += scalePerFrameDistance;
            }

            if (Math.abs(scale - targetScale) < scalePerFrameDistance) {
                scale = targetScale;
            }

        }

    }

    public void setAdjustability(boolean isMovable) {
        this.isMovable = isMovable;
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
