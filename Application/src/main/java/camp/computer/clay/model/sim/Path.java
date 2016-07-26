package camp.computer.clay.model.sim;

public class Path extends Model {

    // TODO: Physical dimensions (distance between boards)

    public enum Direction {

        NONE(0),
        OUTPUT(1),
        INPUT(2);

        // TODO: Change the index to a UUID?
        int index;

        Direction(int index) {
            this.index = index;
        }
    }

    public enum Type {

        NONE(0),
        ELECTRONIC(1),
        MESH(2),
        INTERNET(3),
        BLUETOOTH(4);

        // TODO: NONE, ELECTRONIC, MESH, INTERNET, BLUETOOTH
        // TODO: TCP, UDP, HTTP, HTTPS

        // TODO: Change the index to a UUID?
        int index;

        Type(int index) {
            this.index = index;
        }

        public static Type getNextType(Type currentType) {
            return Type.values()[(currentType.index + 1) % Type.values().length];
        }
    }

    private Type type = Type.NONE;
    private Direction direction = Direction.NONE;

    private Port source;
    private Port target;

    public Path(Port sourcePort, Port targetPort) {
        this.type = Type.NONE;
        this.direction = Direction.NONE;
        this.source = sourcePort;
        this.target = targetPort;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setSource(Port port) {
        this.source = port;
    }

    public Port getSource() {
        return this.source;
    }

    public void setTarget(Port target) {
        this.target = target;
    }

    public Port getTarget() {
        return this.target;
    }

    public boolean contains(Port port) {
        if (this.source == port || this.target == port) {
            return true;
        } else {
            return false;
        }
    }
}
