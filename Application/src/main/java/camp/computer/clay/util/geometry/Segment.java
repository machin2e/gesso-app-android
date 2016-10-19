package camp.computer.clay.util.geometry;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.image.Shape;

public class Segment<T extends Entity> extends Shape<T> {

    protected Point source = new Point();
    protected Point target = new Point();

    public Segment() {
        super();
        setup();
    }

//    public Segment(T entity) {
//        super(entity);
//        setup();
//    }

    public Segment(Point source, Point target) {
        setup();
        set(source, target);
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        boundary.add(new Point(source));
        boundary.add(new Point(target));
    }

    @Override
    protected List<Point> getVertices() {
        List<Point> vertices = new LinkedList<>();
        vertices.add(new Point(source));
        vertices.add(new Point(target));
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawSegment(this);
        }
    }

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source.set(source);

        // Update Position
        position.set(
                (this.target.x - this.source.x) / 2.0,
                (this.target.y - this.source.y) / 2.0
        );

        invalidate();
    }

    public Point getTarget() {
        return target;
    }

    public void setTarget(Point target) {
        this.target.set(target);

        // Update Position
        position.set(
                (this.target.x - this.source.x) / 2.0,
                (this.target.y - this.source.y) / 2.0
        );

        invalidate();
    }

    public void set(Point source, Point target) {
        this.source.set(source);
        this.target.set(target);

        // Update Position
        position.set(
                (this.target.x - this.source.x) / 2.0,
                (this.target.y - this.source.y) / 2.0
        );

        invalidate();
    }
}
