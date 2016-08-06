package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.interactivity.Body;

public class Simulation extends Model {

    private List<Body> bodies = new ArrayList<>();

    private List<Frame> frames = new ArrayList<>();

    private List<Device> devices = new ArrayList<>();

    private System system = new System();

    public void setSystem(System system) {
        this.system = system;
    }

    public System getSystem() {
        return this.system;
    }

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

    public void addDevice(Device device) {
        this.devices.add(device);
    }

    public Device getDevice(int index) {
        return this.devices.get(index);
    }

    public List<Device> getDevices() {
        return this.devices;
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
