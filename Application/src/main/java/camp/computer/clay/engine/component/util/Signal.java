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

        public static Type getNext(Type currentType) {
            Type[] values = Type.values();
            int currentIndex = java.util.Arrays.asList(values).indexOf(currentType);
            return values[(currentIndex + 1) % values.length];
        }
    }

    public enum Direction {

        NONE(0),   // sourcePortUuid  |  destination
        OUTPUT(1), // sourcePortUuid --> destination
        INPUT(2),  // sourcePortUuid <-- destination
        BOTH(3);   // sourcePortUuid <-> destination

        // TODO: Change the index to a UUID?
        int index;

        Direction(int index) {
            this.index = index;
        }
    }
}
