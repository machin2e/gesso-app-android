package camp.computer.clay.engine.component;

import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.Geometry.Rectangle;

public class Camera extends Component {

    public enum Mode {
        FREE,
        FOCUS
    }

    public static double MAXIMUM_SCALE = 1.5;

    public Mode mode = Mode.FREE;
    public Entity focus = null;

    public Rectangle boundingBox = null;
    public List<Transform> boundary = null;

    public Camera() {
        super();
    }
}
