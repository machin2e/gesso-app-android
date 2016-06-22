package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public class Event {

    private UUID uuid;

    private Timeline timeline;

    private Action action;

//    private State state;
    private ArrayList<State> state;


    // <HACK>
    private ContentEntry contentEntry; // Content entry for the event (replaces state)
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
//            this.state.add(new State(defaultState));
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



    public ContentEntry getContent() {
        return this.contentEntry;
    }

    private void initializeContent() {
        // <HACK>
        // TODO: Update this from a list of the observables received from the boards.
        ContentEntry eventContent = new ContentEntry("event");
        ContentEntry channelsContent = eventContent.list("channels");
        //ContentEntry channelsContent = eventContent.list("channels").each(12).has("number", "direction", );
        for (int i = 0; i < 12; i++) {

            // device/<uuid>/channels/<number>
            ContentEntry channelContent = channelsContent.put(String.valueOf(i + 1));

            // device/<uuid>/channels/<number>/enable
            channelContent.put("enable").from("true", "false").set("false");

            // device/<uuid>/channels/<number>/number
            channelContent.put("number", String.valueOf(i + 1));

            // device/<uuid>/channels/<number>/direction
            channelContent.put("direction").from("input", "output").set("input");

            // device/<uuid>/channels/<number>/type
            channelContent.put("type").from("toggle", "waveform", "pulse").set("toggle"); // TODO: switch

            // device/<uuid>/channels/<number>/content
            ContentEntry channelContentContent = channelContent.put("content");

            // device/<uuid>/channels/<number>/content/<observable>
            // TODO: Retreive the "from" values and the "default" value from the exposed observables on the actual hardware (or the hardware profile)
            channelContentContent.put("toggle_value").from("on", "off").set("off");
            channelContentContent.put("waveform_sample_value", "none");
            channelContentContent.put("pulse_period_seconds", "0");
            channelContentContent.put("pulse_duty_cycle", "0");

            for (ContentEntry child : channelContentContent.getChildren()) {
                child.put("valid").from("true", "false").set("false");
                child.put("type");
                child.put("device").set(this.getTimeline().getDevice().getUuid().toString());
                    child.put("sourceMachine");
                child.put("provider");
                child.put("value");
            }
        }
        this.contentEntry = eventContent;
        // </HACK>
    }

    public Event (Timeline timeline) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.state = new ArrayList<State>();

        this.initializeContent();
    }

//    public Event (Timeline timeline, Action action, State state) {
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
        return getTimeline().getDevice().getClay();
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
