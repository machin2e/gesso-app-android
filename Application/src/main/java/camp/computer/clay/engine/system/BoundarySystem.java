package camp.computer.clay.engine.system;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.util.FilterStrategy;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.lib.Geometry.ModelBuilder;
import camp.computer.clay.lib.Geometry.Shape;
import camp.computer.clay.util.Geometry;

public class BoundarySystem extends System {

    Group<Entity> entitiesWithBoundary, entitiesWithModelAndBoundary;

    public BoundarySystem(World world) {
        super(world);

        entitiesWithBoundary = world.entityManager.subscribe(
                new FilterStrategy(Group.Filters.filterWithComponents, Primitive.class, Boundary.class)
        );

        entitiesWithModelAndBoundary = world.entityManager.subscribe(
                new FilterStrategy(Group.Filters.filterWithComponents, Model.class, Boundary.class)
        );
    }

    @Override
    public void update(long dt) {

        // Updates Boundary components
        for (int i = 0; i < entitiesWithBoundary.size(); i++) {
            generateBoundary(entitiesWithBoundary.get(i));
        }

        for (int i = 0; i < entitiesWithModelAndBoundary.size(); i++) {
            long assetUid = entitiesWithModelAndBoundary.get(i).getComponent(Model.class).assetUid;
            ModelBuilder modelBuilder = (ModelBuilder) world.cache.get(assetUid);
            if (modelBuilder != null) {
                generateBoundaries(entitiesWithModelAndBoundary.get(i));
            }
        }
    }

    private void generateBoundary(Entity entity) {
        // TODO: Cache_OLD the boundary and only update when it has been invalidated!

        Shape shape = entity.getComponent(Primitive.class).shape;

        if (shape.getVertices() != null && shape.getVertices().size() > 0) {
            List<Transform> vertices = shape.getVertices();
            List<Transform> boundary = new ArrayList<>(vertices);

            // Translate and rotate the boundary about the updated position
            for (int i = 0; i < vertices.size(); i++) {
                boundary.get(i).set(vertices.get(i));
                Geometry.rotatePoint(boundary.get(i), entity.getComponent(Transform.class).rotation); // Rotate Shape boundary about ModelBuilder position
                Geometry.translatePoint(boundary.get(i), entity.getComponent(Transform.class).x, entity.getComponent(Transform.class).y); // Translate Shape
            }

            // Set the Boundary
//            entity.getComponent(Boundary.class).boundary.clear();
//            entity.getComponent(Boundary.class).boundary.addAll(vertices);
            entity.getComponent(Boundary.class).boundary = vertices;
        }
    }


    //XX TODO: Restore Model file format to have just one model, not multiple models/model indices
    //XX TODO: Label shapes with boundaries (require) in file
    //XX TODO: Load shape labels and store them in the Model, like EventManager does for Events
    // TODO: Generate boundaries for boundary shapes, and make them accessible by their tag (same as the shape). Use shape vertices as base for boundary, and offset/position with Entity's Transform component.
    // ...
    // TODO: Load Model as asset in Cache. Separate style (unique per Entity) and geometry (same for all Entities)
    // TODO: In Model component, Reference Model Asset in Cache with Asset UID
    // ...
    // TODO: Store the "group" from model file
    private void generateBoundaries(Entity entity) {

        Model model = entity.getComponent(Model.class);

        long assetUid = model.assetUid;
        ModelBuilder modelBuilder = (ModelBuilder) world.cache.get(assetUid);
        List<Shape> shapes = modelBuilder.getShapes();

        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).isBoundary) {
                List<Transform> vertices = shapes.get(i).getVertices(); // Computes and returns copy of vertices

                // Translate and rotate the boundary about the updated position
                for (int j = 0; j < vertices.size(); j++) {

                    // Rotate and translate boundary (shape vertices) based on Shape position
                    Geometry.rotatePoint(vertices.get(j), shapes.get(i).getRotation()); // Rotate Shape boundary about ModelBuilder position
                    Geometry.translatePoint(vertices.get(j), shapes.get(i).getPosition().x, shapes.get(i).getPosition().y); // Translate Shape

                    // Rotate and translate boundary based on Entity's Transform
                    Geometry.rotatePoint(vertices.get(j), entity.getComponent(Transform.class).rotation); // Rotate Shape boundary about ModelBuilder position
                    Geometry.translatePoint(vertices.get(j), entity.getComponent(Transform.class).x, entity.getComponent(Transform.class).y); // Translate Shape
                }

                // Store shape boundary in Boundary component
                long shapeBoundaryUid = modelBuilder.getTagUid(shapes.get(i).getTag());
                entity.getComponent(Boundary.class).boundaries.put(shapeBoundaryUid, (ArrayList<Transform>) vertices);
            }
        }
    }
}
