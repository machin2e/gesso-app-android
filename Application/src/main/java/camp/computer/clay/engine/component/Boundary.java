package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.BoundarySystem;
import camp.computer.clay.util.ImageBuilder.Geometry;
import camp.computer.clay.util.ImageBuilder.Rectangle;

public class Boundary extends Component {

//    public static HashMap<Shape, ArrayList<Transform>> innerBoundaries = new HashMap<>();

    private List<Transform> boundary = new ArrayList<>();

    public void setBoundary(List<Transform> points) {
        this.boundary.clear();
        this.boundary.addAll(points);
    }

    public List<Transform> getBoundary() {
        return this.boundary;
    }

    /**
     * Returns {@code true} if any of the {@code Shape}s in the {@code Image} contain the
     * {@code point}.
     *
     * @param point
     * @return
     */
    public static boolean contains(Entity entity, Transform point) {

//        Image image = entity.getComponent(Image.class);
//        List<Shape> shapes = image.getImage().getShapes();
        Group<Entity> shapes = Image.getShapes(entity);
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getComponent(ShapeComponent.class).shape.isBoundary // HACK
                    && Geometry.contains(BoundarySystem.getBoundary(shapes.get(i).getComponent(ShapeComponent.class).shape), point)) { // HACK
                return true;
            }
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
            if (shapes.get(i).getComponent(ShapeComponent.class).shape.isBoundary) { // HACK
                shapeBoundaries.addAll(BoundarySystem.getBoundary(shapes.get(i).getComponent(ShapeComponent.class).shape)); // HACK
            }
        }

        return Geometry.getBoundingBox(shapeBoundaries);
    }
}
