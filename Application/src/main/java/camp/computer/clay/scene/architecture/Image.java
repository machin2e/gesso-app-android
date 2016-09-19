package camp.computer.clay.scene.architecture;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Feature;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.EventListener;
import camp.computer.clay.scene.util.Color;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;
import camp.computer.clay.scene.util.Visibility;

public abstract class Image<T extends Feature> {

    protected List<Shape> shapes = new LinkedList<>();

    protected Point position = new Point(0, 0); // Image position

    protected double scale = 1.0; // Image scale factor

    protected Visibility visibility = Visibility.VISIBLE;

    protected double targetTransparency = 1.0;

    protected double transparency = targetTransparency;

    protected T feature = null;

    protected Scene scene = null;

    // TODO: Make this an interface? Move interface out of class.
    protected EventListener eventListener;

    public Image(T feature) {
        this.feature = feature;
    }

    public T getFeature() {
        return this.feature;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return this.scene;
    }

    // TODO: Delete this after deleting PortImage
    public Image getParentImage() {
        if (getFeature().hasParent()) {
            Feature parentFeature = getFeature().getParent();
            return getScene().getImage(parentFeature);
        }
        return null;
    }

    public Point getPosition() {
        return this.position;
    }

    public double getRotation() {
        return this.position.getRotation();
    }

    public double getScale() {
        return this.scale;
    }

    public void setPosition(Point position) {
        this.position.set(position.getX(), position.getY());
    }

    public void setRotation(double angle) {
        this.position.setRelativeRotation(angle);
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean isVisible() {
        return visibility == Visibility.VISIBLE;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void show() {
        setVisibility(Visibility.VISIBLE);
    }

    public void hide() {
        setVisibility(Visibility.INVISIBLE);
    }

    public void addShape(Shape shape) {
        shape.getPosition().setOrigin(getPosition());
        shapes.add(shape);
    }

    public <T extends Shape> void addShape(T shape, String label) {
        shape.setLabel(label);
        shape.getPosition().setOrigin(getPosition());
        shapes.add(shape);
    }

    public Shape getShape(int index) {
        return shapes.get(index);
    }

    public Shape getShape(String label) {
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.getLabel().equals(label)) {
                return shape;
            }
        }
        return null;
    }

    public Shape getShape(Feature feature) {
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.getFeature() == feature) {
                return shape;
            }
        }
        return null;
    }

    public Shape getShapeByPosition(Point point) {
        List<Shape> shapes = getShapes().getList();
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.contains(point)) {
                return shape;
            }
        }
        return null;
    }

    public ShapeGroup getShapes() {
        ShapeGroup shapeGroup = new ShapeGroup();
        shapeGroup.add(this.shapes);
        return shapeGroup;
    }

    public <T extends Feature> ShapeGroup getShapes(Class<?>... types) {
        return getShapes().filterType(types);
    }

    /**
     * Removes elements <em>that do not match</em> the regular expressions defined in
     * {@code labels}.
     *
     * @param labels The list of {@code Shape} objects matching the regular expressions list.
     * @return A list of {@code Shape} objects.
     */
    public ShapeGroup getShapes(String... labels) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.shapes.size(); i++) {
            for (int j = 0; j < labels.length; j++) {

                Pattern pattern = Pattern.compile(labels[j]);
                Matcher matcher = pattern.matcher(this.shapes.get(i).getLabel());

                boolean isMatch = matcher.matches();

//                if (this.shapes.get(i).getLabel().equals(labels[j])) {
                if (isMatch) {
                    shapeGroup.add(this.shapes.get(i));
                }
            }
        }

        return shapeGroup;
    }

    public Shape removeShape(int index) {
        return shapes.remove(index);
    }

    public abstract void update();

    public abstract void draw(Display display);

//    public abstract boolean contains(Point point);

//    public abstract boolean contains(Point point, double padding);

    public boolean contains(Point point) {
        if (isVisible()) {
            for (int i = 0; i < shapes.size(); i++) {
                if (shapes.get(i).contains(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setOnActionListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void processAction(Action action) {
        if (eventListener != null) {
            eventListener.onAction(action);
        }
    }

    // TODO: Delete?
    public void setTransparency(final double transparency) {
        this.targetTransparency = transparency;

        for (int i = 0; i < shapes.size(); i++) {

            Shape shape = shapes.get(i);

            // Color
            int intColor = android.graphics.Color.parseColor(shapes.get(i).getColor());
            intColor = Color.setTransparency(intColor, this.targetTransparency);
            shape.setColor(Color.getHexColorString(intColor));

            // Outline Color
            int outlineColorIndex = android.graphics.Color.parseColor(shapes.get(i).getOutlineColor());
            outlineColorIndex = Color.setTransparency(outlineColorIndex, this.targetTransparency);
            shape.setOutlineColor(Color.getHexColorString(outlineColorIndex));
        }

        this.transparency = this.targetTransparency;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (Shape shape : shapes) {
            positions.addAll(shape.getVertices());
        }
        return positions;
    }

    // Delete? The above getVertices() should be enough now that Point is refactored! Maybe add getRelativeVertices() if needed.
    public List<Point> getAbsoluteVertices() {
        List<Point> positions = new LinkedList<>();
        for (Shape shape : shapes) {
            List<Point> vertices = shape.getVertices();
            for (Point shapeVertex : vertices) {

                // Rotate shape about its center point
                Point absoluteVertex = Geometry.calculateRotatedPoint(shape.getPosition(), shape.getRotation(), shapeVertex);

                // Rotate shape vertices about the shape's reference point
                Point referencePoint = position;
                absoluteVertex = Geometry.calculateRotatedPoint(referencePoint, getRotation(), absoluteVertex);
                positions.add(absoluteVertex);
            }
        }
        return positions;
    }

    public Rectangle getBoundingBox() {

        List<Point> boundingBoxVertices = new LinkedList<>();

        for (int i = 0; i < shapes.size(); i++) {

            Shape shape = shapes.get(i);
            List<Point> shapeVertices = shape.getVertices();

            for (int j = 0; j < shapeVertices.size(); j++) {
                boundingBoxVertices.add(shapeVertices.get(j));
            }
        }

        return Geometry.calculateBoundingBox(boundingBoxVertices);
    }

}
