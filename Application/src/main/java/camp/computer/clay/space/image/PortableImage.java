package camp.computer.clay.space.image;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Group;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.engine.entity.Portable;
import camp.computer.clay.util.geometry.Vertex;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

public abstract class PortableImage extends Image<Portable> {

    public List<Vertex> headerContactPositions = new ArrayList<>();

    public PortableImage(Portable portable) {
        super(portable);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void draw(Display display) {

    }

    public Portable getPortable() {
        return getEntity();
    }

    public ShapeGroup getPortShapes() {
        return getShapes(getPortable().getPorts());
    }

//    public Shape getPortShape(Port port) {
//        return getShapes().filterEntity(port).get(0);
//    }

    // <REFACTOR>

//    public ImageGroup getPathImages() {
//        ImageGroup pathImages = new ImageGroup();
//        for (int i = 0; i < getPortable().getPorts().size(); i++) {
//            pathImages.addAll(getPathImages(getPortable().getPorts().get(i)));
//        }
//        return pathImages;
//    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public Group<Image> getPathImages(int portIndex) {
        Group<Image> pathImages = new Group<>();
        Group<Path> paths = getPortable().getPort(portIndex).getPaths();
        return paths.getImages();
//        for (int i = 0; i < paths.size(); i++) {
//            Path path = paths.get(i);
//            PathImage pathImage = (PathImage) getSpace().getImages(path);
//            pathImages.add(pathImage);
//        }
//        return pathImages;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public boolean hasVisiblePaths(int portIndex) {
        Group<Image> pathImages = getPathImages(portIndex);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = (PathImage) pathImages.get(i);
            if (pathImage.isVisible()) {
                return true;
            }
        }
        return false;
    }

//    // TODO: Move into Port? something (inner class? custom PortShape?)
//    public boolean hasVisibleAncestorPaths(int portIndex) {
//        PathGroup ancestorPaths = getPortable().getPort(portIndex).getAncestorPaths();
//        for (int i = 0; i < ancestorPaths.size(); i++) {
//            Path ancestorPath = ancestorPaths.get(i);
//            PathImage pathImage = (PathImage) getSpace().getImages(ancestorPath);
//            if (pathImage.isVisible() && !pathImage.isDockVisible()) {
//                return true;
//            }
//        }
//        return false;
//    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public Group<Image> getPathImages(Port port) {
        Group<Image> pathImages = new Group<>();
        for (int i = 0; i < port.getPaths().size(); i++) {
            Path path = port.getPaths().get(i);
            PathImage pathImage = (PathImage) path.getComponent(Image.class);
            pathImages.add(pathImage);
        }
        return pathImages;
    }

    // TODO: Move into PathImage
    public void setPathVisibility(Visibility visibility) {
        Group<Port> ports = getPortable().getPorts();
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
            PathImage pathImage = (PathImage) pathImages.get(i);

            // Update visibility
            if (visibility == Visibility.VISIBLE) {
                pathImage.setVisibility(Visibility.VISIBLE);
                // pathImage.setDockVisibility(Visibility.INVISIBLE);
            } else if (visibility == Visibility.INVISIBLE) {
                pathImage.setVisibility(Visibility.INVISIBLE);
                // pathImage.setDockVisibility(Visibility.VISIBLE);
            }

            // Recursively traverse Ports in descendant Paths and setValue their Path image visibility
            Port targetPort = pathImage.getPath().getTarget();
            Portable targetPortable = (Portable) targetPort.getParent();
            PortableImage targetPortableImage = (PortableImage) targetPortable.getComponent(Image.class);
            if (targetPortableImage != this) { // HACK
                targetPortableImage.setPathVisibility(targetPort, visibility);
            }
        }
    }

//    // TODO: Move into PathImage
//    public void setDockVisibility(Visibility visibility) {
//        Group<Port> ports = getPortable().getPorts();
//        for (int i = 0; i < ports.size(); i++) {
//            Port port = ports.get(i);
//
//            setDockVisibility(port, visibility);
//        }
//    }

//    // TODO: Move into PathImage
//    public void setDockVisibility(Port port, Visibility visibility) {
//        ImageGroup pathImages = getPathImages(port);
//        for (int i = 0; i < pathImages.size(); i++) {
//            PathImage pathImage = (PathImage) pathImages.get(i);
//
//            // Update visibility
//            pathImage.setDockVisibility(visibility);
//
//            // Deep
//            Port targetPort = pathImage.getPath().getTarget();
//            Portable targetPortable = (Portable) targetPort.getParent();
//            PortableImage targetPortableImage = (PortableImage) getSpace().getImages(targetPortable);
//            targetPortableImage.setDockVisibility(targetPort, visibility);
//        }
//    }

    // </REFACTOR>
}
