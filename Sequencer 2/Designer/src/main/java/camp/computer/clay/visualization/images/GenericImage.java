package camp.computer.clay.visualization.images;

import camp.computer.clay.model.simulation.Model;

public class GenericImage<T extends Model> {

    private T model;

    public T getModel() {
        return model;
    }

}
