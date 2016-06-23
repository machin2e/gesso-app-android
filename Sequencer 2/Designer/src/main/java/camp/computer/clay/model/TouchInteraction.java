package camp.computer.clay.model;

import android.graphics.PointF;

public class TouchInteraction {

    final public static long DEFAULT_TIMESTAMP = 0L;

    public enum TouchInteractionType {

        NONE(0),
        TOUCH(1),
        HOLD(2),
        MOVE(3),
        PRE_DRAG(4),
        DRAG(5),
        RELEASE(6),
        TAP(7),
        DOUBLE_DAP(8);

        // TODO: Change the index to a UUID?
        int index;

        TouchInteractionType(int index) {
            this.index = index;
        }
    }

    private TouchInteractionType touchInteractionType;

    private Body body;
    // TODO: targetSprite? or is the state of body containing this info (e.g., hand occupied with model <M>)

    private PointF position;

    // <CONTEXT>
    private long timestamp = DEFAULT_TIMESTAMP;
    // TODO: Sensor data (inc. 3D orienetation, brightness)
    // </CONTEXT>

    //public int touches[];
    public int pointerId = -1;

    // touchedSprite
    // overlappedSprite (not needed, probably, because can look in history, or look at first action in interaction)

    public TouchInteraction(PointF position, TouchInteractionType touchInteractionType) {
        this.position = position;
        this.touchInteractionType = touchInteractionType;
        this.timestamp = java.lang.System.currentTimeMillis ();
    }

    public Body getBody() {
        return this.body;
    }

    public TouchInteractionType getType() {
        return this.touchInteractionType;
    }

    public PointF getPosition() {
        return this.position;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}