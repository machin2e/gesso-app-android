package camp.computer.clay.system;

import java.util.UUID;

public class Event {

    private UUID uuid;

    private Timeline timeline;

    private Behavior behavior;

    public Event (UUID uuid, Timeline timeline) {

        this.uuid = uuid;

        this.timeline = timeline;
    }

    public Event (UUID uuid, Timeline timeline, Behavior behavior) {

        this.uuid = uuid;

        this.timeline = timeline;

        this.behavior = behavior;
    }

    public Event (Timeline timeline) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;
    }

    public Event (Timeline timeline, Behavior behavior) {

        this.uuid = UUID.randomUUID();

        this.timeline = timeline;

        this.behavior = behavior;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Clay getClay() {
        return getTimeline().getUnit().getClay();
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline (Timeline timeline) {
        this.timeline = timeline;
    }

    public Behavior getBehavior() { return this.behavior; }

    public void setBehavior (Behavior behavior) {
        this.behavior = behavior;
    }
}
