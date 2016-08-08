package camp.computer.clay.visualization.image;

import android.graphics.Canvas;
import android.graphics.Paint;

import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Shape;

public class PathImage extends Image {

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

    public void draw(Surface surface) {

        if (isVisible()) {
            // Draw path between ports with style dependant on path type
            Path path = getPath();
            if (path.getType() == Path.Type.MESH) {
                drawTrianglePath(surface);
            } else if (path.getType() == Path.Type.ELECTRONIC) {
                drawLinePath(surface);
            }
        }
    }

    public Path getPath() {
        return (Path) getModel();
    }

    public void drawTrianglePath(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        Path path = getPath();

        PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
        PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getTarget());

        // Show target port
        targetPortImage.setVisibility(Visibility.VISIBLE);
        targetPortImage.setPathVisibility(Visibility.VISIBLE);

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
            Surface.drawTriangle(
                    pathStartPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    surface
            );

            paint.setStyle(Paint.Style.FILL);
            Surface.drawTriangle(
                    pathStopPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    surface
            );

        } else {

            Surface.drawTrianglePath(
                    pathStartPosition,
                    pathStopPosition,
                    triangleWidth,
                    triangleHeight,
                    surface
            );
        }
    }

    private void drawLinePath(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        Path path = getPath();

        PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
        PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getTarget());

        // Show target port
        targetPortImage.setVisibility(Visibility.VISIBLE);
        targetPortImage.setPathVisibility(Visibility.VISIBLE);

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
            Surface.drawTriangle(
                    pathStartPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    surface
            );

            paint.setStyle(Paint.Style.FILL);
            Surface.drawTriangle(
                    pathStopPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    surface
            );

        } else {

            Surface.drawLine(
                    pathStartPosition,
                    pathStopPosition,
                    surface
            );
        }

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
    public void onAction(Action action) {

        if (action.getType() == Action.Type.NONE) {
            // Log.v("onAction", "Action.NONE to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.TOUCH) {
            // Log.v("onAction", "Action.TOUCH to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.TAP) {
            // Log.v("onAction", "Action.TAP to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.HOLD) {
            // Log.v("onAction", "Action.HOLD to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.MOVE) {
            // Log.v("onAction", "Action.MOVE to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.DRAG) {
            // Log.v("onAction", "Action.DRAG to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.RELEASE) {
            // Log.v("onAction", "Action.RELEASE to " + CLASS_NAME);
        }
    }
}
