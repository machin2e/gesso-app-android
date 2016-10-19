package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.image.Shape;

public class Vertex<T extends Entity> extends Shape<T> {

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
        boundary = new ArrayList<>();
        boundary.add(new Point());
    }

    @Override
    protected List<Point> getVertices() {
        List<Point> vertices = new LinkedList<>();
        vertices.add(new Point(imagePosition));
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            // display.drawVertex(this);
        }

        /*
        // Draw bounding box!
        display.paint.setColor(Color.GREEN);
        display.paint.setStyle(Paint.Style.STROKE);
        display.paint.setStrokeWidth(2.0f);
        display.canvas.drawSegment((float) position.x - 10, (float) position.y - 10, (float) position.x + 10, (float) position.y + 10, display.paint);
        */
    }
}
