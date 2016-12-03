package camp.computer.clay.engine.component;

import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;

public class Port extends Component {

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

    // TODO: none, 5v, 3.3v, (data) I2C, SPI, (monitor) A2D, voltage, current
    public enum Type {
        NONE,
        SWITCH,
        PULSE,
        WAVE,
        POWER_REFERENCE,
        POWER_CMOS,
        POWER_TTL; // TODO: Should contain parameters for voltage (5V, 3.3V), current (constant?).

        // TODO: NONE, ELECTRONIC, MESH, INTERNET, BLUETOOTH
        // TODO: TCP, UDP, HTTP, HTTPS

        public static Type getNext(Type currentType) {
            Type[] values = Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[(currentIndex + 1) % values.length];
        }
    }
    // </COMPONENT_DATA>


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

    private Type type = Type.NONE;

    private Direction direction = Direction.NONE;
    // </COMPONENT_DATA>


    // <ABSTRACT_ENTITY_INTERFACE>
    public static int getIndex(Entity port) {
        return port.getComponent(Port.class).index;
    }

    public static void setIndex(Entity port, int index) {
        port.getComponent(Port.class).index = index;
    }

    public static Type getType(Entity port) {
        return port.getComponent(Port.class).type;
    }

    public static void setType(Entity port, Type type) {
        port.getComponent(Port.class).type = type;

        // TODO: Update all other Ports in the connected PathEntity
    }

    public static Direction getDirection(Entity port) {
        return port.getComponent(Port.class).direction;
    }

    public static void setDirection(Entity port, Direction direction) {
        port.getComponent(Port.class).direction = direction;
    }

    public static boolean hasPath(Entity port) {
        Group<Entity> paths = World.getWorld().entities.get().filterWithComponent(Path.class);
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

        Group<Entity> paths = World.getWorld().entities.get().filterWithComponent(Path.class);

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
            if (Path.getSource(path) == port || Path.getTarget(path) == port) {
                if (Path.getSource(path).getParent().hasComponent(Extension.class)) {
                    return Path.getSource(path).getParent();
                } else if (Path.getTarget(path) != null && Path.getTarget(path).getParent().hasComponent(Extension.class)) {
                    return Path.getTarget(path).getParent();
                }
            }
        }
        return null;
    }
    // </HACK>
    // </ABSTRACT_ENTITY_INTERFACE>
}
