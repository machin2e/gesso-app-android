package camp.computer.clay.scene.figure;

import android.graphics.Canvas;
import android.graphics.Paint;

import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.scene.architecture.Figure;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;

public class PathFigure extends Figure<Path> {

    // </STYLE>
    public boolean showDocks = true;
    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;
    // </STYLE>

    public PathFigure(Path path) {
        super(path);
        setup();
    }

    private void setup() {
        setupActions();
    }

    private void setupActions() {
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.TOUCH) {

                } else if (action.getType() == Action.Type.HOLD) {

                } else if (action.getType() == Action.Type.MOVE) {

                } else if (action.getType() == Action.Type.RELEASE) {

                }
            }
        });
    }

    public void update() {
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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

    // TODO: Delete
    public Path getPath() {
        return getConstruct();
    }

    public void drawTrianglePath(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        Path path = getPath();

        PortFigure sourcePortImage = (PortFigure) getScene().getFigure(path.getSource());
        PortFigure targetPortImage = (PortFigure) getScene().getFigure(path.getTarget());

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

        PortFigure sourcePortImage = (PortFigure) getScene().getFigure(path.getSource());
        PortFigure targetPortImage = (PortFigure) getScene().getFigure(path.getTarget());

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

//        if (showDocks) {
//
//            Point pathStartPosition = Geometry.calculatePoint(
//                    sourcePortImage.getPosition(),
//                    pathRotationAngle,
//                    1.4 * triangleSpacing
//            );
//
//            Point pathStopPosition = Geometry.calculatePoint(
//                    targetPortImage.getPosition(),
//                    pathRotationAngle + 180,
//                    1.4 * triangleSpacing
//            );
//
//            paint.setStyle(Paint.Style.FILL);
////            Surface.drawTriangle(
////                    pathStartPosition,
////                    triangleRotationAngle,
////                    triangleWidth,
////                    triangleHeight,
////                    surface
////            );
//            Surface.drawRectangle(
//                    pathStartPosition,
//                    triangleRotationAngle,
//                    triangleWidth,
//                    triangleWidth,
//                    surface
//            );
//
//            paint.setStyle(Paint.Style.FILL);
////            Surface.drawTriangle(
////                    pathStopPosition,
////                    triangleRotationAngle,
////                    triangleWidth,
////                    triangleHeight,
////                    surface
////            );
//            Surface.drawRectangle(
//                    pathStopPosition,
//                    triangleRotationAngle,
//                    triangleWidth,
//                    triangleWidth,
//                    surface
//            );
//
//        } else {

            Point pathStartPosition = Geometry.calculatePoint(
                    sourcePortImage.getPosition(),
                    pathRotationAngle,
                    0
            );

            Point pathStopPosition = Geometry.calculatePoint(
                    targetPortImage.getPosition(),
                    pathRotationAngle + 180,
                    0
            );

            Surface.drawLine(
                    pathStartPosition,
                    pathStopPosition,
                    surface
            );
//        }

    }

    @Override
    public boolean containsPoint(Point point) {

//        if (isVisible()) {
//            Log.v("Touch_", "FLOOOO");
//            Path path = getPath();
//
//            PortFigure sourcePortImage = (PortFigure) getScene().getFigure(path.getSource());
//            PortFigure targetPortImage = (PortFigure) getScene().getFigure(path.getFigureByPosition());
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
}
