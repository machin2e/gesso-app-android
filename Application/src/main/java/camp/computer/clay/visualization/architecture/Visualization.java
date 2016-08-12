package camp.computer.clay.visualization.architecture;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Construct;
import camp.computer.clay.model.architecture.Model;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.model.interactivity.ActionListener;
import camp.computer.clay.model.interactivity.Interaction;
import camp.computer.clay.model.interactivity.Perspective;
import camp.computer.clay.visualization.image.FrameImage;
import camp.computer.clay.visualization.image.PatchImage;
import camp.computer.clay.visualization.image.PathImage;
import camp.computer.clay.visualization.image.PortImage;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.Probability;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;

public class Visualization extends Image {

    public static <T extends Image> List<Point> getPositions(List<T> images) {
        List<Point> positions = new ArrayList<>();
        for (T image : images) {
            positions.add(image.getPosition());
        }
        return positions;
    }

    private List<Layer> layers = new ArrayList<>();

    public Visualization(Model model) {
        super(model);
        setup();
    }

    private void setup() {
        setupInteractivity();
    }

    private void setupInteractivity() {

        // Setup interactivity
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Interaction interaction = action.getInteraction();

                Image targetImage = getImageByPosition(action.getPosition());
                action.setTarget(targetImage);

                Perspective perspective = action.getBody().getPerspective();

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.TOUCH) {

                } else if (action.getType() == Action.Type.HOLD) {

                } else if (action.getType() == Action.Type.MOVE) {

                    if (perspective.isAdjustable()) {

//                        perspective.setScale(0.9f);
//                        perspective.setOffset(
//                                action.getPosition().getX() - interaction.getFirst().getPosition().getX(),
//                                action.getPosition().getY() - interaction.getFirst().getPosition().getY()
//                        );

                        perspective.focusOnVisualization(interaction);

                    }

                } else if (action.getType() == Action.Type.RELEASE) {

                    action.setType(Action.Type.RELEASE);

                    Log.v("Action", "onRelease");
                    Log.v("Action", "focus: " + perspective.getFocus());
                    Log.v("Action", "processAction: " + action.getTarget());
                    Log.v("Action", "-");

                    if (interaction.getDuration() < Action.MAX_TAP_DURATION) {

                    } else {

                        PortImage sourcePortImage = (PortImage) action.getInteraction().getFirst().getTarget();

                        if (sourcePortImage.getCandidatePeripheralVisibility() == Visibility.VISIBLE) {

                            Log.v("IASM", "(1) touch patch to select from store or (2) drag signal to frame or (3) touch elsewhere to cancel");

                            // Construct
                            Patch patch = new Patch();
                            // patch.setParent(getModel());

                            // Add port to construct
                            // for (int j = 0; j < 3; j++) {
                            for (int j = 0; j < 1; j++) {
                                Port port = new Port();
                                patch.addPort(port);
                            }

                            getEnvironment().addPatch(patch);

                            // Create Patch Image
                            PatchImage patchImage = new PatchImage(patch);
                            patchImage.setPosition(action.getPosition());

                            double pathRotationAngle = Geometry.calculateRotationAngle(
                                    sourcePortImage.getPosition(),
                                    patchImage.getPosition()
                            );
                            patchImage.setRotation(pathRotationAngle + 90);

                            // Create Port Images for each of Patch's Ports
                            for (Port port : patch.getPorts()) {
                                PortImage portImage = new PortImage(port);
                                addImage(portImage, "ports");
                            }

                            // Add Patch Image to Visualization
                            addImage(patchImage, "patches");

                            // Configure Ports
                            Port sourcePort = sourcePortImage.getPort();
                            Port destinationPort = patch.getPorts().get(0);

                            if (sourcePort.getDirection() == Port.Direction.NONE) {
                                sourcePort.setDirection(Port.Direction.OUTPUT);
                            }
//                        if (sourcePort.getType() == Port.Type.NONE) {
                            //sourcePort.setType(Port.Type.next(sourcePort.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
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

                            // Update Perspective
                            perspective.focusOnPath(sourcePortImage.getPort());

                        }

                        // Update Image
                        sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);
                        sourcePortImage.setCandidatePeripheralVisibility(Visibility.INVISIBLE);
                    }
                }
            }
        });
    }

    public boolean hasLayer(String tag) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public void addLayer(String tag) {
        if (!hasLayer(tag)) {
            Layer layer = new Layer(this);
            layer.setTag(tag);
            layers.add(layer);
        }
    }

    // TODO: Remove Image parameter. Create that and return it.
    public void addImage(Image image, String layerTag) {

        // Position image
        if (image instanceof FrameImage) {
            locateImagePosition(image);
        }

        // Add image
        if (!hasLayer(layerTag)) {
            addLayer(layerTag);
        }
        getLayer(layerTag).add(image);

        // Update perspective
//        getModel().getBody(0).getPerspective().adjustScale(0);
        // getModel().getBody(0).getPerspective().setPosition(getModel().getBody(0).getPerspective().getVisualization().getImages().filterType(FrameImage.TYPE).getCenterPoint());
        getEnvironment().getBody(0).getPerspective().adjustPosition();
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
        for (Layer layer : getLayers()) {
            if (layer.getIndex() == id) {
                return layer;
            }
        }
        return null;
    }

    private void locateImagePosition(Image image) {

        // Calculate random positions separated by minimum distance
        final float imageSeparationDistance = 550; // 500;

        List<Point> imagePositions = getImages().filterType(FrameImage.class).getPositions();

        Point position = null;
        boolean foundPoint = false;

        Log.v("Position", "imagePositions.size = " + imagePositions.size());

        if (imagePositions.size() == 0) {

            position = new Point(0, 0);

        } else if (imagePositions.size() == 1) {

            position = Geometry.calculatePoint(
                    imagePositions.get(0),
                    Probability.generateRandomInteger(0, 360),
                    imageSeparationDistance
            );

        } else {

            List<Point> hullPoints = Geometry.computeConvexHull(imagePositions);

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

    public Image getImage(Construct construct) {
        for (Layer layer : getLayers()) {
            Image image = layer.getImage(construct);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public Construct getModel(Image image) {
        for (Layer layer : getLayers()) {
            Construct construct = layer.getModel(image);
            if (construct != null) {
                return construct;
            }
        }
        return null;
    }

    public List<FrameImage> getFrameImages() {

        List<FrameImage> images = new ArrayList<>();

        for (Layer layer : getLayers()) {
            for (Image image : layer.getImages()) {
                if (image instanceof FrameImage) {
                    images.add((FrameImage) image);
                }
            }
        }

        return images;
    }

    public List<PortImage> getPortImages() {

        List<PortImage> sprites = new ArrayList<>();

        for (Layer layer : getLayers()) {
            for (Image image : layer.getImages()) {
                if (image instanceof PortImage) {
                    sprites.add((PortImage) image);
                }
            }
        }

        return sprites;
    }

    public <T> List<Image> getImages(List<T> models) {
        List<Image> images = new ArrayList<>();
        for (Layer layer : getLayers()) {
            for (T model : models) {
                Image image = layer.getImage((Construct) model);
                if (image != null) {
                    images.add(image);
                }
            }
        }
        return images;
    }

    public ImageSet getImages() {
        ImageSet imageSet = new ImageSet();
        for (Integer index : getLayerIndices()) {
            Layer layer = getLayer(index);
            if (layer != null) {
                imageSet.add(layer.getImages());
            }
        }
        return imageSet;
    }

    public Image getImageByPosition(Point point) {
        for (Image image : getImages().filterVisibility(Visibility.VISIBLE).getList()) {
            if (image.containsPoint(point)) {
                return image;
            }
        }
        return this;
    }

    public Model getEnvironment() {
        return (Model) getConstruct();
    }

    public void update() {

        getEnvironment().getBody(0).getPerspective().update();

        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < layer.getImages().size(); j++) {
                Image image = layer.getImages().get(j);
                image.update();
            }
        }
    }

    @Override
    public void draw(Surface surface) {

        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
            // <AXES_ANNOTATION>
            surface.getPaint().setColor(Color.CYAN);
            surface.getPaint().setStrokeWidth(1.0f);
            surface.getCanvas().drawLine(-5000, 0, 5000, 0, surface.getPaint());
            surface.getCanvas().drawLine(0, -5000, 0, 5000, surface.getPaint());
            // </AXES_ANNOTATION>
        }

        // Draw images
        for (Integer index : getLayerIndices()) {
            Layer layer = getLayer(index);
            if (layer != null) {
                for (int i = 0; i < layer.getImages().size(); i++) {
                    layer.getImages().get(i).draw(surface);
                }
            }
        }

        // Layout images
        Geometry.computeCirclePacking(getImages().filterType(FrameImage.class, PatchImage.class).getList(), 200, getImages().filterType(FrameImage.class, PatchImage.class).getCentroidPoint());

        // Draw annotations
        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {

            // <FPS_ANNOTATION>
            Point fpsPosition = getImages().filterType(FrameImage.class).getCenterPoint();
            fpsPosition.setY(fpsPosition.getY() - 200);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) fpsPosition.getX(), (float) fpsPosition.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String fpsText = "FPS: " + (int) surface.getRenderer().getFramesPerSecond();
            Rect fpsTextBounds = new Rect();
            surface.getPaint().getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
            surface.getCanvas().drawText(fpsText, (float) fpsPosition.getX() + 20, (float) fpsPosition.getY() + fpsTextBounds.height() / 2.0f, surface.getPaint());
            // </FPS_ANNOTATION>

            // <CENTROID_ANNOTATION>
            Point centroidPosition = getImages().filterType(FrameImage.class).getCentroidPoint();
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) centroidPosition.getX(), (float) centroidPosition.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String text = "CENTROID";
            Rect bounds = new Rect();
            surface.getPaint().getTextBounds(text, 0, text.length(), bounds);
            surface.getCanvas().drawText(text, (float) centroidPosition.getX() + 20, (float) centroidPosition.getY() + bounds.height() / 2.0f, surface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CENTER_ANNOTATION>
            List<Point> formImagePositions = getImages().filterType(FrameImage.class).getPositions();
            Point formImagesCenterPosition = Geometry.calculateCenterPosition(formImagePositions);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) formImagesCenterPosition.getX(), (float) formImagesCenterPosition.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            surface.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            surface.getCanvas().drawText(centerLabeltext, (float) formImagesCenterPosition.getX() + 20, (float) formImagesCenterPosition.getY() + centerLabelTextBounds.height() / 2.0f, surface.getPaint());
            // </CENTER_ANNOTATION>

            // <CONVEX_HULL>
            //List<Point> framePositions = Visualization.getPositions(getFrameImages());
            List<Point> frameVertices = getImages().filterType(FrameImage.class).getAbsoluteVertices();

            // Hull vertices
            for (int i = 0; i < frameVertices.size() - 1; i++) {

                surface.getPaint().setStrokeWidth(1.0f);
                surface.getPaint().setColor(Color.parseColor("#FF2828"));
                surface.getPaint().setStyle(Paint.Style.FILL);

                Point frameVertex = frameVertices.get(i);
                Surface.drawCircle(frameVertex, 5, 0, surface);
            }

            List<Point> convexHullVertices = Geometry.computeConvexHull(frameVertices);

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
            // </CONVEX_HULL>

            // <BOUNDING_BOX>
            surface.getPaint().setStrokeWidth(1.0f);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.STROKE);

            Rectangle boundingBox = getImages().filterType(FrameImage.class).getBoundingBox();
            Surface.drawPolygon(boundingBox.getVertices(), surface);
            // </BOUNDING_BOX>
        }
    }

    public List<Integer> getLayerIndices() {
        List<Integer> layers = new ArrayList<>();
        for (Layer layer : getLayers()) {
            layers.add(layer.getIndex());
        }
        Collections.sort(layers);
        return layers;
    }

    public List<Layer> getLayers() {
        return new ArrayList<>(this.layers);
    }

    @Override
    public boolean containsPoint(Point point) {
        return false;
    }

    @Override
    public boolean containsPoint(Point point, double padding) {
        return false;
    }

    public void onTouchListener(Action action) {

        Image targetImage = getImageByPosition(action.getPosition());
        action.setTarget(targetImage);

    }

    public void onMoveListener(Action action) {

        Interaction interaction = action.getInteraction();

        Image targetImage = getImageByPosition(action.getPosition());
        action.setTarget(targetImage);

        Perspective perspective = action.getBody().getPerspective();

        Log.v("onMoveListener", "" + action.getType() + ": " + action.getTarget());

        Log.v("Action", "onDrag");
        Log.v("Action", "focus: " + perspective.getFocus());
        Log.v("Action", "processAction: " + action.getTarget());
        Log.v("Action", "-");

        if (interaction.getSize() > 1) {
            action.setTarget(interaction.getFirst().getTarget());
        }

        // Holding
        if (interaction.isHolding()) {

            // Holding and dragging

            if (action.getTarget() instanceof FrameImage) {

                // Frame
                action.getTarget().processAction(action);
                action.getTarget().setPosition(action.getPosition());

                // Perspective
                perspective.focusOnFrame(action);

            } else if (action.getTarget() instanceof PortImage) {

                // Port
                PortImage portImage = (PortImage) action.getTarget();

                portImage.setDragging(true);
                portImage.setPosition(action.getPosition());

            } else if (action.getTarget() instanceof Visualization) {

                // Visualization
                action.getTarget().processAction(action);

            }

        } else {

            // Not holding. Drag was detected prior to the hold duration threshold.

            if (action.getTarget() instanceof FrameImage) {

                // Frame
                action.getTarget().processAction(action);
                action.getTarget().setPosition(action.getPosition());

                // Perspective
                perspective.focusOnFrame(action);

            } else if (action.getTarget() instanceof PortImage) {

                // Port
                PortImage portImage = (PortImage) action.getTarget();
                portImage.processAction(action);

                // Perspective
                perspective.focusOnNewPath(interaction, action);

            } else if (action.getTarget() instanceof PatchImage) {

                // Patch
                action.getTarget().setPosition(action.getPosition());
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof Visualization) {

                // Perspective
                if (interaction.getSize() > 1) {
                    perspective.setOffset(
                            action.getPosition().getX() - interaction.getFirst().getPosition().getX(),
                            action.getPosition().getY() - interaction.getFirst().getPosition().getY()
                    );

                }

            }
        }
    }

    public void onReleaseListener(Action action) {

        Interaction interaction = action.getInteraction();

        action.setType(Action.Type.RELEASE);

        Image targetImage = getImageByPosition(action.getPosition());
        action.setTarget(targetImage);

        Perspective perspective = action.getBody().getPerspective();

        Log.v("Action", "onRelease");
        Log.v("Action", "focus: " + perspective.getFocus());
        Log.v("Action", "processAction: " + action.getTarget());
        Log.v("Action", "-");


        if (interaction.getDuration() < Action.MAX_TAP_DURATION) {

            if (action.getTarget() instanceof FrameImage) {

                // Frame
                action.getTarget().processAction(action);

                // Perspective
                perspective.focusOnFrame(action);

            } else if (action.getTarget() instanceof PortImage) {

                // Port
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof PathImage) {

                // Path
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof PatchImage) {

                // Patch
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof Visualization) {

                // Visualization
                action.getTarget().processAction(action);

                // Perspective
                perspective.focusReset();

            }

        } else {

            action.setType(Action.Type.RELEASE);


            // First processAction was on a frame image...
            if (interaction.getFirst().getTarget() instanceof FrameImage) {

                if (action.getTarget() instanceof FrameImage) {

                    // If first processAction was on the same form, then respond
                    if (interaction.getFirst().isTouching() && interaction.getFirst().getTarget() instanceof FrameImage) {

                        // Frame
                        action.getTarget().processAction(action);

                        // Perspective
                        perspective.focusReset();
                    }

                }

            } else if (interaction.getFirst().getTarget() instanceof PortImage) {

                // First processAction was on a port image...

                if (action.getTarget() instanceof FrameImage) {

                    // ...last processAction was on a frame image.

                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTarget();
                    sourcePortImage.setCandidatePathVisibility(Visibility.INVISIBLE);

                } else if (action.getTarget() instanceof PortImage) {

                    // Port
                    action.getTarget().processAction(action);

                } else if (action.getTarget() instanceof PatchImage) {

                    // Patch
                    action.getTarget().processAction(action);

                } else if (action.getTarget() instanceof Visualization) {

                    action.getTarget().processAction(action);

                }

            } else if (interaction.getFirst().getTarget() instanceof PathImage) {

                // Path --> ?

                if (action.getTarget() instanceof PathImage) {
                    // Path --> Path
                    PathImage pathImage = (PathImage) action.getTarget();
                }

            } else if (interaction.getFirst().getTarget() instanceof Visualization) {

                // Visualization --> ?

                // Check if first processAction was on an image
                if (interaction.getFirst().getTarget() instanceof PortImage) {
                    ((PortImage) interaction.getFirst().getTarget()).setCandidatePathVisibility(Visibility.INVISIBLE);
                }

                perspective.focusReset();

            }

            // Interaction
            perspective.setAdjustability(true);

        }
    }
}
