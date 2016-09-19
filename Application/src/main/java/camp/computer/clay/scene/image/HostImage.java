package camp.computer.clay.scene.image;

import android.graphics.Canvas;
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
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.EventListener;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.architecture.ImageGroup;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.architecture.ShapeGroup;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Circle;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

public class HostImage extends Image<Host> {

    private Visibility candidateExtensionVisibility = Visibility.INVISIBLE;
    private Point candidateExtensionSourcePosition = new Point();
    private Point candidateExtensionPosition = new Point();

    private Visibility candidatePathVisibility = Visibility.INVISIBLE;
    private Point candidatePathSourceCoordinate = new Point(40, 80);
    private Point candidatePathDestinationCoordinate = new Point(40, 80);
    double shapeRadius = 40.0;

    // <HACK>
    public static String PORT_COLOR_OFF = "#ffefefef";
    public static String PORT_COLOR_SWITCH = "#ffff0000";
    public static String PORT_COLOR_PULSE = "#ff00ff00";
    public static String PORT_COLOR_WAVE = "#ff0000ff";
    public static String PORT_COLOR_REFERENCE = "#ffffff00";
    public static String PORT_COLOR_CMOS = "#ff00ffff";
    public static String PORT_COLOR_TTL = "#ffff00ff";

    public static String getPortColor(Port.Type portType) {
        if (portType == Port.Type.NONE) {
            return PORT_COLOR_OFF;
        } else if (portType == Port.Type.SWITCH) {
            return PORT_COLOR_SWITCH;
        } else if (portType == Port.Type.PULSE) {
            return PORT_COLOR_PULSE;
        } else if (portType == Port.Type.WAVE) {
            return PORT_COLOR_WAVE;
        } else if (portType == Port.Type.POWER_REFERENCE) {
            return PORT_COLOR_REFERENCE;
        } else if (portType == Port.Type.POWER_CMOS) {
            return PORT_COLOR_CMOS;
        } else if (portType == Port.Type.POWER_TTL) {
            return PORT_COLOR_TTL;
        } else {
            return PORT_COLOR_OFF;
        }
    }
    // </HACK>

    public HostImage(Host host) {
        super(host);
        setup();
    }

    private void setup() {
        setupShapes();
        setupActions();
    }

