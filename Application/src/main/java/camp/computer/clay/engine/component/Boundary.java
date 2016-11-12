package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        for (int i = 0; i < image.getImage().getShapes().size(); i++) {
            if (Geometry.contains(image.getImage().getShapes().get(i).getBoundary(), point)) {
                return true;
            }
        }
        return false;

        // TODO?: return Geometry.contains(this.boundary, point);
    }

    // TODO: Compute bounding box for image when add/remove Shapes and store it here!
    public static Rectangle getBoundingBox(Entity entity) {

        List<Shape> shapes = entity.getComponent(Image.class).getImage().getShapes();

        List<Transform> shapeBoundaries = new ArrayList<>();
        for (int i = 0; i < shapes.size(); i++) {
            shapeBoundaries.addAll(shapes.get(i).getBoundary());
        }
        return Geometry.getBoundingBox(shapeBoundaries);
    }
}
