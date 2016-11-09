package camp.computer.clay.engine.component;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;

public class Port extends Component {

    public enum Direction {
        NONE,
        OUTPUT,
        INPUT,
        BOTH // e.g., I2C, etc.
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

        public static Type getNext(Type currentType) {
            Type[] values = Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[(currentIndex + 1) % values.length];
        }
    }

    public Port() {
        super();
    }

    /**
     * The {@code index} is a unique number that uniquely identifies the {@code PortEntity}. Concretely,
     * the {@code index} identifier is equal to the pin number defined for a particular I/O pin on
     * the physical device (if any).
     * <p>
     * The {@code index} is assumed to be zero-indexed, so the corresponding I/O pin number may be
     * offset by a value of one. (Note that this may changed to be one-indexed.)
     * <p>
     * The {@code PortEntity}'s {@code index} can be used complementary to the {@code PortEntity}'s
     * {@code label} to refer to a specific {@code PortEntity}.
     */
    private int index = 0;

    private Type type = Type.NONE;

    private Direction direction = Direction.NONE;



    // <ABSTRACT_HELPER_INTERFACE>
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
        Group<Entity> paths = World.getWorld().Manager.getEntities().filterWithComponent(Path.class);
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            if (path.getComponent(Path.class).contains(port)) {
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

        Group<Entity> paths = World.getWorld().Manager.getEntities().filterWithComponent(Path.class);

        // TODO: Make into Filter
        Group<Entity> portPaths = new Group<>();
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            if (path.getComponent(Path.class).contains(port)) {
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
            Path pathComponent = path.getComponent(Path.class);
            if (pathComponent.getSource() == port || pathComponent.getTarget() == port) {
                if (pathComponent.getSource().getParent().hasComponent(Extension.class)) {
                    return pathComponent.getSource().getParent();
                } else if (pathComponent.getTarget() != null && pathComponent.getTarget().getParent().hasComponent(Extension.class)) {
                    return pathComponent.getTarget().getParent();
                }
            }
        }
        return null;
    }
    // </HACK>
    // </ABSTRACT_HELPER_INTERFACE>
}
