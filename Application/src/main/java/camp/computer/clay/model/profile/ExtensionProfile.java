package camp.computer.clay.model.profile;

import java.util.UUID;

public class ExtensionProfile {

    private UUID uuid = null;

    private String label = "";

    private int portCount = 0;

    public ExtensionProfile() {
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setPortCount(int portCount) {
        this.portCount = portCount;
    }

    public int getPortCount() {
        return this.portCount;
    }
}
