package camp.computer.clay.model;

import java.util.UUID;

import camp.computer.clay.model.profile.PortableProfile;

public class Host extends Portable {

    protected UUID uuid;

    public Host() {
        super();
    }

    public Host(PortableProfile profile) {
        super(profile);
    }

    // has Script/is Scriptable (i.e., PhoneHost runs a Script)
}
