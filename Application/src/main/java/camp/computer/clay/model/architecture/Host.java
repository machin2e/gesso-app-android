package camp.computer.clay.model.architecture;

import camp.computer.clay.model.profile.PortableProfile;

public class Host extends Portable
{
    public Host()
    {
        super();
    }

    public Host(PortableProfile profile)
    {
        super(profile);
    }

    // has Script/is Scriptable (i.e., Host runs a Script)
}
