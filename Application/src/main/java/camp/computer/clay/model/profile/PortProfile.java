package camp.computer.clay.model.profile;

import java.util.UUID;

import camp.computer.clay.engine.entity.Port;

public class PortProfile {
    private UUID uuid = null;

    private String label = "";

    private Port.Type type = Port.Type.NONE;

    private Port.Direction direction = Port.Direction.NONE;

    public PortProfile() {

    }

    public PortProfile(Port port) {
        setType(port.getType());
        setDirection(port.getDirection());
        // setLabel(port.getLabel());
    }

    public UUID getUuid() {
        return uuid;
    }

    public Port.Type getType() {
        return type;
    }

    public void setType(Port.Type type) {
        this.type = type;
    }

    public Port.Direction getDirection() {
        return direction;
    }

    public void setDirection(Port.Direction direction) {
        this.direction = direction;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
