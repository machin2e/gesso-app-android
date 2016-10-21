package camp.computer.clay.engine.entity;

import java.util.UUID;

import camp.computer.clay.engine.Group;

public class Path extends Entity {

    public static Group<Path> Manager = new Group<>();

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

    //private Port source;
    // TODO: (?) Entity sourcePort;
    private UUID source;

    //private Port target;
    private UUID target;

    public Path(Port sourcePort, Port targetPort) {
        this.type = Type.ELECTRONIC; // Default to ELECTRONIC
        this.direction = Direction.BOTH; // Default to BOTH
//        this.source = sourcePort;
//        this.target = targetPort;
        this.source = sourcePort.getUuid();
        this.target = targetPort.getUuid();

        // Update source Port configuration
        if (sourcePort.getDirection() == Port.Direction.NONE) {
            sourcePort.setDirection(Port.Direction.BOTH); // Default to BOTH
        }
        if (sourcePort.getType() == Port.Type.NONE) {
            sourcePort.setType(Port.Type.next(sourcePort.getType()));
        }

        // Update target Port configuration
        if (targetPort.getDirection() == Port.Direction.NONE) {
            targetPort.setDirection(Port.Direction.BOTH); // Default to BOTH
        }
        if (targetPort.getType() == Port.Type.NONE) {
            targetPort.setType(sourcePort.getType());
        }

        // Add to Manager
        if (!Manager.contains(this)) {
            Manager.add(this);
        }
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setMode(Port.Type type) {
        // Update type of Ports in Path (BUT NOT DIRECTION)
        // <FILTER>
        // TODO: Make Path.Filter
        Group<Port> ports = getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);
            port.setType(type);
        }
        // </FILTER>
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setSource(Port port) {
        this.source = port.getUuid();
    }

    public Port getSource() {
        return Port.Manager.get(source);
    }

    public void setTarget(Port target) {
        this.target = target.getUuid();
    }

    public Port getTarget() {
        return Port.Manager.get(target);
    }

    public Group<Port> getPorts() {
        Group<Port> ports = new Group<>();
        ports.add(getSource());
        ports.add(getTarget());
        return ports;
    }

    public Host getHost() {
        if (getSource().getPortable().getClass() == Host.class) {
            return (Host) getSource().getPortable();
        } else if (getTarget().getPortable().getClass() == Host.class) {
            return (Host) getTarget().getPortable();
        }
        return null;
    }

    public Extension getExtension() {
        if (getSource().getPortable().getClass() == Extension.class) {
            return (Extension) getSource().getPortable();
        } else if (getTarget().getPortable().getClass() == Extension.class) {
            return (Extension) getTarget().getPortable();
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
        if (this.source == port.getUuid() || this.target == port.getUuid()) {
            return true;
        } else {
            return false;
        }
    }
}
