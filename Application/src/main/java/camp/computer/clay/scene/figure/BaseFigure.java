package camp.computer.clay.scene.figure;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Base;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Process;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.scene.architecture.Figure;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

public class BaseFigure extends Figure<Base> {

    public BaseFigure(Base base) {
        super(base);
        setup();
    }

    private void setup() {
        setupShapes();
        setupActions();
    }

    private void setupShapes() {

        // Create shapes for figure
        Rectangle boardShape = new Rectangle(250, 250);
        boardShape.setLabel("board");
        boardShape.setColor("#f7f7f7");
        boardShape.setOutlineThickness(1);
        addShape(boardShape);

        // Headers
        Rectangle headerShape1 = new Rectangle(50, 14);
        headerShape1.setPosition(0, 132);
        // headerShape1.setRotation(0);
        headerShape1.setColor("#3b3b3b");
        headerShape1.setOutlineThickness(0);
        addShape(headerShape1);

        Rectangle headerShape2 = new Rectangle(50, 14);
        headerShape2.setPosition(132, 0);
        headerShape2.setRotation(90);
        headerShape2.setColor("#3b3b3b");
        headerShape2.setOutlineThickness(0);
        addShape(headerShape2);

        Rectangle headerShape3 = new Rectangle(50, 14);
        headerShape3.setPosition(0, -132);
        // headerShape3.setRotation(180);
        headerShape3.setColor("#3b3b3b");
        headerShape3.setOutlineThickness(0);
        addShape(headerShape3);

        Rectangle headerShape4 = new Rectangle(50, 14);
        headerShape4.setPosition(-132, 0);
        headerShape4.setRotation(90);
        headerShape4.setColor("#3b3b3b");
        headerShape4.setOutlineThickness(0);
        addShape(headerShape4);

        // Lights
        Rectangle light1 = new Rectangle(12, 20);
        light1.setLabel("LED 1");
        light1.setPosition(-20, 105);
        // light1.setRotation(90);
//        lightShapes.add(light1);
        addShape(light1);

        Rectangle light2 = new Rectangle(12, 20);
        light2.setLabel("LED 2");
        light2.setPosition(0, 105);
//        light2.setRotation(90);
//        lightShapes.add(light2);
        addShape(light2);

        Rectangle light3 = new Rectangle(12, 20);
        light3.setLabel("LED 3");
        light3.setPosition(20, 105);
//        light3.setRotation(90);
//        lightShapes.add(light3);
        addShape(light3);

        Rectangle light4 = new Rectangle(12, 20);
        light4.setLabel("LED 4");
        light4.setPosition(105, 20);
        light4.setRotation(90);
//        lightShapes.add(light4);
        addShape(light4);

        Rectangle light5 = new Rectangle(12, 20);
        light5.setLabel("LED 5");
        light5.setPosition(105, 0);
        light5.setRotation(90);
//        lightShapes.add(light5);
        addShape(light5);

        Rectangle light6 = new Rectangle(12, 20);
        light6.setLabel("LED 6");
        light6.setPosition(105, -20);
        light6.setRotation(90);
//        lightShapes.add(light6);
        addShape(light6);

        Rectangle light7 = new Rectangle(12, 20);
        light7.setLabel("LED 7");
        light7.setPosition(20, -105);
//        light7.setRotation(90);
//        lightShapes.add(light7);
        addShape(light7);

        Rectangle light8 = new Rectangle(12, 20);
        light8.setLabel("LED 8");
        light8.setPosition(0, -105);
//        light8.setRotation(90);
//        lightShapes.add(light8);
        addShape(light8);

        Rectangle light9 = new Rectangle(12, 20);
        light9.setLabel("LED 9");
        light9.setPosition(-20, -105);
//        light9.setRotation(90);
//        lightShapes.add(light9);
        addShape(light9);

        Rectangle light10 = new Rectangle(12, 20);
        light10.setLabel("LED 10");
        light10.setPosition(-105, -20);
        light10.setRotation(90);
//        lightShapes.add(light10);
        addShape(light10);

        Rectangle light11 = new Rectangle(12, 20);
        light11.setLabel("LED 11");
        light11.setPosition(-105, 0);
        light11.setRotation(90);
//        lightShapes.add(light11);
        addShape(light11);

        Rectangle light12 = new Rectangle(12, 20);
        light12.setLabel("LED 12");
        light12.setPosition(-105, 20);
        light12.setRotation(90);
//        lightShapes.add(light12);
        addShape(light12);

    }

    private void setupActions() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Process process = action.getActionSequence();

