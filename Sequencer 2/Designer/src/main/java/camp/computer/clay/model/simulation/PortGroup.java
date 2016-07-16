package camp.computer.clay.model.simulation;

import java.util.ArrayList;

public class PortGroup {

    private ArrayList<Port> ports = new ArrayList<Port>();

    public PortGroup() {
    }

    public void add (Port port) {
        this.ports.add(port);
    }

    public boolean contains (Port port) {
        return this.ports.contains(port);
    }

    public void remove (Port port) {
        if (this.ports.contains(port)) {
            this.ports.remove(port);
        }
    }

    public Port get (int index) {
        return this.ports.get(index);
    }

    /**
     * Removes all elements except those with the specified type.
     * @param type
     * @return
     */
    public PortGroup filterPort(String type) {
        for (int i = 0; ; i++) {
            if (!this.ports.get(i).getType().equals(type)) {
                this.ports.remove(i);

                if ((i + 1) == ports.size()) {
                    break;
                }
            }
        }
        return this;
    }
}
