package camp.computer.clay.engine.component;

import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.ImageBuilder.Rectangle;

public class Camera extends Component {

    public static final int DEFAULT_SCALE_PERIOD = 200;

    public static final double DEFAULT_ADJUSTMENT_PERIOD = 200;

    public static double MAXIMUM_SCALE = 1.5;

    public static double SCALE_LEVEL_1 = 0.8;
    public static double SCALE_LEVEL_2 = 1.5;

    public Entity previousFocus = null;
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
