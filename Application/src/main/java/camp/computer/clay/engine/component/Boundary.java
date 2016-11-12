package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.ImageBuilder.Geometry;
import camp.computer.clay.util.ImageBuilder.Rectangle;
import camp.computer.clay.util.ImageBuilder.Shape;

public class Boundary extends Component {

    public static HashMap<Shape, ArrayList<Transform>> shapeBoundaries = new HashMap<>();

    private List<Transform> boundary = new ArrayList<>();

    public void setBoundary(List<Transform> points) {
        this.boundary.clear();
        this.boundary.addAll(points);
    }

    /**
     * Returns {@code true} if any of the {@code Shape}s in the {@code Image} contain the
     * {@code point}.
     *
     * @param point
     * @return
     */
    public static boolean contains(Entity entity, Transform point) {

        Image image = entity.getComponent(Image.class);

        List<Shape> shapes = image.getImage().getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).isBoundary
                    && Geometry.contains(shapes.get(i).getBoundary(), point)) {
                return true;
            }
        }
        return false;

        // TODO?: return Geometry.contains(this.boundary, point);
    }

    // TODO: Compute bounding box for image when add/remove Shapes and store it here!
    public static Rectangle getBoundingBox(Entity entity) {

        List<Transform> shapeBoundaries = new ArrayList<>();

        List<Shape> shapes = entity.getComponent(Image.class).getImage().getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).isBoundary) {
                shapeBoundaries.addAll(shapes.get(i).getBoundary());
            }
        }

        return Geometry.getBoundingBox(shapeBoundaries);
    }
}
