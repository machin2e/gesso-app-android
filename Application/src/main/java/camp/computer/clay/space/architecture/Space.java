package camp.computer.clay.space.architecture;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Entity;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Group;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Model;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PathImage;
import camp.computer.clay.space.util.Probability;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;

public class Space extends Image<Model> {
    private List<Layer> layers = new ArrayList<>();

    private Visibility prototypeExtensionVisibility = new Visibility(Visibility.Value.INVISIBLE);
    private Point prototypeExtensionPosition = new Point();

    private Visibility prototypePathVisibility = new Visibility(Visibility.Value.INVISIBLE);
    private Point prototypePathSourcePosition = new Point(0, 0);
    private Point prototypePathDestinationCoordinate = new Point(0, 0);

    double shapeRadius = 40.0;

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

                Event lastEvent = action.getLastEvent();

                Camera camera = lastEvent.getActor().getCamera();

                if (lastEvent.getType() == Event.Type.NONE) {
                } else if (lastEvent.getType() == Event.Type.SELECT) {
                    if (action.isTap()) {

                        // Camera
                        camera.focusSelectSpace();
                    }
                } else if (lastEvent.getType() == Event.Type.HOLD) {
                    // Select patch to connect
//                    Launcher.getView().promptSelection();

                } else if (lastEvent.getType() == Event.Type.MOVE) {
                    if (action.isHolding()) {
                        // Space
                        lastEvent.getTargetImage().processAction(action);
                    } else {
                        // Camera
                        if (action.getSize() > 1) {
                            camera.setOffset(lastEvent.getPosition().getX() - action.getFirstEvent().getPosition().getX(), lastEvent.getPosition().getY() - action.getFirstEvent().getPosition().getY());
                        }
                    }
                } else if (lastEvent.getType() == Event.Type.UNSELECT) {
                }
            }
        });
    }

    public Model getModel() {
        return getEntity();
    }

    private boolean hasLayer(String tag) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    private void addLayer(String tag) {
        if (!hasLayer(tag)) {
            Layer layer = new Layer(this);
            layer.setTag(tag);
            layers.add(layer);
        }
    }

    public Layer getLayer(String tag) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getTag().equals(tag)) {
                return layers.get(i);
            }
        }
        return null;
    }

    public Layer getLayer(int id) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            if (layer.getIndex() == id) {
                return layer;
            }
        }
        return null;
    }

    public <T extends Entity> void addEntity(T entity) {
        if (entity instanceof Host) {

            Host host = (Host) entity;

            // Create Host Image
            HostImage hostImage = new HostImage(host);

            // Create Port Shapes for each of the Host's Ports
            for (int i = 0; i < host.getPorts().size(); i++) {
                Port port = host.getPorts().get(i);
                addEntity(port);
            }

            // Add Host Image to Space
            addImage(hostImage, "hosts");
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
            addImage(extensionImage, "extensions");
        } else if (entity instanceof Port) {

            Port port = (Port) entity;

//            PortImage portImage = new PortImage(port);
//            addImage(portImage, "ports");

            // TODO:

        } else if (entity instanceof Path) {

            Path path = (Path) entity;

            // Create Path Image
            PathImage pathImage = new PathImage(path);

            // Add Path Image to Space
            addImage(pathImage, "paths");
        }
    }

    // TODO: Remove Image parameter. Create that and return it.
    private <T extends Image> void addImage(T image, String layerTag) {
        // Add layer (if it doesn't exist)
        if (!hasLayer(layerTag)) {
            addLayer(layerTag);
        }

        // Add Image
        getLayer(layerTag).addImage(image);

        // Position the Image
        // <HACK>
        if (image instanceof HostImage) {
            adjustLayout(image);
        }
        // </HACK>

        // Update Camera
        getEntity().getActor(0).getCamera().focusSelectSpace();
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
//            image.setRelativeRotation(Probability.getRandomGenerator().nextInt(360));
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
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Image image = layer.getImage(entity);
            if (image != null) {
                return true;
            }
        }
        return false;
    }

    public Image getImage(Entity entity) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Image image = layer.getImage(entity);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public <T> ImageGroup getImages(Group<T> entities) {
        ImageGroup imageGroup = new ImageGroup();
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < entities.size(); j++) {
                T entity = entities.get(j);
                Image image = layer.getImage((Entity) entity);
                if (image != null) {
                    imageGroup.add(image);
                }
            }
        }
        return imageGroup;
    }

    public ImageGroup getImages() {
        ImageGroup imageGroup = new ImageGroup();
        List<Integer> indices = getLayerIndices();
        for (int i = 0; i < indices.size(); i++) {
            Integer index = indices.get(i);
            Layer layer = getLayer(index);
            if (layer != null) {
                imageGroup.add(layer.getImages());
            }
        }
        return imageGroup;
    }

    public <T extends Entity> ImageGroup getImages(Class<?>... entityTypes) {
        return getImages().filterType(entityTypes);
    }

    // TODO: Delete. Replace with ImageGroup.filterPosition(Point)
    public Image getImageByPosition(Point point) {
        ImageGroup images = getImages().filterVisibility(Visibility.Value.VISIBLE);
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            if (image.contains(point)) {
                return image;
            }
        }
        return this;
    }

    public ShapeGroup getShapes() {
        ShapeGroup shapeGroup = new ShapeGroup();
        ImageGroup imageList = getImages();

        for (int i = 0; i < imageList.size(); i++) {
            shapeGroup.add(imageList.get(i).getShapes());
        }

        return shapeGroup;
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

    public Entity getEntity(Image image) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Entity entity = layer.getEntity(image);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public Entity getEntity(Shape shape) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Entity entity = layer.getEntity(shape);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

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
//        super.update();

        // Update perspective
        getEntity().getActor(0).getCamera().update();

        // Update figures
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < layer.getImages().size(); j++) {
                Image image = layer.getImages().get(j);
                image.update();
            }
        }

        // Update figure layout
        // Geometry.computeCirclePacking(getImages().filterType(HostImage.class, ExtensionImage.class).getList(), 200, getImages().filterType(HostImage.class, ExtensionImage.class).getCentroidPosition());
    }

    @Override
    public void draw(Display display) {
        // <DEBUG_LABEL>
        if (Launcher.ENABLE_GEOMETRY_LABELS) {
            // <AXES_LABEL>
            display.getPaint().setColor(Color.CYAN);
            display.getPaint().setStrokeWidth(1.0f);
            display.getCanvas().drawLine(-5000, 0, 5000, 0, display.getPaint());
            display.getCanvas().drawLine(0, -5000, 0, 5000, display.getPaint());
            // </AXES_LABEL>
        }
        // </DEBUG_LABEL>

        // Draw Layers
        drawLayers(display);

        // Draw any prototype Paths and Extensions
        drawPrototypePathImages(display);
        drawPrototypeExtensionImage(display);

        // <DEBUG_LABEL>
        if (Launcher.ENABLE_GEOMETRY_LABELS) {

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
            Point baseImagesCenterCoordinate = Geometry.calculateCenterPosition(figureCoordinates);
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

        /*
        // Test Case
//        Point p1 = new Point(0, 0);
//        p1.setRelativeRotation(0);
//        Point p2 = new Point(0, 0, p1);
//        p2.setRelativeRotation(20);
//        Point p3 = new Point(0, 0, p2);
//        p3.setRelativeRotation(20);
//        Point p4 = new Point(0, 0, p3);
//        p4.setRelativeRotation(20);
//        Point p5 = new Point(0, 0, p4);
//        p5.setRelativeRotation(20);

        // Test Case
        Point p1 = new Point(0, 0);
        p1.setRelativeRotation(0);
        Point p2 = new Point(200, 400, p1);
        p2.setRelativeRotation(20);
        Point p3 = new Point(-250, 0, p2);
        p3.setRelativeRotation(0);
        Point p4 = new Point(0, 150, p3);
        p4.setRelativeRotation(0);
        Point p5 = new Point(0, -75, p4);
        p5.setRelativeRotation(20);

        Log.v("Points", "p2: " + p2.getX() + ", " + p2.getY());

        Log.v("Points2", "angle(A,B): " + Geometry.calculateRotationAngle(p1, p2));
        Log.v("Points2", "distance(A,B): " + Geometry.calculateDistance(p1, p2));
        Log.v("Points2", "angle(B,C): " + Geometry.calculateRotationAngle(p2, p3));
        Log.v("Points2", "distance(B,C): " + Geometry.calculateDistance(p2, p3));
        Log.v("Points2", "angle(C,D): " + Geometry.calculateRotationAngle(p3, p4));
        Log.v("Points2", "distance(C,D): " + Geometry.calculateDistance(p3, p4));
        Log.v("Points2", "angle(D,E): " + Geometry.calculateRotationAngle(p4, p5));
        Log.v("Points2", "distance(D,E): " + Geometry.calculateDistance(p4, p5));
        Log.v("Points2", "---");

//        // Test Case
//        Point p1 = new Point(0, 0);
//        p1.setRelativeRotation(0);
//        Point p2 = new Point(200 + 150, 0, p1);
//        p2.setRelativeRotation(20);
//        Point p3 = new Point(0, 0, p2);
//        p3.setRelativeRotation(20);
//        Point p4 = new Point(0, 0, p3);
//        p4.setRelativeRotation(20);
//        Point p5 = new Point(0, 0, p4);
//        p5.setRelativeRotation(20);

        // Test Case
//        Point p1 = new Point();
//        p1.setRelativeRotation(0);
//        Point p2 = new Point(200 + 150, 0, p1);
//        p2.setRelativeRotation(0);
//        Point p3 = new Point(-200 + -150, 0, p2);
//        p3.setRelativeRotation(0);
//        Point p4 = new Point(200 + 150, 0, p3);
//        p4.setRelativeRotation(0);

        // Test Case
//        Point p1 = new Point();
//        p1.setRelativeRotation(0);
//        Point p2 = new Point(0, 0, p1);
//        p2.setRelativeRotation(45);
//        Point p3 = new Point(0, 0, p2);
//        p3.setRelativeRotation(0);
//        Point p4 = new Point(283, 0, p3);
//        p4.setRelativeRotation(0);

        surface.getPaint().setStyle(Paint.Style.FILL);
        surface.getPaint().setColor(Color.RED);
        surface.drawRectangle(p1, p1.getRotation(), 400, 400, surface);

        surface.getPaint().setColor(Color.GREEN);
        surface.drawRectangle(p2, p2.getRotation(), 300, 300, surface);

        surface.getPaint().setColor(Color.BLUE);
        surface.drawRectangle(p3, p3.getRotation(), 200, 200, surface);

        surface.getPaint().setColor(Color.MAGENTA);
        surface.drawRectangle(p4, p4.getRotation(), 100, 100, surface);

        surface.getPaint().setColor(Color.CYAN);
        surface.drawRectangle(p5, p5.getRotation(), 50, 50, surface);

        //-----

        Rectangle r1 = new Rectangle(p1, 100, 100);
        surface.drawRectangle(r1, surface);

        Rectangle r2 = new Rectangle(p2, 100, 100);
        surface.drawRectangle(r2, surface);

        List<Point> points = r2.getVertices();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            surface.drawRectangle(p, p.getRotation(), 25, 25, surface);

        }

        Rectangle r3 = new Rectangle(p3, 100, 100);
        surface.drawRectangle(r3, surface);
        Log.v("r3", "center relativeX: " + r3.getPosition().getX() + ", relativeY: " + r3.getPosition().getY());
        Log.v("r3", "topLeft relativeX: " + r3.getTopLeft().getX() + ", relativeY: " + r3.getTopLeft().getY());
        Log.v("r3", "topRight relativeX: " + r3.getTopRight().getX() + ", relativeY: " + r3.getTopRight().getY());
        Log.v("r3", "bottomRight relativeX: " + r3.getBottomRight().getX() + ", relativeY: " + r3.getBottomRight().getY());
        Log.v("r3", "bottomLeft relativeX: " + r3.getBottomLeft().getX() + ", relativeY: " + r3.getBottomLeft().getY());

        points = r3.getVertices();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            surface.drawRectangle(p, p.getRotation(), 25, 25, surface);

        }

        Rectangle r4 = new Rectangle(p4, 100, 100);
        surface.drawRectangle(r4, surface);

        Rectangle r5 = new Rectangle(p5, 100, 100);
        surface.drawRectangle(r5, surface);
        */
    }

    private void drawLayers(Display display) {
        if (getLayer("paths") != null) {
            getLayer("paths").draw(display);
        }

        if (getLayer("hosts") != null) {
            getLayer("hosts").draw(display);
        }

        if (getLayer("extensions") != null) {
            getLayer("extensions").draw(display);
        }
    }

    public List<Integer> getLayerIndices() {
        List<Integer> layerIndices = new ArrayList<>();
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            layerIndices.add(layer.getIndex());
        }
        Collections.sort(layerIndices);
        return layerIndices;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    private void drawPrototypeExtensionImage(Display display) {
        if (prototypeExtensionVisibility.getValue() == Visibility.Value.VISIBLE) {

            Paint paint = display.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(prototypePathSourcePosition, prototypeExtensionPosition);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#fff7f7f7"));
            Display.drawRectangle(prototypeExtensionPosition, pathRotationAngle + 180, 200, 200, display);
        }
    }

    // TODO: Make this into a shape and put this on a separate layer!
    public void drawPrototypePathImages(Display display) {
        if (prototypePathVisibility.getValue() == Visibility.Value.VISIBLE) {
//            if (getPort().getType() != Port.Type.NONE) {

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

            Display.drawTrianglePath(pathStartCoordinate, pathStopCoordinate, triangleWidth, triangleHeight, display);

            // Color
            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(getUniqueColor());
            Display.drawCircle(prototypePathDestinationCoordinate, shapeRadius, 0.0f, display);
//            }
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
