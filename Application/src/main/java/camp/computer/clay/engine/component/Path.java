package camp.computer.clay.engine.component;

import java.util.UUID;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;

public class Path extends Component {

    public enum Direction {

        NONE(0),   // sourcePortUuid  |  destination
        OUTPUT(1), // sourcePortUuid --> destination
        INPUT(2),  // sourcePortUuid <-- destination
        BOTH(3);   // sourcePortUuid <-> destination

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

    private UUID sourcePortUuid;

    private UUID targetPortUuid;

    public Path() {
        super();
        setup();
    }

    private void setup() {
        this.type = Type.ELECTRONIC; // Default to ELECTRONIC
        this.direction = Direction.BOTH; // Default to BOTH

        // TODO: PathEntity.connectPath(sourcePortUuid, destination) and do what the following constructor does... auto-configure Ports and PathEntity
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setMode(Port.Type type) {
        // Update type of Ports in PathEntity (BUT NOT DIRECTION)
        // <FILTER>
        // TODO: Make PathEntity.Filter
        Group<Entity> ports = getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Entity portEntity = ports.get(i);
            portEntity.getComponent(Port.class).setType(type);
        }
        // </FILTER>
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void set(Entity sourcePort, Entity targetPort) {

        this.type = Type.ELECTRONIC; // Default to ELECTRONIC
        this.direction = Direction.BOTH; // Default to BOTH

        this.sourcePortUuid = sourcePort.getUuid();
        this.targetPortUuid = targetPort.getUuid();

        // Update sourcePortUuid PortEntity configuration
        if (sourcePort.getComponent(Port.class).getDirection() == Port.Direction.NONE) {
            sourcePort.getComponent(Port.class).setDirection(Port.Direction.BOTH); // Default to BOTH
        }
        if (sourcePort.getComponent(Port.class).getType() == Port.Type.NONE) {
            sourcePort.getComponent(Port.class).setType(Port.Type.getNext(sourcePort.getComponent(Port.class).getType()));
        }

        // Update targetPortUuid PortEntity configuration
        if (targetPort.getComponent(Port.class).getDirection() == Port.Direction.NONE) {
            targetPort.getComponent(Port.class).setDirection(Port.Direction.BOTH); // Default to BOTH
        }
        if (targetPort.getComponent(Port.class).getType() == Port.Type.NONE) {
            targetPort.getComponent(Port.class).setType(sourcePort.getComponent(Port.class).getType());
        }
    }

    public void setSource(Entity portEntity) {
        this.sourcePortUuid = portEntity.getUuid();
    }

    public Entity getSource() {
        return Entity.Manager.get(sourcePortUuid);
    }

    public void setTarget(Entity target) {
        this.targetPortUuid = target.getUuid();
    }

    public Entity getTarget() {
        return Entity.Manager.get(targetPortUuid);
    }

    public Group<Entity> getPorts() {
        Group<Entity> ports = new Group<>();
        ports.add(getSource());
        ports.add(getTarget());
        return ports;
    }

    public Entity getHost() {
        if (getSource().getParent().hasComponent(Host.class)) {
            return getSource().getParent();
        } else if (getTarget().getParent().hasComponent(Host.class)) {
            return getTarget().getParent();
        }
        return null;
    }

    public Entity getExtension() {
        if (getSource().getParent().hasComponent(Extension.class)) {
            return getSource().getParent();
        } else if (getTarget().getParent().hasComponent(Extension.class)) {
            return getTarget().getParent();
        }
        return null;
    }

    public Entity getHostPort() {
        if (getSource().getParent().hasComponent(Host.class)) {
            return getSource();
        } else if (getTarget().getParent().hasComponent(Host.class)) {
            return getTarget();
        }
        return null;
    }

    public boolean contains(Entity port) {
        if (this.sourcePortUuid == port.getUuid() || this.targetPortUuid == port.getUuid()) {
            return true;
        } else {
            return false;
        }
    }
}
