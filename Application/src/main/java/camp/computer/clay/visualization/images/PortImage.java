package camp.computer.clay.visualization.images;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Frame;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Point;
import camp.computer.clay.visualization.util.Rectangle;
import camp.computer.clay.visualization.util.Shape;

public class PortImage extends Image {

    public final static String TYPE = "port";

    // <STYLE>
    public static double DISTANCE_FROM_BOARD = 45.0f;
    public static double DISTANCE_BETWEEN_NODES = 15.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    private boolean showShapeLayer = true;
    private boolean showStyleLayer = true;
    private boolean showDataLayer = true;
    private boolean showAnnotationLayer = true;

    public double shapeRadius = 40.0f;
    private boolean showShapeOutline = false;

    private int uniqueColor = Color.BLACK;

    public int getIndex() {
        return getFormImage().getPortImageIndex(this);
    }
    // </STYLE>

    // <DATA>
    private int dataSampleCount = 40;
    private double[] portDataSamples = new double[dataSampleCount];
    // </DATA>

    // <SIMULATED_DATA>
    private int previousSwitchState = 0;
    private double switchPeriod = 20.0f;
    private int switchHalfPeriodSampleCount = 0;
    private double pulsePeriod = 20.0f;
    private double pulseDutyCycle = 0.5f;
    private int pulsePeriodSampleCount = 0;
    private int previousPulseState = 0;
    private double xWaveStart = 0;
    // </SIMULATED_DATA>

    public static final String CLASS_NAME = "PORT_SPRITE";

    private boolean isCandidatePathVisible = false;
    private Point candidatePathDestinationPosition = new Point(40, 80);

    public PortImage(Port port) {
        super(port);
        setType(TYPE);
        setup();
    }

    private void setup() {
        uniqueColor = updateUniqueColor();
        setupData();
        setVisibility(false);
    }

    private void setupData() {
        for (int i = 0; i < this.portDataSamples.length; i++) {
            this.portDataSamples[i] = -(this.shapeRadius / 2.0f) + 0;
        }
    }

    public FrameImage getFormImage() {
        return (FrameImage) getParentImage();
    }

