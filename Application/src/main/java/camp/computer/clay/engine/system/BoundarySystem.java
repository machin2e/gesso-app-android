package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.ImageBuilder.Geometry;
import camp.computer.clay.util.ImageBuilder.Point;
import camp.computer.clay.util.ImageBuilder.Rectangle;
import camp.computer.clay.util.ImageBuilder.Shape;

public class BoundarySystem extends System {

    public BoundarySystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        // Update Transform
        Group<Entity> entitiesWithTransform = world.Manager.getEntities().filterActive(true).filterWithComponents(Transform.class, Image.class);
//        for (int i = 0; i < entitiesWithTransform.size(); i++) {
//            Entity entity = entitiesWithTransform.get(i);
//            updateTransform(entity);
//        }

        // Update Shapes
        for (int i = 0; i < entitiesWithTransform.size(); i++) {
            Entity entity = entitiesWithTransform.get(i);

            // Update Transform
            updateTransform(entity);

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

            // Update Style
            if (entity.hasComponent(Extension.class)) {
                updateExtensionStyle(entity);
            } else if (entity.hasComponent(Host.class)) {
                updateHostStyle(entity);
            } else if (entity.hasComponent(Port.class)) {
            } else if (entity.hasComponent(Path.class)) {
            }
        }

        // Update Boundaries

        // Update Style
//        for (int i = 0; i < entitiesWithTransform.size(); i++) {
//            Entity entity = entitiesWithTransform.get(i);
//            // <HACK>
//            if (entity.hasComponent(Extension.class)) {
//                updateExtensionStyle(entity);
//            } else if (entity.hasComponent(Host.class)) {
//                updateHostStyle(entity);
//            } else if (entity.hasComponent(Port.class)) {
//            } else if (entity.hasComponent(Path.class)) {
//            }
        // </HACK>
//        }

        // Update Renderables?
    }

    private void updateTransform(Entity entity) {

    }


    // <IMAGE>
    // Previously: Image.update()
    // Required Components: Image, Transform
    public void updateImage(Entity entity) {

        List<Shape> shapes = entity.getComponent(Image.class).getImage().getShapes();

        // Update Shapes
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);

            Transform absoluteReferenceTransform = null;
            if (entity.hasComponent(RelativeLayoutConstraint.class)) {
                // <HACK>
                RelativeLayoutConstraint layoutConstraint = entity.getComponent(RelativeLayoutConstraint.class);
//                Entity referenceEntity = layoutConstraint.getReferenceEntity();
                Transform referenceTransform = layoutConstraint.getReferenceEntity().getComponent(Transform.class);
                Transform relativePosition = entity.getComponent(Transform.class);
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

//            shape.update(transformedPoint);
            if (absoluteReferenceTransform != null) {
                updateShapeTransform(shape, absoluteReferenceTransform);
                updateShapeBoundary(shape);
                shape.isValid = true;
            }
        }

//        updateImageBoundary(entity);
    }
    // </IMAGE>


    // <HOST>
    public void updateHostStyle(Entity host) {

        // Get LED shapes
        Group<Shape> lightShapeGroup = world.imageSystem.getShapes(host.getComponent(Image.class), "^LED (1[0-2]|[1-9])$");

        Group<Entity> ports = Portable.getPorts(host);

        // Update Port LED color
        for (int i = 0; i < ports.size(); i++) {

            // Update color of LED based on corresponding Port's type
            Entity port = ports.get(i);
            String portColor = camp.computer.clay.util.Color.getColor(Port.getType(port));
            lightShapeGroup.get(i).setColor(portColor);
        }
    }
    // </HOST>

    // <EXTENSION>

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
            port.getComponent(Transform.class).x = (i * portSpacing) - (((Portable.getPorts(extension).size() - 1) * portSpacing) / 2.0);
            port.getComponent(Transform.class).y = 175; // i.e., Distance from board

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
        Rectangle header = (Rectangle) portableImage.getImage().getShape("Header");
        double headerWidth = World.PIXEL_PER_MILLIMETER * A;
        header.setWidth(headerWidth);

        // Update Contact Positions for Header
        for (int i = 0; i < Portable.getPorts(extension).size(); i++) {
            double x = World.PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
            if (i < extension.getComponent(Portable.class).headerContactPositions.size()) {
                extension.getComponent(Portable.class).headerContactPositions.get(i).getImagePosition().x = x;
            } else {
                Point point = new Point(new Transform(x, 107));
                extension.getComponent(Portable.class).headerContactPositions.add(point);
                portableImage.getImage().addShape(point);
            }
        }
    }

    private void updateExtensionStyle(Entity extension) {

        // Update Port Colors
        Group<Entity> ports = Portable.getPorts(extension);
        for (int i = 0; i < ports.size(); i++) {
            Entity portEntity = ports.get(i);

//            Shape portShape = extension.getComponent(Image.class).getShape(portEntity);
//
//            // Update color of Port shape based on type
//            if (portShape != null) {
//                portShape.setColor(Color.getColor(portEntity.getComponent(Port.class).getType()));
//            }
        }
    }

    private void updateExtensionPathRoutes(Entity extension) {
        // TODO: Create routes between extension and host.
    }
    // </EXTENSION>


    // <SHAPE>
    // TODO: Move into Image API specific to my shape-based Image format.

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
    public void updateShapeTransform(Shape shape, Transform referencePoint) {

//        if (!shape.isValid) {
//            updateShapePositionAndRotation(shape, referencePoint); // Update the position
//            updateShapeBoundary(shape); // Update the bounds (using the results from the updateImage position and rotation)
//            shape.isValid = true;
//        }

//        if (!shape.isValid) {

        // Position
        shape.getPosition().x = referencePoint.x + Geometry.distance(0, 0, shape.getImagePosition().x, shape.getImagePosition().y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, shape.getImagePosition().x, shape.getImagePosition().y)));
        shape.getPosition().y = referencePoint.y + Geometry.distance(0, 0, shape.getImagePosition().x, shape.getImagePosition().y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, shape.getImagePosition().x, shape.getImagePosition().y)));

        // Rotation
        shape.getPosition().rotation = referencePoint.rotation + shape.getImagePosition().rotation;

