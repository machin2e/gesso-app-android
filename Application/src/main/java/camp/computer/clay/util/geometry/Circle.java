package camp.computer.clay.util.geometry;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

/**
 * Circle. By default, objects are unit circles.
 */
public class Circle<T extends Entity> extends Shape<T> {

    protected List<Point> vertices = new ArrayList<>();

    protected List<Point> bounds = new ArrayList<>();

    /**
     * The index of vertices to use to approximate the circle. By default, this is setValue to 12,
     * corresponding to a vertex every 30 degrees.
     */
    private int vertexCount = 10;

    public double radius = 1.0;

    public Circle(double radius) {
        super(new Point(0, 0));
        this.radius = radius;
        setup();
    }

    public Circle(T entity) {
        this.entity = entity;
        setup();
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        this.vertices = Geometry.getRegularPolygon(position, this.radius, vertexCount);
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public List<Point> getBaseVertices() {
        int segmentCount = vertexCount - 1;
        for (int i = 0; i < segmentCount; i++) {

            // Calculate point prior to rotation
            vertices.get(i).set(
                    0 + radius * Math.cos(2.0f * Math.PI * (double) i / (double) segmentCount) + Math.toRadians(position.rotation),
                    0 + radius * Math.sin(2.0f * Math.PI * (double) i / (double) segmentCount) + Math.toRadians(position.rotation)
            );
        }
        return vertices;
    }

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
            display.drawCircle(this);

            /*
            // Draw bounding box!
            display.paint.setColor(Color.GREEN);
            display.paint.setStyle(Paint.Style.STROKE);
            display.paint.setStrokeWidth(2.0f);
            display.drawPolygon(getVertices());
            */
        }
    }
}
