package camp.computer.clay.engine.component;

import camp.computer.clay.engine.component.util.Visible;

public class Visibility extends Component {

    // TODO: Visibility(boolean defaultVisibility) constructor

    // TODO: 11/10/2016: protected double targetTransparency = 1.0; // Visibility
    // TODO: 11/10/2016: protected double transparency = targetTransparency;

    public Visible visible = Visible.VISIBLE;

    public void setVisible(Visible visible) {
        this.visible = visible;
    }

    public Visible getVisibile() {
        return this.visible;
    }
}
