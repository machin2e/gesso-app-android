package camp.computer.clay.scene.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Base;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.model.interaction.Process;
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
        if (getParentImage() instanceof BaseImage) {
            return getBaseFigure().getPortImageIndex(this);
        } else if (getParentImage() instanceof PatchImage) {
            return ((PatchImage) getParentImage()).getPortFigureIndex(this);
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
    private Point previousCoordinate = new Point(getCoordinate());

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDragging(boolean isDragging) {

        if (this.isDragging == false && isDragging == true) {
            this.previousCoordinate.set(getCoordinate());
            this.isDragging = true;
        } else if (this.isDragging == true && isDragging == false) {
            this.setCoordinate(previousCoordinate);
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

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.SELECT) {

                } else if (action.getType() == Action.Type.MOVE) {

                    Process process = action.getActionSequence();

                    if (process.isHolding()) {

                        // Holding and dragging

                        // Port
                        PortImage portFigure = (PortImage) action.getTarget();

                        portFigure.setDragging(true);
                        portFigure.setCoordinate(action.getCoordinate());

                    } else {

                        // Candidate Path Visibility
                        setCandidatePathDestinationCoordinate(action.getCoordinate());
                        setCandidatePathVisibility(Visibility.VISIBLE);

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

                        // Port type and flow direction
                        Port port = getPort();
                        if (port.getDirection() == Port.Direction.NONE) {
                            port.setDirection(Port.Direction.INPUT);
                        }
                        if (port.getType() == Port.Type.NONE) {
                            port.setType(Port.Type.next(port.getType()));
                        }

                        // Camera
                        Camera camera = action.getActor().getCamera();
                        camera.focusCreatePath(action);
                    }

                } else if (action.getType() == Action.Type.UNSELECT) {

                    Process process = action.getActionSequence();

                    Image targetImage = scene.getImageByCoordinate(action.getCoordinate());
                    action.setTarget(targetImage);

                    Camera camera = action.getActor().getCamera();

                    if (process.getDuration() < Action.MAXIMUM_TAP_DURATION) {

                        Port port = getPort();

                        if (port.getType() == Port.Type.NONE) {

                            Log.v("TouchPort", "A");

                            port.setDirection(Port.Direction.INPUT);
                            port.setType(Port.Type.next(port.getType()));

                            // TODO: Speak ~ "setting as input. you can send the data to another board if you want. points another board."

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
                            List<Image> baseImages = getScene().getImages(Base.class).getList();
                            for (int i = 0; i < baseImages.size(); i++) {
                                BaseImage baseFigure = (BaseImage) baseImages.get(i);
                                baseFigure.setTransparency(0.1);
                                baseFigure.hidePortImages();
                                baseFigure.hidePathImages();
                            }

                            for (Image patchImage2 : getScene().getImages().filterType(Patch.class).getList()) {
                                PatchImage patchImage = (PatchImage) patchImage2;
                                if (patchImage.getPatch() != getParentImage().getConstruct()) {
                                    patchImage.setTransparency(0.1);
                                    patchImage.hidePortFigures();
                                    patchImage.hidePathFigures();
                                }
                            }

                            // Reduce focus on the machine
//                            getParentImage().setTransparency(0.1);

                            // Focus on the port
                            //portImage.getBaseFigure().showPathImage(portImage.getIndex(), true);
                            showPaths();
                            setVisibility(Visibility.VISIBLE);
                            setPathVisibility(Visibility.VISIBLE);

                            List<Path> paths = port.getGraph();
                            for (Path connectedPath : paths) {
                                // Show ports
                                getScene().getImage(connectedPath.getSource()).setVisibility(Visibility.VISIBLE);
                                ((PortImage) getScene().getImage(connectedPath.getSource())).showPaths();
                                getScene().getImage(connectedPath.getTarget()).setVisibility(Visibility.VISIBLE);
                                ((PortImage) getScene().getImage(connectedPath.getTarget())).showPaths();
                                // Show path
                                getScene().getImage(connectedPath).setVisibility(Visibility.VISIBLE);
                            }

                            // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. points another board.");

                            // Camera
                            List<Port> pathPorts = port.getPorts(paths);
                            List<Image> pathPortImages = getScene().getImages(pathPorts);
                            List<Point> pathPortCoordinates = Scene.getCoordinates(pathPortImages);
                            Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortCoordinates);
                            getScene().getModel().getActor(0).getCamera().adjustScale(boundingBox);

                            getScene().getModel().getActor(0).getCamera().setCoordinate(Geometry.calculateCenterCoordinate(pathPortCoordinates));

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

                        if (process.getFirstAction().getTarget() instanceof PortImage) {

                            // First processAction was on a port figure...

                            if (action.getTarget() instanceof BaseImage) {

//                                // ...getLastAction processAction was on a base figure.
//
//                                PortImage sourcePortFigure = (PortImage) process.getFirstAction().getTarget();
//                                sourcePortFigure.setCandidatePathVisibility(Visibility.INVISIBLE);

                            } else if (action.getTarget() instanceof PortImage) {

                                // Port
                                // ...getLastAction processAction was on a port image.

                                // PortImage portImage = (PortImage) action.getImageByCoordinate();
                                PortImage sourcePortImage = (PortImage) action.getActionSequence().getFirstAction().getTarget();

                                if (sourcePortImage.isDragging()) {

                                    // Get nearest port image
                                    PortImage nearestPortImage = (PortImage) getScene().getImages(Port.class).getNearest(action.getCoordinate());
                                    Port nearestPort = nearestPortImage.getPort();
                                    Log.v("DND", "nearestPort: " + nearestPort);

                                    // TODO: When dragging, enable pushing ports?

                                    // Remove the paths from the port and move them to the selected port
//                        Port nearestPort = nearestPortImage.getPort();
//                        while (sourcePortImage.getPort().getPaths().size() > 0) {
//                            Path path = sourcePortImage.getPort().getPaths().remove(0);
//                            path.setSource(nearestPort);
//                            nearestPort.addPath(path);
//                        }

                                    Port sourcePort = sourcePortImage.getPort();

                                    List<Path> paths = getScene().getModel().getPaths();

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
                                    action.getActor().getCamera().focusSelectPath(sourcePort);

                                } else {

                                    // Show ports of nearby forms
                                    boolean useNearbyPortImage = false;
//                            for (BaseImage nearbyBaseImage : getScene().getBaseFigures()) {
//
//                                Log.v("Action", "A");
//
//                                // Update style of nearby machines
//                                double distanceToFrameImage = Geometry.calculateDistance(
//                                        action.getCoordinate(),
//                                        nearbyBaseImage.getCoordinate()
//                                );
//
//                                if (distanceToFrameImage < nearbyBaseImage.getBoundingBox().getHeight() + 50) {

                                    Log.v("Action", "B");

                                    // TODO: Use overlappedImage instanceof PortImage

                                    //for (PortImage nearbyPortImage : nearbyBaseImage.getPortImages()) {
                                    List<Image> nearbyPortImages = getScene().getImages(Port.class).getList();
                                    for (int i = 0; i < nearbyPortImages.size(); i++) {
                                        PortImage nearbyPortImage = (PortImage) nearbyPortImages.get(i);

                                        if (nearbyPortImage != sourcePortImage) {
                                            if (nearbyPortImage.contains(action.getCoordinate(), 50)) {

                                                Log.v("Action", "C");

                                                Port port = sourcePortImage.getPort();
                                                Port nearbyPort = nearbyPortImage.getPort();

                                                useNearbyPortImage = true;

                                                if (port.getDirection() == Port.Direction.NONE) {
                                                    port.setDirection(Port.Direction.INPUT);
                                                }
                                                if (port.getType() == Port.Type.NONE) {
                                                    port.setType(Port.Type.next(port.getType())); // (machineSprite.channelTypes.getAction(i) + 1) % machineSprite.channelTypeColors.length
                                                }

                                                nearbyPort.setDirection(Port.Direction.OUTPUT);
                                                nearbyPort.setType(Port.Type.next(nearbyPort.getType()));

                                                // Create and add path to port
                                                Port sourcePort = (Port) getScene().getModel(sourcePortImage);
                                                Port targetPort = (Port) getScene().getModel(nearbyPortImage);

                                                if (!sourcePort.hasAncestor(targetPort)) {

                                                    Log.v("Action", "D.1");

                                                    Path path = new Path(sourcePort, targetPort);

                                                    if (sourcePort.getParent() instanceof Patch || targetPort.getParent() instanceof Patch) {
                                                        path.setType(Path.Type.ELECTRONIC);
                                                    } else {
                                                        path.setType(Path.Type.MESH);
                                                    }

                                                    sourcePort.addPath(path);

                                                    scene.addConstruct(path);
//                                                    PathImage pathImage = new PathImage(path);
////                                        pathImage.setScene(getScene());
//                                                    getScene().addImage(pathImage, "paths");

                                                    PortImage targetPortImage = (PortImage) getScene().getImage(path.getTarget());
                                                    targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                                    // Camera
                                                    action.getActor().getCamera().focusSelectPath(sourcePort);
                                                }

                                                break;
                                            }
                                        }
                                    }
//                                }
//                            }

//                portImage.processAction(action);

                                    if (!useNearbyPortImage) {

                                        Port port = (Port) sourcePortImage.getConstruct();

                                        port.setDirection(Port.Direction.INPUT);

                                        if (port.getType() == Port.Type.NONE) {
                                            port.setType(Port.Type.next(port.getType()));
                                        }
                                    }

                                    sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);

                                    // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. points another board.");

//                // Camera
//                ArrayList<Port> pathPorts = port.getPorts(paths);
//                ArrayList<Image> pathPortImages = getScene().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Scene.getCoordinates(pathPortImages);
//                Rectangle boundingBox = Geometry.getBoundingBox(pathPortPositions);
//                getScene().getUniverse().getActor(0).getCamera().adjustScale(boundingBox);
//
//                getScene().getUniverse().getActor(0).getCamera().setCoordinate(Geometry.calculateCenterCoordinate(pathPortPositions));

//                action.setTarget(process.getFirstAction().getImageByCoordinate());
//                action.setType(Action.Type.UNSELECT);
//                Log.v("onHoldListener", "Source port: " + action.getImageByCoordinate());
//                targetImage.processAction(action);

                                }


                                setCandidatePathVisibility(Visibility.INVISIBLE);
                                setCandidatePatchVisibility(Visibility.INVISIBLE);

                            } else if (action.getTarget() instanceof PatchImage) {

                                // Patch
//                                action.getTarget().processAction(action);

                            } else if (action.getTarget() instanceof Scene) {

//                                action.getTarget().processAction(action);

                            }

                        }
                    }
                }
            }
        });
    }

    public BaseImage getBaseFigure() {
        return (BaseImage) getParentImage();
    }

    // TODO: Delete
    public Port getPort() {
        return getConstruct();
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

    public void draw(Surface surface) {
        if (isVisible()) {

            // Port
            Surface.drawCircle((Circle) shapes.get(0), surface);

            drawStyle(surface);
//            drawData(surface);
//            drawAnnotation(surface);

            // Candidate Path
            drawCandidatePathImages(surface);

            // Candidate Patch
            drawCandidatePatchImage(surface);
        }
    }

    /**
     * Draws the sprite's detail front layer.
     *
     * @param surface
     */
    public void drawStyle(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        if (getPort().getType() != Port.Type.NONE) {

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.uniqueColor);
            Surface.drawCircle(getCoordinate(), shapeRadius, getRotation(), surface);

            // Outline
            boolean showShapeOutline = false;
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);
                Surface.drawCircle(getCoordinate(), shapeRadius, getRotation(), surface);
            }
        }
    }

    /**
     * Draws the sprite's data layer.
     *
     * @param surface
     */
    private void drawData(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

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
                            this.getCoordinate().getX() + portDataSamples[i],
                            this.getCoordinate().getY() + -shapeRadius + i * plotStep
                    );

                } else if (port.getDirection() == Port.Direction.OUTPUT) {

                    // Set position before rotation adjustment
                    samplePoint = new Point(
                            this.getCoordinate().getX() + portDataSamples[i],
                            this.getCoordinate().getY() + shapeRadius - i * plotStep
                    );

                } else if (port.getDirection() == Port.Direction.BOTH) {

                    // TODO: Visualize bidirectional data.

                }

                // Rotate point
                rotatedPortDataSamplePoints[i] = Geometry.calculateRotatedPoint(
                        getCoordinate(),
                        getParentImage().getRotation() + portGroupRotation[getIndex()],
                        samplePoint
                );
            }

            if (port.getDirection() == Port.Direction.INPUT) {

                for (int i = 0; i < portDataSamples.length - 1; i++) {
                    Surface.drawLine(rotatedPortDataSamplePoints[i], rotatedPortDataSamplePoints[i + 1], surface);
                }

            } else if (port.getDirection() == Port.Direction.OUTPUT) {

                for (int i = 0; i < portDataSamples.length - 1; i++) {
                    Surface.drawLine(rotatedPortDataSamplePoints[i], rotatedPortDataSamplePoints[i + 1], surface);
                }

            }
        }
    }

    /**
     * Draws the sprite's annotation layer. Contains labels and other text.
     *
     * @param surface
     */
    public void drawAnnotation(Surface surface) {

        if (getPort().getType() != Port.Type.NONE) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            // Geometry
            Point labelCoordinate = new Point();
            labelCoordinate.set(
                    getCoordinate().getX() + shapeRadius + 25,
                    getCoordinate().getY()
            );

            // Style
            paint.setColor(this.uniqueColor);
            double typeLabelTextSize = 27;

            // Draw
            Surface.drawText(labelCoordinate, getPort().getType().getTag(), typeLabelTextSize, surface);

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

            if (getParentImage() instanceof BaseImage) {

                // Ports
                BaseImage baseFigure = (BaseImage) getParentImage();
                Base base = baseFigure.getBase();
                getCoordinate().setReferencePoint(baseFigure.getCoordinate());

                Rectangle boardRectangle = (Rectangle) baseFigure.getShape(0);

                double portRadius = 40.0f;
                relativePortCoordinates = new Point[base.getPorts().size()];
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

                getCoordinate().setRelative(relativePortCoordinates[getIndex()]);

            }
        }

        if (getParentImage() instanceof PatchImage) {

            PatchImage patchFigure = (PatchImage) getParentImage();
            Patch patch = patchFigure.getPatch();

            getCoordinate().setReferencePoint(patchFigure.getCoordinate());

            // Ports
            double portRadius = 40.0f;
            relativePortCoordinates = new Point[patch.getPorts().size()];

            // Calculate coordinates of ports
            double leftCoordinate = 0 - ((portRadius * 2) * patch.getPorts().size() + PortImage.DISTANCE_BETWEEN_NODES * (patch.getPorts().size() - 1)) / 2.0;
            double stepSize = (portRadius * 2) + (PortImage.DISTANCE_BETWEEN_NODES);
            double startOffset = portRadius;

            // Update positions of ports
            for (int i = 0; i < patch.getPorts().size(); i++) {
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

            getCoordinate().setRelative(relativePortCoordinates[getIndex()]);
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

    @Override
    public boolean contains(Point point) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getCoordinate()) < (this.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES));
        } else {
            return false;
        }
    }

    public boolean contains(Point point, double padding) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getCoordinate()) < (this.shapeRadius + padding));
        } else {
            return false;
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

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawCandidatePathImages(Surface surface) {
        if (candidatePathVisibility == Visibility.VISIBLE) {

            if (getPort().getType() != Port.Type.NONE) {

                Canvas canvas = surface.getCanvas();
                Paint paint = surface.getPaint();

                double triangleWidth = 20;
                double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
                double triangleSpacing = 35;

                // Color
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(15.0f);
                paint.setColor(this.getUniqueColor());

                double pathRotationAngle = Geometry.calculateRotationAngle(
                        getCoordinate(),
                        candidatePathDestinationCoordinate
                );

                Point pathStartCoordinate = Geometry.calculatePoint(
                        getCoordinate(),
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
                paint.setColor(getUniqueColor());
                Surface.drawCircle(candidatePathDestinationCoordinate, shapeRadius, 0.0f, surface);
            }
        }
    }


    private void drawCandidatePatchImage(Surface surface) {

        if (candidatePatchVisibility == Visibility.VISIBLE) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    getCoordinate(),
                    candidatePathDestinationCoordinate
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getUniqueColor());
            Surface.drawRectangle(candidatePathDestinationCoordinate, pathRotationAngle + 180, 250, 250, surface);

        }

    }
}
