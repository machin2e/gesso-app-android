package camp.computer.clay.engine.event;

import camp.computer.clay.engine.component.Transform;

public class TouchEvent extends Event {

    // Contains a single touch event for a single finger. Multiple eventManager can potentially have the
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

    public TouchEvent() {
        super("TouchEvent");
    }
}
