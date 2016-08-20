package camp.computer.clay.model.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.interaction.*;
import camp.computer.clay.model.interaction.Action;

public class Actor {

    private Perspective perspective = null;

    public List<Gesture> gestures = new LinkedList<>();

    public Actor() {
        setup();
    }

    private void setup() {
        // Perspective
        Perspective perspective = new Perspective();
        setPerspective(perspective);
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    public boolean hasPerspective() {
        return perspective != null;
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    /**
     * Returns the most recent interaction.
     *
     * @return The most recent interaction.
     */
    private Gesture getGesture() {
        if (gestures.size() > 0) {
            return gestures.get(gestures.size() - 1);
        } else {
            return null;
        }
    }

    public void onAction(Action action) {

        action.setActor(this);

        switch (action.getType()) {

            case TOUCH: {

                // Having an idea is just accumulating intention. It's a suggestion from your existential
                // controller.

                // Start a new gesture
                Gesture gesture = new Gesture();
                gestures.add(gesture);

                // Add action to gesture
                gesture.add(action);

                // Record gestures on timeline
                // TODO: Cache and store the processAction gestures before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (gestures.size() > 3) {
                    gestures.remove(0);
                }

                // Process the action
                getPerspective().getVisualization().onTouchListener(action);

                break;
            }

            case MOVE: {

                Gesture gesture = getGesture();
                gesture.add(action);

                // Current
                action.isPointing[action.pointerIndex] = true;

                // Classify/Callback
                if (gesture.getDragDistance() > Action.MINIMUM_DRAG_DISTANCE) {
                    action.setType(Action.Type.MOVE);
                    getPerspective().getVisualization().onMoveListener(action);
                }

                break;
            }

            case RELEASE: {

                Gesture gesture = getGesture();
                gesture.add(action);

                // Current
                action.isPointing[action.pointerIndex] = false;

                // Stop listening for a hold action
                gesture.timerHandler.removeCallbacks(gesture.timerRunnable);

//                if (gesture.getDuration() < Action.MAXIMUM_TAP_DURATION) {
//                    action.setType(Action.Type.TOUCH);
//                    getPerspective().getVisualization().onTapListener(action);
//                } else {
//                    action.setType(Action.Type.RELEASE);
//                    getPerspective().getVisualization().onReleaseListener(action);
//                }

                getPerspective().getVisualization().onReleaseListener(action);

                break;
            }
        }
    }
}
