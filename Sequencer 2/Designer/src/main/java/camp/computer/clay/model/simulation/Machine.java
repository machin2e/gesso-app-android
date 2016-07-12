package camp.computer.clay.model.simulation;

import java.util.ArrayList;

public class Machine extends Model {

    // has Script (i.e., Machine runs a Script)

    private ArrayList<Port> ports = new ArrayList<Port>();
    private String nameTag;

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            port.setParent(this);
            this.ports.add(port);
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public ArrayList<Port> getPorts() {
        return this.ports;
    }

    public ArrayList<Path> getPaths() {
        ArrayList<Path> paths = new ArrayList<Path>();
        for (Port port: getPorts()) {
            paths.addAll (port.getPaths());
        }
        return paths;
    }

    // <TAG_INTERFACE>
    private ArrayList<String> tags = new ArrayList<String>();

    public ArrayList<String> getTags() {
        return this.tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public String getNameTag() {
        return getTags().get(0);
    }
    // </TAG_INTERFACE>
}
