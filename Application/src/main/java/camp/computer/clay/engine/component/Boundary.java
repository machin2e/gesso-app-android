package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

public class Boundary extends Component {

    private List<Transform> boundary = new ArrayList<>();

    public List<Transform> getBoundary() {
        return this.boundary;
    }

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
}
