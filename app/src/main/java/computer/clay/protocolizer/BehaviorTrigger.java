package computer.clay.protocolizer;

public class BehaviorTrigger { // TODO: Consider renaming this to BehaviorPrecursor

    private BehaviorConstruct behaviorConstruct;

    public enum Type {
        NONE,
        SWITCH, // i.e., boolean
        THRESHOLD, // i.e., sensor
        GESTURE, // i.e., when a specified gesture is detected
        MESSAGE // i.e., when the specified message is received
    };

    private Type type = Type.NONE;

    BehaviorTrigger(BehaviorConstruct behaviorConstruct, Type type) {
        this.behaviorConstruct = behaviorConstruct;
        this.type = type;
    }

    public void setBehaviorConstruct (BehaviorConstruct behaviorConstruct) {
        this.behaviorConstruct = behaviorConstruct;
    }

    public BehaviorConstruct getBehaviorConstruct () {
        return this.behaviorConstruct;
    }

    public Type getType () {
        return this.type;
    }

    public void setType (Type type) {
        this.type = type;
    }
}
