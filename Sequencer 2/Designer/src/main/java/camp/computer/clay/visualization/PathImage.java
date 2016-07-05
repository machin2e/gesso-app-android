package camp.computer.clay.visualization;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Shape;

public class PathImage extends Image {

    // TODO: private Channel channel;

    // --- STYLE ---
    public static float DISTANCE_FROM_BOARD = 45.0f;
    public static float DISTANCE_BETWEEN_NODES = 5.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    public boolean showFormLayer = false;
    public boolean showStyleLayer = true;
    public boolean showDataLayer = true;
    private boolean showAnnotationLayer = false;

    public float shapeRadius = 40.0f;
    boolean showShapeOutline = false;

    boolean showChannelLabel = false;
    float labelTextSize = 30.0f;
    // ^^^ STYLE ^^^

    // TODO: Transfer data from in port, run it through the spreadsheet, and transfer it to the out port
//    // --- DATA ---
//    public int dataSampleCount = 40;
//    public float[] dataSamples = new float[dataSampleCount];
//    // ^^^ DATA ^^^

    // --- STYLE ---

    private boolean isVisible = false;
    public boolean showLinePaths = false;
    public boolean showDirectedPaths = true;
    public boolean showPathDocks = true;
    private boolean isEditorVisible = false;
    float pathTerminalLength = 100.0f;
    float triangleWidth = 20;
    float triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
    float triangleSpacing = 35;

    // ^^^ STYLE ^^^

    public PathImage(Path path) {
        super(path);

        initialize();
    }

    private void initialize() {
        initializePathDirections();
        initializePathTypes();
    }

    private void initializePathTypes() {
        getPath().setType(Path.Type.NONE);
    }

    private void initializePathDirections() {
        getPath().setDirection(Path.Direction.NONE);
    }

    public void update() {
    }

    public void draw(MapView mapView) {

        if (isVisible()) {
            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

            if (this.showDirectedPaths) {
                drawTrianglePath(mapCanvas, paint);
            } else {
                drawLinePath(mapCanvas, paint);
            }
        }
    }

    public Path getPath() {
        return (Path) getModel();
    }

    //-------

    private void drawLinePath (Canvas mapCanvas, Paint paint) {

        if (showLinePaths) {

            Path path = (Path) getModel();

            PortImage sourcePortImage = (PortImage) getVisualization().getLayer(0).getImage(path.getSource());
            PortImage destinationPortImage = (PortImage) getVisualization().getLayer(0).getImage(path.getDestination());

            // Show destination port
            destinationPortImage.setVisibility(true);
            destinationPortImage.setPathVisibility(true);

            mapCanvas.save();

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(sourcePortImage.getUniqueColor());

            mapCanvas.drawLine(
                    sourcePortImage.getPosition().x,
                    sourcePortImage.getPosition().y,
                    destinationPortImage.getPosition().x,
                    destinationPortImage.getPosition().y,
                    paint
            );

            mapCanvas.restore();
        }
    }

    float startK = 0.0f;

    public void drawTrianglePath(Canvas mapCanvas, Paint paint) {

        Path path = (Path) getModel();

        PortImage sourcePortImage = (PortImage) getVisualization().getLayer(0).getImage(path.getSource());
        PortImage destinationPortImage = (PortImage) getVisualization().getLayer(0).getImage(path.getDestination());

        // Show destination port
        destinationPortImage.setVisibility(true);
        destinationPortImage.setPathVisibility(true);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(sourcePortImage.getUniqueColor());

        if (showDirectedPaths) {
            float pathRotationAngle = Geometry.calculateRotationAngle(
                    sourcePortImage.getPosition(),
                    destinationPortImage.getPosition()
            );

            float triangleRotationAngle = pathRotationAngle + 90.0f;

            PointF triangleCenterPosition = Geometry.calculatePoint(
                    sourcePortImage.getPosition(),
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            PointF triangleCenterPositionDestination = Geometry.calculatePoint(
                    destinationPortImage.getPosition(),
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            if (showPathDocks) {

                float pathDistance = (float) Geometry.calculateDistance(
                        sourcePortImage.getPosition(),
                        destinationPortImage.getPosition()
                );

                paint.setStyle(Paint.Style.FILL);
                Shape.drawTriangle(
                        triangleCenterPosition,
                        triangleRotationAngle,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

                paint.setStyle(Paint.Style.FILL);
                Shape.drawTriangle(
                        triangleCenterPositionDestination,
                        triangleRotationAngle,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

            } else {

                float pathDistance = (float) Geometry.calculateDistance(
                        triangleCenterPosition,
                        triangleCenterPositionDestination
                );

                int triangleCount = (int) (pathDistance / (triangleHeight + 15));
                float triangleSpacing2 = pathDistance / triangleCount;

                PointF pathMidpoint = Geometry.calculateMidpoint(
                        sourcePortImage.getPosition(),
                        destinationPortImage.getPosition()
                );

                for (int k = 0; k <= triangleCount; k++) {

                    // Calculate triangle position
                    PointF triangleCenterPosition2 = Geometry.calculatePoint(
                            triangleCenterPosition,
                            pathRotationAngle,
                            triangleSpacing2 * k // k * triangleSpacing
                    );

                    /*
                    // Offset position based on port type
                    triangleCenterPosition = Geometry.calculatePoint(
                            triangleCenterPosition,
                            pathRotationAngle + (-90 + (k % 2) * 180),
                            15
                    );
                    */

                    /*
                    // Offset position based on port type
                    triangleCenterPosition = Geometry.calculatePoint(
                            triangleCenterPosition,
                            pathRotationAngle + (-90 + (k % 2) * 180),
                            15 * (float) Math.sin((((startK + k) * triangleSpacing) / pathDistance) * (2 * Math.PI))
                    );
                    startK = startK + 0.01f;
                    */

//                    // Stop drawing if the entire path has been drawn
//                    if (k * triangleSpacing > pathDistance) {
//                        break;
//                    }

//                    if ((k * triangleSpacing) >= (2 * triangleSpacing)
//                            && (k * triangleSpacing) <= (pathDistance - 2 * triangleSpacing)) {

                        paint.setStyle(Paint.Style.FILL);
                        Shape.drawTriangle(
                                triangleCenterPosition2,
                                triangleRotationAngle,
                                triangleWidth,
                                triangleHeight,
                                mapCanvas,
                                paint
                        );
//                    }
                }

                if (isEditorVisible) {
                    // <SPREADSHEET>
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(sourcePortImage.getUniqueColor());
                    float spreadsheetImageWidth = 50.0f;
                    mapCanvas.drawRect(
                            pathMidpoint.x + -(spreadsheetImageWidth / 2.0f),
                            pathMidpoint.y + -(spreadsheetImageWidth / 2.0f),
                            pathMidpoint.x + (spreadsheetImageWidth / 2.0f),
                            pathMidpoint.y + (spreadsheetImageWidth / 2.0f),
                            paint
                    );
                    // </SPREADSHEET>
                }
            }
        }
    }

    public void setVisibility(boolean isVisible) {

        // Hide the path editor by default
        if (this.isVisible == false) {
            setEditorVisibility(false);
        }

        this.isVisible = isVisible;
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setEditorVisibility(boolean isVisible) {
        this.isEditorVisible = isVisible;
    }

    public boolean getEditorVisibility() {
        return this.isEditorVisible;
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    public static final String CLASS_NAME = "PATH_SPRITE";

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
        }
    }
}
