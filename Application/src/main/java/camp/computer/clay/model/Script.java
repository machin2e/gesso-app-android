package camp.computer.clay.model;

import camp.computer.clay.engine.Groupable;

public class Script extends Groupable {

    private String title = "";

    private String code = "";

    public Script() {
        super();

        // <TEMPORARY>
//        Repository.scripts.add(this);
        // </TEMPORARY>
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
