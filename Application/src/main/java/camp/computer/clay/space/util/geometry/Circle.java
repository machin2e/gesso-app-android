package camp.computer.clay.space.util.geometry;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Entity;
import camp.computer.clay.space.architecture.Shape;

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
    }

    public Circle(T entity) {
        this.entity = entity;
    }

    public Circle(Point position, double radius) {
        super(position);
        this.radius = radius;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Returns list of pointerCoordinates on the perimeter of the circle that define a regular polygon that
     * approximates the circle.
     *
     * @return
     */
    @Override
    public List<Point> getVertices() {
        return Geometry.getRegularPolygon(position, this.radius, vertexCount + 1);
    }

    @Override
    public List<Line> getSegments() {
        List<Point> vertices = getVertices();
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

            display.getPaint().setColor(Color.GREEN);
            //Surface.drawCircle(position, this.radius / 2, getRotation(), surface);

            //Surface.drawPolygon(getVertices(), surface);
        }
    }
}
