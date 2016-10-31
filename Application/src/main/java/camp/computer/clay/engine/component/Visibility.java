package camp.computer.clay.engine.component;

public class Visibility extends Component {

    public boolean isVisible = true;

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean getVisibile() {
        return this.isVisible;
    }
}
