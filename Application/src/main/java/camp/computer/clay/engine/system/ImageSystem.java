package camp.computer.clay.engine.system;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Geometry;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.ImageBuilder.Point;
import camp.computer.clay.lib.ImageBuilder.Rectangle;

public class ImageSystem extends System {

    public ImageSystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        Group<Entity> entities = world.Manager.getEntities().filterActive(true).filterWithComponents(Image.class, Transform.class);

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            // Update Shapes
            // <HACK>
            if (entity.hasComponent(Extension.class)) {
                updateExtensionGeometry(entity);
            }
            updateImage(entity);

            // TODO: updateShape(entity) // FOR UPDATING LAYOUT CONSTRAINTS OF SHAPES
            // </HACK>
        }

//        Group<Entity> shapeEntities = world.Manager.getEntities().filterActive(true).filterWithComponents(Geometry.class, RelativeLayoutConstraint.class, Transform.class);
//
//        for (int i = 0; i < shapeEntities.size(); i++) {
//            Entity entity = shapeEntities.get(i);
//            updateImage(entity);
//        }
    }

    // Previously: Image.update()
    // Required Components: Image, Transform
    private void updateImage(Entity entity) {

        // Start by transforming base images. They will never have more than one level of constraints (?).
        Transform absoluteReferenceTransform = null;
        if (entity.hasComponent(RelativeLayoutConstraint.class)) {
            // <HACK>
            RelativeLayoutConstraint layoutConstraint = entity.getComponent(RelativeLayoutConstraint.class);
            Transform referenceTransform = layoutConstraint.getReferenceEntity().getComponent(Transform.class);
            Transform relativeTransform = layoutConstraint.relativeTransform;

            absoluteReferenceTransform = new Transform();
            absoluteReferenceTransform.x = referenceTransform.x + camp.computer.clay.util.Geometry.distance(0, 0, relativeTransform.x, relativeTransform.y) * Math.cos(Math.toRadians(referenceTransform.rotation + camp.computer.clay.util.Geometry.getAngle(0, 0, relativeTransform.x, relativeTransform.y)));
            absoluteReferenceTransform.y = referenceTransform.y + camp.computer.clay.util.Geometry.distance(0, 0, relativeTransform.x, relativeTransform.y) * Math.sin(Math.toRadians(referenceTransform.rotation + camp.computer.clay.util.Geometry.getAngle(0, 0, relativeTransform.x, relativeTransform.y)));
            // </HACK>
        } else {

//            // HACK!
//            // TODO: Remove this. Shouldn't need this in addition to the previous block in this condition... i.e., paths shouldn't be a special case! Generalize handling EDITING state (or make it not important)
//            if (entity.hasComponent(Path.class)) {
//                if (Component.getState(entity) != Component.State.EDITING) {
//                    absoluteReferenceTransform = entity.getComponent(Transform.class);
//                }
//            } else {
            absoluteReferenceTransform = entity.getComponent(Transform.class);
//            }
        }

//        if (entity.hasComponent(Geometry.class)) {
//            if (absoluteReferenceTransform != null) {
//                // TODO: if (shape.hasComponent(RelativeLayoutConstraint.class)) {
//                updateShapeRelativeTransform(entity, absoluteReferenceTransform);
//            }
//        } else {
        // Update Shapes
        if (entity.hasComponent(Path.class)) {
//            // <REFACTOR>
//            // TODO: Fix this... understand it. This works when REMOVED.
//            Group<Entity> shapes = Image.getShapes(entity);
//            for (int i = 0; i < shapes.size(); i++) {
//                // TODO: if (shape.hasComponent(RelativeLayoutConstraint.class)) {
//                updateShapeRelativeTransform(shapes.get(i), shapes.get(i));
//            }
//            // </REFACTOR>
        } else {
            Group<Entity> shapes = Image.getShapes(entity);
            for (int i = 0; i < shapes.size(); i++) {
                if (absoluteReferenceTransform != null) {
                    // TODO: if (shape.hasComponent(RelativeLayoutConstraint.class)) {
                    updateShapeRelativeTransform(shapes.get(i), absoluteReferenceTransform);
                }
            }
        }
    }

    /**
     * Computes and updates the {@code Shape}'s absolute positioning, rotation, and scaling in
     * preparation for drawing and collision detection.
     * <p>
     * Updates the x and y coordinates of {@code Shape} relative to this {@code Image}. Translate
     * the center position of the {@code Shape}. Effectively, this updates the position of the
     * {@code Shape}.
     *
     * @param referenceTransform Position of the containing {@code Image} relative to which the
     *                           {@code Shape} will be drawn.
     */
    private void updateShapeRelativeTransform(Entity shape, Transform referenceTransform) {

        RelativeLayoutConstraint layoutConstraint = shape.getComponent(RelativeLayoutConstraint.class);

        // Position
        double distanceToRelativeTransform = camp.computer.clay.util.Geometry.distance(0, 0, layoutConstraint.relativeTransform.x, layoutConstraint.relativeTransform.y);
        double angle = camp.computer.clay.util.Geometry.getAngle(0, 0, layoutConstraint.relativeTransform.x, layoutConstraint.relativeTransform.y);

        shape.getComponent(Transform.class).x = referenceTransform.x + distanceToRelativeTransform * Math.cos(Math.toRadians(referenceTransform.rotation + angle));
        shape.getComponent(Transform.class).y = referenceTransform.y + distanceToRelativeTransform * Math.sin(Math.toRadians(referenceTransform.rotation + angle));

        // Rotation
        shape.getComponent(Transform.class).rotation = referenceTransform.rotation + layoutConstraint.relativeTransform.rotation;

//        // <HACK>
//        if (shape.hasComponent())
//        // </HACK>
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    public void updateExtensionGeometry(Entity extension) {

        // TODO: Clean up/delete images/shapes for any removed ports...

        updateExtensionPortButtonPositions(extension);
        updateExtensionHeaderDimensions(extension);
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code ExtensionEntity}'s {@code PortEntity}s.
     */
    private void updateExtensionPortButtonPositions(Entity extension) {

        // TODO: Replace above with code that updates the position of Port images, creates new Ports, etc.

        // Update Port positions based on the index of Port
        Group<Entity> ports = Portable.getPorts(extension);
        double halfTotalPortsWidth = (((ports.size() - 1) * World.EXTENSION_PORT_SEPARATION_DISTANCE) / 2.0);
        for (int i = 0; i < ports.size(); i++) {
            ports.get(i).getComponent(RelativeLayoutConstraint.class).relativeTransform.x = (i * World.EXTENSION_PORT_SEPARATION_DISTANCE) - halfTotalPortsWidth;
            ports.get(i).getComponent(RelativeLayoutConstraint.class).relativeTransform.y = 175; // i.e., Distance from board
        }
    }

    // Header Dimensions
    // References:
    // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png
    final double errorToleranceA = 0.0; // ±0.60 mm according to [1]
    final double errorToleranceB = 0.0; // ±0.15 mm according to [1]
    double contactSeparation = 2.54; // Measure in millimeters (mm)

    private void updateExtensionHeaderDimensions(Entity extension) {

        // <FACTOR_OUT>
        final int contactCount = Portable.getPorts(extension).size();

        double A = 2.54 * contactCount + errorToleranceA;
        double B = 2.54 * (contactCount - 1) + errorToleranceB;

        // final double errorToleranceContactSeparation = 0.0; // ±0.1 mm according to [1]
        double contactOffset = (A - B) / 2.0; // Measure in millimeters (mm)
        // </FACTOR_OUT>

        // Update Headers Geometry to match the corresponding ExtensionEntity Configuration
        Entity shape = Image.getShape(extension, "Header");
        double headerWidth = World.PIXEL_PER_MILLIMETER * A;
        Rectangle headerShape = (Rectangle) shape.getComponent(Geometry.class).shape;
        headerShape.setWidth(headerWidth);

        // Update Contact Positions for Header
        for (int i = 0; i < Portable.getPorts(extension).size(); i++) {
            double x = World.PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
            if (i < extension.getComponent(Portable.class).headerContactGeometries.size()) {
                //extension.getComponent(Portable.class).headerContactGeometries.get(i).getImagePosition().x = x;
                Entity headerContactGeometry = extension.getComponent(Portable.class).headerContactGeometries.get(i);
//                headerContactGeometry.getComponent(Transform.class).x = x;
                headerContactGeometry.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(x, 107);
            } else {
                Point headerContactShape = new Point();
//                extension.getComponent(Portable.class).headerContactGeometries.add(headerContactShape);
//                portableImage.getImage().addShape(point);

                // Add new Port shape and set Position
                // TODO: Find better place!
                Entity headerContactGeometry = world.createEntity(Geometry.class);
                headerContactGeometry.getComponent(Geometry.class).shape = headerContactShape;
//                headerContactGeometry.getComponent(Transform.class).set(x, 107);

                // <REFACTOR_TO_REDUCE_REDUNDANCY>
                Image.addShape(extension, headerContactShape);
                extension.getComponent(Portable.class).headerContactGeometries.add(headerContactGeometry);
//                headerContactGeometry.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(x, 107);
                headerContactGeometry.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(x, 107);
                // </REFACTOR_TO_REDUCE_REDUNDANCY>

            }
        }
    }
    // </EXTENSION>
}
