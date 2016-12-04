package camp.computer.clay.engine.component;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.EntityManager;
import camp.computer.clay.engine.manager.Group;

public class Path extends Component {

    // <COMPONENT_DATA>
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

    public enum Mode {

        NONE(0),
        ELECTRONIC(1),
        BLUETOOTH(2),
        MESH(3),
        INTERNET(4);

        // TODO: NONE, ELECTRONIC, MESH, INTERNET, BLUETOOTH
        // TODO: TCP, UDP, HTTP, HTTPS

        // TODO: Change the index to a UUID?
        int index;

        Mode(int index) {
            this.index = index;
        }

        public static Mode getNext(Mode currentType) {
            return Mode.values()[(currentType.index + 1) % Mode.values().length];
        }
    }

    // TODO: none, 5v, 3.3v, (data) I2C, SPI, (monitor) A2D, voltage, current
    public enum Type {
        NONE,
        SWITCH,
        PULSE,
        WAVE,
        POWER_REFERENCE,
        POWER_CMOS,
        POWER_TTL; // TODO: Should contain parameters for voltage (5V, 3.3V), current (constant?).

        public static Path.Type getNext(Path.Type currentType) {
            Path.Type[] values = Path.Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[(currentIndex + 1) % values.length];
        }
    }

    // TODO: public enum Protocol (i.e., BLUETOOTH, TCP, UDP, HTTP, HTTPS)

    public Mode mode = Mode.NONE;

    public Type type = Type.NONE;

    public Direction direction = Direction.NONE;

    public long sourcePortUuid = EntityManager.INVALID_UUID;

    public long targetPortUuid = EntityManager.INVALID_UUID;
    // </COMPONENT_DATA>


    // <CONSTRUCTOR>
    public Path() {
        super();
        setup();
    }

    private void setup() {
        this.mode = Mode.ELECTRONIC;
        this.type = Type.NONE; // Default to ELECTRONIC
        this.direction = Direction.BOTH; // Default to BOTH

        // TODO: PathEntity.connectPath(sourcePortUuid, destination) and do what the following constructor does... auto-configure Ports and PathEntity
    }
    // </CONSTRUCTOR>


    // <ABSTRACT_ENTITY_INTERFACE>
    public static Type getType(Entity path) {
        return path.getComponent(Path.class).type;
    }

    public static void setType(Entity path, Type type) {
        path.getComponent(Path.class).type = type;
    }

    public static Mode getMode(Entity path) {
        return path.getComponent(Path.class).mode;
    }

    public static void setMode(Entity path, Mode mode) {
        path.getComponent(Path.class).mode = mode;
    }

    public static Direction getDirection(Entity path) {
        return path.getComponent(Path.class).direction;
    }

    public static void setDirection(Entity path, Direction direction) {
        path.getComponent(Path.class).direction = direction;
    }

    public static void set(Entity path, Entity sourcePort, Entity targetPort) {

        Path pathComponent = path.getComponent(Path.class);

        pathComponent.mode = Mode.ELECTRONIC; // Default to ELECTRONIC
        if (pathComponent.type == Type.NONE) {
            pathComponent.type = Type.getNext(pathComponent.type);
        }
        pathComponent.direction = Direction.BOTH; // Default to BOTH

        pathComponent.sourcePortUuid = sourcePort.getUuid();
        pathComponent.targetPortUuid = targetPort.getUuid();

        // Update sourcePortUuid PortEntity configuration
        if (Port.getDirection(sourcePort) == Port.Direction.NONE) {
            Port.setDirection(sourcePort, Port.Direction.BOTH); // Default to BOTH
        }
        if (Port.getType(sourcePort) == Port.Type.NONE) {
            Port.setType(sourcePort, Port.Type.getNext(Port.getType(sourcePort)));
        }

        // Update targetPortUuid PortEntity configuration
        if (Port.getDirection(targetPort) == Port.Direction.NONE) {
            Port.setDirection(targetPort, Port.Direction.BOTH); // Default to BOTH
        }
        if (Port.getType(targetPort) == Port.Type.NONE) {
            Port.setType(targetPort, Port.getType(sourcePort));
        }
    }

    public static void setSource(Entity path, Entity source) {
        Path pathComponent = path.getComponent(Path.class);
        if (source == null) {
            pathComponent.sourcePortUuid = -1;
        } else {
            pathComponent.sourcePortUuid = source.getUuid();
        }

//        // <REFACTOR_INTO_SYSTEM>
//        // Set up layout constraint
//        Entity pathSourcePort = Model.getPrimitive(path, "Source Port");
//        if (!pathSourcePort.hasComponent(TransformConstraint.class)) {
//            pathSourcePort.addComponent(new TransformConstraint());
//        }
//        pathSourcePort.getComponent(TransformConstraint.class).setReferenceEntity(source);
//        // </REFACTOR_INTO_SYSTEM>
    }

    public static Entity getSource(Entity path) {
        long sourcePortUuid = path.getComponent(Path.class).sourcePortUuid;
        return World.getWorld().entities.get(sourcePortUuid);
    }

    public static void setTarget(Entity path, Entity target) {
        if (target == null) {
            path.getComponent(Path.class).targetPortUuid = -1;
        } else {
            path.getComponent(Path.class).targetPortUuid = target.getUuid();
        }

//        // <REFACTOR_INTO_SYSTEM>
//        // Set up layout constraint
//        Entity pathTargetPort = Model.getPrimitive(path, "Target Port");
//        if (!pathTargetPort.hasComponent(TransformConstraint.class)) {
//            pathTargetPort.addComponent(new TransformConstraint());
//        }
//        pathTargetPort.getComponent(TransformConstraint.class).setReferenceEntity(target);
//        // </REFACTOR_INTO_SYSTEM>
    }

    public static Entity getTarget(Entity path) {
        long targetPortUuid = path.getComponent(Path.class).targetPortUuid;
        return World.getWorld().entities.get().get(targetPortUuid);
    }

    public static Group<Entity> getPorts(Entity path) {
        Group<Entity> ports = new Group<>();
        if (getSource(path) != null) {
            ports.add(getSource(path));
        }
        if (getTarget(path) != null) {
            ports.add(getTarget(path));
        }
        return ports;
    }

    public static Entity getHost(Entity path) {
        if (getSource(path).getParent().hasComponent(Host.class)) {
            return getSource(path).getParent();
        } else if (getTarget(path).getParent().hasComponent(Host.class)) {
            return getTarget(path).getParent();
        }
        return null;
    }

    public static Entity getExtension(Entity path) {
        if (getSource(path).getParent().hasComponent(Extension.class)) {
            return getSource(path).getParent();
        } else if (getTarget(path).getParent().hasComponent(Extension.class)) {
            return getTarget(path).getParent();
        }
        return null;
    }

    public static Entity getHostPort(Entity path) {
        if (getSource(path).getParent().hasComponent(Host.class)) {
            return getSource(path);
        } else if (getTarget(path).getParent().hasComponent(Host.class)) {
            return getTarget(path);
        }
        return null;
    }

    public static boolean contains(Entity path, Entity port) {
        Path pathComponent = path.getComponent(Path.class);
        if (pathComponent.sourcePortUuid == port.getUuid() || pathComponent.targetPortUuid == port.getUuid()) {
            return true;
        } else {
            return false;
        }
    }
    // </ABSTRACT_ENTITY_INTERFACE>
}
