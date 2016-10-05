package camp.computer.clay.util.image;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.model.Group;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.util.ShapeGroup;

public abstract class Image<T extends Entity> {

    /**
     * The parent {@code Space} containing this {@code Image}.
     */
    protected Space parentSpace = null;

    protected T entity = null;

    protected List<Shape> shapes = new LinkedList<>();

    protected Point position = new Point(0, 0);

    protected double scale = 1.0;

    protected Visibility visibility = new Visibility(Visibility.Value.VISIBLE);

    protected double targetTransparency = 1.0;

    protected double transparency = targetTransparency;

    protected ActionListener actionListener;

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    protected int layerIndex = DEFAULT_LAYER_INDEX;

    public int getLayerIndex() {
        return this.layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;

        parentSpace.sortImagesByLayer();
    }

    // TODO: Rename to sortLayers
    public void sortShapesByLayer() {

        for (int i = 0; i < shapes.size() - 1; i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                // Check for out-of-order pairs, and swap them
                if (shapes.get(i).layerIndex > shapes.get(j).layerIndex) {
                    Shape shape = shapes.get(i);
                    shapes.set(i, shapes.get(j));
                    shapes.set(j, shape);
                }
            }
        }

        /*
        // TODO: Sort using this after making Group implement List
        Collections.sort(Database.arrayList, new Comparator<MyObject>() {
            @Override
            public int compare(MyObject o1, MyObject o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        */
    }
    // </LAYER>

    public Image(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return this.entity;
    }

    public void setSpace(Space space) {
        this.parentSpace = space;
    }

    public Space getSpace() {
        return this.parentSpace;
    }

    public Point getPosition() {
        return this.position;
    }

    public double getRotation() {
        return this.position.getAbsoluteRotation();
    }

    public double getScale() {
        return this.scale;
    }

    public void setPosition(Point position) {
        this.position.setAbsolute(position.getAbsoluteX(), position.getAbsoluteY());
    }

    public void setRotation(double angle) {
        this.position.rotation = angle;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean isVisible() {
        return visibility.getValue() == Visibility.Value.VISIBLE;
    }

    public void setVisibility(Visibility.Value visibility) {
        this.visibility.setValue(visibility);
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public <T extends Shape> void addShape(T shape) {
        shape.setImage(this);
        shape.getPosition().setReferencePoint(getPosition());
        this.shapes.add(shape);
    }

    public <T extends Shape> void addShape(T shape, String label) {
        shape.setImage(this);
        shape.setLabel(label);
        shape.getPosition().setReferencePoint(getPosition());
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

    public Shape getShape(Point point) {

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

    public <T extends Entity> ShapeGroup getShapes(Class<? extends Entity>... entityTypes) {
        return getShapes().filterType(entityTypes);
    }

    public <T extends Entity> ShapeGroup getShapes(Group<T> entities) {
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

                if (matcher.matches()) {
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

        position.update();

//        Log.v("OnUpdate", "Image.update " + shapes.size());
        for (int i = 0; i < this.shapes.size(); i++) {
//            Log.v("OnUpdate", "Image.update " + shapes.get(i));
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
