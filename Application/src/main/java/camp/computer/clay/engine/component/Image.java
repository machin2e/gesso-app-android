package camp.computer.clay.engine.component;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Extension;
import camp.computer.clay.engine.entity.Host;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

public class Image extends Component {

    protected Entity entity = null;

    protected List<Shape> shapes = new LinkedList<>();

    protected Visibility visibility = Visibility.VISIBLE;

    protected double targetTransparency = 1.0;

    protected double transparency = targetTransparency;

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    public int layerIndex = DEFAULT_LAYER_INDEX;

    public int getLayerIndex() {
        return this.layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
        Space.getSpace().updateLayers();
    }

    /**
     * Sorts {@code Shapes}s in the {@code Image} by layer.
     */
    public void updateLayers() {

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
        for (int i = 0; i < shapes.size(); i++) {
            shapes.get(i).invalidate();
        }
    }

    public Image() {
        super();
    }

    public Image(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
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

    public <T extends Shape> void addShape(T shape) {
        shape.setImagePosition(shape.getPosition());
        shapes.add(shape);

        updateLayers(); // Update layer ordering
        shape.invalidate(); // Invalidate Shape
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

    public Shape getShape(Transform point) {
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
        shapeGroup.addAll(this.shapes);
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
        updateGeometry();
    }

    public void updateGeometry() {

        // Update Shapes
        for (int i = 0; i < this.shapes.size(); i++) {
            Shape shape = this.shapes.get(i);

            // Update the Shape
            shape.update(getEntity().getComponent(Transform.class));
        }
    }

//    public void draw(Display display) {
//    }

    // <COLLISION_COMPONENT>
    /**
     * Returns {@code true} if any of the {@code Shape}s in the {@code Image} contain the
     * {@code point}.
     *
     * @param point
     * @return
     */
    public boolean contains(Transform point) {
        if (isVisible()) {
            for (int i = 0; i < shapes.size(); i++) {
                if (shapes.get(i).contains(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Rectangle getBoundingBox() {
        List<Transform> shapeBoundaries = new LinkedList<>();
        for (int i = 0; i < shapes.size(); i++) {
            shapeBoundaries.addAll(shapes.get(i).getBoundary());
        }
        return Geometry.getBoundingBox(shapeBoundaries);
    }
    // </COLLISION_COMPONENT>

    // <STYLE_COMPONENT?>
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
    // </STYLE_COMPONENT?>
}
