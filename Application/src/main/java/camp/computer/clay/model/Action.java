package camp.computer.clay.model;

import camp.computer.clay.engine.manager.Handle;

public class Action extends Handle {

    // TODO: Version/Revision UUID
    // TODO: Execution pre-condition
    // TODO: Repeat setting (once, number, forever, condition)

    private String title = "";

    private String description = "";

    private long scriptUuid;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setScript(Script script) {
        scriptUuid = script.getUid();
    }

    public Script getScript() {
//        return Repository.scripts.get(scriptUuid);
//        for (int i = 0; i < Repository.scripts.size(); i++) {
//            if (Repository.scripts.get(i).getUid() == scriptUuid) {
//                return Repository.scripts.get(i);
//            }
//        }
        return null;
    }
}
