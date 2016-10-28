package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.Shape;

public class Point<T extends Entity> extends Shape<T> {

    public Point() {
        setup();
    }

    public Point(Transform position) {
        super(position);
        setup();
    }

    public Point(T entity) {
        this.entity = entity;
        setup();
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {
        boundary = new ArrayList<>();
        boundary.add(new Transform());
    }

    @Override
    protected List<Transform> getVertices() {
        List<Transform> vertices = new LinkedList<>();
        vertices.add(new Transform(imagePosition));
        return vertices;
    }

    @Override
    public void draw(PlatformRenderSurface platformRenderSurface) {
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
