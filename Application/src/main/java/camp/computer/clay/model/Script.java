package camp.computer.clay.model;

import java.util.UUID;

public class Script {

    private UUID uuid;

    private String title = "";

    private String code = "";

    public Script() {
        uuid = UUID.randomUUID();

        // <TEMPORARY>
        Repository.scripts.add(this);
        // </TEMPORARY>
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
