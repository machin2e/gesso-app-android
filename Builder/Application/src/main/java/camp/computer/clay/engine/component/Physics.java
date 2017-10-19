package camp.computer.clay.engine.component;

import camp.computer.clay.lib.Geometry.Vector;

public class Physics extends Component {

    // 0.0030, 0.0060
    public static double DEFAULT_VELOCITY_X = 0.0060; // previously scaleVelocity
    public static double DEFAULT_VELOCITY_Y = 0.0060; // previously panVelocity
    public static double DEFAULT_VELOCITY_Z = 0.0060; // previously panVelocity

    public Vector velocity = new Vector(DEFAULT_VELOCITY_X, DEFAULT_VELOCITY_Y, DEFAULT_VELOCITY_Z);

    // <REFACTOR>
    // TODO: Replace with "facing direction" and let acceleration cause things to stop? Add moveTo and pushToward functions for computing target trajectory velocity and acceleration, and applying a force in a direction with a vector.
    public Transform targetTransform = new Transform(0, 0);
    // </REFACTOR>

    public Physics() {
        super();
    }
}
