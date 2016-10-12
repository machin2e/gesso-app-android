package camp.computer.clay.util.image;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Actor;
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
import camp.computer.clay.space.image.PortableImage;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.image.util.ImageGroup;
import camp.computer.clay.util.image.util.ShapeGroup;

public class Space extends Image<Model> {

    public static double PIXEL_PER_MILLIMETER = 6.0;

    protected ImageGroup images = new ImageGroup();

    protected Visibility extensionPrototypeVisibility = Visibility.INVISIBLE;
    protected Point extensionPrototypePosition = new Point();

    protected Visibility pathPrototypeVisibility = Visibility.INVISIBLE;
    protected Point pathPrototypeSourcePosition = new Point(0, 0);
    protected Point pathPrototypeDestinationCoordinate = new Point(0, 0);

    private List<Actor> actors = new LinkedList<>();

    public Space(Model model) {
        super(model);
        setup();
    }

    private void setup() {
        setupActions();
    }

    // TODO: Allow user to setAbsolute and change a goal. Track it in relation to the actions taken and things built.
    protected Visibility titleVisibility = Visibility.INVISIBLE;
    protected String titleText = "Project";

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleVisibility(Visibility visibility) {
        if (titleVisibility == Visibility.INVISIBLE && visibility == Visibility.VISIBLE) {
//            Application.getView().openTitleEditor(getTitleText());
            this.titleVisibility = visibility;
        } else if (titleVisibility == Visibility.VISIBLE && visibility == Visibility.VISIBLE) {
//            Application.getView().setTitleEditor(getTitleText());
        } else if (titleVisibility == Visibility.VISIBLE && visibility == Visibility.INVISIBLE) {
//            Application.getView().closeTitleEditor();
            this.titleVisibility = visibility;
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
//                        //lastEvent.getTargetImage().queueAction(action);
//                    } else if (action.isDragging()) {
                    // Camera
                    if (action.getSize() > 1) {
                        //camera.setOffset(event.getPosition().x - action.getFirstEvent().getPosition().x, event.getPosition().y - action.getFirstEvent().getPosition().y);
                        camera.setPosition(-(event.getPosition().x - action.getFirstEvent().getPosition().x), -(event.getPosition().y - action.getFirstEvent().getPosition().y));
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
                            setTitleVisibility(Visibility.VISIBLE);
                        }

                    } else {

                        // NOT a repeat tap on this Image

                        if (action.isTap()) {

                            // Title
                            setTitleVisibility(Visibility.INVISIBLE);

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

    public void addActor(Actor actor) {
        if (!this.actors.contains(actor)) {
            this.actors.add(actor);
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
        if (image instanceof HostImage) {
            adjustLayout();
        }

        // Update Camera
        getEntity().getActor(0).getCamera().setFocus(this);
    }

    @Override
    public Space getSpace() {
        return this;
    }

    // TODO: Use base class's addImage() so Shapes are added to super.shapes. Then add an index instead of layers?

    /**
     * Automatically determines and assigns a valid position for all {@code Host} {@code Image}s.
     */
    private void adjustLayout() {

        ImageGroup hostImages = getImages().filterType(Host.class);

        // Set position
        if (hostImages.size() == 1) {
            hostImages.get(0).setPosition(0, 0);
        } else if (hostImages.size() == 2) {
            hostImages.get(0).setPosition(-300, 0);
            hostImages.get(1).setPosition(300, 0);
        } else if (hostImages.size() == 5) {
            hostImages.get(0).setPosition(-300, -600);
            hostImages.get(0).setRotation(0);
            hostImages.get(1).setPosition(300, -600);
            hostImages.get(1).setRotation(20);
            hostImages.get(2).setPosition(-300, 0);
            hostImages.get(2).setRotation(40);
            hostImages.get(3).setPosition(300, 0);
            hostImages.get(3).setRotation(60);
            hostImages.get(4).setPosition(-300, 600);
            hostImages.get(4).setRotation(80);
        }

        // Set rotation
        // image.setRotation(Probability.getRandomGenerator().nextInt(360));
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
        ImageGroup image = images.filterVisibility(Visibility.VISIBLE).filterContains(point);
        if (image.size() > 0) {
            return image.get(0);
        } else {
            return this;
        }
    }

    public ShapeGroup getShapes() {
        return images.getShapes();
    }

    // TODO: Refactor to be cleaner and leverage other classes...
    public <T extends Entity> ShapeGroup getShapes(Class<? extends Entity>... entityTypes) {
        ShapeGroup shapeGroup = new ShapeGroup();
        ImageGroup imageList = getImages();

        for (int i = 0; i < imageList.size(); i++) {
            shapeGroup.addAll(imageList.get(i).getShapes(entityTypes));
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

    @Override
    public void update() {

        // Update Actors
        for (int i = 0; i < actors.size(); i++) {
            this.actors.get(i).update();
        }

        // Update Images
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            // Update bounding box of Image
            // TODO:

            // Update the Image
            image.update();
        }

        // Update Camera(s)
        getEntity().getActor(0).getCamera().update();
    }

    @Override
    public void draw(Display display) {

        display.canvas.save();

        // Draw Portables
        for (int i = 0; i < images.size(); i++) {
            if (!(images.get(i) instanceof ExtensionImage)) {
                images.get(i).draw(display);
            }
        }

        // Draw Extensions
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) instanceof ExtensionImage) {
                images.get(i).draw(display);
            }
        }

        // Draw any prototype Paths and Extensions
        drawPathPrototype(display);
        drawExtensionPrototype(display);

        display.canvas.restore();

//        getEntity().getActor(0).getCamera().setFocus(this);
    }

    private void drawExtensionPrototype(Display display) {
        if (extensionPrototypeVisibility == Visibility.VISIBLE) {

            Paint paint = display.paint;

            double pathRotationAngle = Geometry.getAngle(pathPrototypeSourcePosition, extensionPrototypePosition);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#fff7f7f7"));

            display.drawRectangle(extensionPrototypePosition, pathRotationAngle + 180, 200, 200);
        }
    }

    // TODO: Make this into a shape and put this on a separate layerIndex!
    public void drawPathPrototype(Display display) {
        if (pathPrototypeVisibility == Visibility.VISIBLE) {

            Paint paint = display.paint;

            double triangleWidth = 20;
            double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
            double triangleSpacing = 35;

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
//            paint.setColor(this.getUniqueColor());

            double pathRotationAngle = Geometry.getAngle(
                    pathPrototypeSourcePosition,
                    pathPrototypeDestinationCoordinate
            );

            Point pathStartCoordinate = Geometry.getRotateTranslatePoint(
                    pathPrototypeSourcePosition,
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            Point pathStopCoordinate = Geometry.getRotateTranslatePoint(
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

    public void setPathPrototypeVisibility(Visibility visibility) {
        pathPrototypeVisibility = visibility;
    }

    public Visibility getPathPrototypeVisibility() {
        return pathPrototypeVisibility;
    }

    public void setPathPrototypeSourcePosition(Point position) {
        this.pathPrototypeSourcePosition.set(position);
    }

    public void setPathPrototypeDestinationPosition(Point position) {
        this.pathPrototypeDestinationCoordinate.set(position);
    }

    public void setExtensionPrototypePosition(Point position) {
        this.extensionPrototypePosition.set(position);
    }

    public void setExtensionPrototypeVisibility(Visibility visibility) {
        extensionPrototypeVisibility = visibility;
    }

    public Visibility getExtensionPrototypeVisibility() {
        return extensionPrototypeVisibility;
    }

    public void setPortableSeparation(double distance) {
        // <HACK>
        // TODO: Replace ASAP. This is shit.
        ImageGroup extensionImages = getImages(Extension.class);
        for (int i = 0; i < extensionImages.size(); i++) {
            ExtensionImage extensionImage = (ExtensionImage) extensionImages.get(i);

            if (extensionImage.getExtension().getHost().size() > 0) {
                Host host = extensionImage.getExtension().getHost().get(0);
                HostImage hostImage = (HostImage) getSpace().getImage(host);
                hostImage.setExtensionDistance(distance);
            }
        }
        // </HACK>
    }

    public void hidePortables() {
        ImageGroup portableImages = getImages(Host.class, Extension.class);
        for (int i = 0; i < portableImages.size(); i++) {
            PortableImage portableImage = (PortableImage) portableImages.get(i);
            portableImage.getPortShapes().setVisibility(Visibility.INVISIBLE);
            portableImage.setPathVisibility(Visibility.INVISIBLE);
            portableImage.setDockVisibility(Visibility.VISIBLE);
            portableImage.setTransparency(1.0);
        }
    }
}
