package camp.computer.clay.scene.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

public class ExtensionImage extends Image<Extension> {

    // Shapes
    private Rectangle boardShape = null;

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

        // Create Shapes for Image
        boardShape = new Rectangle(200, 200);
        boardShape.setColor("#f7f7f7");
        boardShape.setOutlineThickness(1);
        addShape(boardShape);

    }

    private void setupActions() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    Image targetImage = scene.getImageByPosition(event.getPosition());
                    event.setTargetImage(targetImage);

                    if (action.getDuration() < Event.MAXIMUM_TAP_DURATION) {

                        // Focus on touched base
                        showPathImages();
                        showPortShapes();
                        setTransparency(1.0);

                        // Show ports and paths of touched form
                        List<Shape> portShapes = getPortShapes();
                        for (int i = 0; i < portShapes.size(); i++) {
                            Shape portShape = portShapes.get(i);
                            Port port = (Port) portShape.getFeature();

                            List<Path> paths = port.getCompletePath();
                            for (int j = 0; j < paths.size(); j++) {
                                Path path = paths.get(j);

                                // Show ports
                                scene.getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                                scene.getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                // Show path
                                scene.getImage(path).setVisibility(Visibility.VISIBLE);
                            }
                        }
                    }

                } else if (event.getType() == Event.Type.HOLD) {

                    Log.v("Event", "Tapped patch. Port image count: " + getPortShapes().size());
                    Port port = new Port();
                    getExtension().addPort(port);
                    scene.addFeature(port);

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
        return getFeature();
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
    }

    public void draw(Display display) {
        if (isVisible()) {

            // Color
            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).draw(display);
            }

            if (Launcher.ENABLE_GEOMETRY_LABELS) {
                display.getPaint().setColor(Color.GREEN);
                display.getPaint().setStyle(Paint.Style.STROKE);
                Display.drawCircle(getPosition(), boardShape.getWidth(), 0, display);
                Display.drawCircle(getPosition(), boardShape.getWidth() / 2.0f, 0, display);
            }
        }
    }

    public Rectangle getShape() {
        return this.boardShape;
    }

    public void showPortShapes() {
        getShapes("^Port (1[0-2]|[1-9])$").setVisibility(Visibility.VISIBLE);
    }

    public void hidePortShapes() {
        getShapes("^Port (1[0-2]|[1-9])$").setVisibility(Visibility.INVISIBLE);
    }

    public void showPathImages() {
        List<Port> ports = getFeature().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setPathVisibility(port, Visibility.VISIBLE);
        }
    }

    public void hidePathImages() {
        List<Port> ports = getFeature().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setPathVisibility(port, Visibility.INVISIBLE);
            showDocks(port);
        }
    }

    public void showDocks(Port port) {
        List<PathImage> pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = pathImages.get(i);

            pathImage.showDocks = true;

            // Deep
//            PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
//            targetPortImage.showDocks();
            Port targetPort = pathImage.getPath().getTarget();
            // <HACK>
            if (targetPort.getParent() instanceof Host) {
                Host targetHost = (Host) targetPort.getParent();
                HostImage targetHostImage = (HostImage) getScene().getImage(targetHost);
                targetHostImage.showDocks(targetPort);
            } else if (targetPort.getParent() instanceof Extension) {
                Extension targetHost = (Extension) targetPort.getParent();
                ExtensionImage targetHostImage = (ExtensionImage) getScene().getImage(targetHost);
                //targetHostImage.showDocks(targetPort);
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
                HostImage targetHostImage = (HostImage) getScene().getImage(targetHost);
                targetHostImage.setPathVisibility(targetPort, visibility);
            } else if (targetPort.getParent() instanceof Extension) {
                Extension targetHost = (Extension) targetPort.getParent();
                ExtensionImage targetHostImage = (ExtensionImage) getScene().getImage(targetHost);
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
            PathImage pathImage = (PathImage) getScene().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    public boolean contains(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean contains(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
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

