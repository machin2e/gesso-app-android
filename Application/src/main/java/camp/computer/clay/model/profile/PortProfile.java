package camp.computer.clay.model.profile;

import java.util.UUID;

import camp.computer.clay.model.architecture.Port;

public class PortProfile
{
    private UUID uuid = null;

    private Port.Type type = Port.Type.NONE;

    private Port.Direction direction = Port.Direction.NONE;

    public PortProfile()
    {

    }

    public UUID getUuid()
    {
        return uuid;
    }

    public Port.Type getType()
    {
        return type;
    }

    public void setType(Port.Type type)
    {
        this.type = type;
    }

    public Port.Direction getDirection()
    {
        return direction;
    }

    public void setDirection(Port.Direction direction)
    {
        this.direction = direction;
    }
}
