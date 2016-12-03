package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.lib.Geometry.Shape;
import camp.computer.clay.util.Geometry;

public class BoundarySystem extends System {

    public BoundarySystem(World world) {
        super(world);
    }

    @Override
    public void update(long dt) {

        // Update Boundaries
        Group<Entity> entities = world.entities.get().filterActive(true).filterWithComponents(Primitive.class, Boundary.class);
        for (int i = 0; i < entities.size(); i++) {
            updateBoundary(entities.get(i));
        }
    }

    private void updateBoundary(Entity entity) {
        // TODO: Cache the boundary and only update when it has been invalidated!

        Shape shape = entity.getComponent(Primitive.class).shape;

        if (shape.getVertices() != null && shape.getVertices().size() > 0) {
            List<Transform> vertices = shape.getVertices();
            List<Transform> boundary = new ArrayList<>(vertices);

            // Translate and rotate the boundary about the updated position
            for (int i = 0; i < vertices.size(); i++) {
                boundary.get(i).set(vertices.get(i));
                Geometry.rotatePoint(boundary.get(i), entity.getComponent(Transform.class).rotation); // Rotate Shape boundary about Model position
                Geometry.translatePoint(boundary.get(i), entity.getComponent(Transform.class).x, entity.getComponent(Transform.class).y); // Translate Shape
            }

            Boundary.set(entity, boundary);
        }
    }
}
