package camp.computer.clay.scene.architecture;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Feature;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Model;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.EventListener;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.scene.image.ExtensionImage;
import camp.computer.clay.scene.image.HostImage;
import camp.computer.clay.scene.image.PathImage;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.Probability;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

public class Scene extends Image<Model> {

    private List<Layer> layers = new ArrayList<>();

    public Scene(Model model) {
        super(model);
        setup();
    }

    private void setup() {
        setupActions();
    }

    public Visibility goalVisibility = Visibility.INVISIBLE;

    private void setupActions() {

        // Setup interactivity
        setOnActionListener(new EventListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                Image targetImage = getImageByPosition(event.getPosition());
                event.setTargetImage(targetImage);

                Camera camera = event.getActor().getCamera();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                    if (action.isTap()) {

                        // Camera
                        camera.focusSelectScene();

                    }

                } else if (event.getType() == Event.Type.HOLD) {

                    // Select patch to connect
                    Launcher.getLauncherView().displayChooseDialog();

                } else if (event.getType() == Event.Type.MOVE) {

                    if (action.isHolding()) {

                        // Scene
                        event.getTargetImage().processAction(action);

                    } else {

//                        camera.focusMoveView(event);

                        // Camera
                        if (action.getSize() > 1) {
                            camera.setOffset(
                                    event.getPosition().getX() - action.getFirstEvent().getPosition().getX(),
                                    event.getPosition().getY() - action.getFirstEvent().getPosition().getY()
                            );

                        }

//                    camera.setScale(0.9f);
//                    camera.setOffset(
//                            event.getPosition().getX() - action.getFirstEvent().getPosition().getX(),
//                            event.getPosition().getY() - action.getFirstEvent().getPosition().getY()
//                    );

                    }

                } else if (event.getType() == Event.Type.UNSELECT) {

                    Log.v("Event", "onRelease");
                    Log.v("Event", "processAction: " + event.getTargetImage());
                    Log.v("Event", "-");

                    if (action.isTap()) {

//                        if (goalVisibility == Visibility.INVISIBLE) {
//                            goalVisibility = Visibility.VISIBLE;
//                        } else {
//                            goalVisibility = Visibility.INVISIBLE;
//                        }

                    } else {

                        if (action.getSourceFeature() instanceof Host) {

                            Log.v("Test", "Create Extension from Host");

                            // Select patch to connect
                            Launcher.getLauncherView().displayChooseDialog();

//                            if (event.getTargetImage() instanceof HostImage) {
//
//                                Log.v("Test","T1");
//
//                                Log.v("Test","isPointing: " + action.getFirstEvent().isPointing());
//                                Log.v("Test","getTargetImage: " + action.getFirstEvent().getTargetImage());
//
//                                // If getFirstEvent processAction was on the same form, then respond
//                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage() instanceof HostImage) {
//
//                                    Log.v("Test","T2");
//
//                                    // Host
//                                    event.getTargetImage().processAction(event);
//
//                                    // Camera
////                        camera.focusSelectScene();
//                                }
//
//                            } else if (event.getTargetImage() instanceof Scene) {
//
//                                Log.v("Test","T3");
//
//                                // Host
////                                action.getFirstEvent().getTargetImage().processAction(event);
//
//                            }

                        } else if (action.getSourceFeature() instanceof Port) {

//                            Log.v("Test", "Create Extension from Port");
//
//                            PortImage sourcePortImage = (PortImage) event.getAction().getFirstEvent().getTargetImage();
//
//                            if (sourcePortImage.getCandidatePatchVisibility() == Visibility.VISIBLE) {
//
//                                Log.v("IASM", "(1) touch extension to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");
//
//                                // Feature
//                                Extension extension = new Extension();
//
//                                // Add port to feature
//                                // for (int j = 0; j < 3; j++) {
//                                for (int j = 0; j < 1; j++) {
//                                    Port port = new Port();
//                                    extension.addPort(port);
//                                }
//
//                                getFeature().addExtension(extension);
//
//                                // Create Extension Image
//                                ExtensionImage extensionImage = new ExtensionImage(extension);
//                                extensionImage.setPosition(event.getPosition());
//
//                                // Set Rotation
//                                double patchRotation = Geometry.calculateRotationAngle(
//                                        sourcePortImage.getPosition(),
//                                        extensionImage.getPosition()
//                                );
//                                extensionImage.setRotation(patchRotation + 90);
//
////                            Host sourceBase = (Host) sourcePortFigure.getFeature().getParent();
////                            HostImage sourceBaseFigure = (HostImage) getImage(sourceBase);
////                            patchFigure.setRelativeRotation(sourceBaseFigure.getRotation() + 180);
//
//                                // Create Port Figures for each of Extension's Ports
//                                for (int i = 0; i < extension.getPorts().size(); i++) {
//                                    Port port = extension.getPorts().get(i);
//                                    PortImage portImage = new PortImage(port);
//                                    addImage(portImage, "ports");
//                                }
//
//                                // Add Extension Image to Scene
//                                addImage(extensionImage, "patches");
//
//                                // Configure Ports
//                                Port sourcePort = sourcePortImage.getPort();
//                                Port destinationPort = extension.getPorts().get(0);
//
//                                if (sourcePort.getDirection() == Port.Direction.NONE) {
//                                    sourcePort.setDirection(Port.Direction.OUTPUT);
//                                }
////                        if (sourcePort.getType() == Port.Type.NONE) {
//                                //sourcePort.setType(Port.Type.next(sourcePort.getType())); // (machineSprite.channelTypes.getEvent(i) + 1) % machineSprite.channelTypeColors.length
//                                sourcePort.setType(Port.Type.POWER_REFERENCE);
////                        }
//
//                                destinationPort.setDirection(Port.Direction.INPUT);
//                                //destinationPort.setType(Port.Type.next(destinationPort.getType()));
//                                destinationPort.setType(sourcePort.getType());
//
//                                // Create Path
//                                Path path = new Path(sourcePortImage.getPort(), extension.getPorts().get(0));
//                                path.setType(Path.Type.ELECTRONIC);
//                                sourcePort.addPath(path);
//
//                                PathImage pathImage = new PathImage(path);
//                                addImage(pathImage, "paths");
//
//                                PortImage targetPortImage = (PortImage) getImage(path.getTarget());
//                                targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());
//
//                                // Update Camera
//                                camera.focusSelectPath(sourcePortImage.getPort());
//
//                            }
//
//                            // Update Image
//                            sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);
//                            sourcePortImage.setCandidatePatchVisibility(Visibility.INVISIBLE);
                        }
                    }
                }
            }
        });
    }

    public Model getModel() {
        return getFeature();
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

    public <T extends Feature> void addFeature(T feature) {

        if (feature instanceof Host) {

            Host host = (Host) feature;

            // Create Host Image
            HostImage hostImage = new HostImage(host);

            // Create Port Shapes for each of the Host's Ports
            for (int i = 0; i < host.getPorts().size(); i++) {
                Port port = host.getPorts().get(i);
                addFeature(port);
            }

            // Add Host Image to Scene
            addImage(hostImage, "bases");

        } else if (feature instanceof Extension) {

            Extension extension = (Extension) feature;

            // Create Extension Image
            ExtensionImage extensionImage = new ExtensionImage(extension);
//            extensionImage.setPosition(event.getPosition());

//                            Host sourceBase = (Host) sourcePortFigure.getFeature().getParent();
//                            HostImage sourceBaseFigure = (HostImage) getImage(sourceBase);
//                            patchFigure.setRelativeRotation(sourceBaseFigure.getRotation() + 180);

            // Create Port Shapes for each of the Extension's Ports
            for (int i = 0; i < extension.getPorts().size(); i++) {
                Port port = extension.getPorts().get(i);
                //PortImage portImage = new PortImage(port);
                //addImage(portImage, "ports");
                addFeature(port);
            }

            // Add Extension Image to Scene
            addImage(extensionImage, "patches");

        } else if (feature instanceof Port) {

            Port port = (Port) feature;

//            PortImage portImage = new PortImage(port);
//            addImage(portImage, "ports");

        } else if (feature instanceof Path) {

            Path path = (Path) feature;

            PathImage pathImage = new PathImage(path);
            addImage(pathImage, "paths");

        }
    }

    // TODO: Remove Image parameter. Create that and return it.
    private void addImage(Image image, String layerTag) {

        // Add layer (if it doesn't exist)
        if (!hasLayer(layerTag)) {
            addLayer(layerTag);
        }

        // Add image
        getLayer(layerTag).add(image);

        // Coordinate image
        if (image instanceof HostImage) {
            adjustImageCoordinate(image);
        }

        // Update perspective
        getFeature().getActor(0).getCamera().focusSelectScene();
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

    /**
     * Automatically determines and assigns a valid position for the specified {@code Image}.
     *
     * @param image The {@code Image} for which the position will be adjusted.
     */
    private void adjustImageCoordinate(Image image) {

        int adjustmentMethod = 1;

        if (adjustmentMethod == 0) {

            // Calculate random positions separated by minimum distance
            final float imageSeparationDistance = 550; // 500;

            List<Point> imageCoordinates = getImages().filterType(Host.class).getCoordinates();

            Point position = null;
            boolean foundPoint = false;

            Log.v("Coordinate", "imageCoordinates.size = " + imageCoordinates.size());

            if (imageCoordinates.size() == 0) {

                position = new Point(0, 0);

            } else if (imageCoordinates.size() == 1) {

                position = Geometry.calculatePoint(
                        imageCoordinates.get(0),
                        Probability.generateRandomInteger(0, 360),
                        imageSeparationDistance
                );

            } else {

                List<Point> hullPoints = Geometry.computeConvexHull(imageCoordinates);

                int sourceIndex = Probability.generateRandomInteger(0, hullPoints.size() - 1);
                int targetIndex = sourceIndex + 1;

                Point midpoint = Geometry.calculateMidpoint(hullPoints.get(sourceIndex), hullPoints.get(targetIndex));
                position = Geometry.calculatePoint(
                        midpoint,
                        Geometry.calculateRotationAngle(hullPoints.get(sourceIndex), hullPoints.get(targetIndex)) + 90,
                        imageSeparationDistance
                );
            }

            // Assign the found position to the image
            image.setPosition(position);
            image.setRotation(Probability.getRandomGenerator().nextInt(360));
        }

        if (adjustmentMethod == 1) {

            List<Image> imageCoordinates = getImages().filterType(Host.class).getList();

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
     * Returns {@code true} if the {@code Scene} contains a {@code Image} corresponding to the
     * specified {@code Feature}.
     *
     * @param feature The {@code Feature} for which the corresponding {@code Image} will be
     *                returned, if any.
     * @return The {@code Image} corresponding to the specified {@code Feature}, if one is
     * present. If one is not present, this method returns {@code null}.
     */
    public boolean contains(Feature feature) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Image image = layer.getImage(feature);
            if (image != null) {
                return true;
            }
        }
        return false;
    }

    public Image getImage(Feature feature) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Image image = layer.getImage(feature);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public <T> List<Image> getImages(List<T> features) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < features.size(); j++) {
                T model = features.get(j);
                Image image = layer.getImage((Feature) model);
                if (image != null) {
                    images.add(image);
                }
            }
        }
        return images;
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

    public <T extends Feature> ImageGroup getImages(Class<?>... types) {
        return getImages().filterType(types);
    }

    // TODO: Delete. Replace with ImageGroup.filterPosition(Point)
    public Image getImageByPosition(Point point) {
        List<Image> images = getImages().filterVisibility(Visibility.VISIBLE).getList();
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
        List<Image> imageList = getImages().getList();

        for (int i = 0; i < imageList.size(); i++) {
            shapeGroup.add(imageList.get(i).getShapes());
        }

        return shapeGroup;
    }

    // TODO: Refactor to be cleaner and leverage other classes...
    public <T extends Feature> ShapeGroup getShapes(Class<?>... types) {

        ShapeGroup shapeGroup = new ShapeGroup();
        List<Image> imageList = getImages().getList();

        for (int i = 0; i < imageList.size(); i++) {
            shapeGroup.add(imageList.get(i).getShapes(types));
        }


        return shapeGroup.filterType(types);
    }

