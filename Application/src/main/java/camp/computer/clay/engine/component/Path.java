package camp.computer.clay.engine.component;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.util.Signal;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.EntityManager;
import camp.computer.clay.engine.manager.Group;

public class Path extends Component {

    // <COMPONENT_DATA>
//    public enum Mode {
//
//        NONE(0),
//        ELECTRONIC(1),
//        BLUETOOTH(2),
//        MESH(3),
//        INTERNET(4);
//
//        // TODO: NONE, ELECTRONIC, MESH, INTERNET, BLUETOOTH
//        // TODO: TCP, UDP, HTTP, HTTPS
//
//        // TODO: Change the index to a UUID?
//        int index;
//
//        Mode(int index) {
//            this.index = index;
//        }
//
//        public static Mode next(Mode currentType) {
//            return Mode.values()[(currentType.index + 1) % Mode.values().length];
//        }
//    }

    // TODO: public enum Protocol (i.e., BLUETOOTH, TCP, UDP, HTTP, HTTPS)

    public Signal.Mode mode = Signal.Mode.NONE;

    public Signal.Type type = Signal.Type.NONE;

    public Signal.Direction direction = Signal.Direction.NONE;

    public long sourcePortUuid = EntityManager.INVALID_UUID;

    public long targetPortUuid = EntityManager.INVALID_UUID;
    // </COMPONENT_DATA>


    // <CONSTRUCTOR>
    public Path() {
        super();
        setup();
    }

    private void setup() {
        this.mode = Signal.Mode.ELECTRONIC;
        this.type = Signal.Type.NONE; // Default to ELECTRONIC
        this.direction = Signal.Direction.BOTH; // Default to BOTH

        // TODO: PathEntity.connectPath(sourcePortUuid, destination) and do what the following constructor does... auto-configure Ports and PathEntity
    }
    // </CONSTRUCTOR>


    // <ABSTRACT_ENTITY_INTERFACE>
    public static Signal.Type getType(Entity path) {
        return path.getComponent(Path.class).type;
    }

    public static void setType(Entity path, Signal.Type type) {
        path.getComponent(Path.class).type = type;
    }

    public static Signal.Mode getMode(Entity path) {
        return path.getComponent(Path.class).mode;
    }

    public static void setMode(Entity path, Signal.Mode mode) {
        path.getComponent(Path.class).mode = mode;
    }

    public static Signal.Direction getDirection(Entity path) {
        return path.getComponent(Path.class).direction;
    }

    public static void setDirection(Entity path, Signal.Direction direction) {
        path.getComponent(Path.class).direction = direction;
    }

    public static void set(Entity path, Entity sourcePort, Entity targetPort) {

        Path pathComponent = path.getComponent(Path.class);

        pathComponent.mode = Signal.Mode.ELECTRONIC; // Default to ELECTRONIC
        if (pathComponent.type == Signal.Type.NONE) {
            pathComponent.type = Signal.Type.next(pathComponent.type);
        }
        pathComponent.direction = Signal.Direction.BOTH; // Default to BOTH

        pathComponent.sourcePortUuid = sourcePort.getUid();
        pathComponent.targetPortUuid = targetPort.getUid();

        // Update sourcePortUuid PortEntity configuration
        if (Port.getDirection(sourcePort) == Signal.Direction.NONE) {
            Port.setDirection(sourcePort, Signal.Direction.BOTH); // Default to BOTH
        }
        if (Port.getType(sourcePort) == Signal.Type.NONE) {
            Port.setType(sourcePort, Signal.Type.next(Port.getType(sourcePort)));
        }

        // Update targetPortUuid PortEntity configuration
        if (Port.getDirection(targetPort) == Signal.Direction.NONE) {
            Port.setDirection(targetPort, Signal.Direction.BOTH); // Default to BOTH
        }
        if (Port.getType(targetPort) == Signal.Type.NONE) {
            Port.setType(targetPort, Port.getType(sourcePort));
        }
    }

