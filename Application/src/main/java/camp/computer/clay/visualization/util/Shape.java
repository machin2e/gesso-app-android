package camp.computer.clay.visualization.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public abstract class Shape {

    public static void drawTriangle(Point position, double angle, double width, double height, Canvas canvas, Paint paint) {

        // Calculate points before rotation
        Point p1 = new Point(position.getX() + -(width / 2.0f), position.getY() + (height / 2.0f));
        Point p2 = new Point(position.getX() + 0, position.getY() - (height / 2.0f));
        Point p3 = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));

        // Calculate points after rotation
        Point rp1 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p1), (double) Geometry.calculateDistance(position, p1));
        Point rp2 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p2), (double) Geometry.calculateDistance(position, p2));
        Point rp3 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p3), (double) Geometry.calculateDistance(position, p3));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rp1.getX(), (float) rp1.getY());
        path.lineTo((float) rp2.getX(), (float) rp2.getY());
        path.lineTo((float) rp3.getX(), (float) rp3.getY());
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawLine(Point source, Point target, Canvas canvas, Paint paint) {

        // Color
        canvas.drawLine(
                (float) source.getX(),
                (float) source.getY(),
                (float) target.getX(),
                (float) target.getY(),
                paint
        );

    }

    public static void drawCircle(Point position, double radius, double angle, Canvas canvas, Paint paint) {

        // Color
        canvas.drawCircle(
                (float) position.getX(),
                (float) position.getY(),
                (float) radius,
                paint
        );

    }

    public static void drawText(Point position, String text, double size, Canvas canvas, Paint paint) {

        // Style
        paint.setTextSize((float) size);

        // Style (Guaranteed)
        text = text.toUpperCase();
        paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, (float) position.getX(), (float) position.getY() + bounds.height() / 2.0f, paint);
    }

    public static void drawRectangle(Point position, double angle, double width, double height, Canvas canvas, Paint paint) {

        // Calculate points before rotation
        Point topLeft = new Point(position.getX() - (width / 2.0f), position.getY() - (height / 2.0f));
        Point topRight = new Point(position.getX() + (width / 2.0f), position.getY() - (height / 2.0f));
        Point bottomRight = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));
        Point bottomLeft = new Point(position.getX() - (width / 2.0f), position.getY() + (height / 2.0f));

        // Calculate points after rotation
        Point rotatedTopLeft = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, topLeft), (double) Geometry.calculateDistance(position, topLeft));
        Point rotatedTopRight = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, topRight), (double) Geometry.calculateDistance(position, topRight));
        Point rotatedBottomRight = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, bottomRight), (double) Geometry.calculateDistance(position, bottomRight));
        Point rotatedBottomLeft = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, bottomLeft), (double) Geometry.calculateDistance(position, bottomLeft));

        // Draw points in shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rotatedTopLeft.getX(), (float) rotatedTopLeft.getY());
        path.lineTo((float) rotatedTopRight.getX(), (float) rotatedTopRight.getY());
        path.lineTo((float) rotatedBottomRight.getX(), (float) rotatedBottomRight.getY());
        path.lineTo((float) rotatedBottomLeft.getX(), (float) rotatedBottomLeft.getY());
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawTrianglePath(Point startPosition, Point stopPosition, double triangleWidth, double triangleHeight, Canvas canvas, Paint paint) {

        double pathRotationAngle = Geometry.calculateRotationAngle(
                startPosition,
                stopPosition
        );

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        double pathDistance = Geometry.calculateDistance(
                startPosition,
                stopPosition
        );

        int triangleCount = (int) (pathDistance / (triangleHeight + 15));
        double triangleSpacing2 = pathDistance / triangleCount;

        for (int k = 0; k <= triangleCount; k++) {

            // Calculate triangle position
            Point triangleCenterPosition2 = Geometry.calculatePoint(
                    startPosition,
                    pathRotationAngle,
                    k * triangleSpacing2
            );

            paint.setStyle(Paint.Style.FILL);
            Shape.drawTriangle(
                    triangleCenterPosition2,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    canvas,
                    paint
            );
        }
    }

    /**
     * Draw regular shape.
     *
     * Reference:
     * - https://en.wikipedia.org/wiki/Regular_polygon
     * @param position
     * @param radius
     * @param sideCount
     * @param canvas
     * @param paint
     */
    public static void drawRegularPolygon(Point position, int radius, int sideCount, Canvas canvas, Paint paint) {
        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < sideCount; i++) {

            Point vertexPosition = new Point(
                    (position.getX() + radius * Math.cos(2.0f * Math.PI * (double) i / (double) sideCount)),
                    (position.getY() + radius * Math.sin(2.0f * Math.PI * (double) i / (double) sideCount))
            );

            // Draw points in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertexPosition.getX(), (float) vertexPosition.getY());
            }

            path.lineTo((float) vertexPosition.getX(), (float) vertexPosition.getY());
        }

//        path.lineTo(position.x, position.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawPolygon(List<Point> vertices, Canvas canvas, Paint paint) {
        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < vertices.size(); i++) {

            // Draw points in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertices.get(i).getX(), (float) vertices.get(i).getY());
            }

            path.lineTo((float) vertices.get(i).getX(), (float) vertices.get(i).getY());
        }

        path.close();

        canvas.drawPath(path, paint);
    }

}
