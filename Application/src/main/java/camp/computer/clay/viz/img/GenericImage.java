package camp.computer.clay.viz.img;

import camp.computer.clay.model.sim.Model;

public class GenericImage<T extends Model> {

    private T model;

    public T getModel() {
        return model;
    }

}
