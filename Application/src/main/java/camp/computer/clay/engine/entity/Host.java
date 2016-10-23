package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.engine.component.Image;

public class Host extends Portable {

    public Host() {
        super();
        setup();
    }

    public Host(Profile profile) {
        super(profile);
        setup();
    }

    private void setup() {
        setupComponents();
    }

    private void setupComponents() {
        // TODO: InputComponent/ControllerComponent/ActorComponent
        setComponent(new Transform()); // addComponent(new Transform());
        // addComponent(new Image());
    }

    // has Script/is Scriptable/ScriptableComponent (i.e., Host runs a Script)
}
