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

    public Mode mode = Mode.FOCUS;
    public Entity focus = null;

    public Rectangle boundingBox = null;
    public List<Transform> boundary = null;

    // <DELETE>
    // Width of perspective --- actions (e.g., touches) are interpreted relative to this point
    public double width;

    // Height of perspective
    public double height;
    // </DELETE>


    // What if you had to wrap it up before tomorrow? Wrap it up. Wrap it up. Apply your edge. Resist against hate and discontent. Attend to your ideals and self-awareness.

    public Camera() {
        super();
    }
}
