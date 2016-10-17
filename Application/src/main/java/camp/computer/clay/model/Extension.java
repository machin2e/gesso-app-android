package camp.computer.clay.model;

import java.util.UUID;

import camp.computer.clay.model.profile.Profile;

/**
 * {@code Extension} represents a device connected to a {@code Host}.
 */
public class Extension extends Portable {

    public Extension() {
        super();
    }

    public Extension(Profile profile) {
        super(profile);
    }

    public Group<Host> getHosts() {
//        Group<Host> hosts = new Group<>();
//        for (int i = 0; i < ports.size(); i++) {
//            Port port = ports.get(i);
//            Host host = port.getHost();
//            if (host != null) {
//                hosts.add(host);
//            }
//        }
//        return hosts;
        return getImage().getSpace().getHosts(this);
    }
}
