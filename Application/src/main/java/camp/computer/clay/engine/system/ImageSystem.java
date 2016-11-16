package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.ShapeComponent;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.ImageBuilder.Geometry;
import camp.computer.clay.util.ImageBuilder.Point;
import camp.computer.clay.util.ImageBuilder.Rectangle;
import camp.computer.clay.util.ImageBuilder.Shape;

public class ImageSystem extends System {

    public ImageSystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        Group<Entity> entitiesWithTransform = world.Manager.getEntities().filterActive(true).filterWithComponents(Transform.class, Image.class);

        for (int i = 0; i < entitiesWithTransform.size(); i++) {
            Entity entity = entitiesWithTransform.get(i);

            // Update Shapes
            // <HACK>
            if (entity.hasComponent(Extension.class)) {
                updateExtensionGeometry(entity);
            } else if (entity.hasComponent(Host.class)) {
            } else if (entity.hasComponent(Port.class)) {
            } else if (entity.hasComponent(Path.class)) {
            }
            updateImage(entity);
            // </HACK>
        }
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    public void updateExtensionGeometry(Entity extension) {

        // TODO: Clean up/delete images/shapes for any removed ports...

        updateExtensionPortPositions(extension);
        updateExtensionHeaderGeometry(extension);
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code ExtensionEntity}'s {@code PortEntity}s.
     */
    private void updateExtensionPortPositions(Entity extension) {

        // TODO: Replace above with code that updates the position of Port images, creates new Ports, etc.

        // Update Port positions based on the index of Port
        Group<Entity> ports = Portable.getPorts(extension);
        for (int i = 0; i < ports.size(); i++) {
            Entity port = ports.get(i);

            double portSpacing = 115;
//            port.getComponent(Transform.class).x = (i * portSpacing) - (((Portable.getPorts(extension).size() - 1) * portSpacing) / 2.0);
//            port.getComponent(Transform.class).y = 175; // i.e., Distance from board
            port.getComponent(RelativeLayoutConstraint.class).relativeTransform.x = (i * portSpacing) - (((Portable.getPorts(extension).size() - 1) * portSpacing) / 2.0);
            port.getComponent(RelativeLayoutConstraint.class).relativeTransform.y = 175; // i.e., Distance from board

            // <HACK>
            // TODO: World shouldn't call systems. System should operate on the world and interact with other systems/entities in it.
//            world.imageSystem.invalidate(port.getComponent(Image.class));
            // </HACK>
        }
    }

    private void updateExtensionHeaderGeometry(Entity extension) {

        // <FACTOR_OUT>
        // References:
        // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png

        final int contactCount = Portable.getPorts(extension).size();
        final double errorToleranceA = 0.0; // ±0.60 mm according to [1]
        final double errorToleranceB = 0.0; // ±0.15 mm according to [1]

        double A = 2.54 * contactCount + errorToleranceA;
        double B = 2.54 * (contactCount - 1) + errorToleranceB;

        final double errorToleranceContactSeparation = 0.0; // ±0.1 mm according to [1]
        double contactOffset = (A - B) / 2.0; // Measure in millimeters (mm)
        double contactSeparation = 2.54; // Measure in millimeters (mm)
        // </FACTOR_OUT>

        Image portableImage = extension.getComponent(Image.class);

        // Update Headers Geometry to match the corresponding ExtensionEntity Configuration
//        Rectangle header = (Rectangle) portableImage.getImage().getShape("Header");
        Entity shape = Image.getShape(extension, "Header");
        double headerWidth = World.PIXEL_PER_MILLIMETER * A;
        Rectangle headerShape = (Rectangle) shape.getComponent(ShapeComponent.class).shape;
        headerShape.setWidth(headerWidth);

        // Update Contact Positions for Header
        for (int i = 0; i < Portable.getPorts(extension).size(); i++) {
            double x = World.PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
            if (i < extension.getComponent(Portable.class).headerContactPositions.size()) {
                //extension.getComponent(Portable.class).headerContactPositions.get(i).getImagePosition().x = x;
                Entity headerContactShape = Image.getShape(extension, extension.getComponent(Portable.class).headerContactPositions.get(i));
                headerContactShape.getComponent(Transform.class).x = x;
            } else {
                Point point = new Point(new Transform(x, 107));
                extension.getComponent(Portable.class).headerContactPositions.add(point);
//                portableImage.getImage().addShape(point);

                // Add new Port shape and set Position
                // TODO: Find better place!
                Entity headerContactShape = world.createEntity(ShapeComponent.class);
                headerContactShape.getComponent(ShapeComponent.class).shape = point;
                headerContactShape.getComponent(Transform.class).set(x, 107);
                Image.addShape(extension, point);

            }
        }
    }
    // </EXTENSION>

