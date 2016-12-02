package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.Geometry.Shape;

public class BoundarySystem extends System {

    public BoundarySystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        // Update Boundaries
        Group<Entity> entities = world.Manager.getEntities().filterActive(true).filterWithComponents(Primitive.class, Boundary.class);
        for (int i = 0; i < entities.size(); i++) {
            updateBoundary(entities.get(i));
        }

//        // Update Model Boundaries
//        Group<Entity> imageEntities = world.Manager.getEntities().filterActive(true).filterWithComponents(Primitive.class, Boundary.class);
//        for (int i = 0; i < imageEntities.size(); i++) {
//            updateBoundary(imageEntities.get(i));
//        }
    }

    private void updateBoundary(Entity entity) {
        // TODO: Cache the boundary and only update when it has been invalidated!

//        if (entity.hasComponent(Primitive.class)) {
        Shape shape = entity.getComponent(Primitive.class).shape;

        if (shape.getVertices() != null && shape.getVertices().size() > 0) {
            List<Transform> vertices = shape.getVertices();
            List<Transform> boundary = new ArrayList<>(vertices);

            // Translate and rotate the boundary about the updated position
            for (int i = 0; i < vertices.size(); i++) {
                boundary.get(i).set(vertices.get(i));
//                Primitive.rotatePoint(boundary.get(i), shape.getPosition().rotation); // Rotate Shape boundary about Model position
//                Primitive.translatePoint(boundary.get(i), shape.getPosition().x, shape.getPosition().y); // Translate Shape
                camp.computer.clay.util.Geometry.rotatePoint(boundary.get(i), entity.getComponent(Transform.class).rotation); // Rotate Shape boundary about Model position
                camp.computer.clay.util.Geometry.translatePoint(boundary.get(i), entity.getComponent(Transform.class).x, entity.getComponent(Transform.class).y); // Translate Shape
            }

            Boundary.util.set(entity, boundary);
        }
//        } else if (entity.hasComponent(Model.class)) {
//
//            List<Transform> boundary = new ArrayList<>();
//
//            Group<Entity> shapes = Model.getShapes(entity);
//            for (int i = 0; i < shapes.size(); i++) {
////                if (/*shapes.get(i).getComponent(Primitive.class).shape.isBoundary // HACK
////                    &&*/ Primitive.contains(Boundary.get(shapes.get(i)), point)) { // HACK
////                    return true;
////                }
//
//                shapes.get(i).getComponent(Boundary.class).boundary;
//            }
//
//            Boundary.set(entity, boundary);
//
//        }
    }
}
