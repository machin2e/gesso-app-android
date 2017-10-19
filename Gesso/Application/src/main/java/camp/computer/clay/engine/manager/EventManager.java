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
    public long registerEvent(String eventTag) {
        // TODO: Optionally, include an "EventParser" that intercepts the specified event and emits a different one (for generating specific events).
        if (!eventUids.containsKey(eventTag)) {
            long eventTypeUid = eventCounter++;
            eventUids.put(eventTag, eventTypeUid);
        }
        return eventUids.get(eventTag);
    }

    // previously: subscribe(...)
    public boolean registerResponse(String eventType, EventResponse<?> eventResponse) {

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

    public long getEventUid(String eventTag) {
        return eventUids.get(eventTag);
    }

    // <REFACTOR>
    // TODO: Move to EventQueue
    public void schedule(Event event) {
        // TODO: Add to scheduled eventManager so it can be fired at or after the scheduled time.
    }
    // <REFACTOR>

    // TODO: public boolean unsubscribe(...)

    public void dispatch(Event event) { // previously notifySubscribers
        ArrayList<EventResponse> subscribedEventResponses = eventResponses.get(event.getType());
        if (subscribedEventResponses != null) {
            for (int i = 0; i < subscribedEventResponses.size(); i++) {
                subscribedEventResponses.get(i).execute(event);
            }
        }
    }
}