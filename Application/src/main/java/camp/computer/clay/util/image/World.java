package camp.computer.clay.util.image;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Actor;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Transform;

// TODO: DO NOT extend Image. Try to remove World class. If cannot, then consider making it an
// TODO: (...) Entity and adding a ActionListenerComponent.
public class World extends Image {

    public static final double HOST_TO_EXTENSION_SHORT_DISTANCE = 400;
    public static final double HOST_TO_EXTENSION_LONG_DISTANCE = 550;

    public static double PIXEL_PER_MILLIMETER = 6.0;

    public Visibility extensionPrototypeVisibility = Visibility.INVISIBLE;
    public Transform extensionPrototypePosition = new Transform();

    public Visibility pathPrototypeVisibility = Visibility.INVISIBLE;
    public Transform pathPrototypeSourcePosition = new Transform(0, 0);
    public Transform pathPrototypeDestinationCoordinate = new Transform(0, 0);

    private List<Actor> actors = new LinkedList<>();

    public World() {
        super();
        setup();
    }

    private void setup() {
        // <TODO: DELETE>
        World.world = this;
        // </TODO: DELETE>
    }

    // <TODO: DELETE>
    private static World world = null;

    public static World getWorld() {
        return World.world;
    }
    // </TODO: DELETE>

    public void addActor(Actor actor) {
        if (!this.actors.contains(actor)) {
            this.actors.add(actor);
        }
    }

    // <HACK>
    public Actor getActor() {
        return this.actors.get(0);
    }
    // </HACK>

    /**
     * Sorts {@code Image}s by layer.
     */
    @Override
    public void updateLayers() {

        Group<Image> images = Entity.Manager.getImages();

        for (int i = 0; i < images.size() - 1; i++) {
            for (int j = i + 1; j < images.size(); j++) {
                // Check for out-of-order pairs, and swap them
                if (images.get(i).layerIndex > images.get(j).layerIndex) {
                    Image image = images.get(i);
                    images.set(i, images.get(j));
                    images.set(j, image);
                }
            }
        }

        /*
        // TODO: Sort using this after making Group implement List
        Collections.sort(Database.arrayList, new Comparator<MyObject>() {
            @Override
            public int compare(MyObject o1, MyObject o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        */
    }

    // TODO: Use base class's addImage() so Shapes are added to super.shapes. Then add an index instead of layers?

