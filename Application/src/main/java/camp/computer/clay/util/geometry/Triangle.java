package camp.computer.clay.util.geometry;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.Shape;

public class Triangle<T extends Entity> extends Shape<T> {

    private Transform a = new Transform(0, 0);
    private Transform b = new Transform(0, 0);
    private Transform c = new Transform(0, 0);

    // Cached descriptive {@code Transform} geometry for the {@code Shape}.


    public Triangle(T entity) {
        this.entity = entity;
    }

    public Triangle(Transform position) {
        super(position);
    }

    public void setPoints(double width, double height) {
        a = new Transform(position.x + -(width / 2.0f), position.y + (height / 2.0f));
        b = new Transform(position.x + 0, position.y - (height / 2.0f));
        c = new Transform(position.x + (width / 2.0f), position.y + (height / 2.0f));
    }

    public void setPoints(Transform a, Transform b, Transform c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public List<Transform> getVertices() {
        List<Transform> vertices = new LinkedList<>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(c);
        return vertices;
    }
}