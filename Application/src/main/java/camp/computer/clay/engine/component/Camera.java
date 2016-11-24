package camp.computer.clay.engine.component;

public class Camera extends Component {

    public static final int DEFAULT_SCALE_PERIOD = 200;

    public static final double DEFAULT_ADJUSTMENT_PERIOD = 200;

    public static double MAXIMUM_SCALE = 1.5;

    public static double SCALE_LEVEL_1 = 0.8;
    public static double SCALE_LEVEL_2 = 1.5;

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
