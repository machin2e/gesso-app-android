package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Rectangle;

public class Boundary extends Component {

    private List<Transform> boundary = new ArrayList<>();

    public void setBoundary(List<Transform> points) {
        this.boundary.clear();
        this.boundary.addAll(points);
    }

//    public List<Transform> getBoundary() {
//        return this.boundary;
//    }

//    /**
//     * Updates the bounds of the {@code Shape} for use in touch interaction, layout, and collision
//     * detection. Hey there, mango bongo.
//     */
//    protected void updateShapeBoundary() {
//
//        List<Transform> vertices = getVertices();
//        List<Transform> boundary = getBoundary();
//
//        // Translate and rotate the boundary about the updated position
//        for (int i = 0; i < vertices.size(); i++) {
//            boundary.get(i).set(vertices.get(i));
//            Geometry.rotatePoint(boundary.get(i), position.rotation); // Rotate Shape boundary about Image position
//            Geometry.translatePoint(boundary.get(i), position.x, position.y); // Translate Shape
//        }
//    }

    /**
     * Returns {@code true} if any of the {@code Shape}s in the {@code Image} contain the
     * {@code point}.
     *
     * @param point
     * @return
     */
    public boolean contains(Transform point) {

        Image image = getEntity().getComponent(Image.class);

        for (int i = 0; i < image.shapes.size(); i++) {
            //if (shapes.get(i).contains(point)) {
            if (Geometry.contains(image.shapes.get(i).getBoundary(), point)) {
                return true;
            }
        }
        return false;

        // TODO?: return Geometry.contains(this.boundary, point);
    }

    // TODO: Compute bounding box for image when add/remove Shapes and store it here!
    public Rectangle getBoundingBox() {

        Image image = getEntity().getComponent(Image.class);

        List<Transform> shapeBoundaries = new LinkedList<>();
        for (int i = 0; i < image.shapes.size(); i++) {
            shapeBoundaries.addAll(image.shapes.get(i).getBoundary());
        }
        return Geometry.getBoundingBox(shapeBoundaries);
    }
}
