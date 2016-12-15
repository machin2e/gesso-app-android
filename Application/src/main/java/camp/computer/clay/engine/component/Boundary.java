package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.util.Geometry;

public class Boundary extends Component {

    public List<Transform> boundary = new ArrayList<>();

    public static List<Transform> get(Entity entity) {
        if (entity.hasComponent(Boundary.class)
                && entity.getComponent(Boundary.class).boundary.size() > 0) { // TODO: Remove check for size. Boundary existence should be enough!
            return entity.getComponent(Boundary.class).boundary;
        }
        return null;
    }

    /**
     * Returns {@code true} if any of the {@code Shape}s in the {@code ModelBuilder} contain the
     * {@code point}.
     *
     * @param point
     * @return
     */
    public static boolean contains(Entity entity, Transform point) {

        // <HACK>
        if (entity.hasComponent(Model.class)) {
            Group<Entity> primitives = Model.getPrimitives(entity);
            for (int i = 0; i < primitives.size(); i++) {
                if (Geometry.contains(Boundary.get(primitives.get(i)), point)) {
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
    }
}