    public ArrayList<PathImage> getPathImages() {
        ArrayList<PathImage> pathImages = new ArrayList<>();
        for (Path path : getPort().getPaths()) {
            PathImage pathImage = (PathImage) getVisualization().getImage(path);
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
        return camp.computer.clay.visualization.util.Color.getUniqueColor(this);
    }

    public void showPaths() {
        for (PathImage pathImage : getPathImages()) {
            pathImage.showDocks = false;

            // Deep
            PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
            targetPortImage.showPaths();
        }
    }

    public void showDocks() {
        for (PathImage pathImage : getPathImages()) {
            pathImage.showDocks = true;

            // Deep
            PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
            targetPortImage.showDocks();
        }
    }

    public void draw(VisualizationSurface visualizationSurface) {
        if (isVisible()) {
            // Image
            drawShape(visualizationSurface);
            drawStyle(visualizationSurface);
            drawData(visualizationSurface);
            drawAnnotation(visualizationSurface);

            // Draw children sprites
            drawCandidatePathImages(visualizationSurface);
        }
    }

    /**
     * Draws the shape of the sprite filled with a solid color. Graphically, this represents a
     * placeholder for the sprite.
     *
     * @param visualizationSurface
     */
    public void drawShape(VisualizationSurface visualizationSurface) {

        if (showShapeLayer) {

            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            paint.setColor(FLOW_PATH_COLOR_NONE);

            Shape.drawCircle(getPosition(), shapeRadius, 0, canvas, paint);

            // Outline
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);

                Shape.drawCircle(getPosition(), shapeRadius, 0, canvas, paint);
            }
        }
    }

    /**
     * Draws the sprite's detail front layer.
     *
     * @param visualizationSurface
     */
    public void drawStyle(VisualizationSurface visualizationSurface) {

        if (showStyleLayer) {

            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            if (getPort().getType() != Port.Type.NONE) {

                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(this.uniqueColor);
                Shape.drawCircle(getPosition(), shapeRadius, getRotation(), canvas, paint);

                // Outline
                if (showShapeOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    Shape.drawCircle(getPosition(), shapeRadius, getRotation(), canvas, paint);
                }
            }
        }
    }

    public Port getPort() {
        Port port = (Port) getModel();
        return port;
    }

    /**
     * Draws the sprite's data layer.
     *
     * @param visualizationSurface
     */
    private void drawData(VisualizationSurface visualizationSurface) {

        if (showDataLayer) {

            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

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
                        samplePoint = new Point(
                                this.getPosition().getX() + portDataSamples[i],
                                this.getPosition().getY() + shapeRadius - i * plotStep
                        );
                    }

                    // Rotate point
                    FrameImage frameImage = (FrameImage) getParentImage();
                    rotatedPortDataSamplePoints[i] = Geometry.calculateRotatedPoint(
                            getPosition(),
                            getFormImage().getRotation() + portGroupRotation[getIndex()],
                            samplePoint
                    );
//                    rotatedPortDataSamplePoints[i] = samplePoint;
                }

                if (port.getDirection() == Port.Direction.INPUT) {

                    for (int i = 0; i < portDataSamples.length - 1; i++) {
                        Shape.drawLine(rotatedPortDataSamplePoints[i], rotatedPortDataSamplePoints[i + 1], canvas, paint);
                    }
                } else if (port.getDirection() == Port.Direction.OUTPUT) {
                    for (int i = 0; i < portDataSamples.length - 1; i++) {
                        Shape.drawLine(rotatedPortDataSamplePoints[i], rotatedPortDataSamplePoints[i + 1], canvas, paint);
                    }
                }
            }
        }
    }

    /**
     * Draws the sprite's annotation layer. Contains labels and other text.
     *
     * @param visualizationSurface
     */
    public void drawAnnotation(VisualizationSurface visualizationSurface) {

        if (showAnnotationLayer) {

            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            // Geometry
            Point labelPosition = new Point();
            labelPosition.set(
                    getPosition().getX() + shapeRadius + 25,
                    getPosition().getY()
            );

            // Style
            paint.setColor(this.uniqueColor);
            double typeLabelTextSize = 27;

            String typeLabelText = "";
            if (getPort().getType() == Port.Type.SWITCH) {
                typeLabelText = "switch";
            } else if (getPort().getType() == Port.Type.PULSE) {
                typeLabelText = "pulse";
            } else if (getPort().getType() == Port.Type.WAVE) {
                typeLabelText = "wave";
            }
            // TODO: none, 5v, 3.3v, (data) I2C, SPI, (monitor) A2D, voltage, current

            // Draw
            Shape.drawText(labelPosition, typeLabelText, typeLabelTextSize, canvas, paint);
        }
    }

    public void update() {
        if (isVisible()) {
            if (!isTouched) {
                updatePosition();
            }
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
        }
    }

    private void updatePosition() {

        // <TODO>
        // TODO: Replace this with getParentSpriteBounds() -- get bounding box based on parent sprite's shape and orientation (to get width and height)
        FrameImage frameImage = (FrameImage) getParentImage();
        Frame frame = frameImage.getForm();
        // </TODO>

        // Ports
        double portRadius = 40.0f;
        Point[] relativePortPositions = new Point[frame.getPorts().size()];
        relativePortPositions[0] = new Point(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[1] = new Point(
                0,
                +1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[2] = new Point(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[3] = new Point(
                +1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[4] = new Point(
                +1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[5] = new Point(
                +1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[6] = new Point(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[7] = new Point(
                0,
                -1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[8] = new Point(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[9] = new Point(
                -1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[10] = new Point(
                -1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[11] = new Point(
                -1 * ((frameImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );

        relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                new Point(0, 0), //getParentImage().getPosition(),
                getParentImage().getAbsoluteRotation(), //  + (((rot - 1) * 90) - 90) + ((rot - 1) * 90),
                relativePortPositions[getIndex()]
        );

        setRelativePosition(relativePortPositions[getIndex()]);
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

    public void setVisibility(boolean isVisible) {
        super.setVisibility(isVisible);
        showShapeLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public void setPathVisibility(boolean isVisible) {
        for (PathImage pathImage : getPathImages()) {
            if (pathImage != null) {
                pathImage.setVisibility(isVisible);

                // Deep
                PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
                targetPortImage.setVisibility(isVisible);
            }
        }
    }

    public boolean hasVisiblePaths() {
        for (PathImage pathImage : getPathImages()) {
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths() {
        ArrayList<Path> ancestorPaths = getPort().getAncestorPaths();
        for (Path ancestorPath : ancestorPaths) {
            PathImage pathImage = (PathImage) getVisualization().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PathImage> getVisiblePaths() {
        ArrayList<PathImage> visiblePathImages = new ArrayList<PathImage>();
        for (PathImage pathImage : getPathImages()) {
            if (pathImage.isVisible()) {
                visiblePathImages.add(pathImage);
            }
        }
        return visiblePathImages;
    }

    @Override
    public boolean isTouching(Point point) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES));
        } else {
            return false;
        }
    }

    public boolean isTouching(Point point, double padding) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + padding));
        } else {
            return false;
        }
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.Type.NONE) {
            // Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TOUCH) {
            // Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TAP) {

            Port port = getPort();

            if (port.getType() == Port.Type.NONE) {

                port.setDirection(Port.Direction.INPUT);
                port.setType(Port.Type.getNextType(port.getType()));

                // TODO: Speak ~ "setting as input. you can send the data to another board if you want. touchPositions another board."

            } else if (!port.hasPath() && port.getAncestorPaths().size() == 0) {

                // TODO: Replace with state of perspective. i.e., Check if seeing a single path.

                Port.Type nextType = port.getType();
                while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                    nextType = Port.Type.getNextType(nextType);
                }
                port.setType(nextType);

            } else if (!hasVisiblePaths() && !hasVisibleAncestorPaths()) {

                // TODO: Replace hasVisiblePaths() with check for focusedSprite/Path

                // TODO: If second press, change the channel.

                // Remove focus from other machines and their ports.
                for (FrameImage frameImage : getVisualization().getFormImages()) {
                    frameImage.setTransparency(0.05f);
                    frameImage.hidePortImages();
                    frameImage.hidePathImages();
                }

                // Reduce focus on the machine
                getFormImage().setTransparency(0.05f);

                // Focus on the port
                //portImage.getFormImage().showPathImage(portImage.getIndex(), true);
                showPaths();
                setVisibility(true);
                setPathVisibility(true);

                ArrayList<Path> paths = port.getPathsByPort();
                for (Path connectedPath : paths) {
                    // Show ports
                    getVisualization().getImage(connectedPath.getSource()).setVisibility(true);
                    ((PortImage) getVisualization().getImage(connectedPath.getSource())).showPaths();
                    getVisualization().getImage(connectedPath.getTarget()).setVisibility(true);
                    ((PortImage) getVisualization().getImage(connectedPath.getTarget())).showPaths();
                    // Show path
                    getVisualization().getImage(connectedPath).setVisibility(true);
                }

                // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

                // Perspective
                ArrayList<Port> pathPorts = port.getPortsInPaths(paths);
                ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
                ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
                getVisualization().getSimulation().getBody(0).getPerspective().adjustPerspectiveScale(boundingBox);

                getVisualization().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

            } else if (hasVisiblePaths() || hasVisibleAncestorPaths()) {

                // Paths are being shown. Touching a port changes the port type. This will also
                // updates the corresponding path requirement.

                // TODO: Replace with state of perspective. i.e., Check if seeing a single path.

                Port.Type nextType = port.getType();
                while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                    nextType = Port.Type.getNextType(nextType);
                }
                port.setType(nextType);

            }

            setCandidatePathVisibility(false);

        } else if (touchInteraction.getType() == TouchInteraction.Type.HOLD) {
            // Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.MOVE) {
            // Log.v("onTouchInteraction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TWITCH) {
            // Log.v("onTouchInteraction", "TouchInteraction.TWITCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.DRAG) {

            Log.v("onHoldListener", "Port draggin!");

            setCandidatePathDestinationPosition(touchInteraction.getPosition());
            setCandidatePathVisibility(true);

            // Setup port type and flow direction
            Port port = getPort();
            if (port.getDirection() == Port.Direction.NONE) {
                Log.v("onHoldListener", "OH GOD!");
                port.setDirection(Port.Direction.INPUT);
            }
            if (port.getType() == Port.Type.NONE) {
                Log.v("onHoldListener", "OH SHIT!");
                port.setType(Port.Type.getNextType(port.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
            }

        } else if (touchInteraction.getType() == TouchInteraction.Type.RELEASE) {
//             Log.v("onTouchInteraction", "TouchInteraction.RELEASE to " + CLASS_NAME);
            Log.v("onHoldListener", "TouchInteraction.RELEASE to " + CLASS_NAME);

            setCandidatePathVisibility(false);
        }
    }

    public void setCandidatePathVisibility(boolean isVisible) {
        this.isCandidatePathVisible = isVisible;
    }

    public boolean getCandidatePathVisibility() {
        return this.isCandidatePathVisible;
    }

    public void setCandidatePathDestinationPosition(Point position) {
        this.candidatePathDestinationPosition.set(position);
    }

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawCandidatePathImages(VisualizationSurface visualizationSurface) {
        if (isCandidatePathVisible) {

            if (getPort().getType() != Port.Type.NONE) {

                Canvas canvas = visualizationSurface.getCanvas();
                Paint paint = visualizationSurface.getPaint();

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

                Shape.drawTrianglePath(
                        pathStartPosition,
                        pathStopPosition,
                        triangleWidth,
                        triangleHeight,
                        canvas,
                        paint
                );

                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getUniqueColor());
                Shape.drawCircle(candidatePathDestinationPosition, shapeRadius, 0.0f, canvas, paint);
            }
        }
    }
}
