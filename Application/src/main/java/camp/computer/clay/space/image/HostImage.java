package camp.computer.clay.space.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.ui.Dialog;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Group;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.profile.ExtensionProfile;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.architecture.ImageGroup;
import camp.computer.clay.space.architecture.Shape;
import camp.computer.clay.space.architecture.ShapeGroup;
import camp.computer.clay.space.architecture.Space;
import camp.computer.clay.space.util.Probability;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Circle;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Line;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;

public class HostImage extends PortableImage {

    public HostImage(Host host)
    {
        super(host);
        setup();
    }

    private void setup()
    {
        setupShapes();
        setupActions();
    }

    private void setupShapes()
    {
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

        // Setup Ports
        List<Point> portCirclePositions = new ArrayList<>();
        portCirclePositions.add(new Point(-90, 200));
        portCirclePositions.add(new Point(0, 200));
        portCirclePositions.add(new Point(90, 200));
        portCirclePositions.add(new Point(200, 90));
        portCirclePositions.add(new Point(200, 0));
        portCirclePositions.add(new Point(200, -90));
        portCirclePositions.add(new Point(90, -200));
        portCirclePositions.add(new Point(0, -200));
        portCirclePositions.add(new Point(-90, -200));
        portCirclePositions.add(new Point(-200, -90));
        portCirclePositions.add(new Point(-200, 0));
        portCirclePositions.add(new Point(-200, 90));

        for (int i = 0; i < getPortable().getPorts().size(); i++) {

            // Circle
            circle = new Circle<>(getHost().getPort(i));
            circle.setRadius(40);
            circle.setLabel("Port " + (i + 1));
            circle.setPosition(portCirclePositions.get(i));
            // circle.setRelativeRotation(0);
            circle.setColor("#efefef");
            circle.setOutlineThickness(0);
            circle.setVisibility(Visibility.Value.INVISIBLE);
            addShape(circle);

            if (i < 3) {
                circle.setRotation(0);
            } else if (i < 6) {
                circle.setRotation(90);
            } else if (i < 9) {
                circle.setRotation(180);
            } else if (i < 12) {
                circle.setRotation(270);
            }

            // Line (Port Data Plot)
            /*
            Line line = new Line();
            addShape(line);
            line.setOrigin(circle.getPosition()); // Remove this? Weird to have a line with a center...
            line.setSource(new Point(-circle.getRadius(), 0, line.getPosition()));
            line.setTarget(new Point(circle.getRadius(), 0, line.getPosition()));
            line.setRotation(90);
            line.setOutlineColor("#ff000000");
            line.getVisibility().setReference(circle.getVisibility());
            */

            // TODO: Replace the lines with a Polyline/Plot(numPoints)/Plot(numSegments) w. source and destination and calculate paths to be equal lengths) + setData() function to map onto y axis points with most recent data
            Line previousLine = null;
            int segmentCount = 10;
            for (int j = 0; j < segmentCount; j++) {
                Line line = new Line();
                addShape(line);
                line.setOrigin(circle.getPosition()); // Remove this? Weird to have a line with a center...

                if (previousLine == null) {
                    line.setSource(new Point(-circle.getRadius(), 0, line.getPosition()));
                } else {
                    line.setSource(new Point(previousLine.getTarget().getRelativeX(), previousLine.getTarget().getRelativeY(), line.getPosition()));
                }
                if (j < (segmentCount - 1)) {
                    double segmentLength = (circle.getRadius() * 2) / segmentCount;
                    line.setTarget(new Point(line.getSource().getRelativeX() + segmentLength, Probability.generateRandomInteger(-(int) circle.getRadius(), (int) circle.getRadius()), line.getPosition()));

//                    Log.v("OnUpdate", "ADDING onUpdateListener");
//                    final Circle finalCircle = circle;
//                    line.setOnUpdateListener(new OnUpdateListener<Line>() {
//                        @Override
//                        public void onUpdate(Line line)
//                        {
//                            line.getTarget().setRelativeY(Probability.generateRandomInteger(-(int) finalCircle.getRadius(), (int) finalCircle.getRadius()));
//                        }
//                    });

                } else {
                    line.setTarget(new Point(circle.getRadius(), 0, line.getPosition()));
                }

                line.setRotation(90);
                line.setOutlineColor("#ff000000");
                line.setOutlineThickness(3.0);
                line.getVisibility().setReference(circle.getVisibility());

                previousLine = line;
            }
        }
    }

