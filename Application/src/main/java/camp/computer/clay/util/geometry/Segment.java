package camp.computer.clay.util.geometry;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.Shape;

public class Segment<T extends Entity> extends Shape<T> {

    protected Transform source = new Transform();
    protected Transform target = new Transform();

    public Segment() {
        super();
        setup();
    }

//    public Segment(T entity) {
//        super(entity);
//        setup();
//    }

    public Segment(Transform source, Transform target) {
        setup();
        set(source, target);
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        boundary.add(new Transform(source));
        boundary.add(new Transform(target));
    }

    @Override
    protected List<Transform> getVertices() {
        List<Transform> vertices = new LinkedList<>();
        vertices.add(new Transform(source));
        vertices.add(new Transform(target));
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawSegment(this);
        }
    }

    public Transform getSource() {
        return source;
    }

    public void setSource(Transform source) {
        this.source.set(source);

        // Update Position
        position.set(
                (this.target.x - this.source.x) / 2.0,
                (this.target.y - this.source.y) / 2.0
        );

        invalidate();
    }

    public Transform getTarget() {
        return target;
    }

    public void setTarget(Transform target) {
        this.target.set(target);

        // Update Position
        position.set(
                (this.target.x - this.source.x) / 2.0,
                (this.target.y - this.source.y) / 2.0
        );

        invalidate();
    }

    public void set(Transform source, Transform target) {
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
