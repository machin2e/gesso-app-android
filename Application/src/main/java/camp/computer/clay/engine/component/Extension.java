package camp.computer.clay.engine.component;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.image.World;

public class Extension extends Component {

    public Extension() {
        super();
    }

    private Profile profile = null;

    public Profile getProfile() {
        return this.profile;
    }

    public boolean hasProfile() {
        return this.profile != null;
    }

    public void setProfile(Profile profile) {
        // Set the Profile used to configure the ExtensionEntity
        this.profile = profile;

        // Create Ports to match the Profile
        for (int i = 0; i < profile.getPorts().size(); i++) {

            Entity portEntity = World.createEntity(Port.class);

            portEntity.getComponent(Port.class).setIndex(i);
            portEntity.getComponent(Port.class).setType(profile.getPorts().get(i).getType());
            portEntity.getComponent(Port.class).setDirection(profile.getPorts().get(i).getDirection());
            getEntity().getComponent(Portable.class).addPort(portEntity);
        }
    }

    // </FROM_EXTENSION_ENTITY>
}
