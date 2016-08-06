package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.List;

public class Frame extends Model {

    // has Script (i.e., Frame runs a Script)

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
        for (Port port: getPorts()) {
            paths.addAll (port.getPaths());
        }
        return paths;
    }

    // <TAG_INTERFACE>
    private List<String> tags = new ArrayList<>();

    public List<String> getTags() {
        return this.tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public String getNameTag() {
        return getTags().get(0);
    }
    // </TAG_INTERFACE>
}
