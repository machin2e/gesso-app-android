package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public interface ContentManagerInterface {

    /** Retrieve or create basic behavior */
    Behavior getBasicBehavior (BehaviorScript behaviorScript);

    void removeState(BehaviorState behaviorState);

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

    void storeBehavior (Behavior behavior);
    void restoreBehaviors();

    void storeScript(BehaviorScript behaviorScript);
    void restoreScripts();

    void storeState(Event event, BehaviorState behaviorState);
    void restoreState(Event event);

    Behavior getBehaviorComposition(ArrayList<Behavior> childBehaviors);

}