//            updateShapeBoundary(shape); // Update the bounds (using the results from the updateImage position and rotation)
        //shape.isValid = true;
//        }
    }

    /**
     * Updates the bounds of the {@code Shape} for use in touch interaction, layout, and collision
     * detection. Hey there, mango bongo.
     */
//    public static void updateShapeBoundary(Shape shape) {
//
//        if (shape.isBoundary) {
//            List<Transform> vertices = shape.getVertices();
//            List<Transform> boundary = getBoundary(shape);
//
//            // Translate and rotate the boundary about the updated position
//            for (int i = 0; i < vertices.size(); i++) {
//                boundary.get(i).set(vertices.get(i));
//                Geometry.rotatePoint(boundary.get(i), shape.getPosition().rotation); // Rotate Shape boundary about Image position
//                Geometry.translatePoint(boundary.get(i), shape.getPosition().x, shape.getPosition().y); // Translate Shape
//            }
//            // shape.isValid = true;
//        }
//    }

    // TODO: Delete?
//    public static List<Transform> getBoundary(Shape shape) {
//        if (shape.isBoundary) {
//            if (!Boundary.innerBoundaries.containsKey(shape)) {
//                ArrayList<Transform> boundary = new ArrayList<>();
//                boundary.addAll(shape.getVertices());
//                Boundary.innerBoundaries.put(shape, boundary);
//                BoundarySystem.updateShapeBoundary(shape);
//            }
//            return Boundary.innerBoundaries.get(shape);
//        } else {
//            return null;
//        }
//    }
    private static List<Transform> updateShapeBoundary(Shape shape) {

        if (shape.isBoundary) {
            List<Transform> vertices = shape.getVertices();
            List<Transform> boundary = new ArrayList<>(vertices);

            // Translate and rotate the boundary about the updated position
            for (int i = 0; i < vertices.size(); i++) {
                boundary.get(i).set(vertices.get(i));
                Geometry.rotatePoint(boundary.get(i), shape.getPosition().rotation); // Rotate Shape boundary about Image position
                Geometry.translatePoint(boundary.get(i), shape.getPosition().x, shape.getPosition().y); // Translate Shape
            }
            // shape.isValid = true;

            return boundary;
        }
        return null;
    }

    public static List<Transform> getBoundary(Shape shape) {
        return BoundarySystem.updateShapeBoundary(shape);
    }

//    // TODO: Delete! Get boundary in BoundarySystem.
//    public static List<Transform> getBoundary(Shape shape) {
//        ArrayList<Transform> boundary = new ArrayList<>();
//        boundary.addAll(shape.getVertices());
//        return boundary;
//    }
    // </SHAPE>

//    public void updateImageBoundary(Entity entity) {
//        List<Transform> boundary = BoundarySystem.getBoundary(Boundary.getBoundingBox(entity));
//        if (boundary != null) {
//            entity.getComponent(Boundary.class).setBoundary(boundary);
//        }
//    }
}
