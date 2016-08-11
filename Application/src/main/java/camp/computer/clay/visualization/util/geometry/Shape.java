package camp.computer.clay.visualization.util.geometry;

import android.util.Log;

import java.util.List;

import camp.computer.clay.visualization.util.Visibility;

public abstract class Shape {

    protected String label = null;

    protected Visibility visibility = Visibility.VISIBLE;

    protected Point position = new Point(0, 0);

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    protected double outlineThickness = 1.0;

    public Shape() {
    }

    public Shape(Point position) {
        this.position.set(position);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void setRotation(double angle) {
        this.position.setRotation(angle);
    }

    public double getRotation() {
        return this.position.getRotation();
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

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setOutlineColor(String color) {
        this.outlineColor = color;
    }

    public String getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineThickness(double thickness) {
        this.outlineThickness = thickness;
    }

    public double getOutlineThickness() {
        return outlineThickness;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean hasLabel() {
        return this.label != null && this.label.length() > 0;
    }

    public String getLabel() {
        return this.label;
    }
}
