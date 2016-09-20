package camp.computer.clay.space.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Group;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.architecture.ImageGroup;
import camp.computer.clay.space.architecture.Space;
import camp.computer.clay.space.architecture.ShapeGroup;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Circle;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Rectangle;
import camp.computer.clay.space.architecture.Shape;

public class HostImage extends PortableImage {

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
        rectangle = new Rectangle<Host>(getHost());
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

        setOnActionListener(new ActionListener() {
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

                                                space.setPrototypeExtensionPosition(event.getPosition());

                                                getPortShapes().setVisibility(Visibility.INVISIBLE);
                                                setPathVisibility(Visibility.INVISIBLE);
                                                setDockVisibility(Visibility.VISIBLE);

                                                space.setPrototypeExtensionVisibility(Visibility.VISIBLE);

                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                                            if (!action.isHolding()) {

                                                // Candidate Path Visibility
                                                space.setPrototypePathSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                                                space.setPrototypePathDestinationPosition(event.getPosition());
                                                space.setPrototypePathVisibility(Visibility.VISIBLE);

                                                // Candidate Extension Visibility
                                                boolean isCreateExtensionAction = true;
                                                ImageGroup imageGroup = space.getImages(Host.class, Extension.class);
                                                for (int i = 0; i < imageGroup.size(); i++) {
                                                    Image otherImage = imageGroup.get(i);

                                                    // Update style of nearby Hosts
                                                    double distanceToHostImage = Geometry.calculateDistance(
                                                            event.getPosition(), //candidatePathDestinationCoordinate,
                                                            otherImage.getPosition()
                                                    );

                                                    if (distanceToHostImage < 500) {
                                                        isCreateExtensionAction = false;
                                                        break;
                                                    }

                                                    // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                                                }

                                                if (isCreateExtensionAction) {
                                                    space.setPrototypeExtensionVisibility(Visibility.VISIBLE);
                                                    space.setPrototypePathSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                                                    space.setPrototypeExtensionPosition(event.getPosition());
                                                } else {
                                                    space.setPrototypeExtensionVisibility(Visibility.INVISIBLE);
                                                }

                                                // Get Port associated with the touched Port's shape
                                                // TODO: Refactor
                                                Port port = (Port) action.getFirstEvent().getTargetShape().getEntity();

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

                                                // Show Ports of nearby Hosts and Extensions
                                                Port sourcePort = (Port) action.getFirstEvent().getTargetShape().getEntity();
                                                Event lastEvent = action.getLastEvent();

                                                double nearbyRadiusThreshold = 200 + 60;
                                                ImageGroup nearbyImages = imageGroup.filterArea(lastEvent.getPosition(), nearbyRadiusThreshold);

                                                // Show Ports of nearby Hosts
                                                for (int i = 0; i < imageGroup.size(); i++) {
                                                    Image image = imageGroup.get(i);

                                                    if (image.getEntity() == sourcePort.getParent() || nearbyImages.contains(image)) {

//                                                        // <HACK>
//                                                        if (image instanceof HostImage) {
                                                        PortableImage nearbyFigure = (PortableImage) image;
                                                        nearbyFigure.setTransparency(1.0f);
                                                        nearbyFigure.getPortShapes().setVisibility(Visibility.VISIBLE);
//                                                        } else if (image instanceof ExtensionImage) {
//                                                            // ExtensionImage nearbyFigure = (ExtensionImage) image;
//
//                                                        }
                                                        // </HACK>

                                                    } else {

                                                        // <HACK>
//                                                        if (image instanceof HostImage) {
                                                        PortableImage nearbyFigure = (PortableImage) image;
                                                        nearbyFigure.setTransparency(0.1f);
                                                        nearbyFigure.getPortShapes().setVisibility(Visibility.INVISIBLE);
//                                                        } else if (image instanceof ExtensionImage) {
//                                                            ExtensionImage nearbyFigure = (ExtensionImage) image;
//                                                            nearbyFigure.setTransparency(0.1f);
//                                                            //// TODO: nearbyFigure.hidePortShapes();
//                                                        }
                                                        // </HACK>

                                                    }
                                                }

                                            } else if (action.isHolding()) {

//                                                // Holding and dragging

                                            }

                                            // Camera
                                            camera.focusCreatePath(action);

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
                                                    Port port = (Port) action.getFirstEvent().getTargetShape().getEntity();
                                                    int portIndex = getHost().getPorts().indexOf(port);

                                                    if (port.getType() == Port.Type.NONE) {

                                                        Log.v("TouchPort", "A");

                                                        port.setDirection(Port.Direction.INPUT);
                                                        port.setType(Port.Type.next(port.getType()));

                                                    } else if (!port.hasPath() && port.getAncestorPaths().size() == 0) {

                                                        Log.v("TouchPort", "B");

                                                        Port.Type nextType = port.getType();
                                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                                            nextType = Port.Type.next(nextType);
                                                        }
                                                        port.setType(nextType);

                                                    } else if (!hasVisiblePaths(portIndex) && !hasVisibleAncestorPaths(portIndex)) {

                                                        Log.v("TouchPort", "C");

                                                        // Remove focus from other Hosts and their Ports
                                                        ImageGroup hostImages = space.getImages(Host.class);
                                                        for (int i = 0; i < hostImages.size(); i++) {
                                                            HostImage hostImage = (HostImage) hostImages.get(i);
                                                            hostImage.getPortShapes().setVisibility(Visibility.INVISIBLE);
                                                            hostImage.setPathVisibility(Visibility.INVISIBLE);
                                                            hostImage.setDockVisibility(Visibility.VISIBLE);

                                                            // Get shapes in image matching labels "Board", "Header <number>", and "LED <number>"
                                                            ShapeGroup shapes = hostImage.getShapes().filterLabel("^Board$", "^Header (1|2|3|4)$", "^LED (1[0-2]|[1-9])$");
                                                            shapes.setTransparency(0.1);
                                                        }

                                                        ImageGroup extensionImages = space.getImages().filterType(Extension.class);
                                                        for (int i = 0; i < extensionImages.size(); i++) {
                                                            ExtensionImage extensionImage = (ExtensionImage) extensionImages.get(i);
//                                                            if (extensionImage.getExtension() != getParentImage().getEntity()) {
                                                            extensionImage.setTransparency(0.1);
                                                            getPortShapes().setVisibility(Visibility.INVISIBLE);
                                                            extensionImage.setPathVisibility(Visibility.INVISIBLE);
//                                                            }
                                                        }

                                                        // Update the Port's style. Show the Port's Paths.
                                                        setVisibility(Visibility.VISIBLE);
                                                        setPathVisibility(port, Visibility.VISIBLE);
                                                        setDockVisibility(port, Visibility.INVISIBLE);

                                                        List<Path> paths = port.getCompletePath();
                                                        for (int i = 0; i < paths.size(); i++) {
                                                            Path path = paths.get(i);

                                                            // Show Ports
                                                            space.getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                            space.getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                                            // Show Path
                                                            space.getImage(path).setVisibility(Visibility.VISIBLE);
                                                        }

                                                        // <HACK>
                                                        // TODO: Put this code in Camera
                                                        // Camera
                                                        Group<Port> pathPorts = port.getPorts(paths);
                                                        ShapeGroup pathPortShapes = space.getShapes().filterEntity(pathPorts);
                                                        camera.adjustScale(pathPortShapes.getBoundingBox());
                                                        camera.setPosition(pathPortShapes.getCenterPosition());
                                                        // </HACK>

                                                    } else if (hasVisiblePaths(portIndex) || hasVisibleAncestorPaths(portIndex)) {

                                                        Log.v("TouchPort", "D");

                                                        // Paths are being shown. Touching a port changes the port type. This will also
                                                        // updates the corresponding path requirement.

                                                        Port.Type nextType = port.getType();
                                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                                            nextType = Port.Type.next(nextType);
                                                        }
                                                        port.setType(nextType);

                                                    }

                                                    space.setPrototypePathVisibility(Visibility.INVISIBLE);

                                                } else if (action.getFirstEvent().getTargetShape() != action.getLastEvent().getTargetShape()) {

                                                    // (Host.Port A, ..., Host.Port B) Action Pattern
                                                    // i.e., The action's first and last events address different ports.

                                                    Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                                    if (action.isDragging()) {

                                                        Log.v("Events", "B");

                                                        Port sourcePort = (Port) sourcePortShape.getEntity();
                                                        Port targetPort = null;

                                                        Shape targetPortShape = space.getShapes(Port.class).remove(sourcePortShape).filterContains(event.getPosition()).get(0);
                                                        targetPort = (Port) targetPortShape.getEntity();

                                                        if (targetPort == null) {

                                                            // targetPort is null, meaning that a target Port shape was not found

                                                            Log.v("Events", "C.1");

                                                            // Update source port configuration
                                                            if (sourcePort.getDirection() == Port.Direction.NONE) {
                                                                sourcePort.setDirection(Port.Direction.INPUT);
                                                            }
                                                            if (sourcePort.getType() == Port.Type.NONE) {
                                                                sourcePort.setType(Port.Type.next(sourcePort.getType()));
                                                            }

                                                        } else {

                                                            // targetPort is not null, meaning a target Port shape was found

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

                                                            if (!sourcePort.hasAncestor(targetPort)) {

                                                                Log.v("Events", "D.1");

                                                                // Create and configure new Path
                                                                Path path = new Path(sourcePort, targetPort);

                                                                if (sourcePort.getParent() instanceof Extension || targetPort.getParent() instanceof Extension) {
                                                                    path.setType(Path.Type.ELECTRONIC);
                                                                    targetPort.setType(sourcePort.getType());
                                                                } else {
                                                                    path.setType(Path.Type.MESH);
                                                                }

                                                                sourcePort.addPath(path);

                                                                space.addEntity(path);

                                                                // Get the just-created Extension Image
                                                                // PathImage pathImage = (PathImage) space.getImage(path);
                                                            }

                                                            // Camera
                                                            event.getActor().getCamera().focusSelectPath(sourcePort);
                                                        }

                                                        space.setPrototypePathVisibility(Visibility.INVISIBLE);

                                                    }

                                                }

                                            } else if (action.getLastEvent().getTargetShape() == null
                                                    // TODO: && action.getLastEvent().getTargetImage().getLabel().startsWith("Space")) {
                                                    && action.getLastEvent().getTargetImage() == space) {

                                                // (Host.Port, ..., Space) Action Pattern

                                                Log.v("Extension", "Creating Extension from Port");

                                                Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                                if (space.getPrototypeExtensionVisibility() == Visibility.VISIBLE) {

                                                    Log.v("IASM", "(1) touch extension to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

                                                    // Create the Extension
                                                    // TODO: Extension extension = new Extension(ExtensionProfile);
                                                    Extension extension = new Extension();

                                                    // TODO: Prompt to select extension to use! Then use that profile to create and configure ports for the extension.

                                                    // Create Ports and add them to the Extension
                                                    int extensionProfile_portCount = 1;
                                                    for (int j = 0; j < extensionProfile_portCount; j++) {
                                                        Port port = new Port();
                                                        extension.addPort(port);
                                                    }

                                                    // Add Extension to Model
                                                    space.getModel().addExtension(extension);

                                                    // Add Extension to Space
                                                    space.addEntity(extension);

                                                    // Get the just-created Extension Image
                                                    Image extensionImage = space.getImage(extension);

                                                    // Update the Extension Image position and rotation
                                                    extensionImage.setPosition(event.getPosition());

                                                    double extensionImageRotation = Geometry.calculateRotationAngle(
                                                            sourcePortShape.getPosition(),
                                                            extensionImage.getPosition()
                                                    );
                                                    extensionImage.setRotation(extensionImageRotation + 90);
//
                                                    // <HACK>
                                                    // Create Port shapes for each of Extension's Ports
                                                    for (int i = 0; i < extension.getPorts().size(); i++) {
                                                        Port port = extension.getPorts().get(i);

                                                        // Ports
                                                        Circle<Port> circle = new Circle<>(port);
                                                        circle.setRadius(40);
                                                        circle.setLabel("Port " + i + 1);
                                                        circle.setPosition(-90, 200);
                                                        // circle.setRelativeRotation(0);

                                                        circle.setColor("#efefef");
                                                        circle.setOutlineThickness(0);

                                                        circle.setVisibility(Visibility.INVISIBLE);

                                                        extensionImage.addShape(circle);
                                                    }
                                                    // </HACK>

                                                    // Configure Host's Port (i.e., the Path's source Port)
                                                    Port hostPort = (Port) sourcePortShape.getEntity();

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

                                                    // Add Path to Space
                                                    space.addEntity(path);

                                                    // Update Camera
                                                    camera.focusSelectPath(hostPort);

                                                }

                                                // Update Image
                                                space.setPrototypePathVisibility(Visibility.INVISIBLE);
                                                space.setPrototypeExtensionVisibility(Visibility.INVISIBLE);

                                            } else {

                                                // Get Port associated with the touched Port shape
                                                Port port = (Port) action.getFirstEvent().getTargetShape().getEntity();

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

                                                space.setPrototypePathVisibility(Visibility.INVISIBLE);
                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                                            if (action.isTap()) {

                                                // Focus on touched form
                                                setPathVisibility(Visibility.VISIBLE);
                                                getPortShapes().setVisibility(Visibility.VISIBLE);
                                                //setDockVisibility(Visibility.INVISIBLE);

                                                setTransparency(1.0);

                                                // Show ports and paths of touched form
                                                for (int i = 0; i < getHost().getPorts().size(); i++) {
                                                    List<Path> paths = getHost().getPort(i).getCompletePath();

                                                    for (int j = 0; j < paths.size(); j++) {
                                                        Path path = paths.get(j);

                                                        // Show source and target ports in path
                                                        //getPortShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                        space.getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                        space.getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                                        // Show path connection
                                                        space.getImage(path).setVisibility(Visibility.VISIBLE);
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
//                        camera.focusSelectSpace();
                                                        }

                                                    } else if (event.getTargetImage() instanceof Space) {

                                                        // Host
//                                                        action.getFirstEvent().getTargetImage().processAction(action);

                                                    }

                                                }

                                            }

                                            // Check if connecting to a extension
                                            if (space.getPrototypeExtensionVisibility() == Visibility.VISIBLE) {

                                                // Show extension store
                                                Launcher.getLauncherView().displayChooseDialog();
//                        Launcher.getLauncherView().displayTasksDialog();

                                                space.setPrototypeExtensionVisibility(Visibility.INVISIBLE);
                                            }
                                        }

                                    }
                                }
                            }

        );
    }

    public Host getHost() {
        return (Host) getEntity();
    }

    public void update() {

        // Get LED shapes
        ShapeGroup lightShapeGroup = getShapes().filterLabel("^LED (1[0-2]|[1-9])$");

        // Update Port and LED shape styles
        for (int i = 0; i < getEntity().getPorts().size(); i++) {
            Port port = getEntity().getPorts().get(i);
            Shape portShape = getShape(port);

            // Update color of Port shape based on type
            portShape.setColor(camp.computer.clay.space.util.Color.getColor(port.getType()));

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
        }
    }
}

