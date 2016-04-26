package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public class Event {

    private UUID uuid;

    private Timeline timeline;

    private Action action;

//    private State state;
    private ArrayList<State> state;

    public Event (UUID uuid, Timeline timeline) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.state = new ArrayList<State>();
    }

    public Event (UUID uuid, Timeline timeline, Action action) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.action = action;

        this.state = new ArrayList<State>();
        String defaultState = action.getScript().getDefaultState();
        this.state.add(new State(defaultState));
    }

    public Event (Timeline timeline, Action action) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.action = action;

        this.state = new ArrayList<State>();

        this.initializeState(action);
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

    public Event (Timeline timeline) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.state = new ArrayList<State>();
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
