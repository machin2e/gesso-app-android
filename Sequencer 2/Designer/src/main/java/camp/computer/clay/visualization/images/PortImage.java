package camp.computer.clay.visualization.images;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
import camp.computer.clay.visualization.util.Shape;

public class PortImage extends Image {

    public final static String TYPE = "port";

    // --- STYLE ---
    public static float DISTANCE_FROM_BOARD = 45.0f;
    public static float DISTANCE_BETWEEN_NODES = 15.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    private boolean showShapeLayer = true;
    private boolean showStyleLayer = true;
    private boolean showDataLayer = true;
    private boolean showAnnotationLayer = true;

    public float shapeRadius = 40.0f;
    private boolean showShapeOutline = false;

    private int uniqueColor = Color.BLACK;

    public int getIndex() {
        return getMachineImage().getPortImageIndex(this);
    }
    // ^^^ STYLE ^^^

    // --- DATA ---
    private int dataSampleCount = 40;
    private float[] portDataSamples = new float[dataSampleCount];
    // ^^^ DATA ^^^

    public PortImage(Port port) {
        super(port);

        this.setType(TYPE);

        setup();
    }

    private void setup() {
        this.uniqueColor = updateUniqueColor();
        setupData();
        setVisibility(false);
    }

    private void setupData() {
        for (int i = 0; i < this.portDataSamples.length; i++) {
            this.portDataSamples[i] = -(this.shapeRadius / 2.0f) + 0;
        }
    }

    public BaseImage getMachineImage() {
        return (BaseImage) getParentImage();
    }

    public ArrayList<PathImage> getPathImages() {
        ArrayList<PathImage> pathImages = new ArrayList<PathImage>();
        Port machine = getPort();

        for (Path path: getPort().getPaths()) {
            PathImage pathImage = (PathImage) getVisualization().getImage(path);
            pathImages.add(pathImage);
        }

        return pathImages;
    }

