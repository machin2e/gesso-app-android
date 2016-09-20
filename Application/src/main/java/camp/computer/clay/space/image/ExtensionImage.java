package camp.computer.clay.space.image;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Group;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.util.Color;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;
import camp.computer.clay.space.architecture.Shape;

public class ExtensionImage extends Image<Extension> {

    private Visibility candidateExtensionVisibility = Visibility.INVISIBLE;
    private Point candidateExtensionSourcePosition = new Point();
    private Point candidateExtensionPosition = new Point();

    private Visibility candidatePathVisibility = Visibility.INVISIBLE;
    private Point candidatePathSourceCoordinate = new Point(40, 80);
    private Point candidatePathDestinationCoordinate = new Point(40, 80);

    public ExtensionImage(Extension extension) {
        super(extension);
        setup();
    }

    private void setup() {
        setupShapes();
        setupActions();
    }

    private void setupShapes() {

        Rectangle rectangle;

        // Create Shapes for Image
        rectangle = new Rectangle(200, 200);
        rectangle.setLabel("Board");
        rectangle.setColor("#f7f7f7");
        rectangle.setOutlineThickness(1);
        addShape(rectangle);

    }

    private void setupActions() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    Image targetImage = space.getImageByPosition(event.getPosition());
                    event.setTargetImage(targetImage);

                    if (action.getDuration() < Event.MAXIMUM_TAP_DURATION) {

                        // Focus on touched base
                        setPathVisibility(Visibility.VISIBLE);
                        setPortVisibility(Visibility.VISIBLE);
                        setTransparency(1.0);

                        // Show ports and paths of touched form
                        List<Shape> portShapes = getPortShapes();
                        for (int i = 0; i < portShapes.size(); i++) {
                            Shape portShape = portShapes.get(i);
                            Port port = (Port) portShape.getEntity();

                            List<Path> paths = port.getCompletePath();
                            for (int j = 0; j < paths.size(); j++) {
                                Path path = paths.get(j);

                                // Show ports
                                getSpace().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                getSpace().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                // Show path
                                getSpace().getImage(path).setVisibility(Visibility.VISIBLE);
                            }
                        }
                    }

                } else if (event.getType() == Event.Type.HOLD) {

                    Log.v("Event", "Tapped patch. Port image count: " + getPortShapes().size());
                    Port port = new Port();
                    getExtension().addPort(port);
                    space.addEntity(port);

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Update style
                    setCandidatePathVisibility(Visibility.INVISIBLE);
                    setCandidateExtensionVisibility(Visibility.INVISIBLE);

                }
            }
        });
    }

    public Extension getExtension() {
        return getEntity();
    }

    public List<Shape> getPortShapes() {
        List<Shape> portShapes = new LinkedList<>();

        for (int i = 0; i < this.shapes.size(); i++) {
            Shape shape = this.shapes.get(i);
            if (shape.getLabel().startsWith("Port ")) {
                portShapes.add(shape);
            }
        }

        return portShapes;
    }

    public void update() {

        // Update Port style
        for (int i = 0; i < getExtension().getPorts().size(); i++) {
            Port port = getExtension().getPorts().get(i);
            Shape portShape = getShape(port);

            // Update color of Port shape based on type
            if (portShape != null) {
                portShape.setColor(Color.getColor(port.getType()));
            }
        }
    }

    public void draw(Display display) {
        if (isVisible()) {

            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).draw(display);
            }

//            if (Launcher.ENABLE_GEOMETRY_LABELS) {
//                display.getPaint().setColor(Color.GREEN);
//                display.getPaint().setStyle(Paint.Style.STROKE);
//                Display.drawCircle(getPosition(), boardShape.getWidth(), 0, display);
//                Display.drawCircle(getPosition(), boardShape.getWidth() / 2.0f, 0, display);
//            }
        }
    }

    public void setPortVisibility(Visibility visibility) {
        getShapes("^Port (1[0-2]|[1-9])$").setVisibility(visibility);
    }

    public void setPathVisibility(Visibility visibility) {
        Group<Port> ports = getEntity().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            if (visibility == Visibility.INVISIBLE) {
                showDocks(port);
            }

            setPathVisibility(port, visibility);
        }
    }

    public void showDocks(Port port) {
        List<PathImage> pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = pathImages.get(i);

            pathImage.setDockVisibility(Visibility.VISIBLE);

            // Deep
//            PortImage targetPortImage = (PortImage) getSpace().getImage(pathImage.getPath().getTarget());
//            targetPortImage.setDockVisibility();
            Port targetPort = pathImage.getPath().getTarget();
            // <HACK>
            if (targetPort.getParent() instanceof Host) {
                Host targetHost = (Host) targetPort.getParent();
                HostImage targetHostImage = (HostImage) getSpace().getImage(targetHost);
                //// TODO: targetHostImage.setDockVisibility(targetPort, Visibility.VISIBLE);
            } else if (targetPort.getParent() instanceof Extension) {
                Extension targetHost = (Extension) targetPort.getParent();
                ExtensionImage targetHostImage = (ExtensionImage) getSpace().getImage(targetHost);
                //targetHostImage.setDockVisibility(targetPort);
            }
            // </HACK>
        }
    }

    // TODO: Replace with ImageGroup.filter().setVisibility()
    public void setPathVisibility(Port port, Visibility visibility) {
        List<PathImage> pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = pathImages.get(i);

            pathImage.setVisibility(visibility);

            // Deep
            Port targetPort = pathImage.getPath().getTarget();
            // <HACK>
            if (targetPort.getParent() instanceof Host) {
                Host targetHost = (Host) targetPort.getParent();
                HostImage targetHostImage = (HostImage) getSpace().getImage(targetHost);
                //// TODO: targetHostImage.setPathVisibility(targetPort, visibility);
            } else if (targetPort.getParent() instanceof Extension) {
                Extension targetHost = (Extension) targetPort.getParent();
                ExtensionImage targetHostImage = (ExtensionImage) getSpace().getImage(targetHost);
                //targetHostImage.setPathVisibility(targetPort, visibility);
            }
            // </HACK>
        }
    }

    public List<PathImage> getPathImages(Port port) {
        List<PathImage> pathImages = new ArrayList<>();
        List<Path> paths = port.getPaths();
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            PathImage pathImage = (PathImage) getSpace().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    public void setCandidatePathVisibility(Visibility visibility) {
        candidatePathVisibility = visibility;
    }

    public Visibility getCandidatePathVisibility() {
        return candidatePathVisibility;
    }

    public void setCandidatePathDestinationCoordinate(Point position) {
        this.candidatePathDestinationCoordinate.set(position);
    }

    public void setCandidateExtensionVisibility(Visibility visibility) {
        candidateExtensionVisibility = visibility;
    }

    public Visibility getCandidateExtensionVisibility() {
        return candidateExtensionVisibility;
    }
}

