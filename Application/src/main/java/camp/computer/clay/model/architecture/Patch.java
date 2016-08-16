package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.List;

public class Patch extends Construct {

    // Servo: GND, 3.3V, PWM
    // DC Motor: GND, 5V
    // IR Rangefinder: GND, 3.3V, Signal (analog)
    // Potentiometer: GND, 3.3V, Signal (analog)

    private List<Port> ports = new ArrayList<>();

    public void addPort(Port port) {
        if (!this.ports.contains(port)) {
            port.setParent(this);
            this.ports.add(port);
        }
    }

    public Port getPort(int index) {
        return this.ports.get(index);
    }

    public List<Port> getPorts() {
        return this.ports;
    }

    public List<Path> getPaths() {
        List<Path> paths = new ArrayList<>();
        for (Port port : getPorts()) {
            paths.addAll(port.getPaths());
        }
        return paths;
    }

}
