package camp.computer.clay.model;

import java.util.UUID;

public class Script {

    private UUID uuid;

    private String code = "";

    public Script() {
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}
