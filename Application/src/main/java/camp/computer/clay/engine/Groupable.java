package camp.computer.clay.engine;

public abstract class Groupable {

    // TODO: Add UUID back in, but don't use for frequent lookups in engine. Can do lookup by UUID for infrequent events like UI-driven.
    // TODO: protected UUID uuid = null;

    private static long count = 0L;
    public long uuid = 0L;
    public static long INVALID_UUID = -1L;

    public Groupable() {
        setup(INVALID_UUID);
    }

    public Groupable(long uuid) {
        setup(uuid);
    }

    private void setup(long uuid) {
        if (uuid < 0) {
            this.uuid = count++;
        } else {
            this.uuid = uuid;
        }
    }

    public long getUuid() {
        return this.uuid;
    }
}
