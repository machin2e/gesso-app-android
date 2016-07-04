package camp.computer.clay.model.simulation;

import java.util.ArrayList;

import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PortSprite;

public class Path extends Model {

    private ArrayList<Port> ports = new ArrayList<Port>();

    public Path(Port sourcePort, Port destinationPort) {
        addPort(sourcePort);
        addPort(destinationPort);
    }

    public void addPort(Port port) {
        this.ports.add(port);
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public int getPortCount() {
        return this.ports.size();
    }

    public boolean contains(Port port) {
        return this.ports.contains(port);
    }
}
