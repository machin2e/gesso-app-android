package camp.computer.clay.viz.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

public class Palette {

    public Canvas canvas;

    public Paint paint;

    public Palette() {
        this.canvas = new Canvas();
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public Palette(Canvas canvas, Paint paint) {
        this.canvas = canvas;
        this.paint = paint;
    }

    public void drawTriangle(Point position, double angle, double width, double height) {

        // Calculate points before rotation
        Point p1 = new Point(position.getX() + -(width / 2.0f), position.getY() + (height / 2.0f));
        Point p2 = new Point(position.getX() + 0, position.getY() - (height / 2.0f));
        Point p3 = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));

        // Calculate points after rotation
        Point rp1 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p1), Geometry.calculateDistance(position, p1));
        Point rp2 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p2), Geometry.calculateDistance(position, p2));
        Point rp3 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p3), Geometry.calculateDistance(position, p3));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rp1.getX(), (float) rp1.getY());
        path.lineTo((float) rp2.getX(), (float) rp2.getY());
        path.lineTo((float) rp3.getX(), (float) rp3.getY());
        path.close();

        canvas.drawPath(path, paint);
    }

    public void drawTriangle(Point position, Point a, Point b, Point c) {

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) a.getX(), (float) a.getY());
        path.lineTo((float) b.getX(), (float) b.getY());
        path.lineTo((float) c.getX(), (float) c.getY());
        path.close();

        canvas.drawPath(path, paint);
    }

    public void drawLine(Point source, Point target) {

        // Color
        canvas.drawLine(
                (float) source.getX(),
                (float) source.getY(),
                (float) target.getX(),
                (float) target.getY(),
                paint
        );

    }

    public void drawCircle(Point position, double radius, double angle) {

        // Color
        canvas.drawCircle(
                (float) position.getX(),
                (float) position.getY(),
                (float) radius,
                paint
        );

    }

    public void drawText(Point position, String text, double size) {

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

    public void drawRectangle(Point position, double angle, double width, double height) {

        /*
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
        */

        canvas.drawRect(
                (float) (position.getX() - width / 2.0),
                (float) (position.getY() - height / 2.0),
                (float) (position.getX() + width / 2.0),
                (float) (position.getY() + height / 2.0),
                paint
        );
    }

    public void drawRoundRectangle(Point position, double angle, double width, double height, double radius) {

        canvas.drawRoundRect(
                (float) (position.getX() - width / 2.0),
                (float) (position.getY() - height / 2.0),
                (float) (position.getX() + width / 2.0),
                (float) (position.getY() + height / 2.0),
                (float) radius,
                (float) radius,
                paint
        );
    }

    public void drawTrianglePath(Point startPosition, Point stopPosition, double triangleWidth, double triangleHeight) {

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
            drawTriangle(
                    triangleCenterPosition2,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight
            );
        }
    }

    /**
     * Draw regular shape.
     * <p>
     * Reference:
     * - https://en.wikipedia.org/wiki/Regular_polygon
     *
     * @param position
     * @param radius
     * @param sideCount
     */
    public void drawRegularPolygon(Point position, double radius, int sideCount) {
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

        path.close();

        canvas.drawPath(path, paint);
    }

    public void drawShape(List<Point> vertices) {
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

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return this.paint;
    }
}
