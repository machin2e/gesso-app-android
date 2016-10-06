package camp.computer.clay.util.image;

import android.graphics.Color;
import android.graphics.Paint;
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
import camp.computer.clay.util.image.util.ImageGroup;
import camp.computer.clay.util.image.util.ShapeGroup;

public class Space extends Image<Model> {

    protected ImageGroup images = new ImageGroup();

    protected Visibility extensionPrototypeVisibility = new Visibility(Visibility.Value.INVISIBLE);
    protected Point extensionPrototypePosition = new Point();

    protected Visibility pathPrototypeVisibility = new Visibility(Visibility.Value.INVISIBLE);
    protected Point pathPrototypeSourcePosition = new Point(0, 0);
    protected Point pathPrototypeDestinationCoordinate = new Point(0, 0);

    public Space(Model model) {
        super(model);
        setup();
    }

    private void setup() {
        setupActions();
    }

    // TODO: Allow user to setAbsolute and change a goal. Track it in relation to the actions taken and things built.
    protected Visibility titleVisibility = new Visibility(Visibility.Value.INVISIBLE);
    protected String titleText = "Project";

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleVisibility(Visibility.Value visibility) {
        if (titleVisibility.getValue() == Visibility.Value.INVISIBLE && visibility == Visibility.Value.VISIBLE) {
            Application.getView().openTitleEditor(getTitleText());
            this.titleVisibility.setValue(visibility);
        } else if (titleVisibility.getValue() == Visibility.Value.VISIBLE && visibility == Visibility.Value.VISIBLE) {
            Application.getView().setTitleEditor(getTitleText());
        } else if (titleVisibility.getValue() == Visibility.Value.VISIBLE && visibility == Visibility.Value.INVISIBLE) {
            Application.getView().closeTitleEditor();
            this.titleVisibility.setValue(visibility);
        }
    }

    public Visibility getTitleVisibility() {
        return this.titleVisibility;
    }

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
                    if (action.getSize() > 1) {
                        camera.setOffset(event.getPosition().x - action.getFirstEvent().getPosition().x, event.getPosition().y - action.getFirstEvent().getPosition().y);
                    }

//                    camera.setOffset(action.getOffset().getAbsoluteX(), action.getOffset().getAbsoluteY());
//                    }
                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Previous Action targeted also this Extension
                    // TODO: Refactor
                    if (action.getPrevious() != null && action.getPrevious().getFirstEvent().getTargetImage().getEntity() == getEntity()) {

                        if (action.isTap()) {

                            // Title
                            setTitleText("Project");
                            setTitleVisibility(Visibility.Value.VISIBLE);
                        }

                    } else {

                        // NOT a repeat tap on this Image

                        if (action.isTap()) {

                            // Title
                            setTitleVisibility(Visibility.Value.INVISIBLE);

                            // Camera
                            camera.setFocus(getSpace());
                        }
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

    // TODO: Rename to sortLayers
    protected void sortImagesByLayer() {

        for (int i = 0; i < images.size() - 1; i++) {
            for (int j = i + 1; j < images.size(); j++) {
                // Check for out-of-order pairs, and swap them
                if (images.get(i).layerIndex > images.get(j).layerIndex) {
                    Image image = images.get(i);
                    images.set(i, images.get(j));
                    images.set(j, image);
                }
            }
        }

        /*
        // TODO: Sort using this after making Group implement List
        Collections.sort(Database.arrayList, new Comparator<MyObject>() {
            @Override
            public int compare(MyObject o1, MyObject o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        */
    }

    // TODO: Remove Image parameter. Create that and return it.
    private <T extends Image> void addImage(T image) {

        // Add Image
        image.setSpace(this);
        if (!images.contains(image)) {
            images.add(image);
            sortImagesByLayer();
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
//            image.setRotation(Probability.getRandomGenerator().nextInt(360));
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

    public void doUpdate() {

        position.update();

        // Update perspective
        getEntity().getActor(0).getCamera().update();

//        Log.v("Images", "doUpdate: " + images.size());
        for (int i = 0; i < images.size(); i++) {
            images.get(i).update();
        }

//        // Sort Images by increasing layer index
//        for (int layerIndex = -10; layerIndex < 10; layerIndex++) { // HACK...
//            for (int i = 0; i < images.size(); i++) {
//                if (images.get(i).layerIndex == layerIndex) {
//                }
//            }
//        }

        // Update figure layout
        // Geometry.computeCirclePacking(getImages().filterType(HostImage.class, ExtensionImage.class).getList(), 200, getImages().filterType(HostImage.class, ExtensionImage.class).getCentroidPosition());
    }

    @Override
    public void draw(Display display) {

        display.canvas.save();

        // Draw
        for (int i = 0; i < images.size(); i++) {
            images.get(i).draw(display);
        }

        // Draw any prototype Paths and Extensions
        drawPathPrototype(display);
        drawExtensionPrototype(display);

        display.canvas.restore();
    }

    private void drawExtensionPrototype(Display display) {
        if (extensionPrototypeVisibility.getValue() == Visibility.Value.VISIBLE) {

            Paint paint = display.getPaint();

            double pathRotationAngle = Geometry.calculateRotationAngle(pathPrototypeSourcePosition, extensionPrototypePosition);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#fff7f7f7"));

            display.drawRectangle(extensionPrototypePosition, pathRotationAngle + 180, 200, 200);
        }
    }

    // TODO: Make this into a shape and put this on a separate layerIndex!
    public void drawPathPrototype(Display display) {
        if (pathPrototypeVisibility.getValue() == Visibility.Value.VISIBLE) {

            Paint paint = display.getPaint();

            double triangleWidth = 20;
            double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
            double triangleSpacing = 35;

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
//            paint.setColor(this.getUniqueColor());

            double pathRotationAngle = Geometry.calculateRotationAngle(
                    pathPrototypeSourcePosition,
                    pathPrototypeDestinationCoordinate
            );

            Point pathStartCoordinate = Geometry.calculatePoint(
                    pathPrototypeSourcePosition,
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            Point pathStopCoordinate = Geometry.calculatePoint(
                    pathPrototypeDestinationCoordinate,
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            paint.setColor(Color.parseColor("#efefef"));
            display.drawTrianglePath(pathStartCoordinate, pathStopCoordinate, triangleWidth, triangleHeight);

            // Color
            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.parseColor("#efefef"));
            double shapeRadius = 40.0;
            display.drawCircle(pathPrototypeDestinationCoordinate, shapeRadius, 0.0f);
        }
    }

    public void setPathPrototypeVisibility(Visibility.Value visibility) {
        pathPrototypeVisibility.setValue(visibility);
    }

    public Visibility getPathPrototypeVisibility() {
        return pathPrototypeVisibility;
    }

    public void setPathPrototypeSourcePosition(Point position) {
        this.pathPrototypeSourcePosition.copy(position);
    }

    public void setPathPrototypeDestinationPosition(Point position) {
        this.pathPrototypeDestinationCoordinate.copy(position);
    }

    public void setExtensionPrototypePosition(Point position) {
        this.extensionPrototypePosition.copy(position);
    }

    public void setExtensionPrototypeVisibility(Visibility.Value visibility) {
        extensionPrototypeVisibility.setValue(visibility);
    }

    public Visibility getExtensionPrototypeVisibility() {
        return extensionPrototypeVisibility;
    }
}
