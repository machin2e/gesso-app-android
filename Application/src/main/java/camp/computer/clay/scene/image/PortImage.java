package camp.computer.clay.scene.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.EventListener;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Circle;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

public class PortImage extends Image<Port> {

    double shapeRadius = 40.0;

    public static double DISTANCE_FROM_BOARD = 45.0f;
    public static double DISTANCE_BETWEEN_NODES = 15.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    private int uniqueColor = Color.BLACK;

    public int getIndex() {
        if (getParentImage() instanceof HostImage) {
//            return getBaseFigure().getPortImageIndex(this);
        } else if (getParentImage() instanceof ExtensionImage) {
            return ((ExtensionImage) getParentImage()).getPortImageIndex(this);
        }
        return -1;
    }

    private int dataSampleCount = 40;
    private double[] portDataSamples = new double[dataSampleCount];

    private int previousSwitchState = 0;
    private double switchPeriod = 20.0f;
    private int switchHalfPeriodSampleCount = 0;
    private double pulsePeriod = 20.0f;
    private double pulseDutyCycle = 0.5f;
    private int pulsePeriodSampleCount = 0;
    private int previousPulseState = 0;
    private double xWaveStart = 0;

    private boolean isDragging = false;
    private Point previousCoordinate = new Point(getPosition());

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDragging(boolean isDragging) {

        if (this.isDragging == false && isDragging == true) {
            this.previousCoordinate.set(getPosition());
            this.isDragging = true;
        } else if (this.isDragging == true && isDragging == false) {
            this.setPosition(previousCoordinate);
            this.isDragging = false;
        }
    }

    private Visibility candidatePathVisibility = Visibility.INVISIBLE;
    private Point candidatePathDestinationCoordinate = new Point(40, 80);

    private Visibility candidatePatchVisibility = Visibility.INVISIBLE;

    public PortImage(Port port) {
        super(port);
        setup();
    }

    Point[] relativePortCoordinates;

    private void setup() {
        setupShapes();
        setupStyle();
        setupData();
        setupActions();
    }

    private void setupShapes() {

        // Create shapes for image
        double shapeRadius = 40.0f;
        Circle portCircle = new Circle(shapeRadius);
        portCircle.setLabel("Port");
        portCircle.setColor("#f7f7f7");
        portCircle.setOutlineThickness(0);
        addShape(portCircle);

    }

    private void setupStyle() {
        uniqueColor = updateUniqueColor();
        setVisibility(Visibility.INVISIBLE);
    }

    private void setupData() {
        Circle portCircle = (Circle) getShape("Port");
        if (portCircle != null) {
            for (int i = 0; i < this.portDataSamples.length; i++) {
                this.portDataSamples[i] = -(portCircle.getRadius() / 2.0f) + 0;
            }
        }
    }

