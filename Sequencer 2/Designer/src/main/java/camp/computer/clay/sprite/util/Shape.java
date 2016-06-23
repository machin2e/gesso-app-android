package camp.computer.clay.sprite.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class Shape {

    public static void drawTriangle(PointF position, float angle, float width, float height, Canvas canvas, Paint paint) {

        canvas.save();

        canvas.translate(position.x, position.y);
        canvas.rotate(angle);

        PointF p1 = new PointF(-(width / 2.0f), -(height / 2.0f));
        PointF p2 = new PointF(0, (height / 2.0f));
        PointF p3 = new PointF((width / 2.0f), -(height / 2.0f));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();

        canvas.drawPath(path, paint);

        canvas.restore();
    }

}
