package camp.computer.clay.visualization.util.geometry;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.visualization.util.Visibility;

public abstract class Shape {

    protected Visibility visibility = Visibility.VISIBLE;

    protected Point position = new Point(0, 0);

//    protected double rotation = 0;

    public Shape() {
    }

    public Shape(Point position) {
        this.position.set(position);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position.set(position);
    }

    public void setRotation(double angle) {
        this.position.setAngle(angle);
    }

    public double getRotation() {
        return this.position.getAngle();
    }

    abstract public List<Point> getVertices();

    abstract public List<Line> getSegments();

    public boolean containsPoint(Point point) {
        return Geometry.containsPoint(getVertices(), point);
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
}