    private void setupActions()
    {
        setOnActionListener(new ActionListener() {
                                @Override
                                public void onAction(Action action)
                                {

                                    final Event event = action.getLastEvent();

                                    final Camera camera = event.getActor().getCamera();

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
//                                                event.getTargetImage().processAction(action);
                                                event.getTargetImage().setPosition(event.getPosition());

                                                // Camera
                                                camera.focusSelectHost(event);

                                            } else {


                                                // Update position
                                                // event.getTargetImage().setPosition(event.getPosition());

                                                space.setPrototypeExtensionPosition(event.getPosition());

                                                getPortShapes().setVisibility(Visibility.Value.INVISIBLE);
                                                setPathVisibility(Visibility.Value.INVISIBLE);
                                                setDockVisibility(Visibility.Value.VISIBLE);

                                                space.setPrototypeExtensionVisibility(Visibility.Value.VISIBLE);

                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                                            if (!action.isHolding()) {

                                                // Candidate Path Visibility
                                                space.setPrototypePathSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                                                space.setPrototypePathDestinationPosition(event.getPosition());
                                                space.setPrototypePathVisibility(Visibility.Value.VISIBLE);

                                                // Candidate Extension Visibility
                                                boolean isCreateExtensionAction = true;
                                                ImageGroup imageGroup = space.getImages(Host.class, Extension.class);
                                                for (int i = 0; i < imageGroup.size(); i++) {
                                                    Image otherImage = imageGroup.get(i);

                                                    // Update style of nearby Hosts
                                                    double distanceToHostImage = Geometry.calculateDistance(event.getPosition(), //candidatePathDestinationCoordinate,
                                                            otherImage.getPosition());

                                                    if (distanceToHostImage < 500) {
                                                        isCreateExtensionAction = false;
                                                        break;
                                                    }

                                                    // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                                                }

                                                if (isCreateExtensionAction) {
                                                    space.setPrototypeExtensionVisibility(Visibility.Value.VISIBLE);
                                                    space.setPrototypePathSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                                                    space.setPrototypeExtensionPosition(event.getPosition());
                                                } else {
                                                    space.setPrototypeExtensionVisibility(Visibility.Value.INVISIBLE);
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
                                                        PortableImage nearbyImage = (PortableImage) image;
                                                        nearbyImage.setTransparency(1.0f);
                                                        nearbyImage.getPortShapes().setVisibility(Visibility.Value.VISIBLE);

                                                        // TODO: Add additional port!
                                                        if (image instanceof ExtensionImage) {
                                                            Extension extension = ((ExtensionImage) image).getExtension();

                                                            boolean addPrototypePort = true;
                                                            for (int j = 0; j < extension.getPorts().size(); j++) {
                                                                Port existingPort = extension.getPorts().get(j);
                                                                if (existingPort.getType() == Port.Type.NONE) {
                                                                    addPrototypePort = false;
                                                                    break;
                                                                }
                                                            }

                                                            if (addPrototypePort) {
                                                                extension.addPort(new Port());
                                                            }
                                                        }

                                                        // </HACK>

                                                    } else {

                                                        // <HACK>
//                                                        if (image instanceof HostImage) {
                                                        PortableImage nearbyFigure = (PortableImage) image;
                                                        nearbyFigure.setTransparency(0.1f);
                                                        nearbyFigure.getPortShapes().setVisibility(Visibility.Value.INVISIBLE);
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

//                                        Log.v("Ixn", "firstTargetShape: " + action.getFirstEvent().getTargetShape().getLabel());
//                                        Log.v("Ixn", "lastTargetShape: " + action.getLastEvent().getTargetShape());
//                                        Log.v("Ixn", "lastTargetShape: " + action.getLastEvent().getTargetShape().getLabel());

                                        // <HACK>
                                        // TODO: Refactor so this doesn't have to be here! It's messy this way... standardize the way "null shapes" are handled
                                        if (action.getFirstEvent().getTargetShape() == null) {
                                            return;
                                        }
                                        // </HACK>

                                        if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                                            if (action.getLastEvent().getTargetShape() != null && action.getLastEvent().getTargetShape().getLabel().startsWith("Port")) {

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
                                                            hostImage.getPortShapes().setVisibility(Visibility.Value.INVISIBLE);
                                                            hostImage.setPathVisibility(Visibility.Value.INVISIBLE);
                                                            hostImage.setDockVisibility(Visibility.Value.VISIBLE);

                                                            // Get shapes in image matching labels "Board", "Header <number>", and "LED <number>"
                                                            ShapeGroup shapes = hostImage.getShapes().filterLabel("^Board$", "^Header (1|2|3|4)$", "^LED (1[0-2]|[1-9])$");
                                                            shapes.setTransparency(0.1);
                                                        }

                                                        ImageGroup extensionImages = space.getImages().filterType(Extension.class);
                                                        for (int i = 0; i < extensionImages.size(); i++) {
                                                            ExtensionImage extensionImage = (ExtensionImage) extensionImages.get(i);
//                                                            if (extensionImage.getExtension() != getParentImage().getEntity()) {
                                                            extensionImage.setTransparency(0.1);
                                                            getPortShapes().setVisibility(Visibility.Value.INVISIBLE);
                                                            extensionImage.setPathVisibility(Visibility.Value.INVISIBLE);
//                                                            }
                                                        }

                                                        // Update the Port's style. Show the Port's Paths.
                                                        setVisibility(Visibility.Value.VISIBLE);
                                                        setPathVisibility(port, Visibility.Value.VISIBLE);
                                                        setDockVisibility(port, Visibility.Value.INVISIBLE);

                                                        List<Path> paths = port.getCompletePath();
                                                        for (int i = 0; i < paths.size(); i++) {
                                                            Path path = paths.get(i);

                                                            // Show Ports
                                                            space.getShape(path.getSource()).setVisibility(Visibility.Value.VISIBLE);
                                                            space.getShape(path.getTarget()).setVisibility(Visibility.Value.VISIBLE);

                                                            // Show Path
                                                            space.getImage(path).setVisibility(Visibility.Value.VISIBLE);
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

                                                    space.setPrototypePathVisibility(Visibility.Value.INVISIBLE);

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

                                                        space.setPrototypePathVisibility(Visibility.Value.INVISIBLE);

                                                    }

                                                }

                                            } else if (action.getLastEvent().getTargetShape() == null
                                                    // TODO: && action.getLastEvent().getTargetImage().getLabel().startsWith("Space")) {
                                                    && action.getLastEvent().getTargetImage() == space)
                                            {

                                                // (Host.Port, ..., Space) Action Pattern

                                                Log.v("Extension", "Creating Extension from Port");

                                                Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();

                                                if (space.getPrototypeExtensionVisibility().getValue() == Visibility.Value.VISIBLE) {

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

                                                    double extensionImageRotation = Geometry.calculateRotationAngle(hostPortShape.getPosition(), extensionImage.getPosition());
                                                    extensionImage.setRotation(extensionImageRotation + 90);

                                                    // Configure Host's Port (i.e., the Path's source Port)
                                                    Port hostPort = (Port) hostPortShape.getEntity();

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
                                                space.setPrototypePathVisibility(Visibility.Value.INVISIBLE);
                                                space.setPrototypeExtensionVisibility(Visibility.Value.INVISIBLE);

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

                                                space.setPrototypePathVisibility(Visibility.Value.INVISIBLE);
                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                                            Log.v("Ixn", "got to board");

                                            if (action.isTap()) {

                                                // Focus on touched form
                                                setPathVisibility(Visibility.Value.VISIBLE);
                                                getPortShapes().setVisibility(Visibility.Value.VISIBLE);
                                                //setDockVisibility(Visibility.Value.INVISIBLE);

                                                setTransparency(1.0);

                                                // Show ports and paths of touched form
                                                for (int i = 0; i < getHost().getPorts().size(); i++) {
                                                    List<Path> paths = getHost().getPort(i).getCompletePath();

                                                    for (int j = 0; j < paths.size(); j++) {
                                                        Path path = paths.get(j);

                                                        // Show source and target ports in path
                                                        space.getShape(path.getSource()).setVisibility(Visibility.Value.VISIBLE);
                                                        space.getShape(path.getTarget()).setVisibility(Visibility.Value.VISIBLE);

                                                        // Show path connection
                                                        space.getImage(path).setVisibility(Visibility.Value.VISIBLE);
                                                    }
                                                }

                                                // Camera
                                                camera.focusSelectHost(event);

                                            } else {

                                                // TODO: Release longer than tap!

                                                if (event.getTargetImage() instanceof HostImage) {

                                                    // If getFirstEvent processAction was on the same form, then respond
                                                    if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage() instanceof HostImage) {

                                                        // Host
//                                                        event.getTargetImage().processAction(action);

                                                        // Camera
//                                                        camera.focusSelectSpace();
                                                    }

                                                } else if (event.getTargetImage() instanceof Space) {

                                                    // Host
//                                                        action.getFirstEvent().getTargetImage().processAction(action);

                                                }

                                            }

                                            // Check if connecting to a extension
                                            if (space.getPrototypeExtensionVisibility().getValue() == Visibility.Value.VISIBLE) {

                                                space.setPrototypeExtensionVisibility(Visibility.Value.INVISIBLE);

                                                // Get cached extension profiles (and retrieve additional from Internet store)
                                                List<ExtensionProfile> extensionProfiles = Launcher.getLauncherView().getClay().getExtensionProfiles();


                                                if (extensionProfiles.size() == 0) {

                                                    // Show "default" DIY extension builder (or info about there being no extensions)

                                                } else if (extensionProfiles.size() > 0) {

                                                    // Show Extension store and get selection from user
                                                    Launcher.getLauncherView().getUi().promptSelection(extensionProfiles, new Dialog.OnCompleteCallback<ExtensionProfile>() {
                                                        @Override
                                                        public void onComplete(ExtensionProfile extensionProfile)
                                                        {

                                                            Log.v("IASM", "(1) touch extension to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

                                                            // Create the Extension
                                                            final Extension extension = new Extension(extensionProfile);

                                                            // TODO: Prompt to select extension to use! Then use that profile to create and configure ports for the extension.

                                                            /*
                                                            // Create Ports and add them to the Extension
                                                            int extensionProfile_portCount = 1;
                                                            for (int j = 0; j < extensionProfile_portCount; j++) {
                                                                Port port = new Port();
                                                                extension.addPort(port);
                                                            }
                                                            */

                                                            // Add Extension to Model
                                                            space.getModel().addExtension(extension);

                                                            // Add Extension to Space
                                                            space.addEntity(extension);

                                                            // Get the just-created Extension Image
                                                            Image extensionImage = space.getImage(extension);

                                                            // Update the Extension Image position and rotation
                                                            extensionImage.setPosition(event.getPosition());

                                                            double extensionImageRotation = Geometry.calculateRotationAngle(getPosition(), extensionImage.getPosition());
                                                            extensionImage.setRotation(extensionImageRotation + 90);

                                                        /*
                                                        // Configure Host's Port (i.e., the Path's source Port)
                                                        Port hostPort = (Port) hostPortShape.getEntity();

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
                                                        */

                                                        }
                                                    });
                                                    // Launcher.getLauncherView().promptTasks();
                                                }
                                            }
                                        }

                                    }
                                }
                            }

        );
    }

    public Host getHost()
    {
        return (Host) getEntity();
    }

    public void update()
    {
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


        // <HACK>
        // TODO: Move into Shape base class
        for (int i = 0; i < shapes.size(); i++) {
            //Log.v("OnUpdate", "Image.update " + shapes.size());
            shapes.get(i).update();
        }
        // </HACK>
    }

    public void draw(Display display)
    {
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

