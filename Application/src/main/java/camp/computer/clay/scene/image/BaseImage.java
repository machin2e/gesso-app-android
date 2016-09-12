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
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Process;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Circle;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

public class BaseImage extends Image<Base> {

    private Visibility orig_candidatePatchVisibility = Visibility.INVISIBLE;
    private Point orig_candidatePatchCoordinate = new Point(40, 80);

    private Visibility candidatePathVisibility = Visibility.INVISIBLE;
    private Point candidatePathSourceCoordinate = new Point(40, 80);
    private Point candidatePathDestinationCoordinate = new Point(40, 80);
    double shapeRadius = 40.0;

    private Visibility candidatePatchVisibility = Visibility.INVISIBLE;

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
        Circle circle = null;

        // Create shapes for figure
        rectangle = new Rectangle(250, 250);
        rectangle.setLabel("Board");
        rectangle.setColor("#f7f7f7");
        rectangle.setOutlineThickness(1);
        addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header 1");
        rectangle.setCoordinate(0, 132);
        // headerShape1.setRelativeRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        addShape(rectangle);
        rectangle.setLabel("Header 2");
        rectangle.setCoordinate(132, 0);
        rectangle.setRotation(90);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
//        addShape(rectangle);

        rectangle = new Rectangle(50, 14);
        addShape(rectangle);
        rectangle.setLabel("Header 3");
        rectangle.setCoordinate(0, -132);
        // headerShape3.setRelativeRotation(180);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
//        addShape(rectangle);

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
        // light1.setRelativeRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 2");
        rectangle.setCoordinate(0, 105);
        // light1.setRelativeRotation(90);
        addShape(rectangle);

        rectangle = new Rectangle(12, 20);
        rectangle.setLabel("LED 3");
        rectangle.setCoordinate(20, 105);
        // light1.setRelativeRotation(90);
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

