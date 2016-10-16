package camp.computer.clay.model;

import java.util.UUID;

import camp.computer.clay.model.profile.Profile;

public class Host extends Portable {

    public Host() {
        super();
    }

    public Host(Profile profile) {
        super(profile);
    }

    // has Script/is Scriptable (i.e., Host runs a Script)
}
