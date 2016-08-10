package camp.computer.clay.visualization.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Frame;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.model.interactivity.ActionListener;
import camp.computer.clay.model.interactivity.Interaction;
import camp.computer.clay.model.interactivity.Perspective;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;

public class FrameImage extends Image {

    // Shapes
    private Rectangle boardShape = new Rectangle(250, 250);
    private List<Rectangle> lightShapes = new ArrayList<>();

    public FrameImage(Frame frame) {
        super(frame);
        setup();
    }

    private void setup() {
        setupShapes();
        setupInteractivity();
    }

    private void setupShapes() {

        // Create shapes for image
        boardShape = new Rectangle(250, 250);
        boardShape.setColor("#f7f7f7");
        boardShape.setOutlineThickness(1);
        addShape(boardShape);

        // Headers
        Rectangle headerShape1 = new Rectangle(50, 14);
        headerShape1.setPosition(0, 132);
        // headerShape1.setRotation(0);
        headerShape1.setColor("#3b3b3b");
        headerShape1.setOutlineThickness(0);
        addShape(headerShape1);

        Rectangle headerShape2 = new Rectangle(50, 14);
        headerShape2.setPosition(132, 0);
        headerShape2.setRotation(90);
        headerShape2.setColor("#3b3b3b");
        headerShape2.setOutlineThickness(0);
        addShape(headerShape2);

        Rectangle headerShape3 = new Rectangle(50, 14);
        headerShape3.setPosition(0, -132);
        // headerShape3.setRotation(180);
        headerShape3.setColor("#3b3b3b");
        headerShape3.setOutlineThickness(0);
        addShape(headerShape3);

        Rectangle headerShape4 = new Rectangle(50, 14);
        headerShape4.setPosition(-132, 0);
        headerShape4.setRotation(90);
        headerShape4.setColor("#3b3b3b");
        headerShape4.setOutlineThickness(0);
        addShape(headerShape4);

        // Lights
        Rectangle light1 = new Rectangle(12, 20);
        light1.setLabel("LED 1");
        light1.setPosition(-20, 105);
        // light1.setRotation(90);
        lightShapes.add(light1);
        addShape(light1);

        Rectangle light2 = new Rectangle(12, 20);
        light2.setLabel("LED 2");
        light2.setPosition(0, 105);
//        light2.setRotation(90);
        lightShapes.add(light2);
        addShape(light2);

        Rectangle light3 = new Rectangle(12, 20);
        light3.setLabel("LED 3");
        light3.setPosition(20, 105);
//        light3.setRotation(90);
        lightShapes.add(light3);
        addShape(light3);

        Rectangle light4 = new Rectangle(12, 20);
        light4.setLabel("LED 4");
        light4.setPosition(105, 20);
        light4.setRotation(90);
        lightShapes.add(light4);
        addShape(light4);

        Rectangle light5 = new Rectangle(12, 20);
        light5.setLabel("LED 5");
        light5.setPosition(105, 0);
        light5.setRotation(90);
        lightShapes.add(light5);
        addShape(light5);

        Rectangle light6 = new Rectangle(12, 20);
        light6.setLabel("LED 6");
        light6.setPosition(105, -20);
        light6.setRotation(90);
        lightShapes.add(light6);
        addShape(light6);

        Rectangle light7 = new Rectangle(12, 20);
        light7.setLabel("LED 7");
        light7.setPosition(20, -105);
//        light7.setRotation(90);
        lightShapes.add(light7);
        addShape(light7);

        Rectangle light8 = new Rectangle(12, 20);
        light8.setLabel("LED 8");
        light8.setPosition(0, -105);
//        light8.setRotation(90);
        lightShapes.add(light8);
        addShape(light8);

        Rectangle light9 = new Rectangle(12, 20);
        light9.setLabel("LED 9");
        light9.setPosition(-20, -105);
//        light9.setRotation(90);
        lightShapes.add(light9);
        addShape(light9);

        Rectangle light10 = new Rectangle(12, 20);
        light10.setLabel("LED 10");
        light10.setPosition(-105, -20);
        light10.setRotation(90);
        lightShapes.add(light10);
        addShape(light10);

        Rectangle light11 = new Rectangle(12, 20);
        light11.setLabel("LED 11");
        light11.setPosition(-105, 0);
        light11.setRotation(90);
        lightShapes.add(light11);
        addShape(light11);

        Rectangle light12 = new Rectangle(12, 20);
        light12.setLabel("LED 12");
        light12.setPosition(-105, 20);
        light12.setRotation(90);
        lightShapes.add(light12);
        addShape(light12);

    }