    private void setupShapes() {

        Rectangle rectangle;
        Circle circle;

        // Create shapes for figure
        rectangle = new Rectangle<Host>(getFeature());
        rectangle.setWidth(250);
        rectangle.setHeight(250);
        rectangle.setLabel("Board");
        rectangle.setColor("#f7f7f7");
        rectangle.setOutlineThickness(1);
        addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 1");
        rectangle.setPosition(0, 132);
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 2");
        rectangle.setPosition(132, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 3");
        rectangle.setPosition(0, -132);
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 4");
        rectangle.setPosition(-132, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        // Lights
        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 1");
        rectangle.setPosition(-20, 105);
        rectangle.setRotation(0);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 2");
        rectangle.setPosition(0, 105);
        rectangle.setRotation(0);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 3");
        rectangle.setPosition(20, 105);
        rectangle.setRotation(0);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 4");
        rectangle.setPosition(105, 20);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 5");
        rectangle.setPosition(105, 0);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 6");
        rectangle.setPosition(105, -20);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 7");
        rectangle.setPosition(20, -105);
        rectangle.setRotation(0);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 8");
        rectangle.setPosition(0, -105);
        rectangle.setRotation(0);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 9");
        rectangle.setPosition(-20, -105);
        rectangle.setRotation(0);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 10");
        rectangle.setPosition(-105, -20);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 11");
        rectangle.setPosition(-105, 0);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 12");
        rectangle.setPosition(-105, 20);
        rectangle.setRotation(90);
        addShape(rectangle);

        // Ports
        circle = new Circle<Port>(getHost().getPort(0));
        circle.setRadius(40);
        circle.setLabel("Port 1");
        circle.setPosition(-90, 200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(1));
        circle.setRadius(40);
        circle.setLabel("Port 2");
        circle.setPosition(0, 200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(2));
        circle.setRadius(40);
        circle.setLabel("Port 3");
        circle.setPosition(90, 200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(3));
        circle.setRadius(40);
        circle.setLabel("Port 4");
        circle.setPosition(200, 90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(4));
        circle.setRadius(40);
        circle.setLabel("Port 5");
        circle.setPosition(200, 0);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(5));
        circle.setRadius(40);
        circle.setLabel("Port 6");
        circle.setPosition(200, -90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(6));
        circle.setRadius(40);
        circle.setLabel("Port 7");
        circle.setPosition(90, -200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(7));
        circle.setRadius(40);
        circle.setLabel("Port 8");
        circle.setPosition(0, -200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(8));
        circle.setRadius(40);
        circle.setLabel("Port 9");
        circle.setPosition(-90, -200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(9));
        circle.setRadius(40);
        circle.setLabel("Port 10");
        circle.setPosition(-200, -90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(10));
        circle.setRadius(40);
        circle.setLabel("Port 11");
        circle.setPosition(-200, 0);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(11));
        circle.setRadius(40);
        circle.setLabel("Port 12");
        circle.setPosition(-200, 90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

    }

    private void setupActions() {

        setOnActionListener(new EventListener() {
                                @Override
                                public void onAction(Action action) {

                                    Event event = action.getLastEvent();

                                    Camera camera = event.getActor().getCamera();

                                    if (event.getType() == Event.Type.NONE) {

                                    } else if (event.getType() == Event.Type.SELECT) {

                                    } else if (event.getType() == Event.Type.HOLD) {

                                    } else if (event.getType() == Event.Type.MOVE) {

                                        if (action.getFirstEvent().getTargetShape() == null) {
                                            return;
                                        }

                                        if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                                            // Holding
                                            if (action.isHolding()) {

                                                // Holding and dragging

                                                // Host
                                                event.getTargetImage().processAction(action);
                                                event.getTargetImage().setPosition(event.getPosition());

                                                // Camera
                                                camera.focusSelectHost(event);

                                            } else {


                                                // Update position
                                                // event.getTargetImage().setPosition(event.getPosition());

                                                hidePortShapes();
                                                hidePathImages();

                                                candidateExtensionPosition.set(event.getPosition());

                                                setCandidateExtensionVisibility(Visibility.VISIBLE);

                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                                            if (!action.isHolding()) {

                                                // Candidate Path Visibility
                                                setCandidatePathDestinationCoordinate(event.getPosition());
                                                setCandidatePathVisibility(Visibility.VISIBLE);

                                                candidatePathSourceCoordinate = action.getFirstEvent().getTargetShape().getPosition();

                                                // Candidate Extension Visibility

                                                boolean isCreateExtensionAction = true;
                                                List<Image> images = getScene().getImages(Host.class, Extension.class).getList();
                                                for (int i = 0; i < images.size(); i++) {
                                                    Image nearbyImage = images.get(i);

                                                    // Update style of nearby machines
                                                    double distanceToBaseImage = Geometry.calculateDistance(
                                                            event.getPosition(), //candidatePathDestinationCoordinate,
                                                            nearbyImage.getPosition()
                                                    );

                                                    if (distanceToBaseImage < 500) {
                                                        isCreateExtensionAction = false;
                                                        break;
                                                    }

                                                    // TODO: if distance > 800: connect to cloud service
                                                }

                                                if (isCreateExtensionAction) {
                                                    setCandidateExtensionVisibility(Visibility.VISIBLE);
                                                    candidateExtensionSourcePosition.set(action.getFirstEvent().getTargetShape().getPosition());
                                                    candidateExtensionPosition.set(event.getPosition());
                                                } else {
                                                    setCandidateExtensionVisibility(Visibility.INVISIBLE);
                                                }

                                                // Get port associated with the touched port shape
                                                // TODO: Refactor
                                                Port port = (Port) action.getFirstEvent().getTargetShape().getFeature();

//                                                // Port type and flow direction
                                                if (port != null) {
                                                    // Update data model
                                                    if (port.getDirection() == Port.Direction.NONE) {
                                                        port.setDirection(Port.Direction.INPUT);
                                                    }
                                                    if (port.getType() == Port.Type.NONE) {
                                                        port.setType(Port.Type.next(port.getType()));
                                                    }
                                                }

                                                // Show nearby ports
                                                Port sourcePort = (Port) action.getFirstEvent().getTargetShape().getFeature();
                                                Event lastEvent = action.getLastEvent();

                                                ImageGroup nearbyImages = getScene().getImages(Host.class, Extension.class).filterArea(lastEvent.getPosition(), 200 + 60);
                                                List<Image> images2 = getScene().getImages(Host.class, Extension.class).getList();

                                                // Show ports of nearby forms
                                                for (int i = 0; i < images2.size(); i++) {
                                                    Image image = images2.get(i);

                                                    //if (image == portFigure.getParentImage() || nearbyImages.contains(image)) {
                                                    //if (image == boardImage /* || nearbyImages.contains(image) */) {
                                                    if (image.getFeature() == sourcePort.getParent() || nearbyImages.contains(image)) {

                                                        // <HACK>
                                                        if (image instanceof HostImage) {
                                                            HostImage nearbyFigure = (HostImage) image;
                                                            nearbyFigure.setTransparency(1.0f);
                                                            nearbyFigure.showPortShapes();
                                                        } else if (image instanceof ExtensionImage) {
                                                            ExtensionImage nearbyFigure = (ExtensionImage) image;

                                                        }
                                                        // </HACK>

                                                    } else {

                                                        // <HACK>
                                                        if (image instanceof HostImage) {
                                                            HostImage nearbyFigure = (HostImage) image;
                                                            nearbyFigure.setTransparency(0.1f);
                                                            nearbyFigure.hidePortShapes();
                                                        } else if (image instanceof ExtensionImage) {
                                                            ExtensionImage nearbyFigure = (ExtensionImage) image;
                                                            nearbyFigure.setTransparency(0.1f);
                                                            //// TODO: nearbyFigure.hidePortImages();
                                                        }
                                                        // </HACK>

                                                    }
                                                }

                                            } else if (action.isHolding()) {

//                                                // Holding and dragging
//
//                                                // Port
//                                                PortImage portImage = (PortImage) event.getTargetImage();
//
//                                                portImage.setDragging(true);
//                                                portImage.setPosition(event.getPosition());
                                            }

                                            // Camera
                                            camera.focusCreatePath(action);

                                        } else if (event.getTargetShape().getLabel().startsWith("LED")) {

                                        }

                                    } else if (event.getType() == Event.Type.UNSELECT) {

                                        // <HACK>
                                        // TODO: Refactor so this doesn't have to be here! It's messy this way... standardize the way "null shapes" are handled
                                        if (action.getFirstEvent().getTargetShape() == null) {
                                            return;
                                        }
                                        // </HACK>

                                        if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                                            if (action.getLastEvent().getTargetShape() != null
                                                    && action.getLastEvent().getTargetShape().getLabel().startsWith("Port")) {

                                                // (Host.Port, ..., Host.Port) Action Pattern

                                                if (action.getFirstEvent().getTargetShape() == action.getLastEvent().getTargetShape()) { // if (action.isTap()) {

                                                    // (Host.Port A, ..., Host.Port A) Action Pattern
                                                    // i.e., The action's first and last events address the same port. Therefore, it must be either a tap or a hold.

                                                    // Get port associated with the touched port shape
                                                    Port port = (Port) action.getFirstEvent().getTargetShape().getFeature();
                                                    int portIndex = getHost().getPorts().indexOf(port);

                                                    if (port.getType() == Port.Type.NONE) {

                                                        Log.v("TouchPort", "A");

                                                        port.setDirection(Port.Direction.INPUT);
                                                        port.setType(Port.Type.next(port.getType()));

                                                    } else if (!port.hasPath() && port.getAncestorPaths().size() == 0) {

                                                        Log.v("TouchPort", "B");

                                                        // TODO: Replace with state of camera. i.e., Check if seeing a single path.

                                                        Port.Type nextType = port.getType();
                                                        //while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                                            nextType = Port.Type.next(nextType);
                                                        }
                                                        port.setType(nextType);

                                                    } else if (!hasVisiblePaths(portIndex) && !hasVisibleAncestorPaths(portIndex)) {

                                                        Log.v("TouchPort", "C");

                                                        // Remove focus from other hosts and their ports
                                                        List<Image> hostImages = getScene().getImages(Host.class).getList();
                                                        for (int i = 0; i < hostImages.size(); i++) {
                                                            HostImage hostImage = (HostImage) hostImages.get(i);
                                                            hostImage.hidePortShapes();
                                                            hostImage.hidePathImages();

                                                            // Get shapes in image matching labels "Board", "Header <number>", and "LED <number>"
                                                            ShapeGroup shapes = hostImage.getShapes().filterLabel("^Board$", "^Header (1|2|3|4)$", "^LED (1[0-2]|[1-9])$");
                                                            shapes.setTransparency(0.1);
                                                        }

                                                        List<Image> extensionImages = getScene().getImages().filterType(Extension.class).getList();
                                                        for (int i = 0; i < extensionImages.size(); i++) {
                                                            ExtensionImage extensionImage = (ExtensionImage) extensionImages.get(i);
                                                            if (extensionImage.getExtension() != getParentImage().getFeature()) {
                                                                extensionImage.setTransparency(0.1);
                                                                extensionImage.hidePortImages();
                                                                extensionImage.hidePathImages();
                                                            }
                                                        }

                                                        // Focus on the port
                                                        showPaths(port);
                                                        setVisibility(Visibility.VISIBLE);
                                                        setPathVisibility(port, Visibility.VISIBLE);

                                                        List<Path> paths = port.getCompletePath();
                                                        for (int i = 0; i < paths.size(); i++) {
                                                            Path path = paths.get(i);

                                                            // Show Ports
                                                            getScene().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                            getScene().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                                            // Show Path
                                                            getScene().getImage(path).setVisibility(Visibility.VISIBLE);
                                                        }

                                                        // <HACK>
                                                        // TODO: Put this code in Camera
                                                        // Camera
                                                        List<Port> pathPorts = port.getPorts(paths);
                                                        List<Point> pathPortPositions = getScene().getShapes().filterFeature(pathPorts).getCoordinates();
                                                        Rectangle boundingBox = getScene().getShapes().filterFeature(pathPorts).getBoundingBox();
                                                        getScene().getFeature().getActor(0).getCamera().adjustScale(boundingBox);
                                                        getScene().getFeature().getActor(0).getCamera().setPosition(Geometry.calculateCenterCoordinate(pathPortPositions));
                                                        // </HACK>

                                                    } else if (hasVisiblePaths(portIndex) || hasVisibleAncestorPaths(portIndex)) {

                                                        Log.v("TouchPort", "D");

                                                        // Paths are being shown. Touching a port changes the port type. This will also
                                                        // updates the corresponding path requirement.

                                                        // TODO: Replace with state of camera. i.e., Check if seeing a single path.

                                                        Port.Type nextType = port.getType();
                                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                                            nextType = Port.Type.next(nextType);
                                                        }
                                                        port.setType(nextType);

                                                    }

                                                    setCandidatePathVisibility(Visibility.INVISIBLE);

                                                } else if (action.getFirstEvent().getTargetShape() != action.getLastEvent().getTargetShape()) {

                                                    // (Host.Port A, ..., Host.Port B) Action Pattern
                                                    // i.e., The action's first and last events address different ports.

                                                    Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                                    if (action.isDragging()) {

                                                        Log.v("Events", "B");

                                                        Port sourcePort = (Port) sourcePortShape.getFeature();
                                                        Port targetPort = null;

                                                        Shape targetPortShape = getScene().getShapes(Port.class).remove(sourcePortShape).filterContains(event.getPosition()).get(0);
                                                        targetPort = (Port) targetPortShape.getFeature();

                                                        if (targetPort == null) {

                                                            // targetPort is null, meaning that no target port shape was found

                                                            Log.v("Events", "C.1");

                                                            // Update source port configuration
                                                            if (sourcePort.getDirection() == Port.Direction.NONE) {
                                                                sourcePort.setDirection(Port.Direction.INPUT);
                                                            }
                                                            if (sourcePort.getType() == Port.Type.NONE) {
                                                                sourcePort.setType(Port.Type.next(sourcePort.getType()));
                                                            }

                                                        } else {

                                                            // targetPort is not null, meaning a target port shape was found

                                                            Log.v("Events", "C.2");

                                                            // Update source port configuration
                                                            if (sourcePort.getDirection() == Port.Direction.NONE) {
                                                                sourcePort.setDirection(Port.Direction.INPUT);
                                                            }
                                                            if (sourcePort.getType() == Port.Type.NONE) {
                                                                sourcePort.setType(Port.Type.next(sourcePort.getType())); // (machineSprite.channelTypes.getEvent(i) + 1) % machineSprite.channelTypeColors.length
                                                            }

                                                            // Update target port configuration
                                                            if (targetPort.getDirection() == Port.Direction.NONE) {
                                                                targetPort.setDirection(Port.Direction.OUTPUT);
                                                            }
                                                            if (targetPort.getType() == Port.Type.NONE) {
                                                                targetPort.setType(sourcePort.getType());
                                                            }

                                                            Log.v("Events", "targetPort: " + targetPort);

                                                            if (!sourcePort.hasAncestor(targetPort)) {

                                                                Log.v("Events", "D.1");

                                                                // Create and configure new path
                                                                Path path = new Path(sourcePort, targetPort);

                                                                if (sourcePort.getParent() instanceof Extension || targetPort.getParent() instanceof Extension) {
                                                                    path.setType(Path.Type.ELECTRONIC);
                                                                } else {
                                                                    path.setType(Path.Type.MESH);
                                                                }

                                                                sourcePort.addPath(path);
                                                                scene.addFeature(path);
                                                            }

                                                            // Camera
                                                            event.getActor().getCamera().focusSelectPath(sourcePort);
                                                        }

                                                        setCandidatePathVisibility(Visibility.INVISIBLE);

                                                    }

                                                }

                                            } else if (action.getLastEvent().getTargetShape() == null
                                                    // TODO: && action.getLastEvent().getTargetImage().getLabel().startsWith("Scene")) {
                                                    && action.getLastEvent().getTargetImage() == getScene()) {

                                                // (Host.Port, ..., Scene) Action Pattern

                                                Log.v("Extension", "Creating Extension from Port");

                                                Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                                if (getCandidateExtensionVisibility() == Visibility.VISIBLE) {

                                                    Log.v("IASM", "(1) touch extension to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

                                                    // Create the Extension
                                                    Extension extension = new Extension();

                                                    // Create Ports and add them to the Extension
                                                    // for (int j = 0; j < 3; j++) {
                                                    for (int j = 0; j < 1; j++) {
                                                        Port port = new Port();
                                                        extension.addPort(port);
                                                    }

                                                    // Add Extension to Model
                                                    getScene().getModel().addExtension(extension);

                                                    // Add Extension to Scene
                                                    getScene().addFeature(extension);

                                                    // Get the just-created Extension Image
                                                    Image extensionImage = getScene().getImage(extension);

                                                    // Update the Extension Image position and rotation
                                                    extensionImage.setPosition(event.getPosition());

                                                    double extensionImageRotation = Geometry.calculateRotationAngle(
                                                            sourcePortShape.getPosition(),
                                                            extensionImage.getPosition()
                                                    );
                                                    extensionImage.setRotation(extensionImageRotation + 90);
//
                                                    // Create Port shapes for each of Extension's Ports
                                                    for (int i = 0; i < extension.getPorts().size(); i++) {
                                                        Port port = extension.getPorts().get(i);

                                                        // Ports
                                                        Circle<Port> circle = new Circle<>(port);
                                                        circle.setRadius(40);
                                                        circle.setLabel("Port 1");
                                                        circle.setPosition(-90, 200);
                                                        // circle.setRelativeRotation(0);

                                                        circle.setColor("#efefef");
                                                        circle.setOutlineThickness(0);

                                                        circle.setVisibility(Visibility.INVISIBLE);

                                                        extensionImage.addShape(circle);
                                                    }

                                                    // Configure Host's Port (i.e., the Path's source Port)
                                                    Port hostPort = (Port) sourcePortShape.getFeature();

                                                    if (hostPort.getType() == Port.Type.NONE || hostPort.getDirection() == Port.Direction.NONE) {
                                                        hostPort.setType(Port.Type.POWER_REFERENCE); // Set the default type to reference (ground)
                                                        hostPort.setDirection(Port.Direction.BOTH);
                                                    }

                                                    // Configure Extension's Ports (i.e., the Path's target Port)
                                                    Port extensionPort = extension.getPorts().get(0);
                                                    extensionPort.setDirection(Port.Direction.INPUT);
                                                    extensionPort.setType(hostPort.getType());

                                                    // Create Path from Host to Extension
                                                    Path path = new Path(hostPort, extensionPort);
                                                    path.setType(Path.Type.ELECTRONIC);
                                                    hostPort.addPath(path);

                                                    // Add Path to Scene
                                                    scene.addFeature(path);

                                                    //Shape targetPortShape =  getScene().getShape(path.getTarget());
                                                    //targetPortShape.setColor(sourcePortShape.getColor());
                                                    //targetPortShape.setUniqueColor(sourcePortShape.getUniqueColor());

                                                    // Update Camera
                                                    camera.focusSelectPath(hostPort);

                                                }

                                                // Update Image
                                                setCandidatePathVisibility(Visibility.INVISIBLE);
                                                setCandidateExtensionVisibility(Visibility.INVISIBLE);

//                                                setCandidateExtensionVisibility(Visibility.INVISIBLE);


                                            } else {

                                                // Get port associated with the touched port shape
                                                Port port = (Port) action.getFirstEvent().getTargetShape().getFeature();

                                                // Port type and flow direction
                                                if (port != null) {
                                                    // Update data model
                                                    if (port.getDirection() == Port.Direction.NONE) {
                                                        port.setDirection(Port.Direction.INPUT);
                                                    }
                                                    if (port.getType() == Port.Type.NONE) {
                                                        port.setType(Port.Type.next(port.getType()));
                                                    }
                                                }

                                                setCandidatePathVisibility(Visibility.INVISIBLE);
                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                                            if (action.isTap()) {

                                                // Focus on touched form
                                                showPathImages();
                                                showPortShapes();

                                                setTransparency(1.0);

                                                // TODO: Speak "choose a channel to getEvent data."

                                                // Show ports and paths of touched form
                                                for (int i = 0; i < getPortShapes().size(); i++) {
                                                    List<Path> paths = getPort(i).getCompletePath();

                                                    for (int j = 0; j < paths.size(); j++) {
                                                        Path path = paths.get(j);

                                                        Log.v("Events2", "path.getSource(): " + path.getSource());
                                                        Log.v("Events2", "path.getTarget(): " + path.getTarget());

                                                        // Show source and target ports in path
                                                        //getPortShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                        getScene().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                        getScene().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                                        // Show path connection
                                                        getScene().getImage(path).setVisibility(Visibility.VISIBLE);
                                                    }
                                                }

                                                // Camera
                                                camera.focusSelectHost(event);

                                            } else {

                                                // TODO: Release longer than tap!

                                                if (action.getFirstEvent().getTargetImage() instanceof HostImage) {

                                                    if (event.getTargetImage() instanceof HostImage) {

                                                        // If getFirstEvent processAction was on the same form, then respond
                                                        if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage() instanceof HostImage) {

                                                            // Host
                                                            event.getTargetImage().processAction(action);

                                                            // Camera
//                        camera.focusSelectScene();
                                                        }

                                                    } else if (event.getTargetImage() instanceof Scene) {

                                                        // Host
//                                                        action.getFirstEvent().getTargetImage().processAction(action);

                                                    }

                                                }

                                            }

                                            // Check if connecting to a extension
                                            if (getCandidateExtensionVisibility() == Visibility.VISIBLE) {

                                                // Show extension store
                                                Launcher.getLauncherView().displayChooseDialog();
//                        Launcher.getLauncherView().displayTasksDialog();

                                                setCandidateExtensionVisibility(Visibility.INVISIBLE);
                                            }
                                        }

                                    }
                                }
                            }

        );
    }

    // <REFACTOR>

    public Port getPort(int index) {
        return getHost().getPort(index);
    }

    public List<PathImage> getPathImages(int index) {
        List<PathImage> pathImages = new ArrayList<>();
        List<Path> paths = getPort(index).getPaths();
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            PathImage pathImage = (PathImage) getScene().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    public boolean hasVisiblePaths(int index) {
        List<PathImage> pathImages = getPathImages(index);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = pathImages.get(i);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths(int index) {
        List<Path> ancestorPaths = getPort(index).getAncestorPaths();
        for (int i = 0; i < ancestorPaths.size(); i++) {
            Path ancestorPath = ancestorPaths.get(i);
            PathImage pathImage = (PathImage) getScene().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    // </REFACTOR>

    public Host getHost() {
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

    public Shape getPortShape(Port port) {
        for (int i = 0; i < this.shapes.size(); i++) {
            Shape shape = this.shapes.get(i);
            if (shape.getFeature() == port) {
                return shape;
            }
        }
        return null;
    }

    public void update() {

        // Get LED shapes
        ShapeGroup lightShapeGroup = getShapes().filterLabel("^LED (1[0-2]|[1-9])$");

        // Update Port and LED shape styles
        for (int i = 0; i < getFeature().getPorts().size(); i++) {
            Port port = getFeature().getPorts().get(i);
            Shape portShape = getShape(port);

            // Update color of Port shape based on type
            portShape.setColor(getPortColor(port.getType()));

            // Update color of LED based on corresponding Port's type
            lightShapeGroup.get(i).setColor(portShape.getColor());
        }

    }

    public void draw(Display display) {
        if (isVisible()) {

            // Color
            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).draw(display);
            }

            // Labels
            if (Launcher.ENABLE_GEOMETRY_LABELS) {
                display.getPaint().setColor(Color.GREEN);
                display.getPaint().setStyle(Paint.Style.STROKE);
                Rectangle boardShape = (Rectangle) getShape("Board");
                Display.drawCircle(getPosition(), boardShape.getWidth(), 0, display);
                Display.drawCircle(getPosition(), boardShape.getWidth() / 2.0f, 0, display);
            }

            // Draw candidate paths
            drawCandidatePathImages(display);

            // Draw candidate extensions
            drawCandidateExtensionImage(display);
        }
    }

    public void showPortShapes() {
        getShapes("^Port (1[0-2]|[1-9])$").setVisibility(Visibility.VISIBLE);
    }

    public void hidePortShapes() {
        getShapes("^Port (1[0-2]|[1-9])$").setVisibility(Visibility.INVISIBLE);
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

    public void showPaths(Port port) {
        List<PathImage> pathImages = getPathImages(port);
        for (int i = 0; i < pathImages.size(); i++) {
            PathImage pathImage = pathImages.get(i);
            pathImage.showDocks = false;

            // Deep
            Port targetPort = pathImage.getPath().getTarget();
            Host targetHost = (Host) targetPort.getParent();
            HostImage targetHostImage = (HostImage) getScene().getImage(targetHost);

            //PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
            targetHostImage.showPaths(targetPort);
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

    public void setCandidateExtensionVisibility(Visibility visibility) {
        candidateExtensionVisibility = visibility;
    }

    public Visibility getCandidateExtensionVisibility() {
        return candidateExtensionVisibility;
    }

    private void drawCandidateExtensionImage(Display display) {

        if (candidateExtensionVisibility == Visibility.VISIBLE) {

            Canvas canvas = display.getCanvas();
            Paint paint = display.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    candidateExtensionSourcePosition,
                    candidateExtensionPosition
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.CYAN); // paint.setColor(getUniqueColor());
            Display.drawRectangle(candidateExtensionPosition, pathRotationAngle + 180, 250, 250, display);

        }

    }

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawCandidatePathImages(Display display) {
        if (candidatePathVisibility == Visibility.VISIBLE) {

//            if (getPort().getType() != Port.Type.NONE) {

            Canvas canvas = display.getCanvas();
            Paint paint = display.getPaint();

            double triangleWidth = 20;
            double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
            double triangleSpacing = 35;

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
//                paint.setColor(this.getUniqueColor());

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    //getPosition(),
                    candidatePathSourceCoordinate,
                    candidatePathDestinationCoordinate
            );

            Point pathStartCoordinate = Geometry.calculatePoint(
                    // getPosition(),
                    candidatePathSourceCoordinate,
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            Point pathStopCoordinate = Geometry.calculatePoint(
                    candidatePathDestinationCoordinate,
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            Display.drawTrianglePath(
                    pathStartCoordinate,
                    pathStopCoordinate,
                    triangleWidth,
                    triangleHeight,
                    display
            );

            // Color
            paint.setStyle(Paint.Style.FILL);
//                paint.setColor(getUniqueColor());
            Display.drawCircle(candidatePathDestinationCoordinate, shapeRadius, 0.0f, display);
//            }
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
}

