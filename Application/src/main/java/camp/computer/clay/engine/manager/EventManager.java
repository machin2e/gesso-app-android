package camp.computer.clay.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.event.EventResponse;

public class EventManager {

    // <TODO>
    long eventUid = 0L;

    private HashMap<String, Long> eventUids = new HashMap<>();
//    private HashMap<Long, ArrayList<Entity>> eventSubscribers; // then call Entity.handleEvent(Event) for each of the Event's subscribing Entities

    //    private HashMap<Event.Type, ArrayList<EventResponse>> eventResponses = new HashMap<>();
    private HashMap<Long, ArrayList<EventResponse>> eventResponses = new HashMap<>();

    public long registerEvent(String eventLabel) {
        // TODO: Move to EventManager, use with EventQueue
        // TODO: Return -1 if the type name already exists
        if (!eventUids.containsKey(eventLabel)) {
            long eventTypeUid = eventUid++;
            eventUids.put(eventLabel, eventTypeUid);
        }
        return eventUids.get(eventLabel);
    }

    public long getEventUid(String eventLabel) {
        return eventUids.get(eventLabel);
    }

//    public boolean subscribeToEvent(String eventType) {
//        // TODO: Return false if the event name doesn't exist. True otherwise.
//        return false;
//    }

    // TODO: __DECIDE__: list of callbacks VS. [event queue/subscription] component
    public void publish_dispatchEvent(Event event) {
        /*
        long eventUid = eventUids.get(event.eventUid);
        List<Entity> eventSubscribers = this.eventSubscribers.get(eventUid);
        for (int i = 0; i < eventSubscribers.size(); i++) {
            // TODO: Entity subscriber = eventSubscribers.get(i);
            // TODO: subscriber.getComponent(EventProcessor.class).handlers(eventUid).execute(event)
            //
            // TODO: eventSubscribers.get(i).processEvent(event)
        }
        */
    }
    // </TODO>

    //----------------------------------------------------------------------------------------------

    // <REFACTOR>
    // TODO: Move to EventQueue
    public void schedule(Event event) {
        // TODO: Add to scheduled eventManager so it can be fired at the appropriate time.
    }
    // <REFACTOR>

    //    public boolean subscribe(Event.Type eventType, EventResponse<?> eventResponse) {
//    public boolean subscribe(long eventType, EventResponse<?> eventResponse) {
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