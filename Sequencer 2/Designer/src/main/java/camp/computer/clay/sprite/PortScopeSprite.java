package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;

public class PortScopeSprite extends Sprite {

    private PointF position = new PointF(0, 0); // Sprite position
    private float scale = 1.0f; // Sprite scale factor
    private float angle = 0.0f; // Sprite heading angle

//    public ArrayList<PointF> channelScopePositions = new ArrayList<PointF>();
    public ChannelType channelType = PortScopeSprite.ChannelType.NONE;
    public ChannelDirection channelDirection = PortScopeSprite.ChannelDirection.NONE;

    public PortScopeSprite() {
        initializeChannelTypes();
        initializeChannelDirections();
    }

    public enum ChannelDirection {

        NONE(0),
        OUTPUT(1),
        INPUT(2);

        // TODO: Change the index to a UUID?
        int index;

        ChannelDirection(int index) {
            this.index = index;
        }
    }

    public enum ChannelType {

        NONE(0),
        SWITCH(1),
        PULSE(2),
        WAVE(3),
        POWER(4),
        GROUND(5);

        // TODO: Change the index to a UUID?
        int index;

        ChannelType(int index) {
            this.index = index;
        }

        public static ChannelType getNextType(ChannelType currentChannelType) {
            return ChannelType.values()[(currentChannelType.index + 1) % ChannelType.values().length];
        }
    }

    private void initializeChannelTypes() {
        channelType = PortScopeSprite.ChannelType.NONE; // 0 for "none" (disabled)
    }

    private void initializeChannelDirections() {
        channelDirection = PortScopeSprite.ChannelDirection.NONE; // 0 for "none" (disabled)
    }

    public PointF getPosition() {
        return this.position;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
//        this.updateChannelScopePositions();
    }

    public void addPath(BoardSprite touchedBoardSpriteSource, int touchedChannelScopeSource, BoardSprite touchedBoardSpriteDestination, int touchedChannelScopeDestination) {
        ChannelPath channelPath = new ChannelPath();
        channelPath.source = touchedBoardSpriteSource;
        channelPath.sourceChannel = touchedChannelScopeSource;
        channelPath.destination = touchedBoardSpriteDestination;
        channelPath.destinationChannel = touchedChannelScopeDestination;
        channelPaths.add(channelPath);
    }

    public class ChannelPath {
        BoardSprite source;
        int sourceChannel;
        BoardSprite destination;
        int destinationChannel;
    }

    public ArrayList<ChannelPath> channelPaths = new ArrayList<ChannelPath>();

    @Override
    public void draw(Canvas mapCanvas, Paint paint) {

    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }
}
