package camp.computer.clay.model.interaction;

public interface OnTouchActionListener {

    enum Type {

        NONE(0),
        TOUCH(1),
        HOLD(2),
        DRAG(5),
        RELEASE(6),
        TAP(7);

        // TODO: Change the index to a UUID?
        int index;

        Type(int index) {
            this.index = index;
        }
    }

    // NONE, TOUCH, TAP, HOLD, MOVE, TWITCH, DRAG, RELEASE

    // Interaction<Touch>, Interaction<Motion>, Interaction<Utterance>

    void onAction(TouchInteraction touchInteraction);
}
