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
    public enum PortDirection {

        NONE(0),
        OUTPUT(1),
        INPUT(2),
        BOTH(3); // i.e., for I2C, etc.

        // TODO: Change the index to a UUID?
        int index;

        PortDirection(int index) {
            this.index = index;
        }
    }

    public enum PortType {

        NONE(0),
        SWITCH(1),
        PULSE(2),
        WAVE(3);
//        POWER(4),
//        GROUND(5);

        // TODO: Change the index to a UUID?
        int index;

        PortType(int index) {
            this.index = index;
        }

        public static PortType getNextType(PortType currentPortType) {
            return PortType.values()[(currentPortType.index + 1) % PortType.values().length];
        }
    }

    public PortType portType = PortType.NONE;
    public PortDirection portDirection = PortDirection.NONE;

    // TODO: Physical dimensions (of actual physical object)

    // TODO: Move into Port
    public PortType getType() {
        return this.portType;
    }

    // TODO: Move into Port
    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public boolean hasPaths() {
        if (this.paths.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
