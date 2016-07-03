package camp.computer.clay.model.simulation;

import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PortSprite;

public class Path extends Model {
    private Port sourcePort; // (Input) // TODO: Make ArrayList<Port> sourcePorts
    private Port destinationPort; // TODO: Make ArrayList<Port> destinationPorts

    public Path(
            Port sourcePort,
            Port destinationPort
            ) {
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public Port getSourcePort() {
        return this.sourcePort;
    }

    public Port getDestinationPort() {
        return this.destinationPort;
    }

    public boolean contains(Port port) {
        if (port == sourcePort || port == destinationPort) {
            return true;
        } else {
            return false;
        }
    }
}
