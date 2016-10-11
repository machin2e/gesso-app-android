package camp.computer.clay.util.geometry;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Vertex<T extends Entity> extends Shape<T> {

    protected List<Point> vertices = new ArrayList<>();

    private Point vertex = new Point();

    public Vertex() {
        setup();
    }

    public Vertex(Point position) {
        super(position);
        setup();
    }

    public Vertex(T entity) {
        this.entity = entity;
        setup();
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        vertices = new ArrayList<>();
        vertices.add(vertex);
    }

    @Override
    public List<Point> temp_getRelativeVertices() {
        vertex.set(position);
        return vertices;
    }

    @Override
    public List<Point> getVertices() {
        return vertices;
    }

    @Override
    public List<Line> getSegments() {
        return null;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            // display.drawVertex(this);
        }

        // Draw bounding box!
        display.paint.setColor(Color.GREEN);
        display.paint.setStyle(Paint.Style.STROKE);
        display.paint.setStrokeWidth(2.0f);
        display.canvas.drawLine((float) position.x - 10, (float) position.y - 10, (float) position.x + 10, (float) position.y + 10, display.paint);
    }

    public void setX(double x) {
        this.position.x = x;
    }

    public void setY(double y) {
        this.position.y = y;
    }
}
