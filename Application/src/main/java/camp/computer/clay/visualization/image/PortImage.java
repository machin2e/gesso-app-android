package camp.computer.clay.visualization.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Frame;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;

public class PortImage extends Image {

    // <STYLE>
    public static double DISTANCE_FROM_BOARD = 45.0f;
    public static double DISTANCE_BETWEEN_NODES = 15.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    public double shapeRadius = 40.0f;
    private boolean showShapeOutline = false;

    private int uniqueColor = Color.BLACK;

    public int getIndex() {
        if (getParentImage() instanceof FrameImage) {
            return getFrameImage().getPortImageIndex(this);
        } else if (getParentImage() instanceof PatchImage) {
            return ((PatchImage) getParentImage()).getPortImageIndex(this);
        }
        return -1;
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

    private boolean isCandidatePathVisible = false;
    private Point candidatePathDestinationPosition = new Point(40, 80);

    private boolean isCandidatePeripheralVisible = false;

    public PortImage(Port port) {
        super(port);
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

    public FrameImage getFrameImage() {
        return (FrameImage) getParentImage();
    }

    public List<PathImage> getPathImages() {
        List<PathImage> pathImages = new ArrayList<>();
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

    public void draw(Surface surface) {
        if (isVisible()) {

            // Port
            drawShape(surface);
            drawStyle(surface);
            drawData(surface);
            drawAnnotation(surface);

            // Candidate Path
            drawCandidatePathImages(surface);

            // Candidate Patch
            drawCandidatePeripheralImage(surface);
        }
    }

    /**
     * Draws the shape of the sprite filled with a solid color. Graphically, this represents a
     * placeholder for the sprite.
     *
     * @param surface
     */
    public void drawShape(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setColor(FLOW_PATH_COLOR_NONE);

        Surface.drawCircle(getPosition(), shapeRadius, 0, surface);

        // Outline
        if (showShapeOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setColor(Color.BLACK);

            Surface.drawCircle(getPosition(), shapeRadius, 0, surface);
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
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);
                Surface.drawCircle(getPosition(), shapeRadius, getRotation(), surface);
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

        // <TODO>
        // TODO: Replace this with getParentSpriteBounds() -- get bounding box based on parent sprite's shape and orientation (to get width and height)
        if (getParentImage() instanceof FrameImage) {
            FrameImage frameImage = (FrameImage) getParentImage();
            Frame frame = frameImage.getFrame();
            // </TODO>

            Rectangle boundingRectangle = getBoundingRectangle();

            Log.v("BoundingRectangle", "boundary width: " + boundingRectangle.getWidth() + ", height: " + boundingRectangle.getHeight());

            // Ports
            double portRadius = 40.0f;
            Point[] relativePortPositions = new Point[frame.getPorts().size()];
            relativePortPositions[0] = new Point(
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[1] = new Point(
                    0,
                    +1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[2] = new Point(
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[3] = new Point(
                    +1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );
            relativePortPositions[4] = new Point(
                    +1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    0
            );
            relativePortPositions[5] = new Point(
                    +1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );
            relativePortPositions[6] = new Point(
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    -1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[7] = new Point(
                    0,
                    -1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[8] = new Point(
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    -1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[9] = new Point(
                    -1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );
            relativePortPositions[10] = new Point(
                    -1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    0
            );
            relativePortPositions[11] = new Point(
                    -1 * ((boundingRectangle.getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );

            relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                    new Point(0, 0), //getParentImage().getPosition(),
                    getParentImage().getAbsoluteRotation(), //  + (((rot - 1) * 90) - 90) + ((rot - 1) * 90),
                    relativePortPositions[getIndex()]
            );

            setRelativePosition(relativePortPositions[getIndex()]);

        } else if (getParentImage() instanceof PatchImage) {

            PatchImage patchImage = (PatchImage) getParentImage();
            Patch patch = patchImage.getPeripheral();
            // </TODO>

            // Ports
            double portRadius = 40.0f;
            Point[] relativePortPositions = new Point[patch.getPorts().size()];
            relativePortPositions[0] = new Point(
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[1] = new Point(
                    0,
                    +1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[2] = new Point(
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
//            relativePortPositions[3] = new Point(
//                    +1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
//            );
//            relativePortPositions[4] = new Point(
//                    +1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    0
//            );
//            relativePortPositions[5] = new Point(
//                    +1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
//            );
//            relativePortPositions[6] = new Point(
//                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
//                    -1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
//            );
//            relativePortPositions[7] = new Point(
//                    0,
//                    -1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
//            );
//            relativePortPositions[8] = new Point(
//                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
//                    -1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
//            );
//            relativePortPositions[9] = new Point(
//                    -1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
//            );
//            relativePortPositions[10] = new Point(
//                    -1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    0
//            );
//            relativePortPositions[11] = new Point(
//                    -1 * ((patchImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
//            );

            relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                    new Point(0, 0), //getParentImage().getPosition(),
                    getParentImage().getAbsoluteRotation(), //  + (((rot - 1) * 90) - 90) + ((rot - 1) * 90),
                    relativePortPositions[getIndex()]
            );

            setRelativePosition(relativePortPositions[getIndex()]);
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
        for (PathImage pathImage : getPathImages()) {
            if (pathImage != null) {
                pathImage.setVisibility(visibility);

                // Deep
                PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
                targetPortImage.setVisibility(visibility);
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
        List<Path> ancestorPaths = getPort().getAncestorPaths();
        for (Path ancestorPath : ancestorPaths) {
            PathImage pathImage = (PathImage) getVisualization().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public List<PathImage> getVisiblePaths() {
        List<PathImage> visiblePathImages = new ArrayList<>();
        for (PathImage pathImage : getPathImages()) {
            if (pathImage.isVisible()) {
                visiblePathImages.add(pathImage);
            }
        }
        return visiblePathImages;
    }

    @Override
    public boolean containsPoint(Point point) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES));
        } else {
            return false;
        }
    }

    public boolean containsPoint(Point point, double padding) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + padding));
        } else {
            return false;
        }
    }

    @Override
    public void onAction(Action action) {

        if (action.getType() == Action.Type.NONE) {
            // Log.v("onAction", "Action.NONE to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.TOUCH) {
            // Log.v("onAction", "Action.TOUCH to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.TAP) {

            Port port = getPort();

            if (port.getType() == Port.Type.NONE) {

                port.setDirection(Port.Direction.INPUT);
                port.setType(Port.Type.next(port.getType()));

                // TODO: Speak ~ "setting as input. you can send the data to another board if you want. touchPoints another board."

            } else if (!port.hasPath() && port.getAncestorPaths().size() == 0) {

                // TODO: Replace with state of perspective. i.e., Check if seeing a single path.

                Port.Type nextType = port.getType();
                while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                    nextType = Port.Type.next(nextType);
                }
                port.setType(nextType);

            } else if (!hasVisiblePaths() && !hasVisibleAncestorPaths()) {

                // TODO: Replace hasVisiblePaths() with check for focusedSprite/Path

                // TODO: If second press, change the channel.

                // Remove focus from other machines and their ports.
                for (FrameImage frameImage : getVisualization().getFrameImages()) {
                    frameImage.setTransparency(0.1);
                    frameImage.hidePortImages();
                    frameImage.hidePathImages();
                }

                // Reduce focus on the machine
                getParentImage().setTransparency(0.1);

                // Focus on the port
                //portImage.getFrameImage().showPathImage(portImage.getIndex(), true);
                showPaths();
                setVisibility(Visibility.VISIBLE);
                setPathVisibility(Visibility.VISIBLE);

                List<Path> paths = port.getGraph();
                for (Path connectedPath : paths) {
                    // Show ports
                    getVisualization().getImage(connectedPath.getSource()).setVisibility(Visibility.VISIBLE);
                    ((PortImage) getVisualization().getImage(connectedPath.getSource())).showPaths();
                    getVisualization().getImage(connectedPath.getTarget()).setVisibility(Visibility.VISIBLE);
                    ((PortImage) getVisualization().getImage(connectedPath.getTarget())).showPaths();
                    // Show path
                    getVisualization().getImage(connectedPath).setVisibility(Visibility.VISIBLE);
                }

                // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPoints another board.");

                // Perspective
                List<Port> pathPorts = port.getPorts(paths);
                List<Image> pathPortImages = getVisualization().getImages(pathPorts);
                List<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
                getVisualization().getEnvironment().getBody(0).getPerspective().adjustScale(boundingBox);

                getVisualization().getEnvironment().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

            } else if (hasVisiblePaths() || hasVisibleAncestorPaths()) {

                // Paths are being shown. Touching a port changes the port type. This will also
                // updates the corresponding path requirement.

                // TODO: Replace with state of perspective. i.e., Check if seeing a single path.

                Port.Type nextType = port.getType();
                while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                    nextType = Port.Type.next(nextType);
                }
                port.setType(nextType);

            }

            setCandidatePathVisibility(false);

        } else if (action.getType() == Action.Type.MOVE) {
            // Log.v("onAction", "Action.MOVE to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.DRAG) {

            Log.v("onHoldListener", "Port draggin!");

            // Candidate Path Visibility
            setCandidatePathDestinationPosition(action.getPosition());
            setCandidatePathVisibility(true);

            // Candidate Patch Visibility

            boolean isPeripheral = true;
            for (FrameImage nearbyFrameImage : getVisualization().getFrameImages()) {

                // Update style of nearby machines
                double distanceToFrameImage = Geometry.calculateDistance(
                        candidatePathDestinationPosition,
                        nearbyFrameImage.getPosition()
                );

                if (distanceToFrameImage < 500) {
                    isPeripheral = false;
                    break;
                }
            }

            if (isPeripheral) {
                setCandidatePeripheralVisibility(true);
            } else {
                setCandidatePeripheralVisibility(false);
            }

            // Setup port type and flow direction
            Port port = getPort();
            if (port.getDirection() == Port.Direction.NONE) {
                Log.v("onHoldListener", "OH GOD!");
                port.setDirection(Port.Direction.INPUT);
            }
            if (port.getType() == Port.Type.NONE) {
                Log.v("onHoldListener", "OH SHIT!");
                port.setType(Port.Type.next(port.getType()));
            }

        } else if (action.getType() == Action.Type.RELEASE) {


            // ...last processAction was on a port image.

            // PortImage portImage = (PortImage) action.getImageByPosition();
            PortImage sourcePortImage = (PortImage) action.getInteraction().getFirst().getTarget();

            if (sourcePortImage.isDragging()) {

                // Get nearest port image
                PortImage nearestPortImage = (PortImage) getVisualization().getImages().filterType(PortImage.class).getNearest(action.getPosition());
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

                List<Path> paths = getVisualization().getEnvironment().getPaths();

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

//                            Path replacementPath = new Path(nearestPortImage.getPort(), path.getTarget());
//                            nearestPortImage.getPort().addPath(replacementPath);
//
//                            PathImage replacementPathImage = new PathImage(path);
//                            replacementPathImage.setVisualization(perspective.getVisualization());
//                            addImage(path, replacementPathImage, "paths");
//
//                            PortImage targetPortImage = (PortImage) getImage(path.getTarget());
//                            targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                    nearestPort.addPath(path);

                }

                // Restore port image's position
                sourcePortImage.setDragging(false);

                // Perspective
                action.getBody().getPerspective().focusOnPath(sourcePort);

            } else {

//                if (sourcePortImage.getCandidatePeripheralVisibility() == true) {
//
//                    // Model
//                    Patch peripheral = new Patch();
//                    getPerspective().getVisualization().getEnvironment().addPatch(peripheral);
//
//                    // Visualization (Layer)
//                    String layerTag = "peripherals";
//                    getPerspective().getVisualization().addLayer(layerTag);
//                    Layer defaultLayer = getPerspective().getVisualization().getLayer(layerTag);
//
//                    // Image
//                    PatchImage peripheralImage = new PatchImage(peripheral);
//                    peripheralImage.setPosition(action.getPosition());
//                    peripheralImage.setVisualization(getPerspective().getVisualization());
//
//                    // Visualization
//                    getPerspective().getVisualization().addImage(peripheral, peripheralImage, layerTag);
//
//                }

                // Show ports of nearby forms
                boolean useNearbyPortImage = false;
                for (FrameImage nearbyFrameImage : getVisualization().getFrameImages()) {

                    Log.v("Action", "A");

                    // Update style of nearby machines
                    double distanceToFrameImage = Geometry.calculateDistance(
                            action.getPosition(),
                            nearbyFrameImage.getPosition()
                    );

                    if (distanceToFrameImage < nearbyFrameImage.getBoundingRectangle().getHeight() + 50) {

                        Log.v("Action", "B");

                        // TODO: Use overlappedImage instanceof PortImage

                        for (PortImage nearbyPortImage : nearbyFrameImage.getPortImages()) {

                            if (nearbyPortImage != sourcePortImage) {
                                if (nearbyPortImage.containsPoint(action.getPosition(), 50)) {

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
                                    Port sourcePort = (Port) getVisualization().getModel(sourcePortImage);
                                    Port targetPort = (Port) getVisualization().getModel(nearbyPortImage);

                                    if (!sourcePort.hasAncestor(targetPort)) {

                                        Log.v("Action", "D.1");

                                        Path path = new Path(sourcePort, targetPort);
                                        path.setType(Path.Type.MESH);
                                        sourcePort.addPath(path);

                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setVisualization(getVisualization());
                                        getVisualization().addImage(path, pathImage, "paths");

                                        PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getTarget());
                                        targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                        // Perspective
                                        action.getBody().getPerspective().focusOnPath(sourcePort);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }

//                portImage.processAction(action);

                if (!useNearbyPortImage) {

                    Port port = (Port) sourcePortImage.getModel();

                    port.setDirection(Port.Direction.INPUT);

                    if (port.getType() == Port.Type.NONE) {
                        port.setType(Port.Type.next(port.getType()));
                    }
                }

                sourcePortImage.setCandidatePathVisibility(false);

                // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPoints another board.");

//                // Perspective
//                ArrayList<Port> pathPorts = port.getPorts(paths);
//                ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.getBoundingBox(pathPortPositions);
//                getVisualization().getEnvironment().getBody(0).getPerspective().adjustScale(boundingBox);
//
//                getVisualization().getEnvironment().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

//                action.setTarget(interaction.getFirst().getImageByPosition());
//                action.setType(Action.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + action.getImageByPosition());
//                targetImage.processAction(action);

            }


            setCandidatePathVisibility(false);
            setCandidatePeripheralVisibility(false);
        }
    }

    public void setCandidatePathVisibility(boolean isVisible) {
        this.isCandidatePathVisible = isVisible;
    }

    public boolean getCandidatePathVisibility() {
        return this.isCandidatePathVisible;
    }

    public void setCandidatePeripheralVisibility(boolean isVisible) {
        this.isCandidatePeripheralVisible = isVisible;
    }

    public boolean getCandidatePeripheralVisibility() {
        return this.isCandidatePeripheralVisible;
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


    private void drawCandidatePeripheralImage(Surface surface) {

        if (isCandidatePeripheralVisible) {

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
