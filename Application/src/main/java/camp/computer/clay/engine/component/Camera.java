package camp.computer.clay.engine.component;

public class Camera extends Component {

    public static final int DEFAULT_SCALE_PERIOD = 200;

    public static final double DEFAULT_ADJUSTMENT_PERIOD = 200;

    public static double MAXIMUM_SCALE = 1.5;

    public static double SCALE_LEVEL_1 = 0.8;
    public static double SCALE_LEVEL_2 = 1.5;

    // Width of perspective --- actions (e.g., touches) are interpreted relative to this point
    // TODO: Delete!
    public double width;

    // Height of perspective
    // TODO: Delete!
    public double height;

//    // Scale
//    protected final double DEFAULT_SCALE = 1.0f;
//    public double targetScale = DEFAULT_SCALE;

//    // Position
//    protected final Transform DEFAULT_POSITION = new Transform(0, 0);
//    public Transform targetTransform = DEFAULT_POSITION;

    public Camera() {
        super();
    }
}
