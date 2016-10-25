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

    public HostImage(Host host) {
        super(host);
        setup();
    }

    private void setup() {
        setupGeometry();
//        setupActionListener();
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
}

