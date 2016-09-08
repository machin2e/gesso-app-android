package camp.computer.clay.scene.architecture;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Base;
import camp.computer.clay.model.architecture.Construct;
import camp.computer.clay.model.architecture.Universe;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Process;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.scene.image.BaseImage;
import camp.computer.clay.scene.image.PatchImage;
import camp.computer.clay.scene.image.PathImage;
import camp.computer.clay.scene.image.PortImage;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.Probability;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

public class Scene extends Image<Universe> {

    private List<Layer> layers = new ArrayList<>();

    public Scene(Universe universe) {
        super(universe);
        setup();
    }

    private void setup() {
        setupActions();
    }

    public Visibility goalVisibility = Visibility.INVISIBLE;

    private void setupActions() {

        // Setup interactivity
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Process process = action.getActionSequence();

                Image targetImage = getImageByCoordinate(action.getCoordinate());
                action.setTarget(targetImage);

                Camera camera = action.getActor().getCamera();

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.SELECT) {

                    if (process.isTap()) {

                        // Camera
                        camera.focusSelectScene();

                    }

                } else if (action.getType() == Action.Type.HOLD) {

                    // Select patch to connect
                    Application.getDisplay().displayChooseDialog();

                } else if (action.getType() == Action.Type.MOVE) {

                    if (process.isHolding()) {

                        // Scene
                        action.getTarget().processAction(action);

                    } else {

//                        camera.focusMoveView(action);

                        // Camera
                        if (process.getSize() > 1) {
                            camera.setOffset(
                                    action.getCoordinate().getX() - process.getFirstAction().getCoordinate().getX(),
                                    action.getCoordinate().getY() - process.getFirstAction().getCoordinate().getY()
                            );

                        }

//                    camera.setScale(0.9f);
//                    camera.setOffset(
//                            action.getCoordinate().getX() - process.getFirstAction().getCoordinate().getX(),
//                            action.getCoordinate().getY() - process.getFirstAction().getCoordinate().getY()
//                    );

                    }

                } else if (action.getType() == Action.Type.UNSELECT) {

                    Log.v("Action", "onRelease");
                    Log.v("Action", "processAction: " + action.getTarget());
                    Log.v("Action", "-");

                    if (process.isTap()) {

//                        if (goalVisibility == Visibility.INVISIBLE) {
//                            goalVisibility = Visibility.VISIBLE;
//                        } else {
//                            goalVisibility = Visibility.INVISIBLE;
//                        }

                    } else {

                        if (process.getSource() instanceof Base) {

                            Log.v("Test", "Create Patch from Base");

                            // Select patch to connect
                            Application.getDisplay().displayChooseDialog();

//                            if (action.getTarget() instanceof BaseImage) {
//
//                                Log.v("Test","T1");
//
//                                Log.v("Test","isPointing: " + process.getFirstAction().isPointing());
//                                Log.v("Test","getTarget: " + process.getFirstAction().getTarget());
//
//                                // If getFirstAction processAction was on the same form, then respond
//                                if (process.getFirstAction().isPointing() && process.getFirstAction().getTarget() instanceof BaseImage) {
//
//                                    Log.v("Test","T2");
//
//                                    // Base
//                                    action.getTarget().processAction(action);
//
//                                    // Camera
////                        camera.focusSelectScene();
//                                }
//
//                            } else if (action.getTarget() instanceof Scene) {
//
//                                Log.v("Test","T3");
//
//                                // Base
////                                process.getFirstAction().getTarget().processAction(action);
//
//                            }

                        } else if (process.getSource() instanceof Port) {

                            Log.v("Test", "Create Patch from Port");

                            PortImage sourcePortImage = (PortImage) action.getActionSequence().getFirstAction().getTarget();

                            if (sourcePortImage.getCandidatePatchVisibility() == Visibility.VISIBLE) {

                                Log.v("IASM", "(1) touch patch to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

                                // Construct
                                Patch patch = new Patch();

                                // Add port to construct
                                // for (int j = 0; j < 3; j++) {
                                for (int j = 0; j < 1; j++) {
                                    Port port = new Port();
                                    patch.addPort(port);
                                }

                                getModel().addPatch(patch);

                                // Create Patch Image
                                PatchImage patchImage = new PatchImage(patch);
                                patchImage.setCoordinate(action.getCoordinate());

                                // Set Rotation
                                double patchRotation = Geometry.calculateRotationAngle(
                                        sourcePortImage.getCoordinate(),
                                        patchImage.getCoordinate()
                                );
                                patchImage.setRotation(patchRotation + 90);

//                            Base sourceBase = (Base) sourcePortFigure.getConstruct().getParent();
//                            BaseImage sourceBaseFigure = (BaseImage) getImage(sourceBase);
//                            patchFigure.setRotation(sourceBaseFigure.getRotation() + 180);

                                // Create Port Figures for each of Patch's Ports
                                for (int i = 0; i < patch.getPorts().size(); i++) {
                                    Port port = patch.getPorts().get(i);
                                    PortImage portImage = new PortImage(port);
                                    addImage(portImage, "ports");
                                }

                                // Add Patch Image to Scene
                                addImage(patchImage, "patches");

                                // Configure Ports
                                Port sourcePort = sourcePortImage.getPort();
                                Port destinationPort = patch.getPorts().get(0);

                                if (sourcePort.getDirection() == Port.Direction.NONE) {
                                    sourcePort.setDirection(Port.Direction.OUTPUT);
                                }
//                        if (sourcePort.getType() == Port.Type.NONE) {
                                //sourcePort.setType(Port.Type.next(sourcePort.getType())); // (machineSprite.channelTypes.getAction(i) + 1) % machineSprite.channelTypeColors.length
                                sourcePort.setType(Port.Type.POWER_REFERENCE);
//                        }

                                destinationPort.setDirection(Port.Direction.INPUT);
                                //destinationPort.setType(Port.Type.next(destinationPort.getType()));
                                destinationPort.setType(sourcePort.getType());

                                // Create Path
                                Path path = new Path(sourcePortImage.getPort(), patch.getPorts().get(0));
                                path.setType(Path.Type.ELECTRONIC);
                                sourcePort.addPath(path);

                                PathImage pathImage = new PathImage(path);
                                addImage(pathImage, "paths");

                                PortImage targetPortImage = (PortImage) getImage(path.getTarget());
                                targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                // Update Camera
                                camera.focusSelectPath(sourcePortImage.getPort());

                            }

                            // Update Image
                            sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);
                            sourcePortImage.setCandidatePatchVisibility(Visibility.INVISIBLE);
                        }
                    }
                }
            }
        });
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

    public <T extends Construct> void addConstruct(T construct) {

        if (construct instanceof Base) {

            Base base = (Base) construct;

            // Create base figures
            BaseImage baseImage = new BaseImage(base);

            // Setup base's port figures
            // Add a port sprite for each of the associated base's ports
//            for (Port port : base.getPorts()) {
            for (int i = 0; i < base.getPorts().size(); i++) {
                Port port = base.getPorts().get(i);
//                PortImage portFigure = new PortImage(port);
//                scene.addImage(portFigure, "ports");

                addConstruct(port);

            }

            addImage(baseImage, "bases");

        } else if (construct instanceof Port) {

            Port port = (Port) construct;

            PortImage portImage = new PortImage(port);
            addImage(portImage, "ports");

        } else if (construct instanceof Path) {

            Path path = (Path) construct;

            PathImage pathImage = new PathImage(path);
            // pathFigure.setScene(getScene());
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
        if (image instanceof BaseImage) {
            adjustImageCoordinate(image);
        }

        // Update perspective
        getModel().getActor(0).getCamera().focusSelectScene();
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

            List<Point> imageCoordinates = getImages().filterType(Base.class).getCoordinates();

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
            image.setCoordinate(position);
            image.setRotation(Probability.getRandomGenerator().nextInt(360));
        }

        if (adjustmentMethod == 1) {

            List<Image> imageCoordinates = getImages().filterType(Base.class).getList();

            // Set position
            if (imageCoordinates.size() == 1) {
                imageCoordinates.get(0).setCoordinate(new Point(0, 0));
            } else if (imageCoordinates.size() == 2) {
                imageCoordinates.get(0).setCoordinate(new Point(-300, 0));
                imageCoordinates.get(1).setCoordinate(new Point(300, 0));
            } else if (imageCoordinates.size() == 5) {
                imageCoordinates.get(0).setCoordinate(new Point(-300, -600));
                imageCoordinates.get(1).setCoordinate(new Point(300, -600));
                imageCoordinates.get(2).setCoordinate(new Point(-300, 0));
                imageCoordinates.get(3).setCoordinate(new Point(300, 0));
                imageCoordinates.get(4).setCoordinate(new Point(-300, 600));
            }

            // Set rotation
            image.setRotation(Probability.getRandomGenerator().nextInt(360));
        }
    }

    /**
     * Returns {@code true} if the {@code Scene} contains a {@code Image} corresponding to the
     * specified {@code Construct}.
     *
     * @param construct The {@code Construct} for which the corresponding {@code Image} will be
     *                  returned, if any.
     * @return The {@code Image} corresponding to the specified {@code Construct}, if one is
     * present. If one is not present, this method returns {@code null}.
     */
    public boolean contains(Construct construct) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Image image = layer.getImage(construct);
            if (image != null) {
                return true;
            }
        }
        return false;
    }

    public Image getImage(Construct construct) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Image image = layer.getImage(construct);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public Construct getModel(Image image) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            Construct construct = layer.getModel(image);
            if (construct != null) {
                return construct;
            }
        }
        return null;
    }

    public <T> List<Image> getImages(List<T> models) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < models.size(); j++) {
                T model = models.get(j);
                Image image = layer.getImage((Construct) model);
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

    public <T extends Construct> ImageGroup getImages(Class<?>... types) {
        return getImages().filterType(types);
    }

    public Image getImageByCoordinate(Point point) {
        List<Image> images = getImages().filterVisibility(Visibility.VISIBLE).getList();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            if (image.contains(point)) {
                return image;
            }
        }
        return this;
    }

    public Universe getModel() {
        return getConstruct();
    }

    public static <T extends Image> List<Point> getCoordinates(List<T> figures) {
        List<Point> positions = new ArrayList<>();
        for (int i = 0; i < figures.size(); i++) {
            T figure = figures.get(i);
            positions.add(figure.getCoordinate());
        }
        return positions;
    }

    public void update() {

        // Update perspective
        getModel().getActor(0).getCamera().update();

        // Update figures
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < layer.getImages().size(); j++) {
                Image image = layer.getImages().get(j);
                image.update();
            }
        }

        // Update figure layout
        // Geometry.computeCirclePacking(getImages().filterType(BaseImage.class, PatchImage.class).getList(), 200, getImages().filterType(BaseImage.class, PatchImage.class).getCentroidPoint());
    }

    @Override
    public void draw(Surface surface) {

        // <DEBUG_LABEL>
        if (Application.ENABLE_GEOMETRY_LABELS) {

            // <AXES_LABEL>
            surface.getPaint().setColor(Color.CYAN);
            surface.getPaint().setStrokeWidth(1.0f);
            surface.getCanvas().drawLine(-5000, 0, 5000, 0, surface.getPaint());
            surface.getCanvas().drawLine(0, -5000, 0, 5000, surface.getPaint());
            // </AXES_LABEL>

        }
        // </DEBUG_LABEL>

        // Draw Layers
        drawLayers(surface);

        // <DEBUG_LABEL>
        if (Application.ENABLE_GEOMETRY_LABELS) {

            // <FPS_LABEL>
            Point fpsCoordinate = getImages().filterType(Base.class).getCenterPoint();
            fpsCoordinate.setY(fpsCoordinate.getY() - 200);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) fpsCoordinate.getX(), (float) fpsCoordinate.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String fpsText = "FPS: " + (int) surface.getRenderer().getFramesPerSecond();
            Rect fpsTextBounds = new Rect();
            surface.getPaint().getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
            surface.getCanvas().drawText(fpsText, (float) fpsCoordinate.getX() + 20, (float) fpsCoordinate.getY() + fpsTextBounds.height() / 2.0f, surface.getPaint());
            // </FPS_LABEL>

            // <CENTROID_LABEL>
            Point centroidCoordinate = getImages().filterType(Base.class).getCentroidPoint();
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) centroidCoordinate.getX(), (float) centroidCoordinate.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String text = "CENTROID";
            Rect bounds = new Rect();
            surface.getPaint().getTextBounds(text, 0, text.length(), bounds);
            surface.getCanvas().drawText(text, (float) centroidCoordinate.getX() + 20, (float) centroidCoordinate.getY() + bounds.height() / 2.0f, surface.getPaint());
            // </CENTROID_LABEL>

            // <CENTER_LABEL>
            List<Point> figureCoordinates = getImages().filterType(Base.class, Patch.class).getCoordinates();
            Point baseImagesCenterCoordinate = Geometry.calculateCenterCoordinate(figureCoordinates);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) baseImagesCenterCoordinate.getX(), (float) baseImagesCenterCoordinate.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            surface.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            surface.getCanvas().drawText(centerLabeltext, (float) baseImagesCenterCoordinate.getX() + 20, (float) baseImagesCenterCoordinate.getY() + centerLabelTextBounds.height() / 2.0f, surface.getPaint());
            // </CENTER_LABEL>

            // <CONVEX_HULL_LABEL>
            List<Point> baseVertices = getImages().filterType(Base.class, Patch.class).getVertices();

            // Hull vertices
            for (int i = 0; i < baseVertices.size() - 1; i++) {

                surface.getPaint().setStrokeWidth(1.0f);
                surface.getPaint().setColor(Color.parseColor("#FF2828"));
                surface.getPaint().setStyle(Paint.Style.FILL);

                Point baseVertex = baseVertices.get(i);
                Surface.drawCircle(baseVertex, 5, 0, surface);
            }

            List<Point> convexHullVertices = Geometry.computeConvexHull(baseVertices);

            surface.getPaint().setStrokeWidth(1.0f);
            surface.getPaint().setColor(Color.parseColor("#2D92FF"));
            surface.getPaint().setStyle(Paint.Style.STROKE);

            // Hull edges
            Surface.drawPolygon(convexHullVertices, surface);

            // Hull vertices
            for (int i = 0; i < convexHullVertices.size() - 1; i++) {

                surface.getPaint().setStrokeWidth(1.0f);
                surface.getPaint().setColor(Color.parseColor("#FF2828"));
                surface.getPaint().setStyle(Paint.Style.STROKE);

                Point vertex = convexHullVertices.get(i);
                Surface.drawCircle(vertex, 20, 0, surface);
            }
            // </CONVEX_HULL_LABEL>

            // <BOUNDING_BOX_LABEL>
            surface.getPaint().setStrokeWidth(1.0f);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.STROKE);

            Rectangle boundingBox = getImages().filterType(Base.class).getBoundingBox();
            Surface.drawPolygon(boundingBox.getVertices(), surface);
            // </BOUNDING_BOX_LABEL>

        }
        // </DEBUG_LABEL>
    }

    private void drawLayers(Surface surface) {

        Layer layer = null;

        layer = getLayer("bases");
        if (layer != null) {
            for (int i = 0; i < layer.getImages().size(); i++) {
                layer.getImages().get(i).draw(surface);
            }
        }

        layer = getLayer("paths");
        if (layer != null) {
            for (int i = 0; i < layer.getImages().size(); i++) {
                layer.getImages().get(i).draw(surface);
            }
        }

        layer = getLayer("patches");
        if (layer != null) {
            for (int i = 0; i < layer.getImages().size(); i++) {
                layer.getImages().get(i).draw(surface);
            }
        }

        layer = getLayer("ports");
        if (layer != null) {
            for (int i = 0; i < layer.getImages().size(); i++) {
                layer.getImages().get(i).draw(surface);
            }
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

    @Override
    public boolean contains(Point point) {
        return false;
    }

    @Override
    public boolean contains(Point point, double padding) {
        return false;
    }

//    public void onTouchListener(Action action) {
//
//        Image targetFigure = getImageByCoordinate(action.getCoordinate());
//        action.setTarget(targetFigure);
//
//        action.getTarget().processAction(action);
//
//    }
//
//    public void onHoldListener(Action action) {
//
//        Image targetFigure = getImageByCoordinate(action.getCoordinate());
//        action.setTarget(targetFigure);
//
//        action.getTarget().processAction(action);
//
//    }

//    public void onMoveListener(Action action) {
//
//        Process actionSequence = action.getActionSequence();
//
//        Image targetFigure = getImageByCoordinate(action.getCoordinate());
//        action.setTarget(targetFigure);
//
//        Camera camera = action.getActor().getCamera();
//
//        if (actionSequence.getSize() > 1) {
//            action.setTarget(actionSequence.getFirstAction().getTarget());
//        }
//
//        // Holding
//        if (actionSequence.isHolding()) {
//
//            // Holding and dragging
//
//            if (action.getTarget() instanceof BaseImage) {
//
////                // Base
////                action.getTarget().processAction(action);
////                action.getTarget().setCoordinate(action.getCoordinate());
////
////                // Camera
////                camera.focusSelectBase(action);
//
//            } else if (action.getTarget() instanceof PortImage) {
//
////                // Port
////                PortImage portFigure = (PortImage) action.getTarget();
////
////                portFigure.setDragging(true);
////                portFigure.setCoordinate(action.getCoordinate());
//
//            } else if (action.getTarget() instanceof Scene) {
//
//                // Scene
////                action.getTarget().processAction(action);
//
//            }
//
//        } else {
//
//            // Not holding. Drag was detected prior to the hold duration threshold.
//
//            if (action.getTarget() instanceof BaseImage) {
//
////                // Base
////                action.getTarget().processAction(action);
////
////                // Camera
////                camera.focusSelectBase(action);
//
//            } else if (action.getTarget() instanceof PortImage) {
//
////                // Port
////                PortImage portFigure = (PortImage) action.getTarget();
////                portFigure.processAction(action);
////
////                // Camera
////                camera.focusCreatePath(action);
//
//            } else if (action.getTarget() instanceof PatchImage) {
//
////                // Patch
////                action.getTarget().setCoordinate(action.getCoordinate());
////                action.getTarget().processAction(action);
//
//            } else if (action.getTarget() instanceof Scene) {
//
////                // Camera
////                if (actionSequence.getSize() > 1) {
////                    camera.setOffset(
////                            action.getCoordinate().getX() - actionSequence.getFirstAction().getCoordinate().getX(),
////                            action.getCoordinate().getY() - actionSequence.getFirstAction().getCoordinate().getY()
////                    );
////
////                }
//
//            }
//        }
//    }

    public void onReleaseListener(Action action) {

        Process process = action.getActionSequence();

        action.setType(Action.Type.UNSELECT);

        Image targetImage = getImageByCoordinate(action.getCoordinate());
        action.setTarget(targetImage);

        Camera camera = action.getActor().getCamera();

        Log.v("Action", "onRelease");
        Log.v("Action", "processAction: " + action.getTarget());
        Log.v("Action", "-");


        if (process.getDuration() < Action.MAXIMUM_TAP_DURATION) {

//            if (action.getTarget() instanceof BaseImage) {
//
////                // Base
////                action.getTarget().processAction(action);
////
////                // Camera
////                camera.focusSelectBase(action);
//
//            } else if (action.getTarget() instanceof PortImage) {
//
//                // Port
////                action.getTarget().processAction(action);
//
//            } else if (action.getTarget() instanceof PathImage) {
//
//                // Path
////                action.getTarget().processAction(action);
//
//            } else if (action.getTarget() instanceof PatchImage) {
//
//                // Patch
////                action.getTarget().processAction(action);
//
//            } else if (action.getTarget() instanceof Scene) {
//
////                // Scene
////                action.getTarget().processAction(action);
////
////                // Camera
////                camera.focusSelectScene();
//
//            }

        } else {

            action.setType(Action.Type.UNSELECT);

//            action.setTrigger(
//                    Action.Type.NONE,
//                    Action.Type.SELECT,
//                    Action.Type.MOVE,
//                    *,
//                    Action.Type.UNSELECT
//            );

            // onSequence (BaseImage.class, ..., Image.class, null, ) { ... }
            // onSequence (BaseImage.class, *, Image.class, null, ) { ... }

            // First processAction was on a base figure...
            if (process.getFirstAction().getTarget() instanceof BaseImage) {

                if (action.getTarget() instanceof BaseImage) {

                    // If getFirstAction processAction was on the same form, then respond
                    if (process.getFirstAction().isPointing() && process.getFirstAction().getTarget() instanceof BaseImage) {

                        // Base
                        action.getTarget().processAction(action);

                        // Camera
//                        camera.focusSelectScene();
                    }

                } else if (action.getTarget() instanceof Scene) {

                    // Base
                    process.getFirstAction().getTarget().processAction(action);

                }

            } else if (process.getFirstAction().getTarget() instanceof PortImage) {

                // First processAction was on a port figure...

                if (action.getTarget() instanceof BaseImage) {

                    // ...getLastAction processAction was on a base figure.

                    PortImage sourcePortImage = (PortImage) process.getFirstAction().getTarget();
                    sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);

                } else if (action.getTarget() instanceof PortImage) {

                    // Port
                    action.getTarget().processAction(action);

                } else if (action.getTarget() instanceof PatchImage) {

                    // Patch
                    action.getTarget().processAction(action);

                } else if (action.getTarget() instanceof Scene) {

                    action.getTarget().processAction(action);

                }

            } else if (process.getFirstAction().getTarget() instanceof PathImage) {

                // Path --> ?

                if (action.getTarget() instanceof PathImage) {
                    // Path --> Path
                    PathImage pathImage = (PathImage) action.getTarget();
                }

            } else if (process.getFirstAction().getTarget() instanceof Scene) {

                // Scene --> ?

                // Check if getFirstAction processAction was on an figure
                if (process.getFirstAction().getTarget() instanceof PortImage) {
                    ((PortImage) process.getFirstAction().getTarget()).setCandidatePathVisibility(Visibility.INVISIBLE);
                }

//                camera.focusSelectScene();

            }

        }
    }
}
