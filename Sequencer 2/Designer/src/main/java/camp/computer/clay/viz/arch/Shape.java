package camp.computer.clay.viz.arch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Line;
import camp.computer.clay.viz.util.Point;

public abstract class Shape {

    protected HashMap<String, String> styles = new HashMap();

    protected Visibility visibility = Visibility.VISIBLE;

    protected Point position = new Point(0, 0);

    protected double rotation = 0;

    protected double scale = 1.0;

    protected OnTouchActionListener onTouchActionListener;

    public Shape() {
    }

    public Shape(Point position) {
        this.position.set(position);
    }

    public boolean hasStyle(String label) {
        return this.styles.get(label) != null;
    }

    public String getStyle(String label) {
        return this.styles.get(label);
    }

    public void setStyle(String label, String description) {
        this.styles.put(label, description);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position.set(position);
    }

    public void setRotation(double angle) {
        this.rotation = angle;
    }

    public double getRotation() {
        return rotation;
    }

    abstract public List<Point> getVertices();

    abstract public List<Line> getSegments();

    public boolean containsPoint(Point point) {
        return Geometry.containsPoint(getVertices(), point);
    }

    public static List<Point> getRegularPolygon(Point position, double radius, int segmentCount) {

        List<Point> vertices = new ArrayList<>();

        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < segmentCount; i++) {

            Point vertexPosition = new Point(
                    (position.getX() + radius * Math.cos(2.0f * Math.PI * (double) i / (double) segmentCount)),
                    (position.getY() + radius * Math.sin(2.0f * Math.PI * (double) i / (double) segmentCount))
            );

            vertices.add(vertexPosition);

            // Draw points in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertexPosition.getX(), (float) vertexPosition.getY());
            }

            path.lineTo((float) vertexPosition.getX(), (float) vertexPosition.getY());
        }

        path.close();

        return vertices;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public boolean isVisible() {
        return visibility == Visibility.VISIBLE;
    }

    public void setOnTouchActionListener(OnTouchActionListener onTouchActionListener) {
        this.onTouchActionListener = onTouchActionListener;
    }

    public void touch(TouchInteraction touchInteraction) {
        if (onTouchActionListener != null) {
            onTouchActionListener.onAction(touchInteraction);
        }
    }

    public boolean isTouching(Point point) {
        if (containsPoint(point)) {
//            Log.v("Touching", "Shape.isTouching = true: " + this);
            return true;
        } else {
            return false;
        }
    }

    public abstract void draw(Viz viz);

}
