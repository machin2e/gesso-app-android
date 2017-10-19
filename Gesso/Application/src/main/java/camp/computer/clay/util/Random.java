package camp.computer.clay.util;

import java.security.SecureRandom;

public abstract class Random {

    // TODO: Interface to host's cryptographically strong random index generator adhering to
    // TODO: (cont'd) [RFC 1750](http://www.ietf.org/rfc/rfc1750.txt) and tests in
    // TODO: (cont'd) [FIPS 140-2](http://csrc.nist.gov/groups/STM/cmvp/standards.html#02).
    private static SecureRandom randomGenerator = new SecureRandom();

    public static SecureRandom getRandomGenerator() {
        return Random.randomGenerator;
    }

    public static int generateRandomInteger(int minimum, int maximum) {
        return (minimum + Random.randomGenerator.nextInt(maximum - minimum));
    }

    public static int getRandomInteger(int... integers) {
        return integers[randomGenerator.nextInt(integers.length)];
    }
}
