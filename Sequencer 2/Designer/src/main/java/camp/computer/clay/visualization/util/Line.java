package camp.computer.clay.visualization.util;

public class Line {
    private Point source = new Point(0, 0);
    private Point target = new Point(0, 0);

    public Line () {}

    public Line (Point source, Point target) {
        this.source = source;
        this.target = target;
    }

    public Point getSource() {
        return this.source;
    }

    public Point getTarget() {
        return this.target;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public double getLength() {
        return Geometry.calculateDistance(source, target);
    }
}
