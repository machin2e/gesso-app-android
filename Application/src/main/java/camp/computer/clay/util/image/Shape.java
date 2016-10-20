package camp.computer.clay.util.image;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;

public abstract class Shape<T extends Entity> extends Groupable {

    protected String label = "";

    protected Visibility visibility = Visibility.VISIBLE;
    protected double targetTransparency = 1.0;
    protected double transparency = targetTransparency;

    protected Point imagePosition = null;
    protected Point position = new Point(0, 0);

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    public double outlineThickness = 1.0;

    protected T entity = null;

    protected List<Point> boundary = new ArrayList<>();

    protected boolean isValid = false;

    /**
     * <em>Invalidates</em> the {@code Shape}. Invalidating a {@code Shape} causes its cached
     * geometry, such as its boundary, to be updated during the subsequent call to {@code update()}.
     * <p>
     * Note that a {@code Shape}'s geometry cache will only ever be updated when it is first
     * invalidated by calling {@code invalidate()}. Therefore, to cause the {@code Shape}'s
     * geometry cache to be updated, call {@code invalidate()}. The geometry cache will be updated
     * in the first call to {@code update()} following the call to {@code invalidate()}.
     */
    public void invalidate() {
        this.isValid = false;
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    protected int layerIndex = DEFAULT_LAYER_INDEX;

    public int getLayerIndex() {
        return this.layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
//        parentImage.updateLayers();
    }
    // </LAYER>

    public Shape() {
    }

//    public Shape(T entity) {
//        this.entity = entity;
//    }

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
        invalidate();
    }

    public void setImagePosition(Point point) {
        if (imagePosition == null) {
            imagePosition = new Point();
        }
        this.imagePosition.set(point.x, point.y);
        this.imagePosition.setRotation(point.rotation);
        invalidate();
    }

    public Point getImagePosition() {
        return this.imagePosition;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
        invalidate();
    }

    public void setPosition(Point point) {
        this.position.set(point.x, point.y);
        invalidate();
    }

    public void setRotation(double angle) {
        this.position.rotation = angle;
        invalidate();
    }

    public double getRotation() {
        return this.position.rotation;
    }

    protected abstract List<Point> getVertices();

    public abstract void draw(Display display);

    public List<Point> getBoundary() {
        return this.boundary;
    }

    public boolean contains(Point point) {
        return Geometry.contains(getBoundary(), point);
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

    public void update(Point referencePoint) {
        updateGeometry(referencePoint);
    }

    /**
     * Updates the {@code Shape}'s geometry. Specifically, computes the absolute positioning,
     * rotation, and scaling in preparation for drawing and collision detection.
     *
     * @param referencePoint Position of the containing {@code Image} relative to which the
     *                       {@code Shape} will be drawn.
     */
    protected void updateGeometry(Point referencePoint) {

        if (!isValid) {
            updatePosition(referencePoint); // Update the position
            updateRotation(referencePoint); // Update rotation
            updateBoundary(); // Update the bounds (using the results from the update position and rotation)
            isValid = true;
        }
    }

    /**
     * Updates the x and y coordinates of {@code Shape} relative to this {@code Image}. Translate
     * the center position of the {@code Shape}. Effectively, this updates the position of the
     * {@code Shape}.
     *
     * @param referencePoint
     */
    private void updatePosition(Point referencePoint) {
        position.x = referencePoint.x + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
        position.y = referencePoint.y + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
    }

    private void updateRotation(Point referencePoint) {
        this.position.rotation = referencePoint.rotation + imagePosition.rotation;
    }

    /**
     * Updates the bounds of the {@code Shape} for use in touch interaction, layout, and collision
     * detection.
     */
    protected void updateBoundary() {

        List<Point> vertices = getVertices();
        List<Point> boundary = getBoundary();

        // Translate and rotate the boundary about the updated position
        for (int i = 0; i < vertices.size(); i++) {
            boundary.get(i).set(vertices.get(i));
            Geometry.rotatePoint(boundary.get(i), position.rotation); // Rotate Shape boundary about Image position
            Geometry.translatePoint(boundary.get(i), position.x, position.y); // Translate Shape
        }
    }
}
