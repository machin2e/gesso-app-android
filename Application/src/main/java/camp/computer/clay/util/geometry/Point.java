package camp.computer.clay.util.geometry;

public class Point {

    /**
     * The {@code Point} relative to which this point will be positioned.
     */
    protected Point referencePoint = null;

    /**
     * The x coordinate's position relative to {@code referencePoint}. If {@code referencePoint} is
     * {@code null} then this is equivalent to an absolute position.
     */
    public double x = 0;

    /**
     * The y coordinate's position relative to {@code referencePoint}. If {@code referencePoint} is
     * {@code null} then this is equivalent to an absolute position.
     */
    public double y = 0;

    /**
     * Relative rotation of the coordinate with which points referencing this one will be
     * rotated.
     */
    public double rotation = 0;

    /**
     * Rotation rotation in degrees
     */
    public Point() {
        this(0, 0);
    }

    /**
     * Copy constructor. Creates a new {@code Point} object with properties identical to those of
     * {@code otherPoint}.
     *
     * @param otherPoint The {@code Point} to set.
     */
    public Point(Point otherPoint) {
        this.x = otherPoint.x;
        this.y = otherPoint.y;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new {@code Point} positioned relative to {@code referencePoint}.
     *
     * @param x              The x coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param y              The y coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param referencePoint
     */
    public Point(double x, double y, Point referencePoint) {
        setReferencePoint(referencePoint);
        setX(x);
        setY(y);
    }

    public Point getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(Point referencePoint) {
        this.referencePoint = referencePoint;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param point Absolute position. Converted to relative position internally.
     */
    public void set(Point point) {
        x = point.x;
        y = point.y;

        rotation = point.rotation;

        setReferencePoint(point.getReferencePoint());
    }

    /**
     * @param dx Offset along x axis from current x position.
     * @param dy Offset along y axis from current y position.
     */
    public void offset(double dx, double dy) {
        this.x = this.x + dx;
        this.y = this.y + dy;
    }

    public double getRotation() {
        return this.rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * @return Absolute x coordinate.
     */
    public double getAbsoluteX() {
        if (referencePoint != null) {
            double absoluteX = Geometry.calculateDistance(0, 0, x, y) * Math.cos(Math.toRadians(referencePoint.getAbsoluteRotation() + Geometry.getAngle(0, 0, x, y)));
            return referencePoint.getAbsoluteX() + absoluteX;
        } else {
            return this.x;
        }
    }

    /**
     * @param x Absolute x coordinate. Converted to a relative x position internally.
     */
    public void setAbsoluteX(double x) {
        if (referencePoint != null) {
            this.x = x - referencePoint.getAbsoluteX();
        } else {
            this.x = x;
        }
    }

    /**
     * @return Absolute y coordinate.
     */
    public double getAbsoluteY() {
        if (referencePoint != null) {
            double absoluteY = Geometry.calculateDistance(0, 0, x, y) * Math.sin(Math.toRadians(referencePoint.getAbsoluteRotation() + Geometry.getAngle(0, 0, x, y)));
            return referencePoint.getAbsoluteY() + absoluteY;
        } else {
            return this.y;
        }
    }

    /**
     * @param y Absolute y coordinate. Converted to a relative y position internally.
     */
    public void setAbsoluteY(double y) {
        if (referencePoint != null) {
            this.y = y - referencePoint.getAbsoluteY();
        } else {
            this.y = y;
        }
    }

    public double getAbsoluteRotation() {
        if (referencePoint != null) {
            return referencePoint.getAbsoluteRotation() + this.rotation;
        } else {
            return this.rotation;
        }
    }
}
