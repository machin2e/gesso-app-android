package camp.computer.clay.model;

import java.util.UUID;

public class Action {

    private UUID uuid;

    private UUID scriptUuid;

    public Action() {
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}
