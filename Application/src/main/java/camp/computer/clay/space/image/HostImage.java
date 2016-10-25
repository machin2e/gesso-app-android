package camp.computer.clay.space.image;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.Clay;
import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.Extension;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Host;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Vertex;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

public class HostImage extends PortableImage {

    public List<List<Extension>> headerExtensions = new ArrayList<>();

    public HostImage(Host host) {
        super(host);
        setup();
    }

    private void setup() {
        setupGeometry();
//        setupActionListener();

        headerExtensions.add(new ArrayList<Extension>());
        headerExtensions.add(new ArrayList<Extension>());
        headerExtensions.add(new ArrayList<Extension>());
        headerExtensions.add(new ArrayList<Extension>());
    }

    // TODO:
    private void setupGeometry() {
        //Application.getView().restoreGeometry(getEntity().getComponent(Image.class), "Geometry.json");
        Application.getView().restoreGeometry(this, "Geometry.json");
        for (int i = 0; i < shapes.size(); i++) {

            // <HACK>
            if (shapes.get(i).getLabel().startsWith("Port")) {
                String label = shapes.get(i).getLabel();
                Port port = getHost().getPort(label);
                shapes.get(i).setEntity(port);
            }
            // </HACK>
        }
    }

    private void setupGeometry2() {
        Rectangle rectangle;
        Circle circle;

        double mmScaleFactor = 6.0;

        final double boardWidth = 50.8 * mmScaleFactor;
        final double boardCornerRadius = 4.064 * mmScaleFactor;

        final double headerSize = 3; // Vary this for each header, generally
        final double headerOffsetFromCenter = 26.5 * mmScaleFactor; // -132
        final double headerWidth = (2.54 * headerSize) * mmScaleFactor; // 6.0 * (2.54 * 3)
        final double headerHeight = 2.33 * mmScaleFactor;

        final double contactSeparation = 2.54 * mmScaleFactor;

        final double portCircleOffsetFromCenter = 40.0 * mmScaleFactor;
        final double portCircleSeparationDistance = 19.0  * mmScaleFactor;
        final double portCircleRadius = 8.33 * mmScaleFactor;

        final double lightOffsetFromCenter = 21.0 * mmScaleFactor; // 105
        final double lightSeparationDistance = 3.33 * mmScaleFactor;

        final double holeDiameter = 2.9 * mmScaleFactor; // 2.9 mm diameter
        final double holeRadius = holeDiameter / 2.0; // 2.9 mm diameter
        final double holeOffsetFromCenter = (boardWidth / 2.0) - (mmScaleFactor * 3.5); // 125 - (6.0 * 3.5)

        // Board
        rectangle = new Rectangle(boardWidth, boardWidth);
        rectangle.setWidth(boardWidth); // 250px
        rectangle.setHeight(boardWidth); // 250px
        rectangle.setCornerRadius(boardCornerRadius); // 20.0
        rectangle.setLabel("Board");
        rectangle.setColor("#1f1f1e"); // #f7f7f7
        rectangle.setOutlineThickness(1);
        addShape(rectangle);

        // Headers
        rectangle = new Rectangle(headerWidth, headerHeight); // 14
        rectangle.setLabel("Header 1"); // or index 1 (top)
        rectangle.setPosition(0, -headerOffsetFromCenter);
        rectangle.setRotation(0);
        rectangle.setColor("#404040");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(headerWidth, headerHeight);
        rectangle.setLabel("Header 2"); // or index 2 (right)
        rectangle.setPosition(headerOffsetFromCenter, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#404040");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(headerWidth, headerHeight);
        rectangle.setLabel("Header 3"); // or index 3 (bottom)
        rectangle.setPosition(0, headerOffsetFromCenter);
        rectangle.setRotation(180);
        rectangle.setColor("#404040"); // #3b3b3b
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(headerWidth, headerHeight);
        rectangle.setLabel("Header 4"); // or index 4 (left)
        rectangle.setPosition(-headerOffsetFromCenter, 0);
        rectangle.setRotation(270);
        rectangle.setColor("#404040");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        headerContactPositions.add(new Vertex(new Transform(-contactSeparation, headerOffsetFromCenter)));
        headerContactPositions.add(new Vertex(new Transform(0, headerOffsetFromCenter)));
        headerContactPositions.add(new Vertex(new Transform(contactSeparation, headerOffsetFromCenter)));

        headerContactPositions.add(new Vertex(new Transform(headerOffsetFromCenter, contactSeparation)));
        headerContactPositions.add(new Vertex(new Transform(headerOffsetFromCenter, 0)));
        headerContactPositions.add(new Vertex(new Transform(headerOffsetFromCenter, -contactSeparation)));

        headerContactPositions.add(new Vertex(new Transform(contactSeparation, -headerOffsetFromCenter)));
        headerContactPositions.add(new Vertex(new Transform(0, -headerOffsetFromCenter)));
        headerContactPositions.add(new Vertex(new Transform(-contactSeparation, -headerOffsetFromCenter)));

        headerContactPositions.add(new Vertex(new Transform(-headerOffsetFromCenter, -contactSeparation)));
        headerContactPositions.add(new Vertex(new Transform(-headerOffsetFromCenter, 0)));
        headerContactPositions.add(new Vertex(new Transform(-headerOffsetFromCenter, contactSeparation)));

        for (int i = 0; i < headerContactPositions.size(); i++) {
            Log.v("Dimensions", "header contact " + i + ": " + headerContactPositions.get(i).getPosition().x + ", " + headerContactPositions.get(i).getPosition().y);
            addShape(headerContactPositions.get(i));
        }

        // Lights
        List<Transform> lightPositions = new ArrayList<>();
        lightPositions.add(new Transform(-lightSeparationDistance, lightOffsetFromCenter));
        lightPositions.add(new Transform(0, lightOffsetFromCenter));
        lightPositions.add(new Transform(lightSeparationDistance, lightOffsetFromCenter));
        lightPositions.add(new Transform(lightOffsetFromCenter, lightSeparationDistance));
        lightPositions.add(new Transform(lightOffsetFromCenter, 0));
        lightPositions.add(new Transform(lightOffsetFromCenter, -lightSeparationDistance));
        lightPositions.add(new Transform(lightSeparationDistance, -lightOffsetFromCenter));
        lightPositions.add(new Transform(0, -lightOffsetFromCenter));
        lightPositions.add(new Transform(-lightSeparationDistance, -lightOffsetFromCenter));
        lightPositions.add(new Transform(-lightOffsetFromCenter, -lightSeparationDistance));
        lightPositions.add(new Transform(-lightOffsetFromCenter, 0));
        lightPositions.add(new Transform(-lightOffsetFromCenter, lightSeparationDistance));

        List<Double> lightRotations = new ArrayList<>();
        lightRotations.add(0.0);
        lightRotations.add(0.0);
        lightRotations.add(0.0);
        lightRotations.add(90.0);
        lightRotations.add(90.0);
        lightRotations.add(90.0);
        lightRotations.add(180.0);
        lightRotations.add(180.0);
        lightRotations.add(180.0);
        lightRotations.add(270.0);
        lightRotations.add(270.0);
        lightRotations.add(270.0);

        for (int i = 0; i < lightPositions.size(); i++) {
            rectangle = new Rectangle(12, 20);
            rectangle.setPosition(lightPositions.get(i));
            rectangle.setRotation(lightRotations.get(i));
            rectangle.setCornerRadius(3.0);
            rectangle.setLabel("LED " + (i + 1));
            addShape(rectangle);
            Log.v("Dimensions", "light " + i + ": " + lightPositions.get(i).x + ", " + lightPositions.get(i).y);
        }

        // Mounting Holes
        List<Transform> mountingHolePositions = new ArrayList<>();
        mountingHolePositions.add(new Transform(-holeOffsetFromCenter, -holeOffsetFromCenter)); // TODO: make hole centers 5 mm (or so) from the edge of the PCB
        mountingHolePositions.add(new Transform(holeOffsetFromCenter, -holeOffsetFromCenter));
        mountingHolePositions.add(new Transform(holeOffsetFromCenter, holeOffsetFromCenter));
        mountingHolePositions.add(new Transform(-holeOffsetFromCenter, holeOffsetFromCenter));

        for (int i = 0; i < mountingHolePositions.size(); i++) {
            circle = new Circle<>(holeRadius);
            circle.setPosition(mountingHolePositions.get(i));
            circle.setLabel("Mount " + (i + 1));
            circle.setColor("#ffffff");
            circle.setOutlineThickness(0);
//            circle.getVisibility().setReference(getShapes("Board").getVisibility());
            addShape(circle);
            Log.v("Dimensions", "hole " + i + ": " + mountingHolePositions.get(i).x + ", " + mountingHolePositions.get(i).y);
        }

        // Setup Ports
        List<Transform> portCirclePositions = new ArrayList<>();
        portCirclePositions.add(new Transform(-portCircleSeparationDistance, portCircleOffsetFromCenter));
        portCirclePositions.add(new Transform(0, portCircleOffsetFromCenter));
        portCirclePositions.add(new Transform(portCircleSeparationDistance, portCircleOffsetFromCenter));
        portCirclePositions.add(new Transform(portCircleOffsetFromCenter, portCircleSeparationDistance));
        portCirclePositions.add(new Transform(portCircleOffsetFromCenter, 0));
        portCirclePositions.add(new Transform(portCircleOffsetFromCenter, -portCircleSeparationDistance));
        portCirclePositions.add(new Transform(portCircleSeparationDistance, -portCircleOffsetFromCenter));
        portCirclePositions.add(new Transform(0, -portCircleOffsetFromCenter));
        portCirclePositions.add(new Transform(-portCircleSeparationDistance, -portCircleOffsetFromCenter));
        portCirclePositions.add(new Transform(-portCircleOffsetFromCenter, -portCircleSeparationDistance));
        portCirclePositions.add(new Transform(-portCircleOffsetFromCenter, 0));
        portCirclePositions.add(new Transform(-portCircleOffsetFromCenter, portCircleSeparationDistance));

        for (int i = 0; i < getPortable().getPorts().size(); i++) {

            // Circle
            circle = new Circle<>(getHost().getPort(i));
            circle.setLabel("Port " + (i + 1));
            circle.setPosition(portCirclePositions.get(i));
            circle.setRadius(portCircleRadius);
            // circle.setRotation(0);
            circle.setColor("#efefef");
            circle.setOutlineThickness(0);
            circle.setVisibility(Visibility.INVISIBLE);
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

            // Segment (Port Data Plot)
            /*
            Segment line = new Segment();
            addShape(line);
            line.setReferencePoint(circle.getPosition()); // Remove this? Weird to have a line with a center...
            line.setSource(new Transform(-circle.getRadius(), 0, line.getPosition()));
            line.setTarget(new Transform(circle.getRadius(), 0, line.getPosition()));
            line.setRotation(90);
            line.setOutlineColor("#ff000000");
            line.getVisibility().setReferencePoint(circle.getVisibility());
            */

            /*
            // TODO: Replace the lines with a Polyline/Plot(numPoints)/Plot(numSegments) w. source and destination and calculate paths to be equal lengths) + setData() function to map onto y axis endpoints with most recent data
            Segment previousLine = null;
            int segmentCount = 10;
            for (int j = 0; j < segmentCount; j++) {
                Segment line = new Segment();
                addShape(line);
                line.setReferencePoint(circle.getPosition()); // Remove this? Weird to have a line with a center...

                if (previousLine == null) {
                    line.setSource(new Transform(-circle.getRadius(), 0, line.getPosition()));
                } else {
                    line.setSource(new Transform(previousLine.getTarget().getX(), previousLine.getTarget().getY(), line.getPosition()));
                }
                if (j < (segmentCount - 1)) {
                    double segmentLength = (circle.getRadius() * 2) / segmentCount;
                    line.setTarget(new Transform(line.getSource().getX() + segmentLength, Probability.generateRandomInteger(-(int) circle.getRadius(), (int) circle.getRadius()), line.getPosition()));

//                    Log.v("OnUpdate", "ADDING onUpdateListener");
//                    final Circle finalCircle = circle;
//                    line.setOnUpdateListener(new OnUpdateListener<Segment>() {
//                        @Override
//                        public void onUpdate(Segment line)
//                        {
//                            line.getTarget().setY(Probability.generateRandomInteger(-(int) finalCircle.getRadius(), (int) finalCircle.getRadius()));
//                        }
//                    });

                } else {
                    line.setTarget(new Transform(circle.getRadius(), 0, line.getPosition()));
                }

                line.setRotation(90);
                line.setOutlineColor("#ff000000");
                line.setOutlineThickness(3.0);
                line.getVisibility().setReferencePoint(circle.getVisibility());

                previousLine = line;
            }
            */
        }
    }

    public static void createHostImage(Host host) {

        Image hostImage = new Image();

        Rectangle rectangle;
        Circle circle;

        // Board
        rectangle = new Rectangle<>(host);
        rectangle.setWidth(250);
        rectangle.setHeight(250);
        rectangle.setCornerRadius(20.0);
        rectangle.setLabel("Board");
        rectangle.setColor("#1f1f1e"); // #f7f7f7
        rectangle.setOutlineThickness(1);
        hostImage.addShape(rectangle);

        // Headers
        final double headerWidth = 6.0 * (2.54 * 3);

        rectangle = new Rectangle(headerWidth, 14);
        rectangle.setLabel("Header 1"); // or index 1 (top)
        rectangle.setPosition(0, -132);
        rectangle.setRotation(0);
        rectangle.setColor("#404040");
        rectangle.setOutlineThickness(0);
        hostImage.addShape(rectangle);

        rectangle = new Rectangle(headerWidth, 14);
        rectangle.setLabel("Header 2"); // or index 2 (right)
        rectangle.setPosition(132, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#404040");
        rectangle.setOutlineThickness(0);
        hostImage.addShape(rectangle);

        rectangle = new Rectangle(headerWidth, 14);
        rectangle.setLabel("Header 3"); // or index 3 (bottom)
        rectangle.setPosition(0, 132);
        rectangle.setRotation(0);
        rectangle.setColor("#404040"); // #3b3b3b
        rectangle.setOutlineThickness(0);
        hostImage.addShape(rectangle);

        rectangle = new Rectangle(headerWidth, 14);
        rectangle.setLabel("Header 4"); // or index 4 (left)
        rectangle.setPosition(-132, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#404040");
        rectangle.setOutlineThickness(0);
        hostImage.addShape(rectangle);

        final double contactSeparation = 6.0 * 2.54;

        List<Vertex> headerContactPositions = new ArrayList<>();

        headerContactPositions.add(new Vertex(new Transform(-contactSeparation, 132)));
        headerContactPositions.add(new Vertex(new Transform(0, 132)));
        headerContactPositions.add(new Vertex(new Transform(contactSeparation, 132)));

        headerContactPositions.add(new Vertex(new Transform(132, contactSeparation)));
        headerContactPositions.add(new Vertex(new Transform(132, 0)));
        headerContactPositions.add(new Vertex(new Transform(132, -contactSeparation)));

        headerContactPositions.add(new Vertex(new Transform(contactSeparation, -132)));
        headerContactPositions.add(new Vertex(new Transform(0, -132)));
        headerContactPositions.add(new Vertex(new Transform(-contactSeparation, -132)));

        headerContactPositions.add(new Vertex(new Transform(-132, -contactSeparation)));
        headerContactPositions.add(new Vertex(new Transform(-132, 0)));
        headerContactPositions.add(new Vertex(new Transform(-132, contactSeparation)));

        for (int i = 0; i < headerContactPositions.size(); i++) {
            hostImage.addShape(headerContactPositions.get(i));
        }

        // Lights
        List<Transform> lightPositions = new ArrayList<>();
        lightPositions.add(new Transform(-20, 105));
        lightPositions.add(new Transform(0, 105));
        lightPositions.add(new Transform(20, 105));
        lightPositions.add(new Transform(105, 20));
        lightPositions.add(new Transform(105, 0));
        lightPositions.add(new Transform(105, -20));
        lightPositions.add(new Transform(20, -105));
        lightPositions.add(new Transform(0, -105));
        lightPositions.add(new Transform(-20, -105));
        lightPositions.add(new Transform(-105, -20));
        lightPositions.add(new Transform(-105, 0));
        lightPositions.add(new Transform(-105, 20));

        List<Double> lightRotations = new ArrayList<>();
        lightRotations.add(0.0);
        lightRotations.add(0.0);
        lightRotations.add(0.0);
        lightRotations.add(90.0);
        lightRotations.add(90.0);
        lightRotations.add(90.0);
        lightRotations.add(180.0);
        lightRotations.add(180.0);
        lightRotations.add(180.0);
        lightRotations.add(270.0);
        lightRotations.add(270.0);
        lightRotations.add(270.0);

        for (int i = 0; i < lightPositions.size(); i++) {
            rectangle = new Rectangle(12, 20);
            rectangle.setPosition(lightPositions.get(i));
            rectangle.setRotation(lightRotations.get(i));
            rectangle.setCornerRadius(3.0);
            rectangle.setLabel("LED " + (i + 1));
            hostImage.addShape(rectangle);
        }

        // Mounting Holes
        final double holeDiameter = 6.0 * 2.9; // 2.9 mm diameter
        final double holeRadius = holeDiameter / 2.0; // 2.9 mm diameter
        final double holeDistanceFromEdge = 125 - (6.0 * 3.5);

        List<Transform> mountingHolePositions = new ArrayList<>();
        mountingHolePositions.add(new Transform(-holeDistanceFromEdge, -holeDistanceFromEdge)); // TODO: make hole centers 5 mm (or so) from the edge of the PCB
        mountingHolePositions.add(new Transform(holeDistanceFromEdge, -holeDistanceFromEdge));
        mountingHolePositions.add(new Transform(holeDistanceFromEdge, holeDistanceFromEdge));
        mountingHolePositions.add(new Transform(-holeDistanceFromEdge, holeDistanceFromEdge));

        for (int i = 0; i < mountingHolePositions.size(); i++) {
            circle = new Circle<>(holeRadius);
            circle.setPosition(mountingHolePositions.get(i));
            circle.setLabel("Mount " + (i + 1));
            circle.setColor("#ffffff");
            circle.setOutlineThickness(0);
//            circle.getVisibility().setReference(getShapes("Board").getVisibility());
            hostImage.addShape(circle);
        }

        // Setup Ports
        List<Transform> portCirclePositions = new ArrayList<>();
        portCirclePositions.add(new Transform(-90, 200));
        portCirclePositions.add(new Transform(0, 200));
        portCirclePositions.add(new Transform(90, 200));
        portCirclePositions.add(new Transform(200, 90));
        portCirclePositions.add(new Transform(200, 0));
        portCirclePositions.add(new Transform(200, -90));
        portCirclePositions.add(new Transform(90, -200));
        portCirclePositions.add(new Transform(0, -200));
        portCirclePositions.add(new Transform(-90, -200));
        portCirclePositions.add(new Transform(-200, -90));
        portCirclePositions.add(new Transform(-200, 0));
        portCirclePositions.add(new Transform(-200, 90));

        for (int i = 0; i < host.getPorts().size(); i++) {

            // Circle
            circle = new Circle<>(host.getPort(i));
            circle.setLabel("Port " + (i + 1));
            circle.setPosition(portCirclePositions.get(i));
            circle.setRadius(40);
            // circle.setRotation(0);
            circle.setColor("#efefef");
            circle.setOutlineThickness(0);
            circle.setVisibility(Visibility.INVISIBLE);
            hostImage.addShape(circle);

            if (i < 3) {
                circle.setRotation(0);
            } else if (i < 6) {
                circle.setRotation(90);
            } else if (i < 9) {
                circle.setRotation(180);
            } else if (i < 12) {
                circle.setRotation(270);
            }

            // Segment (Port Data Plot)
            /*
            Segment line = new Segment();
            addShape(line);
            line.setReferencePoint(circle.getPosition()); // Remove this? Weird to have a line with a center...
            line.setSource(new Transform(-circle.getRadius(), 0, line.getPosition()));
            line.setTarget(new Transform(circle.getRadius(), 0, line.getPosition()));
            line.setRotation(90);
            line.setOutlineColor("#ff000000");
            line.getVisibility().setReferencePoint(circle.getVisibility());
            */

            /*
            // TODO: Replace the lines with a Polyline/Plot(numPoints)/Plot(numSegments) w. source and destination and calculate paths to be equal lengths) + setData() function to map onto y axis endpoints with most recent data
            Segment previousLine = null;
            int segmentCount = 10;
            for (int j = 0; j < segmentCount; j++) {
                Segment line = new Segment();
                addShape(line);
                line.setReferencePoint(circle.getPosition()); // Remove this? Weird to have a line with a center...

                if (previousLine == null) {
                    line.setSource(new Transform(-circle.getRadius(), 0, line.getPosition()));
                } else {
                    line.setSource(new Transform(previousLine.getTarget().getX(), previousLine.getTarget().getY(), line.getPosition()));
                }
                if (j < (segmentCount - 1)) {
                    double segmentLength = (circle.getRadius() * 2) / segmentCount;
                    line.setTarget(new Transform(line.getSource().getX() + segmentLength, Probability.generateRandomInteger(-(int) circle.getRadius(), (int) circle.getRadius()), line.getPosition()));

//                    Log.v("OnUpdate", "ADDING onUpdateListener");
//                    final Circle finalCircle = circle;
//                    line.setOnUpdateListener(new OnUpdateListener<Segment>() {
//                        @Override
//                        public void onUpdate(Segment line)
//                        {
//                            line.getTarget().setY(Probability.generateRandomInteger(-(int) finalCircle.getRadius(), (int) finalCircle.getRadius()));
//                        }
//                    });

                } else {
                    line.setTarget(new Transform(circle.getRadius(), 0, line.getPosition()));
                }

                line.setRotation(90);
                line.setOutlineColor("#ff000000");
                line.setOutlineThickness(3.0);
                line.getVisibility().setReferencePoint(circle.getVisibility());

                previousLine = line;
            }
            */
        }
    }

    /**
     * Creates a new {@code Extension} connected to {@hostPort}.
     *
     * @param hostPort
     */
    public Extension createExtension(Port hostPort, Transform initialPosition) {

        // TODO: Remove initialPosition... find the position by analyzing the geometry of the HostImage

        //Log.v("Extension", "Creating Extension from Port");

        //Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();
//        Shape hostPortShape = getShapes(hostPort);

        //Log.v("IASM", "(1) touch extension to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // TODO: Prompt to select extension to use! Then use that profile to create and configure ports for the extension.

        // Create Extension Entity
        UUID extensionUuid = Clay.createEntity(Extension.class);
        Extension extension = (Extension) Entity.Manager.get(extensionUuid);

        // Set the initial position of the Extension
        extension.getComponent(Transform.class).set(initialPosition);

        // Configure Host's Port (i.e., the Path's source Port)
        if (hostPort.getType() == Port.Type.NONE || hostPort.getDirection() == Port.Direction.NONE) {
            hostPort.setType(Port.Type.POWER_REFERENCE); // Set the default type to reference (ground)
            hostPort.setDirection(Port.Direction.BOTH);
        }

        // Configure Extension's Ports (i.e., the Path's target Port)
        Port extensionPort = extension.getPorts().get(0);
        extensionPort.setDirection(Port.Direction.INPUT);
        extensionPort.setType(hostPort.getType());

        // Create Path from Host to Extension and configure the new Path
        UUID pathUuid = Clay.createEntity(Path.class);
        Path path = (Path) Entity.getEntity(pathUuid);
        path.set(hostPort, extensionPort);

        // Remove focus from other Hosts and their Ports
        Group<Image> hostImages = Entity.Manager.filterType2(Host.class).getImages();
        for (int i = 0; i < hostImages.size(); i++) {
            HostImage hostImage = (HostImage) hostImages.get(i);
            hostImage.setTransparency(0.05f);
            hostImage.getPortShapes().setVisibility(Visibility.INVISIBLE);
            hostImage.setPathVisibility(Visibility.INVISIBLE);
        }

        // Show Path and all contained Ports
        Group<Path> paths = hostPort.getPaths();
        Group<Port> pathPorts = new Group<>();
        for (int i = 0; i < paths.size(); i++) {
            pathPorts.addAll(paths.get(i).getPorts());
        }

        pathPorts.setVisibility(Visibility.VISIBLE);
        paths.getImages().setVisibility(Visibility.VISIBLE);

        // Update layout
        updateExtensionLayout();

        return extension;
    }

    /**
     * Adds and existing {@code Extension}.
     *
     * @param profile
     * @param initialPosition
     * @return
     */
    public Extension restoreExtension(Profile profile, Transform initialPosition) {
        // NOTE: Previously called fetchExtension(...)

        // Log.v("IASM", "(1) touch extension to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // Create the Extension
        Extension extension = new Extension(profile);

        // Update Extension Position
        extension.getComponent(Transform.class).set(initialPosition);

        // Automatically select and connect all Paths to Host
        autoConnectToHost(extension);

        // TODO: Start IASM based on automatically configured Paths to Host.

        updateExtensionLayout();

        return extension;
    }

    private boolean autoConnectToHost(Extension extension) {

        // Automatically select, connect paths to, and configure the Host's Ports
        for (int i = 0; i < extension.getPorts().size(); i++) {

            // Select an available Host Port
            Port selectedHostPort = autoSelectNearestAvailableHostPort(extension);

            // Configure Host's Port
            selectedHostPort.setType(extension.getPorts().get(i).getType());
            selectedHostPort.setDirection(extension.getPorts().get(i).getDirection());

            // Create Path from Extension Port to Host Port
            UUID pathUuid = Clay.createEntity(Path.class);
            Path path = (Path) Entity.getEntity(pathUuid);
            path.set(selectedHostPort, extension.getPorts().get(i));

            path.setType(Path.Type.ELECTRONIC);
        }

        return true;
    }

    private Port autoSelectNearestAvailableHostPort(Extension extension) {
        // Select an available Host Port
        Port selectedHostPort = null;
        double distanceToSelectedPort = Double.MAX_VALUE;
        for (int j = 0; j < getHost().getPorts().size(); j++) {
            if (getHost().getPorts().get(j).getType() == Port.Type.NONE) {

                double distanceToPort = Geometry.distance(
                        getPortShapes().filterEntity(getHost().getPorts().get(j)).get(0).getPosition(),
                        extension.getComponent(Image.class).getEntity().getComponent(Transform.class)
                );

                // Check if the port is the nearest
                if (distanceToPort < distanceToSelectedPort) {
                    selectedHostPort = getHost().getPorts().get(j);
                    distanceToSelectedPort = distanceToPort;
                }
            }
        }
        // TODO: selectedHostPort = (Port) getPortShapes().getNearestImage(extensionImage.getPosition()).getEntity();
        return selectedHostPort;
    }

    // TODO: Remove this?
    public int getHeaderIndex(Extension extension) {

        int[] indexCounts = new int[4];
        for (int i = 0; i < indexCounts.length; i++) {
            indexCounts[i] = 0;
        }

        Shape boardShape = getShape("Board");
        List<Transform> hostShapeBoundary = boardShape.getBoundary();

        Group<Port> extensionPorts = extension.getPorts();
        for (int j = 0; j < extensionPorts.size(); j++) {

            Port extensionPort = extensionPorts.get(j);

            if (extensionPort == null || extensionPort.getPaths().size() == 0 || extensionPort.getPaths().get(0) == null) {
                continue;
            }

            Port hostPort = extensionPort.getPaths().get(0).getHostPort(); // HACK b/c using index 0
            Transform hostPortPosition = Space.getSpace().getShape(hostPort).getPosition();

            double minimumSegmentDistance = Double.MAX_VALUE; // Stores the distance to the nearest segment
            int nearestSegmentIndex = 0; // Stores the index of the nearest segment (on the connected Host)
            for (int i = 0; i < hostShapeBoundary.size() - 1; i++) {

                Transform segmentMidpoint = Geometry.midpoint(hostShapeBoundary.get(i), hostShapeBoundary.get(i + 1));

                double distance = Geometry.distance(hostPortPosition, segmentMidpoint);

                if (distance < minimumSegmentDistance) {
                    minimumSegmentDistance = distance;
                    nearestSegmentIndex = i;
                }
            }

            indexCounts[nearestSegmentIndex]++;
        }

        // Get the segment with the most counts
        int segmentIndex = -1;
        segmentIndex = 0;
        for (int i = 0; i < indexCounts.length; i++) {
            if (indexCounts[i] > indexCounts[segmentIndex]) {
                segmentIndex = i;
            }
        }

        return segmentIndex;
    }

    public Host getHost() {
        return (Host) getEntity();
    }

    public ShapeGroup lightShapeGroup = null;

    public void update() {

        // Get LED shapes
        if (lightShapeGroup == null) {
            lightShapeGroup = getShapes().filterLabel("^LED (1[0-2]|[1-9])$");
        }

        // Update Port and LED shape styles
        for (int i = 0; i < getHost().getPorts().size(); i++) {
            Port port = getHost().getPorts().get(i);
            Shape portShape = getShape(port.getLabel()); // Shape portShape = getShape(port);

            // Update color of Port shape based on type
            portShape.setColor(camp.computer.clay.util.Color.getColor(port.getType()));

            // Update color of LED based on corresponding Port's type
            lightShapeGroup.get(i).setColor(portShape.getColor());
        }

        super.update();
    }

    protected double distanceToExtensions = 500;

    public void setExtensionDistance(double distance) {
        distanceToExtensions = distance;
        updateExtensionLayout();
    }

    public void updateExtensionLayout() {

        // Get Extensions connected to the Host.
        Group<Extension> extensions = getHost().getExtensions();

        // Reset current layout in preparation for updating it in the presently-running update step.
        for (int i = 0; i < headerExtensions.size(); i++) {
            headerExtensions.get(i).clear();
        }

        // Assign the Extensions connected to this Host to the most-strongly-connected Header.
        // This can be thought of as the "high level layout" of Extension relative to the Host.
        for (int i = 0; i < extensions.size(); i++) {
            Extension extension = extensions.get(i);
            updateExtensionHeaderIndex(extension);
        }

        // Update each Extension's placement, relative to the connected Host.
        for (int headerIndex = 0; headerIndex < headerExtensions.size(); headerIndex++) {
            for (int extensionIndex = 0; extensionIndex < headerExtensions.get(headerIndex).size(); extensionIndex++) {

                Extension extension = headerExtensions.get(headerIndex).get(extensionIndex);

                final double extensionSeparationDistance = 25.0;
                double extensionWidth = 200;
                int extensionCount = headerExtensions.get(headerIndex).size();
                double offset = extensionIndex * 250 - (((extensionCount - 1) * (extensionWidth + extensionSeparationDistance)) / 2.0);

                // Update the Extension's position.
                if (headerIndex == 0) {
                    extension.getComponent(Transform.class).set(
                            0 + offset,
                            -distanceToExtensions,
                            entity.getComponent(Transform.class)
                    );
                } else if (headerIndex == 1) {
                    extension.getComponent(Transform.class).set(
                            distanceToExtensions,
                            0 + offset,
                            entity.getComponent(Transform.class)
                    );
                } else if (headerIndex == 2) {
                    extension.getComponent(Transform.class).set(
                            0 + offset,
                            distanceToExtensions,
                            entity.getComponent(Transform.class)
                    );
                } else if (headerIndex == 3) {
                    extension.getComponent(Transform.class).set(
                            -distanceToExtensions,
                            0 + offset,
                            entity.getComponent(Transform.class)
                    );
                }

                // Update the Extension's rotation.
                double hostEntityRotation = getEntity().getComponent(Transform.class).getRotation();
                if (headerIndex == 0) {
                    extension.getComponent(Transform.class).setRotation(hostEntityRotation + 0);
                } else if (headerIndex == 1) {
                    extension.getComponent(Transform.class).setRotation(hostEntityRotation + 90);
                } else if (headerIndex == 2) {
                    extension.getComponent(Transform.class).setRotation(hostEntityRotation + 180);
                } else if (headerIndex == 3) {
                    extension.getComponent(Transform.class).setRotation(hostEntityRotation + 270);
                }

                // Invalidate Image Component so its geometry (i.e., shapes) will be updated.
                extension.getComponent(Image.class).invalidate();
            }
        }
    }

    // TODO: Refactor this... it's really dumb right now.
    public void updateExtensionHeaderIndex(Extension extension) {
        if (extension.getComponent(Image.class) == null || extension.getHosts().size() == 0) {
            return;
        }
        int segmentIndex = getHeaderIndex(extension);
        headerExtensions.get(segmentIndex).add(extension);
    }
}

