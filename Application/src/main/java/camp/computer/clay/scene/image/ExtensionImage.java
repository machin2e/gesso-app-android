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

        // Create shapes for figure
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

                    Image targetImage = scene.getImageByCoordinate(event.getCoordinate());
                    event.setTargetImage(targetImage);

                    if (action.getDuration() < Event.MAXIMUM_TAP_DURATION) {

                        // Focus on touched base
                        showPathFigures();
                        showPortFigures();
                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to getEvent data."

                        // Show ports and paths of touched form
                        for (PortImage portFigure : getPortFigures()) {
                            List<Path> paths = portFigure.getPort().getAllPaths();
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

                    Log.v("Event", "Tapped patch. Port figure count: " + getPortFigures().size());
                    Port port = new Port();
                    getExtension().addPort(port);
                    scene.addFeature(port);

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Update Image
                    PortImage sourcePortFigure = (PortImage) event.getAction().getFirstEvent().getTargetImage();
                    sourcePortFigure.setCandidatePathVisibility(Visibility.INVISIBLE);
                    sourcePortFigure.setCandidatePatchVisibility(Visibility.INVISIBLE);

                }
            }
        });
    }

    // TODO: Delete
    public Extension getExtension() {
        return getFeature();
    }

    public List<PortImage> getPortFigures() {
        List<PortImage> portFigures = new ArrayList<>();
        Extension extension = getExtension();

        for (Port port : extension.getPorts()) {
            PortImage portFigure = (PortImage) scene.getImage(port);
            portFigures.add(portFigure);
        }

        return portFigures;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortFigureIndex(PortImage portFigure) {
        Port port = (Port) scene.getFeature(portFigure);
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
                Display.drawRectangle((Rectangle) shapes.get(i), display);
            }

            if (Launcher.ENABLE_GEOMETRY_LABELS) {
                display.getPaint().setColor(Color.GREEN);
                display.getPaint().setStyle(Paint.Style.STROKE);
                Display.drawCircle(getCoordinate(), boardShape.getWidth(), 0, display);
                Display.drawCircle(getCoordinate(), boardShape.getWidth() / 2.0f, 0, display);
            }
        }
    }

    public Rectangle getShape() {
        return this.boardShape;
    }

    public void showPortFigures() {
        for (PortImage portFigure : getPortFigures()) {
            portFigure.setVisibility(Visibility.VISIBLE);
            portFigure.showDocks();
        }
    }

    public void hidePortImages() {
        for (PortImage portFigure : getPortFigures()) {
            portFigure.setVisibility(Visibility.INVISIBLE);
        }
    }

    public void showPathFigures() {
        for (PortImage portFigure : getPortFigures()) {
            portFigure.setPathVisibility(Visibility.INVISIBLE);
        }
    }

    public void hidePathImages() {
        for (PortImage portFigure : getPortFigures()) {
            portFigure.setPathVisibility(Visibility.INVISIBLE);
            portFigure.showDocks();
        }
    }

    public boolean contains(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getCoordinate().getX(), (int) this.getCoordinate().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean contains(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getCoordinate().getX(), (int) this.getCoordinate().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }
}

