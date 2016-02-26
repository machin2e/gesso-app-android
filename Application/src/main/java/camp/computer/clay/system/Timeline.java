package camp.computer.clay.system;

//import com.fasterxml.jackson.annotation.JsonIgnore;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class Timeline {

    private UUID uuid;

    private ArrayList<Event> events = new ArrayList<Event>();

    private Unit unit = null;

    Timeline () {

    }

    public Timeline (UUID uuid) {
        this.uuid = uuid;
    }

    public Timeline(Unit unit) {
        super();

        this.uuid = UUID.randomUUID();

        this.unit = unit;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public Unit getUnit () {
        return this.unit;
    }

    // <HACK>
    public void setUnit (Unit unit) {
        this.unit = unit;
    }
    // </HACK>

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

        if (event != null) {
            getUnit().getClay().getContentManager().removeEvent(event, new ContentManagerInterface.CallbackInterface() {
                @Override
                public void onSuccess(Object object) {
                    Log.v("Content_Manager", "Deleted event from database.");

                    // TODO: Remove from cache.
                }

                @Override
                public void onFailure() {
                    // TODO: Remove from object model and cache, even if it couldn't be removed from the store.
                    // TODO: Log failure here in text file for final logging of error. This should help debugging.
                }
            });
        }
    }

    public ArrayList<Event> getEvents () {
        return this.events;
    }

    public Event getEvent (int index) {
        if (0 < index && index < this.events.size()) {
            return this.events.get(index);
        } else {
            return null;
        }
    }
}
