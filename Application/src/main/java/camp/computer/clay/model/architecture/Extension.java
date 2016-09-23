package camp.computer.clay.model.architecture;

import camp.computer.clay.model.profile.ExtensionProfile;

/**
 * {@code Extension} represents a <em>host extension</em>.
 */
public class Extension extends Portable {

    // Servo: GND, 3.3V, PWM
    // DC Motor: GND, 5V
    // IR Rangefinder: GND, 3.3V, Signal (analog)
    // Potentiometer: GND, 3.3V, Signal (analog)

    public Extension() {
        super();
    }

    public Extension(ExtensionProfile extensionProfile) {

        // Create Ports to match the Profile
        for (int i = 0; i < extensionProfile.getPortCount(); i++) {
            Port port = new Port();
            addPort(port);
        }

        // TODO: Update the rest of the Extension to reflect the Profile!
    }

}
