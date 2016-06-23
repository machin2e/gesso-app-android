package camp.computer.clay.model;

import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PortSprite;

public class Path extends Model {
    private MachineSprite sourceMachine; // (Input)
    private PortSprite sourcePort; // (Input) // TODO: Make ArrayList<Port> sourcePorts
    private MachineSprite destinationMachine;
    private PortSprite destinationPort; // TODO: Make ArrayList<Port> destinationPorts

    public Path(
            MachineSprite sourceMachine,
            PortSprite sourcePort,
            MachineSprite destinationMachine,
            PortSprite destinationPort
            ) {
        this.sourceMachine = sourceMachine;
        this.sourcePort = sourcePort;
        this.destinationMachine = destinationMachine;
        this.destinationPort = destinationPort;
    }

    public MachineSprite getSourceMachine() {
        return this.sourceMachine;
    }

    public MachineSprite getDestinationMachine() {
        return this.destinationMachine;
    }

    public PortSprite getSourcePort() {
        return this.sourcePort;
    }

    public PortSprite getDestinationPort() {
        return this.destinationPort;
    }

    public boolean contains(PortSprite portSprite) {
        if (portSprite == sourcePort || portSprite == destinationPort) {
            return true;
        } else {
            return false;
        }
    }
}
