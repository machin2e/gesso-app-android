package camp.computer.clay.viz.img;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import camp.computer.clay.app.Surface;
import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.sim.Frame;
import camp.computer.clay.model.sim.Path;
import camp.computer.clay.model.sim.Port;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.arch.Visibility;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Point;
import camp.computer.clay.viz.util.Rectangle;

public class old_PortImage extends Image {

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
        return getFrameImage().getPortImageIndex(this);
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

    public old_PortImage(Port port) {
        super(port);
        setType(TYPE);
        setup();
    }

    private void setup() {
        uniqueColor = updateUniqueColor();
        setupData();
        setVisibility(Visibility.INVISIBLE);
    }

    private void setupData() {
        for (int i = 0; i < this.portDataSamples.length; i++) {
            this.portDataSamples[i] = -(this.shapeRadius / 2.0f) + 0;
        }
    }

    public old_FrameImage getFrameImage() {
        return (old_FrameImage) getParentImage();
    }

    public List<old_PathImage> getPathImages() {
        List<old_PathImage> oldPathImages = new ArrayList<>();
        for (Path path: getPort().getPaths()) {
            old_PathImage oldPathImage = (old_PathImage) getViz().getImage(path);
            oldPathImages.add(oldPathImage);
        }

        return oldPathImages;
    }

    public int getUniqueColor() {
        return this.uniqueColor;
    }

    public void setUniqueColor(int uniqueColor) {
        this.uniqueColor = uniqueColor;
    }

    public int updateUniqueColor() {
        return camp.computer.clay.viz.util.Color.getUniqueColor(this);
    }

    public void showPaths() {
        for (old_PathImage oldPathImage : getPathImages()) {
            oldPathImage.showDocks = false;

            // Deep
            old_PortImage targetOldPortImage = (old_PortImage) getViz().getImage(oldPathImage.getPath().getTarget());
            targetOldPortImage.showPaths();
        }
    }

    public void showDocks() {
        for (old_PathImage oldPathImage : getPathImages()) {
            oldPathImage.showDocks = true;

            // Deep
            old_PortImage targetOldPortImage = (old_PortImage) getViz().getImage(oldPathImage.getPath().getTarget());
            targetOldPortImage.showDocks();
        }
    }

    public void draw(Surface surface) {
        if (isVisible()) {
            // Image
            drawShape(surface);
            drawStyle(surface);
            drawData(surface);
            drawAnnotation(surface);

            // Draw children sprites
            drawCandidatePathImages(surface);
        }
    }

    /**
     * Draws the shape of the sprite filled with a solid color. Graphically, this represents a
     * placeholder for the sprite.
     * @param surface
     */
    public void drawShape(Surface surface) {

        if (showShapeLayer) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            paint.setColor(FLOW_PATH_COLOR_NONE);

            getViz().drawCircle(getPosition(), shapeRadius, 0);

            // Outline
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);

