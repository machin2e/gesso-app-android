package camp.computer.clay.model.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Entity;

public class Profile {
    private UUID uuid = null;

    private String label = "";

    private List<PortProfile> portProfiles = new ArrayList<>();

    // TODO: Add geometry endpoints or UUID to Image

    public Profile() {
    }

    public Profile(Entity portableEntity) {
        for (int i = 0; i < portableEntity.getComponent(Portable.class).getPortEntities().size(); i++) {
            Entity portEntity = portableEntity.getComponent(Portable.class).getPortEntities().get(i);
            PortProfile portProfile = new PortProfile(portEntity);
            addPort(portProfile);
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public List<PortProfile> getPorts() {
        return portProfiles;
    }

    public void addPort(PortProfile portProfile) {
        this.portProfiles.add(portProfile);
    }
}
