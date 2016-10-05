package camp.computer.clay.model.util;

import camp.computer.clay.model.Path;

public class PathGroup extends EntityGroup<Path> {

    /**
     * Returns all {@code Port}s contained in the current setAbsolute of {@code Path}s.
     *
     * @return The {@code Port}s contained in the current setAbsolute of {@code Path}s.
     */
    public PortGroup getPorts() {
        PortGroup ports = new PortGroup();
        for (int i = 0; i < this.elements.size(); i++) {
            ports.add(elements.get(i).getPorts());
        }
        return ports;
    }
}
