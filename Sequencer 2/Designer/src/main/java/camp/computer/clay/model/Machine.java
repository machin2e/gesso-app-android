package camp.computer.clay.model;

import java.util.ArrayList;

public class Machine extends Model {

    // has Script (i.e., Machine runs a Script)

    private ArrayList<Port> ports = new ArrayList<Port>();

    public void addPort(Port port) {
        this.ports.add(port);
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public ArrayList<Port> getPorts() {
        return this.ports;
    }
}
