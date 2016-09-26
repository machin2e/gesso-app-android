package camp.computer.clay.space.architecture;

import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Entity;

public class Layer { // TODO: Replace Layer with Group (probably ImageGroup)

    private static int LAYER_ID_COUNT = 0;

    private Space space;

    // TODO: Replace this with UUID
    // TODO: Add tags (can search by tags)
    private int id = -1;

    private String tag = "default";

    private ImageGroup images = new ImageGroup();

    public Layer(Space space) {
        this.space = space;

        // Set the layer ID
        this.id = LAYER_ID_COUNT;
        LAYER_ID_COUNT++;
    }

    public int getIndex() {
        return this.id;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Space getSpace() {
        return this.space;
    }

    public void addImage(Image image) {
        this.images.add(image);
        image.setSpace(space);
    }

    public Image getImage(Entity entity) {
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            if (image.getEntity() == entity) {
                return image;
            }
        }
        return null;
    }

    public ImageGroup getImages() {
        return this.images;
    }

    public int size() {
        return this.images.size();
    }

    public Entity getEntity(Image image) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) == image) {
                return images.get(i).getEntity();
            }
        }
        return null;
    }

    // TODO: (?) Call into Image.getEntity(Shape)
    public Entity getEntity(Shape shape) {
        for (int i = 0; i < images.size(); i++) {
            List<Shape> shapeList = images.get(i).getShapes().getList();
            for (int j = 0; j < shapeList.size(); j++) {
                if (shapeList.get(i) == shape) {
                    return images.get(i).getEntity();
                }
            }
        }
        return null;
    }

    public void draw(Display display) {
        for (int i = 0; i < getImages().size(); i++) {
            getImages().get(i).draw(display);
        }
    }
}
