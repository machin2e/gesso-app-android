package camp.computer.clay.model.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.interactivity.*;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.visualization.util.geometry.Geometry;

public class Body {

    private Perspective perspective = null;

    public List<Interaction> interactions = new LinkedList<>();

    public Body() {
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
    private Interaction getInteraction() {
        if (interactions.size() > 0) {
            return interactions.get(interactions.size() - 1);
        } else {
            return null;
        }
    }

    public void onAction(Action action) {

        action.setBody(this);

        switch (action.getType()) {

            case TOUCH: {

                // Having an idea is just accumulating intention. It's a suggestion from your existential
                // controller.

                // Start a new interaction
                Interaction interaction = new Interaction();
                interactions.add(interaction);

                // Add action to interaction
                interaction.add(action);

                // Record interactions on timeline
                // TODO: Cache and store the processAction interactions before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (interactions.size() > 3) {
                    interactions.remove(0);
                }

                // Process the action
                getPerspective().getVisualization().onTouchListener(action);

                break;
            }

            case MOVE: {

                Interaction interaction = getInteraction();
                interaction.add(action);

                // Current
                action.isTouching[action.pointerIndex] = true;

                // Classify/Callback
                if (interaction.getDragDistance() > Action.MIN_DRAG_DISTANCE) {
                    getPerspective().getVisualization().onDragListener(action);
                }

                break;
            }

            case RELEASE: {

                Interaction interaction = getInteraction();
                interaction.add(action);

                // Current
                action.isTouching[action.pointerIndex] = false;

                // Stop listening for a hold action
                interaction.timerHandler.removeCallbacks(interaction.timerRunnable);

                if (interaction.getDuration() < Action.MAX_TAP_DURATION) {
                    getPerspective().getVisualization().onTapListener(action);
                } else {
                    getPerspective().getVisualization().onReleaseListener(action);
                }

                break;
            }
        }
    }
}
