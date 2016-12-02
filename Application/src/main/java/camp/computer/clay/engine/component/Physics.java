package camp.computer.clay.engine.component;

import camp.computer.clay.lib.Geometry.Vector;

public class Physics extends Component {

    public static double DEFAULT_CAMERA_VELOCITY_X = 0.0030; // previously scaleVelocity
    public static double DEFAULT_CAMERA_VELOCITY_Y = 0.0030; // previously panVelocity
    public static double DEFAULT_CAMERA_VELOCITY_Z = 0.0030; // previously panVelocity

    public Vector velocity = new Vector(DEFAULT_CAMERA_VELOCITY_X, DEFAULT_CAMERA_VELOCITY_Y, DEFAULT_CAMERA_VELOCITY_Z);

    // <REFACTOR>
    // TODO: Replace with "facing direction"
    public Transform targetTransform = new Transform(0, 0);
    // </REFACTOR>

    public Physics() {
        super();
    }
}
