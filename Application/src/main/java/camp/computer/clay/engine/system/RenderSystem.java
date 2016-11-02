package camp.computer.clay.engine.system;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.Visibility2;
import camp.computer.clay.util.image.World;

public class RenderSystem extends System {

    @Override
    public boolean update(World world) {
        return true;
    }

    public boolean update(World world, Canvas canvas) {

        // <HACK>
        PlatformRenderSurface platformRenderSurface = Application.getView().platformRenderSurface;
//        Canvas canvas = platformRenderSurface.canvas;
        // </HACK>

        platformRenderSurface.canvas = canvas;
        Bitmap canvasBitmap = platformRenderSurface.canvasBitmap;
        Matrix identityMatrix = platformRenderSurface.identityMatrix;

        // Adjust the Camera
        canvas.save();

        Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);
        Transform cameraPosition = camera.getComponent(Transform.class);
        canvas.translate(
                (float) platformRenderSurface.originPosition.x + (float) cameraPosition.x /* + (float) Application.getPlatform().getOrientationInput().getRotationY()*/,
                (float) platformRenderSurface.originPosition.y + (float) cameraPosition.y /* - (float) Application.getPlatform().getOrientationInput().getRotationX() */
        );
        canvas.scale(
                (float) camera.getComponent(Camera.class).getScale(),
                (float) camera.getComponent(Camera.class).getScale()
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

    public void drawEntities(PlatformRenderSurface platformRenderSurface) {
        for (int j = 0; j < Entity.Manager.size(); j++) {
            Entity entity = Entity.Manager.get(j);

            Canvas canvas = platformRenderSurface.canvas;
            Paint paint = platformRenderSurface.paint;
            World world = platformRenderSurface.getWorld();

            if (entity.hasComponent(Host.class)) {

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.isVisible) {
                    Image image = entity.getComponent(Image.class);
                    canvas.save();
                    for (int i = 0; i < image.getShapes().size(); i++) {
                        image.getShapes().get(i).draw(platformRenderSurface);
                    }
                    canvas.restore();
                }

            } else if (entity.hasComponent(Extension.class)) {

//            Image image = entity.getComponent(Image.class);
//            if (image.isVisible()) {
//                canvas.save();
//                for (int i = 0; i < image.getShapes().size(); i++) {
//                    image.getShapes().get(i).draw(platformRenderSurface);
//                }
//                canvas.restore();
//            }

                // TODO: <MOVE_THIS_INTO_PORTABLE_SYSTEM_AND_MAKE_IT_WORK>
                Group<Entity> ports = entity.getComponent(Portable.class).getPorts();
                for (int i = 0; i < ports.size(); i++) {
                    if (ports.get(i).getComponent(Port.class).getExtension() == null) {
                        // TODO: Remove Port Entity!
                        ports.remove(ports.get(i).getUuid());

                        Entity.Manager.remove(ports.get(i).getUuid());
                    }
                }
                // TODO: </MOVE_THIS_INTO_PORTABLE_SYSTEM_AND_MAKE_IT_WORK>

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.isVisible) {
                    Image image = entity.getComponent(Image.class);
                    canvas.save();
                    for (int i = 0; i < image.getShapes().size(); i++) {
                        image.getShapes().get(i).draw(platformRenderSurface);
                    }
                    canvas.restore();
                }

            } else if (entity.hasComponent(Port.class)) {

//            Image image = entity.getComponent(Image.class);
//            if (image.isVisible()) {
//                canvas.save();
//                for (int i = 0; i < image.getShapes().size(); i++) {
//                    image.getShapes().get(i).draw(platformRenderSurface);
//                }
//                canvas.restore();
//            }

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.isVisible) {
                    Image image = entity.getComponent(Image.class);
                    canvas.save();
                    for (int i = 0; i < image.getShapes().size(); i++) {
                        image.getShapes().get(i).draw(platformRenderSurface);
                    }
                    canvas.restore();
                }

            } else if (entity.hasComponent(Path.class)) {

                Image image = entity.getComponent(Image.class);

//            if (image.isVisible()) {
//                Entity pathEntity = image.getEntity();
//                if (pathEntity.getComponent(Path.class).getType() == Path.Type.MESH) {
//                    // Draw PathEntity between Ports
//                    platformRenderSurface.drawTrianglePath(pathEntity, platformRenderSurface);
//                } else if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
//                    platformRenderSurface.drawLinePath(pathEntity, platformRenderSurface);
//                }
//            } else {
//                Entity pathEntity = entity; // image.getPath();
//                if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
//                    platformRenderSurface.drawPhysicalPath(pathEntity, platformRenderSurface);
//                }
//            }

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.isVisible) {
                    Entity pathEntity = image.getEntity();
                    if (pathEntity.getComponent(Path.class).getType() == Path.Type.MESH) {
                        // Draw PathEntity between Ports
                        platformRenderSurface.drawTrianglePath(pathEntity, platformRenderSurface);
                    } else if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
                        platformRenderSurface.drawLinePath(pathEntity, platformRenderSurface);
                    }
                } else if (visibility != null && !visibility.isVisible) {
                    Entity pathEntity = entity; // image.getPath();
                    if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
                        platformRenderSurface.drawPhysicalPath(pathEntity, platformRenderSurface);
                    }
                }

            } else if (entity.hasComponent(Image.class)) { // e.g., for Prototype Extension

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.isVisible) {
                    Image image = entity.getComponent(Image.class);
                    canvas.save();
                    for (int i = 0; i < image.getShapes().size(); i++) {
                        image.getShapes().get(i).draw(platformRenderSurface);
                    }
                    canvas.restore();
                }

            }
        }
    }

    public void drawPrototypes(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
//        World world = platformRenderSurface.getWorld();

        canvas.save();

        // Draw any prototype Paths and Extensions
        drawPathPrototype(platformRenderSurface);
//        drawExtensionPrototype(platformRenderSurface);

        canvas.restore();
    }

    // TODO: Make this into a shape and put this on a separate layerIndex!
    public void drawPathPrototype(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        World world = platformRenderSurface.getWorld();


        if (world.pathPrototypeVisibility2 == Visibility2.VISIBLE) {

            double triangleWidth = 20;
            double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
            double triangleSpacing = 35;

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
//            paint.setColor(this.getUniqueColor());

            double pathRotationAngle = Geometry.getAngle(
                    world.pathPrototypeSourcePosition,
                    world.pathPrototypeDestinationCoordinate
            );

            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(
                    world.pathPrototypeSourcePosition,
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(
                    world.pathPrototypeDestinationCoordinate,
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            paint.setColor(Color.parseColor("#efefef"));
            platformRenderSurface.drawTrianglePath(pathStartCoordinate, pathStopCoordinate, triangleWidth, triangleHeight);

            // Color
            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.parseColor("#efefef"));
            double shapeRadius = 40.0;
            platformRenderSurface.drawCircle(world.pathPrototypeDestinationCoordinate, shapeRadius, 0.0f);
        }
    }

//    public void drawExtensionPrototype(PlatformRenderSurface platformRenderSurface) {
//
//        Canvas canvas = platformRenderSurface.canvas;
//        Paint paint = platformRenderSurface.paint;
//        World world = platformRenderSurface.getWorld();
//
////        if (world.extensionPrototypeVisibility2 == Visibility2.VISIBLE) {
//        if (world.extensionPrototype.getComponent(Visibility.class).isVisible) {
//
//            double pathRotationAngle = Geometry.getAngle(
//                    world.pathPrototypeSourcePosition,
////                    world.extensionPrototypePosition
//                    world.extensionPrototype.getComponent(Transform.class)
//            );
//
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.parseColor("#fff7f7f7"));
//
////            platformRenderSurface.drawRectangle(world.extensionPrototypePosition, pathRotationAngle + 180, 200, 200);
////            platformRenderSurface.drawRectangle(world.extensionPrototype.getComponent(Transform.class), pathRotationAngle + 180, 200, 200);
//        }
//    }

    public void drawOverlay(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        World world = platformRenderSurface.getWorld();

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
