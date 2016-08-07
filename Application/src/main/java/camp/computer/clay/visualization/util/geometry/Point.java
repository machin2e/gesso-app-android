package camp.computer.clay.visualization.util.geometry;

public class Point {
    //
    // TODO: Update to use numbers that can be composed and given dependencies (used in
    // TODO: (cont'd) expressions) and dynamically update expressions. Do animations by giving them
    // TODO: (cont'd) quantity-change rules.
    //
    // TODO: Refactor to support N dimensions, including rotation angles accordingly.

    /**
     * The {@code Point} relative to which this point will be positioned.
     */
    protected Point referencePoint = null;

    private double x = 0;
    private double y = 0;

    /**
     * Rotation angle in degrees
     */
    // TODO: Refactor so 0 degrees faces upward, not right.
    public Point() {
        this(0, 0);
    }

    /**
     * Copy constructor. Creates a new {@code Point} object with properties identical to those of
     * {@code otherPoint}.
     *
     * @param otherPoint The {@code Point} to copy.
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
     * @param x The x coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param y The y coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param referencePoint
     */
    public Point(double x, double y, Point referencePoint) {
        setReferencePoint(referencePoint);
        setRelativeX(x);
        setRelativeY(y);
    }

    public Point getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(Point referencePoint) {
        this.referencePoint = referencePoint;
    }

    public double getRelativeX() {
        return x;
    }

    public double getRelativeY() {
        return y;
    }

    public void setRelative(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public void setRelativeX(double x) {
        this.x = x;
    }

    public void setRelativeY(double y) {
        this.y = y;
    }

    /**
     * @param dx Absolute offset along x axis from current x position.
     * @param dy Absolute offset along y axis from current y position.
     */
    public void offset(double dx, double dy) {
        this.x = this.x + dx;
        this.y = this.y + dy;
    }

    /**
     * @return Absolute x coordinate.
     */
    public double getX() {
        if (referencePoint != null) {
            return referencePoint.getX() + this.x;
        } else {
            return this.x;
        }
    }

    /**
     * @return Absolute y coordinate.
     */
    public double getY() {
        if (referencePoint != null) {
            return referencePoint.getY() + this.y;
        } else {
            return this.y;
        }
    }

    /**
     * @param x Absolute x coordinate. Converted to relative coordinate internally.
     * @param y Absolute y coordinate. Converted to relative coordinate internally.
     */
    public void set(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * @param point Absolute position. Converted to relative position internally.
     */
    public void set(Point point) {
        setX(point.getX());
        setY(point.getY());
    }

    /**
     * @param x Absolute x coordinate. Converted to a relative x position internally.
     */
    public void setX(double x) {
        if (referencePoint != null) {
            this.x = x - referencePoint.getX();
        } else {
            this.x = x;
        }
    }

    /**
     * @param y Absolute y coordinate. Converted to a relative y position internally.
     */
    public void setY(double y) {
        if (referencePoint != null) {
            this.y = y - referencePoint.getY();
        } else {
            this.y = y;
        }
    }

}
