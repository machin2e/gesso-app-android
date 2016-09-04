package camp.computer.clay.scene.figure;

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
import camp.computer.clay.model.interaction.Transcript;
import camp.computer.clay.scene.architecture.Figure;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

public class PatchFigure extends Figure<Patch> {

    // Shapes
    private Rectangle boardShape = null;

    public PatchFigure(Patch patch) {
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

                } else if (action.getType() == Action.Type.TOUCH) {

                } else if (action.getType() == Action.Type.RELEASE) {

                    Transcript transcript = action.getActionSequence();

                    Figure targetFigure = scene.getFigureByPosition(action.getPosition());
                    action.setTarget(targetFigure);

                    if (transcript.getDuration() < Action.MAXIMUM_TAP_DURATION) {

                        // Focus on touched form
                        showPathFigures();
                        showPortFigures();
                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to get data."

                        // Show ports and paths of touched form
                        for (PortFigure portFigure : getPortFigures()) {
                            List<Path> paths = portFigure.getPort().getGraph();
                            for (Path path : paths) {

                                // Show ports
                                scene.getFigure(path.getSource()).setVisibility(Visibility.VISIBLE);
                                scene.getFigure(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                // Show path
                                scene.getFigure(path).setVisibility(Visibility.VISIBLE);
                            }
                        }
                    }

                } else if (action.getType() == Action.Type.HOLD) {

                    Log.v("Action", "Tapped patch. Port figure count: " + getPortFigures().size());
                    Port port = new Port();
                    getPatch().addPort(port);
                    scene.addConstruct(port);

                } else if (action.getType() == Action.Type.MOVE) {

                } else if (action.getType() == Action.Type.RELEASE) {

                    // Update Figure
                    PortFigure sourcePortFigure = (PortFigure) action.getActionSequence().getFirst().getTarget();
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

    public List<PortFigure> getPortFigures() {
        List<PortFigure> portFigures = new ArrayList<>();
        Patch patch = getPatch();

        for (Port port : patch.getPorts()) {
            PortFigure portFigure = (PortFigure) scene.getFigure(port);
            portFigures.add(portFigure);
        }

        return portFigures;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortFigureIndex(PortFigure portFigure) {
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

    public void showPortFigures() {
        for (PortFigure portFigure : getPortFigures()) {
            portFigure.setVisibility(Visibility.VISIBLE);
            portFigure.showDocks();
        }
    }

    public void hidePortFigures() {
        for (PortFigure portFigure : getPortFigures()) {
            portFigure.setVisibility(Visibility.INVISIBLE);
        }
    }

    public void showPathFigures() {
        for (PortFigure portFigure : getPortFigures()) {
            portFigure.setPathVisibility(Visibility.INVISIBLE);
        }
    }

    public void hidePathFigures() {
        for (PortFigure portFigure : getPortFigures()) {
            portFigure.setPathVisibility(Visibility.INVISIBLE);
            portFigure.showDocks();
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

