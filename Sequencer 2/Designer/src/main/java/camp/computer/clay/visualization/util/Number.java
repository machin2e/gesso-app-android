package camp.computer.clay.visualization.util;

import java.util.Random;

public abstract class Number {
    private static Random random = new Random();

    public static Random getRandom() {
        return Number.random;
    }

    public static int generateRandomInteger(int minimum, int maximum) {
        return (Number.random.nextInt(maximum - minimum) - minimum);
    }
}
