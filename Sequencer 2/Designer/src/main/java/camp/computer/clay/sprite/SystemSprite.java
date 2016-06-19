package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;

import camp.computer.clay.sprite.util.physics.Diagram;
import camp.computer.clay.sprite.util.physics.Node;
import camp.computer.clay.sprite.util.physics.Rectangle;

public class SystemSprite extends Sprite {

    // Sprites
    private ArrayList<BoardSprite> boardSprites = new ArrayList<BoardSprite>();

    public SystemSprite() {
        initialize();
    }

    private void initialize() {
        initializeSprites();
    }

    public void initializeSprites() {
        boardSprites.add(new BoardSprite(0, 0, 0));
        boardSprites.add(new BoardSprite(300, 400, 30));
        boardSprites.add(new BoardSprite(-200, -440, -55));
    }

    public ArrayList<BoardSprite> getBoardSprites() {
        return this.boardSprites;
    }

    @Override
    public void draw(Canvas mapCanvas, Paint paint) {
        // drawTitle();

        for (BoardSprite boardSprite : boardSprites) {
            boardSprite.draw(mapCanvas, paint);
        }

//        for (BoardSprite boardSprite : boardSprites) {
//            boardSprite.drawFormLayer(mapCanvas, paint);
//        }

//        for (BoardSprite boardSprite : boardSprites) {
            // boardSprite.drawPaths(mapCanvas, paint);
//            boardSprite.drawTrianglePaths(mapCanvas, paint);
//        }

//        for (BoardSprite boardSprite : boardSprites) {
//            boardSprite.drawStyleLayer(mapCanvas, paint);
//        }
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    public void updateState () {

        for (BoardSprite boardSprite : boardSprites) {
            boardSprite.updateChannelData();
        }
    }
}
