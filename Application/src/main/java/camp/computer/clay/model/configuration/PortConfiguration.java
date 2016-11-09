package camp.computer.clay.model.configuration;

import java.util.UUID;

import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.entity.Entity;

public class PortConfiguration {
    private UUID uuid = null;

    private String label = "";

    private Port.Type type = Port.Type.NONE;

    private Port.Direction direction = Port.Direction.NONE;

    public PortConfiguration() {

    }

    public PortConfiguration(Entity portEntity) {
        setType(Port.getType(portEntity));
        setDirection(Port.getDirection(portEntity));
        // setLabel(portEntity.getLabel());
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
