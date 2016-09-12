package camp.computer.clay.scene.util.geometry;

import android.util.Log;

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
    protected Point referenceCoordinate = null;

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
     * Creates a new {@code Point} positioned relative to {@code referenceCoordinate}.
     *
     * @param x                   The x coordinate of this {@code Point} relative to {@code referenceCoordinate}.
     * @param y                   The y coordinate of this {@code Point} relative to {@code referenceCoordinate}.
     * @param referenceCoordinate
     */
    public Point(double x, double y, Point referenceCoordinate) {
        setOrigin(referenceCoordinate);
        setRelativeX(x);
        setRelativeY(y);
    }

    public Point getOrigin() {
        return referenceCoordinate;
    }

    public void setOrigin(Point referenceCoordinate) {
        this.referenceCoordinate = referenceCoordinate;
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
//        if (referenceCoordinate != null) {
//            return referenceCoordinate.getX() + this.x;
////            return Geometry.calculateRotatedPoint(referenceCoordinate, getRotation(), this).getX();
//        } else {
//            return this.x;
//        }
        if (referenceCoordinate != null) {

            /*
            // Method (original)
//            return referenceCoordinate.getX() + this.x;
            //return referenceCoordinate.getX() + Geometry.calculateRotatedPoint(referenceCoordinate, referenceCoordinate.getRotation(), this).getX();
            */

            // TODO: Rotate center point WRT reference plane!
//            Point localOrigin = new Point(referenceCoordinate.getX(), referenceCoordinate.getY());
            //Point localOrigin = new Point(referenceCoordinate.getRelativeX(), referenceCoordinate.getRelativeY());
//            Point localOrigin = new Point(0, 0);
            Point localOrigin = new Point(0, 0);
            Point localPoint = new Point(x, y);
//            Log.v("Points", "local: " + localPoint.getX() + ", " + localPoint.getY());
            Point rotatedLocalPoint = new Point();

            double xOffset = Geometry.calculateDistance(localOrigin, localPoint);
            double rotationOffset = Geometry.calculateRotationAngle(localOrigin, localPoint);

            //rotatedLocalPoint.setX(0 + Geometry.calculateDistance(localOrigin, localPoint) * Math.cos(Math.toRadians(referenceCoordinate.getRotation())));
            //rotatedLocalPoint.setX(0 + Geometry.calculateDistance(localOrigin, localPoint) * Math.cos(Math.toRadians(Geometry.calculateRotationAngle(localOrigin, localPoint))));
//            point.setY(originPoint.getY() + distance * Math.sin(Math.toRadians(rotation)));
            //return referenceCoordinate.getX() + rotatedLocalPoint.getX();
//            Log.v("Points", "local: " + referenceCoordinate.getX() + ", " + referenceCoordinate.getY());

            rotatedLocalPoint.setX(0 + xOffset * Math.cos(Math.toRadians(rotationOffset)));

            //rotatedLocalPoint.setY(Geometry.calculateDistance(localOrigin, localPoint) * Math.sin(Math.toRadians(referenceCoordinate.getRotation())));
//            rotatedLocalPoint.setX(Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRotation(), rotatedLocalPoint).getX());
            //rotatedLocalPoint.setX(Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint).getX());
//            rotatedLocalPoint.setX(Geometry.calculateRotatedPoint(referenceCoordinate, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint).getX());


            Point rotatedPoint = Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRotation(), localPoint);

//            if (x > 0) {
            //return referenceCoordinate.getX() + rotatedLocalPoint.getX();
            return referenceCoordinate.getX() + rotatedPoint.getX();
//            } else {
//                return referenceCoordinate.getX() - rotatedLocalPoint.getX();
//            }

            /*
            // Method (from above)
            Point localOrigin = new Point(0, 0);
            Point localPoint = new Point(x, y);
            Point rotatedLocalPoint = new Point();
            rotatedLocalPoint.setX(0 + Geometry.calculateDistance(localOrigin, localPoint) * Math.cos(Math.toRadians(Geometry.calculateRotationAngle(localOrigin, localPoint))));
            rotatedLocalPoint.setX(Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint).getX());
            return referenceCoordinate.getX() + rotatedLocalPoint.getX();
            */

            /*
            // Method (from above)
            Point localOrigin = new Point(0, 0);
            Point localPoint = new Point(x, y);
            Point rotatedLocalPoint = new Point();
            rotatedLocalPoint.setX(0 + Geometry.calculateDistance(localOrigin, localPoint) * Math.cos(Math.toRadians(Geometry.calculateRotationAngle(localOrigin, localPoint))));
            rotatedLocalPoint.setX(Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint).getX());
            return referenceCoordinate.getX() + rotatedLocalPoint.getX();
            */
//
//            // Method
//            Point referenceOrigin = referenceCoordinate;
//            double referenceRotation = referenceCoordinate.getRotation();
//            Point localPoint = new Point(x, y);
//
//            //Point rotatedLocalPoint = Geometry.calculateRotatedPoint(referenceOrigin, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint);
//            Point rotatedLocalPoint = Geometry.calculateRotatedPoint(referenceOrigin, referenceCoordinate.getRotation(), localPoint);
//
//            return rotatedLocalPoint.getX();

//            return Geometry.calculateRotatedPoint(referenceCoordinate, getRotation(), this).getX();
        } else {
            return this.x;
        }
    }

    /**
     * @return Absolute y coordinate.
     */
    public double getY() {
        if (referenceCoordinate != null) {
//            return referenceCoordinate.getY() + this.y;
//            return referenceCoordinate.getX() + Geometry.calculateRotatedPoint(referenceCoordinate, referenceCoordinate.getRotation(), this).getY();

//            Point localOrigin = new Point(referenceCoordinate.getX(), referenceCoordinate.getY());
//            Point localOrigin = new Point(referenceCoordinate.getRelativeX(), referenceCoordinate.getRelativeY());
            Point localOrigin = new Point();
            Point localPoint = new Point(x, y);
            Point rotatedLocalPoint = new Point();

            double yOffset = Geometry.calculateDistance(localOrigin, localPoint);
            double rotationOffset = Geometry.calculateRotationAngle(localOrigin, localPoint);

//            rotatedLocalPoint.setY(Geometry.calculateDistance(localOrigin, localPoint) * Math.sin(Math.toRadians(referenceCoordinate.getRotation())));
            rotatedLocalPoint.setY(0 + yOffset * Math.sin(Math.toRadians(rotationOffset)));
//            point.setY(originPoint.getY() + distance * Math.sin(Math.toRadians(rotation)));

            //rotatedLocalPoint.setY(Geometry.calculateDistance(localOrigin, localPoint) * Math.sin(Math.toRadians(referenceCoordinate.getRotation())));
//            rotatedLocalPoint.setY(Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRotation(), rotatedLocalPoint).getY());
            //rotatedLocalPoint.setY(Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint).getY());
//            rotatedLocalPoint.setY(Geometry.calculateRotatedPoint(referenceCoordinate, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint).getY());


//            if (getRelativeY() > 0) {
//                return referenceCoordinate.getY() + rotatedLocalPoint.getY();
//            } else {
//            return referenceCoordinate.getY() + rotatedLocalPoint.getY();
//            }

            //Point rotatedPoint = Geometry.calculateRotatedPoint(localOrigin, rotationOffset, localPoint);
            Point rotatedPoint = Geometry.calculateRotatedPoint(localOrigin, referenceCoordinate.getRotation(), localPoint);

//            if (x > 0) {
            //return referenceCoordinate.getX() + rotatedLocalPoint.getX();
            return referenceCoordinate.getY() + rotatedPoint.getY();

//            return Geometry.calculateRotatedPoint(referenceCoordinate, getRotation(), this).getY();

//            // Method
//            Point referenceOrigin = referenceCoordinate;
//            double referenceRotation = referenceCoordinate.getRotation();
//            Point localPoint = new Point(x, y);
//
//            //Point rotatedLocalPoint = Geometry.calculateRotatedPoint(referenceOrigin, referenceCoordinate.getRelativeRotation(), rotatedLocalPoint);
//            Point rotatedLocalPoint = Geometry.calculateRotatedPoint(referenceOrigin, referenceCoordinate.getRotation(), localPoint);
//
//            return rotatedLocalPoint.getY();
        } else {
            return this.y;
        }
    }

    public double getRotation() {
//        return this.rotation;
        if (referenceCoordinate != null) {
            return referenceCoordinate.getRotation() + this.rotation;
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
        setX(point.getX());
        setY(point.getY());
    }

    /**
     * @param x Absolute x coordinate. Converted to a relative x position internally.
     */
    public void setX(double x) {
        if (referenceCoordinate != null) {

//            Point newPoint = new Point(x, getY());
//            Point rotatedPoint = Geometry.calculateRotatedPoint(referenceCoordinate, referenceCoordinate.getRotation(), newPoint);
//            this.x = rotatedPoint.getX() - referenceCoordinate.getX();

            this.x = x - referenceCoordinate.getX();
        } else {
            this.x = x;
        }
    }

    /**
     * @param y Absolute y coordinate. Converted to a relative y position internally.
     */
    public void setY(double y) {
        if (referenceCoordinate != null) {

//            Point newPoint = new Point(getX(), y);
//            Point rotatedPoint = Geometry.calculateRotatedPoint(referenceCoordinate, referenceCoordinate.getRotation(), newPoint);
//            this.x = rotatedPoint.getY() - referenceCoordinate.getY();

            this.y = y - referenceCoordinate.getY();
        } else {
            this.y = y;
        }
    }

    public void setRelativeRotation(double rotation) {
//        if (referenceCoordinate != null) {
//            this.rotation = rotation - referenceCoordinate.getRotation();
//        } else {
        this.rotation = rotation;
//        }
    }

}
