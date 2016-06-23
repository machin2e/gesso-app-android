package camp.computer.clay.sprite.util;

import java.util.Random;

public abstract class Number {
    private static Random random = new Random();

    public static Random getRandom() {
        return Number.random;
    }

    public static int getRandomInteger(int minimum, int maxinum) {
        return Number.random.nextInt(maxinum - minimum) - minimum;
    }
}
