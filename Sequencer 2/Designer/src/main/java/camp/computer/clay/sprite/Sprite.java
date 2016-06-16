package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class Sprite {

    public abstract void draw (Canvas mapCanvas, Paint paint);

    public abstract boolean isTouching (PointF point);

}
