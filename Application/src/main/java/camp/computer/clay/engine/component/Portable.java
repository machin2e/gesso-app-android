package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.BuilderImage.Point;

public class Portable extends Component {

    protected Group<Entity> ports = new Group<>();

    public List<Point> headerContactPositions = new ArrayList<>();

    public Portable() {
        super();
    }

    public Group<Entity> getPorts() {
        return this.ports;
    }

    public void addPort(Entity port) {
        if (!this.ports.contains(port)) {
            this.ports.add(port);
            port.setParent(getEntity());
        }
    }

    public Entity getPort(int index) {
        return this.ports.get(index);
    }

    public Entity getPort(String label) {
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getComponent(Label.class).getLabel().equals(label)) {
                return ports.get(i);
            }
        }
        return null;
    }

    public Group<Entity> getExtensions() {
        Group<Entity> extensions = new Group<>();
        for (int i = 0; i < getPorts().size(); i++) {
            Entity portEntity = getPorts().get(i);

            Entity extensionEntity = portEntity.getComponent(Port.class).getExtension();

            if (extensionEntity != null && !extensions.contains(extensionEntity)) {
                extensions.add(extensionEntity);
            }

        }
        return extensions;
    }

    // <EXTENSION>
    // HACK: Assumes Extension
    public Group<Entity> getHosts() {
        return getHosts(getEntity());
    }

    // Expects Extension
    private Group<Entity> getHosts(Entity extension) {

        List<Entity> hostEntities = Entity.Manager.filterWithComponent(Host.class);

        Group<Entity> hostEntityGroup = new Group<>();
        for (int i = 0; i < hostEntities.size(); i++) {
            if (hostEntities.get(i).getComponent(Portable.class).getExtensions().contains(extension)) {
                if (!hostEntityGroup.contains(hostEntities.get(i))) {
                    hostEntityGroup.add(hostEntities.get(i));
                }
            }
        }

        return hostEntityGroup;
    }
    // </EXTENSION>

    // TODO: Make a Mapper in Group
    // Expects Group<Entity>
    // Requires components: Port
    public Group<Entity> getPaths() {
        Group<Entity> paths = new Group<>();
        for (int i = 0; i < ports.size(); i++) {
            paths.addAll(ports.get(i).getComponent(Port.class).getPaths());
        }
        return paths;
    }
}