        // Ports
        circle = new Circle(40);
        circle.setLabel("Port 1");
        circle.setCoordinate(-90, 300);
        // circle.setRelativeRotation(0);
        circle.setColor("#3f3f3f");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 2");
        circle.setCoordinate(0, 300);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 3");
        circle.setCoordinate(90, 300);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 4");
        circle.setCoordinate(300, 90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 5");
        circle.setCoordinate(300, 0);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 6");
        circle.setCoordinate(300, -90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 7");
        circle.setCoordinate(90, -300);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 8");
        circle.setCoordinate(0, -300);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 9");
        circle.setCoordinate(-90, -300);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 10");
        circle.setCoordinate(-300, -90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 11");
        circle.setCoordinate(-300, 0);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle(40);
        circle.setLabel("Port 12");
        circle.setCoordinate(-300, 90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

    }

    private void setupActions() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Process process = action.getProcess();

                Camera camera = action.getActor().getCamera();

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.SELECT) {

                } else if (action.getType() == Action.Type.HOLD) {

                } else if (action.getType() == Action.Type.MOVE) {

                    if (action.getTargetShape().getLabel().equals("Board")) {

                        // Holding
                        if (process.isHolding()) {

                            // Holding and dragging

                            // Base
                            action.getTargetImage().processAction(action);
                            action.getTargetImage().setCoordinate(action.getCoordinate());

                            // Camera
                            camera.focusSelectBase(action);

                        } else {


                            // Update position
                            // action.getTargetImage().setCoordinate(action.getCoordinate());

                            hidePortImages();
                            hidePathImages();

                            orig_candidatePatchCoordinate.set(action.getCoordinate());

                            setOrig_candidatePatchVisibility(Visibility.VISIBLE);

                        }

                    } else if (action.getTargetShape().getLabel().startsWith("Port")) {

                        if (process.isHolding()) {

                            // Holding and dragging

                            // Port
                            PortImage portFigure = (PortImage) action.getTargetImage();

                            portFigure.setDragging(true);
                            portFigure.setCoordinate(action.getCoordinate());

                        } else {

                            // Candidate Path Visibility
                            setCandidatePathDestinationCoordinate(action.getCoordinate());
                            setCandidatePathVisibility(Visibility.VISIBLE);


                            candidatePathSourceCoordinate = new Point(process.getStartAction().getTargetShape().getCoordinate());

                            // Candidate Patch Visibility

                            boolean isCreatePatchAction = true;
                            List<Image> images = getScene().getImages(Base.class, Patch.class).getList();
                            for (int i = 0; i < images.size(); i++) {
                                Image nearbyImage = images.get(i);

                                // Update style of nearby machines
                                double distanceToBaseImage = Geometry.calculateDistance(
                                        action.getCoordinate(), //candidatePathDestinationCoordinate,
                                        nearbyImage.getCoordinate()
                                );

                                if (distanceToBaseImage < 500) {
                                    isCreatePatchAction = false;
                                    break;
                                }

                                // TODO: if distance > 800: connect to cloud service
                            }

                            if (isCreatePatchAction) {
                                setCandidatePatchVisibility(Visibility.VISIBLE);
                            } else {
                                setCandidatePatchVisibility(Visibility.INVISIBLE);
                            }

//                            // Port type and flow direction
//                            Port port = getPort();
//                            if (port.getDirection() == Port.Direction.NONE) {
//                                port.setDirection(Port.Direction.INPUT);
//                            }
//                            if (port.getType() == Port.Type.NONE) {
//                                port.setType(Port.Type.next(port.getType()));
//                            }

                            // Camera
//                            Camera camera = action.getActor().getCamera();
                            camera.focusCreatePath(action);
                        }

                    } else if (action.getTargetShape().getLabel().startsWith("LED")) {

                    }

                } else if (action.getType() == Action.Type.UNSELECT) {

                    Image targetImage = scene.getImageByCoordinate(action.getCoordinate());
                    action.setTargetImage(targetImage);



                    // Check if shapes are touched...
//                    for (int i = 0; i < shapes.size(); i++) {
//                        if (shapes.get(i) instanceof Circle) {
//                            if (shapes.get(i).contains(action.getCoordinate())) {
//                                Log.v("Shape", "Touched shape " + shapes.get(i));
//                                shapes.get(i).setVisibility(Visibility.INVISIBLE);
//                                return;
//                            }
//                        }
//                    }

                    if (action.getTargetShape().getLabel().startsWith("Port")) {

                        action.getTargetShape().setColor("#ff00ffff");

                    } else if (action.getTargetShape().getLabel().equals("Board")) {

                        if (process.isTap()) {

                            // Focus on touched form
                            showPathImages();
                            showPortImages();

                            setTransparency(1.0);

                            // TODO: Speak "choose a channel to getAction data."

                            // Show ports and paths of touched form
                            for (int i = 0; i < getPortImages().size(); i++) {
                                PortImage portImage = getPortImages().get(i);
                                List<Path> paths = portImage.getPort().getNetwork();
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

                            if (process.getStartAction().getTargetImage() instanceof BaseImage) {

                                if (action.getTargetImage() instanceof BaseImage) {

                                    // If getStartAction processAction was on the same form, then respond
                                    if (process.getStartAction().isPointing() && process.getStartAction().getTargetImage() instanceof BaseImage) {

                                        // Base
                                        action.getTargetImage().processAction(action);

                                        // Camera
//                        camera.focusSelectScene();
                                    }

                                } else if (action.getTargetImage() instanceof Scene) {

                                    // Base
                                    process.getStartAction().getTargetImage().processAction(action);

                                }

                            }

                        }

                        // Check if connecting to a patch
                        if (getOrig_candidatePatchVisibility() == Visibility.VISIBLE) {

                            // Show patch store
                            Application.getDisplay().displayChooseDialog();
//                        Application.getDisplay().displayTasksDialog();

                            setOrig_candidatePatchVisibility(Visibility.INVISIBLE);
                        }
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
                shapes.get(i).draw(surface);
            }

            // Labels
            if (Application.ENABLE_GEOMETRY_LABELS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Rectangle boardShape = (Rectangle) getShape("Board");
                Surface.drawCircle(getCoordinate(), boardShape.getWidth(), 0, surface);
                Surface.drawCircle(getCoordinate(), boardShape.getWidth() / 2.0f, 0, surface);
            }

            // Draw candidate paths
            drawCandidatePathImages(surface);

            // Draw candidate patches
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

        getShape("Port 1").setVisibility(Visibility.VISIBLE);
        getShape("Port 2").setVisibility(Visibility.VISIBLE);
        getShape("Port 3").setVisibility(Visibility.VISIBLE);
        getShape("Port 4").setVisibility(Visibility.VISIBLE);
        getShape("Port 5").setVisibility(Visibility.VISIBLE);
        getShape("Port 6").setVisibility(Visibility.VISIBLE);
        getShape("Port 7").setVisibility(Visibility.VISIBLE);
        getShape("Port 8").setVisibility(Visibility.VISIBLE);
        getShape("Port 9").setVisibility(Visibility.VISIBLE);
        getShape("Port 10").setVisibility(Visibility.VISIBLE);
        getShape("Port 11").setVisibility(Visibility.VISIBLE);
        getShape("Port 12").setVisibility(Visibility.VISIBLE);
    }

    public void hidePortImages() {
        List<PortImage> portImages = getPortImages();
        for (int i = 0; i < portImages.size(); i++) {
            PortImage portImage = portImages.get(i);
            portImage.setVisibility(Visibility.INVISIBLE);
        }

        getShape("Port 1").setVisibility(Visibility.INVISIBLE);
        getShape("Port 2").setVisibility(Visibility.INVISIBLE);
        getShape("Port 3").setVisibility(Visibility.INVISIBLE);
        getShape("Port 4").setVisibility(Visibility.INVISIBLE);
        getShape("Port 5").setVisibility(Visibility.INVISIBLE);
        getShape("Port 6").setVisibility(Visibility.INVISIBLE);
        getShape("Port 7").setVisibility(Visibility.INVISIBLE);
        getShape("Port 8").setVisibility(Visibility.INVISIBLE);
        getShape("Port 9").setVisibility(Visibility.INVISIBLE);
        getShape("Port 10").setVisibility(Visibility.INVISIBLE);
        getShape("Port 11").setVisibility(Visibility.INVISIBLE);
        getShape("Port 12").setVisibility(Visibility.INVISIBLE);
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

    public void setOrig_candidatePatchVisibility(Visibility visibility) {
        orig_candidatePatchVisibility = visibility;
    }

    public Visibility getOrig_candidatePatchVisibility() {
        return orig_candidatePatchVisibility;
    }

    private void drawCandidatePatchImage(Surface surface) {

        if (orig_candidatePatchVisibility == Visibility.VISIBLE) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    getCoordinate(),
                    orig_candidatePatchCoordinate
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.CYAN); // paint.setColor(getUniqueColor());
            Surface.drawRectangle(orig_candidatePatchCoordinate, pathRotationAngle + 180, 250, 250, surface);

        }

    }

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawCandidatePathImages(Surface surface) {
        if (candidatePathVisibility == Visibility.VISIBLE) {

//            if (getPort().getType() != Port.Type.NONE) {

                Canvas canvas = surface.getCanvas();
                Paint paint = surface.getPaint();

                double triangleWidth = 20;
                double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
                double triangleSpacing = 35;

                // Color
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(15.0f);
//                paint.setColor(this.getUniqueColor());

                double pathRotationAngle = Geometry.calculateRotationAngle(
                        //getCoordinate(),
                        candidatePathSourceCoordinate,
                        candidatePathDestinationCoordinate
                );

                Point pathStartCoordinate = Geometry.calculatePoint(
                        // getCoordinate(),
                        candidatePathSourceCoordinate,
                        pathRotationAngle,
                        2 * triangleSpacing
                );

                Point pathStopCoordinate = Geometry.calculatePoint(
                        candidatePathDestinationCoordinate,
                        pathRotationAngle + 180,
                        2 * triangleSpacing
                );

                Surface.drawTrianglePath(
                        pathStartCoordinate,
                        pathStopCoordinate,
                        triangleWidth,
                        triangleHeight,
                        surface
                );

                // Color
                paint.setStyle(Paint.Style.FILL);
//                paint.setColor(getUniqueColor());
                Surface.drawCircle(candidatePathDestinationCoordinate, shapeRadius, 0.0f, surface);
//            }
        }
    }

    public void setCandidatePathVisibility(Visibility visibility) {
        candidatePathVisibility = visibility;
    }

    public Visibility getCandidatePathVisibility() {
        return candidatePathVisibility;
    }

    public void setCandidatePatchVisibility(Visibility visibility) {
        candidatePatchVisibility = visibility;
    }

    public Visibility getCandidatePatchVisibility() {
        return candidatePatchVisibility;
    }

    public void setCandidatePathDestinationCoordinate(Point position) {
        this.candidatePathDestinationCoordinate.set(position);
    }
}

