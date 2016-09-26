package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Port extends Entity {

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

    private List<Path> paths = new ArrayList<>();

    private Type type = Type.NONE;

    private Direction direction = Direction.NONE;

    public Portable getPortable() {
        return (Portable) getParent();
    }

    public void addPath(Path path) {
        if (!hasPath(path)) {
            this.paths.add(path);
            path.setParent(this);
        }
    }

    public void removePath(Path path) {
        if (hasPath(path)) {
            this.paths.remove(path);
        }
    }

    public List<Path> getPaths() {
        return this.getDescendantPaths(1);
    }

    public Extension getExtension() {
        List<Path> paths = this.getCompletePaths(1);
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if (path.getSource() == this || path.getTarget() == this) {
                if (path.getSource().getParent() instanceof Extension) {
                    return (Extension) path.getSource().getParent();
                } else if (path.getTarget().getParent() instanceof Extension) {
                    return (Extension) path.getTarget().getParent();
                }
            }
        }
        return null;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;

        // Recursively setValue physically connected ports to the same type
        for (int i = 0; i < this.paths.size(); i++) {
            Path path = this.paths.get(i);
            if (path.getType() == Path.Type.ELECTRONIC) {

                if (path.getSource() == this) {
                    if (path.getTarget().getType() != type) {
                        path.getTarget().setType(type);
                    }
                }

                if (path.getTarget() == this) {
                    if (path.getSource().getType() != type) {
                        path.getSource().setType(type);
                    }
                }
            }
        }
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean hasPath() {
        if (this.paths.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasPath(Path path) {
        return this.paths.contains(path);
    }

    public List<Path> getCompletePaths(int depth) {
        List<Path> connectedPaths = new ArrayList<>();
        connectedPaths.addAll(getAncestorPaths(depth));
        connectedPaths.addAll(getDescendantPaths(depth));
        return connectedPaths;
    }

    /**
     * Returns the paths connected, directly and indirectly, to the port. These paths constitute
     * the graph containing the port.
     *
     * @return List of paths in the graph containing the port.
     */
    public List<Path> getCompletePaths() {

        // TODO: Replace List<Path> with PathGroup with filters

        List<Path> connectedPaths = new ArrayList<>();
        connectedPaths.addAll(getAncestorPaths());
        connectedPaths.addAll(getDescendantPaths());
        return connectedPaths;
    }

    public List<Path> getAncestorPaths(int depth) {

        List<Path> ancestorPaths = new ArrayList<>();

        Model model = (Model) getParent().getParent();

        List<Path> paths = model.getPaths();

        // Search for direct ancestor paths from port
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if (path.getTarget() == this) {
                ancestorPaths.add(path); // Store the path

                // Recursive call to the Path's source Port
                if (depth > 1) {
                    ancestorPaths.addAll(path.getSource().getAncestorPaths(depth - 1));
                }
            }
        }
//        }

        return ancestorPaths;
    }

    public List<Path> getAncestorPaths() {

        Model model = (Model) getParent().getParent();
        List<Path> paths = model.getPaths();

        List<Path> ancestorPaths = new ArrayList<>();

        // Search for direct ancestor paths from port
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if (path.getTarget() == this) {
                ancestorPaths.add(path); // Store the path

                // Recursive call to the Path's source Port
                ancestorPaths.addAll(path.getSource().getAncestorPaths());
            }
        }

        return ancestorPaths;
    }

    public List<Path> getDescendantPaths(int depth) {

        List<Path> descendantPaths = new LinkedList<>();

        for (int i = 0; i < this.paths.size(); i++) {
            Path path = this.paths.get(i);
            descendantPaths.add(path); // Store the path

            // Recursive call to the Path's target Port
            if (depth > 1) {
                descendantPaths.addAll(path.getTarget().getDescendantPaths(depth - 1));
            }
        }

        return descendantPaths;
    }

    public List<Path> getDescendantPaths() {

        List<Path> descendantPaths = new LinkedList<>();

        for (int i = 0; i < this.paths.size(); i++) {
            Path path = this.paths.get(i);
            descendantPaths.add(path); // Store the path
            descendantPaths.addAll(path.getTarget().getDescendantPaths());
        }

        return descendantPaths;
    }

    public boolean hasAncestor(Port port) {
        List<Path> ancestorPaths = getAncestorPaths();
        for (int i = 0; i < ancestorPaths.size(); i++) {
            Path ancestorPath = ancestorPaths.get(i);
            if (ancestorPath.getSource() == port || ancestorPath.getTarget() == port) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDescendant(Port port) {
        List<Path> descendantPaths = getDescendantPaths();
        for (int i = 0; i < descendantPaths.size(); i++) {
            Path descendantPath = descendantPaths.get(i);
            if (descendantPath.getSource() == port || descendantPath.getTarget() == port) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of the ports contained in the list of paths {@code paths}.
     *
     * @param paths List of paths for which a list contained ports will be returned.
     * @return List of ports contained in the specified list of paths {@code paths}.
     */
    public Group<Port> getPorts(List<Path> paths) {
        Group<Port> ports = new Group<>();
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if (!ports.contains(path.getSource())) {
                ports.add(path.getSource());
            }
            if (!ports.contains(path.getTarget())) {
                ports.add(path.getTarget());
            }
        }
        return ports;
    }
}
