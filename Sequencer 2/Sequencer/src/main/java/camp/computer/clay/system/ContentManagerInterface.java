package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public interface ContentManagerInterface {

    /** Retrieve or create basic behavior */
    Action getBasicBehavior (Script script);

    void removeState(State state);

    interface Callback {
        void onSuccess(Object object);
        void onFailure();
    }

    void resetDatabase();
    void writeDatabase();

    void storeDevice(Unit unit);
    void restoreDevice(UUID uuid, Callback callback);

    void storeTimeline (Timeline timeline);
    void restoreTimeline (Unit unit, UUID uuid, Callback callback);

    void storeEvent (Event event);
    boolean hasEvent (Event event);
    void removeEvent (Event event, Callback callback);

    void storeBehavior (Action action);
    void restoreBehaviors();

    void storeScript(Script script);
    void restoreScripts();

    void storeState(Event event, State state);
    void restoreState(Event event);

    Action getBehaviorComposition(ArrayList<Action> childActions);

}
