package edu.kit.assignmentone.model;

/**
 * Utility class for mathematical operations needed in the game.
 */
public final class MathUtils {

    private MathUtils() {
        throw new UnsupportedOperationException("Utility classes cannot be instantiated");
    }


    public static int gcd(int a, int b) {
        while (true) {
            if (b == 0) {
                return Math.abs(a);
            }
            int a1 = a;
            a = b;
            b = a1 % b;
        }
    }

    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}