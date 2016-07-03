package camp.computer.clay.model.simulation;

import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PortSprite;

public class Path extends Model {
    private Machine sourceMachine; // (Input)
    private Port sourcePort; // (Input) // TODO: Make ArrayList<Port> sourcePorts
    private Machine destinationMachine;
    private Port destinationPort; // TODO: Make ArrayList<Port> destinationPorts

    public Path(
            Machine sourceMachine,
            Port sourcePort,
            Machine destinationMachine,
            Port destinationPort
            ) {
        this.sourceMachine = sourceMachine;
        this.sourcePort = sourcePort;
        this.destinationMachine = destinationMachine;
        this.destinationPort = destinationPort;
    }

    public Machine getSourceMachine() {
        return this.sourceMachine;
    }

    public Machine getDestinationMachine() {
        return this.destinationMachine;
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
