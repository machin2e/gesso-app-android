package camp.computer.clay.model;

import java.util.UUID;

import camp.computer.clay.engine.Groupable;

public class Action extends Groupable {

    private String title = "";

    private UUID scriptUuid;

    public Action() {
        super();

        // <TEMPORARY>
//        Repository.actions.add(this);
        // </TEMPORARY>
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setScript(Script script) {
        scriptUuid = script.getUuid();
    }

    public Script getScript() {
        return Repository.scripts.get(scriptUuid);
    }
}
