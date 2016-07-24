package camp.computer.clay.model.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Frame extends Model {

    // TODO: Script (i.e., Frame runs a Script)

    private List<Port> ports = new ArrayList<>();

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            port.setParent(this);
            this.ports.add(port);
        }
    }

    public void addPorts(List<Port> ports) {
        for (Port port: ports) {
            if (!this.ports.contains(port)) {
                port.setParent(this);
                this.ports.add(port);
            }
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public Port getPort(UUID uuid) {
        for (Port port: ports) {
            if (port.getUuid().equals(uuid)) {
                return port;
            }
        }
        return null;
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