//    public <T> List<Image> getShapes(List<T> features)
//        List<Image> images = new ArrayList<>();
//        for (int i = 0; i < layers.size(); i++) {
//            Layer layer = layers.get(i);
//            for (int j = 0; j < features.size(); j++) {
//                T model = features.get(j);
//                Image image = layer.getImage((Feature) model);
//                if (image != null) {
//                    images.add(image);
//                }
//            }
//        }
//        return images;
//    }

    public Shape getShape(Feature feature) {
        List<Image> imageList = getImages().getList();
        for (int i = 0; i < imageList.size(); i++) {
            Image image = imageList.get(i);
            Shape shape = image.getShape(feature);
            if (shape != null) {
                return shape;
            }
        }
        return null;
    }

    public Model getFeature() {
        return this.feature;
    }

    public Feature getFeature(Image image) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Feature feature = layer.getFeature(image);
            if (feature != null) {
                return feature;
            }
        }
        return null;
    }

    public Feature getFeature(Shape shape) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Feature feature = layer.getFeature(shape);
            if (feature != null) {
                return feature;
            }
        }
        return null;
    }

    public static <T extends Image> List<Point> getCoordinates(List<T> images) {
        List<Point> positions = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            T figure = images.get(i);
            positions.add(figure.getPosition());
        }
        return positions;
    }

    public void update() {

        // Update perspective
        getFeature().getActor(0).getCamera().update();

        // Update figures
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < layer.getImages().size(); j++) {
                Image image = layer.getImages().get(j);
                image.update();
            }
        }

        // Update figure layout
        // Geometry.computeCirclePacking(getImages().filterType(HostImage.class, ExtensionImage.class).getList(), 200, getImages().filterType(HostImage.class, ExtensionImage.class).getCentroidPoint());
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
            List<Point> figureCoordinates = getImages().filterType(Host.class, Extension.class).getCoordinates();
            Point baseImagesCenterCoordinate = Geometry.calculateCenterCoordinate(figureCoordinates);
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
        Log.v("r3", "center x: " + r3.getPosition().getX() + ", y: " + r3.getPosition().getY());
        Log.v("r3", "topLeft x: " + r3.getTopLeft().getX() + ", y: " + r3.getTopLeft().getY());
        Log.v("r3", "topRight x: " + r3.getTopRight().getX() + ", y: " + r3.getTopRight().getY());
        Log.v("r3", "bottomRight x: " + r3.getBottomRight().getX() + ", y: " + r3.getBottomRight().getY());
        Log.v("r3", "bottomLeft x: " + r3.getBottomLeft().getX() + ", y: " + r3.getBottomLeft().getY());

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

        Layer layer = null;

        layer = getLayer("bases");
        if (layer != null) {
            for (int i = 0; i < layer.getImages().size(); i++) {
                layer.getImages().get(i).draw(display);
            }
        }

        layer = getLayer("paths");
        if (layer != null) {
            for (int i = 0; i < layer.getImages().size(); i++) {
                layer.getImages().get(i).draw(display);
            }
        }

        layer = getLayer("patches");
        if (layer != null) {
            for (int i = 0; i < layer.getImages().size(); i++) {
                layer.getImages().get(i).draw(display);
            }
        }

