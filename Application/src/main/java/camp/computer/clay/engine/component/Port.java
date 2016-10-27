package camp.computer.clay.engine.component;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;

public class Port extends Component {

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

    public Port(Entity entity) {
        super(entity);
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
    protected int index = 0;

    protected Type type = Type.NONE;

    protected Direction direction = Direction.NONE;

    // <DELETE>
    public Entity getPortable() {
        return getEntity();
    }
    // </DELETE>

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
        //Group<Entity> pathEntityGroup = Entity.Manager.filterType2(PathEntity.class);
        Group<Entity> pathEntityGroup = Entity.Manager.filterWithComponent(Path.class);
        for (int i = 0; i < pathEntityGroup.size(); i++) {
            Entity pathEntity = pathEntityGroup.get(i);
            if (pathEntity.getComponent(Path.class).contains(getEntity())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPath(Entity pathEntity) {
        return Entity.Manager.contains(pathEntity.getUuid());
    }

    /**
     * Returns the paths connected, directly and indirectly, to the port. These paths constitute
     * the graph containing the port.
     *
     * @return List of paths in the graph containing the port.
     */
    public Group<Entity> getPaths() {

        //Group<Entity> pathEntityGroup = Entity.Manager.filterType2(PathEntity.class);
        Group<Entity> pathEntityGroup = Entity.Manager.filterWithComponent(Path.class);

        // TODO: Make into Filter
        Group<Entity> pathEntities = new Group<>();
        for (int i = 0; i < pathEntityGroup.size(); i++) {
            Entity pathEntity = pathEntityGroup.get(i);
            if (pathEntity.getComponent(Path.class).contains(getEntity())) {
                pathEntities.add(pathEntity);
            }
        }
        return pathEntities;
    }

    // <HACK>
    public Entity getExtension() {
        Group<Entity> pathEntities = getPaths();
        for (int i = 0; i < pathEntities.size(); i++) {
            Entity pathEntity = pathEntities.get(i);
            if (pathEntity.getComponent(Path.class).getSource() == getEntity() || pathEntity.getComponent(Path.class).getTarget() == getEntity()) {
                //if (pathEntity.getSource().getPortable().hasComponent(Extension.class)) {
                // WRONG: if (pathEntity.getSource().getComponent(Port.class).getPortable().hasComponent(Extension.class)) {
                if (pathEntity.getComponent(Path.class).getSource().getParent().hasComponent(Extension.class)) {
                    return pathEntity.getComponent(Path.class).getSource().getParent(); //return pathEntity.getSource().getComponent(Port.class).getPortable();
                } else if (pathEntity.getComponent(Path.class).getTarget().getParent().hasComponent(Extension.class)) {
                    return pathEntity.getComponent(Path.class).getTarget().getParent();
                }
            }
        }
        return null;
    }
    // </HACK>
}
