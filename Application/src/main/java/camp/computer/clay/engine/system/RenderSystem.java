package camp.computer.clay.engine.system;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import camp.computer.clay.application.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

public class RenderSystem extends System {

    @Override
    public boolean update(Space space) {
        return true;
    }

    public boolean update(PlatformRenderSurface platformRenderSurface, Space space, Canvas canvas) {

        platformRenderSurface.canvas = canvas;
        Bitmap canvasBitmap = platformRenderSurface.canvasBitmap;
        Matrix identityMatrix = platformRenderSurface.identityMatrix;

        if (platformRenderSurface.space == null || platformRenderSurface.canvas == null) {
            return false;
        }

        // Adjust the Camera
        canvas.save();

        Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);
        canvas.translate(
                (float) platformRenderSurface.originPosition.x + (float) cameraEntity.getComponent(Camera.class).getPosition().x /* + (float) Application.getView().getOrientationInput().getRotationY()*/,
                (float) platformRenderSurface.originPosition.y + (float) cameraEntity.getComponent(Camera.class).getPosition().y /* - (float) Application.getView().getOrientationInput().getRotationX() */
        );
        canvas.scale(
                (float) cameraEntity.getComponent(Camera.class).getScale(),
                (float) cameraEntity.getComponent(Camera.class).getScale()
        );


        canvas.drawColor(Color.WHITE); // Draw the background

        // TODO: renderSystem.update();

        drawPrototypes(platformRenderSurface);
        drawEntities(platformRenderSurface);

        canvas.restore();

        drawOverlay(platformRenderSurface);

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap(canvasBitmap, identityMatrix, null);

        /*
        // Alternative to the above
        canvas.save();
        canvas.concat(identityMatrix);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        canvas.restore();
        */

        return true;
    }

    public void drawPrototypes(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        Space space = platformRenderSurface.getSpace();

        canvas.save();

        // Draw any prototype Paths and Extensions
        drawPathPrototype(platformRenderSurface);
        drawExtensionPrototype(platformRenderSurface);

        canvas.restore();
    }

    // TODO: Make this into a shape and put this on a separate layerIndex!
    public void drawPathPrototype(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        Space space = platformRenderSurface.getSpace();


        if (space.pathPrototypeVisibility == Visibility.VISIBLE) {

            double triangleWidth = 20;
            double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
            double triangleSpacing = 35;

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
//            paint.setColor(this.getUniqueColor());

            double pathRotationAngle = Geometry.getAngle(
                    space.pathPrototypeSourcePosition,
                    space.pathPrototypeDestinationCoordinate
            );

            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(
                    space.pathPrototypeSourcePosition,
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(
                    space.pathPrototypeDestinationCoordinate,
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            paint.setColor(Color.parseColor("#efefef"));
            platformRenderSurface.drawTrianglePath(pathStartCoordinate, pathStopCoordinate, triangleWidth, triangleHeight);

            // Color
            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.parseColor("#efefef"));
            double shapeRadius = 40.0;
            platformRenderSurface.drawCircle(space.pathPrototypeDestinationCoordinate, shapeRadius, 0.0f);
        }
    }

    public void drawExtensionPrototype(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        Space space = platformRenderSurface.getSpace();

        if (space.extensionPrototypeVisibility == Visibility.VISIBLE) {

            double pathRotationAngle = Geometry.getAngle(
                    space.pathPrototypeSourcePosition,
                    space.extensionPrototypePosition
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#fff7f7f7"));

            platformRenderSurface.drawRectangle(space.extensionPrototypePosition, pathRotationAngle + 180, 200, 200);
        }
    }

    public void drawEntities(PlatformRenderSurface platformRenderSurface) {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            Entity entity = Entity.Manager.get(i);
            drawEntity(platformRenderSurface, entity);
        }
    }

    public void drawEntity(PlatformRenderSurface platformRenderSurface, Entity entity) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        Space space = platformRenderSurface.getSpace();

        if (entity.hasComponent(Host.class)) {

            Image image = entity.getComponent(Image.class);
            if (image.isVisible()) {
                canvas.save();
                for (int i = 0; i < image.getShapes().size(); i++) {
                    image.getShapes().get(i).draw(platformRenderSurface);
                }
                canvas.restore();
            }

        } else if (entity.hasComponent(Extension.class)) {

            Image image = entity.getComponent(Image.class);
            if (image.isVisible()) {
                canvas.save();
                for (int i = 0; i < image.getShapes().size(); i++) {
                    image.getShapes().get(i).draw(platformRenderSurface);
                }
                canvas.restore();
            }

        } else if (entity.hasComponent(Port.class)) {

            Image image = entity.getComponent(Image.class);
            if (image.isVisible()) {
                canvas.save();
                for (int i = 0; i < image.getShapes().size(); i++) {
                    image.getShapes().get(i).draw(platformRenderSurface);
                }
                canvas.restore();
            }

        } else if (entity.hasComponent(Path.class)) {

            Image image = entity.getComponent(Image.class);

            if (image.isVisible()) {
                Entity pathEntity = image.getEntity();
                if (pathEntity.getComponent(Path.class).getType() == Path.Type.MESH) {
                    // Draw PathEntity between Ports
                    platformRenderSurface.drawTrianglePath(pathEntity, platformRenderSurface);
                } else if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
                    platformRenderSurface.drawLinePath(pathEntity, platformRenderSurface);
                }
            } else {
                Entity pathEntity = entity; // image.getPath();
                if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
                    platformRenderSurface.drawPhysicalPath(pathEntity, platformRenderSurface);
                }
            }

        }
    }

    public void drawOverlay(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        Space space = platformRenderSurface.getSpace();

        int linePosition = 0;

        // <FPS_LABEL>
        canvas.save();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(35);

        String fpsText = "FPS: " + (int) platformRenderSurface.platformRenderer.getFramesPerSecond();
        Rect fpsTextBounds = new Rect();
        paint.getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
        linePosition += 25 + fpsTextBounds.height();
        canvas.drawText(fpsText, 25, linePosition, paint);
        canvas.restore();
        // </FPS_LABEL>

        // <ENTITY_STATISTICS>
        canvas.save();
        int entityCount = Entity.Manager.size();
        int hostCount = Entity.Manager.filterWithComponent(Host.class).size();
        int portCount = Entity.Manager.filterWithComponent(Port.class).size();
        int extensionCount = Entity.Manager.filterWithComponent(Extension.class).size();
        int pathCount = Entity.Manager.filterWithComponent(Path.class).size();
        int cameraCount = Entity.Manager.filterWithComponent(Camera.class).size();

        // Entities
        String text = "Entities: " + entityCount;
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Hosts
        canvas.save();
        text = "Hosts: " + hostCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Ports
        canvas.save();
        text = "Ports: " + portCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Extensions
        canvas.save();
        text = "Extensions: " + extensionCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Paths
        canvas.save();
        text = "Paths: " + pathCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Cameras
        canvas.save();
        text = "Cameras: " + cameraCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();
        // </ENTITY_STATISTICS>
    }
}