    public static void setSource(Entity path, Entity source) {
        Path pathComponent = path.getComponent(Path.class);
        if (source == null) {
            pathComponent.sourcePortUuid = -1;
        } else {
            pathComponent.sourcePortUuid = source.getUid();
        }

//        // <REFACTOR_INTO_SYSTEM>
//        // Set up layout constraint
//        Entity pathSourcePort = ModelBuilder.getPrimitive(path, "Source Port");
//        if (!pathSourcePort.hasComponent(TransformConstraint.class)) {
//            pathSourcePort.addComponent(new TransformConstraint());
//        }
//        pathSourcePort.getComponent(TransformConstraint.class).setReferenceEntity(source);
//        // </REFACTOR_INTO_SYSTEM>
    }

    public static Entity getSourcePort(Entity path) {
        long sourcePortUuid = path.getComponent(Path.class).sourcePortUuid;
        return World.getInstance().entityManager.get(sourcePortUuid);
    }

    public static void setTarget(Entity path, Entity target) {
        if (target == null) {
            path.getComponent(Path.class).targetPortUuid = -1;
        } else {
            path.getComponent(Path.class).targetPortUuid = target.getUid();
        }

//        // <REFACTOR_INTO_SYSTEM>
//        // Set up layout constraint
//        Entity pathTargetPort = ModelBuilder.getPrimitive(path, "Target Port");
//        if (!pathTargetPort.hasComponent(TransformConstraint.class)) {
//            pathTargetPort.addComponent(new TransformConstraint());
//        }
//        pathTargetPort.getComponent(TransformConstraint.class).setReferenceEntity(target);
//        // </REFACTOR_INTO_SYSTEM>
    }

    public static Entity getTargetPort(Entity path) {
        long targetPortUuid = path.getComponent(Path.class).targetPortUuid;
        return World.getInstance().entityManager.get().get(targetPortUuid);
    }

    public static Group<Entity> getPorts(Entity path) {
        Group<Entity> ports = new Group<>();
        if (getSourcePort(path) != null) {
            ports.add(getSourcePort(path));
        }
        if (getTargetPort(path) != null) {
            ports.add(getTargetPort(path));
        }
        return ports;
    }

//    public static Entity getHost(Entity path) {
//        if (getSourcePort(path).getComponent(Structure.class).parentEntity.hasComponent(Host.class)) {
//            return getSourcePort(path).getComponent(Structure.class).parentEntity;
//        } else if (getTargetPort(path).getComponent(Structure.class).parentEntity.hasComponent(Host.class)) {
//            return getTargetPort(path).getComponent(Structure.class).parentEntity;
//        }
//        return null;
//    }

    /**
     * Returns the {@code Extension} connected to the specified {@code path}.
     */
    public static Entity getExtension(Entity path) {
        Entity sourcePortable = getSourcePort(path).getComponent(Structure.class).parentEntity;
        Entity targetPortable = getTargetPort(path).getComponent(Structure.class).parentEntity;
        if (sourcePortable.hasComponent(Extension.class)) {
            return sourcePortable;
        } else if (targetPortable.hasComponent(Extension.class)) {
            return targetPortable;
        }
        return null;
    }

//    public static Entity getHostPort(Entity path) {
//        if (getSourcePort(path).getParent().hasComponent(Host.class)) {
//            return getSourcePort(path);
//        } else if (getTargetPort(path).getParent().hasComponent(Host.class)) {
//            return getTargetPort(path);
//        }
//        return null;
//    }

    public static boolean contains(Entity path, Entity port) {
        Path pathComponent = path.getComponent(Path.class);
        if (pathComponent.sourcePortUuid == port.getUid() || pathComponent.targetPortUuid == port.getUid()) {
            return true;
        } else {
            return false;
        }
    }
    // </ABSTRACT_ENTITY_INTERFACE>
}
