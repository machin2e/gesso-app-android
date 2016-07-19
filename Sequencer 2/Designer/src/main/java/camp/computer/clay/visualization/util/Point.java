package camp.computer.clay.visualization.util;

public class Point {
    // TODO: Add subscriber/publisher to automate geometry updates!

    // TODO: Update to use numbers that can be composed and given dependencies (used in
    // TODO: (cont'd) expressions) and dynamically update expressions. Do animations by giving them
    // TODO: (cont'd) quantity-change rules.
    // TODO: Refactor to support N dimensions, including rotation angles accordingly.
    private double x = 0;
    private double y = 0;

    /** Rotation angle in degrees */
    // TODO: Refactor so 0 degrees faces upward, not right.

    public Point() {
        this(0, 0);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void set (double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void offset (double dx, double dy) {
        this.x = this.x + dx;
        this.y = this.y + dy;
    }

    public void set (Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

}
