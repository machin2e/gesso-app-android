package camp.computer.clay.structure;

import java.util.UUID;

import camp.computer.clay.engine.manager.Handle;

public class Script extends Handle {

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
