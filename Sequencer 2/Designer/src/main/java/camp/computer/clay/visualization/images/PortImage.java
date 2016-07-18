package camp.computer.clay.visualization.images;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Base;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.arch.Image;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.PointHolder;
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

    private double shapeRadius = 40.0f;
    private boolean showShapeOutline = false;

    private int uniqueColor = Color.BLACK;

    public int getIndex() {
        return getBaseImage().getPortImageIndex(this);
    }
    // </STYLE>

    // <DATA>
    private int dataSampleCount = 40;
    private double[] portDataSamples = new double[dataSampleCount];
    // </DATA>

    public static final String CLASS_NAME = "PORT_SPRITE";

    private boolean isCandidatePathVisible = false;
    private PointHolder candidatePathDestinationPosition = new PointHolder(40, 80);

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

    public BaseImage getBaseImage() {
        return (BaseImage) getParentImage();
    }

    public ArrayList<PathImage> getPathImages() {
        ArrayList<PathImage> pathImages = new ArrayList<>();
        for (Path path: getPort().getPaths()) {
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
        for (PathImage pathImage: getPathImages()) {
            pathImage.showDocks = false;

            // Deep
            PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
            targetPortImage.showPaths();
        }
    }

    public void showDocks() {
        for (PathImage pathImage: getPathImages()) {
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
     * @param visualizationSurface
     */
    private void drawData(VisualizationSurface visualizationSurface) {

        if (showDataLayer) {

            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.Type.NONE) {

                // Outline
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.0f);
                paint.setColor(Color.WHITE);

                double plotStep = (float) ((2.0f * (float) shapeRadius) / (float) portDataSamples.length);

                PointHolder[] rotatedPortDataSamplePoints = new PointHolder[portDataSamples.length];

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

                    PointHolder samplePoint = null;

                    if (port.getDirection() == Port.Direction.INPUT) {
                        // Set position before rotation adjustment
                        samplePoint = new PointHolder(
                                this.getPosition().getX() + portDataSamples[k],
                                this.getPosition().getY() + -shapeRadius + k * plotStep
                        );
                    } else if (port.getDirection() == Port.Direction.OUTPUT) {
                        samplePoint = new PointHolder(
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
                        Shape.drawLine(rotatedPortDataSamplePoints[k], rotatedPortDataSamplePoints[k + 1], canvas, paint);
                    }
                } else if (port.getDirection() == Port.Direction.OUTPUT) {
                    for (int k = 0; k < portDataSamples.length - 1; k++) {
                        Shape.drawLine(rotatedPortDataSamplePoints[k], rotatedPortDataSamplePoints[k + 1], canvas, paint);
                    }
                }
            }
        }
    }

    /**
     * Draws the sprite's annotation layer. Contains labels and other text.
     * @param visualizationSurface
     */
    public void drawAnnotation(VisualizationSurface visualizationSurface) {

        if (showAnnotationLayer) {

            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            // Geometry
            PointHolder labelPosition = new PointHolder();
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

            // Draw
            Shape.drawText(labelPosition, typeLabelText, typeLabelTextSize, canvas, paint);
        }
    }

    public void update() {
        if (this.isVisible()) {
            if (!isTouched) {
                updatePosition();
            }
            updateData();
        }
    }

    private int previousSwitchState = 0;
    private double switchPeriod = 20.0f;
    private int switchHalfPeriodSampleCount = 0;
    private double pulsePeriod = 20.0f;
    private double pulseDutyCycle = 0.5f;
    private int pulsePeriodSampleCount = 0;
    private int previousPulseState = 0;
    private double xWaveStart = 0;
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
        BaseImage baseImage = (BaseImage) getParentImage();
        Base base = baseImage.getBase();
        // </TODO>

        // Ports
        double portRadius = 40.0f;
        PointHolder[] relativePortPositions = new PointHolder[base.getPorts().size()];
        relativePortPositions[0] = new PointHolder(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((baseImage.boardWidth/ 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[1] = new PointHolder(
                0,
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[2] = new PointHolder(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[3] = new PointHolder(
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[4] = new PointHolder(
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[5] = new PointHolder(
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[6] = new PointHolder(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[7] = new PointHolder(
                0,
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[8] = new PointHolder(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[9] = new PointHolder(
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[10] = new PointHolder(
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[11] = new PointHolder(
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );

        relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                new PointHolder(0, 0), //getParentImage().getPosition(),
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

    public void setPathVisibility (boolean isVisible) {
        for (PathImage pathImage: getPathImages()) {
            pathImage.setVisibility(isVisible);

            // Deep
            PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
            targetPortImage.setVisibility(isVisible);
        }
    }

    public boolean hasVisiblePaths () {
        for (PathImage pathImage: getPathImages()) {
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths() {
        ArrayList<Path> ancestorPaths = getVisualization().getSimulation().getAncestorPathsByPort(getPort());
        for (Path ancestorPath: ancestorPaths) {
            PathImage pathImage = (PathImage) getVisualization().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showDocks) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PathImage> getVisiblePaths() {
        ArrayList<PathImage> visiblePathImages = new ArrayList<PathImage>();
        for (PathImage pathImage: getPathImages()) {
            if (pathImage.isVisible()) {
                visiblePathImages.add(pathImage);
            }
        }
        return visiblePathImages;
    }

    @Override
    public boolean isTouching(PointHolder point) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES));
        } else {
            return false;
        }
    }

    public boolean isTouching (PointHolder point, double padding) {
        if (isVisible()) {
            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + padding));
        } else {
            return false;
        }
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.Type.NONE) {
            Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TOUCH) {
            Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TAP) {
            Log.v("onTouchInteraction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.HOLD) {
            Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.MOVE) {
            Log.v("onTouchInteraction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.PRE_DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.RELEASE) {
            Log.v("onTouchInteraction", "TouchInteraction.RELEASE to " + CLASS_NAME);

            setCandidatePathVisibility(false);
        }
    }

    public void setCandidatePathVisibility(boolean isVisible) {
        this.isCandidatePathVisible = isVisible;
    }

    public boolean getCandidatePathVisibility() {
        return this.isCandidatePathVisible;
    }

    public void setCandidatePathDestinationPosition(PointHolder position) {
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

                PointHolder pathStartPosition = Geometry.calculatePoint(
                        getPosition(),
                        pathRotationAngle,
                        2 * triangleSpacing
                );

                PointHolder pathStopPosition = Geometry.calculatePoint(
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
