package camp.computer.clay.visualization.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.architecture.Frame;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Device;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Impression;
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
        if (getParentImage() instanceof FrameImage) {
            return getFrameImage().getPortImageIndex(this);
        } else if (getParentImage() instanceof DeviceImage) {
            return ((DeviceImage) getParentImage()).getPortImageIndex(this);
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

    public static final String CLASS_NAME = "PORT_SPRITE";

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

    public void draw(VisualizationSurface visualizationSurface) {
        if (isVisible()) {

            // Port
            drawShape(visualizationSurface);
            drawStyle(visualizationSurface);
            drawData(visualizationSurface);
            drawAnnotation(visualizationSurface);

            // Candidate Path
            drawCandidatePathImages(visualizationSurface);

            // Candidate Device
            drawCandidatePeripheralImage(visualizationSurface);
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

            if (getPort().getType() != Port.Type.NONE) {

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

                // Draw
                Shape.drawText(labelPosition, getPort().getType().getTag(), typeLabelTextSize, canvas, paint);

            }
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
        } else if (port.getType() == Port.Type.POWER_COMMON) {
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

            // Ports
            double portRadius = 40.0f;
            Point[] relativePortPositions = new Point[frame.getPorts().size()];
            relativePortPositions[0] = new Point(
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[1] = new Point(
                    0,
                    +1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[2] = new Point(
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[3] = new Point(
                    +1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );
            relativePortPositions[4] = new Point(
                    +1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    0
            );
            relativePortPositions[5] = new Point(
                    +1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );
            relativePortPositions[6] = new Point(
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    -1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[7] = new Point(
                    0,
                    -1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[8] = new Point(
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    -1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[9] = new Point(
                    -1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );
            relativePortPositions[10] = new Point(
                    -1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    0
            );
            relativePortPositions[11] = new Point(
                    -1 * ((frameImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
            );

            relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                    new Point(0, 0), //getParentImage().getPosition(),
                    getParentImage().getAbsoluteRotation(), //  + (((rot - 1) * 90) - 90) + ((rot - 1) * 90),
                    relativePortPositions[getIndex()]
            );

            setRelativePosition(relativePortPositions[getIndex()]);

        } else if (getParentImage() instanceof DeviceImage) {

            DeviceImage deviceImage = (DeviceImage) getParentImage();
            Device device = deviceImage.getPeripheral();
            // </TODO>

            // Ports
            double portRadius = 40.0f;
            Point[] relativePortPositions = new Point[device.getPorts().size()];
            relativePortPositions[0] = new Point(
                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[1] = new Point(
                    0,
                    +1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
            relativePortPositions[2] = new Point(
                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                    +1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
            );
//            relativePortPositions[3] = new Point(
//                    +1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
//            );
//            relativePortPositions[4] = new Point(
//                    +1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    0
//            );
//            relativePortPositions[5] = new Point(
//                    +1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
//            );
//            relativePortPositions[6] = new Point(
//                    +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
//                    -1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
//            );
//            relativePortPositions[7] = new Point(
//                    0,
//                    -1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
//            );
//            relativePortPositions[8] = new Point(
//                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
//                    -1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
//            );
//            relativePortPositions[9] = new Point(
//                    -1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
//            );
//            relativePortPositions[10] = new Point(
//                    -1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
//                    0
//            );
//            relativePortPositions[11] = new Point(
//                    -1 * ((deviceImage.getShape().getWidth() / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
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
    public boolean contains(Point point) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES));
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

    @Override
    public void onInteraction(Impression impression) {

        if (impression.getType() == Impression.Type.NONE) {
            // Log.v("onInteraction", "Impression.NONE to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.TOUCH) {
            // Log.v("onInteraction", "Impression.TOUCH to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.TAP) {

            Port port = getPort();

            if (port.getType() == Port.Type.NONE) {

                port.setDirection(Port.Direction.INPUT);
                port.setType(Port.Type.next(port.getType()));

                // TODO: Speak ~ "setting as input. you can send the data to another board if you want. touchPositions another board."

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
                getFrameImage().setTransparency(0.1);

                // Focus on the port
                //portImage.getFrameImage().showPathImage(portImage.getIndex(), true);
                showPaths();
                setVisibility(true);
                setPathVisibility(true);

                List<Path> paths = port.getGraph();
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
                List<Port> pathPorts = port.getPorts(paths);
                List<Image> pathPortImages = getVisualization().getImages(pathPorts);
                List<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
                getVisualization().getSimulation().getBody(0).getPerspective().adjustScale(boundingBox);

                getVisualization().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

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

        } else if (impression.getType() == Impression.Type.MOVE) {
            // Log.v("onInteraction", "Impression.MOVE to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.DRAG) {

            Log.v("onHoldListener", "Port draggin!");

            // Candidate Path Visibility
            setCandidatePathDestinationPosition(impression.getPosition());
            setCandidatePathVisibility(true);

            // Candidate Device Visibility

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

        } else if (impression.getType() == Impression.Type.RELEASE) {
//             Log.v("onInteraction", "Impression.RELEASE to " + CLASS_NAME);
            Log.v("onHoldListener", "Impression.RELEASE to " + CLASS_NAME);

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


    private void drawCandidatePeripheralImage(VisualizationSurface visualizationSurface) {

        if (isCandidatePeripheralVisible) {

            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    getPosition(),
                    candidatePathDestinationPosition
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getUniqueColor());
            Shape.drawRectangle(candidatePathDestinationPosition, pathRotationAngle + 180, 250, 250, canvas, paint);

        }

    }
}
