package camp.computer.clay.scene.image;

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
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Process;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

public class PatchImage extends Image<Patch> {

    // Shapes
    private Rectangle boardShape = null;

    public PatchImage(Patch patch) {
        super(patch);
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

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.SELECT) {

                } else if (action.getType() == Action.Type.UNSELECT) {

                    Process process = action.getActionSequence();

                    Image targetImage = scene.getImageByCoordinate(action.getCoordinate());
                    action.setTarget(targetImage);

                    if (process.getDuration() < Action.MAXIMUM_TAP_DURATION) {

                        // Focus on touched form
                        showPathFigures();
                        showPortFigures();
                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to getAction data."

                        // Show ports and paths of touched form
                        for (PortImage portFigure : getPortFigures()) {
                            List<Path> paths = portFigure.getPort().getGraph();
                            for (Path path : paths) {

                                // Show ports
                                scene.getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                                scene.getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                // Show path
                                scene.getImage(path).setVisibility(Visibility.VISIBLE);
                            }
                        }
                    }

                } else if (action.getType() == Action.Type.HOLD) {

                    Log.v("Action", "Tapped patch. Port figure count: " + getPortFigures().size());
                    Port port = new Port();
                    getPatch().addPort(port);
                    scene.addConstruct(port);

                } else if (action.getType() == Action.Type.MOVE) {

                } else if (action.getType() == Action.Type.UNSELECT) {

                    // Update Image
                    PortImage sourcePortFigure = (PortImage) action.getActionSequence().getFirstAction().getTarget();
                    sourcePortFigure.setCandidatePathVisibility(Visibility.INVISIBLE);
                    sourcePortFigure.setCandidatePatchVisibility(Visibility.INVISIBLE);

                }
            }
        });
    }

    // TODO: Delete
    public Patch getPatch() {
        return getConstruct();
    }

    public List<PortImage> getPortFigures() {
        List<PortImage> portFigures = new ArrayList<>();
        Patch patch = getPatch();

        for (Port port : patch.getPorts()) {
            PortImage portFigure = (PortImage) scene.getImage(port);
            portFigures.add(portFigure);
        }

        return portFigures;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortFigureIndex(PortImage portFigure) {
        Port port = (Port) scene.getModel(portFigure);
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

            if (Application.ENABLE_GEOMETRY_LABELS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Surface.drawCircle(getCoordinate(), boardShape.getWidth(), 0, surface);
                Surface.drawCircle(getCoordinate(), boardShape.getWidth() / 2.0f, 0, surface);
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

    public void hidePortFigures() {
        for (PortImage portFigure : getPortFigures()) {
            portFigure.setVisibility(Visibility.INVISIBLE);
        }
    }

    public void showPathFigures() {
        for (PortImage portFigure : getPortFigures()) {
            portFigure.setPathVisibility(Visibility.INVISIBLE);
        }
    }

    public void hidePathFigures() {
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

