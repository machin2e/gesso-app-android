package camp.computer.clay.scene.util.geometry;

import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Feature;
import camp.computer.clay.scene.util.Color;
import camp.computer.clay.scene.util.Visibility;

public abstract class Shape<T extends Feature> {

    protected String label = "";

    protected Visibility visibility = Visibility.VISIBLE;
    protected double targetTransparency = 1.0;
    protected double transparency = targetTransparency;

    protected Point position = new Point(0, 0);

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    protected double outlineThickness = 1.0;

    protected T feature = null;

    public Shape() {
    }

    public Shape(T feature) {
        this.feature = feature;
    }

    public Shape(Point position) {
        this.position.set(position);
    }

    public T getFeature() {
        return this.feature;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void setRotation(double angle) {
        this.position.setRelativeRotation(angle);
    }

    public double getRotation() {
        return this.position.getRotation();
    }

    public void setOrigin(Point point) {
        this.position.setOrigin(point);
    }

    public Point getOrigin() {
        return this.position.getOrigin();
    }

    abstract public List<Point> getVertices();

    /**
     * Returns the axis-aligned minimum bounding box for the set of vertices that define the shape.
     *
     * @return A {@code Rectangle} representing the minimum bounding box.
     * @see <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">Minimum bounding box</a>
     */
    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getVertices());
    }

    abstract public List<Line> getSegments();

    public boolean contains(Point point) {
        return Geometry.containsPoint(getVertices(), point);
    }

//    public boolean contains(Point point) {
//        if (isVisible()) {
//            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (((Rectangle) getShape("Board")).getHeight() / 2.0f);
//        } else {
//            return false;
//        }
//    }

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

    public abstract void draw(Display display);
}
