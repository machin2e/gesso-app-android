package camp.computer.clay.model.arch;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.interactivity.Body;

public class Simulation extends Model {

    private List<Body> bodies = new ArrayList<>();

    private List<Frame> frames = new ArrayList<>();

    private List<Peripheral> peripherals = new ArrayList<>();

    public void addFrame(Frame path) {
        this.frames.add(path);
    }

    public Frame getFrame(int index) {
        return this.frames.get(index);
    }

    public List<Frame> getFrames() {
        return this.frames;
    }

    public List<Port> getPorts() {
        List<Port> ports = new ArrayList<>();
        for (Frame frame : this.frames) {
            ports.addAll(frame.getPorts());
        }
        return ports;
    }

    public List<Path> getPaths() {
        List<Path> paths = new ArrayList<>();
        for (Frame frame : this.frames) {
            for (Port port : frame.getPorts()) {
                paths.addAll(port.getPaths());
            }
        }
        return paths;
    }

    public void addPeripheral(Peripheral peripheral) {
        this.peripherals.add(peripheral);
    }

    public Peripheral getPeripheral(int index) {
        return this.peripherals.get(index);
    }

    public List<Peripheral> getPeripherals() {
        return this.peripherals;
    }

    public void addBody(Body body) {
        this.bodies.add(body);
    }

    public Body getBody(int index) {
        return this.bodies.get(index);
    }

    public List<Body> getBodies() {
        return this.bodies;
    }
}
