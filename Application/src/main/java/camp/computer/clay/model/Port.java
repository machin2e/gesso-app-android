package camp.computer.clay.model;

import camp.computer.clay.model.util.PathGroup;

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

    protected PathGroup paths = new PathGroup();

    public Portable getPortable() {
        return (Portable) getParent();
    }

    // TODO: Rename or add to exposePort(PhoneHost remoteHost) then add the "virtual" Port to the remote PhoneHost
    public void addForwardPath(Path path) {
        if (!hasForwardPath(path)) {
            this.paths.add(path);
            path.setParent(this);
        }
    }

    public void removeDescendantPath(Path path) {
        if (hasForwardPath(path)) {
            this.paths.remove(path);
        }
    }

    // TODO: Rename to getRemotePorts() // returns "virtual ports" exposed to remove devices (other Clay)
    public PathGroup getForwardPaths() { // formerly getPaths()
        return this.getDescendantPaths(1);
    }

    public Extension getExtension() {
        PathGroup paths = this.getPaths(1);
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

    public boolean hasForwardPath() {
        if (this.paths.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasForwardPath(Path path) {
        return this.paths.contains(path);
    }

    public PathGroup getPaths(int depth) {
        PathGroup connectedPaths = new PathGroup();
        connectedPaths.add(getAncestorPaths(depth));
        connectedPaths.add(getDescendantPaths(depth));
        return connectedPaths;
    }

    /**
     * Returns the paths connected, directly and indirectly, to the port. These paths constitute
     * the graph containing the port.
     *
     * @return List of paths in the graph containing the port.
     */
    public PathGroup getPaths() {
        PathGroup paths = new PathGroup();
        paths.add(getAncestorPaths());
        paths.add(getDescendantPaths());
        return paths;
    }

    public PathGroup getAncestorPaths(int depth) {

        PathGroup ancestorPaths = new PathGroup();

        Model model = (Model) getParent().getParent();

        PathGroup paths = model.getPaths();

        // Search for direct ancestor paths from port
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if (path.getTarget() == this) {
                ancestorPaths.add(path); // Store the path

                // Recursive call to the Path's source Port
                if (depth > 1) {
                    ancestorPaths.add(path.getSource().getAncestorPaths(depth - 1));
                }
            }
        }
//        }

        return ancestorPaths;
    }

    public PathGroup getAncestorPaths() {

        Model model = (Model) getParent().getParent();
        PathGroup paths = model.getPaths();

        PathGroup ancestorPaths = new PathGroup();

        // Search for direct ancestor paths from port
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if (path.getTarget() == this) {
                ancestorPaths.add(path); // Store the path

                // Recursive call to the Path's source Port
                ancestorPaths.add(path.getSource().getAncestorPaths());
            }
        }

        return ancestorPaths;
    }

    public PathGroup getDescendantPaths(int depth) {

        PathGroup descendantPaths = new PathGroup();

        for (int i = 0; i < this.paths.size(); i++) {
            Path path = this.paths.get(i);
            descendantPaths.add(path); // Store the path

            // Recursive call to the Path's target Port
            if (depth > 1) {
                descendantPaths.add(path.getTarget().getDescendantPaths(depth - 1));
            }
        }

        return descendantPaths;
    }

    public PathGroup getDescendantPaths() {

        PathGroup descendantPaths = new PathGroup();

        for (int i = 0; i < this.paths.size(); i++) {
            Path path = this.paths.get(i);
            descendantPaths.add(path); // Store the path
            descendantPaths.add(path.getTarget().getDescendantPaths());
        }

        return descendantPaths;
    }

    public boolean hasAncestor(Port port) {
        PathGroup ancestorPaths = getAncestorPaths();
        for (int i = 0; i < ancestorPaths.size(); i++) {
            Path ancestorPath = ancestorPaths.get(i);
            if (ancestorPath.getSource() == port || ancestorPath.getTarget() == port) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDescendant(Port port) {
        PathGroup descendantPaths = getDescendantPaths();
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
//    public Group<Port> getPorts(List<Path> paths) {
//        Group<Port> ports = new Group<>();
//        for (int i = 0; i < paths.size(); i++) {
//            Path path = paths.get(i);
//            if (!ports.contains(path.getSource())) {
//                ports.add(path.getSource());
//            }
//            if (!ports.contains(path.getTarget())) {
//                ports.add(path.getTarget());
//            }
//        }
//        return ports;
//    }
}
