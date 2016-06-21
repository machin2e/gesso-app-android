package camp.computer.clay.model;

import android.graphics.PointF;

public class TouchInteraction {

    public enum TouchInteractionType {

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

        TouchInteractionType(int index) {
            this.index = index;
        }
    }

    private TouchInteractionType touchInteractionType;

    private Body body;
    // TODO: targetSprite? or is the state of body containing this info (e.g., hand occupied with model <M>)
    private PointF position;

    // touchedSprite
    // overlappedSprite (not needed, probably, because can look in history, or look at first action in interaction)

    public TouchInteraction(PointF position, TouchInteractionType touchInteractionType) {
        this.position = position;
        this.touchInteractionType = touchInteractionType;
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
}