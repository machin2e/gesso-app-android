package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.model.profile.Profile;

public class PortableEntity extends Entity {

    private Profile profile = null;

    public PortableEntity() {
        super();
    }

    @Override
    public void update() {
    }

    public Profile getProfile() {
        return this.profile;
    }

    public boolean hasProfile() {
        return this.profile != null;
    }

    public void setProfile(Profile profile) {
        // Set the Profile used to configure the Extension
        this.profile = profile;

        // Create Ports to match the Profile
        for (int i = 0; i < profile.getPorts().size(); i++) {
            Port port = new Port();
            port.setIndex(i);
            port.setType(profile.getPorts().get(i).getType());
            port.setDirection(profile.getPorts().get(i).getDirection());
            getComponent(Portable.class).addPort(port);
        }
    }
}
