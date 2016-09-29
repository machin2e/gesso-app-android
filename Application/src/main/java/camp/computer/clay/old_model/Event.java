package camp.computer.clay.old_model;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.Clay;

public class Event {

    private UUID uuid;

    private Timeline timeline;

    private Action action;

//    private State state;
    private ArrayList<State> state;


    // <HACK>
    private Descriptor descriptor; // Descriptor entry for the event (replaces state)
    // </HACK>

    public Event (UUID uuid, Timeline timeline) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.state = new ArrayList<State>();

        this.initializeContent();
    }

    public Event (UUID uuid, Timeline timeline, Action action) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.action = action;

        this.state = new ArrayList<State>();
        String defaultState = action.getScript().getDefaultState();
        this.state.add(new State(defaultState));

        this.initializeContent();
    }

    public Event (Timeline timeline, Action action) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.action = action;

        this.state = new ArrayList<State>();

        this.initializeState(action);
        this.initializeContent();
//        if (action.getActions().size() == 0) {
//            String defaultState = action.getScript().getDefaultState();
//            this.state.addEvent(new State(defaultState));
//        } else {
//            this.initializeState(action);
//        }
    }

    private void initializeState(Action action) {
        if (action.hasScript()) {
            String defaultState = action.getScript().getDefaultState();
            this.state.add(new State(defaultState));
        } else {
            for (Action child : action.getActions()) {
                initializeState(child);
            }
        }
    }



    public Descriptor getDescriptor() {
        return this.descriptor;
    }

    private void initializeContent() {
        // <HACK>
        // TODO: Update this from a list of the observables received from the boards.
        Descriptor eventDescriptor = new Descriptor("event");
        Descriptor channelsDescriptor = eventDescriptor.list("channels");
        //Descriptor channelsDescriptor = eventDescriptor.list("channels").each(12).has("number", "direction", );
        for (int i = 0; i < 12; i++) {

            // device/<uuid>/channels/<number>
            Descriptor channelDescriptor = channelsDescriptor.put(String.valueOf(i + 1));

            // device/<uuid>/channels/<number>/enable
            channelDescriptor.put("enable").from("true", "false").set("false");

            // device/<uuid>/channels/<number>/number
            channelDescriptor.put("number", String.valueOf(i + 1));

            // device/<uuid>/channels/<number>/direction
            channelDescriptor.put("direction").from("input", "output").set("input");

            // device/<uuid>/channels/<number>/type
            channelDescriptor.put("type").from("toggle", "waveform", "pulse").set("toggle"); // TODO: switch

            // device/<uuid>/channels/<number>/descriptor
            Descriptor channelContentDescriptor = channelDescriptor.put("descriptor");

            // device/<uuid>/channels/<number>/descriptor/<observable>
            // TODO: Retreive the "from" values and the "default" value from the exposed observables on the actual hardware (or the hardware profile)
            channelContentDescriptor.put("toggle_value").from("on", "off").set("off");
            channelContentDescriptor.put("waveform_sample_value", "none");
            channelContentDescriptor.put("pulse_period_seconds", "0");
            channelContentDescriptor.put("pulse_duty_cycle", "0");

            for (Descriptor child : channelContentDescriptor.getChildren()) {
                child.put("valid").from("true", "false").set("false");
                child.put("type");
                child.put("device").set(this.getTimeline().getPhoneHost().getUuid().toString());
                    child.put("sourceMachine");
                child.put("provider");
                child.put("value");
            }
        }
        this.descriptor = eventDescriptor;
        // </HACK>
    }

    public Event (Timeline timeline) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.state = new ArrayList<State>();

        this.initializeContent();
    }

//    public Event (Timeline timeline, Event action, State state) {
//
//        this.uuid = UUID.randomUUID();
//
//        this.timeline = timeline;
//
//        this.action = action;
//
//        this.state = state;
//    }

    public UUID getUuid() {
        return uuid;
    }

    public Clay getClay() {
        return getTimeline().getPhoneHost().getClay();
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline (Timeline timeline) {
        this.timeline = timeline;
    }

    public Action getAction() { return this.action; }

    public void setAction(Action action) {
        this.action = action;
    }

    public ArrayList<State> getState() { return this.state; }

    public void addActionState(State state) { this.state.add (state); };

    //public void setBehaviorState (State state) { this.state = state; };
}
