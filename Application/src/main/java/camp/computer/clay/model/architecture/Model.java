package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Model} represents the build state of available and online Clay hosts and features of the
 * discovered physical environment sensed or computed based on data collected from Clay hosts.
 */
public class Model extends Feature {

    private System system = new System();

    private List<Actor> actors = new ArrayList<>();

    private List<Host> hosts = new ArrayList<>();

    private List<Extension> extensions = new ArrayList<>();

    public void addActor(Actor actor) {
        this.actors.add(actor);
    }

    public Actor getActor(int index) {
        return this.actors.get(index);
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

    public List<Port> getPorts() {
        List<Port> ports = new ArrayList<>();
        for (int i = 0; i < this.hosts.size(); i++) {
            Host host = this.hosts.get(i);
            ports.addAll(host.getPorts());
        }
        return ports;
    }

    public List<Path> getPaths() {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < this.hosts.size(); i++) {
            Host host = this.hosts.get(i);
            for (int j = 0; j < host.getPorts().size(); j++) {
                Port port = host.getPorts().get(j);
                paths.addAll(port.getPaths());
            }
        }
        return paths;
    }

    public void addExtension(Extension extension) {
        this.extensions.add(extension);
        extension.setParent(this);
    }

    public Extension getPatch(int index) {
        return this.extensions.get(index);
    }

    public List<Extension> getExtensions() {
        return this.extensions;
    }
}
