package camp.computer.clay.model.simulation;

import java.util.ArrayList;

public class Path extends Model {

    // <MODEL>
    public enum Direction {

        NONE(0),
        OUTPUT(1),
        INPUT(2);

        // TODO: Change the index to a UUID?
        int index;

        Direction(int index) {
            this.index = index;
        }
    }

    public enum Type {

        NONE(0),
        ELECTRONIC(1),
        MESH(2),
        INTERNET(3),
        BLUETOOTH(4);

        // TODO: NONE, ELECTRONIC, MESH, INTERNET, BLUETOOTH
        // TODO: TCP, UDP, HTTP, HTTPS

        // TODO: Change the index to a UUID?
        int index;

        Type(int index) {
            this.index = index;
        }

        public static Type getNextType(Type currentType) {
            return Type.values()[(currentType.index + 1) % Type.values().length];
        }
    }

    private Type type = Type.NONE;
    private Direction direction = Direction.NONE;

    // TODO: Physical dimensions
    // </MODEL>

    private ArrayList<Port> ports = new ArrayList<Port>();

    public Path(Port sourcePort, Port destinationPort) {
        this.type = Type.NONE;
        this.direction = Direction.NONE;
        addPort(sourcePort);
        addPort(destinationPort);
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
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
