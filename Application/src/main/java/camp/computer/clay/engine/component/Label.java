package camp.computer.clay.engine.component;

public class Label extends Component {

    protected String label = "";

    public Label() {
        super();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
