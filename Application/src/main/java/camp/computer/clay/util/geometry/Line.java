package camp.computer.clay.util.geometry;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.image.Shape;

public class Line<T extends Entity> extends Shape<T> {

    public Line() {
        super();
        setup();
    }

//    public Line(T entity) {
//        super(entity);
//        setup();
//    }

    public Line(Point position, double rotation) {
        setup();
        position.set(position);
        position.setRotation(rotation);
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
    }

    @Override
    protected List<Point> getVertices() {
        List<Point> vertices = new LinkedList<>();
//        vertices.add(new Point(source));
//        vertices.add(new Point(target));
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
//            display.drawSegment(this);
        }
    }

    /**
     * Returns a {@code Point} on the {@code Line} offset from {@code position} by {@code offset}.
     *
     * @param offset
     * @return
     */
    public Point getPoint(double offset) {
        return Geometry.getRotateTranslatePoint(position, position.rotation, offset);
    }
}
