package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.ShapeComponent;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.ImageBuilder.Geometry;
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

            // Update Shapes
            // <HACK>
//            updateImage(entity);
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
        Group<Entity> entitiesWithTransform2 = world.Manager.getEntities().filterActive(true).filterWithComponents(ShapeComponent.class, Boundary.class);
        for (int i = 0; i < entitiesWithTransform2.size(); i++) {
            updateBoundary(entitiesWithTransform2.get(i));
        }

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

    // <HOST>
    public void updateHostStyle(Entity host) {

        // Get LED shapes
        // TODO: Optimize! Cache!
        Group<Shape> lightShapeGroup = world.imageSystem.getShapes(host.getComponent(Image.class), "^LED (1[0-2]|[1-9])$");

        Group<Entity> ports = Portable.getPorts(host);

        // Update Port LED color
        for (int i = 0; i < ports.size(); i++) {

            // Update color of LED based on corresponding Port's type
            Entity port = ports.get(i);
            String portColor = camp.computer.clay.util.Color.getColor(Port.getType(port));
            lightShapeGroup.get(i).setColor(portColor);
        }
//        Log.e("TODO", "BoundarySystem.updateHostStyle()");
    }
    // </HOST>

    // <EXTENSION>

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
//    private static List<Transform> updateShapeBoundary(Shape shape) {
//
//
//    }
    private void updateBoundary(Entity entity) {

        Shape shape = entity.getComponent(ShapeComponent.class).shape;

        if (shape.isBoundary) { // HACK // TODO: Remove need for this!
            List<Transform> vertices = shape.getVertices();
            List<Transform> boundary = new ArrayList<>(vertices);

            // Translate and rotate the boundary about the updated position
            for (int i = 0; i < vertices.size(); i++) {
                boundary.get(i).set(vertices.get(i));
                Geometry.rotatePoint(boundary.get(i), shape.getPosition().rotation); // Rotate Shape boundary about Image position
                Geometry.translatePoint(boundary.get(i), shape.getPosition().x, shape.getPosition().y); // Translate Shape
            }

            entity.getComponent(Boundary.class).setBoundary(boundary);
        }
    }

    //public static List<Transform> getBoundary(Shape shape) {
    public static List<Transform> getBoundary(Entity entity) {

        Shape shape = entity.getComponent(ShapeComponent.class).shape;

        if (shape.isBoundary) { // HACK // TODO: Remove need for this crap
//            List<Transform> vertices = shape.getVertices();
//            List<Transform> boundary = new ArrayList<>(vertices);
//
//            // Translate and rotate the boundary about the updated position
//            for (int i = 0; i < vertices.size(); i++) {
//                boundary.get(i).set(vertices.get(i));
//                Geometry.rotatePoint(boundary.get(i), shape.getPosition().rotation); // Rotate Shape boundary about Image position
//                Geometry.translatePoint(boundary.get(i), shape.getPosition().x, shape.getPosition().y); // Translate Shape
//            }
//            // shape.isValid = true;
//
//            return boundary;
            return entity.getComponent(Boundary.class).getBoundary();
        }
        return null;
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
