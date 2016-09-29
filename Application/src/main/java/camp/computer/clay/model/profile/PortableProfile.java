package camp.computer.clay.model.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.model.Port;
import camp.computer.clay.model.Portable;

public class PortableProfile {
    private UUID uuid = null;

    private String label = "";

    private List<PortProfile> portProfiles = new ArrayList<>();

    public PortableProfile() {
    }

    public PortableProfile(Portable portable) {
        for (int i = 0; i < portable.getPorts().size(); i++) {
            Port port = portable.getPorts().get(i);
            PortProfile portProfile = new PortProfile(port);
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
