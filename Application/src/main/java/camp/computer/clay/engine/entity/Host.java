package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.component.TransformComponent;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.image.ImageComponent;

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
        addComponent(new ImageComponent());
    }

    // has Script/is Scriptable/ScriptableComponent (i.e., Host runs a Script)
}
