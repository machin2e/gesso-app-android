package camp.computer.clay.engine.manager.util;

import camp.computer.clay.engine.component.Transform;

public class TouchEvent extends Event2 {

    // Contains a single touch event for a single finger. Multiple events can potentially have the
    // same timestamp (e.g., simultaneous press). These are used to update a "touch state
    // accumulator" that represents the most up-to-date state (each finger's state register).

    public static int MAXIMUM_POINT_COUNT = 5;

    public enum Type {
        DOWN,
        MOVE,
        UP
    }

    public Transform[] surfaceTransforms = new Transform[MAXIMUM_POINT_COUNT];
    public Type[] touchStates = new Type[MAXIMUM_POINT_COUNT];

    public long timestamp;
}