    /**
     * Automatically determines and assigns a valid position for all {@code HostEntity} {@code Image}s.
     */
    public void adjustLayout() {

//        Group<Image> hostImages = Entity.Manager.filterType2(HostEntity.class).getImages();
        Group<Image> hostImages = Entity.Manager.filterWithComponent(Host.class).getImages();

        // Set position on grid layout
        if (hostImages.size() == 1) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(0, 0);
        } else if (hostImages.size() == 2) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(-300, 0);
            hostImages.get(1).getEntity().getComponent(Transform.class).set(300, 0);
        } else if (hostImages.size() == 5) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(-300, -600);
            hostImages.get(0).getEntity().getComponent(Transform.class).setRotation(0);
            hostImages.get(1).getEntity().getComponent(Transform.class).set(300, -600);
            hostImages.get(1).getEntity().getComponent(Transform.class).setRotation(20);
            hostImages.get(2).getEntity().getComponent(Transform.class).set(-300, 0);
            hostImages.get(2).getEntity().getComponent(Transform.class).setRotation(40);
            hostImages.get(3).getEntity().getComponent(Transform.class).set(300, 0);
            hostImages.get(3).getEntity().getComponent(Transform.class).setRotation(60);
            hostImages.get(4).getEntity().getComponent(Transform.class).set(-300, 600);
            hostImages.get(4).getEntity().getComponent(Transform.class).setRotation(80);
        }

        // TODO: Set position on "scatter" layout

        // Set rotation
        // image.setRotation(Probability.getRandomGenerator().nextInt(360));
    }

    // TODO: Remove this! First don't extend Image on Shape (this class)? Make TouchableComponent?
    public Group<Shape> getShapes() {
        Group<Shape> shapes = new Group<>();
        Group<Image> images = Entity.Manager.getImages();
        for (int i = 0; i < images.size(); i++) {
            shapes.addAll(images.get(i).getShapes());
        }
        return shapes;
    }

    public Shape getShape(Entity entity) {
        Group<Image> images = Entity.Manager.getImages();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            Shape shape = image.getShape(entity);
            if (shape != null) {
                return shape;
            }
        }
        return null;
    }


    // <EXTENSION_PROTOTYPE>
    public void setPathPrototypeVisibility(Visibility visibility) {
        pathPrototypeVisibility = visibility;
    }

    public Visibility getPathPrototypeVisibility() {
        return pathPrototypeVisibility;
    }

    public void setPathPrototypeSourcePosition(Transform position) {
        this.pathPrototypeSourcePosition.set(position);
    }

    public void setPathPrototypeDestinationPosition(Transform position) {
        this.pathPrototypeDestinationCoordinate.set(position);
    }

    public void setExtensionPrototypePosition(Transform position) {
        this.extensionPrototypePosition.set(position);
    }

    public void setExtensionPrototypeVisibility(Visibility visibility) {
        extensionPrototypeVisibility = visibility;
    }

    public Visibility getExtensionPrototypeVisibility() {
        return extensionPrototypeVisibility;
    }
    // </EXTENSION_PROTOTYPE>


    public void setPortableSeparation(double distance) {
        // <HACK>
        // TODO: Replace ASAP. This is shit.
//        Group<Image> extensionImages = Entity.Manager.filterType2(ExtensionEntity.class).getImages();
        Group<Image> extensionImages = Entity.Manager.filterWithComponent(Extension.class).getImages();
        for (int i = 0; i < extensionImages.size(); i++) {
            Image extensionImage = extensionImages.get(i);

            Entity extension = extensionImage.getEntity();
            if (extension.getComponent(Portable.class).getHosts().size() > 0) {
                Entity hostEntity = extension.getComponent(Portable.class).getHosts().get(0);
                hostEntity.getComponent(Host.class).setExtensionDistance(distance);
            }
        }
        // </HACK>
    }


    public void hideAllPorts() {
        // TODO: getEntities().filterType2(PortEntity.class).getShapes().setVisibility(Visibility.INVISIBLE);

//        Group<Image> portableImages = Entity.Manager.filterType2(HostEntity.class, ExtensionEntity.class).getImages();
//        Group<Image> portableImages = Entity.Manager.filterType2(HostEntity.class).getImages();
        Group<Image> portableImages = Entity.Manager.filterWithComponent(Host.class, Extension.class).getImages(); // HACK

//        ImageGroup portableImages = getImages(HostEntity.class, ExtensionEntity.class);
        for (int i = 0; i < portableImages.size(); i++) {
            Image portableImage = portableImages.get(i);
            Entity portableEntity = portableImage.getEntity();
//            portableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
//            portableEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.INVISIBLE);
            portableEntity.getComponent(Portable.class).getPorts().setVisibility(false);
            portableEntity.getComponent(Portable.class).setPathVisibility(false);
//            portableImage.setDockVisibility(Visibility.VISIBLE);
            portableImage.setTransparency(1.0);
        }
    }


    // <TITLE>
    // TODO: Allow user to setAbsolute and change a goal. Track it in relation to the actions taken and things built.
    protected Visibility titleVisibility = Visibility.INVISIBLE;
    protected String titleText = "Project";

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleVisibility(Visibility visibility) {
        if (titleVisibility == Visibility.INVISIBLE && visibility == Visibility.VISIBLE) {
//            Application.getView().openTitleEditor(getTitleText());
            this.titleVisibility = visibility;
        } else if (titleVisibility == Visibility.VISIBLE && visibility == Visibility.VISIBLE) {
//            Application.getView().setTitleEditor(getTitleText());
        } else if (titleVisibility == Visibility.VISIBLE && visibility == Visibility.INVISIBLE) {
//            Application.getView().closeTitleEditor();
            this.titleVisibility = visibility;
        }
    }

    public Visibility getTitleVisibility() {
        return this.titleVisibility;
    }
    // </TITLE>
}
