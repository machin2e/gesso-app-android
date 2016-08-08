package camp.computer.clay.visualization.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;
import camp.computer.clay.visualization.util.geometry.Shape;

public class PatchImage extends Image {

    // <STYLE>
    // TODO: Make these private once the map is working well and the sprite is working well.

    // Shapes
    private Rectangle shape = new Rectangle(200, 200);

    // Color, Transparency
    private String colorString = "f7f7f7"; // "404040"; // "414141";
    private int color = Color.parseColor("#ff" + colorString); // Color.parseColor("#212121");
    private boolean outlineVisibility = true;
    private String outlineColorString = "414141";
    private int outlineColor = Color.parseColor("#ff" + outlineColorString); // Color.parseColor("#737272");
    private double outlineThickness = 3.0;

    private String portGroupColorString = "3b3b3b";
    private String portGroupOutlineColorString = "000000";
    // </STYLE>

    public PatchImage(Patch patch) {
        super(patch);
    }

    public Patch getPeripheral() {
        return (Patch) getModel();
    }

    public List<PortImage> getPortImages() {
        List<PortImage> portImages = new ArrayList<>();
        Patch patch = getPeripheral();

        for (Port port : patch.getPorts()) {
            PortImage portImage = (PortImage) getVisualization().getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    public PortImage getPortImage(int index) {
        Patch patch = getPeripheral();
        PortImage portImage = (PortImage) getVisualization().getImage(patch.getPort(index));
        return portImage;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getVisualization().getModel(portImage);
        if (getPeripheral().getPorts().contains(port)) {
            return this.getPeripheral().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {

        // Transparency
        String transparencyString = String.format("%02x", (int) currentTransparency * 255);
        color = Color.parseColor("#" + transparencyString + colorString);
        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);

    }

    public void draw(Surface surface) {
        if (isVisible()) {
//            drawPortGroupImages(visualizationSurface);
            drawBoardImage(surface);
//            drawLightImages(visualizationSurface);

            if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Surface.drawCircle(getPosition(), shape.getWidth(), 0, surface);
                Surface.drawCircle(getPosition(), shape.getWidth() / 2.0f, 0, surface);
            }
        }
    }

    public Rectangle getShape() {
        return this.shape;
    }

    private void drawBoardImage(Surface surface) {

        Paint paint = surface.getPaint();

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        Surface.drawRectangle(getPosition(), getRotation(), shape.getWidth(), shape.getHeight(), surface);

        // Outline
        if (this.outlineVisibility) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(this.outlineColor);
            paint.setStrokeWidth((float) outlineThickness);
            Surface.drawRectangle(getPosition(), getRotation(), shape.getWidth(), shape.getHeight(), surface);
        }
    }

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
            portImage.setPathVisibility(Visibility.INVISIBLE);
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
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.shape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean containsPoint(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.shape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }

    @Override
    public void onImpression(Action action) {

        if (action.getType() == Action.Type.NONE) {
            // Log.v("Action", "Action.NONE to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.TOUCH) {
            // Log.v("Action", "Action.TOUCH to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.TAP) {

            Log.v("Action", "Tapped peripheral. Port image count: " + getPortImages().size());

            // Focus on touched form
            showPortImages();
            showPathImages();
            setTransparency(1.0);

            // TODO: Speak "choose a channel to get data."

            // Show ports and paths of touched form
            for (PortImage portImage : getPortImages()) {
                List<Path> paths = portImage.getPort().getGraph();
                for (Path path : paths) {
                    // Show ports
                    getVisualization().getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                    getVisualization().getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);
                    // Show path
                    getVisualization().getImage(path).setVisibility(Visibility.VISIBLE);
                }
            }

        } else if (action.getType() == Action.Type.HOLD) {
            // Log.v("Action", "Action.HOLD to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.MOVE) {
            // Log.v("Action", "Action.MOVE to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.DRAG) {
            // Log.v("Action", "Action.DRAG to " + CLASS_NAME);
        } else if (action.getType() == Action.Type.RELEASE) {
            // Log.v("Action", "Action.RELEASE to " + CLASS_NAME);
        }
    }
}

