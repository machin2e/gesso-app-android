package camp.computer.clay.sequencer;

/*
* Defines a simple object to be displayed in a choose view.
*
* This serves as "placeholder" representing the data for the view corresponding to an object in the
* object model.
*/

import java.util.UUID;

import camp.computer.clay.system.Action;
import camp.computer.clay.system.State;
import camp.computer.clay.system.Event;

public class EventHolder {

    // The UUID of the behavior represented by this object.
    private Event event;

    public String tag;

    // for composition
    public String summary;

    private String type; // Used by the custom BaseAdapter to select the layout for the list_item_type_light.

    public String getType () {
        return this.type;
    }

    public void setType (String type) {
        this.type = type;
    }

    private String triggerMessage = "";

    private boolean isSelected = false;

    private boolean isStateVisible = false;

    public String getTriggerMessage () {
        return this.triggerMessage;
    }

    public void setTriggerMessage (String message) {
        this.triggerMessage = message;
    }

    public EventHolder(Event event) {
        super();

        this.event = event;

        // Set parameters
        Action action = event.getAction();

        // <HACK>
        if (action.getScript().getUuid().equals(UUID.fromString("1470f5c4-eaf1-43fb-8fb3-d96dc4e2bee4"))) {
            this.type = "light";
        } else if (action.getScript().getUuid().equals(UUID.fromString("bdb49750-9ead-466e-96a0-3aa88e7d246c"))) {
            this.type = "signal";
        } else if (action.getScript().getUuid().equals(UUID.fromString("99ff8f6d-a0e7-4b6e-8033-ee3e0dc9a78e"))) {
            this.type = "message";
        } else if (action.getScript().getUuid().equals(UUID.fromString("16626b1e-cf41-413f-bdb4-0188e82803e2"))) {
            this.type = "tone";
        } else if (action.getScript().getUuid().equals(UUID.fromString("56d0cf7d-ede6-4529-921c-ae9307d1afbc"))) {
            this.type = "pause";
        } else if (action.getScript().getUuid().equals(UUID.fromString("269f2e19-1fc8-40f5-99b2-6ca67e828e70"))) {
            this.type = "say";

        } else {
            this.type = "complex";
        }
        // </HACK>

        // <HACK>
        // TODO: Update getTitle so it retrieves the value from the associated Event (e.g., from an Event's Action.getTag() method).
        this.tag = action.getTag();
        // <HACK>

        // Initialize
        this.isSelected = false;

        this.isStateVisible = false;

        summary = "";
    }

    // TODO: Remove this constructor!
    public EventHolder(String tag, String type) {
        super();

        // Set parameters
        this.tag = tag;

        this.type = type;

        summary = "";

        // Initialize
        this.isSelected = false;

        this.isStateVisible = false;
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

    public boolean isStateVisible () { return this.isStateVisible; }

    public void setStateVisible (boolean isStateVisible) {
        this.isStateVisible = isStateVisible;
    }

    /**
     * Updates the state of the event's behavior. Creates a new State object and associates
     * it to the Action for this Event.
     * @param stateString The new state to assign to this event's behavior.
     */
    public void updateState (String stateString) {
        State state = new State(stateString);
        //getEvent().getAction().setState (state);
//        getEvent().setBehaviorState (state);
        getEvent().getClay().getStore().removeState (getEvent().getState().get(0));
        this.getEvent().getState().set(0, state);

        // TODO: Update state for the associated event! Do this by searching the leaf nodes!
    }
}
