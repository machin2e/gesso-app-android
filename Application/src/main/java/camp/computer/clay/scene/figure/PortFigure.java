package camp.computer.clay.scene.figure;

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
import camp.computer.clay.model.interaction.ActionSequence;
import camp.computer.clay.scene.architecture.Figure;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Circle;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

public class PortFigure extends Figure<Port> {

    double shapeRadius = 40.0;

    public static double DISTANCE_FROM_BOARD = 45.0f;
    public static double DISTANCE_BETWEEN_NODES = 15.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    private int uniqueColor = Color.BLACK;

    public int getIndex() {
        if (getParentFigure() instanceof BaseFigure) {
            return getBaseFigure().getPortFigureIndex(this);
        } else if (getParentFigure() instanceof PatchFigure) {
            return ((PatchFigure) getParentFigure()).getPortFigureIndex(this);
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
    private Point previousPosition = new Point(getPosition());

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDragging(boolean isDragging) {

        if (this.isDragging == false && isDragging == true) {
            this.previousPosition.set(getPosition());
            this.isDragging = true;
        } else if (this.isDragging == true && isDragging == false) {
            this.setPosition(previousPosition);
            this.isDragging = false;
        }
    }

    private Visibility candidatePathVisibility = Visibility.INVISIBLE;
    private Point candidatePathDestinationPosition = new Point(40, 80);

    private Visibility candidatePatchVisibility = Visibility.INVISIBLE;

    public PortFigure(Port port) {
        super(port);
        setup();
    }

    Point[] relativePortPositions;

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

                } else if (action.getType() == Action.Type.TOUCH) {

                } else if (action.getType() == Action.Type.MOVE) {

                    // Candidate Path Visibility
                    setCandidatePathDestinationPosition(action.getPosition());
                    setCandidatePathVisibility(Visibility.VISIBLE);

                    // Candidate Patch Visibility

                    boolean isCreatePatchAction = true;
                    List<Figure> figures = getScene().getFigures(Base.class, Patch.class).getList();
                    for (int i = 0; i < figures.size(); i++) {
                        Figure nearbyFigure = figures.get(i);

                        // Update style of nearby machines
                        double distanceToBaseImage = Geometry.calculateDistance(
                                action.getPosition(), //candidatePathDestinationPosition,
                                nearbyFigure.getPosition()
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

                } else if (action.getType() == Action.Type.RELEASE) {

                    ActionSequence actionSequence = action.getActionSequence();

                    Figure targetFigure = scene.getFigureByPosition(action.getPosition());
                    action.setTarget(targetFigure);

                    Camera camera = action.getActor().getCamera();

                    if (actionSequence.getDuration() < Action.MAXIMUM_TAP_DURATION) {

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
                            List<Figure> baseFigures = getScene().getFigures(Base.class).getList();
                            for (int i = 0; i < baseFigures.size(); i++) {
                                BaseFigure baseFigure = (BaseFigure) baseFigures.get(i);
                                baseFigure.setTransparency(0.1);
                                baseFigure.hidePortFigures();
                                baseFigure.hidePathFigures();
                            }

                            for (Figure patchFigure2 : getScene().getFigures().filterType(Patch.class).getList()) {
                                PatchFigure patchImage = (PatchFigure) patchFigure2;
                                if (patchImage.getPatch() != getParentFigure().getConstruct()) {
                                    patchImage.setTransparency(0.1);
                                    patchImage.hidePortFigures();
                                    patchImage.hidePathFigures();
                                }
                            }

                            // Reduce focus on the machine
//                            getParentFigure().setTransparency(0.1);

                            // Focus on the port
                            //portImage.getBaseFigure().showPathImage(portImage.getIndex(), true);
                            showPaths();
                            setVisibility(Visibility.VISIBLE);
                            setPathVisibility(Visibility.VISIBLE);

                            List<Path> paths = port.getGraph();
                            for (Path connectedPath : paths) {
                                // Show ports
                                getScene().getFigure(connectedPath.getSource()).setVisibility(Visibility.VISIBLE);
                                ((PortFigure) getScene().getFigure(connectedPath.getSource())).showPaths();
                                getScene().getFigure(connectedPath.getTarget()).setVisibility(Visibility.VISIBLE);
                                ((PortFigure) getScene().getFigure(connectedPath.getTarget())).showPaths();
                                // Show path
                                getScene().getFigure(connectedPath).setVisibility(Visibility.VISIBLE);
                            }

                            // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. points another board.");

                            // Camera
                            List<Port> pathPorts = port.getPorts(paths);
                            List<Figure> pathPortFigures = getScene().getFigures(pathPorts);
                            List<Point> pathPortPositions = Scene.getPositions(pathPortFigures);
                            Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
                            getScene().getModel().getActor(0).getCamera().adjustScale(boundingBox);

                            getScene().getModel().getActor(0).getCamera().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

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

                        // ...last processAction was on a port image.

                        // PortFigure portImage = (PortFigure) action.getFigureByPosition();
                        PortFigure sourcePortImage = (PortFigure) action.getActionSequence().getFirst().getTarget();

                        if (sourcePortImage.isDragging()) {

                            // Get nearest port image
                            PortFigure nearestPortImage = (PortFigure) getScene().getFigures(Port.class).getNearest(action.getPosition());
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
//                            for (BaseFigure nearbyBaseImage : getScene().getBaseFigures()) {
//
//                                Log.v("Action", "A");
//
//                                // Update style of nearby machines
//                                double distanceToFrameImage = Geometry.calculateDistance(
//                                        action.getPosition(),
//                                        nearbyBaseImage.getPosition()
//                                );
//
//                                if (distanceToFrameImage < nearbyBaseImage.getBoundingBox().getHeight() + 50) {

                            Log.v("Action", "B");

                            // TODO: Use overlappedImage instanceof PortFigure

                            //for (PortFigure nearbyPortImage : nearbyBaseImage.getPortFigures()) {
                            List<Figure> nearbyPortFigures = getScene().getFigures(Port.class).getList();
                            for (int i = 0; i < nearbyPortFigures.size(); i++) {
                                PortFigure nearbyPortImage = (PortFigure) nearbyPortFigures.get(i);

                                if (nearbyPortImage != sourcePortImage) {
                                    if (nearbyPortImage.contains(action.getPosition(), 50)) {

                                        Log.v("Action", "C");

                                        Port port = sourcePortImage.getPort();
                                        Port nearbyPort = nearbyPortImage.getPort();

                                        useNearbyPortImage = true;

                                        if (port.getDirection() == Port.Direction.NONE) {
                                            port.setDirection(Port.Direction.INPUT);
                                        }
                                        if (port.getType() == Port.Type.NONE) {
                                            port.setType(Port.Type.next(port.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
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
//                                                    PathFigure pathImage = new PathFigure(path);
////                                        pathImage.setScene(getScene());
//                                                    getScene().addImage(pathImage, "paths");

                                            PortFigure targetPortImage = (PortFigure) getScene().getFigure(path.getTarget());
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
//                ArrayList<Figure> pathPortImages = getScene().getFigures(pathPorts);
//                ArrayList<Point> pathPortPositions = Scene.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.getBoundingBox(pathPortPositions);
//                getScene().getModel().getActor(0).getCamera().adjustScale(boundingBox);
//
//                getScene().getModel().getActor(0).getCamera().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

//                action.setTarget(actionSequence.getFirst().getFigureByPosition());
//                action.setType(Action.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + action.getFigureByPosition());
//                targetFigure.processAction(action);

                        }


                        setCandidatePathVisibility(Visibility.INVISIBLE);
                        setCandidatePatchVisibility(Visibility.INVISIBLE);
                    }
                }
            }
        });
    }

    public BaseFigure getBaseFigure() {
        return (BaseFigure) getParentFigure();
    }

    // TODO: Delete
    public Port getPort() {
        return getConstruct();
    }

    public List<PathFigure> getPathFigures() {
        List<PathFigure> pathImages = new ArrayList<>();
        for (Path path : getPort().getPaths()) {
            PathFigure pathImage = (PathFigure) getScene().getFigure(path);
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
        for (PathFigure pathImage : getPathFigures()) {
            pathImage.showDocks = false;

            // Deep
            PortFigure targetPortImage = (PortFigure) getScene().getFigure(pathImage.getPath().getTarget());
            targetPortImage.showPaths();
        }
    }

    public void showDocks() {
        for (PathFigure pathImage : getPathFigures()) {
            pathImage.showDocks = true;

            // Deep
            PortFigure targetPortImage = (PortFigure) getScene().getFigure(pathImage.getPath().getTarget());
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
            Surface.drawCircle(getPosition(), shapeRadius, getRotation(), surface);

            // Outline
            boolean showShapeOutline = false;
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);
                Surface.drawCircle(getPosition(), shapeRadius, getRotation(), surface);
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
                        getParentFigure().getRotation() + portGroupRotation[getIndex()],
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
            Point labelPosition = new Point();
            labelPosition.set(
                    getPosition().getX() + shapeRadius + 25,
                    getPosition().getY()
            );

            // Style
            paint.setColor(this.uniqueColor);
            double typeLabelTextSize = 27;

            // Draw
            Surface.drawText(labelPosition, getPort().getType().getTag(), typeLabelTextSize, surface);

        }
    }

    public void update() {
        if (isVisible()) {
            updatePosition();
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

    private void updatePosition() {

        if (relativePortPositions == null) {

            if (getParentFigure() instanceof BaseFigure) {

                // Ports
                BaseFigure baseFigure = (BaseFigure) getParentFigure();
                Base base = baseFigure.getBase();
                getPosition().setReferencePoint(baseFigure.getPosition());

                Rectangle boardRectangle = (Rectangle) baseFigure.getShape(0);

                double portRadius = 40.0f;
                relativePortPositions = new Point[base.getPorts().size()];
                relativePortPositions[0] = new Point(
                        -1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES),
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortPositions[1] = new Point(
                        0,
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortPositions[2] = new Point(
                        +1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES),
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortPositions[3] = new Point(
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius),
                        +1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES)
                );
                relativePortPositions[4] = new Point(
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius),
                        0
                );
                relativePortPositions[5] = new Point(
                        +1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius),
                        -1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES)
                );
                relativePortPositions[6] = new Point(
                        +1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES),
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortPositions[7] = new Point(
                        0,
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortPositions[8] = new Point(
                        -1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES),
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius)
                );
                relativePortPositions[9] = new Point(
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius),
                        -1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES)
                );
                relativePortPositions[10] = new Point(
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius),
                        0
                );
                relativePortPositions[11] = new Point(
                        -1 * ((boardRectangle.getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius),
                        +1 * ((portRadius * 2) + PortFigure.DISTANCE_BETWEEN_NODES)
                );

                relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                        new Point(0, 0),
                        getParentFigure().getRotation(),
                        relativePortPositions[getIndex()]
                );

                getPosition().setRelative(relativePortPositions[getIndex()]);

            }
        }

