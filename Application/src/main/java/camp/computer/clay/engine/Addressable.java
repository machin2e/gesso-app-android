package camp.computer.clay.engine;

import java.util.UUID;

public abstract class Addressable {

    protected UUID uuid = null;

    public Addressable() {
        setup(null);
    }

    public Addressable(UUID uuid) {
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
