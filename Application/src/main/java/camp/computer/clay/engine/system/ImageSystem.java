package camp.computer.clay.engine.system;

import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;

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
//            if (entity.hasComponent(Extension.class)) {
//                updateExtensionGeometry(entity);
//            }
            updateImage(entity);

            // TODO: updateShape(entity) // FOR UPDATING LAYOUT CONSTRAINTS OF SHAPES
            // </HACK>
        }

//        Group<Entity> shapeEntities = world.Manager.getEntities().filterActive(true).filterWithComponents(Model.class, RelativeLayoutConstraint.class, Transform.class);
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

//        if (entity.hasComponent(Model.class)) {
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
}
