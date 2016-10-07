package camp.computer.clay.model;

import camp.computer.clay.model.profile.PortableProfile;
import camp.computer.clay.model.util.PathGroup;
import camp.computer.clay.model.util.PortGroup;

public class Portable extends Entity {
    private PortableProfile profile = null;

    protected PortGroup ports = new PortGroup();

    public Portable() {

    }

    public Portable(PortableProfile profile) {
        // Set the Profile used to configure the Extension
        this.profile = profile;

        // Create Ports to match the Profile
        for (int i = 0; i < profile.getPorts().size(); i++) {
            Port port = new Port();
            port.setIndex(i);
            port.setType(profile.getPorts().get(i).getType());
            port.setDirection(profile.getPorts().get(i).getDirection());
            addPort(port);
        }

        // TODO: Update the rest of the Extension to reflect the Profile!
    }

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            this.ports.add(port);
            port.setParent(this);
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public PortGroup getPorts() {
        return this.ports;
    }

    public PathGroup getPaths() {
        PathGroup paths = new PathGroup();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);
            paths.addAll(port.getForwardPaths());
        }
        return paths;
    }

    public PortableProfile getProfile() {
        return this.profile;
    }

    public boolean hasProfile() {
        return this.profile != null;
    }

    public void setProfile(PortableProfile profile) {
        this.profile = profile;
    }

    public Group<Extension> getExtensions() {
        Group<Extension> extensions = new Group<>();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            Extension extension = port.getExtension();

            if (extension != null && !extensions.contains(extension)) {
                extensions.add(extension);
            }

        }
        return extensions;
    }

    // TODO: getHeaders()/getPortNodes()
}
