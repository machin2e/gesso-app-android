package camp.computer.clay.space.image;

import android.graphics.Canvas;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.model.Extension;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.PortableProfile;
import camp.computer.clay.model.util.PathGroup;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

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

                    // Previous Action targeted also this Extension
                    // TODO: Refactor
                    if (action.getPrevious().getFirstEvent().getTargetImage().getEntity() == getExtension()) {

                        if (action.isTap()) {
                            // TODO: Replace with script editor/timeline
                            Application.getView().openActionEditor(getExtension());
                        }

                    } else {


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

                                PathGroup paths = port.getPaths();
                                for (int j = 0; j < paths.size(); j++) {
                                    Path path = paths.get(j);

                                    // Show ports
                                    getSpace().getShape(path.getSource()).setVisibility(Visibility.Value.VISIBLE);
                                    getSpace().getShape(path.getTarget()).setVisibility(Visibility.Value.VISIBLE);

                                    // Show path
                                    getSpace().getImage(path).setVisibility(Visibility.Value.VISIBLE);
                                }
                            }

                            // Camera
                            event.getActor().getCamera().setFocus(getExtension());

                            // Title
                            parentSpace.setTitleText("Extension");
                            parentSpace.setTitleVisibility(Visibility.Value.VISIBLE);
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

        // Create additional Images or Shapes to match the corresponding Entity
        updateImage();

        // Update Port style
        for (int i = 0; i < getExtension().getPorts().size(); i++) {
            Port port = getExtension().getPorts().get(i);
            Shape portShape = getShape(port);

            // Update color of Port shape based on type
            if (portShape != null) {
                portShape.setColor(Color.getColor(port.getType()));
            }
        }

        super.update();
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    private void updateImage() {

        updatePortShapes();

        updateHeaderShapes();

        // TODO: Clean up/delete images/shapes for any removed ports...
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code Extension}'s {@code Port}s.
     */
    private void updatePortShapes() {

        // Remove Port shapes from the Image that do not have a corresponding Port in the Entity
        ShapeGroup portShapes = getShapes(Port.class);
        for (int i = 0; i < portShapes.size(); i++) {
            Shape portShape = portShapes.get(i);

            if (!getPortable().getPorts().contains((Port) portShape.getEntity())) {
                portShapes.remove(portShape);
            }
        }

        // Create Port shapes for each of Extension's Ports if they don't already exist
        for (int i = 0; i < getExtension().getPorts().size(); i++) {
            Port port = getExtension().getPorts().get(i);

            if (getShape(port) == null) {

                // Ports
                Circle<Port> circle = new Circle<>(port);
                circle.setRadius(40);
                circle.setLabel("Port " + (getExtension().getPorts().size() + 1));
                circle.setPosition(-90, 175);
                // circle.setRotation(0);

                circle.setColor("#efefef");
                circle.setOutlineThickness(0);

                circle.setVisibility(Visibility.Value.INVISIBLE);

                addShape(circle);
            }
        }
    }

    private void updateHeaderShapes() {

        // Update Header (size, etc.)
        // References:
        // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png
        double PIXEL_PER_MILLIMETER = 6.0;

        final int contactCount = getPortable().getPorts().size();
        final double errorToleranceA = 0.0; // ±0.60 mm according to [1]
        final double errorToleranceB = 0.0; // ±0.15 mm according to [1]

//        double A = PIXEL_PER_MILLIMETER * (2.54 * contactCount + errorToleranceA);
//        double B = PIXEL_PER_MILLIMETER * (2.54 * (contactCount - 1) + errorToleranceB);
        double A = 2.54 * contactCount + errorToleranceA;
        double B = 2.54 * (contactCount - 1) + errorToleranceB;

        final double errorToleranceContactSeparation = 0.0; // ±0.1 mm according to [1]
        double contactOffset = (A - B) / 2.0; // Measure in millimeters (mm)
        double contactSeparation = 2.54; // Measure in millimeters (mm)

        // Update dimensions of Headers based on the corresponding Entity
        Rectangle header = (Rectangle) getShape("Header");
        double headerWidth = PIXEL_PER_MILLIMETER * A;
        header.setWidth(headerWidth);

        // Update physical positions of Ports based on the corresponding Header's dimensions
        for (int i = 0; i < getPortable().getPorts().size(); i++) {

            // Calculate Port connector positions
            //double connectorPositionDistance = (PIXEL_PER_MILLIMETER * (2.54 * portConnectorPositions.size() + 0.6));

            if (i < portConnectorPositions.size()) {
                //double x = (PIXEL_PER_MILLIMETER * (2.54 * i + 0.6)) - (connectorPositionDistance / 2.0);
                double x = PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
                portConnectorPositions.get(i).setX(x);
            } else {
                //double x = PIXEL_PER_MILLIMETER * (2.54 * i + 0.6);
                double x = PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
                portConnectorPositions.add(new Point(x, 107, position));
            }
        }

        // Update Port positions based on the index of ports
        for (int i = 0; i < getPortable().getPorts().size(); i++) {
            Port port = getPortable().getPorts().get(i);
            Circle portShape = (Circle) getShape(port);

            if (portShape != null) {
                double portSpacing = 100;
                double x = (i * portSpacing) - (((getPortable().getPorts().size() - 1) * portSpacing) / 2.0);
                portShape.getPosition().setX(x);
                // TODO: Also update y coordinate
            }
        }
    }

    public void draw(Display display) {
        if (isVisible()) {

            Canvas canvas = display.getCanvas();

            canvas.save();

            // <HACK>
            if (getPosition().getReferencePoint() != null) {
                canvas.translate(
                        (float) getPosition().getReferencePoint().x,
                        (float) getPosition().getReferencePoint().y
                );

                canvas.rotate((float) getPosition().getReferencePoint().rotation);
            }
            // </HACK>

            canvas.translate(
                    (float) getPosition().x,
                    (float) getPosition().y
            );

            canvas.rotate((float) getPosition().rotation);

            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).draw(display);
            }

            canvas.restore();
        }
    }
}

