package camp.computer.clay.sequencer;

/*
* Defines a simple object to be displayed in a list view.
*
* This serves as "placeholder" representing the data for the view corresponding to an object in the
* object model.
*/

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.Behavior;
import camp.computer.clay.system.BehaviorState;
import camp.computer.clay.system.Event;

public class EventHolder {

    // The UUID of the behavior represented by this object.
    private Event event;

    public String title;

    // for composition
    public String summary;

    public static int DEFAULT_TYPE = EventHolderAdapter.IO_CONTROL_LAYOUT;

    public int type; // Used by the custom BaseAdapter to select the layout for the list_item_type_light.

    private boolean isSelected = false;

//    public boolean hasFocus = false;

//    public boolean repeat = false;

    // default constructor
//    public EventHolder() {
//        this("Title", "Subtitle", DEFAULT_TYPE);
//    }

    public EventHolder(Event event) {
        super();

        this.event = event;

        // Set parameters
        Behavior behavior = event.getBehavior();

        // <HACK>
        // TODO: Update getTitle so it retrieves the value from the associated Event (e.g., from an Event's Behavior.getTag() method).
        this.title = behavior.getTag();
        // <HACK>

        // <HACK>
        // Get the layout type to prepare for view generation
        // TODO: Automatically generate or retrieve layouts dynamically based on UUID here based on behavior events model, rather than referencing them as stored in XML.
        if (behavior.getTag().equals("light")) {
            this.type = EventHolderAdapter.LIGHT_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("signal")) {
            this.type = EventHolderAdapter.IO_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("message")) {
            this.type = EventHolderAdapter.MESSAGE_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("pause")) {
            this.type = EventHolderAdapter.WAIT_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("say")) {
            this.type = EventHolderAdapter.SAY_CONTROL_LAYOUT;
        } else {
            this.type = EventHolderAdapter.COMPLEX_LAYOUT;
        }
        // </HACK>

        // Initialize
        this.isSelected = false;

        // Initialize type
//        initializeType();
    }

    // TODO: Remove this constructor!
    public EventHolder(String title, String subTitle, int type) {
        super();

        // Set parameters
        this.title = title;

        this.type = type;

        // Initialize
        this.isSelected = false;
    }

    public Event getEvent() {
        return this.event;
    }

    public boolean isSelected () {
        return this.isSelected;
    }

    public void setSelected (boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * Updates the state of the event's behavior. Creates a new BehaviorState object and associates
     * it to the Behavior for this Event.
     * @param stateString The new state to assign to this event's behavior.
     */
    public void updateState (String stateString) {
        BehaviorState behaviorState = new BehaviorState(stateString);
        //getEvent().getBehavior().setState (behaviorState);
//        getEvent().setBehaviorState (behaviorState);
        getEvent().getClay().getStore().removeState (getEvent().getBehaviorState().get(0));
        this.getEvent().getBehaviorState().set(0, behaviorState);

        // TODO: Update state for the associated event! Do this by searching the leaf nodes!
    }
}
