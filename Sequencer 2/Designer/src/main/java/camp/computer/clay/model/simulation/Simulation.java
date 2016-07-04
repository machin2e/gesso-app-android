package camp.computer.clay.model.simulation;

import java.util.ArrayList;

public class Simulation extends Model {

    private ArrayList<Body> bodies = new ArrayList<Body>();

    private ArrayList<Machine> machines = new ArrayList<Machine>();

    public void addMachine(Machine path) {
        this.machines.add(path);
    }

    public Machine getMachine(int index) {
        return this.machines.get(index);
    }

    public ArrayList<Machine> getMachines() {
        return this.machines;
    }

    public ArrayList<Port> getPorts() {
        ArrayList<Port> ports = new ArrayList<Port>();
        for (Machine machine: this.machines) {
            ports.addAll(machine.getPorts());
        }
        return ports;
    }

    public ArrayList<Path> getPaths() {
        ArrayList<Path> paths = new ArrayList<Path>();
        for (Machine machine: this.machines) {
            for (Port port: machine.getPorts()) {
                paths.addAll(port.getPaths());
            }
        }
        return paths;
    }

    public ArrayList<Path> getPathsByPort(Port port) {

        ArrayList<Path> systemPaths = getPaths();
        ArrayList<Path> ancestorPaths = new ArrayList<Path>();
        ArrayList<Path> descendantPaths = new ArrayList<Path>();
        ArrayList<Port> searchablePorts = new ArrayList<Port>();

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(port);

        // Search descendant paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);
            for (Path path: dequeuedPort.getPaths()) {
                descendantPaths.add(path); // Store the path
                searchablePorts.add(path.getPort(1)); // Queue the destination port in the search
            }
        }

        // Seed port queue with the specified port
        searchablePorts.clear();
        searchablePorts.add(port);

        // Search descendant paths from port
        while (searchablePorts.size() > 0) {
            Port dequeuedPort = searchablePorts.remove(0);

            // Search for direct ancestor paths from port
            for (Path path : systemPaths) {
                if (path.getPort(1) == dequeuedPort) {
                    ancestorPaths.add(path); // Store the path
                    searchablePorts.add(path.getPort(0)); // Queue the source port in the search
                }
            }
        }

        ArrayList<Path> connectedPaths = new ArrayList<Path>();
        connectedPaths.addAll(ancestorPaths);
        connectedPaths.addAll(descendantPaths);

        return connectedPaths;
    }

    public void addBody(Body body) {
        this.bodies.add(body);
    }

    public Body getBody(int index) {
        return this.bodies.get(index);
    }

    public ArrayList<Body> getBodies() {
        return this.bodies;
    }
}
