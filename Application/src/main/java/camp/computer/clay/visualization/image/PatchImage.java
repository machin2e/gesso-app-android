package camp.computer.clay.visualization.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.model.interactivity.ActionListener;
import camp.computer.clay.model.interactivity.Interaction;
import camp.computer.clay.model.interactivity.Perspective;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;

public class PatchImage extends Image {

    // Shapes
    private Rectangle boardShape = null;

    public PatchImage(Patch patch) {
        super(patch);
        setup();
    }

    private void setup() {
        setupShapes();
        setupInteractivity();
    }

    private void setupShapes() {

        // Create shapes for image
        boardShape = new Rectangle(200, 200);
        boardShape.setColor("#f7f7f7");
        boardShape.setOutlineThickness(1);
        addShape(boardShape);

    }

    private void setupInteractivity() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.TOUCH) {

                } else if (action.getType() == Action.Type.RELEASE) {

                    Interaction interaction = action.getInteraction();

                    Image targetImage = visualization.getImageByPosition(action.getPosition());
                    action.setTarget(targetImage);

                    if (interaction.getDuration() < Action.MAX_TAP_DURATION) {

                        Log.v("Action", "Tapped patch. Port image count: " + getPortImages().size());

                        // Focus on touched form
                        showPortImages();
                        showPathImages();
                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to get data."

                        // Show ports and paths of touched form
                        for (PortImage portImage : getPortImages()) {
                            List<Path> paths = portImage.getPort().getGraph();
                            for (Path path : paths) {

                                // Show ports
                                visualization.getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                                visualization.getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                // Show path
                                visualization.getImage(path).setVisibility(Visibility.VISIBLE);
                            }
                        }
                    }

                } else if (action.getType() == Action.Type.HOLD) {

                } else if (action.getType() == Action.Type.MOVE) {

                } else if (action.getType() == Action.Type.RELEASE) {

                    // Update Image
                    PortImage sourcePortImage = (PortImage) action.getInteraction().getFirst().getTarget();
                    sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);
                    sourcePortImage.setCandidatePeripheralVisibility(Visibility.INVISIBLE);

                }
            }
        });
    }

    public Patch getPatch() {
        return (Patch) getModel();
    }

    public List<PortImage> getPortImages() {
        List<PortImage> portImages = new ArrayList<>();
        Patch patch = getPatch();

        for (Port port : patch.getPorts()) {
            PortImage portImage = (PortImage) visualization.getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) visualization.getModel(portImage);
        if (getPatch().getPorts().contains(port)) {
            return this.getPatch().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {

//        // Transparency
//        String transparencyString = String.format("%02x", (int) transparency * 255);
//        color = Color.parseColor("#" + transparencyString + colorString);
//        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);

    }

    public void draw(Surface surface) {
        if (isVisible()) {

            // Color
            for (int i = 0; i < shapes.size(); i++) {
                Surface.drawRectangle((Rectangle) shapes.get(i), surface);
            }

            if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Surface.drawCircle(getPosition(), boardShape.getWidth(), 0, surface);
                Surface.drawCircle(getPosition(), boardShape.getWidth() / 2.0f, 0, surface);
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

    public boolean containsPoint(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean containsPoint(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }
}

