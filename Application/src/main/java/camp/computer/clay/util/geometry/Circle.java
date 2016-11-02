package camp.computer.clay.util.geometry;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.component.Transform;

/**
 * Circle. By default, objects are unit circles.
 */
public class Circle<T extends Entity> extends Shape { // <T> {

    /**
     * The index of boundary to use to approximate the circle. By default, this is setValue to 12,
     * corresponding to a vertex every 30 degrees.
     */
    public static int BOUNDARY_VERTEX_COUNT = 10;

    public double radius = 1.0;

    public Circle() {
        setup();
    }

    public Circle(double radius) {
        super(new Transform(0, 0));
        this.radius = radius;
        setup();
    }

//    public Circle(T entity) {
//        this.entity = entity;
//        setup();
//    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        this.boundary = Geometry.getRegularPolygon(position, this.radius, BOUNDARY_VERTEX_COUNT);
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public List<Transform> getVertices() {
        List<Transform> vertices = new LinkedList<>();
        int segmentCount = BOUNDARY_VERTEX_COUNT - 1;
        for (int i = 0; i < segmentCount; i++) {

            // Calculate point prior to rotation
            vertices.add(new Transform(
                    0 + radius * Math.cos(2.0f * Math.PI * (double) i / (double) segmentCount) + Math.toRadians(position.rotation),
                    0 + radius * Math.sin(2.0f * Math.PI * (double) i / (double) segmentCount) + Math.toRadians(position.rotation)
            ));
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
    public List<Transform> getBoundary() {
        return boundary;
    }
}
