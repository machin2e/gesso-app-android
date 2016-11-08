package camp.computer.clay.engine.system;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.util.Visible;

public class RenderSystem extends System {

    public RenderSystem(World world) {
        super(world);
    }

    @Override
    public void update() {
    }

    public boolean update(Canvas canvas) {

        // TODO: 11/5/2016 Remove need to pass canvas. Do this in a way that separates platform-specific rendering from preparation to render.

        // <HACK>
        PlatformRenderSurface platformRenderSurface = Application.getView().platformRenderSurface;
        // Canvas canvas = platformRenderSurface.canvas;
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
                (float) world.cameraSystem.getScale(camera),
                (float) world.cameraSystem.getScale(camera)
        );


        canvas.drawColor(Color.WHITE); // Draw the background

        // TODO: renderSystem.update();

        drawEntities(platformRenderSurface);

        for (int i = 0; i < notifications.size(); ) {
            Notification notification = notifications.get(i);
            if (notification.state == State.WAITING) {
                notification.run();
                drawNotification(notification, platformRenderSurface);
            } else if (notification.state == State.RUNNING) {
                drawNotification(notification, platformRenderSurface);
                i++;
            } else if (notification.state == State.COMPLETE) {
                notifications.remove(i);
            }
        }
//        if (addNotification) {
//            drawNotification(echoText, (float) echoTextPosition.x, (float) echoTextPosition.y, platformRenderSurface);
//        }

        canvas.restore();

        drawOverlay(platformRenderSurface);

//        if (addNotification) {
//            drawNotification("connected port", (float) position.x, (float) position.y, platformRenderSurface);
//        }

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

        Group<Entity> entities = Entity.Manager.filterActive(true).filterWithComponent(Image.class).sortByLayer();

        for (int j = 0; j < entities.size(); j++) {
            Entity entity = entities.get(j);

            Canvas canvas = platformRenderSurface.canvas;
            // Paint paint = platformRenderSurface.paint;
            // World world = platformRenderSurface.getWorld();

            // TODO: <MOVE_THIS_INTO_PORTABLE_SYSTEM>
            /*
            // TODO: Check world state to see if CREATING_PORT or CREATING_EXTENSION.
            Group<Entity> ports = entity.getComponent(Portable.class).getPorts();
            int size = ports.size();
            for (int i = 0; i < size; i++) {
                Entity port = ports.get(i);
                if (port.getComponent(Port.class).getExtension() == null) {
                    // TODO: Remove Port Entity!
                    ports.remove(port);

                    Entity.Manager.remove(port);

                    size--;
                }
            }
            */
            // TODO: </MOVE_THIS_INTO_PORTABLE_SYSTEM>

            if (entity.hasComponent(Path.class)) {

                Image image = entity.getComponent(Image.class);

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    Entity pathEntity = image.getEntity();
                    platformRenderSurface.drawEditablePath(pathEntity, platformRenderSurface);
                    /*
                    if (pathEntity.getComponent(Path.class).getType() == Path.Type.MESH) {
                        // TODO: Draw Path between wirelessly connected Ports
                        // platformRenderSurface.drawTrianglePath(pathEntity, platformRenderSurface);
                    } else if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
                        platformRenderSurface.drawEditablePath(pathEntity, platformRenderSurface);
                    }
                    */
                } else if (visibility != null && visibility.getVisibile() == Visible.INVISIBLE) {
                    Entity pathEntity = entity; // image.getPath();
                    if (pathEntity.getComponent(Path.class).getMode() == Path.Mode.ELECTRONIC) {
                        platformRenderSurface.drawOverviewPath(pathEntity, platformRenderSurface);
                    }
                }

            }
            // TODO: <REFACTOR>
            // This was added so Prototype Extension/Path would render without Extension/Path components
            else if (entity.hasComponent(Image.class)) {

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    Image image = entity.getComponent(Image.class);
                    canvas.save();
                    for (int i = 0; i < image.getImage().getShapes().size(); i++) {
                        platformRenderSurface.drawShape(image.getImage().getShapes().get(i));
                    }
                    canvas.restore();
                }

            }
            // TODO: </REFACTOR>
        }
    }

    public enum State {
        WAITING,
        RUNNING,
        COMPLETE
    }

    public class Notification {
        public String text = "";
        public Transform position = new Transform(0, 0);
        public float xOffset = 0;
        public float yOffset = -50;
        public long timeout = 1000;

        public State state = State.WAITING;

        public Notification(String text, Transform position, long timeout) {
            this.text = text;
            this.position.set(position);
            this.timeout = timeout;
        }

        public void run() {
            state = State.RUNNING;
//            platformRenderSurface.startTimer(timeout);
        }
    }

    private List<Notification> notifications = new ArrayList<>();

    public void addNotification(String text, Transform position, long timeout) {
        Notification notification = new Notification(text, position, timeout);
        notifications.add(notification);

        PlatformRenderSurface platformRenderSurface = Application.getView().platformRenderSurface;
        platformRenderSurface.startTimer(timeout, notification);
    }

    private void drawNotification(Notification notification, PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;

        canvas.save();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(45);

        // Font
        Typeface typeface = Typeface.createFromAsset(Application.getView().getAssets(), "fonts/Dosis-Bold.ttf");
        Typeface boldTypeface = Typeface.create(typeface, Typeface.BOLD);
        paint.setTypeface(boldTypeface);

        Rect textBounds = new Rect();
        paint.getTextBounds(notification.text, 0, notification.text.length(), textBounds);
        float centeredX = (float) (notification.position.x - (textBounds.width() / 2.0));
        float centeredY = (float) (notification.position.y + (textBounds.height() / 2.0));
        float x = centeredX + notification.xOffset;
        float y = centeredY + notification.yOffset;
        canvas.drawText(notification.text, x, y, paint);
        canvas.restore();
    }

    public void drawOverlay(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        // World world = platformRenderSurface.getWorld();

        int linePosition = 0;

        // <FPS_LABEL>
        canvas.save();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(25);

        String fpsText = "FPS: " + (int) platformRenderSurface.platformRenderClock.getFramesPerSecond();
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

        // <CAMERA_SCALE_MONITOR>
        canvas.save();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(25);

        Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0); // HACK
        String cameraScaleText = "Camera Scale: " + camera.getComponent(Transform.class).scale;
        Rect cameraScaleTextBounds = new Rect();
        paint.getTextBounds(cameraScaleText, 0, cameraScaleText.length(), cameraScaleTextBounds);
        linePosition += 25 + cameraScaleTextBounds.height();
        canvas.drawText(cameraScaleText, 25, linePosition, paint);
        canvas.restore();
        // </CAMERA_SCALE_MONITOR>

        // <CAMERA_POSITION_MONITOR>
        canvas.save();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(25);

        String cameraPositionText = "Camera Position: " + camera.getComponent(Transform.class).x + ", " + camera.getComponent(Transform.class).x;
        Rect cameraPositionTextBounds = new Rect();
        paint.getTextBounds(cameraPositionText, 0, cameraPositionText.length(), cameraPositionTextBounds);
        linePosition += 25 + cameraPositionTextBounds.height();
        canvas.drawText(cameraPositionText, 25, linePosition, paint);
        canvas.restore();
        // </CAMERA_POSITION_MONITOR>
    }
}
