package camp.computer.clay.util.ImageBuilder;

import java.util.List;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.Color;

public abstract class Shape extends Groupable {

//    public class ShapeBoundary {
//        public Shape shape;
//        public List<Transform> boundary;
//        public ShapeBoundary(Shape shape) {
//            this.shape = shape;
//            this.boundary = new ArrayList<>();
//        }
//    }

//    public static HashMap<Shape, ShapeBoundary> innerBoundaries = new HashMap<>();

    // <TODO>
    // TODO: 11/15/2016 Delete this after creating Shape component.
    protected String label = ""; // Component

    public boolean isBoundary = false;

    protected double targetTransparency = 1.0; // Visibility
    protected double transparency = targetTransparency;

    protected Transform imagePosition = null;
    protected Transform position = new Transform(0, 0);
    // </TODO>

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    public double outlineThickness = 1.0;

    // TODO: RIGHT NOW EVERY FUCKING SHAPE COMPUTES A BOUNDING BOX. MOST OF THEM ARE NOT EVEN WANTED. THIS MUST CHANGE! IN FILE FORMAT, ADD FLAG TO CREATE BOUNDS!
    // TODO: Start by moving boundary out of Shape into Image/Boundary.shapeBounds hashmap, with call to create bounds in Image/Boundary
    // TODO: Then only compute boundary for Shapes/Images that need it!
//    protected List<Transform> boundary;
//    protected ShapeBoundary shapeBoundary;

    public boolean isValid = false;

    /**
     * <em>Invalidates</em> the {@code Shape}. Invalidating a {@code Shape} causes its cached
     * geometry, such as its boundary, to be updated during the subsequent call to {@code updateImage()}.
     * <p>
     * Note that a {@code Shape}'s geometry cache will only ever be updated when it is first
     * invalidated by calling {@code invalidate()}. Therefore, to cause the {@code Shape}'s
     * geometry cache to be updated, call {@code invalidate()}. The geometry cache will be updated
     * in the first call to {@code updateImage()} following the call to {@code invalidate()}.
     */
    public void invalidate() {
        this.isValid = false;
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    private int layerIndex = DEFAULT_LAYER_INDEX;

    public int getLayerIndex() {
        return this.layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
        // parentImage.updateLayers();
    }
    // </LAYER>

    public Shape() {
    }

    public Shape(Transform position) {
        this.position.set(position);
    }

    // TODO: <DELETE>
    // TODO: Move into ImageSystem, PortableLayoutSystem, or RenderSystem
    // TODO: Replace with RelativeLayoutConstraint
    public void setImagePosition(Transform point) {
        if (imagePosition == null) {
            imagePosition = new Transform();
        }
        this.imagePosition.set(point.x, point.y);
        this.imagePosition.setRotation(point.rotation);
        invalidate();
    }
    // TODO: </DELETE>

    public Transform getImagePosition() {
        return this.imagePosition;
    }

    public Transform getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
        invalidate();
    }

    public void setPosition(Transform point) {
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

    public abstract List<Transform> getVertices();

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

    public String getLabel() {
        return this.label;
    }
}
