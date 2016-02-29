package camp.computer.clay.system;

import java.util.UUID;

public interface ContentManagerInterface {

    interface CallbackInterface {
        void onSuccess(Object object);
        void onFailure();
    }

    void resetDatabase();

    void storeBehaviorScript (BehaviorScript behaviorScript);
    void restoreBehaviorScripts();
    void restoreBehaviors(); // TODO: Restore cache

    boolean hasBehavior(Behavior behavior);
    void storeBehavior(Behavior behavior);
    void restoreBehavior (UUID uuid, CallbackInterface callback);

    boolean hasBehaviorState (BehaviorState behaviorState);
    void storeBehaviorState (BehaviorState behaviorState);
    void restoreBehaviorState (Behavior behavior, UUID uuid, CallbackInterface callback);

    void storeUnit (Unit unit);
    void restoreUnit (UUID uuid, CallbackInterface callback);

    void storeTimeline (Timeline timeline);
    void restoreTimeline (Unit unit, UUID uuid, CallbackInterface callback);

    boolean hasEvent (Event event);
    void storeEvent (Event event);
    void restoreEvent (Timeline timeline, UUID uuid);
    void restoreEvents(Timeline timeline);
    void removeEvent (Event event, CallbackInterface callback);

}
