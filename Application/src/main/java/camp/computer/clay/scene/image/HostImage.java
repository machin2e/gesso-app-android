package camp.computer.clay.scene.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.EventListener;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.architecture.ImageGroup;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.architecture.ShapeGroup;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Circle;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

public class HostImage extends Image<Host> {

    private Visibility orig_candidatePatchVisibility = Visibility.INVISIBLE;
    private Point orig_candidatePatchCoordinate = new Point(40, 80);

    private Visibility candidatePathVisibility = Visibility.INVISIBLE;
    private Point candidatePathSourceCoordinate = new Point(40, 80);
    private Point candidatePathDestinationCoordinate = new Point(40, 80);
    double shapeRadius = 40.0;

    private Visibility candidatePatchVisibility = Visibility.INVISIBLE;

    public HostImage(Host host) {
        super(host);
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
        rectangle = new Rectangle<Host>(getFeature());
        rectangle.setWidth(250);
        rectangle.setHeight(250);
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
        circle = new Circle<Port>(getHost().getPort(0));
        circle.setRadius(40);
        circle.setLabel("Port 1");
        circle.setCoordinate(-90, 200);
        // circle.setRelativeRotation(0);
//        circle.setColor("#3f3f3f");
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(1));
        circle.setRadius(40);
        circle.setLabel("Port 2");
        circle.setCoordinate(0, 200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(2));
        circle.setRadius(40);
        circle.setLabel("Port 3");
        circle.setCoordinate(90, 200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(3));
        circle.setRadius(40);
        circle.setLabel("Port 4");
        circle.setCoordinate(200, 90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(4));
        circle.setRadius(40);
        circle.setLabel("Port 5");
        circle.setCoordinate(200, 0);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(5));
        circle.setRadius(40);
        circle.setLabel("Port 6");
        circle.setCoordinate(200, -90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(6));
        circle.setRadius(40);
        circle.setLabel("Port 7");
        circle.setCoordinate(90, -200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(7));
        circle.setRadius(40);
        circle.setLabel("Port 8");
        circle.setCoordinate(0, -200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(8));
        circle.setRadius(40);
        circle.setLabel("Port 9");
        circle.setCoordinate(-90, -200);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(9));
        circle.setRadius(40);
        circle.setLabel("Port 10");
        circle.setCoordinate(-200, -90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(10));
        circle.setRadius(40);
        circle.setLabel("Port 11");
        circle.setCoordinate(-200, 0);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

        circle = new Circle<Port>(getHost().getPort(11));
        circle.setRadius(40);
        circle.setLabel("Port 12");
        circle.setCoordinate(-200, 90);
        // circle.setRelativeRotation(0);
        circle.setColor("#efefef");
        circle.setOutlineThickness(0);
        circle.setVisibility(Visibility.INVISIBLE);
        addShape(circle);

    }

    private void setupActions() {

        setOnActionListener(new EventListener() {
                                @Override
                                public void onAction(Action action) {

                                    Event event = action.getLastEvent();

                                    Camera camera = event.getActor().getCamera();

                                    if (event.getType() == Event.Type.NONE) {

                                    } else if (event.getType() == Event.Type.SELECT) {

                                    } else if (event.getType() == Event.Type.HOLD) {

                                    } else if (event.getType() == Event.Type.MOVE) {

                                        if (action.getFirstEvent().getTargetShape() == null) {
                                            return;
                                        }

                                        if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                                            // Holding
                                            if (action.isHolding()) {

                                                // Holding and dragging

                                                // Host
                                                event.getTargetImage().processAction(action);
                                                event.getTargetImage().setCoordinate(event.getCoordinate());

                                                // Camera
                                                camera.focusSelectHost(event);

                                            } else {


                                                // Update position
                                                // event.getTargetImage().setCoordinate(event.getCoordinate());

                                                hidePortShapes();
                                                hidePathImages();

                                                orig_candidatePatchCoordinate.set(event.getCoordinate());

                                                setOrig_candidatePatchVisibility(Visibility.VISIBLE);

                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                                            if (!action.isHolding()) {

                                                // Candidate Path Visibility
                                                setCandidatePathDestinationCoordinate(event.getCoordinate());
                                                setCandidatePathVisibility(Visibility.VISIBLE);

                                                candidatePathSourceCoordinate = action.getFirstEvent().getTargetShape().getCoordinate();

                                                // Candidate Extension Visibility

                                                boolean isCreatePatchAction = true;
                                                List<Image> images = getScene().getImages(Host.class, Extension.class).getList();
                                                for (int i = 0; i < images.size(); i++) {
                                                    Image nearbyImage = images.get(i);

                                                    // Update style of nearby machines
                                                    double distanceToBaseImage = Geometry.calculateDistance(
                                                            event.getCoordinate(), //candidatePathDestinationCoordinate,
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

                                                // Get port associated with the touched port shape
                                                Port port = null;
                                                if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 1")) {
                                                    port = getHost().getPort(0);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 2")) {
                                                    port = getHost().getPort(1);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 3")) {
                                                    port = getHost().getPort(2);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 4")) {
                                                    port = getHost().getPort(3);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 5")) {
                                                    port = getHost().getPort(4);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 6")) {
                                                    port = getHost().getPort(5);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 7")) {
                                                    port = getHost().getPort(6);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 8")) {
                                                    port = getHost().getPort(7);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 9")) {
                                                    port = getHost().getPort(8);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 10")) {
                                                    port = getHost().getPort(9);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 11")) {
                                                    port = getHost().getPort(10);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 12")) {
                                                    port = getHost().getPort(11);
                                                }

//                            // Port type and flow direction
//                            Port port = getPort();
                                                if (port != null) {
                                                    // Update data model
                                                    if (port.getDirection() == Port.Direction.NONE) {
                                                        port.setDirection(Port.Direction.INPUT);
                                                    }
                                                    if (port.getType() == Port.Type.NONE) {
                                                        port.setType(Port.Type.next(port.getType()));
                                                    }
                                                }

                                                // Show nearby ports
                                                Port sourcePort = (Port) action.getFirstEvent().getTargetShape().getFeature();
                                                Event lastEvent = action.getLastEvent();

                                                ImageGroup nearbyImages = getScene().getImages(Host.class, Extension.class).filterArea(lastEvent.getCoordinate(), 200 + 60);
                                                List<Image> images2 = getScene().getImages(Host.class, Extension.class).getList();

                                                // Show ports of nearby forms
                                                for (int i = 0; i < images2.size(); i++) {
                                                    Image image = images2.get(i);

                                                    //if (image == portFigure.getParentImage() || nearbyImages.contains(image)) {
                                                    //if (image == boardImage /* || nearbyImages.contains(image) */) {
                                                    if (image.getFeature() == sourcePort.getParent() || nearbyImages.contains(image)) {

//                                    if (image instanceof HostImage) {
                                                        HostImage nearbyFigure = (HostImage) image;
                                                        nearbyFigure.setTransparency(1.0f);
                                                        nearbyFigure.showPortShapes();
//                                    } else if (image instanceof ExtensionImage) {
//                                        ExtensionImage nearbyFigure = (ExtensionImage) image;
//
//                                    }

                                                    } else {

//                                    if (image instanceof HostImage) {
                                                        HostImage nearbyFigure = (HostImage) image;
                                                        nearbyFigure.setTransparency(0.1f);
                                                        nearbyFigure.hidePortShapes();
//                                    } else if (image instanceof ExtensionImage) {
//                                        ExtensionImage nearbyFigure = (ExtensionImage) image;
//                                        nearbyFigure.setTransparency(0.1f);
//                                        nearbyFigure.hidePortImages();
//                                    }

                                                    }
                                                }

                                            } else if (action.isHolding()) {

//                            // Holding and dragging
//
//                            // Port
//                            PortImage portImage = (PortImage) event.getTargetImage();
//
//                            portImage.setDragging(true);
//                            portImage.setCoordinate(event.getCoordinate());
                                            }

                                            // Camera
                                            camera.focusCreatePath(action);

                                        } else if (event.getTargetShape().getLabel().startsWith("LED")) {

                                        }

                                    } else if (event.getType() == Event.Type.UNSELECT) {

                                        if (action.getFirstEvent().getTargetShape() == null) {
                                            return;
                                        }

                                        Image targetImage = scene.getImageByCoordinate(event.getCoordinate());
                                        event.setTargetImage(targetImage);

                                        // Check if shapes are touched...
//                    for (int i = 0; i < shapes.size(); i++) {
//                        if (shapes.get(i) instanceof Circle) {
//                            if (shapes.get(i).contains(event.getCoordinate())) {
//                                Log.v("Shape", "Touched shape " + shapes.get(i));
//                                shapes.get(i).setVisibility(Visibility.INVISIBLE);
//                                return;
//                            }
//                        }
//                    }

                                        if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                                            if (action.isTap()) {

                                                // Get port associated with the touched port shape
                                                Port port = null;
                                                int index = -1;
                                                if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 1")) {
                                                    port = getHost().getPort(0);
                                                    index = 0;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 2")) {
                                                    port = getHost().getPort(1);
                                                    index = 1;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 3")) {
                                                    port = getHost().getPort(2);
                                                    index = 2;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 4")) {
                                                    port = getHost().getPort(3);
                                                    index = 3;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 5")) {
                                                    port = getHost().getPort(4);
                                                    index = 4;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 6")) {
                                                    port = getHost().getPort(5);
                                                    index = 5;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 7")) {
                                                    port = getHost().getPort(6);
                                                    index = 6;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 8")) {
                                                    port = getHost().getPort(7);
                                                    index = 7;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 9")) {
                                                    port = getHost().getPort(8);
                                                    index = 8;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 10")) {
                                                    port = getHost().getPort(9);
                                                    index = 9;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 11")) {
                                                    port = getHost().getPort(10);
                                                    index = 10;
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 12")) {
                                                    port = getHost().getPort(11);
                                                    index = 11;
                                                }

//                            // Port type and flow direction
//                            Port port = getPort();
                                                if (port != null) {
                                                    // Update data model
//                                if (port.getDirection() == Port.Direction.NONE) {
//                                    port.setDirection(Port.Direction.INPUT);
//                                }
//                                if (port.getType() == Port.Type.NONE) {
                                                    port.setType(Port.Type.next(port.getType()));
//                                }

                                                    // Update style
                                                    if (port.getType() == Port.Type.NONE) {
                                                        action.getFirstEvent().getTargetShape().setColor("#ffe2e2e2");
                                                    } else if (port.getType() == Port.Type.SWITCH) {
                                                        action.getFirstEvent().getTargetShape().setColor("#ffff0000");
                                                    } else if (port.getType() == Port.Type.PULSE) {
                                                        action.getFirstEvent().getTargetShape().setColor("#ff00ff00");
                                                    } else if (port.getType() == Port.Type.WAVE) {
                                                        action.getFirstEvent().getTargetShape().setColor("#ff0000ff");
                                                    }
                                                }

                                                if (port.getType() == Port.Type.NONE) {

                                                    Log.v("TouchPort", "A");

                                                    port.setDirection(Port.Direction.INPUT);
                                                    port.setType(Port.Type.next(port.getType()));

                                                    // TODO: Speak ~ "setting as input. you can send the data to another board if you want. pointerCoordinates another board."

                                                } else if (!port.hasPath() && port.getAncestorPaths().size() == 0) {

                                                    Log.v("TouchPort", "B");

                                                    // TODO: Replace with state of camera. i.e., Check if seeing a single path.

                                                    Port.Type nextType = port.getType();
                                                    while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                                        nextType = Port.Type.next(nextType);
                                                    }
                                                    port.setType(nextType);

                                                } else if (!hasVisiblePaths(index) && !hasVisibleAncestorPaths(index)) {

                                                    Log.v("TouchPort", "C");

                                                    // Remove focus from other hosts and their ports
                                                    List<Image> hostImages = getScene().getImages(Host.class).getList();
                                                    for (int i = 0; i < hostImages.size(); i++) {
                                                        HostImage hostImage = (HostImage) hostImages.get(i);
                                                        hostImage.hidePortShapes();
                                                        hostImage.hidePathImages();

                                                        // Get shapes in image matching labels "Board", "Header <number>", and "LED <number>"
                                                        ShapeGroup shapes = hostImage.getShapes().filterLabel("^Board$", "^Header (1|2|3|4)$", "^LED (1[0-2]|[1-9])$");
                                                        shapes.setTransparency(0.1);
                                                    }

                                                    List<Image> extensionImages = getScene().getImages().filterType(Extension.class).getList();
                                                    for (int i = 0; i < extensionImages.size(); i++) {
                                                        ExtensionImage extensionImage = (ExtensionImage) extensionImages.get(i);
                                                        if (extensionImage.getExtension() != getParentImage().getFeature()) {
                                                            extensionImage.setTransparency(0.1);
                                                            extensionImage.hidePortImages();
                                                            extensionImage.hidePathImages();
                                                        }
                                                    }

                                                    // Focus on the port
                                                    showPaths(port);
                                                    setVisibility(Visibility.VISIBLE);
                                                    setPathVisibility(port, Visibility.VISIBLE);

                                                    List<Path> paths = port.getAllPaths();
                                                    for (Path connectedPath : paths) {

                                                        // Show ports
                                                        getScene().getShape(connectedPath.getSource()).setVisibility(Visibility.VISIBLE);
                                                        getScene().getShape(connectedPath.getTarget()).setVisibility(Visibility.VISIBLE);

                                                        // Show path
                                                        getScene().getImage(connectedPath).setVisibility(Visibility.VISIBLE);
                                                    }

                                                    // ApplicationView.getLauncherView().speakPhrase("setting as input. you can send the data to another board if you want. pointerCoordinates another board.");

                                                    // Camera
                                                    List<Port> pathPorts = port.getPorts(paths);
                                                    List<Image> pathPortImages = getScene().getImages(pathPorts);
                                                    List<Point> pathPortCoordinates = Scene.getCoordinates(pathPortImages);
                                                    Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortCoordinates);
                                                    getScene().getFeature().getActor(0).getCamera().adjustScale(boundingBox);

                                                    getScene().getFeature().getActor(0).getCamera().setCoordinate(Geometry.calculateCenterCoordinate(pathPortCoordinates));

                                                } else if (hasVisiblePaths(index) || hasVisibleAncestorPaths(index)) {

                                                    Log.v("TouchPort", "D");

                                                    // Paths are being shown. Touching a port changes the port type. This will also
                                                    // updates the corresponding path requirement.

                                                    // TODO: Replace with state of camera. i.e., Check if seeing a single path.

                                                    Port.Type nextType = port.getType();
                                                    while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                                        nextType = Port.Type.next(nextType);
                                                    }
                                                    port.setType(nextType);

                                                }

                                                setCandidatePathVisibility(Visibility.INVISIBLE);

                                            } else {

                                                if (action.getTargetFeature() instanceof Port) { // TODO: Refactor

                                                    // Port
                                                    // ...getLastEvent processAction was on a port image.

                                                    // PortImage portImage = (PortImage) event.getImageByCoordinate();
                                                    Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                                    //if (sourcePortImage.isDragging()) {
                                                    if (action.isDragging()) {

//                                                        Log.v("Events", "A");
//
//                                                        // Get nearest port image
//                                                        Shape nearestPortShape = getScene().getShapes(Port.class).getNearest(event.getCoordinate());
//                                                        Port nearestPort = (Port) nearestPortShape.getFeature();
//                                                        Log.v("DND", "nearestPort: " + nearestPort);
//
//                                                        // TODO: When dragging, enable pushing ports?
//
//                                                        Port sourcePort = (Port) sourcePortShape.getFeature();
//                                                        Log.v("DND", "sourcePort: " + sourcePort);
//
//                                                        List<Path> paths = getScene().getFeature().getPaths();
//
//                                                        // Copy configuration
//                                                        nearestPort.setDirection(sourcePort.getDirection());
//                                                        nearestPort.setType(sourcePort.getType());
//                                                        //nearestPortImage.setUniqueColor(sourcePortShape.getUniqueColor());
//
//                                                        // Reset port configuration
//                                                        sourcePort.setDirection(Port.Direction.NONE);
//                                                        sourcePort.setType(Port.Type.NONE);
//                                                        //sourcePortImage.updateUniqueColor();
//
//                                                        // Clear the port's list of paths
//                                                        sourcePort.getPaths().clear();
//
//                                                        // Copy paths
//                                                        for (Path path : paths) {
//
//                                                            // Update source
//                                                            if (path.getSource() == sourcePort) {
//                                                                path.setSource(nearestPort);
//                                                                Log.v("DND", "Updating source");
//                                                            }
//
//                                                            // Update target
//                                                            if (path.getTarget() == sourcePort) {
//                                                                path.setTarget(nearestPort);
//                                                                Log.v("DND", "Updating target");
//                                                            }
//
//                                                            nearestPort.addPath(path);
//
//                                                        }
//
//                                                        // Restore port image's position
//                                                        //sourcePortImage.setDragging(false);
//
//                                                        // Camera
//                                                        event.getActor().getCamera().focusSelectPath(sourcePort);

//                                                    } else {

                                                        // Show ports of nearby forms
                                                        boolean useNearbyPortShape = false;

                                                        Log.v("Events", "B");

                                                        // TODO: Use overlappedImage instanceof PortImage

//                                                        for (PortImage nearbyPortImage : nearbyBaseImage.getPortShapes()) {
                                                        //List<Image> nearbyPortShapes = getScene().getImages(Port.class).getList();
                                                        List<Shape> nearbyPortShapes = getScene().getShapes(Port.class).getList();
                                                        for (int i = 0; i < nearbyPortShapes.size(); i++) {
                                                            Shape nearbyPortShape = nearbyPortShapes.get(i);

                                                            if (nearbyPortShape != sourcePortShape) {
                                                                if (nearbyPortShape.contains(event.getCoordinate())) {

                                                                    Log.v("Events", "C");

                                                                    Port port = (Port) sourcePortShape.getFeature();
                                                                    Port nearbyPort = (Port) nearbyPortShape.getFeature();

                                                                    useNearbyPortShape = true;

                                                                    if (port.getDirection() == Port.Direction.NONE) {
                                                                        port.setDirection(Port.Direction.INPUT);
                                                                    }
                                                                    if (port.getType() == Port.Type.NONE) {
                                                                        port.setType(Port.Type.next(port.getType())); // (machineSprite.channelTypes.getEvent(i) + 1) % machineSprite.channelTypeColors.length
                                                                    }

                                                                    nearbyPort.setDirection(Port.Direction.OUTPUT);
                                                                    nearbyPort.setType(Port.Type.next(nearbyPort.getType()));

                                                                    // Create and addEvent path to port
//                                                                    Port sourcePort = (Port) getScene().getFeature(sourcePortShape);
//                                                                    Port targetPort = (Port) getScene().getFeature(nearbyPortShape);
                                                                    Port sourcePort = (Port) sourcePortShape.getFeature();
                                                                    Port targetPort = (Port) nearbyPortShape.getFeature();

                                                                    Log.v("Events", "targetPort: " + targetPort);

                                                                    if (!sourcePort.hasAncestor(targetPort)) {

                                                                        Log.v("Events", "D.1");

                                                                        Path path = new Path(sourcePort, targetPort);

                                                                        if (sourcePort.getParent() instanceof Extension || targetPort.getParent() instanceof Extension) {
                                                                            path.setType(Path.Type.ELECTRONIC);
                                                                        } else {
                                                                            path.setType(Path.Type.MESH);
                                                                        }

                                                                        sourcePort.addPath(path);

                                                                        scene.addFeature(path);

                                                                        //PortImage targetPortImage = (PortImage) getScene().getImage(path.getTargetFeature());
                                                                        //targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                                                        // Camera
                                                                        event.getActor().getCamera().focusSelectPath(sourcePort);
                                                                    }

                                                                    break;
                                                                }
                                                            }
                                                        }

                                                        if (!useNearbyPortShape) {

                                                            Port port = (Port) sourcePortShape.getFeature();

                                                            port.setDirection(Port.Direction.INPUT);

                                                            if (port.getType() == Port.Type.NONE) {
                                                                port.setType(Port.Type.next(port.getType()));
                                                            }
                                                        }

                                                        setCandidatePathVisibility(Visibility.INVISIBLE);

                                                    }
                                                }

//                        action.getFirstEvent().getTargetShape().setColor("#ff00ffff");
                                                // Get port associated with the touched port shape
                                                Port port = null;
                                                if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 1")) {
                                                    port = getHost().getPort(0);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 2")) {
                                                    port = getHost().getPort(1);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 3")) {
                                                    port = getHost().getPort(2);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 4")) {
                                                    port = getHost().getPort(3);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 5")) {
                                                    port = getHost().getPort(4);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 6")) {
                                                    port = getHost().getPort(5);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 7")) {
                                                    port = getHost().getPort(6);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 8")) {
                                                    port = getHost().getPort(7);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 9")) {
                                                    port = getHost().getPort(8);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 10")) {
                                                    port = getHost().getPort(9);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 11")) {
                                                    port = getHost().getPort(10);
                                                } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Port 12")) {
                                                    port = getHost().getPort(11);
                                                }

//                            // Port type and flow direction
//                            Port port = getPort();
                                                if (port != null) {
                                                    // Update data model
                                                    if (port.getDirection() == Port.Direction.NONE) {
                                                        port.setDirection(Port.Direction.INPUT);
                                                    }
                                                    if (port.getType() == Port.Type.NONE) {
                                                        port.setType(Port.Type.next(port.getType()));
                                                    }

                                                    // Update style
                                                    if (port.getType() == Port.Type.SWITCH) {
                                                        action.getFirstEvent().getTargetShape().setColor("#ffff0000");
                                                    } else if (port.getType() == Port.Type.PULSE) {
                                                        action.getFirstEvent().getTargetShape().setColor("#ff00ff00");
                                                    } else if (port.getType() == Port.Type.WAVE) {
                                                        action.getFirstEvent().getTargetShape().setColor("#ff0000ff");
                                                    }
                                                }

                                                setCandidatePathVisibility(Visibility.INVISIBLE);
                                            }

                                        } else if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                                            if (action.isTap()) {

                                                // Focus on touched form
                                                showPathImages();
                                                showPortShapes();

                                                setTransparency(1.0);

                                                // TODO: Speak "choose a channel to getEvent data."

                                                // Show ports and paths of touched form
                                                for (int i = 0; i < getPortShapes().size(); i++) {
                                                    List<Path> paths = getPort(i).getAllPaths();

                                                    for (int j = 0; j < paths.size(); j++) {
                                                        Path path = paths.get(j);

                                                        Log.v("Events2", "path.getSource(): " + path.getSource());
                                                        Log.v("Events2", "path.getTarget(): " + path.getTarget());

                                                        // Show source and target ports in path
                                                        //getPortShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                        getScene().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                                        getScene().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                                        // Show path connection
                                                        getScene().getImage(path).setVisibility(Visibility.VISIBLE);
                                                    }
                                                }

                                                // Camera
                                                camera.focusSelectHost(event);

                                            } else {

                                                // TODO: Release longer than tap!

                                                if (action.getFirstEvent().getTargetImage() instanceof HostImage) {

                                                    if (event.getTargetImage() instanceof HostImage) {

                                                        // If getFirstEvent processAction was on the same form, then respond
                                                        if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage() instanceof HostImage) {

                                                            // Host
                                                            event.getTargetImage().processAction(action);

                                                            // Camera
//                        camera.focusSelectScene();
                                                        }

                                                    } else if (event.getTargetImage() instanceof Scene) {

                                                        // Host
                                                        action.getFirstEvent().getTargetImage().processAction(action);

                                                    }

                                                }

                                            }

                                            // Check if connecting to a patch
                                            if (getOrig_candidatePatchVisibility() == Visibility.VISIBLE) {

                                                // Show patch store
                                                Launcher.getLauncherView().displayChooseDialog();
//                        Launcher.getLauncherView().displayTasksDialog();

                                                setOrig_candidatePatchVisibility(Visibility.INVISIBLE);
                                            }
                                        }

                                    }
                                }
                            }

        );
    }

    // <REFACTOR>

    public Port getPort(int index) {
        return getHost().getPort(index);
    }

    public List<PathImage> getPathImages(int index) {
        List<PathImage> pathImages = new ArrayList<>();
        for (Path path : getPort(index).getPaths()) {
            PathImage pathImage = (PathImage) getScene().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    public boolean hasVisiblePaths(int index) {
        for (PathImage pathImage : getPathImages(index)) {
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths(int index) {
        List<Path> ancestorPaths = getPort(index).getAncestorPaths();
        for (Path ancestorPath : ancestorPaths) {
            PathImage pathImage = (PathImage) getScene().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    // </REFACTOR>

    // TODO: Delete this
    public Host getHost() {
        return getFeature();
    }

    public List<Shape> getPortShapes() {
        List<Shape> portShapes = new LinkedList<>();

        for (int i = 0; i < this.shapes.size(); i++) {
            Shape shape = this.shapes.get(i);
            if (shape.getLabel().startsWith("Port ")) {
                portShapes.add(shape);
            }
        }

        return portShapes;
    }

    public Shape getPortShape(Port port) {
        for (int i = 0; i < this.shapes.size(); i++) {
            Shape shape = this.shapes.get(i);
            if (shape.getFeature() == port) {
                return shape;
            }
        }
        return null;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getScene().getFeature(portImage);
        if (getHost().getPorts().contains(port)) {
            return this.getHost().getPorts().indexOf(port);
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

//        for (int i = 0; i < lightShapes.length; i++) {
//            Port port = getHost().getPort(i);
//            if (port.getType() != Port.Type.NONE) {
//                int intColor = getPortShapes().get(i).getUniqueColor();
//                String hexColor = camp.computer.clay.scene.util.Color.getHexColorString(intColor);
//                lightShapes[i].setColor(hexColor);
//            } else {
//                lightShapes[i].setColor(camp.computer.clay.scene.util.Color.getHexColorString(PortImage.FLOW_PATH_COLOR_NONE));
//            }
//        }


//        String transparencyString = String.format("%02x", (int) transparency * 255);
//
//        // Host color
//        color = Color.parseColor("#" + transparencyString + colorString);
//        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);
//
//        // Header color
//        portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
//        portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);

//        updatePortGroupFigures();
    }

    public void draw(Display display) {
        if (isVisible()) {

            // Color
            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).draw(display);
            }

            // Labels
            if (Launcher.ENABLE_GEOMETRY_LABELS) {
                display.getPaint().setColor(Color.GREEN);
                display.getPaint().setStyle(Paint.Style.STROKE);
                Rectangle boardShape = (Rectangle) getShape("Board");
                Display.drawCircle(getCoordinate(), boardShape.getWidth(), 0, display);
                Display.drawCircle(getCoordinate(), boardShape.getWidth() / 2.0f, 0, display);
            }

            // Draw candidate paths
            drawCandidatePathImages(display);

            // Draw candidate patches
            drawCandidatePatchImage(display);
        }
    }

    // TODO: Refactor
    public void showPortShapes() {

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

    // TODO: Refactor
    public void hidePortShapes() {

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

    public List<PathImage> getPathImages(Port port) {
        List<PathImage> pathImages = new ArrayList<>();
        for (Path path : port.getPaths()) {
            PathImage pathImage = (PathImage) getScene().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    public void showPaths(Port port) {
        for (PathImage pathImage : getPathImages(port)) {
            pathImage.showDocks = false;

            // Deep
            Port targetPort = pathImage.getPath().getTarget();
            Host targetHost = (Host) targetPort.getParent();
            HostImage targetHostImage = (HostImage) getScene().getImage(targetHost);

            //PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
            targetHostImage.showPaths(targetPort);
        }
    }

    public void showDocks(Port port) {
        for (PathImage pathImage : getPathImages(port)) {
            pathImage.showDocks = true;

            // Deep
//            PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
//            targetPortImage.showDocks();
            Port targetPort = pathImage.getPath().getTarget();
            Host targetHost = (Host) targetPort.getParent();
            HostImage targetHostImage = (HostImage) getScene().getImage(targetHost);

            //PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
            targetHostImage.showDocks(targetPort);
        }
    }

    // TODO: Replace with ImageGroup.filter().setVisibility()
    public void setPathVisibility(Port port, Visibility visibility) {
        for (PathImage pathImage : getPathImages(port)) {
            pathImage.setVisibility(visibility);

            // Deep
            Port targetPort = pathImage.getPath().getTarget();
            Host targetHost = (Host) targetPort.getParent();
            HostImage targetHostImage = (HostImage) getScene().getImage(targetHost);
            targetHostImage.setPathVisibility(targetPort, visibility);
        }
    }

    public void showPathImages() {
        List<Port> ports = getFeature().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setPathVisibility(port, Visibility.VISIBLE);
        }
    }

    public void hidePathImages() {
        List<Port> ports = getFeature().getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);

            setPathVisibility(port, Visibility.INVISIBLE);
            showDocks(port);
        }
    }

    public void setOrig_candidatePatchVisibility(Visibility visibility) {
        orig_candidatePatchVisibility = visibility;
    }

    public Visibility getOrig_candidatePatchVisibility() {
        return orig_candidatePatchVisibility;
    }

    private void drawCandidatePatchImage(Display display) {

        if (orig_candidatePatchVisibility == Visibility.VISIBLE) {

            Canvas canvas = display.getCanvas();
            Paint paint = display.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    getCoordinate(),
                    orig_candidatePatchCoordinate
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.CYAN); // paint.setColor(getUniqueColor());
            Display.drawRectangle(orig_candidatePatchCoordinate, pathRotationAngle + 180, 250, 250, display);

        }

    }

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawCandidatePathImages(Display display) {
        if (candidatePathVisibility == Visibility.VISIBLE) {

//            if (getPort().getType() != Port.Type.NONE) {

            Canvas canvas = display.getCanvas();
            Paint paint = display.getPaint();

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

            Display.drawTrianglePath(
                    pathStartCoordinate,
                    pathStopCoordinate,
                    triangleWidth,
                    triangleHeight,
                    display
            );

            // Color
            paint.setStyle(Paint.Style.FILL);
//                paint.setColor(getUniqueColor());
            Display.drawCircle(candidatePathDestinationCoordinate, shapeRadius, 0.0f, display);
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

