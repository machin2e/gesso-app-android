package camp.computer.clay.system;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.UUID;

public class Timeline {

    private UUID uuid;

    private ArrayList<Event> events = new ArrayList<Event>();

    private Unit unit = null;

    Timeline () {

    }

    public Timeline(Unit unit) {
        super();

        this.uuid = UUID.randomUUID();

        this.unit = unit;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    @JsonIgnore
    public Unit getUnit () {
        return this.unit;
    }

    public void addEvent (Event event) {

        // Add behavior to the list of behaviors in the loop sequence
        if (!this.events.contains(event)) {
            this.events.add(event);

            // TODO: Store in remote repository
        }

    }

    public boolean hasEvents () {
        return (this.events.size () > 0);
    }

    public boolean hasEvent (Event event) {
        return this.events.contains (event);
    }

    /**
     * Remove the specified behavior from the loop (if it is present)
     */
    public void removeEvent (Event event) {
        if (event != null) {
            if (this.events.contains (event)) {
                this.events.remove(event);
            }
        }
    }

    public ArrayList<Event> getEvents () {
        return this.events;
    }

    @JsonIgnore
    public Event getEvent (int index) {
        if (0 < index && index < this.events.size()) {
            return this.events.get(index);
        } else {
            return null;
        }
    }
}
