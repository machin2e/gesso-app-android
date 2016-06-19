package camp.computer.clay.model;

import camp.computer.clay.sprite.DroneSprite;

public class Path {
    public DroneSprite source; // (Input)
    public int sourcePort; // (Input) // TODO: Make ArrayList<Port> sourcePorts
    public DroneSprite destination;
    public int destinationPort; // TODO: Make ArrayList<Port> destinationPorts

    public Path() {

    }
}
