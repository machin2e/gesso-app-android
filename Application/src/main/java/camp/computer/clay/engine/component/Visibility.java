package camp.computer.clay.engine.component;

import camp.computer.clay.util.image.Visible;

public class Visibility extends Component {

    // TODO: Visibility(boolean defaultVisibility) constructor

    Visible visible = Visible.VISIBLE;

    public void setVisible(Visible visible) {
        this.visible = visible;
    }

    public Visible getVisibile() {
        return this.visible;
    }
}
