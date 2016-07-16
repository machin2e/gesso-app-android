package camp.computer.clay.visualization.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

public abstract class Shape {

    public static void drawTriangle(PointF position, float angle, float width, float height, Canvas canvas, Paint paint) {

        // Calculate points before rotation
        PointF p1 = new PointF(position.x + -(width / 2.0f), position.y + (height / 2.0f));
        PointF p2 = new PointF(position.x + 0, position.y - (height / 2.0f));
        PointF p3 = new PointF(position.x + (width / 2.0f), position.y + (height / 2.0f));

        // Calculate points after rotation
        PointF rp1 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p1), (float) Geometry.calculateDistance(position, p1));
        PointF rp2 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p2), (float) Geometry.calculateDistance(position, p2));
        PointF rp3 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p3), (float) Geometry.calculateDistance(position, p3));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo(rp1.x, rp1.y);
        path.lineTo(rp2.x, rp2.y);
        path.lineTo(rp3.x, rp3.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawCircle(PointF position, float radius, float angle, Canvas canvas, Paint paint) {

        // Color
        canvas.drawCircle(
                position.x,
                position.y,
                radius,
                paint
        );

    }

    public static void drawText(PointF position, String text, float size, Canvas canvas, Paint paint) {

        // Style
        paint.setTextSize(size);

        // Style (Guaranteed)
        text = text.toUpperCase();
        paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, position.x, position.y + bounds.height() / 2.0f, paint);
    }

    public static void drawRectangle(PointF position, float angle, float width, float height, Canvas canvas, Paint paint) {

        // Calculate points before rotation
        PointF p1 = new PointF(position.x - (width / 2.0f), position.y - (height / 2.0f));
        PointF p2 = new PointF(position.x + (width / 2.0f), position.y - (height / 2.0f));
        PointF p3 = new PointF(position.x + (width / 2.0f), position.y + (height / 2.0f));
        PointF p4 = new PointF(position.x - (width / 2.0f), position.y + (height / 2.0f));

        // Calculate points after rotation
        PointF rp1 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p1), (float) Geometry.calculateDistance(position, p1));
        PointF rp2 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p2), (float) Geometry.calculateDistance(position, p2));
        PointF rp3 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p3), (float) Geometry.calculateDistance(position, p3));
        PointF rp4 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p4), (float) Geometry.calculateDistance(position, p4));

        // Draw points in shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo(rp1.x, rp1.y);
        path.lineTo(rp2.x, rp2.y);
        path.lineTo(rp3.x, rp3.y);
        path.lineTo(rp4.x, rp4.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawTrianglePath(PointF startPosition, PointF stopPosition, float triangleWidth, float triangleHeight, Canvas canvas, Paint paint) {

        float pathRotationAngle = Geometry.calculateRotationAngle(
                startPosition,
                stopPosition
        );

        float triangleRotationAngle = pathRotationAngle + 90.0f;

        float pathDistance = (float) Geometry.calculateDistance(
                startPosition,
                stopPosition
        );

        int triangleCount = (int) (pathDistance / (triangleHeight + 15));
        float triangleSpacing2 = pathDistance / triangleCount;

        for (int k = 0; k <= triangleCount; k++) {

            // Calculate triangle position
            PointF triangleCenterPosition2 = Geometry.calculatePoint(
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
     * Draw regular polygon.
     *
     * Reference:
     * - https://en.wikipedia.org/wiki/Regular_polygon
     * @param position
     * @param radius
     * @param sideCount
     * @param canvas
     * @param paint
     */
    public static void drawRegularPolygon(PointF position, int radius, int sideCount, Canvas canvas, Paint paint) {
        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < sideCount; i++) {

            PointF vertexPosition = new PointF(
                    (float) (position.x + radius * Math.cos(2.0f * Math.PI * (float) i / (float) sideCount)),
                    (float) (position.y + radius * Math.sin(2.0f * Math.PI * (float) i / (float) sideCount))
            );

            // Draw points in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo(vertexPosition.x, vertexPosition.y);
            }

            path.lineTo(vertexPosition.x, vertexPosition.y);
        }

//        path.lineTo(position.x, position.y);
        path.close();

        canvas.drawPath(path, paint);
    }

}