                getViz().drawCircle(getPosition(), shapeRadius, 0);
            }
        }
    }

    /**
     * Draws the sprite's detail front layer.
     * @param surface
     */
    public void drawStyle(Surface surface) {

        if (showStyleLayer) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            if (getPort().getType() != Port.Type.NONE) {

                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(this.uniqueColor);
                getViz().drawCircle(getPosition(), shapeRadius, getRotation());

                // Outline
                if (showShapeOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    getViz().drawCircle(getPosition(), shapeRadius, getRotation());
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
     * @param surface
     */
    private void drawData(Surface surface) {

        if (showDataLayer) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.Type.NONE) {

                // Outline
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.0f);
                paint.setColor(Color.WHITE);

                double plotStep = (float) ((2.0f * (float) shapeRadius) / (float) portDataSamples.length);

                Point[] rotatedPortDataSamplePoints = new Point[portDataSamples.length];

                double[] portGroupRotation = new double[] {
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

                for (int k = 0; k < portDataSamples.length; k++) {

                    Point samplePoint = null;

                    if (port.getDirection() == Port.Direction.INPUT) {
                        // Set position before rotation adjustment
                        samplePoint = new Point(
                                this.getPosition().getX() + portDataSamples[k],
                                this.getPosition().getY() + -shapeRadius + k * plotStep
                        );
                    } else if (port.getDirection() == Port.Direction.OUTPUT) {
                        samplePoint = new Point(
                                this.getPosition().getX() + portDataSamples[k],
                                this.getPosition().getY() + shapeRadius - k * plotStep
                        );
                    }

                    // Rotate point
                    rotatedPortDataSamplePoints[k] = Geometry.calculateRotatedPoint(
                            this.getPosition(),
                            getAbsoluteRotation() + portGroupRotation[getIndex()],
                            samplePoint
                    );
                }

                if (port.getDirection() == Port.Direction.INPUT) {

                    for (int k = 0; k < portDataSamples.length - 1; k++) {
                        getViz().drawLine(rotatedPortDataSamplePoints[k], rotatedPortDataSamplePoints[k + 1]);
                    }
                } else if (port.getDirection() == Port.Direction.OUTPUT) {
                    for (int k = 0; k < portDataSamples.length - 1; k++) {
                        getViz().drawLine(rotatedPortDataSamplePoints[k], rotatedPortDataSamplePoints[k + 1]);
                    }
                }
            }
        }
    }

    /**
     * Draws the sprite's annotation layer. Contains labels and other text.
     * @param surface
     */
    public void drawAnnotation(Surface surface) {

        if (showAnnotationLayer) {

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
            getViz().drawText(labelPosition, typeLabelText, typeLabelTextSize);
        }
    }

    public void generate() {
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
        old_FrameImage oldFrameImage = (old_FrameImage) getParentImage();
        Frame frame = oldFrameImage.getFrame();
        // </TODO>

        // Ports
        double portRadius = 40.0f;
        Point[] relativePortPositions = new Point[frame.getPorts().size()];
        relativePortPositions[0] = new Point(
                -1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((oldFrameImage.boardWidth/ 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[1] = new Point(
                0,
                +1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[2] = new Point(
                +1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[3] = new Point(
                +1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[4] = new Point(
                +1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[5] = new Point(
                +1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[6] = new Point(
                +1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[7] = new Point(
                0,
                -1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[8] = new Point(
                -1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[9] = new Point(
                -1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[10] = new Point(
                -1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[11] = new Point(
                -1 * ((oldFrameImage.boardWidth / 2.0f) + old_PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + old_PortImage.DISTANCE_BETWEEN_NODES)
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

    public void setVisibility(Visibility visibility) {
        super.setVisibility(visibility);
//        showShapeLayer = visibility;
//        showStyleLayer = visibility;
//        showDataLayer = visibility;
//        showAnnotationLayer = isVisible;
    }

    public void setPathVisibility (Visibility visibility) {
        for (old_PathImage oldPathImage : getPathImages()) {
            if (oldPathImage != null) {
                oldPathImage.setVisibility(visibility);

                // Deep
                old_PortImage targetOldPortImage = (old_PortImage) getViz().getImage(oldPathImage.getPath().getTarget());
                targetOldPortImage.setVisibility(visibility);
            }
        }
    }

    public boolean hasVisiblePaths () {
        for (old_PathImage oldPathImage : getPathImages()) {
            if (oldPathImage.isVisible() && !oldPathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths() {
        List<Path> ancestorPaths = getPort().getAncestorPaths();
        for (Path ancestorPath: ancestorPaths) {
            old_PathImage oldPathImage = (old_PathImage) getViz().getImage(ancestorPath);
            if (oldPathImage.isVisible() && !oldPathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public List<old_PathImage> getVisiblePaths() {
        List<old_PathImage> visibleOldPathImages = new ArrayList<>();
        for (old_PathImage oldPathImage : getPathImages()) {
            if (oldPathImage.isVisible()) {
                visibleOldPathImages.add(oldPathImage);
            }
        }
        return visibleOldPathImages;
    }

    @Override
    public boolean isTouching(Point point) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, getPosition()) < (shapeRadius + old_PortImage.DISTANCE_BETWEEN_NODES));
        } else {
            return false;
        }
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == OnTouchActionListener.Type.NONE) {
            // Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.TOUCH) {
            // Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.TAP) {

            Port port = getPort();

            if (port.getType() == Port.Type.NONE) {

                port.setDirection(Port.Direction.INPUT);
                port.setType(Port.Type.getNextType(port.getType()));

                // TODO: Speak ~ "setting as input. you can send the data to another board if you want. touchPositions another board."

            } else if (!port.hasPaths() && port.getAncestorPaths().size() == 0) {

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
                for (Image image : getViz().getImages().filterType(Frame.class).getList()) {
                    old_FrameImage oldFrameImage = (old_FrameImage) image;
                    oldFrameImage.setTransparency(0.05f);
                    oldFrameImage.hidePortImages();
                    oldFrameImage.hidePathImages();
                }

                // Reduce focus on the machine
                getFrameImage().setTransparency(0.05f);

                // Focus on the port
                //portImage.getFrameImage().showPathImage(portImage.getIndex(), true);
                showPaths();
                setVisibility(Visibility.VISIBLE);
                setPathVisibility(Visibility.VISIBLE);

                List<Path> paths = port.getConnectedPaths();
                for (Path connectedPath : paths) {
                    // Show ports
                    getViz().getImage(connectedPath.getSource()).setVisibility(Visibility.VISIBLE);
                    ((old_PortImage) getViz().getImage(connectedPath.getSource())).showPaths();
                    getViz().getImage(connectedPath.getTarget()).setVisibility(Visibility.VISIBLE);
                    ((old_PortImage) getViz().getImage(connectedPath.getTarget())).showPaths();
                    // Show path
                    getViz().getImage(connectedPath).setVisibility(Visibility.VISIBLE);
                }

                // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

                // Perspective
                List<Port> pathPorts = port.getPortsByPath(paths);
                List<Point> pathPortPositions = getViz().getImages(pathPorts).getPositions();
                // List<Point> pathPortPositions = getViz().getImages().old_filterType(old_PortImage.TYPE).getPositions();
                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
                getViz().getSimulation().getBody(0).getPerspective().adjustPerspectiveScale(boundingBox);

                getViz().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenter(pathPortPositions));

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

        } else if (touchInteraction.getType() == OnTouchActionListener.Type.HOLD) {
            // Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.DRAG) {

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

        } else if (touchInteraction.getType() == OnTouchActionListener.Type.RELEASE) {
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
    public void drawCandidatePathImages(Surface surface) {
        if (isCandidatePathVisible) {

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

                getViz().drawTrianglePath(
                        pathStartPosition,
                        pathStopPosition,
                        triangleWidth,
                        triangleHeight
                );

                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getUniqueColor());
                getViz().drawCircle(candidatePathDestinationPosition, shapeRadius, 0.0f);
            }
        }
    }
}
