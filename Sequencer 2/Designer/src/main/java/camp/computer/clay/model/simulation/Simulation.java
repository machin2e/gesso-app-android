package camp.computer.clay.model.simulation;

import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.model.interaction.Body;

public class Simulation extends Model {

    private ArrayList<Body> bodies = new ArrayList<Body>();

    private ArrayList<Form> forms = new ArrayList<Form>();

    public void addForm(Form path) {
        this.forms.add(path);
    }

    public Form getForm(int index) {
        return this.forms.get(index);
    }

    public ArrayList<Form> getForms() {
        return this.forms;
    }

    public ArrayList<Port> getPorts() {
        ArrayList<Port> ports = new ArrayList<Port>();
        for (Form form : this.forms) {
            ports.addAll(form.getPorts());
        }
        return ports;
    }

    public ArrayList<Path> getPaths() {
        ArrayList<Path> paths = new ArrayList<Path>();
        for (Form form : this.forms) {
            for (Port port: form.getPorts()) {
                paths.addAll(port.getPaths());
            }
        }
        return paths;
    }

//    public ArrayList<Path> getPathsByPort(Port port) {
//
//        ArrayList<Path> systemPaths = getPaths();
//        ArrayList<Path> ancestorPaths = new ArrayList<Path>();
//        ArrayList<Path> descendantPaths = new ArrayList<Path>();
//        ArrayList<Port> searchablePorts = new ArrayList<Port>();
//
//        // Seed port queue with the specified port
//        searchablePorts.clear();
//        searchablePorts.add(port);
//
//        // Search descendant paths from port
//        while (searchablePorts.size() > 0) {
//            Port dequeuedPort = searchablePorts.remove(0);
//            for (Path path: dequeuedPort.getPaths()) {
//                descendantPaths.add(path); // Store the path
//                searchablePorts.add(path.getTarget()); // Queue the target port in the search
//            }
//        }
//
//        // Seed port queue with the specified port
//        searchablePorts.clear();
//        searchablePorts.add(port);
//
//        // Search ancestor paths from port
//        while (searchablePorts.size() > 0) {
//            Port dequeuedPort = searchablePorts.remove(0);
//
//            // Search for direct ancestor paths from port
//            for (Path path : systemPaths) {
//                if (path.getTarget() == dequeuedPort) {
//                    ancestorPaths.add(path); // Store the path
//                    searchablePorts.add(path.getSource()); // Queue the source port in the search
//                }
//            }
//        }
//
//        ArrayList<Path> connectedPaths = new ArrayList<Path>();
//        connectedPaths.addAll(ancestorPaths);
//        connectedPaths.addAll(descendantPaths);
//
//        return connectedPaths;
//    }
//
//    public ArrayList<Path> getAncestorPaths(Port port) {
//
//        ArrayList<Path> systemPaths = getPaths();
//        ArrayList<Path> ancestorPaths = new ArrayList<Path>();
//        ArrayList<Port> searchablePorts = new ArrayList<Port>();
//
//        // Seed port queue with the specified port
//        searchablePorts.clear();
//        searchablePorts.add(port);
//
//        // Search ancestor paths from port
//        while (searchablePorts.size() > 0) {
//            Port dequeuedPort = searchablePorts.remove(0);
//
//            // Search for direct ancestor paths from port
//            for (Path path: systemPaths) {
//                if (path.getTarget() == dequeuedPort) {
//                    ancestorPaths.add(path); // Store the path
//                    searchablePorts.add(path.getSource()); // Queue the source port in the search
//                }
//            }
//        }
//
//        Log.v("PathProcedure", "getAncestorPaths: size = " + ancestorPaths.size());
//
//        return ancestorPaths;
//    }
//
//    public ArrayList<Path> getDescendantPathsByPort(Port port) {
//
//        ArrayList<Path> systemPaths = getPaths();
//        ArrayList<Path> descendantPaths = new ArrayList<Path>();
//        ArrayList<Port> searchablePorts = new ArrayList<Port>();
//
//        // Seed port queue with the specified port
//        searchablePorts.clear();
//        searchablePorts.add(port);
//
//        // Search descendant paths from port
//        while (searchablePorts.size() > 0) {
//            Port dequeuedPort = searchablePorts.remove(0);
//            for (Path path: dequeuedPort.getPaths()) {
//                descendantPaths.add(path); // Store the path
//                searchablePorts.add(path.getTarget()); // Queue the target port in the search
//            }
//        }
//
//        return descendantPaths;
//    }
//
//    public boolean hasAncestor(Port port, Port ancestorPort) {
//        ArrayList<Path> ancestorPaths = getAncestorPaths(port);
//        for (Path ancestorPath: ancestorPaths) {
//            if (ancestorPath.getSource() == ancestorPort || ancestorPath.getTarget() == ancestorPort) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean hasDescendant(Port port, Port descendant) {
//        ArrayList<Path> descendantPaths = getDescendantPathsByPort(port);
//        for (Path descendantPath: descendantPaths) {
//            if (descendantPath.getSource() == descendant || descendantPath.getTarget() == descendant) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public ArrayList<Port> getPortsInPaths(ArrayList<Path> paths) {
//        ArrayList<Port> ports = new ArrayList<>();
//        for (Path path: paths) {
//            if (!ports.contains(path.getSource())) {
//                ports.add(path.getSource());
//            }
//            if (!ports.contains(path.getTarget())) {
//                ports.add(path.getTarget());
//            }
//        }
//        return ports;
//    }

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
