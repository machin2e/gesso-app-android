package camp.computer.clay.model;

import camp.computer.clay.sprite.MachineSprite;

public class Path {
    public MachineSprite source; // (Input)
    public int sourcePort; // (Input) // TODO: Make ArrayList<Port> sourcePorts
    public MachineSprite destination;
    public int destinationPort; // TODO: Make ArrayList<Port> destinationPorts

    public Path() {

    }
}
