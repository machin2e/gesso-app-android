package camp.computer.clay.application.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.engine.component.Actor;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Camera;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.Host;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.engine.entity.PortableEntity;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.geometry.Polygon;
import camp.computer.clay.util.geometry.Polyline;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Triangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

public class Display extends SurfaceView implements SurfaceHolder.Callback {

    // Space Rendering Context
    private Bitmap canvasBitmap = null;
    public Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Space DisplayOutput
    private SurfaceHolder surfaceHolder;
    private DisplayOutput displayOutput;

    // Coordinate System (Grid)
    private Transform originPosition = new Transform();

    // Space
    private Space space;

    public Display(Context context) {
        super(context);
        setFocusable(true);
    }

    public Display(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Display(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Get dimensions of the Surface
        canvasWidth = getWidth();
        canvasHeight = getHeight();

        // Create a bitmap to use as a drawing buffer equal in size to the full size of the Surface
        canvasBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(canvasBitmap);

        // Create Identity Matrix
        identityMatrix = new Matrix();

        // Center the space coordinate system
        originPosition.set(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO: Resize the Viewport to width and height
        Log.v("Display", "surfaceChanged");
        Log.v("Display", "width: " + width + ", height: " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        /*
        // Kill the background Thread
        boolean retry = true;
        // displayOutput.setRunning (false);
        while (retry) {
            try {
                displayOutput.join ();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
        */
    }

    public void onResume() {

        surfaceHolder = getHolder();
        getHolder().addCallback(this);

        // Create and start background Thread
        displayOutput = new DisplayOutput(this);
        displayOutput.setRunning(true);
        displayOutput.start();

//        // Start communications
//        getClay ().getCommunication ().startDatagramServer();

        // Remove this?
        update();

    }

    public void onPause() {
        // Log.v("MapView", "onPause");

        // Pause the communications
//        getClay ().getCommunication ().stopDatagramServer (); // HACK: This was commented out to prevent the server from "crashing" into an invalid state!

        // Kill the background Thread
        boolean retry = true;
        displayOutput.setRunning(false);

        while (retry) {
            try {
                displayOutput.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doDraw(Canvas canvas) {
        this.canvas = canvas;

        if (this.space == null || this.canvas == null) {
            return;
        }

        // Adjust the Camera
        canvas.save();
        adjustCamera();
        canvas.drawColor(Color.WHITE); // Draw the background

        getSpace().doDraw(this); // Space
        drawEntities();

        canvas.restore();

        drawOverlay();

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap(canvasBitmap, identityMatrix, null);

        /*
        // Alternative to the above
        canvas.save();
        canvas.concat(identityMatrix);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        canvas.restore();
        */
    }

    private void drawOverlay() {

        int linePosition = 0;

        // <FPS_LABEL>
        canvas.save();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(35);

        String fpsText = "FPS: " + (int) displayOutput.getFramesPerSecond();
        Rect fpsTextBounds = new Rect();
        paint.getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
        linePosition += 25 + fpsTextBounds.height();
        canvas.drawText(fpsText, 25, linePosition, paint);
        canvas.restore();
        // </FPS_LABEL>

        // <ENTITY_STATISTICS>
        canvas.save();
        int entityCount = Entity.Manager.size();
        int hostCount = Entity.Manager.filterType2(Host.class).size();
        int portCount = Entity.Manager.filterType2(Port.class).size();
//        int extensionCount = Entity.Manager.filterType2(ExtensionEntity.class).size();
        int extensionCount = Entity.Manager.filterWithComponent(Extension.class).size();
        int pathCount = Entity.Manager.filterType2(Path.class).size();

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
        // </ENTITY_STATISTICS>
    }

    /**
     * Adjust the perspective
     */
    private void adjustCamera() {
//        canvas.translate((float) originPosition.x + (float) space.getEntity().getActor(0).getCamera().getPosition().x /* + (float) Application.getView().getOrientationInput().getRotationY()*/, (float) originPosition.y + (float) space.getEntity().getActor(0).getCamera().getPosition().y /* - (float) Application.getView().getOrientationInput().getRotationX() */);
//        canvas.scale((float) space.getEntity().getActor(0).getCamera().getScale(), (float) space.getEntity().getActor(0).getCamera().getScale());
        Camera camera = getCamera();
        canvas.translate(
                (float) originPosition.x + (float) camera.getPosition().x /* + (float) Application.getView().getOrientationInput().getRotationY()*/,
                (float) originPosition.y + (float) camera.getPosition().y /* - (float) Application.getView().getOrientationInput().getRotationX() */
        );
        canvas.scale(
                (float) camera.getScale(),
                (float) camera.getScale()
        );
    }

    /**
     * Returns {@code Camera} {@code Entity}.
     * @return
     */
    private Camera getCamera() {
        Camera camera = (Camera) Entity.Manager.filterType2(Camera.class).get(0);
        return camera;
    }

    /**
     * The function run in background thread, not UI thread.
     */
    public void update() {

        if (space == null) {
            return;
        }

        Canvas canvas = null;

        SurfaceHolder holder = getHolder();

        try {
            canvas = holder.lockCanvas();

            if (canvas != null) {
                synchronized (holder) {


                    // <UPDATE>

                    // Update Actors
                    space.getActor().update(); // HACK

                    // Update
                    updateEntities();

                    // Update Camera(s)
                    Camera camera = (Camera) Entity.Manager.filterType2(Camera.class).get(0);
                    camera.update();
                    // </UPDATE>



                    // Draw
                    doDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public DisplayOutput getDisplayOutput() {
        return this.displayOutput;
    }

    public void setSpace(Space space) {
        this.space = space;

        // Get screen width and height of the device
        DisplayMetrics metrics = new DisplayMetrics();
        Application.getView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

//        space.getEntity().getActor(0).getCamera().setWidth(screenWidth);
//        space.getEntity().getActor(0).getCamera().setHeight(screenHeight);
        Camera camera = getCamera();
        camera.setWidth(screenWidth);
        camera.setHeight(screenHeight);
    }

    public Space getSpace() {
        return this.space;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (this.space == null) {
            return false;
        }

        // - Motion events contain information about all of the pointers that are currently active
        //   even if some of them have not moved since the getLastEvent event was delivered.
        //
        // - The index of pointers only ever changes by one as individual pointers go up and down,
        //   except when the gesture is canceled.
        //
        // - Use the getPointerId(int) method to obtain the pointer id of a pointer to track it
        //   across all subsequent motion events in a gesture. Then for successive motion events,
        //   use the findPointerIndex(int) method to obtain the pointer index for a given pointer
        //   id in that motion event.

        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId = motionEvent.getPointerId(pointerIndex);
        int touchInteractionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        final int pointerCount = motionEvent.getPointerCount();

        // Get active actor
        Actor actor = space.getActor();

        // Create pointerCoordinates event
        Event event = new Event();

        if (pointerCount <= Event.MAXIMUM_POINT_COUNT) {
            if (pointerIndex <= Event.MAXIMUM_POINT_COUNT - 1) {

                // Current
                // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId(i);
                    Transform perspectivePosition = actor.getCamera().getPosition();
                    double perspectiveScale = actor.getCamera().getScale();
                    event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originPosition.x + perspectivePosition.x)) / perspectiveScale;
                    event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originPosition.y + perspectivePosition.y)) / perspectiveScale;
                }

                // ACTION_DOWN is called only for the getFirstEvent pointer that touches the screen. This
                // starts the gesture. The pointer data for this pointer is always at index 0 in
                // the MotionEvent.
                //
                // ACTION_POINTER_DOWN is called for extra pointers that enter the screen beyond
                // the getFirstEvent. The pointer data for this pointer is at the index returned by
                // getActionIndex().
                //
                // ACTION_MOVE is sent when a change has happened during a press gesture for any
                // pointer.
                //
                // ACTION_POINTER_UP is sent when a non-primary pointer goes up.
                //
                // ACTION_UP is sent when the getLastEvent pointer leaves the screen.
                //
                // REFERENCES:
                // - https://developer.android.com/training/gestures/multi.html

                // Update the state of the touched object based on the current pointerCoordinates event state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    event.setType(Event.Type.SELECT);
                    event.pointerIndex = pointerId;
                    actor.queueEvent(event);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    event.setType(Event.Type.MOVE);
                    event.pointerIndex = pointerId;
                    actor.queueEvent(event);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    event.setType(Event.Type.UNSELECT);
                    event.pointerIndex = pointerId;
                    actor.queueEvent(event);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }

    // TODO: Remove reference to Image. WTF.
    public void updateEntities() {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            Entity entity = Entity.Manager.get(i);
//            Image image = entity.getComponent(Image.class);
//            if (image != null) {
////                image.draw(this);
//                image.update();
//            }
            entity.update();
        }
    }

    public void drawEntities() {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            Entity entity = Entity.Manager.get(i);
            Image image = entity.getComponent(Image.class);
            if (image != null) {
//                image.draw(this);
                drawEntity(entity);
            }
        }
    }

    public void drawEntity(Entity entity) {

        if (entity.getClass() == Host.class) {

            Image image = entity.getComponent(Image.class);
            if (image.isVisible()) {
                canvas.save();
                for (int i = 0; i < image.getShapes().size(); i++) {
                    image.getShapes().get(i).draw(this);
                }
                canvas.restore();
            }

        } else if (entity.hasComponent(Extension.class)) {

            Image image = entity.getComponent(Image.class);
            if (image.isVisible()) {
                canvas.save();
                for (int i = 0; i < image.getShapes().size(); i++) {
                    image.getShapes().get(i).draw(this);
                }
                canvas.restore();
            }

        } else if (entity.getClass() == Path.class) {

            Image image = entity.getComponent(Image.class);

            if (image.isVisible()) {
                Path path = (Path) image.getEntity();
                if (path.getType() == Path.Type.MESH) {
                    // Draw Path between Ports
                    drawTrianglePath(path, this);
                } else if (path.getType() == Path.Type.ELECTRONIC) {
                    drawLinePath(path, this);
                }
            } else {
                Path path = (Path) entity; // image.getPath();
                if (path.getType() == Path.Type.ELECTRONIC) {
                    drawPhysicalPath(path, this);
                }
            }

        }
    }

    // <PATH_IMAGE_HELPERS>
    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;

    public void drawTrianglePath(Path path, Display display) {

        Paint paint = display.paint;

        Shape sourcePortShape = Space.getSpace().getShape(path.getSource());
        Shape targetPortShape = Space.getSpace().getShape(path.getTarget());

        // Show target port
        targetPortShape.setVisibility(Visibility.VISIBLE);
        //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(Color.parseColor(sourcePortShape.getColor()));

        double pathRotation = Geometry.getAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
        Transform sourcePoint = Geometry.getRotateTranslatePoint(sourcePortShape.getPosition(), pathRotation, 2 * triangleSpacing);
        Transform targetPoint = Geometry.getRotateTranslatePoint(targetPortShape.getPosition(), pathRotation + 180, 2 * triangleSpacing);

        display.drawTrianglePath(sourcePoint, targetPoint, triangleWidth, triangleHeight);
    }

    public void drawLinePath(Path path, Display display) {

        Paint paint = display.paint;

        Shape sourcePortShape = Space.getSpace().getShape(path.getSource());
        Shape targetPortShape = Space.getSpace().getShape(path.getTarget());

        if (sourcePortShape != null && targetPortShape != null) {

            // Show target port
            targetPortShape.setVisibility(Visibility.VISIBLE);
            //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(Color.parseColor(sourcePortShape.getColor()));

            double pathRotationAngle = Geometry.getAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(sourcePortShape.getPosition(), pathRotationAngle, 0);
            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(targetPortShape.getPosition(), pathRotationAngle + 180, 0);

//            display.drawSegment(pathStartCoordinate, pathStopCoordinate);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
//            double pathRotation = getSpace().getImages(getPath().getHosts()).getRotation();

            Segment segment = (Segment) path.getComponent(Image.class).getShape("Path");
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortShape.getColor());

            segment.setSource(pathStartCoordinate);
            segment.setTarget(pathStopCoordinate);

            display.drawSegment(segment);
        }
    }

    public void drawPhysicalPath(Path path, Display display) {

        // Get Host and ExtensionEntity Ports
        Port hostPort = path.getSource();
        Port extensionPort = path.getTarget();

        // Draw the connection to the Host's Port

        Image hostImage = hostPort.getPortable().getComponent(Image.class);
        Image extensionImage = extensionPort.getPortable().getComponent(Image.class);

        PortableEntity host = (PortableEntity) hostImage.getEntity();
        Entity extension = extensionImage.getEntity();

        if (host.getComponent(Portable.class).headerContactPositions.size() > hostPort.getIndex()
                && extension.getComponent(Portable.class).headerContactPositions.size() > extensionPort.getIndex()) {

            Transform hostConnectorPosition = host.getComponent(Portable.class).headerContactPositions.get(hostPort.getIndex()).getPosition();
            Transform extensionConnectorPosition = extension.getComponent(Portable.class).headerContactPositions.get(extensionPort.getIndex()).getPosition();

            // Draw connection between Ports
            display.paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(extensionPort.getType())));
            display.paint.setStrokeWidth(10.0f);
//            display.drawSegment(hostConnectorPosition, extensionConnectorPosition);

//            Polyline polyline = new Polyline();
//            polyline.addVertex(hostConnectorPosition);
//            polyline.addVertex(extensionConnectorPosition);
//            display.drawPolyline(polyline);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment segment = (Segment) path.getComponent(Image.class).getShape("Path");
            segment.setOutlineThickness(10.0);
            segment.setOutlineColor(camp.computer.clay.util.Color.getColor(extensionPort.getType()));

            segment.setSource(hostConnectorPosition);
            segment.setTarget(extensionConnectorPosition);

            display.drawSegment(segment);
        }
    }
    // </PATH_IMAGE_HELPERS>

    public void drawSegment(Transform source, Transform target) {
        canvas.drawLine((float) source.x, (float) source.y, (float) target.x, (float) target.y, paint);
    }

    public void drawSegment(Segment segment) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(segment.outlineColorCode);
        paint.setStrokeWidth((float) segment.getOutlineThickness());

        // Color
        canvas.drawLine((float) segment.getSource().x, (float) segment.getSource().y, (float) segment.getTarget().x, (float) segment.getTarget().y, paint);
    }

    public void drawPolyline(Polyline polyline) {
        drawPolyline(polyline.getPoints());
    }

    // TODO: Refactor with transforms
    public void drawPolyline(List<Transform> vertices) {

        for (int i = 0; i < vertices.size() - 1; i++) {

            canvas.drawLine(
                    (float) vertices.get(i).x,
                    (float) vertices.get(i).y,
                    (float) vertices.get(i + 1).x,
                    (float) vertices.get(i + 1).y,
                    paint
            );
        }
    }

    public void drawCircle(Circle circle) {

        canvas.save();

        canvas.translate((float) circle.getPosition().x, (float) circle.getPosition().y);
        canvas.rotate((float) circle.getPosition().rotation);

        // Fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circle.colorCode);
        canvas.drawCircle(0, 0, (float) circle.radius, paint);

        // Outline
        if (circle.getOutlineThickness() > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(circle.outlineColorCode);
            paint.setStrokeWidth((float) circle.outlineThickness);

            canvas.drawCircle(0, 0, (float) circle.radius, paint);
        }

        canvas.restore();
    }

    public void drawCircle(Transform position, double radius, double angle) {

        canvas.save();

        canvas.translate((float) position.x, (float) position.y);
        canvas.rotate((float) angle);

        canvas.drawCircle(0.0f, 0.0f, (float) radius, paint);

        canvas.restore();

    }

    public void drawRectangle(Rectangle rectangle) {

        // Set style
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rectangle.colorCode);

        canvas.save();
        canvas.translate((float) rectangle.getPosition().x, (float) rectangle.getPosition().y);
        canvas.rotate((float) rectangle.getRotation());

        canvas.drawRoundRect(
                (float) (0 - (rectangle.width / 2.0)),
                (float) (0 - (rectangle.height / 2.0)),
                (float) (0 + (rectangle.width / 2.0)),
                (float) (0 + (rectangle.height / 2.0)),
                (float) rectangle.cornerRadius,
                (float) rectangle.cornerRadius,
                paint
        );

        // Draw Points in Shape
        if (rectangle.getOutlineThickness() > 0) {

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(rectangle.outlineColorCode);
            paint.setStrokeWidth((float) rectangle.outlineThickness);

            canvas.drawRoundRect(
                    (float) (0 - (rectangle.width / 2.0)),
                    (float) (0 - (rectangle.height / 2.0)),
                    (float) (0 + (rectangle.width / 2.0)),
                    (float) (0 + (rectangle.height / 2.0)),
                    (float) rectangle.cornerRadius,
                    (float) rectangle.cornerRadius,
                    paint
            );
        }

        canvas.restore();
    }

