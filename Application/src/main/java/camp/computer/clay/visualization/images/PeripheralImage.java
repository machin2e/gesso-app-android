package camp.computer.clay.visualization.images;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.simulation.Model;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Point;

public class PeripheralImage extends Image {

    public PeripheralImage(Model model) {
        super(model);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(VisualizationSurface visualizationSurface) {

    }

    @Override
    public boolean isTouching(Point point) {
        return false;
    }

    @Override
    public boolean isTouching(Point point, double padding) {
        return false;
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

    }
}
