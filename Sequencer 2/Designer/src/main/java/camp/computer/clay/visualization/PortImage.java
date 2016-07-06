package camp.computer.clay.visualization;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.simulation.Machine;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Shape;

public class PortImage extends Image {

    // --- STYLE ---
    public static float DISTANCE_FROM_BOARD = 45.0f;
    public static float DISTANCE_BETWEEN_NODES = 10.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    private boolean showFormLayer = true;
    private boolean showStyleLayer = true;
    private boolean showDataLayer = true;
    private boolean showAnnotationLayer = false;

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

    public ArrayList<PathImage> pathImages = new ArrayList<PathImage>();

    public PortImage(Port port) {
        super(port);
        initialize();
    }

    private void initialize() {
        this.uniqueColor = updateUniqueColor();
        initializeData();
        setVisibility(false);
    }

    private void initializeData () {
        for (int i = 0; i < this.portDataSamples.length; i++) {
            this.portDataSamples[i] = -(this.shapeRadius / 2.0f) + 0;
        }
    }

    public MachineImage getMachineImage() {
        return (MachineImage) getParentImage();
    }

//    public void setPosition(PointF position) {
//        this.setPosition(
//                position.x,
//                position.y);
//    }

    // TODO: Move into Port
    public PathImage addPath(Port sourcePort, Port destinationPort) {


        // TODO: Create Path model, then access that model. Don't store the sprites. Look those up in the visualization.
        Path path = new Path(sourcePort, destinationPort);

        PathImage pathImage = new PathImage(path);
        pathImage.setParentImage(this);
        pathImage.setVisualization(getVisualization());
        getVisualization().getLayer(0).addImage(path, pathImage);

        PortImage destinationPortImage = (PortImage) getVisualization().getLayer(0).getImage(path.getDestination());
        destinationPortImage.setUniqueColor(this.uniqueColor);
        this.pathImages.add(pathImage);
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
        for (PathImage pathImage : pathImages) {
            pathImage.showPathDocks = false;

            // Deep
            PortImage destinationPortImage = (PortImage) getVisualization().getLayer(0).getImage(pathImage.getPath().getDestination());
            destinationPortImage.showPaths();
        }
    }

    // TODO: showIncomingPath
    // TODO: showOutgoingPath

    public void showPathDocks() {
        for (PathImage pathImage : pathImages) {
            pathImage.showPathDocks = true;

            // Deep
            PortImage destinationPortImage = (PortImage) getVisualization().getLayer(0).getImage(pathImage.getPath().getDestination());
            destinationPortImage.showPathDocks();
        }
    }

    public void draw(MapView mapView) {
        this.mapView = mapView;

        if (isVisible()) {

            drawShapeLayer(mapView);
            drawStyleLayer(mapView);
            drawDataLayer(mapView);
            drawAnnotationLayer(mapView);

            // Draw children sprites
            drawPathImages(mapView);
            drawCandidatePathImages(mapView);
        }
    }

    private void drawPathImages(MapView mapView) {
        for (int i = 0; i < this.pathImages.size(); i++) {
            PathImage pathImage = this.pathImages.get(i);
            pathImage.draw(mapView);
        }
    }


