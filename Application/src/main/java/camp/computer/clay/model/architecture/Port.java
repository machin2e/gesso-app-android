package camp.computer.clay.model.architecture;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Port extends Model {

    public enum Direction {

        NONE("none"),
        OUTPUT("output"),
        INPUT("input"),
        BOTH("both"); // e.g., I2C, etc.

        // TODO: Change the index to a UUID?
        String tag;

        Direction(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return this.tag;
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
        String tag;

        Type(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return this.tag;
        }

        public static Type next(Type currentType) {
            Type[] values = Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[(currentIndex + 1) % values.length];
        }
    }

    private List<Path> paths = new ArrayList<>();

    public void addPath(Path path) {
        if (!hasPath(path)) {
            path.setParent(this);
            this.paths.add(path);
        }
    }

    public void removePath(Path path) {
        if (hasPath(path)) {
            this.paths.remove(path);
        }
    }

    public Path getPath(int index) {
        return this.paths.get(index);
    }

    public List<Path> getPaths() {
        return this.paths;
    }

    private Type type = Type.NONE;
    private Direction direction = Direction.NONE;

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

    /**
     * Returns the paths connected, directly and indirectly, to the port. These paths constitute
     * the graph containing the port.
     *
     * @return List of paths in the graph containing the port.
     */
    public List<Path> getGraph() {

//        Simulation simulation = (Simulation) getParent().getParent();
//        //List<Path> paths = getPaths();
//        List<Path> paths = simulation.getPaths();
//        List<Path> ancestorPaths = new ArrayList<>();
//        List<Path> descendantPaths = new ArrayList<>();
//        List<Port> searchablePorts = new ArrayList<>();
//
//        // Seed port queue with the specified port
//        searchablePorts.clear();
//        searchablePorts.add(this);
//
//        // Search descendant paths from port
//        while (searchablePorts.size() > 0) {
//            Port dequeuedPort = searchablePorts.remove(0);
//            for (Path path : dequeuedPort.getPaths()) {
//                descendantPaths.add(path); // Store the path
//                searchablePorts.add(path.getTarget()); // Queue the target port in the search
//            }
//        }
//
//        // Seed port queue with the specified port
//        searchablePorts.clear();
//        searchablePorts.add(this);
//
//        // Search ancestor paths from port
//        while (searchablePorts.size() > 0) {
//            Port dequeuedPort = searchablePorts.remove(0);
//
//            // Search for direct ancestor paths from port
//            for (Path path : paths) {
//                if (path.getTarget() == dequeuedPort) {
//                    ancestorPaths.add(path); // Store the path
//                    searchablePorts.add(path.getSource()); // Queue the source port in the search
//                }
//            }
//        }

        List<Path> connectedPaths = new ArrayList<>();
        connectedPaths.addAll(getAncestorPaths());
        connectedPaths.addAll(getDescendantPaths());
        return connectedPaths;
    }

    public List<Path> getAncestorPaths() {

        Simulation simulation = (Simulation) getParent().getParent();
        //List<Path> paths = getPaths();
        List<Path> paths = simulation.getPaths();

        List<Path> ancestorPaths = new ArrayList<>();
        List<Port> searchablePorts = new ArrayList<>();

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(this);

        // Search ancestor paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);

            // Search for direct ancestor paths from port
            for (Path path : paths) {
                if (path.getTarget() == dequeuedPort) {
                    ancestorPaths.add(path); // Store the path
                    // TODO: ancestorPaths.add(path.getSource().getDescendantPaths()) will allow to
                    // TODO: (cont'd) get complete ancestor graph.
                    searchablePorts.add(path.getSource()); // Queue the source port in the search
                }
            }
        }

        Log.v("PathProcedure", "getAncestorPaths: size = " + ancestorPaths.size());

        return ancestorPaths;
    }

    public List<Path> getDescendantPaths() {

//        Simulation simulation = (Simulation) getParent().getParent();
//        //List<Path> paths = getPaths();
//        List<Path> paths = simulation.getPaths();
        List<Path> descendantPaths = new ArrayList<>();
        List<Port> searchablePorts = new ArrayList<>();

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(this);

        // Search descendant paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);
            for (Path path : dequeuedPort.getPaths()) {
                descendantPaths.add(path); // Store the path
                searchablePorts.add(path.getTarget()); // Queue the target port in the search
            }
        }

        return descendantPaths;
    }

    public boolean hasAncestor(Port port) {
        List<Path> ancestorPaths = getAncestorPaths();
        for (Path ancestorPath : ancestorPaths) {
            if (ancestorPath.getSource() == port || ancestorPath.getTarget() == port) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDescendant(Port port) {
        List<Path> descendantPaths = getDescendantPaths();
        for (Path descendantPath : descendantPaths) {
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
    public List<Port> getPorts(List<Path> paths) {
        List<Port> ports = new ArrayList<>();
        for (Path path : paths) {
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
