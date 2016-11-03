package camp.computer.clay.util.BuilderImage;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.component.Transform;

public class Polyline<T extends Entity> extends Shape { // extends Shape<T> {

    private List<Transform> vertices = new ArrayList<>();

    public Polyline() {
        super();
        setup();
    }

//    public Polyline(T entity) {
//        this.entity = entity;
//        setup();
//    }
//
//    public Polyline(Transform position, List<Transform> vertices) {
//        super(position);
//        setup();
//        setVertices(vertices);
//    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        this.vertices.addAll(vertices);
    }

    public void setVertices(List<Transform> vertices) {
        this.vertices.addAll(vertices);
    }

    public void addVertex(Transform point) {
        this.vertices.add(point);
    }

    @Override
    public List<Transform> getVertices() {
        return vertices;
    }

    public List<Transform> getPoints() {
        return vertices;
    }

}
