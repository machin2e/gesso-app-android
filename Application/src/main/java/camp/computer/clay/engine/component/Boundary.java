package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.ImageBuilder.Rectangle;
import camp.computer.clay.util.Geometry;

public class Boundary extends Component {

    public List<Transform> boundary = new ArrayList<>();

    public static class util {
        public static void set(Entity entity, List<Transform> vertices) {
            entity.getComponent(Boundary.class).boundary.clear();
            entity.getComponent(Boundary.class).boundary.addAll(vertices);
        }
    }

    public static List<Transform> get(Entity entity) {
        if (entity.hasComponent(Boundary.class)
                && entity.getComponent(Boundary.class).boundary.size() > 0) { // TODO: Remove check for size. Boundary existence should be enough!
            return entity.getComponent(Boundary.class).boundary;
        }
        return null;
    }


//    public static List<Transform> get(Entity entity) {
//        if (entity.hasComponent(Boundary.class)
//                && Boundary.get(entity).size() > 0) { // TODO: Remove check for size. Boundary existence should be enough!
//            return Boundary.get(entity);
//        }
//        return null;
//    }

    /**
     * Returns {@code true} if any of the {@code Shape}s in the {@code Image} contain the
     * {@code point}.
     *
     * @param point
     * @return
     */
    public static boolean contains(Entity entity, Transform point) {

        // <HACK>
        if (entity.hasComponent(Image.class)) {
            Group<Entity> shapes = Image.getShapes(entity);
            for (int i = 0; i < shapes.size(); i++) {
                if (/*shapes.get(i).getComponent(Geometry.class).shape.isBoundary // HACK
                    &&*/ Geometry.contains(Boundary.get(shapes.get(i)), point)) { // HACK
                    return true;
                }
            }
            return false;
        }
        // </HACK>

        else if (Geometry.contains(Boundary.get(entity), point)) {
            return true;
        }
        return false;

        // TODO: return Geometry.contains(entity.getComponent(Boundary.class).boundary, point);

        // TODO?: return Geometry.contains(this.boundary, point);
    }

    // TODO: Compute bounding box for image when add/remove Shapes and store it here!
    public static Rectangle getBoundingBox(Entity entity) {

        List<Transform> shapeBoundaries = new ArrayList<>();

//        List<Shape> shapes = entity.getComponent(Image.class).getImage().getShapes();
        Group<Entity> shapes = Image.getShapes(entity);
        for (int i = 0; i < shapes.size(); i++) {
//            if (shapes.get(i).getComponent(Geometry.class).shape.isBoundary) { // HACK
            //shapeBoundaries.addAll(BoundarySystem.get(shapes.get(i).getComponent(Geometry.class).shape)); // HACK
            shapeBoundaries.addAll(Boundary.get(shapes.get(i))); // HACK
//            }
        }

        return Geometry.getBoundingBox(shapeBoundaries);
    }
}
