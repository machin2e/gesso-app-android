package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;

public class SystemSprite extends Sprite {

    // Sprites
    private ArrayList<DroneSprite> droneSprites = new ArrayList<DroneSprite>();

    public SystemSprite() {
        initialize();
    }

    private void initialize() {
        initializeSprites();
    }

    public void initializeSprites() {
        droneSprites.add(new DroneSprite(0, 0, 0));
        droneSprites.add(new DroneSprite(300, 400, 30));
        droneSprites.add(new DroneSprite(-200, -440, -55));
    }

    public ArrayList<DroneSprite> getDroneSprites() {
        return this.droneSprites;
    }

    @Override
    public void draw(Canvas mapCanvas, Paint paint) {
        // drawTitle();

        for (DroneSprite droneSprite : droneSprites) {
            droneSprite.draw(mapCanvas, paint);
        }

//        for (DroneSprite boardSprite : droneSprites) {
//            boardSprite.drawShapeLayer(mapCanvas, paint);
//        }

//        for (DroneSprite boardSprite : droneSprites) {
            // boardSprite.drawPaths(mapCanvas, paint);
//            boardSprite.drawTrianglePath(mapCanvas, paint);
//        }

//        for (DroneSprite boardSprite : droneSprites) {
//            boardSprite.drawStyleLayer(mapCanvas, paint);
//        }
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    public void updateState () {

        for (DroneSprite droneSprite : droneSprites) {
            droneSprite.updateChannelData();
        }
    }
}
