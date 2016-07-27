package camp.computer.clay.app;

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

import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.sim.Body;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.sim.Frame;
import camp.computer.clay.model.sim.Path;
import camp.computer.clay.model.sim.Port;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.arch.Viz;
import camp.computer.clay.viz.img.old_FrameImage;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Palette;
import camp.computer.clay.viz.util.Point;
import camp.computer.clay.viz.util.Rectangle;
import camp.computer.clay.viz.arch.Shape;

public class Surface extends SurfaceView implements SurfaceHolder.Callback {

    // Viz Rendering Context
    private Bitmap canvasBitmap = null;
    //    private Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    //    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Viz Renderer
    private SurfaceHolder surfaceHolder;
    private Renderer renderer;

    // Coordinate System (Grid)
    private Point originPosition = new Point();

    // Viz
    private Viz viz;

    public Surface(Context context) {
        super(context);

        setFocusable(true);
    }

    public Surface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Surface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Palette palette = new Palette();

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        canvasWidth = getWidth();
        canvasHeight = getHeight();

        canvasBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);

        palette.getCanvas().setBitmap(canvasBitmap);

        identityMatrix = new Matrix();

        // Center the canvas on the display
        originPosition.set(palette.getCanvas().getWidth() / 2.0f, palette.getCanvas().getHeight() / 2.0f);
