package camp.computer.clay.space.image;

import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Extension;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.PortableProfile;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.util.ShapeGroup;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;

public class ExtensionImage extends PortableImage {

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
        rectangle = new Rectangle(getExtension());
        rectangle.setWidth(200);
        rectangle.setHeight(200);
        rectangle.setLabel("Board");
        rectangle.setColor("#f7f7f7");
        rectangle.setOutlineThickness(1);
        addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header");
        rectangle.setPosition(0, 107);
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);
    }

    private void setupActions() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                    createProfile();

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    if (action.isTap()) {

                        // Focus on touched base
                        setPathVisibility(Visibility.Value.VISIBLE);
                        getPortShapes().setVisibility(Visibility.Value.VISIBLE);
                        setTransparency(1.0);

                        // Show ports and paths of touched form
                        ShapeGroup portShapes = getPortShapes();
                        for (int i = 0; i < portShapes.size(); i++) {
                            Shape portShape = portShapes.get(i);
                            Port port = (Port) portShape.getEntity();

                            List<Path> paths = port.getPaths();
                            for (int j = 0; j < paths.size(); j++) {
                                Path path = paths.get(j);

                                // Show ports
                                getSpace().getShape(path.getSource()).setVisibility(Visibility.Value.VISIBLE);
                                getSpace().getShape(path.getTarget()).setVisibility(Visibility.Value.VISIBLE);

                                // Show path
                                getSpace().getImage(path).setVisibility(Visibility.Value.VISIBLE);
                            }
                        }
                    }

                }
            }
        });
    }

    // TODO: This is an action that Clay can perform. Place this better, maybe in Clay.
    private void createProfile() {
        if (!getExtension().hasProfile()) {

            // TODO: Only call promptInputText if the extension is a draft (i.e., does not have an associated PortableProfile)
            Application.getView().getActionPrompts().promptInputText(new Prompt.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {
                    // Create Extension Profile
                    PortableProfile portableProfile = new PortableProfile(getExtension());
                    portableProfile.setLabel(text);

                    // Assign the Profile to the Extension
                    getExtension().setProfile(portableProfile);

                    // Cache the new Extension Profile
                    Application.getView().getClay().getPortableProfiles().add(portableProfile);

                    // TODO: Persist the profile in the user's private store (either local or online)

                    // TODO: Persist the profile in the global store online
                }
            });
        } else {
            Application.getView().getActionPrompts().promptAcknowledgment(new Prompt.OnActionListener() {
                @Override
                public void onComplete(Object result) {

                }
            });
        }
    }

    public Extension getExtension() {
        return (Extension) getEntity();
    }

    public void update() {

        // Create any additional images or shapes to match the Entity
        // <HACK>
        // Create Port shapes for each of Extension's Ports
        for (int i = 0; i < getExtension().getPorts().size(); i++) {
            Port port = getExtension().getPorts().get(i);

            if (getShape(port) == null) {

                // Ports
                Circle<Port> circle = new Circle<>(port);
                circle.setRadius(40);
                circle.setLabel("Port " + (getExtension().getPorts().size() + 1));
                circle.setPosition(-90, 200);
                // circle.setRelativeRotation(0);

                circle.setColor("#efefef");
                circle.setOutlineThickness(0);

                circle.setVisibility(Visibility.Value.INVISIBLE);

                addShape(circle);
            }
        }

        // TODO: Clean up/delete images/shapes for any removed ports...
        // </HACK>

        // Update Header (size, etc.)
        // Reference: http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png
        double pixelsPerMillimeter = 6;
        double headerWidth = pixelsPerMillimeter * (2.54 * getPortable().getPorts().size() + 0.6); // +-0.6

        Rectangle header = ((Rectangle) getShape("Header"));
        header.setWidth(headerWidth);

        // TODO: Update Port positions based on the number of ports

        for (int i = 0; i < getPortable().getPorts().size(); i++) {
            Port port = getPortable().getPorts().get(i);
            Shape portShape = getShape(port);

            if (portShape != null) {
                double portSpacing = 100;
                double x = (i * portSpacing) - (((getPortable().getPorts().size() - 1) * portSpacing) / 2.0);
                portShape.getPosition().setRelativeX(x);
                // TODO: Also update relativeY coordinate
            }

            // Calculate Port connector positions
            if (portInterfacePositions.size() > i) {
//                portInterfacePositions.add(new Point(-20, 132, position));
                double connectionX = pixelsPerMillimeter * (2.54 * i + 0.6);
                portInterfacePositions.get(i).setRelativeX(connectionX);
            } else {
                double connectionX = pixelsPerMillimeter * (2.54 * i + 0.6);
                portInterfacePositions.add(new Point(connectionX, 107, position));
            }

        }

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

            drawConnections(display);
        }
    }

    private void drawConnections(Display display) {

        // <REFACTOR>
        // TODO: Move into the Shape.draw() for Port shape
        // Draw connections to PhoneHost
        ShapeGroup portShapes = getPortShapes();
        for (int i = 0; i < portShapes.size(); i++) {

            Shape portShape = portShapes.get(i);
            Port port = (Port) portShape.getEntity();

            // Search for connected PhoneHost's Port
            Port hostPort = null;
            List<Path> paths = port.getPaths(1);
            int hostPortIndex = -1;
            for (int j = 0; j < paths.size(); j++) {
                hostPort = paths.get(j).getHostPort();
            }

            // Draw the connection to the PhoneHost's Port
            if (hostPort != null) {

                // <HACK>
                hostPortIndex = hostPort.getPortable().getPorts().indexOf(hostPort);
                int extensionPortIndex = getPortable().getPorts().indexOf(port);
                // </HACK>

                // Get connect PhoneHost's Port shape
                Shape hostPortShape = space.getShape(hostPort);

                HostImage hostImage = (HostImage) space.getImage(hostPort.getPortable());

                Point extensionPortConnectorPosition = portInterfacePositions.get(extensionPortIndex);
                Point hostPortConnectorPosition = hostImage.portInterfacePositions.get(hostPortIndex);

                // Draw connection between Ports
                display.getPaint().setColor(android.graphics.Color.parseColor(Color.getColor(port.getType())));
                display.getPaint().setStrokeWidth(15.0f);
                //Display.drawLine(portShape.getPosition(), hostPortShape.getPosition(), display);
//                Display.drawLine(portShape.getPosition(), hostPortConnectorPosition, display);
                Display.drawLine(extensionPortConnectorPosition, hostPortConnectorPosition, display);
            }
        }
        // </REFACTOR>
    }
}

