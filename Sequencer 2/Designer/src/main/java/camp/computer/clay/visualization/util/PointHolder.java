package camp.computer.clay.visualization.util;

public class PointHolder {
    // TODO: Add subscriber/publisher to automate geometry updates!

    // TODO: Update to use numbers that can be composed and given dependencies (used in
    // TODO: (cont'd) expressions) and dynamically update expressions. Do animations by giving them
    // TODO: (cont'd) quantity-change rules.
    // TODO: Refactor to support N dimensions, including rotation angles accordingly.
    private double x = 0;
    private double y = 0;
    // double z;

    /** Rotation angle in degrees */
    // TODO: Refactor so 0 degrees faces upward, not right.
    private double angle = 0;

    public PointHolder() {
        this(0, 0);
    }

    public PointHolder(double x, double y) {
        this(x, y, 0);
    }

    public PointHolder(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public void set (double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void offset (double dx, double dy) {
        this.x = this.x + dx;
        this.y = this.y + dy;
    }

    public void set (PointHolder pointHolder) {
        this.x = pointHolder.x;
        this.y = pointHolder.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

}
