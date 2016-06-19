package camp.computer.clay.sprite.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import camp.computer.clay.sprite.BoardSprite;
import camp.computer.clay.sprite.Sprite;

public class FlowPathSprite extends Sprite {

    public class FlowPath {
        BoardSprite source;
        int sourceChannel;
        BoardSprite destination;
        int destinationChannel;
    }

    private FlowPath flowPath;

    // --- STYLE ---

    public boolean showDirectedPaths = true;
    public boolean showOnlyPathTerminals = true;
    float pathTerminalLength = 100.0f;
    float triangleWidth = 25;
    float triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
    float triangleSpacing = 35;

    // ^^^ STYLE ^^^

    public FlowPathSprite(BoardSprite touchedBoardSpriteSource, int touchedChannelScopeSource, BoardSprite touchedBoardSpriteDestination, int touchedChannelScopeDestination) {
        FlowPath flowPath = new FlowPath();
        flowPath.source = touchedBoardSpriteSource;
        flowPath.sourceChannel = touchedChannelScopeSource;
        flowPath.destination = touchedBoardSpriteDestination;
        flowPath.destinationChannel = touchedChannelScopeDestination;
        this.flowPath = flowPath;
    }

    @Override
    public void draw(Canvas mapCanvas, Paint paint) {
        if (this.showDirectedPaths) {
            drawTrianglePaths(mapCanvas, paint);
        } else {
            drawLinePath(mapCanvas, paint);
        }
    }

    private void drawLinePath (Canvas mapCanvas, Paint paint) {
        flowPath.destination.showChannelScope(flowPath.destinationChannel);

        mapCanvas.save();
        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(flowPath.source.CHANNEL_COLOR_PALETTE[flowPath.sourceChannel]);

        mapCanvas.drawLine(
                flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition().x,
                flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition().y,
                flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition().x,
                flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition().y,
                paint
        );

        mapCanvas.restore();
    }

    public void drawTrianglePaths(Canvas mapCanvas, Paint paint) {

        flowPath.destination.showChannelScope(flowPath.destinationChannel);

        mapCanvas.save();

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(flowPath.source.CHANNEL_COLOR_PALETTE[flowPath.sourceChannel]);

        boolean showLinePaths = false;
        if (showLinePaths) {
            mapCanvas.drawLine(
                    flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition().x,
                    flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition().y,
                    flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition().x,
                    flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition().y,
                    paint
            );
        }

        if (showDirectedPaths) {
            float rotationAngle = Geometry.calculateRotationAngle(
                    flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition(),
                    flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition()
            );

            if (showOnlyPathTerminals) {

                float distance = (float) Geometry.getDistance(
                        flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition(),
                        flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition()
                );

                PointF triangleCenterPosition = Geometry.calculatePoint(
                        flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition(),
                        rotationAngle,
                        2 * triangleSpacing
                );

                drawTriangle(
                        triangleCenterPosition,
                        rotationAngle + 180,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

                PointF triangleCenterPositionDestination = Geometry.calculatePoint(
                        flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition(),
                        rotationAngle,
                        distance - 2 * triangleSpacing
                );

                drawTriangle(
                        triangleCenterPositionDestination,
                        rotationAngle + 180,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

            } else {

                for (int k = 0; ; k++) {

                    PointF triangleCenterPosition = Geometry.calculatePoint(
                            flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition(),
                            rotationAngle,
                            k * triangleSpacing
                    );

                    float pathDistance = (float) Geometry.getDistance(
                            flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition(),
                            flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition()
                    );

                    float pathDistanceToStart = (float) Geometry.getDistance(
                            flowPath.source.portScopeSprites.get(flowPath.sourceChannel).getPosition(),
                            triangleCenterPosition
                    );

                    float pathDistanceToEnd = (float) Geometry.getDistance(
                            triangleCenterPosition,
                            flowPath.destination.portScopeSprites.get(flowPath.destinationChannel).getPosition()
                    );

//                if (showOnlyPathTerminals) {
//                    if ((pathDistanceToStart > pathTerminalLength && !(pathDistanceToEnd < pathTerminalLength))
//                            || (!(pathDistanceToStart < pathTerminalLength) && pathDistanceToEnd > pathTerminalLength)) {
//                        continue;
//                    }
//                }

                    if (k * triangleSpacing > pathDistance) {
                        break;
                    }

                    drawTriangle(
                            triangleCenterPosition,
                            rotationAngle + 180,
                            triangleWidth,
                            triangleHeight,
                            mapCanvas,
                            paint
                    );
                }
            }
        }



        mapCanvas.restore();
    }

    private void drawTriangle(PointF position, float angle, float width, float height, Canvas canvas, Paint paint) {

        canvas.save();

        canvas.translate(position.x, position.y);
        canvas.rotate(angle);

        PointF p1 = new PointF(-(width / 2.0f), -(height / 2.0f));
        PointF p2 = new PointF(0, (height / 2.0f));
        PointF p3 = new PointF((width / 2.0f), -(height / 2.0f));

//        paint.setStrokeWidth(0);
//        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        // paint.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();

        canvas.drawPath(path, paint);

        canvas.restore();
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }
}