    private void setupActions() {

        setOnActionListener(new EventListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.MOVE) {

                    if (action.isHolding()) {

                        // Holding and dragging

                        // Port
                        PortImage portFigure = (PortImage) event.getTargetImage();

                        portFigure.setDragging(true);
                        portFigure.setPosition(event.getPosition());

                    } else {

                        // Candidate Path Visibility
                        setCandidatePathDestinationCoordinate(event.getPosition());
                        setCandidatePathVisibility(Visibility.VISIBLE);

                        // Candidate Extension Visibility

                        boolean isCreatePatchAction = true;
                        List<Image> images = getScene().getImages(Host.class, Extension.class).getList();
                        for (int i = 0; i < images.size(); i++) {
                            Image nearbyImage = images.get(i);

                            // Update style of nearby machines
                            double distanceToBaseImage = Geometry.calculateDistance(
                                    event.getPosition(), //candidatePathDestinationCoordinate,
                                    nearbyImage.getPosition()
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

                        // Port type and flow direction
                        Port port = getPort();
                        if (port.getDirection() == Port.Direction.NONE) {
                            port.setDirection(Port.Direction.INPUT);
                        }
                        if (port.getType() == Port.Type.NONE) {
                            port.setType(Port.Type.next(port.getType()));
                        }

                        // Camera
                        Camera camera = event.getActor().getCamera();
                        camera.focusCreatePath(action);
                    }

                } else if (event.getType() == Event.Type.UNSELECT) {

                    Image targetImage = scene.getImageByPosition(event.getPosition());
                    event.setTargetImage(targetImage);

                    if (action.getDuration() < Event.MAXIMUM_TAP_DURATION) {

                        Port port = getPort();

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

                        } else if (!hasVisiblePaths() && !hasVisibleAncestorPaths()) {

                            Log.v("TouchPort", "C");

                            // TODO: Replace hasVisiblePaths() with check for focusedSprite/Path

                            // TODO: If second press, change the channel.

                            // Remove focus from other machines and their ports.
                            List<Image> baseImages = getScene().getImages(Host.class).getList();
                            for (int i = 0; i < baseImages.size(); i++) {
                                HostImage baseFigure = (HostImage) baseImages.get(i);
                                baseFigure.setTransparency(0.1);
                                baseFigure.hidePortShapes();
                                baseFigure.hidePathImages();
                            }

                            for (Image patchImage2 : getScene().getImages().filterType(Extension.class).getList()) {
                                ExtensionImage extensionImage = (ExtensionImage) patchImage2;
                                if (extensionImage.getExtension() != getParentImage().getFeature()) {
                                    extensionImage.setTransparency(0.1);
                                    extensionImage.hidePortImages();
                                    extensionImage.hidePathImages();
                                }
                            }

                            // Reduce focus on the machine
//                            getParentImage().setTransparency(0.1);

                            // Focus on the port
                            //portImage.getBaseFigure().showPathImage(portImage.getIndex(), true);
                            showPaths();
                            setVisibility(Visibility.VISIBLE);
                            setPathVisibility(Visibility.VISIBLE);

                            List<Path> paths = port.getCompletePath();
                            for (Path connectedPath : paths) {
                                // Show ports
                                getScene().getImage(connectedPath.getSource()).setVisibility(Visibility.VISIBLE);
                                ((PortImage) getScene().getImage(connectedPath.getSource())).showPaths();
                                getScene().getImage(connectedPath.getTarget()).setVisibility(Visibility.VISIBLE);
                                ((PortImage) getScene().getImage(connectedPath.getTarget())).showPaths();
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

                            getScene().getFeature().getActor(0).getCamera().setPosition(Geometry.calculateCenterCoordinate(pathPortCoordinates));

                        } else if (hasVisiblePaths() || hasVisibleAncestorPaths()) {

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

                        if (action.getFirstEvent().getTargetImage() instanceof PortImage) {

                            // First processAction was on a port figure...

                            if (event.getTargetImage() instanceof HostImage) {

//                                // ...getLastEvent processAction was on a base figure.
//
//                                PortImage sourcePortFigure = (PortImage) action.getFirstEvent().getTargetImage();
//                                sourcePortFigure.setCandidatePathVisibility(Visibility.INVISIBLE);

                            } else if (event.getTargetImage() instanceof PortImage) {

                                // Port
                                // ...getLastEvent processAction was on a port image.

                                // PortImage portImage = (PortImage) event.getImageByPosition();
                                PortImage sourcePortImage = (PortImage) event.getAction().getFirstEvent().getTargetImage();

                                if (sourcePortImage.isDragging()) {

                                    // Get nearest port image
                                    PortImage nearestPortImage = (PortImage) getScene().getImages(Port.class).getNearest(event.getPosition());
                                    Port nearestPort = nearestPortImage.getPort();
                                    Log.v("DND", "nearestPort: " + nearestPort);

                                    // TODO: When dragging, enable pushing ports?

                                    Port sourcePort = sourcePortImage.getPort();

                                    List<Path> paths = getScene().getFeature().getPaths();

                                    // Copy configuration
                                    nearestPort.setDirection(sourcePort.getDirection());
                                    nearestPort.setType(sourcePort.getType());
                                    nearestPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                    // Reset port configuration
                                    sourcePort.setDirection(Port.Direction.NONE);
                                    sourcePort.setType(Port.Type.NONE);
                                    sourcePortImage.updateUniqueColor();

                                    // Clear the port's list of paths
                                    sourcePort.getPaths().clear();

                                    // Copy paths
                                    for (Path path : paths) {

                                        // Update source
                                        if (path.getSource() == sourcePort) {
                                            path.setSource(nearestPort);
                                            Log.v("DND", "Updating source");
                                        }

                                        // Update target
                                        if (path.getTarget() == sourcePort) {
                                            path.setTarget(nearestPort);
                                            Log.v("DND", "Updating target");
                                        }

                                        nearestPort.addPath(path);

                                    }

                                    // Restore port image's position
                                    sourcePortImage.setDragging(false);

                                    // Camera
                                    event.getActor().getCamera().focusSelectPath(sourcePort);

                                } else {

                                    // Show ports of nearby forms
                                    boolean useNearbyPortImage = false;

                                    Log.v("Event", "B");

                                    // TODO: Use overlappedImage instanceof PortImage

                                    //for (PortImage nearbyPortImage : nearbyBaseImage.getPortShapes()) {
                                    List<Image> nearbyPortImages = getScene().getImages(Port.class).getList();
                                    for (int i = 0; i < nearbyPortImages.size(); i++) {
                                        PortImage nearbyPortImage = (PortImage) nearbyPortImages.get(i);

                                        if (nearbyPortImage != sourcePortImage) {
                                            if (nearbyPortImage.contains(event.getPosition())) {

                                                Log.v("Event", "C");

                                                Port port = sourcePortImage.getPort();
                                                Port nearbyPort = nearbyPortImage.getPort();

                                                useNearbyPortImage = true;

                                                if (port.getDirection() == Port.Direction.NONE) {
                                                    port.setDirection(Port.Direction.INPUT);
                                                }
                                                if (port.getType() == Port.Type.NONE) {
                                                    port.setType(Port.Type.next(port.getType())); // (machineSprite.channelTypes.getEvent(i) + 1) % machineSprite.channelTypeColors.length
                                                }

                                                nearbyPort.setDirection(Port.Direction.OUTPUT);
                                                nearbyPort.setType(Port.Type.next(nearbyPort.getType()));

                                                // Create and addEvent path to port
                                                Port sourcePort = (Port) getScene().getFeature(sourcePortImage);
                                                Port targetPort = (Port) getScene().getFeature(nearbyPortImage);

                                                if (!sourcePort.hasAncestor(targetPort)) {

                                                    Log.v("Event", "D.1");

                                                    Path path = new Path(sourcePort, targetPort);

                                                    if (sourcePort.getParent() instanceof Extension || targetPort.getParent() instanceof Extension) {
                                                        path.setType(Path.Type.ELECTRONIC);
                                                    } else {
                                                        path.setType(Path.Type.MESH);
                                                    }

                                                    sourcePort.addPath(path);

                                                    scene.addFeature(path);

                                                    PortImage targetPortImage = (PortImage) getScene().getImage(path.getTarget());
                                                    targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                                    // Camera
                                                    event.getActor().getCamera().focusSelectPath(sourcePort);
                                                }

                                                break;
                                            }
                                        }
                                    }

                                    if (!useNearbyPortImage) {

                                        Port port = sourcePortImage.getFeature();

                                        port.setDirection(Port.Direction.INPUT);

                                        if (port.getType() == Port.Type.NONE) {
                                            port.setType(Port.Type.next(port.getType()));
                                        }
                                    }

                                    sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);

                                    // ApplicationView.getLauncherView().speakPhrase("setting as input. you can send the data to another board if you want. pointerCoordinates another board.");

//                // Camera
//                ArrayList<Port> pathPorts = port.getPorts(paths);
//                ArrayList<Image> pathPortImages = getScene().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Scene.getCoordinates(pathPortImages);
//                Rectangle boundingBox = Geometry.getBoundingBox(pathPortPositions);
//                getScene().getFeature().getActor(0).getCamera().adjustScale(boundingBox);
//
//                getScene().getFeature().getActor(0).getCamera().setPosition(Geometry.calculateCenterCoordinate(pathPortPositions));

//                event.setTargetImage(action.getFirstEvent().getImageByPosition());
//                event.setType(Event.Type.UNSELECT);
//                Log.v("onHoldListener", "Source port: " + event.getImageByPosition());
//                targetImage.processAction(event);

                                }


                                setCandidatePathVisibility(Visibility.INVISIBLE);
                                setCandidatePatchVisibility(Visibility.INVISIBLE);

                            } else if (event.getTargetImage() instanceof ExtensionImage) {

                                // Extension
//                                event.getTargetImage().processAction(event);

                            } else if (event.getTargetImage() instanceof Scene) {

//                                event.getTargetImage().processAction(event);

                            }

                        }
                    }
                }
            }
        });
    }

    public HostImage getBaseFigure() {
        return (HostImage) getParentImage();
    }

    // TODO: Delete
    public Port getPort() {
        return getFeature();
    }

    public List<PathImage> getPathFigures() {
        List<PathImage> pathImages = new ArrayList<>();
        for (Path path : getPort().getPaths()) {
            PathImage pathImage = (PathImage) getScene().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    public int getUniqueColor() {
        return this.uniqueColor;
    }

    public void setUniqueColor(int uniqueColor) {
        this.uniqueColor = uniqueColor;
    }

    public int updateUniqueColor() {
        return camp.computer.clay.scene.util.Color.getUniqueColor(this);
    }

    public void showPaths() {
        for (PathImage pathImage : getPathFigures()) {
            pathImage.showDocks = false;

            // Deep
            PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
            targetPortImage.showPaths();
        }
    }

    public void showDocks() {
        for (PathImage pathImage : getPathFigures()) {
            pathImage.showDocks = true;

            // Deep
            PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
            targetPortImage.showDocks();
        }
    }

    public void draw(Display display) {
        if (isVisible()) {

            // Port
            Display.drawCircle((Circle) shapes.get(0), display);

            drawStyle(display);
//            drawData(surface);
//            drawAnnotation(surface);

            // Candidate Path
            drawCandidatePathImages(display);

            // Candidate Extension
            drawCandidatePatchImage(display);
        }
    }

    /**
     * Draws the sprite's detail front layer.
     *
     * @param display
     */
    public void drawStyle(Display display) {

        Canvas canvas = display.getCanvas();
        Paint paint = display.getPaint();

        if (getPort().getType() != Port.Type.NONE) {

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.uniqueColor);
            Display.drawCircle(getPosition(), shapeRadius, getRotation(), display);

            // Outline
            boolean showShapeOutline = false;
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);
                Display.drawCircle(getPosition(), shapeRadius, getRotation(), display);
            }
        }
    }

    /**
     * Draws the sprite's data layer.
     *
     * @param display
     */
    private void drawData(Display display) {

        Canvas canvas = display.getCanvas();
        Paint paint = display.getPaint();

        Port port = getPort();

        if (port.getType() != Port.Type.NONE) {

            // Outline
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);
            paint.setColor(Color.WHITE);

            double plotStep = ((2.0f * (float) shapeRadius) / (float) portDataSamples.length);

            Point[] rotatedPortDataSamplePoints = new Point[portDataSamples.length];

            double[] portGroupRotation = new double[]{
                    0,
                    0,
                    0,
                    -90,
                    -90,
                    -90,
                    -180,
                    -180,
                    -180,
                    -270,
                    -270,
                    -270
            };

            for (int i = 0; i < portDataSamples.length; i++) {

                Point samplePoint = null;

                if (port.getDirection() == Port.Direction.INPUT) {

                    // Set position before rotation adjustment
                    samplePoint = new Point(
                            this.getPosition().getX() + portDataSamples[i],
                            this.getPosition().getY() + -shapeRadius + i * plotStep
                    );

                } else if (port.getDirection() == Port.Direction.OUTPUT) {

                    // Set position before rotation adjustment
                    samplePoint = new Point(
                            this.getPosition().getX() + portDataSamples[i],
                            this.getPosition().getY() + shapeRadius - i * plotStep
                    );

                } else if (port.getDirection() == Port.Direction.BOTH) {

                    // TODO: Visualize bidirectional data.

                }

                // Rotate point
                rotatedPortDataSamplePoints[i] = Geometry.calculateRotatedPoint(
                        getPosition(),
                        getParentImage().getRotation() + portGroupRotation[getIndex()],
                        samplePoint
                );
            }

            if (port.getDirection() == Port.Direction.INPUT) {

                for (int i = 0; i < portDataSamples.length - 1; i++) {
                    Display.drawLine(rotatedPortDataSamplePoints[i], rotatedPortDataSamplePoints[i + 1], display);
                }

            } else if (port.getDirection() == Port.Direction.OUTPUT) {

                for (int i = 0; i < portDataSamples.length - 1; i++) {
                    Display.drawLine(rotatedPortDataSamplePoints[i], rotatedPortDataSamplePoints[i + 1], display);
                }

            }
        }
    }

    /**
     * Draws the sprite's annotation layer. Contains labels and other text.
     *
     * @param display
     */
    public void drawAnnotation(Display display) {

        if (getPort().getType() != Port.Type.NONE) {

            Canvas canvas = display.getCanvas();
            Paint paint = display.getPaint();

            // Geometry
            Point labelCoordinate = new Point();
            labelCoordinate.set(
                    getPosition().getX() + shapeRadius + 25,
                    getPosition().getY()
            );

            // Style
            paint.setColor(this.uniqueColor);
            double typeLabelTextSize = 27;

            // Draw
            Display.drawText(labelCoordinate, getPort().getType().getLabel(), typeLabelTextSize, display);

        }
    }

    public void update() {
        if (isVisible()) {
            updateCoordinate();
            updateData();
        }
    }

    private void updateData() {
        Random random = new Random();
        Port port = getPort();
        if (port.getType() == Port.Type.SWITCH) {
            // Shift data to make room for new samples
            for (int k = 0; k < portDataSamples.length - 1; k++) {
                portDataSamples[k] = portDataSamples[k + 1];
            }
            // Add new samples for the channel type
            double sample = getSyntheticSwitchSample();
            portDataSamples[portDataSamples.length - 1] = sample;
            switchHalfPeriodSampleCount = (switchHalfPeriodSampleCount + 1) % ((int) switchPeriod / 2);
            if (switchHalfPeriodSampleCount == 0) {
                previousSwitchState = (previousSwitchState + 1) % 2;
            }
        } else if (port.getType() == Port.Type.PULSE) {
            // Shift data to make room for new samples
            for (int k = 0; k < portDataSamples.length - 1; k++) {
                portDataSamples[k] = portDataSamples[k + 1];
            }
            // Add new samples for the channel type
            double sample = getSyntheticPulseSample();
            portDataSamples[portDataSamples.length - 1] = sample;
            //pulsePeriodSampleCount = (pulsePeriodSampleCount + 1) % ((int) (pulseDutyCycle * pulsePeriod) / 2);
            pulsePeriodSampleCount = (pulsePeriodSampleCount + 1) % (1 + (int) (pulseDutyCycle * pulsePeriod));
            if (pulsePeriodSampleCount == 0) {
                pulseDutyCycle = random.nextFloat();
                previousPulseState = (previousPulseState + 1) % 2;
            }
        } else if (port.getType() == Port.Type.WAVE) {
            // Add new sample for the channel type
            for (int k = 0; k < portDataSamples.length; k++) {
                portDataSamples[k] = getSyntheticWaveSample(k);
            }
            //xWaveStart = (xWaveStart + ((2.0f * (float) Math.PI) / ((float) this.portDataSamples[j].length))) % (2.0f * (float) Math.PI);
            xWaveStart = (xWaveStart + 0.5f) % ((float) Math.PI * 2.0f);
        } else if (port.getType() == Port.Type.POWER_TTL) {
            // Shift data to make room for new samples
            for (int k = 0; k < portDataSamples.length - 1; k++) {
                portDataSamples[k] = portDataSamples[k + 1];
            }
            // Add new samples for the type
            double sample = getSyntheticSample(1.0);
            portDataSamples[portDataSamples.length - 1] = sample;
        } else if (port.getType() == Port.Type.POWER_CMOS) {
            // Shift data to make room for new samples
            for (int k = 0; k < portDataSamples.length - 1; k++) {
                portDataSamples[k] = portDataSamples[k + 1];
            }
            // Add new samples for the type
            double sample = getSyntheticSample(0.0);
            portDataSamples[portDataSamples.length - 1] = sample;
        } else if (port.getType() == Port.Type.POWER_REFERENCE) {
            // Shift data to make room for new samples
            for (int k = 0; k < portDataSamples.length - 1; k++) {
                portDataSamples[k] = portDataSamples[k + 1];
            }
            // Add new samples for the type
            double sample = getSyntheticSample(0.5);
            portDataSamples[portDataSamples.length - 1] = sample;
        }
    }

    private void updateCoordinate() {

        if (relativePortCoordinates == null) {

            if (getParentImage() instanceof HostImage) {

                // Ports
                HostImage baseFigure = (HostImage) getParentImage();
                Host host = baseFigure.getHost();
                getPosition().setOrigin(baseFigure.getPosition());

                Rectangle boardRectangle = (Rectangle) baseFigure.getShape(0);

                double portRadius = 40.0f;
                relativePortCoordinates = new Point[host.getPorts().size()];
                relativePortCoordinates[0] = new Point(
                        -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortCoordinates[1] = new Point(
                        0,
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortCoordinates[2] = new Point(
                        +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortCoordinates[3] = new Point(
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                        +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
                );
                relativePortCoordinates[4] = new Point(
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                        0
                );
                relativePortCoordinates[5] = new Point(
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                        -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
                );
                relativePortCoordinates[6] = new Point(
                        +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortCoordinates[7] = new Point(
                        0,
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortCoordinates[8] = new Point(
                        -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortCoordinates[9] = new Point(
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                        -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
                );
                relativePortCoordinates[10] = new Point(
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                        0
                );
                relativePortCoordinates[11] = new Point(
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                        +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
                );

                relativePortCoordinates[getIndex()] = Geometry.calculateRotatedPoint(
                        new Point(0, 0),
                        getParentImage().getRotation(),
                        relativePortCoordinates[getIndex()]
                );

                getPosition().setRelative(relativePortCoordinates[getIndex()]);

            }
        }

        if (getParentImage() instanceof ExtensionImage) {

            ExtensionImage patchFigure = (ExtensionImage) getParentImage();
            Extension extension = patchFigure.getExtension();

            getPosition().setOrigin(patchFigure.getPosition());

            // Ports
            double portRadius = 40.0f;
            relativePortCoordinates = new Point[extension.getPorts().size()];

            // Calculate coordinates of ports
            double leftCoordinate = 0 - ((portRadius * 2) * extension.getPorts().size() + PortImage.DISTANCE_BETWEEN_NODES * (extension.getPorts().size() - 1)) / 2.0;
            double stepSize = (portRadius * 2) + (PortImage.DISTANCE_BETWEEN_NODES);
            double startOffset = portRadius;

            // Update positions of ports
            for (int i = 0; i < extension.getPorts().size(); i++) {
                relativePortCoordinates[i] = new Point(
                        leftCoordinate + i * stepSize + startOffset,
                        +1 * ((patchFigure.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
                );
            }

            relativePortCoordinates[getIndex()] = Geometry.calculateRotatedPoint(
                    new Point(0, 0),
                    getParentImage().getRotation(),
                    relativePortCoordinates[getIndex()]
            );

            getPosition().setRelative(relativePortCoordinates[getIndex()]);
        }
    }

    private double getSyntheticSample(double value) {
        return -(shapeRadius / 2.0f) + value * shapeRadius;
    }

    private double getSyntheticSwitchSample() {
        return -(shapeRadius / 2.0f) + previousSwitchState * shapeRadius;
    }

    private double getSyntheticPulseSample() {
        return -(shapeRadius / 2.0f) + previousPulseState * shapeRadius;
    }

    private double getSyntheticWaveSample(int x) {
        return ((float) Math.sin(xWaveStart + x * 0.2)) * shapeRadius * 0.5f;
    }

    public void setPathVisibility(Visibility visibility) {
        for (PathImage pathImage : getPathFigures()) {
            pathImage.setVisibility(visibility);

            // Deep
            PortImage targetPortImage = (PortImage) getScene().getImage(pathImage.getPath().getTarget());
            targetPortImage.setVisibility(visibility);
        }
    }

    public boolean hasVisiblePaths() {
        for (PathImage pathImage : getPathFigures()) {
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths() {
        List<Path> ancestorPaths = getPort().getAncestorPaths();
        for (Path ancestorPath : ancestorPaths) {
            PathImage pathImage = (PathImage) getScene().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public List<PathImage> getVisiblePaths() {
        List<PathImage> visiblePathImages = new ArrayList<>();
        for (PathImage pathImage : getPathFigures()) {
            if (pathImage.isVisible()) {
                visiblePathImages.add(pathImage);
            }
        }
        return visiblePathImages;
    }

//    @Override
//    public boolean contains(Point point) {
//        if (isVisible()) {
//            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES));
//        } else {
//            return false;
//        }
//    }
//
//    public boolean contains(Point point, double padding) {
//        if (isVisible()) {
//            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + padding));
//        } else {
//            return false;
//        }
//    }

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

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawCandidatePathImages(Display display) {
        if (candidatePathVisibility == Visibility.VISIBLE) {

            if (getPort().getType() != Port.Type.NONE) {

                Canvas canvas = display.getCanvas();
                Paint paint = display.getPaint();

                double triangleWidth = 20;
                double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
                double triangleSpacing = 35;

                // Color
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(15.0f);
                paint.setColor(this.getUniqueColor());

                double pathRotationAngle = Geometry.calculateRotationAngle(
                        getPosition(),
                        candidatePathDestinationCoordinate
                );

                Point pathStartCoordinate = Geometry.calculatePoint(
                        getPosition(),
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
                paint.setColor(getUniqueColor());
                Display.drawCircle(candidatePathDestinationCoordinate, shapeRadius, 0.0f, display);
            }
        }
    }


    private void drawCandidatePatchImage(Display display) {

        if (candidatePatchVisibility == Visibility.VISIBLE) {

            Canvas canvas = display.getCanvas();
            Paint paint = display.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    getPosition(),
                    candidatePathDestinationCoordinate
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getUniqueColor());
            Display.drawRectangle(candidatePathDestinationCoordinate, pathRotationAngle + 180, 250, 250, display);

        }

    }
}
