package camp.computer.clay.space.image;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Group;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.Portable;
import camp.computer.clay.model.util.PathGroup;
import camp.computer.clay.util.geometry.Vertex;
import camp.computer.clay.util.image.Image;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ImageGroup;
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

    public Shape getPortShape(Port port) {
        return getShapes().filterEntity(port).get(0);
    }

    // <REFACTOR>

    public ImageGroup getPathImages() {
        ImageGroup pathImages = new ImageGroup();
        for (int i = 0; i < getPortable().getPorts().size(); i++) {
            pathImages.addAll(getPathImages(getPortable().getPorts().get(i)));
        }
        return pathImages;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public ImageGroup getPathImages(int portIndex) {
        ImageGroup pathImages = new ImageGroup();
        PathGroup paths = getPortable().getPort(portIndex).getForwardPaths();
        // TODO: ImageGroup images = parentSpace.getImages(paths);
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            PathImage pathImage = (PathImage) getSpace().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public boolean hasVisiblePaths(int portIndex) {
        ImageGroup pathImages = getPathImages(portIndex);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = (PathImage) pathImages.get(i);
            if (pathImage.isVisible() && !pathImage.isDockVisible()) {
                return true;
            }
        }
        return false;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public boolean hasVisibleAncestorPaths(int portIndex) {
        PathGroup ancestorPaths = getPortable().getPort(portIndex).getAncestorPaths();
        for (int i = 0; i < ancestorPaths.size(); i++) {
            Path ancestorPath = ancestorPaths.get(i);
            PathImage pathImage = (PathImage) getSpace().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.isDockVisible()) {
                return true;
            }
        }
        return false;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public ImageGroup getPathImages(Port port) {
        ImageGroup pathImages = new ImageGroup();
        for (int i = 0; i < port.getForwardPaths().size(); i++) {
            Path path = port.getForwardPaths().get(i);
            PathImage pathImage = (PathImage) getSpace().getImage(path);
            pathImages.add(pathImage);
        }
        return pathImages;
    }

    // TODO: Move into PathImage
    public void setPathVisibility(Visibility.Value visibility) {
        Group<Port> ports = getPortable().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setPathVisibility(port, visibility);
        }
    }

    // TODO: Move into PathImage
    // TODO: Replace with ImageGroup.filter().setVisibility()
    public void setPathVisibility(Port port, Visibility.Value visibility) {
        ImageGroup pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = (PathImage) pathImages.get(i);

            // Update visibility
            if (visibility == Visibility.Value.VISIBLE) {
                pathImage.setVisibility(Visibility.Value.VISIBLE);
                // pathImage.setDockVisibility(Visibility.INVISIBLE);
            } else if (visibility == Visibility.Value.INVISIBLE) {
                pathImage.setVisibility(Visibility.Value.INVISIBLE);
                // pathImage.setDockVisibility(Visibility.VISIBLE);
            }

            // Recursively traverse Ports in descendant Paths and setValue their Path image visibility
            Port targetPort = pathImage.getPath().getTarget();
            Portable targetPortable = (Portable) targetPort.getParent();
            PortableImage targetPortableImage = (PortableImage) getSpace().getImage(targetPortable);
            targetPortableImage.setPathVisibility(targetPort, visibility);
        }
    }

    // TODO: Move into PathImage
    public void setDockVisibility(Visibility.Value visibility) {
        Group<Port> ports = getPortable().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setDockVisibility(port, visibility);
        }
    }

    // TODO: Move into PathImage
    public void setDockVisibility(Port port, Visibility.Value visibility) {
        ImageGroup pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = (PathImage) pathImages.get(i);

            // Update visibility
            pathImage.setDockVisibility(visibility);

            // Deep
            Port targetPort = pathImage.getPath().getTarget();
            Portable targetPortable = (Portable) targetPort.getParent();
            PortableImage targetPortableImage = (PortableImage) getSpace().getImage(targetPortable);
            targetPortableImage.setDockVisibility(targetPort, visibility);
        }
    }

    // </REFACTOR>
}
