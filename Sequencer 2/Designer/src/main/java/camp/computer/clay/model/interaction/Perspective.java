package camp.computer.clay.model.interaction;

import camp.computer.clay.application.Application;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Point;
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

    public Point getPosition() {
        return this.position;
    }

    public final double DEFAULT_SCALE = 1.0f;
    public final int DEFAULT_SCALE_PERIOD = 250;

    public final Point DEFAULT_POSITION = new Point(0, 0);
    public final double DEFAULT_ADJUSTMENT_PERIOD = 250;

    private double targetScale = DEFAULT_SCALE;
    public double scale = DEFAULT_SCALE;
    private int scalePeriod = DEFAULT_SCALE_PERIOD;
    private double scaleDelta = 0;

    private Point targetPosition = DEFAULT_POSITION;
    private Point position = new Point(targetPosition.getX(), targetPosition.getY());
    private double positionPeriod = DEFAULT_ADJUSTMENT_PERIOD;
    private int positionFrameIndex = 0;
    private int positionFrameLimit = 0;

    private Point originalPosition = new Point();

    public void setPosition (Point targetPosition) {
        setPosition(targetPosition, positionPeriod);
    }

    public void setPosition (Point targetPosition, double duration) {

        if (targetPosition.getX() == position.getX() && targetPosition.getY() == position.getY()) {
            return;
        }

        /*
        // Solution 1: This works without per-frame adjustment. It's a starting point for that.
        // this.targetPosition.setX(-targetPosition.getX() * targetScale);
        // this.targetPosition.setY(-targetPosition.getY() * targetScale);
        */

        this.targetPosition.setX(-targetPosition.getX());
        this.targetPosition.setY(-targetPosition.getY());

        // <PLAN_ANIMATION>
        originalPosition.set(position);

        positionFrameLimit = (int) (Application.getDisplay().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND));
        // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
        positionFrameIndex = 0;
        // </PLAN_ANIMATION>
    }

    public void setOffset(double xOffset, double yOffset) {
        this.position.offset(xOffset, yOffset);
    }

    public void setScale (double targetScale) {
        setScale (targetScale, scalePeriod);
    }

    public void setScale (double targetScale, double duration) {

        this.targetScale = targetScale;

        double frameCount = Application.getDisplay().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND);
        // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit

        scaleDelta = Math.abs(targetScale - scale) / frameCount;
    }

    public double getScale() {
        return this.scale;
    }

    public void update() {

        /*
        // Solution 1: This works without per-frame adjustment. It's a starting point for that.
        scale = this.targetScale;

        position.setX(targetPosition.getX());
        position.setY(targetPosition.getY());

        position.setX(position.getX() * scale);
        position.setY(position.getY() * scale);
        */

        // Scale
        if (scale != targetScale) {

            if (scale > targetScale) {
                scale -= scaleDelta;
            } else {
                scale += scaleDelta;
            }

            if (Math.abs(scale - targetScale) < scaleDelta) {
                scale = targetScale;
            }

        }

        // Position
        if (positionFrameIndex < positionFrameLimit) {

            double totalDistanceToTarget = Geometry.calculateDistance(originalPosition, targetPosition);
            double totalDistanceToTargetX = targetPosition.getX() - originalPosition.getX();
            double totalDistanceToTargetY = targetPosition.getY() - originalPosition.getY();

            // double currentDistanceToTarget = Geometry.calculateDistance(position, targetPosition);
            // double currentDistance = (distanceToTarget - currentDistanceToTarget) / distanceToTarget;
            double currentDistanceTarget = ((((double) (positionFrameIndex + 1) / (double) positionFrameLimit) * totalDistanceToTarget) / totalDistanceToTarget) /* (1.0 / scale) */;
            // Log.v("Progress", "frame: " + (positionFrameIndex + 1) + " of " + positionFrameLimit + ", done: " + currentDistance + ", target: " + currentDistanceTarget + ", left: " + (1.0 - currentDistance));

            double newX = currentDistanceTarget * totalDistanceToTargetX + originalPosition.getX();
            double newY = currentDistanceTarget * totalDistanceToTargetY + originalPosition.getY();

            position.set(
                    newX * scale,
                    newY * scale
            );

            positionFrameIndex++;

        } else if (positionFrameIndex == positionFrameLimit) {

            position.setX(targetPosition.getX() * scale);
            position.setY(targetPosition.getY() * scale);

        }

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
