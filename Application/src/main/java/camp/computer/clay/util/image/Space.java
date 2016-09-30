package camp.computer.clay.util.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.model.Extension;
import camp.computer.clay.model.Group;
import camp.computer.clay.model.Host;
import camp.computer.clay.model.Model;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Camera;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PathImage;
import camp.computer.clay.util.Probability;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.util.ImageGroup;
import camp.computer.clay.util.image.util.ShapeGroup;

public class Space extends Image<Model> {

    protected ImageGroup images = new ImageGroup();

    protected Visibility prototypeExtensionVisibility = new Visibility(Visibility.Value.INVISIBLE);
    protected Point prototypeExtensionPosition = new Point();

    protected Visibility prototypePathVisibility = new Visibility(Visibility.Value.INVISIBLE);
    protected Point prototypePathSourcePosition = new Point(0, 0);
    protected Point prototypePathDestinationCoordinate = new Point(0, 0);

    public Space(Model model) {
        super(model);
        setup();
    }

    private void setup() {
        setupActions();
    }

    public Visibility goalVisibility = new Visibility(Visibility.Value.INVISIBLE);

    private void setupActions() {
        // Setup interactivity
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();
                Camera camera = event.getActor().getCamera();

                if (event.getType() == Event.Type.NONE) {
                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {
//                    if (action.isHolding()) {
//                        // Space
//                        //lastEvent.getTargetImage().processAction(action);
//                    } else if (action.isDragging()) {
                    // Camera
//                    if (action.getSize() > 1) {
//                        camera.setOffset(event.getPosition().getX() - action.getFirstEvent().getPosition().getX(), event.getPosition().getY() - action.getFirstEvent().getPosition().getY());
//                    }

                    camera.setOffset(action.getOffset().getX(), action.getOffset().getY());
//                    }
                } else if (event.getType() == Event.Type.UNSELECT) {
                    if (action.isTap()) {

                        // Camera
                        camera.setFocus(getSpace());
                    }
                }
            }
        });
    }

    public Model getModel() {
        return getEntity();
    }

    public <T extends Entity> void addEntity(T entity) {
        if (entity instanceof Host) {

            Host host = (Host) entity;

            // Create PhoneHost Image
            HostImage hostImage = new HostImage(host);

            // Create Port Shapes for each of the PhoneHost's Ports
            for (int i = 0; i < host.getPorts().size(); i++) {
                Port port = host.getPorts().get(i);
                addEntity(port);
            }

            // Add PhoneHost Image to Space
            addImage(hostImage);

        } else if (entity instanceof Extension) {

            Extension extension = (Extension) entity;

            // Create Extension Image
            ExtensionImage extensionImage = new ExtensionImage(extension);

            // Create Port Shapes for each of the Extension's Ports
            for (int i = 0; i < extension.getPorts().size(); i++) {
                Port port = extension.getPorts().get(i);
                addEntity(port);
            }

            // Add Extension Image to Space
            addImage(extensionImage);

        } else if (entity instanceof Port) {

//            Port port = (Port) entity;

        } else if (entity instanceof Path) {

            Path path = (Path) entity;

            // Create Path Image
            PathImage pathImage = new PathImage(path);

            // Add Path Image to Space
            addImage(pathImage);
        }
    }

    // TODO: Remove Image parameter. Create that and return it.
    private <T extends Image> void addImage(T image) {

        // Add Image
        image.setSpace(this);
        if (!images.contains(image)) {
            images.add(image);
        }

        // Position the Image
        // <HACK>
        if (image instanceof HostImage) {
            adjustLayout(image);
        }
        // </HACK>

        // Update Camera
        getEntity().getActor(0).getCamera().setFocus(getSpace());
    }

    // TODO: Use base class's addImage() so Shapes are added to super.shapes. Then add an index instead of layers?

    /**
     * Automatically determines and assigns a valid position for the specified {@code Image}.
     *
     * @param image The {@code Image} for which the position will be adjusted.
     */
    private void adjustLayout(Image image) {
        int adjustmentMethod = 1;

        if (adjustmentMethod == 0) {

            // Calculate random positions separated by minimum distance
            final float imageSeparationDistance = 550; // 500;

            List<Point> imageCoordinates = getImages().filterType(Host.class).getPositions();

            Point position = null;
            boolean foundPoint = false;

            Log.v("Coordinate", "imageCoordinates.size = " + imageCoordinates.size());

            if (imageCoordinates.size() == 0) {
                position = new Point(0, 0);
            } else if (imageCoordinates.size() == 1) {
                position = Geometry.calculatePoint(imageCoordinates.get(0), Probability.generateRandomInteger(0, 360), imageSeparationDistance);
            } else {
                List<Point> hullPoints = Geometry.computeConvexHull(imageCoordinates);

                int sourceIndex = Probability.generateRandomInteger(0, hullPoints.size() - 1);
                int targetIndex = sourceIndex + 1;

                Point midpoint = Geometry.calculateMidpoint(hullPoints.get(sourceIndex), hullPoints.get(targetIndex));
                position = Geometry.calculatePoint(midpoint, Geometry.calculateRotationAngle(hullPoints.get(sourceIndex), hullPoints.get(targetIndex)) + 90, imageSeparationDistance);
            }

            // Assign the found position to the image
            image.setPosition(position);
            image.setRotation(Probability.getRandomGenerator().nextInt(360));
        }

        if (adjustmentMethod == 1) {

            ImageGroup imageCoordinates = getImages().filterType(Host.class);

            // Set position
            if (imageCoordinates.size() == 1) {
                imageCoordinates.get(0).setPosition(new Point(0, 0));
            } else if (imageCoordinates.size() == 2) {
                imageCoordinates.get(0).setPosition(new Point(-300, 0));
                imageCoordinates.get(1).setPosition(new Point(300, 0));
            } else if (imageCoordinates.size() == 5) {
                imageCoordinates.get(0).setPosition(new Point(-300, -600));
                imageCoordinates.get(0).setRotation(0);
                imageCoordinates.get(1).setPosition(new Point(300, -600));
                imageCoordinates.get(1).setRotation(20);
                imageCoordinates.get(2).setPosition(new Point(-300, 0));
                imageCoordinates.get(2).setRotation(40);
                imageCoordinates.get(3).setPosition(new Point(300, 0));
                imageCoordinates.get(3).setRotation(60);
                imageCoordinates.get(4).setPosition(new Point(-300, 600));
                imageCoordinates.get(4).setRotation(80);
            }

            // Set rotation
            image.setRotation(Probability.getRandomGenerator().nextInt(360));
        }
    }

    /**
     * Returns {@code true} if the {@code Space} contains a {@code Image} corresponding to the
     * specified {@code Entity}.
     *
     * @param entity The {@code Entity} for which the corresponding {@code Image} will be
     *               returned, if any.
     * @return The {@code Image} corresponding to the specified {@code Entity}, if one is
     * present. If one is not present, this method returns {@code null}.
     */
    public boolean contains(Entity entity) {
        return images.filterEntity(entity).size() > 0;
    }

    public Image getImage(Entity entity) {
        return images.filterEntity(entity).get(0);
    }

    public <T extends Entity> ImageGroup getImages(Group<T> entities) {
        return images.filterEntity(entities);
    }

    public ImageGroup getImages() {
        return images;
    }

    public <T extends Entity> ImageGroup getImages(Class<?>... entityTypes) {
        return images.filterType(entityTypes);
    }

    // TODO: Delete. Replace with ImageGroup.filterPosition(Point)
    public Image getImage(Point point) {
        ImageGroup image = images.filterVisibility(Visibility.Value.VISIBLE).filterContains(point);
        if (image.size() > 0) {
            return image.get(0);
        } else {
            return this;
        }
    }

    public ShapeGroup getShapes() {
//        ShapeGroup shapeGroup = new ShapeGroup();
//        ImageGroup imageList = getImages();
//        for (int i = 0; i < imageList.size(); i++) {
//            shapeGroup.add(imageList.get(i).getShapes());
//        }
//        return shapeGroup;
        return images.getShapes();
    }

    // TODO: Refactor to be cleaner and leverage other classes...
    public <T extends Entity> ShapeGroup getShapes(Class<? extends Entity>... entityTypes) {
        ShapeGroup shapeGroup = new ShapeGroup();
        ImageGroup imageList = getImages();

        for (int i = 0; i < imageList.size(); i++) {
            shapeGroup.add(imageList.get(i).getShapes(entityTypes));
        }

        return shapeGroup.filterType(entityTypes);
    }

    public Shape getShape(Entity entity) {
        ImageGroup images = getImages();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            Shape shape = image.getShape(entity);
            if (shape != null) {
                return shape;
            }
        }
        return null;
    }

    public Model getEntity() {
        return this.entity;
    }

    // TODO: Delete this! It is redundant...
    public static <T extends Image> List<Point> getPositions(List<T> images) {
        List<Point> positions = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            T figure = images.get(i);
            positions.add(figure.getPosition());
        }
        return positions;
    }

    @Override
    public void update() {
        // Update perspective
        getEntity().getActor(0).getCamera().update();

        for (int i = 0; i < images.size(); i++) {
            images.get(i).update();
        }

        // Update figure layout
        // Geometry.computeCirclePacking(getImages().filterType(HostImage.class, ExtensionImage.class).getList(), 200, getImages().filterType(HostImage.class, ExtensionImage.class).getCentroidPosition());
    }

    @Override
    public void draw(Display display) {
        // <DEBUG_LABEL>
        if (Application.ENABLE_GEOMETRY_LABELS) {
            // <AXES_LABEL>
            display.getPaint().setColor(Color.CYAN);
            display.getPaint().setStrokeWidth(1.0f);
            display.getCanvas().drawLine(-5000, 0, 5000, 0, display.getPaint());
            display.getCanvas().drawLine(0, -5000, 0, 5000, display.getPaint());
            // </AXES_LABEL>
        }
        // </DEBUG_LABEL>

        // Draw
        for (int i = 0; i < images.size(); i++) {
            images.get(i).draw(display);
        }

        // Draw any prototype Paths and Extensions
        drawPrototypePath(display);
        drawPrototypeExtension(display);

        // <DEBUG_LABEL>
        if (Application.ENABLE_GEOMETRY_LABELS) {

            // <FPS_LABEL>
            Point fpsCoordinate = getImages().filterType(Host.class).getCenterPoint();
            fpsCoordinate.setY(fpsCoordinate.getY() - 200);
            display.getPaint().setColor(Color.RED);
            display.getPaint().setStyle(Paint.Style.FILL);
            display.getCanvas().drawCircle((float) fpsCoordinate.getX(), (float) fpsCoordinate.getY(), 10, display.getPaint());

            display.getPaint().setStyle(Paint.Style.FILL);
            display.getPaint().setTextSize(35);

            String fpsText = "FPS: " + (int) display.getDisplayOutput().getFramesPerSecond();
            Rect fpsTextBounds = new Rect();
            display.getPaint().getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
            display.getCanvas().drawText(fpsText, (float) fpsCoordinate.getX() + 20, (float) fpsCoordinate.getY() + fpsTextBounds.height() / 2.0f, display.getPaint());
            // </FPS_LABEL>

            // <CENTROID_LABEL>
            Point centroidCoordinate = getImages().filterType(Host.class).getCentroidPoint();
            display.getPaint().setColor(Color.RED);
            display.getPaint().setStyle(Paint.Style.FILL);
            display.getCanvas().drawCircle((float) centroidCoordinate.getX(), (float) centroidCoordinate.getY(), 10, display.getPaint());

            display.getPaint().setStyle(Paint.Style.FILL);
            display.getPaint().setTextSize(35);

            String text = "CENTROID";
            Rect bounds = new Rect();
            display.getPaint().getTextBounds(text, 0, text.length(), bounds);
            display.getCanvas().drawText(text, (float) centroidCoordinate.getX() + 20, (float) centroidCoordinate.getY() + bounds.height() / 2.0f, display.getPaint());
            // </CENTROID_LABEL>

            // <CENTER_LABEL>
            List<Point> figureCoordinates = getImages().filterType(Host.class, Extension.class).getPositions();
            Point baseImagesCenterCoordinate = Geometry.calculateCenter(figureCoordinates);
            display.getPaint().setColor(Color.RED);
            display.getPaint().setStyle(Paint.Style.FILL);
            display.getCanvas().drawCircle((float) baseImagesCenterCoordinate.getX(), (float) baseImagesCenterCoordinate.getY(), 10, display.getPaint());

            display.getPaint().setStyle(Paint.Style.FILL);
            display.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            display.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            display.getCanvas().drawText(centerLabeltext, (float) baseImagesCenterCoordinate.getX() + 20, (float) baseImagesCenterCoordinate.getY() + centerLabelTextBounds.height() / 2.0f, display.getPaint());
            // </CENTER_LABEL>

            // <CONVEX_HULL_LABEL>
            List<Point> baseVertices = getImages().filterType(Host.class, Extension.class).getVertices();

            // Hull vertices
            for (int i = 0; i < baseVertices.size() - 1; i++) {
                display.getPaint().setStrokeWidth(1.0f);
                display.getPaint().setColor(Color.parseColor("#FF2828"));
                display.getPaint().setStyle(Paint.Style.FILL);

                Point baseVertex = baseVertices.get(i);
                Display.drawCircle(baseVertex, 5, 0, display);
            }

            List<Point> convexHullVertices = Geometry.computeConvexHull(baseVertices);

            display.getPaint().setStrokeWidth(1.0f);
            display.getPaint().setColor(Color.parseColor("#2D92FF"));
            display.getPaint().setStyle(Paint.Style.STROKE);

            // Hull edges
            Display.drawPolygon(convexHullVertices, display);

            // Hull vertices
            for (int i = 0; i < convexHullVertices.size() - 1; i++) {
                display.getPaint().setStrokeWidth(1.0f);
                display.getPaint().setColor(Color.parseColor("#FF2828"));
                display.getPaint().setStyle(Paint.Style.STROKE);

                Point vertex = convexHullVertices.get(i);
                Display.drawCircle(vertex, 20, 0, display);
            }
            // </CONVEX_HULL_LABEL>

            // <BOUNDING_BOX_LABEL>
            display.getPaint().setStrokeWidth(1.0f);
            display.getPaint().setColor(Color.RED);
            display.getPaint().setStyle(Paint.Style.STROKE);

            Rectangle boundingBox = getImages().filterType(Host.class).getBoundingBox();
            Display.drawPolygon(boundingBox.getVertices(), display);
            // </BOUNDING_BOX_LABEL>
        }
        // </DEBUG_LABEL>
    }

    private void drawPrototypeExtension(Display display) {
        if (prototypeExtensionVisibility.getValue() == Visibility.Value.VISIBLE) {

            Paint paint = display.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(prototypePathSourcePosition, prototypeExtensionPosition);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#fff7f7f7"));
            Display.drawRectangle(prototypeExtensionPosition, pathRotationAngle + 180, 200, 200, display);
        }
    }

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawPrototypePath(Display display) {
        if (prototypePathVisibility.getValue() == Visibility.Value.VISIBLE) {

            Paint paint = display.getPaint();

            double triangleWidth = 20;
            double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
            double triangleSpacing = 35;

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
//            paint.setColor(this.getUniqueColor());

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    prototypePathSourcePosition,
                    prototypePathDestinationCoordinate
            );

            Point pathStartCoordinate = Geometry.calculatePoint(
                    prototypePathSourcePosition,
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            Point pathStopCoordinate = Geometry.calculatePoint(
                    prototypePathDestinationCoordinate,
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            paint.setColor(Color.parseColor("#efefef"));
            Display.drawTrianglePath(pathStartCoordinate, pathStopCoordinate, triangleWidth, triangleHeight, display);

            // Color
            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.parseColor("#efefef"));
            double shapeRadius = 40.0;
            Display.drawCircle(prototypePathDestinationCoordinate, shapeRadius, 0.0f, display);
        }
    }

    public void setPrototypePathVisibility(Visibility.Value visibility) {
        prototypePathVisibility.setValue(visibility);
    }

    public Visibility getPrototypePathVisibility() {
        return prototypePathVisibility;
    }

    public void setPrototypePathSourcePosition(Point position) {
        this.prototypePathSourcePosition.set(position);
    }

    public void setPrototypePathDestinationPosition(Point position) {
        this.prototypePathDestinationCoordinate.set(position);
    }

    public void setPrototypeExtensionPosition(Point position) {
        this.prototypeExtensionPosition.set(position);
    }

    public void setPrototypeExtensionVisibility(Visibility.Value visibility) {
        prototypeExtensionVisibility.setValue(visibility);
    }

    public Visibility getPrototypeExtensionVisibility() {
        return prototypeExtensionVisibility;
    }
}
