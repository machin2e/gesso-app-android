package camp.computer.clay.model;

import camp.computer.clay.engine.components.TransformComponent;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.image.Image;

public class Host extends Portable {

    public Host() {
        super();
    }

    public Host(Profile profile) {
        super(profile);
    }

    private void setup() {
        setupComponents();
    }

    private void setupComponents() {
        // TODO: InputComponent/ControllerComponent
        addComponent(new TransformComponent());
        addComponent(new Image());
    }

    // has Script/is Scriptable/ScriptableComponent (i.e., Host runs a Script)
}