//        originPosition.set(0, 0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        // Kill the background Thread
//        boolean retry = true;
//        // renderer.setRunning (false);
//        while (retry) {
//            try {
//                renderer.join ();
//                retry = false;
//            } catch (InterruptedException e) {
//                e.printStackTrace ();
//            }
//        }
    }

    public void onResume() {
        // Log.v("MapView", "onResume");

        surfaceHolder = getHolder();
        getHolder().addCallback(this);

        // Create and start background Thread
        renderer = new Renderer(this);
        renderer.setRunning(true);
        renderer.start();

//        // Start communications
//        getClay ().getCommunication ().startDatagramServer();

        // Remove this?
        generate();

    }

    public void onPause() {
        // Log.v("MapView", "onPause");

        // Pause the communications
//        getClay ().getCommunication ().stopDatagramServer (); // HACK: This was commented out to prevent the server from "crashing" into an invalid state!

        // Kill the background Thread
        boolean retry = true;
        renderer.setRunning(false);

        while (retry) {
            try {
                renderer.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doDraw(Canvas canvas) {
        setCanvas(canvas);

        if (this.viz == null || this.palette.getCanvas() == null) {
            return;
        }

        // <PERSPECTIVE>
        // Adjust the perspective
        canvas.save();
        canvas.translate(
//                originPosition.x + viz.getSimulation().getBody(0).getPerspective().getPosition().x + (float) Application.getDisplay().getSensorAdapter().getRotationY(),
//                originPosition.y + viz.getSimulation().getBody(0).getPerspective().getPosition().y - (float) Application.getDisplay().getSensorAdapter().getRotationX()
                (float) originPosition.getX() + (float) viz.getSimulation().getBody(0).getPerspective().getPosition().getX(),
                (float) originPosition.getY() + (float) viz.getSimulation().getBody(0).getPerspective().getPosition().getY()
        );
        // this.canvas.rotate((float) ApplicationView.getDisplay().getSensorAdapter().getRotationZ());
        canvas.scale(
                (float) viz.getSimulation().getBody(0).getPerspective().getScale(),
                (float) viz.getSimulation().getBody(0).getPerspective().getScale()
        );
        // </PERSPECTIVE>

        // TODO: Get Simulation
        // TODO: Get Simulation's selected Viz

        // Draw the background
        canvas.drawColor(Color.WHITE);

        // Scene
        renderViz(getViz());

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap(canvasBitmap, identityMatrix, null);

        canvas.restore();
    }

    private void renderViz(Viz viz) {

        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
            // <AXES_ANNOTATION>
            getPaint().setColor(Color.CYAN);
            getPaint().setStrokeWidth(1.0f);
            getCanvas().drawLine(-1000, 0, 1000, 0, getPaint());
            getCanvas().drawLine(0, -1000, 0, 1000, getPaint());
            // </AXES_ANNOTATION>
        }

        for (Image<Frame> image : viz.getImages().filterType(Frame.class).getList()) {
            if (image.isVisible()) {
                for (int i = 0; i < image.getShapes().size(); i++) {
                    Shape shape = image.getShape(i);

                    if (shape.isVisible()) {

                        getCanvas().save();

                        getPaint().setStyle(Paint.Style.FILL);
                        if (shape.hasStyle("color")) {
                            getPaint().setColor(Color.parseColor(shape.getStyle("color")));
                        }
                        shape.draw(viz);

                        if (shape.hasStyle("outlineThickness")) {
                            if (Double.parseDouble(shape.getStyle("outlineThickness")) > 0) {
                                getPaint().setStyle(Paint.Style.STROKE);
                                getPaint().setStrokeWidth((float) Double.parseDouble(shape.getStyle("outlineThickness")));

                                if (shape.hasStyle("outlineColor")) {
                                    getPaint().setColor(Color.parseColor(shape.getStyle("outlineColor")));
                                }

                                shape.draw(viz);
                            }
                        }

                        getCanvas().restore();

                    }
                }
            }
        }

        for (Image<Port> image : viz.getImages().filterType(Port.class).getList()) {
            if (image.isVisible()) {
                for (int i = 0; i < image.getShapes().size(); i++) {
                    Shape shape = image.getShape(i);

                    if (shape.isVisible()) {

                        getCanvas().save();

                        getPaint().setStyle(Paint.Style.FILL);
                        if (shape.hasStyle("color")) {
                            getPaint().setColor(Color.parseColor(shape.getStyle("color")));
                        }
                        shape.draw(viz);

                        if (shape.hasStyle("outlineThickness")) {
                            if (Double.parseDouble(shape.getStyle("outlineThickness")) > 0) {
                                getPaint().setStyle(Paint.Style.STROKE);
                                getPaint().setStrokeWidth((float) Double.parseDouble(shape.getStyle("outlineThickness")));

                                if (shape.hasStyle("outlineColor")) {
                                    getPaint().setColor(Color.parseColor(shape.getStyle("outlineColor")));
                                }

                                shape.draw(viz);
                            }
                        }

                        getCanvas().restore();

                    }
                }
            }

            // Draw paths
            for (Path path : image.getModel().getPaths()) {

                getPaint().setStyle(Paint.Style.FILL);
                getPaint().setColor(Color.BLACK);

                viz.drawTrianglePath(
                        viz.getImage(path.getSource()).getPosition(),
                        viz.getImage(path.getTarget()).getPosition(),
                        10,
                        10
                );

            }
        }

//        // Draw images
//        for (Integer id : viz.getLayerIndices()) {
//            Layer layer = viz.getLayer(id);
//            if (layer == null) {
//                break;
//            }
//            for (Image image : layer.getImages()) {
////                Log.v("Image", "drawing image class: " + image.getModel().getClass());
//
////                if (image.getModel().getClass() == Frame.class) {
////                    Image<Frame> img = (Image<Frame>) image;
////                    img.draw(visualizationSurface);
////                } else if (image.getModel().getClass() == Frame.class) {
////                    Image<Port> img = (Image<Port>) image;
////                    img.draw(visualizationSurface);
////                }
//
//                if (image.isVisible()) {
//                    getCanvas().save();
//
////                    getCanvas().translate(
////                            (float) image.getPosition().getX(),
////                            (float) image.getPosition().getY()
//////                            (float) image.getPosition().getAbsoluteX(),
//////                            (float) image.getPosition().getAbsoluteY()
////                    );
//
//                    //for (Shape shape : image.getShapes()) {
//                    for (int i = 0; i < image.getShapes().size(); i++) {
//
//                        Shape shape = image.getShape(i);
//
//                        if (shape.isVisible()) {
//
//                            getCanvas().save();
//
////                            getCanvas().translate(
////                                    (float) -shape.getPosition().getX(),
////                                    (float) -shape.getPosition().getY()
////                            );
//
//                            getPaint().setStyle(Paint.Style.FILL);
//                            if (shape.hasStyle("color")) {
//                                getPaint().setColor(Color.parseColor(shape.getStyle("color")));
//                            }
//                            shape.draw(viz);
//
//                            if (shape.hasStyle("outlineThickness")) {
//                                if (Double.parseDouble(shape.getStyle("outlineThickness")) > 0) {
//                                    getPaint().setStyle(Paint.Style.STROKE);
//                                    getPaint().setStrokeWidth((float) Double.parseDouble(shape.getStyle("outlineThickness")));
//
//                                    if (shape.hasStyle("outlineColor")) {
//                                        getPaint().setColor(Color.parseColor(shape.getStyle("outlineColor")));
//                                    }
//
//                                    shape.draw(viz);
//                                }
//                            }
//
//                            getCanvas().restore();
//                        }
//                    }
//
//                    //image.draw(viz);
//
//                    getCanvas().restore();
//                }
//            }
//        }

        //Geometry.computeCirclePacking(getFrameImages(), 200, getImages().old_filterType(old_FrameImage.TYPE).calculateCentroid());

        // Draw annotations
        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {

            // <FPS_ANNOTATION>
            Point fpsPosition = viz.getImages().old_filterType(old_FrameImage.TYPE).calculateCenter();
            fpsPosition.setY(fpsPosition.getY() - 200);
            getPaint().setColor(Color.RED);
            getPaint().setStyle(Paint.Style.FILL);
            getCanvas().drawCircle((float) fpsPosition.getX(), (float) fpsPosition.getY(), 10, getPaint());

            getPaint().setStyle(Paint.Style.FILL);
            getPaint().setTextSize(35);

            String fpsText = "FPS: " + (int) getRenderer().getFramesPerSecond();
            Rect fpsTextBounds = new Rect();
            getPaint().getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
            getCanvas().drawText(fpsText, (float) fpsPosition.getX() + 20, (float) fpsPosition.getY() + fpsTextBounds.height() / 2.0f, getPaint());
            // </FPS_ANNOTATION>

            // <CENTROID_ANNOTATION>
            Point centroidPosition = viz.getImages().old_filterType(old_FrameImage.TYPE).calculateCentroid();
            getPaint().setColor(Color.RED);
            getPaint().setStyle(Paint.Style.FILL);
            getCanvas().drawCircle((float) centroidPosition.getX(), (float) centroidPosition.getY(), 10, getPaint());

            getPaint().setStyle(Paint.Style.FILL);
            getPaint().setTextSize(35);

            String text = "CENTROID";
            Rect bounds = new Rect();
            getPaint().getTextBounds(text, 0, text.length(), bounds);
            getCanvas().drawText(text, (float) centroidPosition.getX() + 20, (float) centroidPosition.getY() + bounds.height() / 2.0f, getPaint());
            // </CENTROID_ANNOTATION>

            // <CENTROID_ANNOTATION>
            List<Point> frameImagePositions = viz.getImages().old_filterType(old_FrameImage.TYPE).getPositions();
            Point frameImagesCenterPosition = Geometry.calculateCenter(frameImagePositions);
            getPaint().setColor(Color.RED);
            getPaint().setStyle(Paint.Style.FILL);
            getCanvas().drawCircle((float) frameImagesCenterPosition.getX(), (float) frameImagesCenterPosition.getY(), 10, getPaint());

            getPaint().setStyle(Paint.Style.FILL);
            getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            getCanvas().drawText(centerLabeltext, (float) frameImagesCenterPosition.getX() + 20, (float) frameImagesCenterPosition.getY() + centerLabelTextBounds.height() / 2.0f, getPaint());
            // </CENTROID_ANNOTATION>

            // <CONVEX_HULL>
            List<Point> framePositions = viz.getImages().old_filterType(old_FrameImage.TYPE).getPositions();
            List<Point> convexHullVertices = Geometry.computeConvexHull(framePositions);

            getPaint().setStrokeWidth(1.0f);
            getPaint().setColor(Color.RED);
            getPaint().setStyle(Paint.Style.STROKE);

            for (int i = 0; i < convexHullVertices.size() - 1; i++) {
                viz.getPalette().drawShape(convexHullVertices);
            }
            // </CONVEX_HULL>

            // <BOUNDING_BOX>
            getPaint().setStrokeWidth(1.0f);
            getPaint().setColor(Color.RED);
            getPaint().setStyle(Paint.Style.STROKE);

            Rectangle boundingBox = viz.getImages().old_filterType(old_FrameImage.TYPE).calculateBoundingBox();
            viz.getPalette().drawShape(boundingBox.getVertices());
            // </BOUNDING_BOX>
        }

    }

    /**
     * The function run in background thread, not UI thread.
     */
    public void generate() {

        if (viz == null) {
            return;
        }

        Canvas canvas = null;

        try {
            canvas = getHolder().lockCanvas();

            if (canvas != null) {
                synchronized (getHolder()) {

                    // Update
                    viz.generate();

                    // Draw
                    doDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    private void setCanvas(Canvas canvas) {
        palette.setCanvas(canvas);
    }

    public Canvas getCanvas() {
        return this.palette.getCanvas();
    }

    public Paint getPaint() {
        return this.palette.getPaint();
    }

    public void setViz(Viz viz) {

        viz.setPalette(palette);

        this.viz = viz;

        // Get screen width and height of the device
        DisplayMetrics metrics = new DisplayMetrics();
        Application.getDisplay().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        viz.getSimulation().getBody(0).getPerspective().setWidth(screenWidth);
        viz.getSimulation().getBody(0).getPerspective().setHeight(screenHeight);
    }

    public Viz getViz() {
        return this.viz;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // - Motion events contain information about all of the pointers that are currently active
        //   even if some of them have not moved since the last event was delivered.
        //
        // - The number of pointers only ever changes by one as individual pointers go up and down,
        //   except when the gesture is canceled.
        //
        // - Use the getPointerId(int) method to obtain the pointer id of a pointer to track it
        //   across all subsequent motion events in a gesture. Then for successive motion events,
        //   use the findPointerIndex(int) method to obtain the pointer index for a given pointer
        //   id in that motion event.

        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId = motionEvent.getPointerId(pointerIndex);
        int touchInteractionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        int pointerCount = motionEvent.getPointerCount();

        if (this.viz == null) {
            return false;
        }

        // Get active body
        Body currentBody = viz.getSimulation().getBody(0);

        // Create touchPositions interaction
        TouchInteraction touchInteraction = new TouchInteraction(OnTouchActionListener.Type.NONE);
        touchInteraction.setBody(currentBody);

        if (pointerCount <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT) {
            if (pointerIndex <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touchPositions state based the points given by the host OS (e.g., Android).
                Point perspectivePosition = viz.getSimulation().getBody(0).getPerspective().getPosition();
                double perspectiveScale = viz.getSimulation().getBody(0).getPerspective().getScale();
                for (int i = 0; i < pointerCount; i++) {
                    int otherPointerId = motionEvent.getPointerId(i);
                    touchInteraction.getPosition(otherPointerId).set(
//                            motionEvent.getX(i) - (originPosition.getX() + perspectivePosition.getX()) / perspectiveScale,
//                            motionEvent.getY(i) - (originPosition.getY() + perspectivePosition.getY()) / perspectiveScale
                            motionEvent.getX(i) - (originPosition.getX() + perspectivePosition.getX()) / perspectiveScale,
                            motionEvent.getY(i) - (originPosition.getY() + perspectivePosition.getY()) / perspectiveScale
                    );
                }

                Log.v("Touch", "-");

                Log.v("Touch", "Surface.Touch: " + motionEvent.getX(pointerIndex) + ", " + motionEvent.getY(pointerIndex));

                // ACTION_DOWN is called only for the first pointer that touches the screen. This
                // starts the gesture. The pointer data for this pointer is always at index 0 in
                // the MotionEvent.
                //
                // ACTION_POINTER_DOWN is called for extra pointers that enter the screen beyond
                // the first. The pointer data for this pointer is at the index returned by
                // getActionIndex().
                //
                // ACTION_MOVE is sent when a change has happened during a press gesture for any
                // pointer.
                //
                // ACTION_POINTER_UP is sent when a non-primary pointer goes up.
                //
                // ACTION_UP is sent when the last pointer leaves the screen.
                //
                // REFERENCES:
                // - https://developer.android.com/training/gestures/multi.html

                // Update the state of the touched object based on the current touchPositions interaction state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    touchInteraction.pointerIndex = pointerId;
                    currentBody.onStartInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    touchInteraction.pointerIndex = pointerId;
                    currentBody.onContinueInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    touchInteraction.pointerIndex = pointerId;
                    currentBody.onCompleteInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }
}
