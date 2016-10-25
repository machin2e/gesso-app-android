package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.Extension;
import camp.computer.clay.engine.entity.Host;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.engine.entity.PortableEntity;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

public class Portable extends Component {

    protected Group<Port> ports = new Group<>();

    public Portable(Entity entity) {
        super(entity);
    }

    public Group<Port> getPorts() {
        return this.ports;
    }

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            this.ports.add(port);
            port.setParent(getEntity());
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public Port getPort(String label) {
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getLabel().equals(label)) {
                return ports.get(i);
            }
        }
        return null;
    }


    public Group<Extension> getExtensions() {
        Group<Extension> extensions = new Group<>();
        for (int i = 0; i < getPorts().size(); i++) {
            Port port = getPorts().get(i);

            Extension extension = port.getExtension();

            if (extension != null && !extensions.contains(extension)) {
                extensions.add(extension);
            }

        }
        return extensions;
    }

    // <EXTENSION>
    // HACK: Assumes Extension
    public Group<Host> getHosts() {
        return getHosts((Extension) getEntity());
    }

    private Group<Host> getHosts(Extension extension) {

        List<Host> hosts = Entity.Manager.filterType2(Host.class);

        Group<Host> hostGroup = new Group<>();
        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getComponent(Portable.class).getExtensions().contains(extension)) {
                if (!hostGroup.contains(hosts.get(i))) {
                    hostGroup.add(hosts.get(i));
                }
            }
        }

        return hostGroup;
    }
    // </EXTENSION>



    // --- FROM PortableEntity ---


    // <PORTABLE_IMAGE>
    public List<Point> headerContactPositions = new ArrayList<>();
    // </PORTABLE_IMAGE>

    // <PORTABLE_IMAGE_HELPERS>

    public ShapeGroup getPortShapes() {
        Group<Port> ports = getEntity().getComponent(Portable.class).getPorts();
        return getEntity().getComponent(Image.class).getShapes(ports);
    }

    // <REFACTOR>

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public boolean hasVisiblePaths(int portIndex) {
        Group<Image> pathImages = getEntity().getComponent(Portable.class).getPort(portIndex).getPaths().getImages();
        for (int i = 0; i < pathImages.size(); i++) {
            Image pathImage = pathImages.get(i);
            if (pathImage.isVisible()) {
                return true;
            }
        }
        return false;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public Group<Image> getPathImages(Port port) {
        Group<Image> pathImages = new Group<>();
        for (int i = 0; i < port.getPaths().size(); i++) {
            Path path = port.getPaths().get(i);
            Image pathImage = path.getComponent(Image.class);
            pathImages.add(pathImage);
        }
        return pathImages;
    }

    // <VISIBILITY_COMPONENT>
    // TODO: Move into PathImage
    public void setPathVisibility(Visibility visibility) {
        Group<Port> ports = getEntity().getComponent(Portable.class).getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setPathVisibility(port, visibility);
        }
    }

    // TODO: Move into PathImage
    // TODO: Replace with ImageGroup.filter().setImageVisibility()
    public void setPathVisibility(Port port, Visibility visibility) {
        Group<Image> pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            Image pathImage = pathImages.get(i);

            // Update visibility
            if (visibility == Visibility.VISIBLE) {
                pathImage.setVisibility(Visibility.VISIBLE);
                // pathImage.setDockVisibility(Visibility.INVISIBLE);
            } else if (visibility == Visibility.INVISIBLE) {
                pathImage.setVisibility(Visibility.INVISIBLE);
                // pathImage.setDockVisibility(Visibility.VISIBLE);
            }

            // Recursively traverse Ports in descendant Paths and setValue their Path image visibility
            Path path = (Path) pathImage.getEntity();
            Port targetPort = path.getTarget();
            PortableEntity targetPortableEntity = (PortableEntity) targetPort.getParent();
            Image targetPortableImage = targetPortableEntity.getComponent(Image.class);
            if (targetPortableImage != getEntity().getComponent(Image.class)) { // HACK //if (targetPortableImage != this) { // HACK
                targetPortableEntity.getComponent(Portable.class).setPathVisibility(targetPort, visibility);
            }
        }
    }
    // </VISIBILITY_COMPONENT>
    // </PORTABLE_IMAGE_HELPERS>


    // <EXTENSION_IMAGE_HELPERS>
    // TODO: This is an action that Clay can perform. Place this better, maybe in Clay.
    public void createProfile(final Extension extension) {
        if (!extension.hasProfile()) {

            // TODO: Only call promptInputText if the extension is a draft (i.e., does not have an associated Profile)
            Application.getView().getActionPrompts().promptInputText(new Prompt.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {
                    // Create Extension Profile
                    Profile profile = new Profile(extension);
                    profile.setLabel(text);

                    // Assign the Profile to the Extension
                    extension.setProfile(profile);

                    // Cache the new Extension Profile
                    Application.getView().getClay().getProfiles().add(profile);

                    // TODO: Persist the profile in the user's private store (either local or online)

                    // TODO: Persist the profile in the global store online
                }
            });
        } else {
            Application.getView().getActionPrompts().promptAcknowledgment(new Prompt.OnActionListener() {
                @Override
                public void onComplete(Object result) {

                }
            });
        }
    }
    // </EXTENSION_IMAGE_HELPERS>


    // ^^^ FROM PortableEntity ^^^
}
