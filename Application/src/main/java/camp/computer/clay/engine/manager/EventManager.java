package camp.computer.clay.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.event.Event;

public class EventManager {

    // <TODO>
    long eventUid = 0L;

    private HashMap<String, Long> eventTypeUids = new HashMap<>();
    private HashMap<Long, ArrayList<Entity>> eventSubscribers; // then call Entity.handleEvent(Event) for each of the Event's subscribing Entities

    public long registerEventType(String eventType) {
        // TODO: Move to EventManager, use with EventQueue
        // TODO: Return -1 if the type name already exists
        if (eventTypeUids.containsKey(eventType)) {
            return eventTypeUids.get(eventType);
        } else {
            long eventTypeUid = eventUid++;
            eventTypeUids.put(eventType, eventTypeUid);
            return eventTypeUid;
        }
    }

    public long getEventType(String eventType) {
        return eventTypeUids.get(eventType);
    }

//    public boolean subscribeToEvent(String eventType) {
//        // TODO: Return false if the event name doesn't exist. True otherwise.
//        return false;
//    }

    // TODO: __DECIDE__: list of callbacks VS. [event queue/subscription] component
    public void publish_dispatchEvent(Event event) {
        /*
        long eventUid = eventTypeUids.get(event.eventUid);
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

    //    private HashMap<Event.Type, ArrayList<EventResponse>> eventHandlers = new HashMap<>();
    private HashMap<Long, ArrayList<EventResponse>> eventHandlers = new HashMap<>();

    public void schedule(Event event) {
        // TODO: Add to scheduled events so it can be fired at the appropriate time.
    }

    //    public boolean subscribe(Event.Type eventType, EventResponse<?> eventResponse) {
//    public boolean subscribe(long eventType, EventResponse<?> eventResponse) {
    public boolean subscribe(String eventType, EventResponse<?> eventResponse) {

        // <REFACTOR>
        long eventTypeUid = World.getWorld().events.getEventType(eventType);
        // </REFACTOR>

        if (!eventHandlers.containsKey(eventTypeUid)) {
            eventHandlers.put(eventTypeUid, new ArrayList());
            eventHandlers.get(eventTypeUid).add(eventResponse);
            return true;
        } else if (eventHandlers.containsKey(eventTypeUid) && !eventHandlers.get(eventTypeUid).contains(eventResponse)) {
            eventHandlers.get(eventTypeUid).add(eventResponse);
            return true;
        } else {
            return false;
        }
    }

    // TODO: public boolean unsubscribe(...)

    public void dispatch(Event event) { // previously notifySubscribers

        // Get subscribers to Event
        ArrayList<EventResponse> subscribedEventResponses = eventHandlers.get(event.getType());
        if (subscribedEventResponses != null) {
            for (int i = 0; i < subscribedEventResponses.size(); i++) {
                subscribedEventResponses.get(i).execute(event);
            }
        }
    }
}