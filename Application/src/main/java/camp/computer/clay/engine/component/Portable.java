package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.BuilderImage.Point;

public class Portable extends Component {

    protected Group<Entity> ports = new Group<>();

    public List<Point> headerContactPositions = new ArrayList<>();

    public Portable() {
        super();
    }

    // <MOVE_TO_SYSTEM?>
    public static Group<Entity> getPorts(Entity portable) {
        return portable.getComponent(Portable.class).ports;
    }

    public static void addPort(Entity portable, Entity port) {
        Portable portableComponent = portable.getComponent(Portable.class);
        if (!portableComponent.ports.contains(port)) {
            portableComponent.ports.add(port);
            port.setParent(portable);
        }
    }

    public static Entity getPort(Entity portable, int index) {
        return portable.getComponent(Portable.class).ports.get(index);
    }

    public static Entity getPort(Entity portable, String label) {
        Portable portableComponent = portable.getComponent(Portable.class);
        for (int i = 0; i < portableComponent.ports.size(); i++) {
            if (portableComponent.ports.get(i).getComponent(Label.class).getLabel().equals(label)) {
                return portableComponent.ports.get(i);
            }
        }
        return null;
    }

    public static Group<Entity> getExtensions(Entity portable) {
        Group<Entity> ports = Portable.getPorts(portable);
        Group<Entity> extensions = new Group<>();
        for (int i = 0; i < ports.size(); i++) {
            Entity extension = ports.get(i).getComponent(Port.class).getExtension();
            if (extension != null && !extensions.contains(extension)) {
                extensions.add(extension);
            }

        }
        return extensions;
    }

    // <EXTENSION>
    // HACK: Assumes Extension
    public static Group<Entity> getHosts(Entity portable) {
        List<Entity> hostEntities = World.getWorld().Manager.getEntities().filterWithComponent(Host.class);

        Group<Entity> hostEntityGroup = new Group<>();
        for (int i = 0; i < hostEntities.size(); i++) {
            if (Portable.getExtensions(hostEntities.get(i)).contains(portable)) {
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
    public static Group<Entity> getPaths(Entity portable) {
        Portable portableComponent = portable.getComponent(Portable.class);
        Group<Entity> paths = new Group<>();
        for (int i = 0; i < portableComponent.ports.size(); i++) {
            paths.addAll(portableComponent.ports.get(i).getComponent(Port.class).getPaths());
        }
        return paths;
    }
    // </MOVE_TO_SYSTEM?>
}
