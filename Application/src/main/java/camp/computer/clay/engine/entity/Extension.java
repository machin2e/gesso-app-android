package camp.computer.clay.engine.entity;

import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.space.image.HostImage;

/**
 * {@code Extension} represents a device connected to a {@code Host}.
 */
public class Extension extends Portable {

    public Extension() {
        super();
        setup();
    }

    // TODO: Remove this! Create the Extension using the static method Clay.createExtensionEntity()
    public Extension(Profile profile) {
        super(profile);
        setup();
    }

    private void setup() {
    }

    public Group<Host> getHosts() {
        return getHosts(this);
    }

    private Group<Host> getHosts(Extension extension) {

        List<Host> hosts = Entity.Manager.filterType2(Host.class);

        Group<Host> hostGroup = new Group<>();
        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getExtensions().contains(extension)) {
                if (!hostGroup.contains(hosts.get(i))) {
                    hostGroup.add(hosts.get(i));
                }
            }
        }

        return hostGroup;
    }

    @Override
    public void update() {
        ExtensionImage extensionImage = (ExtensionImage) getComponent(Image.class);
        extensionImage.update();
    }
}
