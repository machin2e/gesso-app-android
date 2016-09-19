package camp.computer.clay.scene.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.EventListener;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

public class ExtensionImage extends Image<Extension> {

    // Shapes
    private Rectangle boardShape = null;

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

        setOnActionListener(new EventListener() {
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
                        showPortImages();
                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to getEvent data."

                        // Show ports and paths of touched form
                        for (PortImage portImage : getPortImages()) {
                            List<Path> paths = portImage.getPort().getCompletePath();
                            for (Path path : paths) {

                                // Show ports
                                scene.getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                                scene.getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                // Show path
                                scene.getImage(path).setVisibility(Visibility.VISIBLE);
                            }
                        }
                    }

                } else if (event.getType() == Event.Type.HOLD) {

                    Log.v("Event", "Tapped patch. Port image count: " + getPortImages().size());
                    Port port = new Port();
                    getExtension().addPort(port);
                    scene.addFeature(port);

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Update Image
                    PortImage sourcePortImage = (PortImage) event.getAction().getFirstEvent().getTargetImage();
                    sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);
                    sourcePortImage.setCandidatePatchVisibility(Visibility.INVISIBLE);

                }
            }
        });
    }

    // TODO: Delete
    public Extension getExtension() {
        return getFeature();
    }

    public List<PortImage> getPortImages() {
        List<PortImage> portShapes = new ArrayList<>();
        Extension extension = getExtension();

        for (Port port : extension.getPorts()) {
            PortImage portShape = (PortImage) scene.getImage(port);
            portShapes.add(portShape);
        }

        return portShapes;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) scene.getFeature(portImage);
        if (getExtension().getPorts().contains(port)) {
            return this.getExtension().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {

//        // Transparency
//        String transparencyString = String.format("%02x", (int) transparency * 255);
//        color = Color.parseColor("#" + transparencyString + colorString);
//        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);

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

    public void showPortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(Visibility.VISIBLE);
            portImage.showDocks();
        }
    }

    public void hidePortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(Visibility.INVISIBLE);
        }
    }

    public void showPathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(Visibility.INVISIBLE);
        }
    }

    public void hidePathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(Visibility.INVISIBLE);
            portImage.showDocks();
        }
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
}

