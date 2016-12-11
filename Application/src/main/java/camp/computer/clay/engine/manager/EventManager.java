package camp.computer.clay.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.event.EventResponse;

public class EventManager {

    long eventCounter = 0L;

    private HashMap<String, Long> eventUids = new HashMap<>();

    private HashMap<Long, ArrayList<EventResponse>> eventResponses = new HashMap<>();

    /**
     * Return -1 if the type name already exists.
     */
    public long registerEvent(String eventTitle) {
        if (!eventUids.containsKey(eventTitle)) {
            long eventTypeUid = eventCounter++;
            eventUids.put(eventTitle, eventTypeUid);
        }
        return eventUids.get(eventTitle);
    }

    public long getEventUid(String eventLabel) {
        return eventUids.get(eventLabel);
    }

    //----------------------------------------------------------------------------------------------

    // <REFACTOR>
    // TODO: Move to EventQueue
    public void schedule(Event event) {
        // TODO: Add to scheduled eventManager so it can be fired at or after the scheduled time.
    }
    // <REFACTOR>

    public boolean subscribe(String eventType, EventResponse<?> eventResponse) {

        // <REFACTOR>
        long eventTypeUid = World.getInstance().eventManager.getEventUid(eventType);
        // </REFACTOR>

        if (!eventResponses.containsKey(eventTypeUid)) {
            eventResponses.put(eventTypeUid, new ArrayList());
            eventResponses.get(eventTypeUid).add(eventResponse);
            return true;
        } else if (eventResponses.containsKey(eventTypeUid) && !eventResponses.get(eventTypeUid).contains(eventResponse)) {
            eventResponses.get(eventTypeUid).add(eventResponse);
            return true;
        } else {
            return false;
        }
    }

    // TODO: public boolean unsubscribe(...)

    public void dispatch(Event event) { // previously notifySubscribers

        // Get subscribers to Event
        ArrayList<EventResponse> subscribedEventResponses = eventResponses.get(event.getType());
        if (subscribedEventResponses != null) {
            for (int i = 0; i < subscribedEventResponses.size(); i++) {
                subscribedEventResponses.get(i).execute(event);
            }
        }
    }
}