    /**
     * Draws the shape of the sprite filled with a solid color. Graphically, this represents a
     * placeholder for the sprite.
     * @param mapView
     */
    public void drawShapeLayer(MapView mapView) {

        if (showFormLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

//            mapCanvas.save();
//
//            mapCanvas.translate(this.getPosition().x, this.getPosition().y);

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(PortImage.FLOW_PATH_COLOR_NONE);
            mapCanvas.drawCircle(
                    getPosition().x,
                    getPosition().y,
                    shapeRadius,
                    paint
            );

            // Outline
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);
                mapCanvas.drawCircle(
                        getPosition().x,
                        getPosition().y,
                        shapeRadius,
                        paint
                );
            }

//            mapCanvas.restore();
        }
    }

    /**
     * Draws the sprite's detail front layer.
     * @param mapView
     */
    public void drawStyleLayer(MapView mapView) {

        if (showStyleLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.Type.NONE) {

                mapCanvas.save();

//                mapCanvas.translate(this.getPosition().x, this.getPosition().y);

                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(this.uniqueColor); // [3 * i + j]);
                mapCanvas.drawCircle(
                        this.getPosition().x,
                        this.getPosition().y,
                        shapeRadius,
                        paint
                );

                // Outline
                if (showShapeOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    mapCanvas.drawCircle(
                            this.getPosition().x,
                            this.getPosition().y,
                            shapeRadius,
                            paint
                    );
                }

                mapCanvas.restore();
            }
        }
    }

    public Port getPort() {
        Port port = (Port) getModel();
        return port;
    }

    /**
     * Draws the sprite's data layer.
     * @param mapView
     */
    private void drawDataLayer(MapView mapView) {

        if (showDataLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

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
     * @param mapView
     */
    public void drawAnnotationLayer(MapView mapView) {

        if (showAnnotationLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.Type.NONE) {

                mapCanvas.save();

                mapCanvas.translate(this.getPosition().x, this.getPosition().y);

                /*
                // Label
                if (showChannelLabel) {
                    paint.setTextSize(labelTextSize);
                    Rect textBounds = new Rect();
                    String channelNumberText = String.valueOf(3 * i + j + 1);
                    paint.getTextBounds(channelNumberText, 0, channelNumberText.length(), textBounds);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    mapCanvas.drawText(channelNumberText, -(textBounds.width() / 2.0f), textBounds.height() / 2.0f, paint);
                }
                */

                mapCanvas.restore();
            }
        }
    }

    public void update() {
        if (this.isVisible()) {
            updatePosition();
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
        MachineImage machineImage = (MachineImage) getParentImage();
        Machine machine = (Machine) machineImage.getModel();
        // </TODO>

        // Ports
        float portRadius = 40.0f;
        PointF[] relativePortPositions = new PointF[machine.getPorts().size()];
        relativePortPositions[0] = new PointF(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((machineImage.boardWidth/ 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[1] = new PointF(
                0,
                +1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[2] = new PointF(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                +1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[3] = new PointF(
                +1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[4] = new PointF(
                +1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[5] = new PointF(
                +1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[6] = new PointF(
                +1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[7] = new PointF(
                0,
                -1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[8] = new PointF(
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES),
                -1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[9] = new PointF(
                -1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortImage.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[10] = new PointF(
                -1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[11] = new PointF(
                -1 * ((machineImage.boardWidth / 2.0f) + PortImage.DISTANCE_FROM_BOARD + portRadius),
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
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public void setPathVisibility (boolean isVisible) {
        for (PathImage pathImage : this.pathImages) {
            pathImage.setVisibility(isVisible);

            // Deep
            PortImage destinationPortImage = (PortImage) getVisualization().getLayer(0).getImage(pathImage.getPath().getDestination());
            destinationPortImage.setVisibility(isVisible);
        }
    }

    public boolean hasVisiblePaths () {
        for (PathImage pathImage: this.pathImages) {
            if (pathImage.isVisible() && !pathImage.showPathDocks) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleAncestorPaths() {
        ArrayList<Path> ancestorPaths = getVisualization().getSimulation().getAncestorPathsByPort(getPort());
        for (Path ancestorPath: ancestorPaths) {
            PathImage pathImage = (PathImage) getVisualization().getLayer(0).getImage(ancestorPath);
            if (pathImage.isVisible() && !pathImage.showPathDocks) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PathImage> getVisiblePaths() {
        ArrayList<PathImage> visiblePathImages = new ArrayList<PathImage>();
        for (PathImage pathImage: pathImages) {
            if (pathImage.isVisible()) {
                visiblePathImages.add(pathImage);
            }
        }
        return visiblePathImages;
    }

    public MapView mapView = null;

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

    public static final String CLASS_NAME = "PORT_SPRITE";

    @Override
    public void onTouchAction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.NONE) {
            Log.v("onTouchAction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TOUCH) {
            Log.v("onTouchAction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TAP) {
            Log.v("onTouchAction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DOUBLE_DAP) {
            Log.v("onTouchAction", "TouchInteraction.DOUBLE_TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.HOLD) {
            Log.v("onTouchAction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.MOVE) {
            Log.v("onTouchAction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.PRE_DRAG) {
            Log.v("onTouchAction", "TouchInteraction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DRAG) {
            Log.v("onTouchAction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.RELEASE) {
            Log.v("onTouchAction", "TouchInteraction.RELEASE to " + CLASS_NAME);

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

    public void drawCandidatePathImages(MapView mapView) {
        if (isCandidatePathVisible) {

            Port port = (Port) getModel();

            if (port.getType() != Port.Type.NONE) {

                Canvas mapCanvas = mapView.getCanvas();
                Paint paint = mapView.getPaint();

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