    private void setupInteractivity() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                if (action.getType() == Action.Type.NONE) {

                } else if (action.getType() == Action.Type.TOUCH) {

                } else if (action.getType() == Action.Type.HOLD) {

                } else if (action.getType() == Action.Type.MOVE) {

                } else if (action.getType() == Action.Type.RELEASE) {

                    Interaction interaction = action.getInteraction();

                    Image targetImage = visualization.getImageByPosition(action.getPosition());
                    action.setTarget(targetImage);

                    Perspective perspective = action.getBody().getPerspective();

                    if (interaction.getDuration() < Action.MAX_TAP_DURATION) {

                        // Focus on touched form
                        showPortImages();
                        showPathImages();

                        setTransparency(1.0);

                        // TODO: Speak "choose a channel to get data."

                        // Show ports and paths of touched form
                        for (PortImage portImage : getPortImages()) {
                            List<Path> paths = portImage.getPort().getGraph();
                            Log.v("TouchFrame", "\tpaths.size = " + paths.size());
                            for (Path path : paths) {
                                Log.v("TouchFrame", "\t\tsource = " + path.getSource());
                                Log.v("TouchFrame", "\t\ttarget = " + path.getTarget());
                                // Show ports
                                getVisualization().getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                                getVisualization().getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);
                                // Show path
                                getVisualization().getImage(path).setVisibility(Visibility.VISIBLE);
                            }
                        }

                    } else {

                        // TODO: Release longer than tap!

                    }

                }
            }
        });
    }

    public Frame getFrame() {
        return (Frame) getModel();
    }

    public List<PortImage> getPortImages() {
        List<PortImage> portImages = new ArrayList<>();

        for (Port port : getFrame().getPorts()) {
            PortImage portImage = (PortImage) getVisualization().getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getVisualization().getModel(portImage);
        if (getFrame().getPorts().contains(port)) {
            return this.getFrame().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {

        for (int i = 0; i < lightShapes.size(); i++) {
            Port port = getFrame().getPort(i);
            if (port.getType() != Port.Type.NONE) {
                int intColor = getPortImages().get(i).getUniqueColor();
                String hexColor = camp.computer.clay.visualization.util.Color.getHexColorString(intColor);
                lightShapes.get(i).setColor(hexColor);
            } else {
                lightShapes.get(i).setColor(camp.computer.clay.visualization.util.Color.getHexColorString(PortImage.FLOW_PATH_COLOR_NONE));
            }
        }

//        String transparencyString = String.format("%02x", (int) transparency * 255);
//
//        // Frame color
//        color = Color.parseColor("#" + transparencyString + colorString);
//        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);
//
//        // Header color
//        portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
//        portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);

//        updatePortGroupImages();
    }

    public void draw(Surface surface) {
        if (isVisible()) {

            // Color
            for (int i = 0; i < shapes.size(); i++) {
                // TODO: Change "drawRectangle" to "drawShape()"
                Surface.drawRectangle((Rectangle) shapes.get(i), surface);
            }

            // Annotations
            if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Surface.drawCircle(getPosition(), boardShape.getWidth(), 0, surface);
                Surface.drawCircle(getPosition(), boardShape.getWidth() / 2.0f, 0, surface);
            }
        }
    }

//    private void drawLightImages(Surface surface) {
//
//        Canvas canvas = surface.getCanvas();
//        Paint paint = surface.getPaint();
//
//        for (int i = 0; i < PORT_COUNT; i++) {
//
//            // Color
//            paint.setStyle(Paint.Style.FILL);
//            paint.setStrokeWidth(3);
//            Port port = (Port) getFrame().getPort(i);
//            if (port.getType() != Port.Type.NONE) {
//                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(this.getPortImage(i).getUniqueColor(), (float) transparency));
//            } else {
//                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(PortImage.FLOW_PATH_COLOR_NONE, (float) transparency));
//            }
//            Surface.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, surface);
//
//            // Outline
//            if (this.showLightOutline) {
//                paint.setStyle(Paint.Style.STROKE);
//                paint.setStrokeWidth((float) lightOutlineThickness);
//                paint.setColor(this.lightOutlineColor);
//                Surface.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, surface);
//            }
//        }
//    }

    public void showPortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(Visibility.VISIBLE);
            portImage.showDocks();
        }
    }

    public void hidePortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(Visibility.INVISIBLE);
        }
    }

    public void showPathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(Visibility.VISIBLE);
        }
    }

    public void hidePathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(Visibility.INVISIBLE);
            portImage.showDocks();
        }
    }

    public boolean containsPoint(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean containsPoint(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }
}

