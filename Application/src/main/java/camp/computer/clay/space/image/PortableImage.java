package camp.computer.clay.space.image;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Group;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.architecture.Portable;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.architecture.util.ImageGroup;
import camp.computer.clay.space.architecture.util.ShapeGroup;
import camp.computer.clay.space.util.Visibility;

public abstract class PortableImage extends Image<Portable>
{

    public PortableImage(Portable portable)
    {
        super(portable);
    }

    @Override
    public void update()
    {

    }

    @Override
    public void draw(Display display)
    {

    }

    public Portable getPortable()
    {
        return getEntity();
    }

    public ShapeGroup getPortShapes()
    {
        return getShapes(getPortable().getPorts());
    }

    // <REFACTOR>

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public List<PathImage> getPathImages(int portIndex)
    {
        List<PathImage> pathImages = new ArrayList<>();
        List<Path> paths = getPortable().getPort(portIndex).getPaths();
        // TODO: ImageGroup images = space.getImages(paths);
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            PathImage pathImage = (PathImage) getSpace().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public boolean hasVisiblePaths(int portIndex)
    {
        List<PathImage> pathImages = getPathImages(portIndex);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = pathImages.get(i);
            if (pathImage.isVisible() && !pathImage.isDockVisible()) {
                return true;
            }
        }
        return false;
    }

    // TODO: Move into Port? something (inner class? custom PortShape?)
    public boolean hasVisibleAncestorPaths(int portIndex)
    {
        List<Path> ancestorPaths = getPortable().getPort(portIndex).getAncestorPaths();
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
    public ImageGroup getPathImages(Port port)
    {
        ImageGroup pathImages = new ImageGroup();
        for (int i = 0; i < port.getPaths().size(); i++) {
            Path path = port.getPaths().get(i);
            PathImage pathImage = (PathImage) getSpace().getImage(path);
            pathImages.add(pathImage);
        }
        return pathImages;
    }

    // TODO: Move into PathImage
    public void setPathVisibility(Visibility.Value visibility)
    {
        Group<Port> ports = getPortable().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setPathVisibility(port, visibility);
        }
    }

    // TODO: Move into PathImage
    // TODO: Replace with ImageGroup.filter().setVisibility()
    public void setPathVisibility(Port port, Visibility.Value visibility)
    {
        ImageGroup pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = (PathImage) pathImages.get(i);

            // Update visibility
            if (visibility == Visibility.Value.VISIBLE) {
                pathImage.setVisibility(Visibility.Value.VISIBLE);
//                 pathImage.setDockVisibility(Visibility.INVISIBLE);
            } else if (visibility == Visibility.Value.INVISIBLE) {
                pathImage.setVisibility(Visibility.Value.INVISIBLE);
                //pathImage.setDockVisibility(Visibility.VISIBLE);
            }

            // Recursively traverse Ports in descendant Paths and setValue their Path image visibility
            Port targetPort = pathImage.getPath().getTarget();
            Portable targetPortable = (Portable) targetPort.getParent();
            PortableImage targetPortableImage = (PortableImage) getSpace().getImage(targetPortable);
            targetPortableImage.setPathVisibility(targetPort, visibility);
        }
    }

    // TODO: Move into PathImage
    public void setDockVisibility(Visibility.Value visibility)
    {
        Group<Port> ports = getPortable().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setDockVisibility(port, visibility);
        }
    }

    // TODO: Move into PathImage
    public void setDockVisibility(Port port, Visibility.Value visibility)
    {
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