//        layer = getLayer("ports");
//        if (layer != null) {
//            for (int i = 0; i < layer.getImages().size(); i++) {
//                layer.getImages().get(i).draw(display);
//            }
//        }
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

//    @Override
//    public boolean contains(Point point) {
//        return false;
//    }
//
//    @Override
//    public boolean contains(Point point, double padding) {
//        return false;
//    }

//    public void onTouchListener(Event action) {
//
//        Image targetFigure = getImageByPosition(action.getPosition());
//        action.setTargetImage(targetFigure);
//
//        action.getTargetImage().processAction(action);
//
//    }
//
//    public void onHoldListener(Event action) {
//
//        Image targetFigure = getImageByPosition(action.getPosition());
//        action.setTargetImage(targetFigure);
//
//        action.getTargetImage().processAction(action);
//
//    }

//    public void onMoveListener(Event action) {
//
//        Action actionSequence = action.getAction();
//
//        Image targetFigure = getImageByPosition(action.getPosition());
//        action.setTargetImage(targetFigure);
//
//        Camera camera = action.getActor().getCamera();
//
//        if (actionSequence.getSize() > 1) {
//            action.setTargetImage(actionSequence.getFirstEvent().getTargetImage());
//        }
//
//        // Holding
//        if (actionSequence.isHolding()) {
//
//            // Holding and dragging
//
//            if (action.getTargetImage() instanceof HostImage) {
//
////                // Host
////                action.getTargetImage().processAction(action);
////                action.getTargetImage().setPosition(action.getPosition());
////
////                // Camera
////                camera.focusSelectHost(action);
//
//            } else if (action.getTargetImage() instanceof PortImage) {
//
////                // Port
////                PortImage portFigure = (PortImage) action.getTargetImage();
////
////                portFigure.setDragging(true);
////                portFigure.setPosition(action.getPosition());
//
//            } else if (action.getTargetImage() instanceof Scene) {
//
//                // Scene
////                action.getTargetImage().processAction(action);
//
//            }
//
//        } else {
//
//            // Not holding. Drag was detected prior to the hold duration threshold.
//
//            if (action.getTargetImage() instanceof HostImage) {
//
////                // Host
////                action.getTargetImage().processAction(action);
////
////                // Camera
////                camera.focusSelectHost(action);
//
//            } else if (action.getTargetImage() instanceof PortImage) {
//
////                // Port
////                PortImage portFigure = (PortImage) action.getTargetImage();
////                portFigure.processAction(action);
////
////                // Camera
////                camera.focusCreatePath(action);
//
//            } else if (action.getTargetImage() instanceof ExtensionImage) {
//
////                // Extension
////                action.getTargetImage().setPosition(action.getPosition());
////                action.getTargetImage().processAction(action);
//
//            } else if (action.getTargetImage() instanceof Scene) {
//
////                // Camera
////                if (actionSequence.getSize() > 1) {
////                    camera.setOffset(
////                            action.getPosition().getX() - actionSequence.getFirstEvent().getPosition().getX(),
////                            action.getPosition().getY() - actionSequence.getFirstEvent().getPosition().getY()
////                    );
////
////                }
//
//            }
//        }
//    }

    public void onReleaseListener(Event event) {

        Action action = event.getAction();

        event.setType(Event.Type.UNSELECT);

        Image targetImage = getImageByPosition(event.getPosition());
        event.setTargetImage(targetImage);

        Camera camera = event.getActor().getCamera();

        Log.v("Event", "onRelease");
        Log.v("Event", "processAction: " + event.getTargetImage());
        Log.v("Event", "-");


        if (action.getDuration() < Event.MAXIMUM_TAP_DURATION) {

//            if (event.getTargetImage() instanceof HostImage) {
//
////                // Host
////                event.getTargetImage().processAction(event);
////
////                // Camera
////                camera.focusSelectHost(event);
//
//            } else if (event.getTargetImage() instanceof PortImage) {
//
//                // Port
////                event.getTargetImage().processAction(event);
//
//            } else if (event.getTargetImage() instanceof PathImage) {
//
//                // Path
////                event.getTargetImage().processAction(event);
//
//            } else if (event.getTargetImage() instanceof ExtensionImage) {
//
//                // Extension
////                event.getTargetImage().processAction(event);
//
//            } else if (event.getTargetImage() instanceof Scene) {
//
////                // Scene
////                event.getTargetImage().processAction(event);
////
////                // Camera
////                camera.focusSelectScene();
//
//            }

        } else {

            event.setType(Event.Type.UNSELECT);

//            event.setTrigger(
//                    Event.Type.NONE,
//                    Event.Type.SELECT,
//                    Event.Type.MOVE,
//                    *,
//                    Event.Type.UNSELECT
//            );

            // onSequence (HostImage.class, ..., Image.class, null, ) { ... }
            // onSequence (HostImage.class, *, Image.class, null, ) { ... }

            // First processAction was on a base figure...
            if (action.getFirstEvent().getTargetImage() instanceof HostImage) {

                if (event.getTargetImage() instanceof HostImage) {

                    // If getFirstEvent processAction was on the same form, then respond
                    if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage() instanceof HostImage) {

                        // Host
                        event.getTargetImage().processAction(action);

                        // Camera
//                        camera.focusSelectScene();
                    }

                } else if (event.getTargetImage() instanceof Scene) {

                    // Host
                    action.getFirstEvent().getTargetImage().processAction(action);

                }

            } /* else if (action.getFirstEvent().getTargetImage() instanceof PortImage) {

                // First processAction was on a port figure...

                if (event.getTargetImage() instanceof HostImage) {

                    // ...getLastEvent processAction was on a base figure.

                    //// TODO: PortImage sourcePortImage = (PortImage) action.getFirstEvent().getTargetImage();
                    //// TODO: sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);

                    //// TODO: } else if (event.getTargetImage() instanceof PortImage) {

                    // Port
                    event.getTargetImage().processAction(action);

                } else if (event.getTargetImage() instanceof ExtensionImage) {

                    // Extension
                    event.getTargetImage().processAction(action);

                } else if (event.getTargetImage() instanceof Scene) {

                    event.getTargetImage().processAction(action);

                }

            } */ else if (action.getFirstEvent().getTargetImage() instanceof PathImage) {

                // Path --> ?

                if (event.getTargetImage() instanceof PathImage) {
                    // Path --> Path
                    PathImage pathImage = (PathImage) event.getTargetImage();
                }

            } else if (action.getFirstEvent().getTargetImage() instanceof Scene) {

                // Scene --> ?

//                // Check if getFirstEvent processAction was on an figure
//                if (action.getFirstEvent().getTargetImage() instanceof PortImage) {
//                    //// TODO: ((PortImage) action.getFirstEvent().getTargetImage()).setCandidatePathVisibility(Visibility.INVISIBLE);
//                }

//                camera.focusSelectScene();

            }

        }
    }
}
