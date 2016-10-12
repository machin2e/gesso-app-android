package camp.computer.clay.util.image;

import android.util.Log;

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

    protected Point imagePosition = null;
    protected Point position = new Point(0, 0);

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    public double outlineThickness = 1.0;

    protected T entity = null;

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    protected int layerIndex = DEFAULT_LAYER_INDEX;

    public int getLayerIndex() {
        return this.layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
//        parentImage.sortShapesByLayer();
    }
    // </LAYER>

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

    public void setImagePosition(double x, double y) {
        if (imagePosition == null) {
            imagePosition = new Point();
        }
        this.imagePosition.set(x, y);
    }

    public void setImagePosition(Point point) {
        if (imagePosition == null) {
            imagePosition = new Point();
        }
        this.imagePosition.set(point.x, point.y);
        this.imagePosition.setRotation(point.rotation);
    }

    public Point getImagePosition() {
        return this.imagePosition;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void setPosition(Point point) {
        this.position.set(point.x, point.y);
    }

    public void setRotation(double angle) {
        this.position.rotation = angle;
    }

    public double getRotation() {
        return this.position.rotation;
    }

    abstract public List<Point> getVertices();

    /**
     * Returns the axis-aligned minimum bounding box for the setValue of vertices that define the shape.
     *
     * @return A {@code Rectangle} representing the minimum bounding box.
     * @see <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">Minimum bounding box</a>
     */
    public Rectangle getBoundingBox() {
        return Geometry.getBoundingBox(getVertices());
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
        Log.v("BBBB", "shape.position.x: " + position.x + ", y: " + position.y);
    }

    /**
     * Updates the {@code Shape}'s geometry. Specifically, computes the absolute positioning,
     * rotation, and scaling in preparation for drawing and collision detection.
     *
     * @param referenceImage Position of the containing {@code Image} relative to which the
     *                       {@code Shape} will be drawn.
     */
    protected void updateGeometry(Image referenceImage) {

        updatePosition(referenceImage); // Update the position
        updateRotation(referenceImage); // Update rotation
        updateBoundary(referenceImage); // Update the bounds (using the results from the update position and rotation)
    }

    /**
     * Translate the center position of the {@code Shape}. Effectively, this updates the position
     * of the {@code Shape}.
     *
     * @param referenceImage
     */
    private void updatePosition(Image referenceImage) {
        updatePositionX(referenceImage);
        updatePositionY(referenceImage);
    }

    /**
     * @return Updates absolute x coordinate of {@code Shape} relative to this {@code Image}.
     */
    private void updatePositionX(Image referenceImage) {
        double x = Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.cos(Math.toRadians(referenceImage.position.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
        position.x = referenceImage.position.x + x;
    }

    /**
     * @return Updates absolute y coordinate of {@code Shape} relative to this {@code Image}.
     */
    private void updatePositionY(Image referenceImage) {
        double y = Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.sin(Math.toRadians(referenceImage.position.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
        position.y = referenceImage.position.y + y;
    }

    private void updateRotation(Image referenceImage) {
        this.position.rotation = referenceImage.position.rotation + imagePosition.rotation;
    }

    // TODO: Delete! Refactor this out.
    public List<Point> getBaseVertices() {
        return getVertices(); // HACK
    }

    /**
     * Updates the bounds of the {@code Shape} for use in touch interaction, layout, and collision
     * detection.
     *
     * @param referenceImage
     */
    private void updateBoundary(Image referenceImage) {

        getBaseVertices();
        List<Point> vertices = getVertices();

        // Translate and rotate the vertices about the updated position
        for (int i = 0; i < vertices.size(); i++) {
            Geometry.rotatePoint(vertices.get(i), position.rotation); // Rotate Shape vertices about Image position
            Geometry.translatePoint(vertices.get(i), position.x, position.y); // Translate Shape
        }
    }

    public abstract void draw(Display display);
}