    public void drawRectangle(Transform position, double angle, double width, double height) {

        canvas.save();

        canvas.translate((float) position.x, (float) position.y);
        canvas.rotate((float) angle);

        canvas.drawRect(
                (float) (0 - (width / 2.0f)),
                (float) (0 - (height / 2.0f)),
                (float) (0 + (width / 2.0f)),
                (float) (0 + (height / 2.0f)),
                paint
        );

        canvas.restore();
    }

    public void drawText(Transform position, String text, double size) {

        // Style
        paint.setTextSize((float) size);

        // Style (Guaranteed)
        text = text.toUpperCase();
        paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, (float) position.x, (float) position.y + bounds.height() / 2.0f, paint);
    }

    public void drawTrianglePath(Transform startPosition, Transform stopPosition, double triangleWidth, double triangleHeight) {

        double pathRotationAngle = Geometry.getAngle(startPosition, stopPosition);

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        double pathDistance = Geometry.distance(startPosition, stopPosition);

        int triangleCount = (int) (pathDistance / (triangleHeight + 15));
        double triangleSpacing2 = pathDistance / triangleCount;

        for (int k = 0; k <= triangleCount; k++) {

            // Calculate triangle position
            Transform triangleCenterPosition = Geometry.getRotateTranslatePoint(startPosition, pathRotationAngle, k * triangleSpacing2);

            paint.setStyle(Paint.Style.FILL);
            drawTriangle(triangleCenterPosition, triangleRotationAngle, triangleWidth, triangleHeight);
        }
    }

    // TODO: Refactor with transforms
    public void drawPolygon(Polygon polygon) {
        drawPolygon(polygon.getBoundary());
    }

    // TODO: Refactor with transforms
    public void drawPolygon(List<Transform> vertices) {

        // Draw vertex Points in Shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) vertices.get(0).x, (float) vertices.get(0).y);
        for (int i = 1; i < vertices.size(); i++) {
            path.lineTo((float) vertices.get(i).x, (float) vertices.get(i).y);
        }
//        path.lineTo((float) boundary.get(0).x, (float) boundary.get(0).y);
        path.close();

        canvas.drawPath(path, paint);
    }

    // TODO: Refactor with transforms
    public void drawTriangle(Triangle triangle) {
        // TODO:
    }

    // TODO: Refactor with transforms
    public void drawTriangle(Transform position, double angle, double width, double height) {

        // Calculate pointerCoordinates before rotation
        Transform p1 = new Transform(position.x + -(width / 2.0f), position.y + (height / 2.0f));
        Transform p2 = new Transform(position.x + 0, position.y - (height / 2.0f));
        Transform p3 = new Transform(position.x + (width / 2.0f), position.y + (height / 2.0f));

        // Calculate pointerCoordinates after rotation
        Transform rp1 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p1), Geometry.distance(position, p1));
        Transform rp2 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p2), Geometry.distance(position, p2));
        Transform rp3 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p3), Geometry.distance(position, p3));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rp1.x, (float) rp1.y);
        path.lineTo((float) rp2.x, (float) rp2.y);
        path.lineTo((float) rp3.x, (float) rp3.y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
