package camp.computer.clay.model.sim;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Port extends Model {

    public enum Direction {

        NONE(0),
        OUTPUT(1),
        INPUT(2),
        BOTH(3); // e.g., I2C, etc.

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
        // POWER(4),
        // GROUND(5);

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

    private List<Path> paths = new ArrayList<>();

    public Frame getFrame() {
        return (Frame) getParent();
    }

    public boolean hasPaths() {
        if (this.paths.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasPath(Path path) {
        return this.paths.contains(path);
    }

    public void addPath(Path path) {
        if (!hasPath(path)) {
            path.setParent(this);
            this.paths.add(path);
        }
    }

    public Path getPath(int index) {
        return this.paths.get(index);
    }

    /**
     * Returns a list the port's outgoing paths.
     * @return
     */
    public List<Path> getPaths() {
        return this.paths;
    }

    public void removePath(Path path) {
        if (hasPath(path)) {
            this.paths.remove(path);
        }
    }

    public List<Path> getConnectedPaths() {

        List<Path> systemPaths = getPaths();
        List<Path> ancestorPaths = new ArrayList<>();
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

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(this);

        // Search ancestor paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);

            // Search for direct ancestor paths from port
            for (Path path : systemPaths) {
                if (path.getTarget() == dequeuedPort) {
                    ancestorPaths.add(path); // Store the path
                    searchablePorts.add(path.getSource()); // Queue the source port in the search
                }
            }
        }

        List<Path> connectedPaths = new ArrayList<>();
        connectedPaths.addAll(ancestorPaths);
        connectedPaths.addAll(descendantPaths);

        return connectedPaths;
    }

    public List<Path> getAncestorPaths() {

        List<Path> systemPaths = getPaths();
        List<Path> ancestorPaths = new ArrayList<>();
        List<Port> searchablePorts = new ArrayList<>();

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(this);

        // Search ancestor paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);

            // Search for direct ancestor paths from port
            for (Path path : systemPaths) {
                if (path.getTarget() == dequeuedPort) {
                    ancestorPaths.add(path); // Store the path
                    searchablePorts.add(path.getSource()); // Queue the source port in the search
                }
            }
        }

        Log.v("PathProcedure", "getAncestorPaths: size = " + ancestorPaths.size());

        return ancestorPaths;
    }

    public List<Path> getDescendantPaths() {

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

    public boolean hasAncestorPort(Port port) {
        List<Path> ancestorPaths = getAncestorPaths();
        for (Path ancestorPath : ancestorPaths) {
            if (ancestorPath.getSource() == port || ancestorPath.getTarget() == port) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDescendantPort(Port port) {
        List<Path> descendantPaths = getDescendantPaths();
        for (Path descendantPath : descendantPaths) {
            if (descendantPath.getSource() == port || descendantPath.getTarget() == port) {
                return true;
            }
        }
        return false;
    }

    public List<Port> getPortsByPath(List<Path> paths) {
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
}
