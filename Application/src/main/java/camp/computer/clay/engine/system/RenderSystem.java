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
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.Palette;
import camp.computer.clay.platform.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
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

    // TODO: Move into platform layer (PlatformRenderSurface)?
    Entity camera = null;
    public boolean update(Palette palette) {

        // TODO: 11/5/2016 Remove need to pass canvas. Do this in a way that separates platform-specific rendering from preparation to render.

        // <HACK>
        PlatformRenderSurface platformRenderSurface = Application.getView().platformRenderSurface;
         Canvas canvas = platformRenderSurface.canvas;
        // </HACK>

        platformRenderSurface.canvas = canvas;
        Bitmap canvasBitmap = platformRenderSurface.canvasBitmap;
        Matrix identityMatrix = platformRenderSurface.identityMatrix;

        // Adjust the Camera
        palette.canvas.save();

        if (camera == null) {
            camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);
        }
        Transform cameraPosition = camera.getComponent(Transform.class);
        palette.canvas.translate(
                (float) platformRenderSurface.originPosition.x + (float) cameraPosition.x /* + (float) Application.getPlatform().getOrientationInput().getRotationY()*/,
                (float) platformRenderSurface.originPosition.y + (float) cameraPosition.y /* - (float) Application.getPlatform().getOrientationInput().getRotationX() */
        );
        palette.canvas.scale(
                (float) world.cameraSystem.getScale(camera),
                (float) world.cameraSystem.getScale(camera)
        );


        palette.canvas.drawColor(Color.WHITE); // Draw the background

        // TODO: renderSystem.update();

        drawEntities(palette);

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

        palette.canvas.restore();

        if (World.ENABLE_DRAW_OVERLAY) {
            drawOverlay(platformRenderSurface);
        }

//        if (addNotification) {
//            drawNotification("connected port", (float) position.x, (float) position.y, platformRenderSurface);
//        }

        // Paint the bitmap to the "primary" canvas.
        palette.canvas.drawBitmap(canvasBitmap, identityMatrix, null);

        /*
        // Alternative to the above
        canvas.save();
        canvas.concat(identityMatrix);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        canvas.restore();
        */

        return true;
    }

    public void drawEntities(Palette palette) {

        Group<Entity> entities = world.Manager.getEntities().filterActive(true).filterWithComponent(Image.class).sortByLayer();

        for (int j = 0; j < entities.size(); j++) {
            Entity entity = entities.get(j);

            // <HACK>
            PlatformRenderSurface platformRenderSurface = Application.getView().platformRenderSurface;
            // </HACK>

//            Canvas canvas = platformRenderSurface.canvas;
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
                    platformRenderSurface.drawEditablePath(pathEntity, palette);
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
                    if (Path.getMode(pathEntity) == Path.Mode.ELECTRONIC) {
                        platformRenderSurface.drawOverviewPath(pathEntity, palette);
                    }
                }

            }
            // TODO: <REFACTOR>
            // This was added so Prototype Extension/Path would render without Extension/Path components
            else if (entity.hasComponent(Image.class)) {

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    Image image = entity.getComponent(Image.class);
                    palette.canvas.save();
                    for (int i = 0; i < image.getImage().getShapes().size(); i++) {
                        platformRenderSurface.drawShape(image.getImage().getShapes().get(i), palette);
                    }
                    palette.canvas.restore();
                }

