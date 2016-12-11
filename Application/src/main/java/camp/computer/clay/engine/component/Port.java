package camp.computer.clay.engine.component;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.util.Signal;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;

public class Port extends Component {

    // <CONSTRUCTOR>
    public Port() {
        super();
    }
    // </CONSTRUCTOR>


    // <COMPONENT_DATA>
    /**
     * The {@code index} is a unique number that uniquely identifies the {@code Port}. Concretely,
     * the {@code index} identifier is equal to the pin number defined for a particular I/O pin on
     * the physical device (if any).
     * <p>
     * The {@code index} is assumed to be zero-indexed, so the corresponding I/O pin number may be
     * offset by a value of one. (Note that this may changed to be one-indexed.)
     * <p>
     * The {@code Port}'s {@code index} can be used complementary to the {@code Port}'s
     * {@code label} to refer to a specific {@code Port}.
     */
    private int index = 0;

    private Signal.Type type = Signal.Type.NONE;

    private Signal.Direction direction = Signal.Direction.NONE;
    // </COMPONENT_DATA>


    // <ABSTRACT_ENTITY_INTERFACE>
    public static int getIndex(Entity port) {
        return port.getComponent(Port.class).index;
    }

    public static void setIndex(Entity port, int index) {
        port.getComponent(Port.class).index = index;
    }

    public static Signal.Type getType(Entity port) {
        return port.getComponent(Port.class).type;
    }

    public static void setType(Entity port, Signal.Type type) {
        port.getComponent(Port.class).type = type;

        // TODO: Update all other Ports in the connected PathEntity
    }

    public static Signal.Direction getDirection(Entity port) {
        return port.getComponent(Port.class).direction;
    }

    public static void setDirection(Entity port, Signal.Direction direction) {
        port.getComponent(Port.class).direction = direction;
    }

    public static boolean hasPath(Entity port) {
        Group<Entity> paths = World.getInstance().entityManager.get().filterWithComponent(Path.class);
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            if (Path.contains(path, port)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the paths connected, directly and indirectly, to the port. These paths constitute
     * the graph containing the port.
     *
     * @return List of paths in the graph containing the port.
     */
    public static Group<Entity> getPaths(Entity port) {

        Group<Entity> paths = World.getInstance().entityManager.get().filterWithComponent(Path.class);

        // TODO: Make into Filter
        Group<Entity> portPaths = new Group<>();
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            if (Path.contains(path, port)) {
                portPaths.add(path);
            }
        }
        return portPaths;
    }

    // <HACK>
    public static Entity getExtension(Entity port) {
        Group<Entity> paths = Port.getPaths(port);
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            if (Path.getSourcePort(path) == port || Path.getTargetPort(path) == port) {
                Entity sourcePortable = Path.getSourcePort(path).getComponent(Structure.class).parentEntity;
                if (sourcePortable.hasComponent(Extension.class)) {
                    return sourcePortable;
                } else if (Path.getTargetPort(path) != null) {
                    Entity targetPortable = Path.getTargetPort(path).getComponent(Structure.class).parentEntity;
                    if (targetPortable.hasComponent(Extension.class)) {
                        return targetPortable;
                    }
                }
            }
        }
        return null;
    }
    // </HACK>
    // </ABSTRACT_ENTITY_INTERFACE>
}
