package camp.computer.clay.engine.component;

import camp.computer.clay.util.Geometry;

public class Transform extends Component {

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
     * The z coordinate's position relative to {@code referencePoint}. If {@code referencePoint} is
     * {@code null} then this is equivalent to an absolute position.
     */
    public double z = 0;

    /**
     * Relative rotation of the coordinate with which endpoints referencing this one will be
     * rotated.
     */
    public double rotation = 0;

    /**
     * Scale for the {@code Transform}.
     */
    public double scale = 1.0;

    /**
     * Rotation rotation in degrees
     */
    public Transform() {
        this(0, 0);
    }

    /**
     * Copy constructor. Creates a new {@code Transform} object with properties identical to those of
     * {@code otherPoint}.
     *
     * @param otherTransform The {@code Transform} to set.
     */
    public Transform(Transform otherTransform) {

        // <HACK>
        super();
        // </HACK>

        this.x = otherTransform.x;
        this.y = otherTransform.y;
    }

    public Transform(double x, double y) {

        // <HACK>
        super();
        // </HACK>

        this.x = x;
        this.y = y;
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
    public void set(Transform point) {
        x = point.x;
        y = point.y;
        rotation = point.rotation;
    }

    public void set(Transform point, Transform referencePoint) {
        double x2 = Geometry.distance(0, 0, point.x, point.y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, point.x, point.y)));
        this.x = referencePoint.x + x2;

        double y2 = Geometry.distance(0, 0, point.x, point.y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, point.x, point.y)));
        this.y = referencePoint.y + y2;
    }

    public void set(double x, double y, Transform referencePoint) {
        double x2 = Geometry.distance(0, 0, x, y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, x, y)));
        this.x = referencePoint.x + x2;

        double y2 = Geometry.distance(0, 0, x, y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, x, y)));
        this.y = referencePoint.y + y2;
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

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
