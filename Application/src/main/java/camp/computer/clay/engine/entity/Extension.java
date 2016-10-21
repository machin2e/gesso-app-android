package camp.computer.clay.engine.entity;

import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.engine.component.Image;

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

    private void setup() {
        setupComponents();
    }

    private void setupComponents() {
        // TODO: InputComponent/ControllerComponent/ActorComponent
        addComponent(new Transform());
        addComponent(new Image());
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
        return getHosts(this);
    }

    private Group<Host> getHosts(Extension extension) {

        Group<Host> hostGroup = new Group<>();

        List<Host> hosts = getComponent(Image.class).getSpace().getModel().getEntities().filterType2(Host.class);

        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getExtensions().contains(extension)) {
                if (!hostGroup.contains(hosts.get(i))) {
                    hostGroup.add(hosts.get(i));
                }
            }
        }

        return hostGroup;
    }
}
