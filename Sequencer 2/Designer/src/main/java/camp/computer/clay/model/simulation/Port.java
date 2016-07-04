package camp.computer.clay.model.simulation;

import java.util.ArrayList;

public class Port extends Model {

    private ArrayList<Path> paths = new ArrayList<Path>();

    public void addPath(Path path) {
        this.paths.add(path);
    }

    public Path getPath(int index) {
        return this.paths.get(index);
    }

    public ArrayList<Path> getPaths() {
        return this.paths;
    }

    // <MODEL>
    public enum Direction {

        NONE(0),
        OUTPUT(1),
        INPUT(2),
        BOTH(3); // i.e., for I2C, etc.

        // TODO: Change the index to a UUID?
        int index;

        Direction(int index) {
            this.index = index;
        }
    }

    public enum Type {

        NONE(0),
        SWITCH(1),
        PULSE(2),
        WAVE(3);
//        POWER(4),
//        GROUND(5);

        // TODO: Change the index to a UUID?
        int index;

        Type(int index) {
            this.index = index;
        }

        public static Type getNextType(Type currentType) {
            return Type.values()[(currentType.index + 1) % Type.values().length];
        }
    }

    private Type type = Type.NONE;
    private Direction direction = Direction.NONE;

    // TODO: Physical dimensions (of actual physical object)

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean hasPaths() {
        if (this.paths.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
