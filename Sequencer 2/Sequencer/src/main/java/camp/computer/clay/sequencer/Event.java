package camp.computer.clay.sequencer;

public class Event {

    private int type;

    private String title;

    public Event (int type, String title) {
        this.type = type;
        this.title = title;
    }

    public int getType () {
        return this.type;
    }

    public String getTitle () {
        return this.title;
    }
}
