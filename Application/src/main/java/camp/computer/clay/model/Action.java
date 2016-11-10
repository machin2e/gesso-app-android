package camp.computer.clay.model;

import java.util.UUID;

public class Action {

    private UUID uuid;

    private UUID scriptUuid;

    public Action() {
        uuid = UUID.randomUUID();

        // <TEMPORARY>
        Repository.actions.add(this);
        // </TEMPORARY>
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setScript(Script script) {
        scriptUuid = script.getUuid();
    }

    public Script getScript(UUID uuid) {
        return null; // TODO: Return the actual Script object!
    }
}
