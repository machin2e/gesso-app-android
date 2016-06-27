package camp.computer.clay.sprite.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class Shape {

    public static void drawTriangle(PointF position, float angle, float width, float height, Canvas canvas, Paint paint) {

//        canvas.save();
//
//        canvas.translate(position.x, position.y);
//        canvas.rotate(angle);

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

//        canvas.restore();
    }

    public static void drawRectangle(PointF position, float angle, float width, float height, Canvas canvas, Paint paint) {

//        canvas.save();

//        canvas.translate(position.x, position.y);
//        canvas.rotate(angle);

//        PointF p1 = new PointF(-(width / 2.0f), -(height / 2.0f));
//        PointF p2 = new PointF(0, (height / 2.0f));
//        PointF p3 = new PointF((width / 2.0f), -(height / 2.0f));
//        PointF p4 = new PointF((width / 2.0f), -(height / 2.0f));

//        PointF p1 = Geometry.calculatePoint(position, angle -135, (width / 2.0f));
//        PointF p2 = Geometry.calculatePoint(position, angle - 45, (height / 2.0f));
//        PointF p3 = Geometry.calculatePoint(position, angle + 45, (width / 2.0f));
//        PointF p4 = Geometry.calculatePoint(position, angle + 135, (height / 2.0f));

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

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo(rp1.x, rp1.y);
        path.lineTo(rp2.x, rp2.y);
        path.lineTo(rp3.x, rp3.y);
        path.lineTo(rp4.x, rp4.y);
        path.close();

        canvas.drawPath(path, paint);

//        canvas.restore();
    }

}
