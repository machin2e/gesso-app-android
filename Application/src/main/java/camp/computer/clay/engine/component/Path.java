package camp.computer.clay.engine.component;

import java.util.UUID;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;

public class Path extends Component {

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

    private UUID source;

    private UUID target;

    public Path() {
        super();
        setup();
    }

    private void setup() {
        this.type = Type.ELECTRONIC; // Default to ELECTRONIC
        this.direction = Direction.BOTH; // Default to BOTH

        // TODO: PathEntity.connectPath(source, destination) and do what the following constructor does... auto-configure Ports and PathEntity
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
        Group<Entity> portEntities = getPorts();
        for (int i = 0; i < portEntities.size(); i++) {
            Entity portEntity = portEntities.get(i);
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

    public void set(Entity sourcePortEntity, Entity targetPortEntity) {

        this.type = Type.ELECTRONIC; // Default to ELECTRONIC
        this.direction = Direction.BOTH; // Default to BOTH

        this.source = sourcePortEntity.getUuid();
        this.target = targetPortEntity.getUuid();

        // Update source PortEntity configuration
        if (sourcePortEntity.getComponent(Port.class).getDirection() == Port.Direction.NONE) {
            sourcePortEntity.getComponent(Port.class).setDirection(Port.Direction.BOTH); // Default to BOTH
        }
        if (sourcePortEntity.getComponent(Port.class).getType() == Port.Type.NONE) {
            sourcePortEntity.getComponent(Port.class).setType(Port.Type.getNext(sourcePortEntity.getComponent(Port.class).getType()));
        }

        // Update target PortEntity configuration
        if (targetPortEntity.getComponent(Port.class).getDirection() == Port.Direction.NONE) {
            targetPortEntity.getComponent(Port.class).setDirection(Port.Direction.BOTH); // Default to BOTH
        }
        if (targetPortEntity.getComponent(Port.class).getType() == Port.Type.NONE) {
            targetPortEntity.getComponent(Port.class).setType(sourcePortEntity.getComponent(Port.class).getType());
        }
    }

    public void setSource(Entity portEntity) {
        this.source = portEntity.getUuid();
    }

    public Entity getSource() {
        return Entity.Manager.get(source);
    }

    public void setTarget(Entity target) {
        this.target = target.getUuid();
    }

    public Entity getTarget() {
        return Entity.Manager.get(target);
    }

    public Group<Entity> getPorts() {
        Group<Entity> portEntities = new Group<>();
        portEntities.add(getSource());
        portEntities.add(getTarget());
        return portEntities;
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

    public boolean contains(Entity portEntity) {
        if (this.source == portEntity.getUuid() || this.target == portEntity.getUuid()) {
            return true;
        } else {
            return false;
        }
    }
}
