package camp.computer.clay.engine.entity;

import camp.computer.clay.model.Group;
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
        return getImageComponent().getSpace().getHosts(this);
    }
}
