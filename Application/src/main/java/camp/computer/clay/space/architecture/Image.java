package camp.computer.clay.space.architecture;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Entity;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.space.util.Color;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;
import camp.computer.clay.space.util.Visibility;

public abstract class Image<T extends Entity> {

    protected List<Shape> shapes = new LinkedList<>();

    protected Point position = new Point(0, 0); // Image position

    protected double scale = 1.0; // Image scale factor

    protected Visibility visibility = Visibility.VISIBLE;

    protected double targetTransparency = 1.0;

    protected double transparency = targetTransparency;

    protected T entity = null;

    protected Space space = null;

    // TODO: Make this an interface? Move interface out of class.
    protected ActionListener actionListener;

    public Image(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return this.entity;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
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

    public Shape getShape(Entity entity) {
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.getEntity() == entity) {
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

    //    public <T extends Entity> ShapeGroup getShapes(Class<?>... entityTypes) {
    public <T extends Entity> ShapeGroup getShapes(Class<? extends Entity>... entityTypes) {
        return getShapes().filterType(entityTypes);
    }

    public <T extends Entity> ShapeGroup getShapes(List<T> entities) {
        return getShapes().filterEntity(entities);
    }

    /**
     * Removes elements <em>that do not match</em> the regular expressions defined in
     * {@code labels}.
     *
     * @param labelPatterns The list of {@code Shape} objects matching the regular expressions list.
     * @return A list of {@code Shape} objects.
     */
    public ShapeGroup getShapes(String... labelPatterns) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.shapes.size(); i++) {
            for (int j = 0; j < labelPatterns.length; j++) {

                Pattern pattern = Pattern.compile(labelPatterns[j]);
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

    public void update() {
        for (int i = 0; i < this.shapes.size(); i++) {
            this.shapes.get(i).update();
        }
    }

    public abstract void draw(Display display);

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

    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void processAction(Action action) {
        if (actionListener != null) {
            actionListener.onAction(action);
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
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            positions.addAll(shape.getVertices());
        }
        return positions;
    }

    // Delete? The above getVertices() should be enough now that Point is refactored! Maybe addImage getRelativeVertices() if needed.
    public List<Point> getAbsoluteVertices() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            List<Point> vertices = shape.getVertices();
            for (int j = 0; j < vertices.size(); j++) {
                Point shapeVertex = vertices.get(j);

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
