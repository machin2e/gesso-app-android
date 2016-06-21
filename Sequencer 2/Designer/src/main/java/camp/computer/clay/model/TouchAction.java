package camp.computer.clay.model;

import android.graphics.PointF;

public class TouchAction {

    public enum TouchActionType {

        NONE(0),
        TOUCH(1),
        TAP(2),
        DOUBLE_DAP(3),
        HOLD(4),
        MOVE(5),
        PRE_DRAG(6),
        DRAG(7),
        RELEASE(8);

        // TODO: Change the index to a UUID?
        int index;

        TouchActionType(int index) {
            this.index = index;
        }
    }

    private TouchActionType touchActionType;

    private Body body;
    // TODO: targetSprite? or is the state of body containing this info (e.g., hand occupied with model <M>)
    private PointF position;

    // touchedSprite
    // overlappedSprite (not needed, probably, because can look in history, or look at first action in interaction)

    public TouchAction(PointF position, TouchActionType touchActionType) {
        this.position = position;
        this.touchActionType = touchActionType;
    }

    public Body getBody() {
        return this.body;
    }

    public TouchActionType getType() {
        return this.touchActionType;
    }

    public PointF getPosition() {
        return this.position;
    }
}