package camp.computer.clay.lib.Geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.component.Transform;

public class Segment extends Shape {

    protected Transform source = new Transform();
    protected Transform target = new Transform();

    public Segment() {
        super();
        setup();
    }

    public Segment(Transform source, Transform target) {
        setup();
        set(source, target);
    }

    private void setup() {
    }

    @Override
    public List<Transform> getVertices() {
        List<Transform> vertices = new ArrayList<>();
        vertices.add(new Transform(source));
        vertices.add(new Transform(target));
        return vertices;
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
    }

    public void set(Transform source, Transform target) {
        this.source.set(source);
        this.target.set(target);

        // Update Position
        position.set(
                (this.target.x - this.source.x) / 2.0,
                (this.target.y - this.source.y) / 2.0
        );
    }
}
