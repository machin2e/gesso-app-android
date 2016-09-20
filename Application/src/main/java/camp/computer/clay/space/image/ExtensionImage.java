package camp.computer.clay.space.image;

import android.util.Log;

import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.architecture.ShapeGroup;
import camp.computer.clay.space.util.Color;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Rectangle;
import camp.computer.clay.space.architecture.Shape;

public class ExtensionImage extends PortableImage { // Image<Extension> {

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
                        getPortShapes().setVisibility(Visibility.VISIBLE);
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
                    space.setPrototypePathVisibility(Visibility.INVISIBLE);
                    space.setPrototypeExtensionVisibility(Visibility.INVISIBLE);

                }
            }
        });
    }

    public Extension getExtension() {
        return (Extension) getEntity();
    }

    public void update() {

        // TODO: Update Port positions based on the number of ports
        int portCount = getPortable().getPorts().size();

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
}

