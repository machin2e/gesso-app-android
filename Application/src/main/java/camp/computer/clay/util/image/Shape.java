package camp.computer.clay.util.image;

import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Line;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;

public abstract class Shape<T extends Entity> {

    protected String label = "";

    protected Visibility visibility = new Visibility(Visibility.Value.VISIBLE);
    protected double targetTransparency = 1.0;
    protected double transparency = targetTransparency;

    protected Point position = new Point(0, 0);

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    public double outlineThickness = 1.0;

    protected T entity = null;

    public Shape() {
    }

    public Shape(T entity) {
        this.entity = entity;
    }

    public Shape(Point position) {
        this.position.set(position);
    }

    public T getEntity() {
        return this.entity;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void setPosition(Point point) {
        this.position.set(point.getX(), point.getY());
    }

    public void setRotation(double angle) {
        this.position.setRelativeRotation(angle);
    }

    public double getRotation() {
        return this.position.getRotation();
    }

    public void setReferencePoint(Point point) {
        this.position.setReferencePoint(point);
    }

    public Point getReferencePoint() {
        return this.position.getReferencePoint();
    }

    abstract public List<Point> getVertices();

    /**
     * Returns the axis-aligned minimum bounding box for the setValue of vertices that define the shape.
     *
     * @return A {@code Rectangle} representing the minimum bounding box.
     * @see <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">Minimum bounding box</a>
     */
    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getVertices());
    }

    abstract public List<Line> getSegments();

    public boolean contains(Point point) {
        return Geometry.contains(getVertices(), point);
    }

    public void setVisibility(Visibility.Value visibility) {
        this.visibility.setValue(visibility);
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public boolean isVisible() {
        return visibility.getValue() == Visibility.Value.VISIBLE;
    }

    public void setColor(String color) {
        this.color = color;

        // <ANDROID>
        this.colorCode = android.graphics.Color.parseColor(color);
        // </ANDROID>
    }

    public String getColor() {
        return color;
    }

    // <ANDROID>
    public int colorCode = android.graphics.Color.WHITE;
    public int outlineColorCode = android.graphics.Color.BLACK;
    // </ANDROID>

    public void setTransparency(final double transparency) {
        this.targetTransparency = transparency;

        // Color
        int intColor = android.graphics.Color.parseColor(getColor());
        intColor = Color.setTransparency(intColor, this.targetTransparency);
        setColor(Color.getHexColorString(intColor));

        // Outline Color
        int outlineColorIndex = android.graphics.Color.parseColor(getOutlineColor());
        outlineColorIndex = Color.setTransparency(outlineColorIndex, this.targetTransparency);
        setOutlineColor(Color.getHexColorString(outlineColorIndex));

        this.transparency = this.targetTransparency;
    }

    public void setOutlineColor(String color) {
        this.outlineColor = color;

        // <ANDROID>
        this.outlineColorCode = android.graphics.Color.parseColor(color);
        // </ANDROID>
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

    public void update() {

        position.update();

        //Log.v("OnUpdate", "Shape.update");
        if (onUpdateListener != null) {
            onUpdateListener.onUpdate(this);
        }
    }

    public abstract void draw(Display display);

    protected OnUpdateListener onUpdateListener = null;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }
}
