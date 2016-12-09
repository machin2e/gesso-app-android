package camp.computer.clay.structure.configuration;

import java.util.UUID;

import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.util.Signal;
import camp.computer.clay.engine.entity.Entity;

public class PortConfiguration {
    private UUID uuid = null;

    private String label = "";

    private Signal.Type type = Signal.Type.NONE;

    private Signal.Direction direction = Signal.Direction.NONE;

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

    public Signal.Type getType() {
        return type;
    }

    public void setType(Signal.Type type) {
        this.type = type;
    }

    public Signal.Direction getDirection() {
        return direction;
    }

    public void setDirection(Signal.Direction direction) {
        this.direction = direction;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