        if (getParentFigure() instanceof PatchFigure) {

            PatchFigure patchFigure = (PatchFigure) getParentFigure();
            Patch patch = patchFigure.getPatch();

            getPosition().setReferencePoint(patchFigure.getPosition());

            // Ports
            double portRadius = 40.0f;
            relativePortPositions = new Point[patch.getPorts().size()];

            // Calculate coordinates of ports
            double leftPosition = 0 - ((portRadius * 2) * patch.getPorts().size() + PortFigure.DISTANCE_BETWEEN_NODES * (patch.getPorts().size() - 1)) / 2.0;
            double stepSize = (portRadius * 2) + (PortFigure.DISTANCE_BETWEEN_NODES);
            double startOffset = portRadius;

            // Update positions of ports
            for (int i = 0; i < patch.getPorts().size(); i++) {
                relativePortPositions[i] = new Point(
                        leftPosition + i * stepSize + startOffset,
                        +1 * ((patchFigure.getShape().getWidth() / 2.0f) + PortFigure.DISTANCE_FROM_BOARD + portRadius)
                );
            }

            relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                    new Point(0, 0),
                    getParentFigure().getRotation(),
                    relativePortPositions[getIndex()]
            );

            getPosition().setRelative(relativePortPositions[getIndex()]);
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
        for (PathFigure pathImage : getPathFigures()) {
            pathImage.setVisibility(visibility);

            // Deep
            PortFigure targetPortImage = (PortFigure) getScene().getFigure(pathImage.getPath().getTarget());
            targetPortImage.setVisibility(visibility);
        }
    }

    public boolean hasVisiblePaths() {
        for (PathFigure pathImage : getPathFigures()) {
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths() {
        List<Path> ancestorPaths = getPort().getAncestorPaths();
        for (Path ancestorPath : ancestorPaths) {
            PathFigure pathImage = (PathFigure) getScene().getFigure(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public List<PathFigure> getVisiblePaths() {
        List<PathFigure> visiblePathImages = new ArrayList<>();
        for (PathFigure pathImage : getPathFigures()) {
            if (pathImage.isVisible()) {
                visiblePathImages.add(pathImage);
            }
        }
        return visiblePathImages;
    }

    @Override
    public boolean contains(Point point) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + PortFigure.DISTANCE_BETWEEN_NODES));
        } else {
            return false;
        }
    }

    public boolean contains(Point point, double padding) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + padding));
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

    public void setCandidatePathDestinationPosition(Point position) {
        this.candidatePathDestinationPosition.set(position);
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
                        getPosition(),
                        candidatePathDestinationPosition
                );

                Point pathStartPosition = Geometry.calculatePoint(
                        getPosition(),
                        pathRotationAngle,
                        2 * triangleSpacing
                );

                Point pathStopPosition = Geometry.calculatePoint(
                        candidatePathDestinationPosition,
                        pathRotationAngle + 180,
                        2 * triangleSpacing
                );

                Surface.drawTrianglePath(
                        pathStartPosition,
                        pathStopPosition,
                        triangleWidth,
                        triangleHeight,
                        surface
                );

                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getUniqueColor());
                Surface.drawCircle(candidatePathDestinationPosition, shapeRadius, 0.0f, surface);
            }
        }
    }


    private void drawCandidatePatchImage(Surface surface) {

        if (candidatePatchVisibility == Visibility.VISIBLE) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    getPosition(),
                    candidatePathDestinationPosition
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getUniqueColor());
            Surface.drawRectangle(candidatePathDestinationPosition, pathRotationAngle + 180, 250, 250, surface);

        }

    }
}