                Camera camera = action.getActor().getCamera();

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.SELECT) {

                } else if (action.getType() == Action.Type.HOLD) {

                } else if (action.getType() == Action.Type.MOVE) {

                    // Holding
                    if (process.isHolding()) {

                        // Holding and dragging

                        // Base
                        action.getTarget().processAction(action);
                        action.getTarget().setPosition(action.getPosition());

                        // Camera
                        camera.focusSelectBase(action);

                    } else {


                        // Update position
                        // action.getTarget().setPosition(action.getPosition());

                        hidePortFigures();
                        hidePathFigures();

                        candidatePatchPosition.set(action.getPosition());

                        setCandidatePatchVisibility(Visibility.VISIBLE);

                    }

                } else if (action.getType() == Action.Type.UNSELECT) {

                    Figure targetFigure = scene.getFigureByPosition(action.getPosition());
                    action.setTarget(targetFigure);

                    if (process.isTap()) {

                        // Focus on touched form
                        showPathFigures();
                        showPortFigures();

                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to getAction data."

                        // Show ports and paths of touched form
                        for (int i = 0; i < getPortFigures().size(); i++) {
                            PortFigure portFigure = getPortFigures().get(i);
                            List<Path> paths = portFigure.getPort().getGraph();
                            Log.v("TouchFrame", "\tpaths.size = " + paths.size());
                            for (Path path : paths) {
                                Log.v("TouchFrame", "\t\tsource = " + path.getSource());
                                Log.v("TouchFrame", "\t\ttarget = " + path.getTarget());
                                // Show ports
                                getScene().getFigure(path.getSource()).setVisibility(Visibility.VISIBLE);
                                getScene().getFigure(path.getTarget()).setVisibility(Visibility.VISIBLE);
                                // Show path
                                getScene().getFigure(path).setVisibility(Visibility.VISIBLE);
                            }
                        }

                        // Camera
                        camera.focusSelectBase(action);

                    } else {

                        // TODO: Release longer than tap!

                        if (process.getFirstAction().getTarget() instanceof BaseFigure) {

                            if (action.getTarget() instanceof BaseFigure) {

                                // If getFirstAction processAction was on the same form, then respond
                                if (process.getFirstAction().isPointing() && process.getFirstAction().getTarget() instanceof BaseFigure) {

                                    // Base
                                    action.getTarget().processAction(action);

                                    // Camera
//                        camera.focusSelectVisualization();
                                }

                            } else if (action.getTarget() instanceof Scene) {

                                // Base
                                process.getFirstAction().getTarget().processAction(action);

                            }

                        }

                    }

                    // Check if connecting to a patch
                    if (getCandidatePatchVisibility() == Visibility.VISIBLE) {

                        // Show patch store
                        Application.getDisplay().displayChooseDialog();
//                        Application.getDisplay().displayTasksDialog();

                        setCandidatePatchVisibility(Visibility.INVISIBLE);
                    }

                }
            }
        });
    }

    // TODO: Delete this
    public Base getBase() {
        return getConstruct();
    }

    public List<PortFigure> getPortFigures() {
        List<PortFigure> portFigures = new ArrayList<>();

        for (Port port : getBase().getPorts()) {
            PortFigure portFigure = (PortFigure) getScene().getFigure(port);
            portFigures.add(portFigure);
        }

        return portFigures;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortFigureIndex(PortFigure portFigure) {
        Port port = (Port) getScene().getModel(portFigure);
        if (getBase().getPorts().contains(port)) {
            return this.getBase().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {

        // TODO: Filter by regular expression "LED [0-9]+"
        Shape[] lightShapes = new Shape[12];
        lightShapes[0] = getShape("LED 1");
        lightShapes[1] = getShape("LED 2");
        lightShapes[2] = getShape("LED 3");
        lightShapes[3] = getShape("LED 4");
        lightShapes[4] = getShape("LED 5");
        lightShapes[5] = getShape("LED 6");
        lightShapes[6] = getShape("LED 7");
        lightShapes[7] = getShape("LED 8");
        lightShapes[8] = getShape("LED 9");
        lightShapes[9] = getShape("LED 10");
        lightShapes[10] = getShape("LED 11");
        lightShapes[11] = getShape("LED 12");

        for (int i = 0; i < lightShapes.length; i++) {
            Port port = getBase().getPort(i);
            if (port.getType() != Port.Type.NONE) {
                int intColor = getPortFigures().get(i).getUniqueColor();
                String hexColor = camp.computer.clay.scene.util.Color.getHexColorString(intColor);
                lightShapes[i].setColor(hexColor);
            } else {
                lightShapes[i].setColor(camp.computer.clay.scene.util.Color.getHexColorString(PortFigure.FLOW_PATH_COLOR_NONE));
            }
        }

//        String transparencyString = String.format("%02x", (int) transparency * 255);
//
//        // Base color
//        color = Color.parseColor("#" + transparencyString + colorString);
//        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);
//
//        // Header color
//        portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
//        portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);

//        updatePortGroupFigures();
    }

    public void draw(Surface surface) {
        if (isVisible()) {

            // Color
            for (int i = 0; i < shapes.size(); i++) {
                // TODO: Change "drawRectangle" to "drawShape()"
                Surface.drawRectangle((Rectangle) shapes.get(i), surface);
            }

            // Annotations
            if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Rectangle boardShape = (Rectangle) getShape("board");
                Surface.drawCircle(getPosition(), boardShape.getWidth(), 0, surface);
                Surface.drawCircle(getPosition(), boardShape.getWidth() / 2.0f, 0, surface);
            }

            drawCandidatePatchImage(surface);
        }
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
            portFigure.setPathVisibility(Visibility.VISIBLE);
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
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (((Rectangle) getShape("board")).getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean contains(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (((Rectangle) getShape("board")).getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }


    private Visibility candidatePatchVisibility = Visibility.INVISIBLE;
    private Point candidatePatchPosition = new Point(40, 80);

    public void setCandidatePatchVisibility(Visibility visibility) {
        candidatePatchVisibility = visibility;
    }

    public Visibility getCandidatePatchVisibility() {
        return candidatePatchVisibility;
    }

    private void drawCandidatePatchImage(Surface surface) {

        if (candidatePatchVisibility == Visibility.VISIBLE) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    getPosition(),
                    candidatePatchPosition
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.CYAN); // paint.setColor(getUniqueColor());
            Surface.drawRectangle(candidatePatchPosition, pathRotationAngle + 180, 250, 250, surface);

        }

    }
}

