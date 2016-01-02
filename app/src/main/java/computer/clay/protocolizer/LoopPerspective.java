package computer.clay.protocolizer;

import android.graphics.Point;

public class LoopPerspective {

    public static int DEFAULT_RADIUS = 350;
    public static int DEFAULT_RADIUS_EXTENSION = 100;
    public static int DEFAULT_START_ANGLE = 15; // i.e., -75
    public static int DEFAULT_ANGLE_SPAN = 330;

    private int radius = DEFAULT_RADIUS;

    public int startAngle = DEFAULT_START_ANGLE; // The starting angle in degrees for this perspective.
    public int span = DEFAULT_ANGLE_SPAN; // The arc length in degrees of this perspective.
    public Point startAnglePoint = null;
    public Point spanPoint = null;

    public double DEFAULT_SNAP_DISTANCE = 200;

    private Perspective perspective = null;

    private LoopConstruct loopConstruct = null;
    private LoopPerspective previousPerspective = null;
    private LoopPerspective nextPerspective = null;

    private double snapDistance = DEFAULT_SNAP_DISTANCE;

    private BehaviorConstruct firstBehaviorConstruct = null; // The first behavior construct displayed in the perspective's span

    LoopPerspective (LoopConstruct loopConstruct, int angle, int span) { // TODO: LoopPerspective (Perspective perspective, Loop loop) {

        // TODO: this.perspective = perspective;

        this.loopConstruct = loopConstruct;

        this.setStartAngle (angle);
        this.setSpan (span);
    }

    public int getRadius () {
        return this.radius + (DEFAULT_RADIUS_EXTENSION - (int) ((this.getSpan () / 360.0) * DEFAULT_RADIUS_EXTENSION));
    }

    public boolean hasPreviousPerspective () {
        return (this.previousPerspective != null);
    }

    public LoopPerspective getPreviousPerspective () {
        return this.previousPerspective;
    }

    public boolean hasNextPerspective () {
        return (this.nextPerspective != null);
    }

    public LoopPerspective getNextPerspective () {
        return this.nextPerspective;
    }

    public void setPreviousPerspective (LoopPerspective loopPerspective) {
        this.previousPerspective = loopPerspective;
    }

    public void setNextPerspective (LoopPerspective loopPerspective) {
        this.nextPerspective = loopPerspective;
    }

    /**
     * Returns the perspective associated with this loop perspective.
     */
    Perspective getPerspective () {
        return this.perspective;
    }

    /**
     * Returns the loop for which this perspective applies.
     */
    LoopConstruct getLoopConstruct () {
        return this.loopConstruct;
    }

    /**
     * Get the start angle.
     *
     * @return The start angle.
     */
    public int getStartAngle () {
        return this.startAngle;
    }

    /**
     * Sets the start angle to the specified angle.
     *
     * @param angle The angle in degrees.
     */
    public void setStartAngle (int angle) {

        int previousStartAngle = this.startAngle;

        this.startAngle = angle;
        this.startAnglePoint = this.loopConstruct.getPoint(this.startAngle + 15); // new Point (120, -463);
//        this.spanPoint = this.loopConstruct.getPoint(this.span);

        // Update the next perspective's start angle and span.
        if (this.getNextPerspective () != null) {
            this.getNextPerspective ().setStartAngle (this.getStopAngle ());
        } else if (this.getNextPerspective () == null) {
            this.setSpan (this.span - (angle - previousStartAngle));
        }

    }

    public double getSnapDistance () {
        return this.snapDistance;
    }

    public int getSpan () {
        return this.span;
    }

    public void setSpan (int span) {
        this.span = span;
        this.spanPoint = this.loopConstruct.getPoint (this.span); // new Point (120, -463);
    }

    public void updatePerspectives () {

        // Update previous perspective's start angle and span, if one exists.
        if (this.getPreviousPerspective() != null) {
            this.getPreviousPerspective ().setSpan(this.getStartAngle() - this.getPreviousPerspective().getStartAngle());
        }

        // Update the the next perspective's start angle and span, if one exits.
        if (this.getNextPerspective() != null) {
            this.getNextPerspective().setStartAngle(this.getStopAngle());
        }

    }

    public int getStopAngle () {
        return this.startAngle + this.span;
    }
}
