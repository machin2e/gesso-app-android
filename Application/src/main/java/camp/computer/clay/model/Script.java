package camp.computer.clay.model;

import java.util.UUID;

import camp.computer.clay.engine.manager.Groupable;

public class Script extends Groupable {

    private UUID parentAction;

    private String code = "";

    private String description = "";

    public Script() {
        super();

        // <TEMPORARY>
//        Repository.scripts.add(this);
        // </TEMPORARY>
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
