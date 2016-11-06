package camp.computer.clay.engine.component;

public class Camera extends Component {

    public static final int DEFAULT_SCALE_PERIOD = 200;

    public static final double DEFAULT_ADJUSTMENT_PERIOD = 200;

    public static double MAXIMUM_SCALE = 1.5;

    // Width of perspective --- actions (e.g., touches) are interpreted relative to this point
    public double width;

    // Height of perspective
    public double height;

    // Scale
    protected final double DEFAULT_SCALE = 1.0f;
    public double targetScale = DEFAULT_SCALE;
    protected int scalePeriod = DEFAULT_SCALE_PERIOD;
    public double scaleDelta = 0;

    // Position
    protected final Transform DEFAULT_POSITION = new Transform(0, 0);
    public Transform targetPosition = DEFAULT_POSITION;
    public int positionFrameIndex = 0;
    public int positionFrameLimit = 0;
    public Transform originalPosition = new Transform();

    public Camera() {
        super();
    }
}
