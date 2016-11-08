package camp.computer.clay.engine.component;

import camp.computer.clay.engine.Group;
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

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;

        // TODO: Update all other Ports in the connected PathEntity
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean hasPath() {
        Group<Entity> paths = Entity.Manager.filterWithComponent(Path.class);
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            if (path.getComponent(Path.class).contains(getEntity())) {
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
    public Group<Entity> getPaths() {

        Group<Entity> paths = Entity.Manager.filterWithComponent(Path.class);

        // TODO: Make into Filter
        Group<Entity> portPaths = new Group<>();
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            if (path.getComponent(Path.class).contains(getEntity())) {
                portPaths.add(path);
            }
        }
        return portPaths;
    }

    // <HACK>
    public Entity getExtension() {
        Group<Entity> paths = getPaths();
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);
            Path pathComponent = path.getComponent(Path.class);
            if (pathComponent.getSource() == getEntity() || pathComponent.getTarget() == getEntity()) {
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
}
