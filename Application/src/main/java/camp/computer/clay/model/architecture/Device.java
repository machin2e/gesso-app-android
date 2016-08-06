package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.List;

public class Device extends Model {

    private List<Port> ports = new ArrayList<>();

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            port.setParent(this);
            this.ports.add(port);
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public List<Port> getPorts() {
        return this.ports;
    }

    public List<Path> getPaths() {
        List<Path> paths = new ArrayList<>();
        for (Port port : getPorts()) {
            paths.addAll(port.getPaths());
        }
        return paths;
    }

}
