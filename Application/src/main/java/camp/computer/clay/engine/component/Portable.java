package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.image.Visibility;

public class Portable extends Component {

    protected Group<Entity> portEntities = new Group<>();

    public Portable() {
        super();
    }

    public Group<Entity> getPorts() {
        return this.portEntities;
    }

    public void addPort(Entity portEntity) {
        if (!this.portEntities.contains(portEntity)) {
            this.portEntities.add(portEntity);
            portEntity.setParent(getEntity());
        }
    }

    public Entity getPort(int index) {
        return this.portEntities.get(index);
    }

    public Entity getPort(String label) {
        for (int i = 0; i < portEntities.size(); i++) {
            if (portEntities.get(i).getComponent(Label.class).getLabel().equals(label)) {
                return portEntities.get(i);
            }
        }
        return null;
    }


    public Group<Entity> getExtensions() {
        Group<Entity> extensionEntities = new Group<>();
        for (int i = 0; i < getPorts().size(); i++) {
            Entity portEntity = getPorts().get(i);

            Entity extensionEntity = portEntity.getComponent(Port.class).getExtension();

            if (extensionEntity != null && !extensionEntities.contains(extensionEntity)) {
                extensionEntities.add(extensionEntity);
            }

        }
        return extensionEntities;
    }

    // <EXTENSION>
    // HACK: Assumes Extension
    public Group<Entity> getHosts() {
        return getHosts(getEntity());
    }

    // HACK: Assumes Extension
    private Group<Entity> getHosts(Entity extensionEntity) {

        List<Entity> hostEntities = Entity.Manager.filterWithComponent(Host.class);

        Group<Entity> hostEntityGroup = new Group<>();
        for (int i = 0; i < hostEntities.size(); i++) {
            if (hostEntities.get(i).getComponent(Portable.class).getExtensions().contains(extensionEntity)) {
                if (!hostEntityGroup.contains(hostEntities.get(i))) {
                    hostEntityGroup.add(hostEntities.get(i));
                }
            }
        }

        return hostEntityGroup;
    }
    // </EXTENSION>



    // --- FROM PortableEntity ---


    // <PORTABLE_IMAGE>
    public List<Point> headerContactPositions = new ArrayList<>();
    // </PORTABLE_IMAGE>

    // <PORTABLE_IMAGE_HELPERS>

//    public Group<Shape> getPortShapes() {
//        Group<Entity> portEntities = getEntity().getComponent(Portable.class).getPorts();
//        return getEntity().getComponent(Image.class).getShapes(portEntities);
//    }

    // <REFACTOR>

//    // TODO: Move into PortEntity? something (inner class? custom PortShape?)
//    public boolean hasVisiblePaths(int portIndex) {
//        Group<Image> pathImages = getEntity().getComponent(Portable.class).getPort(portIndex).getComponent(Port.class).getPaths().getImages();
//        for (int i = 0; i < pathImages.size(); i++) {
//            Image pathImage = pathImages.get(i);
//            if (pathImage.isVisible()) {
//                return true;
//            }
//        }
//        return false;
//    }

    // TODO: Move into PortEntity? something (inner class? custom PortShape?)
    public Group<Image> getPathImages(Entity portEntity) {
        Group<Image> pathImages = new Group<>();
        for (int i = 0; i < portEntity.getComponent(Port.class).getPaths().size(); i++) {
            Entity pathEntity = portEntity.getComponent(Port.class).getPaths().get(i);
            Image pathImage = pathEntity.getComponent(Image.class);
            pathImages.add(pathImage);
        }
        return pathImages;
    }

    // <VISIBILITY_COMPONENT>
    // TODO: Move into PathImage
    public void setPathVisibility(Visibility visibility) {
        Group<Entity> portEntities = getEntity().getComponent(Portable.class).getPorts();
        for (int i = 0; i < portEntities.size(); i++) {
            Entity portEntity = portEntities.get(i);

            setPathVisibility(portEntity, visibility);
        }
    }

    // TODO: Move into PathImage
    // TODO: Replace with ImageGroup.filter().setImageVisibility()
    public void setPathVisibility(Entity portEntity, Visibility visibility) {
        Group<Image> pathImages = getPathImages(portEntity);
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

            // Recursively traverse Ports in descendant Paths and setValue their PathEntity image visibility
            Entity pathEntity = pathImage.getEntity();
            Entity targetPortEntity = pathEntity.getComponent(Path.class).getTarget();
            Entity targetPortableEntity = targetPortEntity.getParent();
            Image targetPortableImage = targetPortableEntity.getComponent(Image.class);
            if (targetPortableImage != getEntity().getComponent(Image.class)) { // HACK //if (targetPortableImage != this) { // HACK
                targetPortableEntity.getComponent(Portable.class).setPathVisibility(targetPortEntity, visibility);
            }
        }
    }
    // </VISIBILITY_COMPONENT>
    // </PORTABLE_IMAGE_HELPERS>


    // <EXTENSION_IMAGE_HELPERS>
    // TODO: This is an action that Clay can perform. Place this better, maybe in Clay.
    public void createProfile(final Entity extensionEntity) {
        if (!extensionEntity.getComponent(Extension.class).hasProfile()) {

            // TODO: Only call promptInputText if the extensionEntity is a draft (i.e., does not have an associated Profile)
            Application.getView().getActionPrompts().promptInputText(new Prompt.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {
                    // Create ExtensionEntity Profile
                    Profile profile = new Profile(extensionEntity);
                    profile.setLabel(text);

                    // Assign the Profile to the ExtensionEntity
                    extensionEntity.getComponent(Extension.class).setProfile(profile);

                    // Cache the new ExtensionEntity Profile
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
