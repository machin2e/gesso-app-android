package camp.computer.clay.model.architecture;

public class Path extends Entity {

    // TODO: Physical dimensions (distance between boards)

    public enum Direction {

        NONE(0),   // source  |  destination
        OUTPUT(1), // source --> destination
        INPUT(2),  // source <-- destination
        BOTH(3);   // source <-> destination

        // TODO: Change the index to a UUID?
        int index;

        Direction(int index) {
            this.index = index;
        }
    }

    public enum Type {

        NONE(0),
        ELECTRONIC(1),
        BLUETOOTH(2),
        MESH(3),
        INTERNET(4);

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

    // TODO: public enum Protocol (i.e., BLUETOOTH, TCP, UDP, HTTP, HTTPS)

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

    public Host getHost() {
        if (getSource().getPortable().getClass() == Extension.class) {
            return (Host) getTarget().getPortable();
        } else if (getTarget().getPortable().getClass() == Extension.class) {
            return (Host) getSource().getPortable();
        }
        return null;
    }

    public Extension getExtension() {
        if (getSource().getPortable().getClass() == Host.class) {
            return (Extension) getTarget().getPortable();
        } else if (getTarget().getPortable().getClass() == Host.class) {
            return (Extension) getSource().getPortable();
        }
        return null;
    }

    public Port getHostPort() {
        if (getSource().getPortable().getClass() == Host.class) {
            return getSource();
        } else if (getTarget().getPortable().getClass() == Host.class) {
            return getTarget();
        }
        return null;
    }

    public boolean contains(Port port) {
        if (this.source == port || this.target == port) {
            return true;
        } else {
            return false;
        }
    }
}
