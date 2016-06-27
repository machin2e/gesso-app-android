package camp.computer.clay.model;

import java.util.ArrayList;

import camp.computer.clay.sprite.Sprite;

// An interactivity is a temporal sequence of one or more interactions.
//
// Model this with a "touch interaction envelope" or "interaction envelope".

public class TouchInteractivity {
    private ArrayList<TouchInteraction> touchInteractions = new ArrayList<TouchInteraction>();

    // TODO: Classify these!
    public boolean[] isTouchingSprite = new boolean[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];
    public Sprite[] touchedSprite = new Sprite[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];

    public TouchInteractivity() {
    }

    public ArrayList<TouchInteraction> getInteractions() {
        return this.touchInteractions;
    }

    public void addInteraction (TouchInteraction touchInteraction) {
        this.touchInteractions.add(touchInteraction);
    }

    public TouchInteraction getFirstInteraction() {
        if (touchInteractions.size() > 0) {
            return touchInteractions.get(0);
        } else {
            return null;
        }
    }

    public TouchInteraction getLatestInteraction() {
        if (touchInteractions.size() > 0) {
            return touchInteractions.get(touchInteractions.size() - 1);
        } else {
            return null;
        }
    }

    public TouchInteraction getPreviousInteraction(TouchInteraction touchInteraction) {
        for (int i = 0; i < touchInteractions.size() - 1; i++) {
            if (touchInteractions.get(i + 1) == touchInteraction) {
                return touchInteractions.get(i);
            }
        }
        return null;
    }

    // TODO: getStartTime()
    // TODO: getStopTime()
    // TODO: getDuration()
    // TODO: getTouchPath()

    // <CLASSIFIER>

    // </CLASSIFIER>
}
