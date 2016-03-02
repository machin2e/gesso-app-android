package camp.computer.clay.system;

import java.util.UUID;

public interface ContentManagerInterface {

    interface Callback {
        void onSuccess(Object object);
        void onFailure();
    }

    void resetDatabase();

    void storeUnit (Unit unit);
    void restoreUnit (UUID uuid, Callback callback);

    void storeTimeline (Timeline timeline);
    void restoreTimeline (Unit unit, UUID uuid, Callback callback);

    boolean hasEvent (Event event);
    void storeEvent (Event event);
//    void restoreEvents (Timeline timeline);
//    void restoreEvent (Timeline timeline, UUID uuid);
    void removeEvent (Event event, Callback callback);

    void restoreBehaviors ();
//    boolean hasBehavior (Behavior behavior);
    void storeBehavior (Behavior behavior);
//    void restoreBehavior (UUID uuid, Callback callback);

    void restoreBehaviorScripts ();
    void storeBehaviorScript (BehaviorScript behaviorScript);

    // TODO: Remove these! Handle them in restoreEvent and restoreBehavior
//    boolean hasBehaviorState (BehaviorState behaviorState);
//    void storeBehaviorState (BehaviorState behaviorState); // This is handled in both storeEvent and storeBehavior
    void restoreBehaviorState (Behavior behavior);

}
