package camp.computer.clay.visualization.image;

import android.graphics.Canvas;
import android.graphics.Paint;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.interactivity.Impression;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Point;
import camp.computer.clay.visualization.util.Shape;

public class PathImage extends Image {

    public final static String TYPE = "path";

    // </SETTINGS>
    private boolean showFormLayer = false;
    private boolean showStyleLayer = true;
    private boolean showDataLayer = true;
    private boolean showAnnotationLayer = false;
    // </SETTINGS>

    // </STYLE>
    private boolean isVisible = false;
    public boolean showDocks = true;
    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * ((double) Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;
    // </STYLE>

    public PathImage(Path path) {
        super(path);
        setup();
    }

    private void setup() {
    }

    public void update() {
    }

    public void draw(VisualizationSurface visualizationSurface) {

        if (isVisible()) {
            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            // Draw path between ports with style dependant on path type
            Path path = getPath();
            if (path.getType() == Path.Type.MESH) {
                drawTrianglePath(canvas, paint);
            } else if (path.getType() == Path.Type.ELECTRONIC) {
                drawLinePath(canvas, paint);
            }
        }
    }

    public Path getPath() {
        return (Path) getModel();
    }

    public void drawTrianglePath(Canvas canvas, Paint paint) {

        Path path = getPath();

        PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
        PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getTarget());

        // Show target port
        targetPortImage.setVisibility(true);
        targetPortImage.setPathVisibility(true);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(sourcePortImage.getUniqueColor());

        double pathRotationAngle = Geometry.calculateRotationAngle(
                sourcePortImage.getPosition(),
                targetPortImage.getPosition()
        );

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        Point pathStartPosition = Geometry.calculatePoint(
                sourcePortImage.getPosition(),
                pathRotationAngle,
                2 * triangleSpacing
        );

        Point pathStopPosition = Geometry.calculatePoint(
                targetPortImage.getPosition(),
                pathRotationAngle + 180,
                2 * triangleSpacing
        );

        if (showDocks) {

            paint.setStyle(Paint.Style.FILL);
            Shape.drawTriangle(
                    pathStartPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    canvas,
                    paint
            );

            paint.setStyle(Paint.Style.FILL);
            Shape.drawTriangle(
                    pathStopPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    canvas,
                    paint
            );

        } else {

            Shape.drawTrianglePath(
                    pathStartPosition,
                    pathStopPosition,
                    triangleWidth,
                    triangleHeight,
                    canvas,
                    paint
            );
        }
    }

    private void drawLinePath(Canvas canvas, Paint paint) {

        Path path = getPath();

        PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
        PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getTarget());

        // Show target port
        targetPortImage.setVisibility(true);
        targetPortImage.setPathVisibility(true);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(sourcePortImage.getUniqueColor());

        double pathRotationAngle = Geometry.calculateRotationAngle(
                sourcePortImage.getPosition(),
                targetPortImage.getPosition()
        );

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        Point pathStartPosition = Geometry.calculatePoint(
                sourcePortImage.getPosition(),
                pathRotationAngle,
                2 * triangleSpacing
        );

        Point pathStopPosition = Geometry.calculatePoint(
                targetPortImage.getPosition(),
                pathRotationAngle + 180,
                2 * triangleSpacing
        );

        if (showDocks) {

            paint.setStyle(Paint.Style.FILL);
            Shape.drawTriangle(
                    pathStartPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    canvas,
                    paint
            );

            paint.setStyle(Paint.Style.FILL);
            Shape.drawTriangle(
                    pathStopPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    canvas,
                    paint
            );

        } else {

            Shape.drawLine(
                    pathStartPosition,
                    pathStopPosition,
                    canvas,
                    paint
            );
        }

    }

    public void setVisibility(boolean isVisible) {

        this.isVisible = isVisible;
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public boolean containsPoint(Point point) {

//        if (isVisible()) {
//            Log.v("Touch_", "FLOOOO");
//            Path path = getPath();
//
//            PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
//            PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getImageByPosition());
//
//            double distanceToLine = Geometry.calculateLineToPointDistance(
//                    sourcePortImage.getPosition(),
//                    targetPortImage.getPosition(),
//                    point,
//                    true
//            );
//
//            if (distanceToLine < 60) {
//                return true;
//            } else {
//                return false;
//            }
//        }
        return false;
    }

    public boolean containsPoint(Point point, double padding) {
        return false;
    }

    @Override
    public void onImpression(Impression impression) {

        if (impression.getType() == Impression.Type.NONE) {
            // Log.v("onImpression", "Impression.NONE to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.TOUCH) {
            // Log.v("onImpression", "Impression.TOUCH to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.TAP) {
            // Log.v("onImpression", "Impression.TAP to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.HOLD) {
            // Log.v("onImpression", "Impression.HOLD to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.MOVE) {
            // Log.v("onImpression", "Impression.MOVE to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.DRAG) {
            // Log.v("onImpression", "Impression.DRAG to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.RELEASE) {
            // Log.v("onImpression", "Impression.RELEASE to " + CLASS_NAME);
        }
    }
}
