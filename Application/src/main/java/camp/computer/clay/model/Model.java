package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.util.PathGroup;

/**
 * {@code Model} represents the build state of available and online Clay Hosts and Entities of the
 * discovered physical environment sensed or computed based on data collected from Clay hosts.
 */
public class Model extends Entity {

    private System system = new System();

    private List<Actor> actors = new ArrayList<>();

    private List<Host> hosts = new ArrayList<>();

    private List<Extension> extensions = new ArrayList<>();

    public void addActor(Actor actor) {
        this.actors.add(actor);
    }

    public Actor getActor(int index) {
        if (index < this.actors.size()) {
            return this.actors.get(index);
        } else {
            return null;
        }
    }

    public List<Actor> getActors() {
        return this.actors;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public System getSystem() {
        return this.system;
    }

    public void addHost(Host host) {
        this.hosts.add(host);
        host.setParent(this);
    }

    public Host getHost(int index) {
        return this.hosts.get(index);
    }

    public List<Host> getHosts() {
        return this.hosts;
    }

    public Group<Port> getPorts() {
        Group<Port> ports = new Group<>();
        for (int i = 0; i < this.hosts.size(); i++) {
            Host host = this.hosts.get(i);
            ports.add(host.getPorts());
        }
        return ports;
    }

    public PathGroup getPaths() {
        PathGroup paths = new PathGroup();
        for (int i = 0; i < this.hosts.size(); i++) {
            Host host = this.hosts.get(i);
            for (int j = 0; j < host.getPorts().size(); j++) {
                Port port = host.getPorts().get(j);
                paths.add(port.getForwardPaths());
            }
        }
        return paths;
    }

    public void addExtension(Extension extension) {
        this.extensions.add(extension);
        extension.setParent(this);
    }

    public Extension getExtension(int index) {
        return this.extensions.get(index);
    }

    public List<Extension> getExtensions() {
        return this.extensions;
    }
}
