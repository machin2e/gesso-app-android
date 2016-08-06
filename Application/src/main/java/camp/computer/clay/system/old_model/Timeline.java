package camp.computer.clay.system.old_model;

import java.util.ArrayList;
import java.util.UUID;

public class Timeline {

    private UUID uuid;

    private ArrayList<Event> events = new ArrayList<Event>();

    private Device device = null;

    public Timeline (UUID uuid) {
        this.uuid = uuid;
    }

    public Timeline(Device device) {
        super();

        this.uuid = UUID.randomUUID();

        this.device = device;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public Device getDevice() {
        return this.device;
    }

    // <HACK>
    public void setDevice(Device device) {
        this.device = device;
    }
    // </HACK>

    public void addEvent (Event event) {

        // Add behavior to the list of actions in the loop sequence
        if (!this.events.contains(event)) {
            this.events.add(event);

            // TODO: Store in remote repository
        }

    }

    public void addEvent (Event event, int index) {
        getEvents().add (index, event);
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

//        if (event != null) {
//            getPatch().getClay().getStore().removeEvent(event, new ContentManagerInterface.Callback() {
//                @Override
//                public void onSuccess(Object object) {
//                    Log.v("Content_Manager", "Deleted event from database.");
//
//                    // TODO: Remove from cache.
//                }
//
//                @Override
//                public void onFailure() {
//                    // TODO: Remove from object model and cache, even if it couldn't be removed from the store.
//                    // TODO: Log failure here in text file for final logging of error. This should help debugging.
//                }
//            });
//        }
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

    public void addEvent(int index, Event event) {
        this.events.add(index, event);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
