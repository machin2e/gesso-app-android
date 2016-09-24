package camp.computer.clay.model.architecture;

import camp.computer.clay.model.profile.PortableProfile;

/**
 * {@code Extension} represents a <em>host extension</em>.
 */
public class Extension extends Portable
{
    // Servo: GND, 3.3V, PWM
    // DC Motor: GND, 5V
    // IR Rangefinder: GND, 3.3V, Signal (analog)
    // Potentiometer: GND, 3.3V, Signal (analog)

    public Extension()
    {
        super();
    }

    public Extension(PortableProfile portableProfile)
    {
        super(portableProfile);
    }
}
