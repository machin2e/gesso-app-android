package camp.computer.clay.engine.system;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.util.ImageBuilder.ImageBuilder;
import camp.computer.clay.util.ImageBuilder.Shape;

public class ImageSystem extends System {

    public ImageSystem(World world) {
        super(world);
    }

    @Override
    public void update() {
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
}
