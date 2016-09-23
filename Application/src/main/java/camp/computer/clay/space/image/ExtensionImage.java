package camp.computer.clay.space.image;

import android.util.Log;

import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.ui.Dialog;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.profile.ExtensionProfile;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.architecture.Shape;
import camp.computer.clay.space.architecture.ShapeGroup;
import camp.computer.clay.space.util.Color;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Circle;
import camp.computer.clay.space.util.geometry.Rectangle;

public class ExtensionImage extends PortableImage { // Image<Extension> {

    public ExtensionImage(Extension extension)
    {
        super(extension);
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

    private void setupActions()
    {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action)
            {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                    // TODO: Only call promptInputText if the extension is a draft (i.e., does not have an associated ExtensionProfile)
                    Launcher.getLauncherView().getUi().promptInputText(new Dialog.OnCompleteCallback<String>() {
                        @Override
                        public void onComplete(String result)
                        {

                            // Create Extension Profile
                            ExtensionProfile extensionProfile = new ExtensionProfile();
                            extensionProfile.setLabel(result);
                            extensionProfile.setPortCount(getPortable().getPorts().size());
                            Log.v("HoldAction", "Creating new profile \"" + extensionProfile.getLabel() + "\" with " + extensionProfile.getPortCount() + " ports. Only added if no duplicate (i.e., doesn't already exist)");

                            // Cache the new Extension Profile
                            Launcher.getLauncherView().getClay().getExtensionProfiles().add(extensionProfile);

                            // TODO: Persist the profile in the user's private store (either local or online)

                            // TODO: Persist the profile in the global store online
                        }
                    });

                } else if (event.getType() == Event.Type.UNSELECT) {

                    Image targetImage = space.getImageByPosition(event.getPosition());
                    event.setTargetImage(targetImage);

                    if (action.getDuration() < Event.MAXIMUM_TAP_DURATION) {

                        // Focus on touched base
                        setPathVisibility(Visibility.Value.VISIBLE);
                        getPortShapes().setVisibility(Visibility.Value.VISIBLE);
                        setTransparency(1.0);

                        // Show ports and paths of touched form
                        ShapeGroup portShapes = getPortShapes();
                        for (int i = 0; i < portShapes.size(); i++) {
                            Shape portShape = portShapes.get(i);
                            Port port = (Port) portShape.getEntity();

                            List<Path> paths = port.getCompletePath();
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

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Update style
                    space.setPrototypePathVisibility(Visibility.Value.INVISIBLE);
                    space.setPrototypeExtensionVisibility(Visibility.Value.INVISIBLE);

                }
            }
        });
    }

    public Extension getExtension()
    {
        return (Extension) getEntity();
    }

    public void update()
    {

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

        // TODO: Update Port positions based on the number of ports
        int portCount = getPortable().getPorts().size();
        for (int i = 0; i < getPortable().getPorts().size(); i++) {
            Port port = getPortable().getPorts().get(i);
            Shape portShape = getShape(port);

            if (portShape != null) {
                double portSpacing = 100;
                double x = (i * portSpacing) - (((getPortable().getPorts().size() - 1) * portSpacing) / 2.0);
                portShape.getPosition().setRelativeX(x);
                // TODO: Also update y coordinate
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

        // Update Header (size, etc.)
        ((Rectangle) getShape("Header")).setWidth(15 * getExtension().getPorts().size());
    }

    public void draw(Display display)
    {
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
}

