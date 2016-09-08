package camp.computer.clay.scene.image;

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
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

public class BaseImage extends Image<Base> {

    public BaseImage(Base base) {
        super(base);
        setup();
    }

    private void setup() {
        setupShapes();
        setupActions();
    }

    private void setupShapes() {

        Rectangle rectangle = null;

        // Create shapes for figure
        rectangle = new Rectangle(250, 250);
        rectangle.setLabel("board");
        rectangle.setColor("#f7f7f7");
        rectangle.setOutlineThickness(1);
        addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 1");
        rectangle.setCoordinate(0, 132);
        // headerShape1.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 2");
        rectangle.setCoordinate(132, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 3");
        rectangle.setCoordinate(0, -132);
        // headerShape3.setRotation(180);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 4");
        rectangle.setCoordinate(-132, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        // Lights
        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 1");
        rectangle.setCoordinate(-20, 105);
        // light1.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 2");
        rectangle.setCoordinate(0, 105);
        // light1.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 3");
        rectangle.setCoordinate(20, 105);
        // light1.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 4");
        rectangle.setCoordinate(105, 20);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 5");
        rectangle.setCoordinate(105, 0);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 6");
        rectangle.setCoordinate(105, -20);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 7");
        rectangle.setCoordinate(20, -105);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 8");
        rectangle.setCoordinate(0, -105);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 9");
        rectangle.setCoordinate(-20, -105);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 10");
        rectangle.setCoordinate(-105, -20);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 11");
        rectangle.setCoordinate(-105, 0);
        rectangle.setRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 12");
        rectangle.setCoordinate(-105, 20);
        rectangle.setRotation(90);
        addShape(rectangle);

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
                        action.getTarget().setCoordinate(action.getCoordinate());

                        // Camera
                        camera.focusSelectBase(action);

                    } else {


                        // Update position
                        // action.getTarget().setCoordinate(action.getCoordinate());

                        hidePortImages();
                        hidePathImages();

                        candidatePatchCoordinate.set(action.getCoordinate());

                        setCandidatePatchVisibility(Visibility.VISIBLE);

                    }

                } else if (action.getType() == Action.Type.UNSELECT) {

                    Image targetImage = scene.getImageByCoordinate(action.getCoordinate());
                    action.setTarget(targetImage);

                    if (process.isTap()) {

                        // Focus on touched form
                        showPathImages();
                        showPortImages();

                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to getAction data."

                        // Show ports and paths of touched form
                        for (int i = 0; i < getPortImages().size(); i++) {
                            PortImage portImage = getPortImages().get(i);
                            List<Path> paths = portImage.getPort().getGraph();
                            Log.v("TouchFrame", "\tpaths.size = " + paths.size());
                            for (int j = 0; j < paths.size(); j++) {
                                Path path = paths.get(j);
                                Log.v("TouchFrame", "\t\tsource = " + path.getSource());
                                Log.v("TouchFrame", "\t\ttarget = " + path.getTarget());
                                // Show ports
                                getScene().getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                                getScene().getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);
                                // Show path
                                getScene().getImage(path).setVisibility(Visibility.VISIBLE);
                            }
                        }

                        // Camera
                        camera.focusSelectBase(action);

                    } else {

                        // TODO: Release longer than tap!

                        if (process.getFirstAction().getTarget() instanceof BaseImage) {

                            if (action.getTarget() instanceof BaseImage) {

                                // If getFirstAction processAction was on the same form, then respond
                                if (process.getFirstAction().isPointing() && process.getFirstAction().getTarget() instanceof BaseImage) {

                                    // Base
                                    action.getTarget().processAction(action);

                                    // Camera
//                        camera.focusSelectScene();
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

    public List<PortImage> getPortImages() {
        List<PortImage> portImages = new ArrayList<>();

        for (int i = 0; i < getBase().getPorts().size(); i++) {
            Port port = getBase().getPorts().get(i);
            PortImage portImage = (PortImage) getScene().getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getScene().getModel(portImage);
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
                int intColor = getPortImages().get(i).getUniqueColor();
                String hexColor = camp.computer.clay.scene.util.Color.getHexColorString(intColor);
                lightShapes[i].setColor(hexColor);
            } else {
                lightShapes[i].setColor(camp.computer.clay.scene.util.Color.getHexColorString(PortImage.FLOW_PATH_COLOR_NONE));
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

            // Labels
            if (Application.ENABLE_GEOMETRY_LABELS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Rectangle boardShape = (Rectangle) getShape("board");
                Surface.drawCircle(getCoordinate(), boardShape.getWidth(), 0, surface);
                Surface.drawCircle(getCoordinate(), boardShape.getWidth() / 2.0f, 0, surface);
            }

            // Draw patches
            drawCandidatePatchImage(surface);
        }
    }

    public void showPortImages() {
        List<PortImage> portImages = getPortImages();
        for (int i = 0; i < portImages.size(); i++) {
            PortImage portImage = portImages.get(i);
            portImage.setVisibility(Visibility.VISIBLE);
            portImage.showDocks();
        }
    }

    public void hidePortImages() {
        List<PortImage> portImages = getPortImages();
        for (int i = 0; i < portImages.size(); i++) {
            PortImage portImage = portImages.get(i);
            portImage.setVisibility(Visibility.INVISIBLE);
        }
    }

    public void showPathImages() {
        List<PortImage> portImages = getPortImages();
        for (int i = 0; i < portImages.size(); i++) {
            PortImage portImage = portImages.get(i);
            portImage.setPathVisibility(Visibility.VISIBLE);
        }
    }

    public void hidePathImages() {
        List<PortImage> portImages = getPortImages();
        for (int i = 0; i < portImages.size(); i++) {
            PortImage portImage = portImages.get(i);
            portImage.setPathVisibility(Visibility.INVISIBLE);
            portImage.showDocks();
        }
    }

    public boolean contains(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getCoordinate().getX(), (int) this.getCoordinate().getY(), point.getX(), point.getY()) < (((Rectangle) getShape("board")).getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean contains(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getCoordinate().getX(), (int) this.getCoordinate().getY(), point.getX(), point.getY()) < (((Rectangle) getShape("board")).getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }


    private Visibility candidatePatchVisibility = Visibility.INVISIBLE;
    private Point candidatePatchCoordinate = new Point(40, 80);

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
                    getCoordinate(),
                    candidatePatchCoordinate
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.CYAN); // paint.setColor(getUniqueColor());
            Surface.drawRectangle(candidatePatchCoordinate, pathRotationAngle + 180, 250, 250, surface);

        }

    }
}

