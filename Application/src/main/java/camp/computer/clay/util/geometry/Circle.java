package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

/**
 * Circle. By default, objects are unit circles.
 */
public class Circle<T extends Entity> extends Shape<T> {

    /**
     * The number of vertices to use to approximate the circle. By default, this is setValue to 12,
     * corresponding to a vertex every 30 degrees.
     */
    private int vertexCount = 12;

    private double radius = 1.0;

    public Circle(double radius) {
        super(new Point(0, 0));
        this.radius = radius;

        updateGeometryCache();
    }

    public Circle(T entity) {
        this.entity = entity;

        updateGeometryCache();
    }

    public Circle(Point position, double radius) {
        super(position);
        this.radius = radius;

        updateGeometryCache();
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;

        updateGeometryCache();
    }

    protected void updateGeometryCache() {
        vertices = Geometry.getRegularPolygon(position, this.radius, vertexCount + 1);
    }

    protected List<Point> vertices = new ArrayList<>();

    /**
     * Returns list of pointerCoordinates on the perimeter of the circle that define a regular polygon that
     * approximates the circle.
     *
     * @return
     */
    @Override
    public List<Point> getVertices() {
        return vertices;
    }

    @Override
    public List<Line> getSegments() {
        //List<Point> vertices = getVertices();
        ArrayList<Line> segments = new ArrayList<>();
        for (int i = 0; i < vertices.size() - 1; i++) {
            segments.add(new Line(vertices.get(i), vertices.get(i + 1)));
        }
        return segments;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            Display.drawCircle(this, display);
        }
    }
}
