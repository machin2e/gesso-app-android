package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.Entity;
import camp.computer.clay.model.Group;

public class Port extends Entity {

    public static Group<Port> Manager = new Group<>();

    public enum Direction {
        NONE("none"),
        OUTPUT("output"),
        INPUT("input"),
        BOTH("both"); // e.g., I2C, etc.

        // TODO: Change the index to a UUID?
        String label;

        Direction(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }
    }

    // TODO: none, 5v, 3.3v, (data) I2C, SPI, (monitor) A2D, voltage, current
    public enum Type {
        NONE("none"),
        SWITCH("switch"),
        PULSE("pulse"),
        WAVE("wave"),
        POWER_REFERENCE("reference"),
        POWER_CMOS("+3.3v"),
        POWER_TTL("+5v"); // TODO: Should contain parameters for voltage (5V, 3.3V), current (constant?).

        // TODO: Change the index to a UUID?
        String label;

        Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

        public static Type next(Type currentType) {
            Type[] values = Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[(currentIndex + 1) % values.length];
        }
    }

    public Port() {
        // Add to Manager
        if (!Manager.contains(this)) {
            Manager.add(this);
        }
    }

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
    protected int index = 0;

    protected Type type = Type.NONE;

    protected Direction direction = Direction.NONE;

    public Portable getPortable() {
        return (Portable) getParent();
    }

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

        // TODO: Update all other Ports in the connected Path
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean hasPath() {
        for (int i = 0; i < Path.Manager.size(); i++) {
            Path path = Path.Manager.get(i);
            if (path.contains(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPath(Path path) {
        return Path.Manager.contains(path.getUuid());
    }

    /**
     * Returns the paths connected, directly and indirectly, to the port. These paths constitute
     * the graph containing the port.
     *
     * @return List of paths in the graph containing the port.
     */
    public Group<Path> getPaths() {

        // TODO: Make into Filter
        Group<Path> paths = new Group<Path>();
        for (int i = 0; i < Path.Manager.size(); i++) {
            Path path = Path.Manager.get(i);
            if (path.contains(this)) {
                paths.add(path);
            }
        }
        return paths;
    }

    // <HACK>
    public Extension getExtension() {
        Group<Path> paths = getPaths();
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if (path.getSource() == this || path.getTarget() == this) {
                if (path.getSource().getPortable() instanceof Extension) {
                    return (Extension) path.getSource().getPortable();
                } else if (path.getTarget().getPortable() instanceof Extension) {
                    return (Extension) path.getTarget().getPortable();
                }
            }
        }
        return null;
    }
    // </HACK>
}
