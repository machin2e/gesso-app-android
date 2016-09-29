package camp.computer.clay.util.geometry;

public class Point {

    public static int count = 0;

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

    protected double relativeX = 0;
    protected double relativeY = 0;

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

//        count++;
//        Log.v("Point", "count: " + count);
    }

    /**
     * Copy constructor. Creates a new {@code Point} object with properties identical to those of
     * {@code otherPoint}.
     *
     * @param otherPoint The {@code Point} to copy.
     */
    public Point(Point otherPoint) {
        this.relativeX = otherPoint.relativeX;
        this.relativeY = otherPoint.relativeY;

//        count++;
//        Log.v("Point", "count: " + count);
    }

    public Point(double x, double y) {
        this.relativeX = x;
        this.relativeY = y;

//        count++;
//        Log.v("Point", "count: " + count);
    }

    /**
     * Creates a new {@code Point} positioned relative to {@code referencePoint}.
     *
     * @param x              The relativeX coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param y              The relativeY coordinate of this {@code Point} relative to {@code referencePoint}.
     * @param referencePoint
     */
    public Point(double x, double y, Point referencePoint) {
        setReferencePoint(referencePoint);
        setRelativeX(x);
        setRelativeY(y);

//        count++;
//        Log.v("Point", "count: " + count);
    }

    public static double calculateDistance(Point source, Point target) {
        return calculateDistance(source.getX(), source.getY(), target.getX(), target.getY());
    }

    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double distanceSquare = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        double distance = Math.sqrt(distanceSquare);
        return distance;
    }

//    protected void finalize() throws Throwable {
//        count--;
//    }

    public Point getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(Point referenceCoordinate) {
        this.referencePoint = referenceCoordinate;
    }

    public double getRelativeX() {
        return relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    public void setRelativeX(double x) {
        this.relativeX = x;
    }

    public void setRelativeY(double y) {
        this.relativeY = y;
    }

    /**
     * @param dx Absolute offset along relativeX axis from current relativeX position.
     * @param dy Absolute offset along relativeY axis from current relativeY position.
     */
    public void offset(double dx, double dy) {
        this.relativeX = this.relativeX + dx;
        this.relativeY = this.relativeY + dy;
    }

    /**
     * @return Absolute relativeX coordinate.
     */
    public double getX() {

        if (referencePoint != null) {
            double globalX = calculateDistance(0, 0, relativeX, relativeY) * Math.cos(Math.toRadians(referencePoint.getRotation() + Geometry.calculateRotationAngle(0, 0, relativeX, relativeY)));
            return referencePoint.getX() + globalX;
        } else {
            return this.relativeX;
        }
    }

    /**
     * @return Absolute relativeY coordinate.
     */
    public double getY() {
        if (referencePoint != null) {
            double globalY = calculateDistance(0, 0, relativeX, relativeY) * Math.sin(Math.toRadians(referencePoint.getRotation() + Geometry.calculateRotationAngle(0, 0, relativeX, relativeY)));
            return referencePoint.getY() + globalY;
        } else {
            return this.relativeY;
        }
    }

    public double getRotation() {
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
     * @param x Absolute relativeX coordinate. Converted to relative coordinate internally.
     * @param y Absolute relativeY coordinate. Converted to relative coordinate internally.
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
        setReferencePoint(point.getReferencePoint());
        setRelativeRotation(point.getRelativeRotation());
        // </HACK?>
    }

    /**
     * @param x Absolute relativeX coordinate. Converted to a relative relativeX position internally.
     */
    public void setX(double x) {
        if (referencePoint != null) {
            this.relativeX = x - referencePoint.getX();
        } else {
            this.relativeX = x;
        }
    }

    /**
     * @param y Absolute relativeY coordinate. Converted to a relative relativeY position internally.
     */
    public void setY(double y) {
        if (referencePoint != null) {
            this.relativeY = y - referencePoint.getY();
        } else {
            this.relativeY = y;
        }
    }

    public void setRelativeRotation(double rotation) {
        this.rotation = rotation;
    }
}
