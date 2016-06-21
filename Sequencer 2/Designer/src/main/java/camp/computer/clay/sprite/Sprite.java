package camp.computer.clay.sprite;

import android.graphics.PointF;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.TouchInteraction;

public abstract class Sprite {

    private TouchActionListener touchActionListener;

    // TODO: Model model;
    // TODO: Sprite(Model model) --- Constructor

    public abstract void draw (MapView mapView);

    public abstract boolean isTouching (PointF point);

    public interface TouchActionListener {
//        void onTouch(TouchInteraction touchAction);
//        void onTap(TouchInteraction touchAction);
//        void onDoubleTap(TouchInteraction touchAction);
//        void onHold(TouchInteraction touchAction);
//        void onMove(TouchInteraction touchAction);
//        void onPreDrag(TouchInteraction touchAction);
//        void onDrag(TouchInteraction touchAction);
//        void onRelease(TouchInteraction touchAction);
    }

    public abstract void onTouchAction(TouchInteraction touchInteraction);

    // TODO: change this to addOnTouchListener (since have abstract onTouchAction)... and call at end of that
    public void setOnTouchActionListener(TouchActionListener touchActionListener) {
        this.touchActionListener = touchActionListener;
    }

    public void touch (TouchInteraction touchInteraction) {
        onTouchAction(touchInteraction);
    }

}
