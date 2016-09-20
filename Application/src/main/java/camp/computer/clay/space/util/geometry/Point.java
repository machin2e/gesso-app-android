package camp.computer.clay.space.util.geometry;

public class Point {
    //
    // TODO: Update to use numbers that can be composed and given dependencies (used in
    // TODO: (cont'd) expressions) and dynamically update expressions. Do animations by giving them
    // TODO: (cont'd) quantity-change rules.
    //
    // TODO: Refactor to support N dimensions, including rotation angles accordingly.

    // private static Point origin = new Point(0, 0);

    /**
     * The {@code Point} relative to which this point will be positioned.
     */
    protected Point referencePoint = null;

    private double x = 0;
    private double y = 0;

    /**
     * Relative rotation of the the coordinate with which points referencing this one will be
     * rotated.
     */
    private double rotation = 0;

    /**
     * Rotation rotation in degrees
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
     * @param x                   The x coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param y                   The y coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param referencePoint
     */
    public Point(double x, double y, Point referencePoint) {
        setOrigin(referencePoint);
        setRelativeX(x);
        setRelativeY(y);
    }

    public Point getOrigin() {
        return referencePoint;
    }

    public void setOrigin(Point referenceCoordinate) {
        this.referencePoint = referenceCoordinate;
    }

    public double getRelativeX() {
        return x;
    }

    public double getRelativeY() {
        return y;
    }

    public double getRelativeAngle() {
        return rotation;
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

    public void setRelativeAngle(double angle) {
        this.rotation = angle;
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

            double globalX = Geometry.calculateDistance(0, 0, x, y)
                    * Math.cos(Math.toRadians(referencePoint.getRotation()
                    + Geometry.calculateRotationAngle(0, 0, x, y)));

            return referencePoint.getX() + globalX;

        } else {
            return this.x;
        }
    }

    /**
     * @return Absolute y coordinate.
     */
    public double getY() {
        if (referencePoint != null) {

            double globalY = Geometry.calculateDistance(0, 0, x, y)
                    * Math.sin(Math.toRadians(referencePoint.getRotation()
                    + Geometry.calculateRotationAngle(0, 0, x, y)));

            return referencePoint.getY() + globalY;

        } else {
            return this.y;
        }
    }

    public double getRotation() {
//        return this.rotation;
        if (referencePoint != null) {
            return referencePoint.getRotation() + this.rotation;
        } else {
            return this.rotation;
        }
    }

    public double getRelativeRotation() {
        return this.rotation;
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
        setRelativeX(point.getRelativeX());
        setRelativeY(point.getRelativeY());

        // <HACK?>
        // TODO: Set these?
        setOrigin(point.getOrigin());
        setRelativeRotation(point.getRelativeRotation());
        // </HACK?>
    }

    /**
     * @param x Absolute x coordinate. Converted to a relative x position internally.
     */
    public void setX(double x) {
        if (referencePoint != null) {

//            Point newPoint = new Point(x, getY());
//            Point rotatedPoint = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), newPoint);
//            this.x = rotatedPoint.getX() - referencePoint.getX();

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

//            Point newPoint = new Point(getX(), y);
//            Point rotatedPoint = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), newPoint);
//            this.x = rotatedPoint.getY() - referencePoint.getY();

            this.y = y - referencePoint.getY();
        } else {
            this.y = y;
        }
    }

    public void setRelativeRotation(double rotation) {
//        if (referencePoint != null) {
//            this.rotation = rotation - referencePoint.getRotation();
//        } else {
        this.rotation = rotation;
//        }
    }

}