    // <IMAGE>
    // Previously: Image.update()
    // Required Components: Image, Transform
    public void updateImage(Entity entity) {

        // <HACK>
//        if (entity.hasComponent(Extension.class)) {
//            return;
//        }
        // </HACK>

//        List<Shape> shapes = entity.getComponent(Image.class).getImage().getShapes();
        Group<Entity> shapes = Image.getShapes(entity);

        // Update Shapes
        for (int i = 0; i < shapes.size(); i++) {
            Entity shape = shapes.get(i);

            Transform absoluteReferenceTransform = null;
            if (entity.hasComponent(RelativeLayoutConstraint.class)) {
                // <HACK>
                RelativeLayoutConstraint layoutConstraint = entity.getComponent(RelativeLayoutConstraint.class);
//                Entity referenceEntity = layoutConstraint.getReferenceEntity();
                Transform referenceTransform = layoutConstraint.getReferenceEntity().getComponent(Transform.class);
                //Transform relativePosition = entity.getComponent(Transform.class);
                Transform relativePosition = layoutConstraint.relativeTransform;
                absoluteReferenceTransform = new Transform();

                absoluteReferenceTransform.x = referenceTransform.x + Geometry.distance(0, 0, relativePosition.x, relativePosition.y) * Math.cos(Math.toRadians(referenceTransform.rotation + Geometry.getAngle(0, 0, relativePosition.x, relativePosition.y)));
                absoluteReferenceTransform.y = referenceTransform.y + Geometry.distance(0, 0, relativePosition.x, relativePosition.y) * Math.sin(Math.toRadians(referenceTransform.rotation + Geometry.getAngle(0, 0, relativePosition.x, relativePosition.y)));
                // </HACK>
            } else {

                // HACK!
                if (entity.hasComponent(Path.class)) {
                    if (Path.getState(entity) != Component.State.EDITING) {
                        absoluteReferenceTransform = entity.getComponent(Transform.class);
                    }
                } else {
                    absoluteReferenceTransform = entity.getComponent(Transform.class);
                }
            }

            //if (shape.hasComponent(Port.class) && shape.getComponent(Visibility.class).visible == Visible.VISIBLE) {
            if (entity.hasComponent(Port.class)) {
                Log.v("RenderSystem", "drawing port");
            }

//            shape.update(transformedPoint);
            if (absoluteReferenceTransform != null) {
//            if (shape.hasComponent(RelativeLayoutConstraint.class)) {
                updateShapeRelativeTransform(shape, absoluteReferenceTransform);
//                updateShapeBoundary(shape);
//                shape.isValid = true;
            }
        }

//        updateImageBoundary(entity);
    }

    /**
     * Computes and updates the {@code Shape}'s absolute positioning, rotation, and scaling in
     * preparation for drawing and collision detection.
     * <p>
     * Updates the x and y coordinates of {@code Shape} relative to this {@code Image}. Translate
     * the center position of the {@code Shape}. Effectively, this updates the position of the
     * {@code Shape}.
     *
     * @param referencePoint Position of the containing {@code Image} relative to which the
     *                       {@code Shape} will be drawn.
     */
    public void updateShapeRelativeTransform(Entity shape, Transform referencePoint) {

//        if (!shape.isValid) {
//            updateShapePositionAndRotation(shape, referencePoint); // Update the position
//            updateShapeBoundary(shape); // Update the bounds (using the results from the updateImage position and rotation)
//            shape.isValid = true;
//        }

//        if (!shape.isValid) {

//        // Position
//        shape.getPosition().x = referencePoint.x + Geometry.distance(0, 0, shape.getImagePosition().x, shape.getImagePosition().y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, shape.getImagePosition().x, shape.getImagePosition().y)));
//        shape.getPosition().y = referencePoint.y + Geometry.distance(0, 0, shape.getImagePosition().x, shape.getImagePosition().y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, shape.getImagePosition().x, shape.getImagePosition().y)));
//
//        // Rotation
//        shape.getPosition().rotation = referencePoint.rotation + shape.getImagePosition().rotation;

        if (shape.hasComponent(Port.class)) {
            Log.v("RenderSystem", "drawing port");
        }

        if (shape.getComponent(RelativeLayoutConstraint.class) == null) {
            Log.v("RenderSystem", "drawing port");
        }

        if (shape.getComponent(RelativeLayoutConstraint.class).relativeTransform == null) {
            Log.v("RenderSystem", "drawing port");
        }

        // Position
        shape.getComponent(Transform.class).x = referencePoint.x + Geometry.distance(0, 0, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.x, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.x, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.y)));
        shape.getComponent(Transform.class).y = referencePoint.y + Geometry.distance(0, 0, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.x, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.x, shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.y)));

        // Rotation
        shape.getComponent(Transform.class).rotation = referencePoint.rotation + shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.rotation;

//            updateShapeBoundary(shape); // Update the bounds (using the results from the updateImage position and rotation)
        //shape.isValid = true;
//        }
    }
    // </IMAGE>


    // TODO: Remove! Image interaction should happen in ImageBuilder.
    // TODO: <REMOVE?>
    public Group<Shape> getShapes(Image image, String... labels) {

        Entity entity = image.getEntity();

//        if (image.getImage() == null) {
//            image.setImage(new ImageBuilder());
//        }
//        List<Shape> shapes = image.getImage().getShapes();
        Group<Entity> shapes = Image.getShapes(entity);

        Group<Shape> matchingShapes = new Group<>();

        for (int i = 0; i < shapes.size(); i++) {
            for (int j = 0; j < labels.length; j++) {
                Pattern pattern = Pattern.compile(labels[j]);
                Matcher matcher = pattern.matcher(Label.getLabel(shapes.get(i)));
                if (matcher.matches()) {
                    matchingShapes.add(shapes.get(i).getComponent(ShapeComponent.class).shape);
                }
            }
        }

        return matchingShapes;
    }
    // TODO: </REMOVE?>

//    // TODO: Remove! Image interaction should happen in ImageBuilder.
//    public Group<Shape> getShapes(Image image) {
//
//        if (image.getImage() == null) {
//            image.setImage(new ImageBuilder());
//        }
//        List<Shape> shapes = image.getImage().getShapes();
//
//        // TODO: Don't create a new Group. Will that work?
//        Group<Shape> shapeGroup = new Group<>();
//        shapeGroup.addAll(shapes);
//        return shapeGroup;
//    }
//
//    // TODO: Remove! Image interaction should happen in ImageBuilder.
//    public Shape removeShape(Image image, int index) {
//
//        if (image.getImage() == null) {
//            image.setImage(new ImageBuilder());
//        }
//        List<Shape> shapes = image.getImage().getShapes();
//
//        return shapes.remove(index);
//    }
}
