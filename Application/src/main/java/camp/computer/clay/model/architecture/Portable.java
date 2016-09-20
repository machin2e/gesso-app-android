package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.List;

public class Portable extends Entity {

    protected Group<Port> ports = new Group<>();

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            this.ports.add(port);
            port.setParent(this);
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public Group<Port> getPorts() {
        return this.ports;
    }

    public List<Path> getPaths() {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);
            paths.addAll(port.getPaths());
        }
        return paths;
    }
}
