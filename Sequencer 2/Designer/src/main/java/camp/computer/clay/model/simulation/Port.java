package camp.computer.clay.model.simulation;

import android.util.Log;

import java.util.ArrayList;

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

    private ArrayList<Path> paths = new ArrayList<Path>();

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

    public Form getForm() {
        return (Form) getParent();
    }

    public Path getPath(int index) {
        return this.paths.get(index);
    }

    public ArrayList<Path> getPaths() {
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

    public ArrayList<Path> getPathsByPort() {

        ArrayList<Path> systemPaths = getPaths();
        ArrayList<Path> ancestorPaths = new ArrayList<Path>();
        ArrayList<Path> descendantPaths = new ArrayList<Path>();
        ArrayList<Port> searchablePorts = new ArrayList<Port>();

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(this);

        // Search descendant paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);
            for (Path path: dequeuedPort.getPaths()) {
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

        ArrayList<Path> connectedPaths = new ArrayList<Path>();
        connectedPaths.addAll(ancestorPaths);
        connectedPaths.addAll(descendantPaths);

        return connectedPaths;
    }

    public ArrayList<Path> getAncestorPaths() {

        ArrayList<Path> systemPaths = getPaths();
        ArrayList<Path> ancestorPaths = new ArrayList<Path>();
        ArrayList<Port> searchablePorts = new ArrayList<Port>();

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(this);

        // Search ancestor paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);

            // Search for direct ancestor paths from port
            for (Path path: systemPaths) {
                if (path.getTarget() == dequeuedPort) {
                    ancestorPaths.add(path); // Store the path
                    searchablePorts.add(path.getSource()); // Queue the source port in the search
                }
            }
        }

        Log.v("PathProcedure", "getAncestorPaths: size = " + ancestorPaths.size());

        return ancestorPaths;
    }

    public ArrayList<Path> getDescendantPathsByPort() {

        ArrayList<Path> systemPaths = getPaths();
        ArrayList<Path> descendantPaths = new ArrayList<Path>();
        ArrayList<Port> searchablePorts = new ArrayList<Port>();

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(this);

        // Search descendant paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);
            for (Path path: dequeuedPort.getPaths()) {
                descendantPaths.add(path); // Store the path
                searchablePorts.add(path.getTarget()); // Queue the target port in the search
            }
        }

        return descendantPaths;
    }

    public boolean hasAncestor(Port ancestorPort) {
        ArrayList<Path> ancestorPaths = getAncestorPaths();
        for (Path ancestorPath: ancestorPaths) {
            if (ancestorPath.getSource() == ancestorPort || ancestorPath.getTarget() == ancestorPort) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDescendant(Port descendant) {
        ArrayList<Path> descendantPaths = getDescendantPathsByPort();
        for (Path descendantPath: descendantPaths) {
            if (descendantPath.getSource() == descendant || descendantPath.getTarget() == descendant) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Port> getPortsInPaths(ArrayList<Path> paths) {
        ArrayList<Port> ports = new ArrayList<>();
        for (Path path: paths) {
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