    // TODO: Move into Port
    public PathImage addPath(Port sourcePort, Port targetPort) {


        // TODO: Create Path model, then access that model. Don't store the sprites. Look those up in the visualization.
        Path path = new Path(sourcePort, targetPort);

        PathImage pathImage = new PathImage(path);
        pathImage.setVisualization(getVisualization());
//        getVisualization().getLayer(0).addImage(path, pathImage);
        getVisualization().addImage(path, pathImage, "paths");

        PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getTarget());
        targetPortImage.setUniqueColor(this.uniqueColor);
//        this.pathImages.add(pathImage);
        return pathImage;
    }

    public int getUniqueColor() {
        return this.uniqueColor;
    }

    public void setUniqueColor(int uniqueColor) {
        this.uniqueColor = uniqueColor;
    }

    public int updateUniqueColor() {
        this.uniqueColor = camp.computer.clay.visualization.util.Color.getUniqueColor(this);
        return this.uniqueColor;
    }

    public void showPaths() {
        for (PathImage pathImage: getPathImages()) {
            pathImage.showPathDocks = false;

            // Deep
            PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
            targetPortImage.showPaths();
        }
    }

    // TODO: showIncomingPath
    // TODO: showOutgoingPath

    public void showPathDocks() {
        for (PathImage pathImage: getPathImages()) {
            pathImage.showPathDocks = true;

            // Deep
            PortImage targetPortImage = (PortImage) getVisualization().getImage(pathImage.getPath().getTarget());
            targetPortImage.showPathDocks();
        }
    }

    public void draw(VisualizationSurface visualizationSurface) {
        this.visualizationSurface = visualizationSurface;

        if (isVisible()) {

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

            Canvas mapCanvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.Type.NONE) {

                // Outline
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.0f);
                paint.setColor(Color.WHITE);

                float plotStep = (float) ((2.0f * (float) shapeRadius) / (float) portDataSamples.length);

                PointF[] rotatedPortDataSamplePoints = new PointF[portDataSamples.length];

                float[] portGroupRotation = new float[] {
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

                    PointF samplePoint = null;

                    if (port.getDirection() == Port.Direction.INPUT) {
                        // Set position before rotation adjustment
                        samplePoint = new PointF(
                                this.getPosition().x + portDataSamples[k],
                                this.getPosition().y + -shapeRadius + k * plotStep
                        );
                    } else if (port.getDirection() == Port.Direction.OUTPUT) {
                        samplePoint = new PointF(
                                this.getPosition().x + portDataSamples[k],
                                this.getPosition().y + shapeRadius - k * plotStep
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
                        mapCanvas.drawLine(
                                rotatedPortDataSamplePoints[k].x,
                                rotatedPortDataSamplePoints[k].y,
                                rotatedPortDataSamplePoints[k + 1].x,
                                rotatedPortDataSamplePoints[k + 1].y,
                                paint
                        );
                    }
                } else if (port.getDirection() == Port.Direction.OUTPUT) {
                    for (int k = 0; k < portDataSamples.length - 1; k++) {
                        mapCanvas.drawLine(
                                rotatedPortDataSamplePoints[k].x,
                                rotatedPortDataSamplePoints[k].y,
                                rotatedPortDataSamplePoints[k + 1].x,
                                rotatedPortDataSamplePoints[k + 1].y,
                                paint
                        );
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
            PointF labelPosition = new PointF();
            labelPosition.set(
                    getPosition().x + shapeRadius + 25,
                    getPosition().y
            );

            // Style
            paint.setColor(this.uniqueColor);
            float typeLabelTextSize = 27;

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
    private float switchPeriod = 20.0f;
    private int switchHalfPeriodSampleCount = 0;
    private float pulsePeriod = 20.0f;
    private float pulseDutyCycle = 0.5f;
    private int pulsePeriodSampleCount = 0;
    private int previousPulseState = 0;
    private float xWaveStart = 0;
    private void updateData() {
        Random random = new Random();
        Port port = (Port) getModel();
        if (port.getType() == Port.Type.SWITCH) {
            // Shift data to make room for new samples
            for (int k = 0; k < portDataSamples.length - 1; k++) {
                portDataSamples[k] = portDataSamples[k + 1];
            }
            // Add new samples for the channel type
            float sample = getSyntheticSwitchSample();
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
            float sample = getSyntheticPulseSample();
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
        Base base = (Base) baseImage.getModel();
        // </TODO>

        // Ports
        float portRadius = 40.0f;
        PointF[] relativePortPositions = new PointF[base.getPorts().size()];
        relativePortPositions[0] = new PointF(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((baseImage.boardWidth/ 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[1] = new PointF(
                0,
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[2] = new PointF(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[3] = new PointF(
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[4] = new PointF(
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[5] = new PointF(
                +1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[6] = new PointF(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[7] = new PointF(
                0,
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[8] = new PointF(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[9] = new PointF(
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[10] = new PointF(
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[11] = new PointF(
                -1 * ((baseImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );

        relativePortPositions[getIndex()] = Geometry.calculateRotatedPoint(
                new PointF(0, 0), //getParentImage().getPosition(),
                getParentImage().getAbsoluteRotation(), //  + (((rot - 1) * 90) - 90) + ((rot - 1) * 90),
                relativePortPositions[getIndex()]
        );

        setRelativePosition(relativePortPositions[getIndex()]);
    }

    private float getSyntheticSwitchSample() {
        return -(shapeRadius / 2.0f) + previousSwitchState * shapeRadius;
    }

    private float getSyntheticPulseSample() {
        return -(shapeRadius / 2.0f) + previousPulseState * shapeRadius;
    }

    private float getSyntheticWaveSample(int x) {
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
            if (pathImage.isVisible() && !pathImage.showPathDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths() {
        ArrayList<Path> ancestorPaths = getVisualization().getSimulation().getAncestorPathsByPort(getPort());
        for (Path ancestorPath: ancestorPaths) {
            PathImage pathImage = (PathImage) getVisualization().getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showPathDocks) {
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

    public VisualizationSurface visualizationSurface = null;

    @Override
    public boolean isTouching(PointF point) {
        if (isVisible()) {

//            boolean touching = (Geometry.calculateDistance(point, this.getRelativePosition()) < (this.shapeRadius + 10));
//            if (touching) {
//                Matrix m = new Matrix();
//                mapView.canvasMatrix.invert(m);
//
//                float[] touch2 = new float[]{this.getPosition().x, this.getPosition().y};
//                m.mapPoints(touch2);
//
//                int X = (int) touch2[0];
//                int Y = (int) touch2[1];
//
//                Log.v("mtouch", "X: " + X + ", Y: " + Y);
//            }

            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES));
        } else {
            return false;
        }
    }

    public boolean isTouching (PointF point, float padding) {
        if (isVisible()) {

//            boolean touching = (Geometry.calculateDistance(point, this.getRelativePosition()) < (this.shapeRadius + 10));
//            if (touching) {
//                Matrix m = new Matrix();
//                mapView.canvasMatrix.invert(m);
//
//                float[] touch2 = new float[]{this.getPosition().x, this.getPosition().y};
//                m.mapPoints(touch2);
//
//                int X = (int) touch2[0];
//                int Y = (int) touch2[1];
//
//                Log.v("mtouch", "X: " + X + ", Y: " + Y);
//            }

            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + padding));
        } else {
            return false;
        }
    }

    public static final String CLASS_NAME = "PORT_SPRITE";

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.Type.NONE) {
            Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TOUCH) {
            Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TAP) {
            Log.v("onTouchInteraction", "TouchInteraction.DOUBLE_TAP to " + CLASS_NAME);
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

            this.setCandidatePathVisibility(false);
        }
    }

    private boolean isCandidatePathVisible = false;
    private PointF candidatePathDestinationPosition = new PointF(40, 80);

    public void setCandidatePathVisibility(boolean isVisible) {
        this.isCandidatePathVisible = isVisible;
    }

    public boolean getCandidatePathVisibility() {
        return this.isCandidatePathVisible;
    }

    public void setCandidatePathDestinationPosition(PointF position) {
        this.candidatePathDestinationPosition.x = position.x;
        this.candidatePathDestinationPosition.y = position.y;
    }

    public void drawCandidatePathImages(VisualizationSurface visualizationSurface) {
        if (isCandidatePathVisible) {

            Port port = (Port) getModel();

            if (port.getType() != Port.Type.NONE) {

                Canvas mapCanvas = visualizationSurface.getCanvas();
                Paint paint = visualizationSurface.getPaint();

                float triangleWidth = 20;
                float triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
                float triangleSpacing = 35;

                // Color
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(15.0f);
                paint.setColor(this.getUniqueColor());

                float pathRotationAngle = Geometry.calculateRotationAngle(
                        this.getPosition(),
                        candidatePathDestinationPosition
                );

                PointF pathStartPosition = Geometry.calculatePoint(
                        this.getPosition(),
                        pathRotationAngle,
                        2 * triangleSpacing
                );

                PointF pathStopPosition = Geometry.calculatePoint(
                        candidatePathDestinationPosition,
                        pathRotationAngle + 180,
                        2 * triangleSpacing
                );

                Shape.drawTrianglePath(
                        pathStartPosition,
                        pathStopPosition,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

                // Color
                float radius = this.shapeRadius;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(this.getUniqueColor());
                mapCanvas.drawCircle(
                        candidatePathDestinationPosition.x,
                        candidatePathDestinationPosition.y,
                        radius,
                        paint
                );
            }
        }
    }
}
