package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;

public class Portable extends Component {

    // <COMPONENT_DATA>
    // TODO: Change to Group<Long>
    public Group<Entity> ports = new Group<>();

    // <DELETE>
    // TODO: Look up in the Model geometry?
    public List<Entity> headerContactGeometries = new ArrayList<>(); // TODO: replace with List<Entity> for shapes
    // </DELETE>
    // </COMPONENT_DATA>


    // <CONSTRUCTOR>
    public Portable() {
        super();
    }
    // </CONSTRUCTOR>


    // <COMPONENT_DATA>
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
            if (Label.getLabel(portableComponent.ports.get(i)).equals(label)) {
                return portableComponent.ports.get(i);
            }
        }
        return null;
    }

    public static Group<Entity> getExtensions(Entity portable) {
        Group<Entity> ports = Portable.getPorts(portable);
        Group<Entity> extensions = new Group<>();
        for (int i = 0; i < ports.size(); i++) {
            Entity extension = Port.getExtension(ports.get(i));
            if (extension != null && !extensions.contains(extension)) {
                extensions.add(extension);
            }

        }
        return extensions;
    }

    // Expects Extension
    // Requires components: Portable, Extension
    public static Group<Entity> getHosts(Entity portable) {
        List<Entity> hosts = World.getWorld().Manager.getEntities().filterWithComponent(Host.class);

        Group<Entity> portableHosts = new Group<>();
        for (int i = 0; i < hosts.size(); i++) {
            if (Portable.getExtensions(hosts.get(i)).contains(portable)) {
                if (!portableHosts.contains(hosts.get(i))) {
                    portableHosts.add(hosts.get(i));
                }
            }
        }

        return portableHosts;
    }

    // TODO: Make a Mapper in Group
    // Expects Group<Entity>
    // Requires components: Port
    public static Group<Entity> getPaths(Entity portable) {
        Portable portableComponent = portable.getComponent(Portable.class);
        Group<Entity> paths = new Group<>();
        for (int i = 0; i < portableComponent.ports.size(); i++) {
            paths.addAll(Port.getPaths(portableComponent.ports.get(i)));
        }
        return paths;
    }
    // </COMPONENT_DATA>
}
