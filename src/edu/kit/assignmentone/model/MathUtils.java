package edu.kit.assignmentone.model;

/**
 * Utility class for mathematical operations needed in the game (like GCD and Prime checks).
 *
 * @author Programmieren-Team
 */
public final class MathUtils {

    private static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";

    private MathUtils() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
    }

    /**
     * Calculates the greatest common divisor (ggT) of two integers.
     *
     * @param a The first integer
     * @param b The second integer
     * @return The GCD
     */
    public static int gcd(int a, int b) {
        if (b == 0) {
            return Math.abs(a);
        }
        return gcd(b, a % b);
    }

    /**
     * Checks if a given number is prime.
     *
     * @param n The number to check
     * @return true if prime, false otherwise
     */
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