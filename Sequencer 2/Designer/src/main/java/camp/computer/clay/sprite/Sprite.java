package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.TouchAction;

public abstract class Sprite {

    private TouchActionListener touchActionListener;

    // TODO: Model model;
    // TODO: Sprite(Model model) --- Constructor

    public abstract void draw (MapView mapView);

    public abstract boolean isTouching (PointF point);

    public interface TouchActionListener {
//        void onTouch(TouchAction touchAction);
//        void onTap(TouchAction touchAction);
//        void onDoubleTap(TouchAction touchAction);
//        void onHold(TouchAction touchAction);
//        void onMove(TouchAction touchAction);
//        void onPreDrag(TouchAction touchAction);
//        void onDrag(TouchAction touchAction);
//        void onRelease(TouchAction touchAction);
    }

    public abstract void onTouchAction(TouchAction touchAction);

    // TODO: change this to addOnTouchListener (since have abstract onTouchAction)... and call at end of that
    public void setOnTouchActionListener(TouchActionListener touchActionListener) {
        this.touchActionListener = touchActionListener;
    }

    public void touch (TouchAction touchAction) {
        onTouchAction(touchAction);
    }

}
