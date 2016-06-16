package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class SystemSprite extends Sprite {
    @Override
    public void draw(Canvas mapCanvas, Paint paint) {

    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }
}
