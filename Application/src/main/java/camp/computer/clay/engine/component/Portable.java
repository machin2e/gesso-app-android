package camp.computer.clay.engine.component;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.Port;

public class Portable extends Component {

    protected Group<Port> ports = new Group<>();

    public Portable(Entity entity) {
        super(entity);
    }

    public Group<Port> getPorts() {
        return this.ports;
    }

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            this.ports.add(port);
            port.setParent(getEntity());
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public Port getPort(String label) {
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getLabel().equals(label)) {
                return ports.get(i);
            }
        }
        return null;
    }
}
