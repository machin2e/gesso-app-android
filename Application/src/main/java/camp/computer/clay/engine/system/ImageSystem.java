package camp.computer.clay.engine.system;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.util.ImageBuilder.ImageBuilder;
import camp.computer.clay.util.ImageBuilder.Shape;
import camp.computer.clay.util.Color;

public class ImageSystem extends System {

    public ImageSystem(World world) {
        super(world);
    }

    @Override
    public void update() {
    }

    /**
     * Sorts {@code Shapes}s in the {@code Image} by layer.
     */
    public void updateLayers(Image image) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        for (int i = 0; i < shapes.size() - 1; i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                // Check for out-of-order pairs, and swap them
                if (shapes.get(i).getLayerIndex() > shapes.get(j).getLayerIndex()) {
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
     * geometry, such as its boundary, to be updated during the subsequent call to {@code updateImage()}.
     * <p>
     * Note that a {@code Shape}'s geometry cache will only ever be updated when it is first
     * invalidated by calling {@code invalidate()}. Therefore, to cause the {@code Shape}'s
     * geometry cache to be updated, call {@code invalidate()}. The geometry cache will be updated
     * in the first call to {@code updateImage()} following the call to {@code invalidate()}.
     */
    public void invalidate(Image image) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        for (int i = 0; i < shapes.size(); i++) {
            shapes.get(i).invalidate();
        }
    }

    // TODO: Remove! Image building should happen in ImageBuilder.
    public <T extends Shape> void addShape(Image image, T shape) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        shape.setImagePosition(shape.getPosition());
        shapes.add(shape);

        // Update layer ordering
        // <HACK>
        // TODO: World shouldn't call systems. System should operate on the world and interact with other systems/entities in it.
        world.imageSystem.invalidate(image);
        // </HACK>

        shape.invalidate(); // Invalidate Shape
    }

    // TODO: Remove! Image interaction should happen in ImageBuilder.
    public Shape getShape(Image image, String label) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.getLabel().equals(label)) {
                return shape;
            }
        }
        return null;
    }

    // TODO: Remove! Image interaction should happen in ImageBuilder.
    // TODO: <REMOVE?>
    public Group<Shape> getShapes(Image image, String... labels) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        Group<Shape> matchingShapes = new Group<>();
        for (int i = 0; i < shapes.size(); i++) {
            for (int j = 0; j < labels.length; j++) {
                Pattern pattern = Pattern.compile(labels[j]);
                Matcher matcher = pattern.matcher(shapes.get(i).getLabel());
                if (matcher.matches()) {
                    matchingShapes.add(shapes.get(i));
                }
            }
        }

        return matchingShapes;
    }
    // TODO: </REMOVE?>

    // TODO: Remove! Image interaction should happen in ImageBuilder.
    public Group<Shape> getShapes(Image image) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        // TODO: Don't create a new Group. Will that work?
        Group<Shape> shapeGroup = new Group<>();
        shapeGroup.addAll(shapes);
        return shapeGroup;
    }

    // TODO: Remove! Image interaction should happen in ImageBuilder.
    public Shape removeShape(Image image, int index) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        return shapes.remove(index);
    }

    // <STYLE_COMPONENT?>
    // TODO: Delete?
    public void setTransparency(Image image, final double transparency) {

        if (image.getImage() == null) {
            image.setImage(new ImageBuilder());
        }
        List<Shape> shapes = image.getImage().getShapes();

        image.targetTransparency = transparency;

        for (int i = 0; i < shapes.size(); i++) {

            Shape shape = shapes.get(i);

            // Color
            int intColor = android.graphics.Color.parseColor(shapes.get(i).getColor());
            intColor = Color.setTransparency(intColor, image.targetTransparency);
            shape.setColor(Color.getHexColorString(intColor));

            // Outline Color
            int outlineColorIndex = android.graphics.Color.parseColor(shapes.get(i).getOutlineColor());
            outlineColorIndex = Color.setTransparency(outlineColorIndex, image.targetTransparency);
            shape.setOutlineColor(Color.getHexColorString(outlineColorIndex));
        }

        image.transparency = image.targetTransparency;
    }
    // </STYLE_COMPONENT?>
}
