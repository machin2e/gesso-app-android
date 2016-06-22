package camp.computer.clay.sprite;

import android.graphics.PointF;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.TouchArticulation;

public abstract class Sprite {

    private TouchActionListener touchActionListener;

    // TODO: Model model;
    // TODO: Sprite(Model model) --- Constructor

    private boolean isVisible = true;

    public void setVisibility (boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean getVisibility () {
        return this.isVisible;
    }

    public abstract void draw (MapView mapView);

    public abstract boolean isTouching (PointF point);

    public interface TouchActionListener {
    }

    public abstract void onTouchAction(TouchArticulation touchArticulation);

    // TODO: change this to addOnTouchListener (since have abstract onTouchAction)... and call at end of that
    public void setOnTouchActionListener(TouchActionListener touchActionListener) {
        this.touchActionListener = touchActionListener;
    }

    public void touch (TouchArticulation touchArticulation) {
        onTouchAction(touchArticulation);
    }

}
