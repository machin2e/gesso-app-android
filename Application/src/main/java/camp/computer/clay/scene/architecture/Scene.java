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
import camp.computer.clay.model.architecture.Model;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.model.interaction.Camera;
import camp.computer.clay.model.interaction.Pattern;
import camp.computer.clay.scene.figure.BaseFigure;
import camp.computer.clay.scene.figure.PatchFigure;
import camp.computer.clay.scene.figure.PathFigure;
import camp.computer.clay.scene.figure.PortFigure;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.Probability;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

public class Scene extends Figure<Model> {

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
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Pattern pattern = action.getPattern();

                Figure targetFigure = getFigureByPosition(action.getPosition());
                action.setTarget(targetFigure);

                Camera camera = action.getActor().getCamera();

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.TOUCH) {

                } else if (action.getType() == Action.Type.HOLD) {

                    // Select patch to connect
                    Application.getDisplay().displayOptionsDialog();

                } else if (action.getType() == Action.Type.MOVE) {

//                    camera.setScale(0.9f);
//                    camera.setOffset(
//                            action.getPosition().getX() - pattern.getFirst().getPosition().getX(),
//                            action.getPosition().getY() - pattern.getFirst().getPosition().getY()
//                    );

                    camera.focusMoveView(action);

                } else if (action.getType() == Action.Type.RELEASE) {

                    action.setType(Action.Type.RELEASE);

                    Log.v("Action", "onRelease");
                    Log.v("Action", "processAction: " + action.getTarget());
                    Log.v("Action", "-");

                    if (pattern.getDuration() < Action.MAXIMUM_TAP_DURATION) {

//                        if (goalVisibility == Visibility.INVISIBLE) {
//                            goalVisibility = Visibility.VISIBLE;
//                        } else {
//                            goalVisibility = Visibility.INVISIBLE;
//                        }

                    } else {

                        PortFigure sourcePortFigure = (PortFigure) action.getPattern().getFirst().getTarget();

                        if (sourcePortFigure.getCandidatePatchVisibility() == Visibility.VISIBLE) {

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

                            // Create Patch Figure
                            PatchFigure patchFigure = new PatchFigure(patch);
                            patchFigure.setPosition(action.getPosition());

                            // Set Rotation
                            double patchRotation = Geometry.calculateRotationAngle(
                                    sourcePortFigure.getPosition(),
                                    patchFigure.getPosition()
                            );
                            patchFigure.setRotation(patchRotation + 90);

//                            Base sourceBase = (Base) sourcePortFigure.getConstruct().getParent();
//                            BaseFigure sourceBaseFigure = (BaseFigure) getFigure(sourceBase);
//                            patchFigure.setRotation(sourceBaseFigure.getRotation() + 180);

                            // Create Port Figures for each of Patch's Ports
                            for (Port port : patch.getPorts()) {
                                PortFigure portFigure = new PortFigure(port);
                                addFigure(portFigure, "ports");
                            }

                            // Add Patch Figure to Scene
                            addFigure(patchFigure, "patches");

                            // Configure Ports
                            Port sourcePort = sourcePortFigure.getPort();
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
                            Path path = new Path(sourcePortFigure.getPort(), patch.getPorts().get(0));
                            path.setType(Path.Type.ELECTRONIC);
                            sourcePort.addPath(path);

                            PathFigure pathFigure = new PathFigure(path);
                            addFigure(pathFigure, "paths");

                            PortFigure targetPortFigure = (PortFigure) getFigure(path.getTarget());
                            targetPortFigure.setUniqueColor(sourcePortFigure.getUniqueColor());

                            // Update Camera
                            camera.focusSelectPath(sourcePortFigure.getPort());

                        }

                        // Update Figure
                        sourcePortFigure.setCandidatePathVisibility(Visibility.INVISIBLE);
                        sourcePortFigure.setCandidatePatchVisibility(Visibility.INVISIBLE);
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
            BaseFigure baseFigure = new BaseFigure(base);

            // Setup base's port figures
            // Add a port sprite for each of the associated base's ports
            for (Port port : base.getPorts()) {
//                PortFigure portFigure = new PortFigure(port);
//                scene.addFigure(portFigure, "ports");

                addConstruct(port);

            }

            addFigure(baseFigure, "bases");

        } else if (construct instanceof Port) {

            Port port = (Port) construct;

            PortFigure portFigure = new PortFigure(port);
            addFigure(portFigure, "ports");

        } else if (construct instanceof Path) {

            Path path = (Path) construct;

            PathFigure pathFigure = new PathFigure(path);
            // pathFigure.setScene(getScene());
            addFigure(pathFigure, "paths");

        }

    }

    // TODO: Remove Figure parameter. Create that and return it.
    private void addFigure(Figure figure, String layerTag) {

        // Position figure
        if (figure instanceof BaseFigure) {
            locateFigurePosition(figure);
        }

        // Add figure
        if (!hasLayer(layerTag)) {
            addLayer(layerTag);
        }
        getLayer(layerTag).add(figure);

        // Update perspective
//        getModel().getActor(0).getCamera().adjustScale(0);
        // getModel().getActor(0).getCamera().setPosition(getModel().getActor(0).getCamera().getScene().getFigures().filterType(BaseFigure.TYPE).getCenterPoint());
//        getModel().getActor(0).getCamera().adjustPosition();


        getModel().getActor(0).getCamera().focusSelectVisualization();
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

    private void locateFigurePosition(Figure figure) {

        // Calculate random positions separated by minimum distance
        final float figureSeparationDistance = 550; // 500;

        List<Point> figurePositions = getFigures().filterType(Base.class).getPositions();

        Point position = null;
        boolean foundPoint = false;

        Log.v("Position", "figurePositions.size = " + figurePositions.size());

        if (figurePositions.size() == 0) {

            position = new Point(0, 0);

        } else if (figurePositions.size() == 1) {

            position = Geometry.calculatePoint(
                    figurePositions.get(0),
                    Probability.generateRandomInteger(0, 360),
                    figureSeparationDistance
            );

        } else {

            List<Point> hullPoints = Geometry.computeConvexHull(figurePositions);

            int sourceIndex = Probability.generateRandomInteger(0, hullPoints.size() - 1);
            int targetIndex = sourceIndex + 1;

            Point midpoint = Geometry.calculateMidpoint(hullPoints.get(sourceIndex), hullPoints.get(targetIndex));
            position = Geometry.calculatePoint(
                    midpoint,
                    Geometry.calculateRotationAngle(hullPoints.get(sourceIndex), hullPoints.get(targetIndex)) + 90,
                    figureSeparationDistance
            );
        }

        // Assign the found position to the figure
        figure.setPosition(position);
        figure.setRotation(Probability.getRandomGenerator().nextInt(360));
    }

    public boolean hasFigure(Construct construct) {
        for (Layer layer : getLayers()) {
            Figure figure = layer.getFigure(construct);
            if (figure != null) {
                return true;
            }
        }
        return false;
    }

    public Figure getFigure(Construct construct) {
        for (Layer layer : getLayers()) {
            Figure figure = layer.getFigure(construct);
            if (figure != null) {
                return figure;
            }
        }
        return null;
    }

    public Construct getModel(Figure figure) {
        for (Layer layer : getLayers()) {
            Construct construct = layer.getModel(figure);
            if (construct != null) {
                return construct;
            }
        }
        return null;
    }

    public <T> List<Figure> getFigures(List<T> models) {
        List<Figure> figures = new ArrayList<>();
        for (Layer layer : getLayers()) {
            for (T model : models) {
                Figure figure = layer.getFigure((Construct) model);
                if (figure != null) {
                    figures.add(figure);
                }
            }
        }
        return figures;
    }

    public FigureSet getFigures() {
        FigureSet figureSet = new FigureSet();
        for (Integer index : getLayerIndices()) {
            Layer layer = getLayer(index);
            if (layer != null) {
                figureSet.add(layer.getFigures());
            }
        }
        return figureSet;
    }

    public <T extends Construct> FigureSet getFigures(Class<?>... types) {
        return getFigures().filterType(types);
    }

    public Figure getFigureByPosition(Point point) {
        for (Figure figure : getFigures().filterVisibility(Visibility.VISIBLE).getList()) {
            if (figure.containsPoint(point)) {
                return figure;
            }
        }
        return this;
    }

    public Model getModel() {
        return getConstruct();
    }

    public static <T extends Figure> List<Point> getPositions(List<T> figures) {
        List<Point> positions = new ArrayList<>();
        for (T figure : figures) {
            positions.add(figure.getPosition());
        }
        return positions;
    }

    public void update() {

        // Update perspective
        getModel().getActor(0).getCamera().update();

        // Update figures
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            for (int j = 0; j < layer.getFigures().size(); j++) {
                Figure figure = layer.getFigures().get(j);
                figure.update();
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

        // Draw figures
//        for (Integer index : getLayerIndices()) {
//            Layer layer = getLayer(index);
//            if (layer != null) {
//                for (int i = 0; i < layer.getFigures().size(); i++) {
//                    layer.getFigures().get(i).draw(surface);
//                }
//            }

        Layer layer = null;

        layer = getLayer("bases");
        if (layer != null) {
            for (int i = 0; i < layer.getFigures().size(); i++) {
                layer.getFigures().get(i).draw(surface);
            }
        }

        layer = getLayer("paths");
        if (layer != null) {
            for (int i = 0; i < layer.getFigures().size(); i++) {
                layer.getFigures().get(i).draw(surface);
            }
        }

        layer = getLayer("patches");
        if (layer != null) {
            for (int i = 0; i < layer.getFigures().size(); i++) {
                layer.getFigures().get(i).draw(surface);
            }
        }

        layer = getLayer("ports");
        if (layer != null) {
            for (int i = 0; i < layer.getFigures().size(); i++) {
                layer.getFigures().get(i).draw(surface);
            }
        }

//            getLayer("paths").getFigures().get(i).draw(surface);
//            getLayer("patches").getFigures().get(i).draw(surface);
//            getLayer("ports").getFigures().get(i).draw(surface);
//        }

        // Layout figures
//        Geometry.computeCirclePacking(getFigures().filterType(BaseFigure.class, PatchFigure.class).getList(), 200, getFigures().filterType(BaseFigure.class, PatchFigure.class).getCentroidPoint());

        // Draw annotations
        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {

            // <FPS_ANNOTATION>
            Point fpsPosition = getFigures().filterType(Base.class).getCenterPoint();
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
            Point centroidPosition = getFigures().filterType(Base.class).getCentroidPoint();
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
            List<Point> figurePositions = getFigures().filterType(Base.class, Patch.class).getPositions();
            Point baseFiguresCenterPosition = Geometry.calculateCenterPosition(figurePositions);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) baseFiguresCenterPosition.getX(), (float) baseFiguresCenterPosition.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            surface.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            surface.getCanvas().drawText(centerLabeltext, (float) baseFiguresCenterPosition.getX() + 20, (float) baseFiguresCenterPosition.getY() + centerLabelTextBounds.height() / 2.0f, surface.getPaint());
            // </CENTER_ANNOTATION>

            // <CONVEX_HULL>
            //List<Point> basePositions = Scene.getPositions(getBaseFigures());
            List<Point> baseVertices = getFigures().filterType(Base.class, Patch.class).getVertices();

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
            // </CONVEX_HULL>

            // <BOUNDING_BOX>
            surface.getPaint().setStrokeWidth(1.0f);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.STROKE);

            Rectangle boundingBox = getFigures().filterType(Base.class).getBoundingBox();
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

        Figure targetFigure = getFigureByPosition(action.getPosition());
        action.setTarget(targetFigure);

        action.getTarget().processAction(action);

    }

    public void onHoldListener(Action action) {

        Figure targetFigure = getFigureByPosition(action.getPosition());
        action.setTarget(targetFigure);

        action.getTarget().processAction(action);

    }

    public void onMoveListener(Action action) {

        Pattern pattern = action.getPattern();

        Figure targetFigure = getFigureByPosition(action.getPosition());
        action.setTarget(targetFigure);

        Camera camera = action.getActor().getCamera();

        if (pattern.getSize() > 1) {
            action.setTarget(pattern.getFirst().getTarget());
        }

        // Holding
        if (pattern.isHolding()) {

            // Holding and dragging

            if (action.getTarget() instanceof BaseFigure) {

                // Base
                action.getTarget().processAction(action);
                action.getTarget().setPosition(action.getPosition());

                // Camera
                camera.focusSelectBase(action);

            } else if (action.getTarget() instanceof PortFigure) {

                // Port
                PortFigure portFigure = (PortFigure) action.getTarget();

                portFigure.setDragging(true);
                portFigure.setPosition(action.getPosition());

            } else if (action.getTarget() instanceof Scene) {

                // Scene
                action.getTarget().processAction(action);

            }

        } else {

            // Not holding. Drag was detected prior to the hold duration threshold.

            if (action.getTarget() instanceof BaseFigure) {

                // Base
                action.getTarget().processAction(action);

                // Camera
                camera.focusSelectBase(action);

            } else if (action.getTarget() instanceof PortFigure) {

                // Port
                PortFigure portFigure = (PortFigure) action.getTarget();
                portFigure.processAction(action);

                // Camera
                camera.focusCreatePath(action);

            } else if (action.getTarget() instanceof PatchFigure) {

                // Patch
                action.getTarget().setPosition(action.getPosition());
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof Scene) {

                // Camera
                if (pattern.getSize() > 1) {
                    camera.setOffset(
                            action.getPosition().getX() - pattern.getFirst().getPosition().getX(),
                            action.getPosition().getY() - pattern.getFirst().getPosition().getY()
                    );

                }

            }
        }
    }

    public void onReleaseListener(Action action) {

        Pattern pattern = action.getPattern();

        action.setType(Action.Type.RELEASE);

        Figure targetFigure = getFigureByPosition(action.getPosition());
        action.setTarget(targetFigure);

        Camera camera = action.getActor().getCamera();

        Log.v("Action", "onRelease");
        Log.v("Action", "processAction: " + action.getTarget());
        Log.v("Action", "-");


        if (pattern.getDuration() < Action.MAXIMUM_TAP_DURATION) {

            if (action.getTarget() instanceof BaseFigure) {

                // Base
                action.getTarget().processAction(action);

                // Camera
                camera.focusSelectBase(action);

            } else if (action.getTarget() instanceof PortFigure) {

                // Port
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof PathFigure) {

                // Path
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof PatchFigure) {

                // Patch
                action.getTarget().processAction(action);

            } else if (action.getTarget() instanceof Scene) {

                // Scene
                action.getTarget().processAction(action);

                // Camera
                camera.focusSelectVisualization();

            }

        } else {

            action.setType(Action.Type.RELEASE);

//            action.setTrigger(
//                    Action.Type.NONE,
//                    Action.Type.TOUCH,
//                    Action.Type.MOVE,
//                    *,
//                    Action.Type.RELEASE
//            );

            // onSequence (BaseFigure.class, ..., Figure.class, null, ) { ... }
            // onSequence (BaseFigure.class, *, Figure.class, null, ) { ... }

            // First processAction was on a base figure...
            if (pattern.getFirst().getTarget() instanceof BaseFigure) {

                if (action.getTarget() instanceof BaseFigure) {

                    // If first processAction was on the same form, then respond
                    if (pattern.getFirst().isPointing() && pattern.getFirst().getTarget() instanceof BaseFigure) {

                        // Base
                        action.getTarget().processAction(action);

                        // Camera
//                        camera.focusSelectVisualization();
                    }

                } else if (action.getTarget() instanceof Scene) {

                    // Base
                    pattern.getFirst().getTarget().processAction(action);

                }

            } else if (pattern.getFirst().getTarget() instanceof PortFigure) {

                // First processAction was on a port figure...

                if (action.getTarget() instanceof BaseFigure) {

                    // ...last processAction was on a base figure.

                    PortFigure sourcePortFigure = (PortFigure) pattern.getFirst().getTarget();
                    sourcePortFigure.setCandidatePathVisibility(Visibility.INVISIBLE);

                } else if (action.getTarget() instanceof PortFigure) {

                    // Port
                    action.getTarget().processAction(action);

                } else if (action.getTarget() instanceof PatchFigure) {

                    // Patch
                    action.getTarget().processAction(action);

                } else if (action.getTarget() instanceof Scene) {

                    action.getTarget().processAction(action);

                }

            } else if (pattern.getFirst().getTarget() instanceof PathFigure) {

                // Path --> ?

                if (action.getTarget() instanceof PathFigure) {
                    // Path --> Path
                    PathFigure pathFigure = (PathFigure) action.getTarget();
                }

            } else if (pattern.getFirst().getTarget() instanceof Scene) {

                // Scene --> ?

                // Check if first processAction was on an figure
                if (pattern.getFirst().getTarget() instanceof PortFigure) {
                    ((PortFigure) pattern.getFirst().getTarget()).setCandidatePathVisibility(Visibility.INVISIBLE);
                }

//                camera.focusSelectVisualization();

            }

        }
    }
}
