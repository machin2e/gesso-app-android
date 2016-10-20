package camp.computer.clay.engine;

import java.util.UUID;

public abstract class Groupable {

    protected UUID uuid = null;

    public Groupable() {
        setup(null);
    }

    public Groupable(UUID uuid) {
        setup(uuid);
    }

    private void setup(UUID uuid) {
        if (uuid == null) {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = uuid;
        }
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