//                // Create Buffer Bitmap
//                Bitmap b = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
//                Canvas c = new Canvas(b);
//                Paint p = new Paint();
//                p.setColor(Color.RED);
//                c.drawCircle(100, 100, 40, p);
//
//                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
//                    Image image = entity.getComponent(Image.class);
//                    c.save();
//                    for (int i = 0; i < image.getImage().getShapes().size(); i++) {
//                        platformRenderSurface.drawShape(image.getImage().getShapes().get(i));
//                    }
//                    c.restore();
//                }

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
        public Transform offset = new Transform(DEFAULT_NOTIFICATION_OFFSET_X, DEFAULT_NOTIFICATION_OFFSET_Y);
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

    public static String NOTIFICATION_FONT = "fonts/ProggyClean.ttf";
    public static float NOTIFICATION_FONT_SIZE = 45;
    public static final float DEFAULT_NOTIFICATION_OFFSET_X = 0;
    public static final float DEFAULT_NOTIFICATION_OFFSET_Y = -50;
    private void drawNotification(Notification notification, PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;

        canvas.save();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(NOTIFICATION_FONT_SIZE);

        // Font
        Typeface typeface = Typeface.createFromAsset(Application.getView().getAssets(), NOTIFICATION_FONT);
        Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);
        paint.setTypeface(boldTypeface);

        Rect textBounds = new Rect();
        paint.getTextBounds(notification.text, 0, notification.text.length(), textBounds);
        float x = (float) (notification.position.x - (textBounds.width() / 2.0)) + (float) notification.offset.x;
        float y = (float) (notification.position.y + (textBounds.height() / 2.0)) + (float) notification.offset.y;
        canvas.drawText(notification.text, x, y, paint);
        canvas.restore();
    }

    public static int OVERLAY_TOP_MARGIN = 25;
    public static int OVERLAY_LEFT_MARGIN = 25;
    public static int OVERLAY_LINE_SPACING = 10;
    public static String OVERLAY_FONT = "fonts/ProggySquare.ttf";
    public static float OVERLAY_FONT_SIZE = 25;
    public static String OVERLAY_FONT_COLOR = "#ffff0000";
    public void drawOverlay(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        // World world = platformRenderSurface.getWorld(););

        // Font
        Typeface typeface = Typeface.createFromAsset(Application.getView().getAssets(), OVERLAY_FONT);
        Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);
        paint.setTypeface(boldTypeface);

        int linePosition = 0;

        // <FPS_LABEL>
        canvas.save();
        paint.setColor(Color.parseColor(OVERLAY_FONT_COLOR));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(OVERLAY_FONT_SIZE);

        String fpsText = "FPS: " + (int) platformRenderSurface.platformRenderClock.getFramesPerSecond();
        Rect fpsTextBounds = new Rect();
        paint.getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
        linePosition += OVERLAY_TOP_MARGIN + fpsTextBounds.height();
        canvas.drawText(fpsText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FPS_LABEL>

        // <FRAME_TIME>
        canvas.save();
        String frameTimeText = "Frame Time: " + (int) platformRenderSurface.platformRenderClock.currentFrameTime;
        Rect frameTimeTextBounds = new Rect();
        paint.getTextBounds(frameTimeText, 0, frameTimeText.length(), frameTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + frameTimeTextBounds.height();
        canvas.drawText(frameTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_TIME>

        // <FRAME_SLEEP_TIME>
        canvas.save();
        String frameSleepTimeText = "Frame Sleep Time: " + (int) platformRenderSurface.platformRenderClock.currentSleepTime;
        Rect frameSleepTimeTextBounds = new Rect();
        paint.getTextBounds(frameSleepTimeText, 0, frameSleepTimeText.length(), frameSleepTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + frameSleepTimeTextBounds.height();
        canvas.drawText(frameSleepTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_SLEEP_TIME>

        // <UPDATE_TIME>
        canvas.save();
        String updateTimeText = "Update Time: " + (int) world.updateTime;
        Rect updateTimeTextBounds = new Rect();
        paint.getTextBounds(updateTimeText, 0, updateTimeText.length(), updateTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + updateTimeTextBounds.height();
        canvas.drawText(updateTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </UPDATE_TIME>

        // <RENDER_TIME>
        canvas.save();
        String renderTimeText = "Render Time: " + (int) world.renderTime;
        Rect renderTimeTextBounds = new Rect();
        paint.getTextBounds(renderTimeText, 0, renderTimeText.length(), renderTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + renderTimeTextBounds.height();
        canvas.drawText(renderTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </RENDER_TIME>

        // <RENDER_TIME>
        canvas.save();
        String filterWithComponentCountText = "Filter Count: " + (int) world.lookupCount;
        Rect filterWithComponentCountTextBounds = new Rect();
        paint.getTextBounds(filterWithComponentCountText, 0, filterWithComponentCountText.length(), filterWithComponentCountTextBounds);
        linePosition += OVERLAY_LINE_SPACING + filterWithComponentCountTextBounds.height();
        canvas.drawText(filterWithComponentCountText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        world.lookupCount = 0;
        // </RENDER_TIME>

        // <ENTITY_STATISTICS>
        canvas.save();
        int entityCount = world.Manager.getEntities().size();
        int hostCount = world.Manager.getEntities().filterWithComponent(Host.class).size();
        int portCount = world.Manager.getEntities().filterWithComponent(Port.class).size();
        int extensionCount = world.Manager.getEntities().filterWithComponent(Extension.class).size();
        int pathCount = world.Manager.getEntities().filterWithComponent(Path.class).size();
        int cameraCount = world.Manager.getEntities().filterWithComponent(Camera.class).size();

        // Entities
        String text = "Entities: " + entityCount;
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Hosts
        canvas.save();
        text = "Hosts: " + hostCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Ports
        canvas.save();
        text = "Ports: " + portCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Extensions
        canvas.save();
        text = "Extensions: " + extensionCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Paths
        canvas.save();
        text = "Paths: " + pathCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Cameras
        canvas.save();
        text = "Cameras: " + cameraCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </ENTITY_STATISTICS>

        // <CAMERA_SCALE_MONITOR>
        canvas.save();
        Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0); // HACK
        String cameraScaleText = "Camera Scale: " + camera.getComponent(Transform.class).scale;
        Rect cameraScaleTextBounds = new Rect();
        paint.getTextBounds(cameraScaleText, 0, cameraScaleText.length(), cameraScaleTextBounds);
        linePosition += OVERLAY_LINE_SPACING + cameraScaleTextBounds.height();
        canvas.drawText(cameraScaleText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </CAMERA_SCALE_MONITOR>

        // <CAMERA_POSITION_MONITOR>
        canvas.save();
        String cameraPositionText = "Camera Position: " + camera.getComponent(Transform.class).x + ", " + camera.getComponent(Transform.class).x;
        Rect cameraPositionTextBounds = new Rect();
        paint.getTextBounds(cameraPositionText, 0, cameraPositionText.length(), cameraPositionTextBounds);
        linePosition += OVERLAY_LINE_SPACING + cameraPositionTextBounds.height();
        canvas.drawText(cameraPositionText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </CAMERA_POSITION_MONITOR>
    }
}
