package camp.computer.clay.engine.entity.util;

import java.util.UUID;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.entity.Entity;

public abstract class EPath {

    // TODO: Impelement these abstact classes
    // TODO: Finish wiring up Repository, Process, Action, Script, etc.

    /*
    if (entity.isType(EPath.class)) { // or EPath.matchesType(entity) // if (entity.hasComponent(Path.class)) {
        if (Path.getState(entity) != Path.State.EDITING) {
            absoluteReferenceTransform = entity.getComponent(Transform.class);
        }
    }
    */

    /*
    // <ABSTRACT_ENTITY_INTERFACE>
    public static Path.State getState(Entity path) {
        return path.getComponent(Path.class).state;
    }

    public static void setState(Entity path, Path.State state) {
        path.getComponent(Path.class).state = state;
    }

    public static Path.Type getType(Entity path) {
        return path.getComponent(Path.class).type;
    }

    public static void setType(Entity path, Path.Type type) {
        path.getComponent(Path.class).type = type;
    }

    public static Path.Mode getMode(Entity path) {
        return path.getComponent(Path.class).mode;
    }

    public static void setMode(Entity path, Path.Mode mode) {
        path.getComponent(Path.class).mode = mode;
    }

    public static Path.Direction getDirection(Entity path) {
        return path.getComponent(Path.class).direction;
    }

    public static void setDirection(Entity path, Path.Direction direction) {
        path.getComponent(Path.class).direction = direction;
    }

    public static void set(Entity path, Entity sourcePort, Entity targetPort) {

        Path pathComponent = path.getComponent(Path.class);

        pathComponent.mode = Path.Mode.ELECTRONIC; // Default to ELECTRONIC
        if (pathComponent.type == Path.Type.NONE) {
            pathComponent.type = Path.Type.getNext(pathComponent.type);
        }
        pathComponent.direction = Path.Direction.BOTH; // Default to BOTH

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
            pathComponent.sourcePortUuid = null;
        } else {
            pathComponent.sourcePortUuid = source.getUuid();
        }
    }

    public static Entity getSource(Entity path) {
        UUID sourcePortUuid = path.getComponent(Path.class).sourcePortUuid;
        return World.getWorld().Manager.getEntities().get(sourcePortUuid);
    }

    public static void setTarget(Entity path, Entity target) {
        if (target == null) {
            path.getComponent(Path.class).targetPortUuid = null;
        } else {
            path.getComponent(Path.class).targetPortUuid = target.getUuid();
        }
    }

    public static Entity getTarget(Entity path) {
        UUID targetPortUuid = path.getComponent(Path.class).targetPortUuid;
        return World.getWorld().Manager.getEntities().get(targetPortUuid);
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
    */
}
