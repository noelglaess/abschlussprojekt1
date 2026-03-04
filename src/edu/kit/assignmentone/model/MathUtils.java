package edu.kit.assignmentone.model;

/**
 * Utility class for mathematical operations needed in the game.
 *
 * @author uqhkm
 * @version 1.0
 */
public final class MathUtils {
    private MathUtils() { }

    /**
     * Calculates the greatest common divisor.
     * @param firstValue The first integer
     * @param secondValue The second integer
     * @return The greatest common divisor
     */
    public static int calculateGreatestCommonDivisor(int firstValue, int secondValue) {
        int temporaryValue1 = Math.abs(firstValue);
        int temporaryValue2 = Math.abs(secondValue);
        while (temporaryValue2 != 0) {
            int remainder = temporaryValue1 % temporaryValue2;
            temporaryValue1 = temporaryValue2;
            temporaryValue2 = remainder;
        }
        return temporaryValue1;
    }

    private static boolean isPrime(int numberToCheck) {
        if (numberToCheck <= 1) return false;
        if (numberToCheck <= 3) return true;
        if (numberToCheck % 2 == 0 || numberToCheck % 3 == 0) return false;
        for (int index = 5; index * index <= numberToCheck; index += 6) {
            if (numberToCheck % index == 0 || numberToCheck % (index + 2) == 0) return false;
        }
        return true;
    }

    /**
     * Checks if either of two values is prime.
     * @param firstValue First value
     * @param secondValue Second value
     * @return true if one or both are prime
     */
    public static boolean hasPrime(int firstValue, int secondValue) {
        return isPrime(firstValue) || isPrime(secondValue);
    }
}