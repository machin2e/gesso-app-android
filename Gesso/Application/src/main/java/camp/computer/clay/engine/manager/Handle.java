package camp.computer.clay.engine.manager;

/**
 * "In computer programming, a handle is an abstract reference to a resource. [...] A resource
 * handle can be an opaque identifier, in which case it is often an integer number (often an array
 * index in an array or "table" that is used to manage that type of resource), or it can be a
 * pointer that allows access to further information."
 * [via https://en.wikipedia.org/wiki/Handle_(computing)]
 */
public abstract class Handle {

    private static long handleUidCount = 0L;

    public static long INVALID_UID = -1L; // UIDs less than 0 are invalid.

    public long uid = 0L;

    public Handle() {
        setup(INVALID_UID);
    }

    public Handle(long uid) {
        setup(uid);
    }

    private void setup(long uuid) {
        if (uuid < 0) {
            this.uid = handleUidCount++;
        } else {
            this.uid = uuid;
        }
    }

    public long getUid() {
        return this.uid;
    }
}
