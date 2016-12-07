package camp.computer.clay.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;

import camp.computer.clay.engine.entity.Entity;

public class EventManager {

    // <TODO>
    long eventUid = 0L;

    private HashMap<String, Long> eventTypeUids;
    private HashMap<Long, ArrayList<Entity>> eventSubscribers; // then call Entity.handleEvent(Event) for each of the Event's subscribing Entities

    public long registerEventType(String eventType) {
        // TODO: Move to EventManager, use with EventQueue
        // TODO: Return -1 if the type name already exists
        return 0L;
    }

    public boolean subscribeToEvent(String eventType) {
        // TODO: Return false if the event name doesn't exist. True otherwise.
        return false;
    }

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

    private HashMap<Event.Type, ArrayList<EventHandler>> eventHandlers = new HashMap<>();

    public void schedule(Event event) {
        // TODO: Add to scheduled events so it can be fired at the appropriate time.
    }

    public boolean subscribe(Event.Type eventType, EventHandler<?> eventHandler) {
        if (!eventHandlers.containsKey(eventType)) {
            eventHandlers.put(eventType, new ArrayList());
            eventHandlers.get(eventType).add(eventHandler);
            return true;
        } else if (eventHandlers.containsKey(eventType) && !eventHandlers.get(eventType).contains(eventHandler)) {
            eventHandlers.get(eventType).add(eventHandler);
            return true;
        } else {
            return false;
        }
    }

    // TODO: public boolean unsubscribe(...)

    public void dispatch(Event event) { // previously notifySubscribers

        // Get subscribers to Event
        ArrayList<EventHandler> subscribedEventHandlers = eventHandlers.get(event.getType());
        if (subscribedEventHandlers != null) {
            for (int i = 0; i < subscribedEventHandlers.size(); i++) {
                subscribedEventHandlers.get(i).execute(event);
            }
        }
    }
}