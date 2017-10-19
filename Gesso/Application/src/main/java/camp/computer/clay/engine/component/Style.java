package camp.computer.clay.engine.component;

public class Style extends Component {
    // TODO: Consider renaming to "Appearance"

    // <REFACTOR>
    // TODO: Replace with alpha?
    public float targetTransparency = 1.0f;
    public float transparency = targetTransparency;
    // </REFACTOR>

    public Style() {
        super();
    }
}
