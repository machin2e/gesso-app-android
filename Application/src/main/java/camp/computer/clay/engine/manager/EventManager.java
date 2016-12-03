package camp.computer.clay.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {

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