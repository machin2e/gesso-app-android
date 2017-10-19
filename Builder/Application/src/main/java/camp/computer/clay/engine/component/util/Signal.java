package camp.computer.clay.engine.component.util;

public abstract class Signal {

    // TODO: none, 5v, 3.3v, (data) I2C, SPI, (monitor) A2D, voltage, current
    public enum Type {
        NONE,
        SWITCH,
        PULSE,
        WAVE,
        POWER_REFERENCE,
        POWER_CMOS,
        POWER_TTL; // TODO: Should contain parameters for voltage (5V, 3.3V), current (constant?).

        public static Type next(Type currentType) {
            Type[] values = Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[(currentIndex + 1) % values.length];
        }

        public static Type previous(Type currentType) {
            Type[] values = Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[((currentIndex - 1) >= 0 ? (currentIndex - 1) : values.length - 1)];
        }
    }

    public enum Direction {
        NONE,   // sourcePortUuid  |  destination
        OUTPUT, // sourcePortUuid --> destination
        INPUT,  // sourcePortUuid <-- destination
        BOTH    // sourcePortUuid <-> destination
    }

    // TODO: VIEW, ELECTRONIC, MESH, INTERNET, BLUETOOTH
    // TODO: TCP, UDP, HTTP, HTTPS
    public enum Mode {
        NONE,
        ELECTRONIC,
        BLUETOOTH,
        THREAD,
        INTERNET
    }
}
