package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.Entity;
import camp.computer.clay.model.Group;
import camp.computer.clay.model.profile.Profile;

public class Portable extends Entity {

    public static Group<Portable> Manager = new Group<>();

    private Profile profile = null;

    protected Group<Port> ports = new Group<>();

    public Portable() {
        // Add to Manager
        if (!Manager.contains(this)) {
            Manager.add(this);
        }
    }

    public Portable(Profile profile) {
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

        // Add to Manager
        if (!Manager.contains(this)) {
            Manager.add(this);
        }
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

    public Group<Port> getPorts() {
        return this.ports;
    }

    public Group<Path> getPaths() {
        Group<Path> paths = new Group<>();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);
            paths.addAll(port.getPaths());
        }
        return paths;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public boolean hasProfile() {
        return this.profile != null;
    }

    public void setProfile(Profile profile) {
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
