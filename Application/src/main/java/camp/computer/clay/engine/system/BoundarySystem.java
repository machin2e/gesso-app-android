package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Image;
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

        // Update Boundaries
        Group<Entity> entitiesWithTransform2 = world.Manager.getEntities().filterActive(true).filterWithComponents(ShapeComponent.class, Boundary.class);
        for (int i = 0; i < entitiesWithTransform2.size(); i++) {
            updateBoundary(entitiesWithTransform2.get(i));
        }
    }

    private void updateBoundary(Entity entity) {

        Shape shape = entity.getComponent(ShapeComponent.class).shape;

        if (shape.isBoundary) { // HACK // TODO: Remove need for this!

            // TODO: Cache the boundary and only update when it has been invalidated!

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

    public static List<Transform> getBoundary(Entity entity) {
        if (entity.hasComponent(Boundary.class)
                && entity.getComponent(Boundary.class).getBoundary().size() > 0) { // TODO: Remove check for size. Boundary existence should be enough!
            return entity.getComponent(Boundary.class).getBoundary();
        }
        return null;
    }
}
