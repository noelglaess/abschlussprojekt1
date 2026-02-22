package edu.kit.assignmentone.model;

import edu.kit.assignmentone.StringConstants;

/**
 * Utility class for mathematical operations needed in the game (like GCD and Prime checks).
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class MathUtils {

    private MathUtils() {
        throw new UnsupportedOperationException(StringConstants.UTILITY_CLASS_ERROR);
    }

    /**
     * Calculates the greatest common divisor (ggT) of two integers.
     *
     * @param value1 The first integer
     * @param value2 The second integer
     * @return The GCD
     */
    public static int gcd(int value1, int value2) {
        int tempValue1 = Math.abs(value1);
        int tempValue2 = Math.abs(value2);

        while (tempValue2 != 0) {
            int remainder = tempValue1 % tempValue2;
            tempValue1 = tempValue2;
            tempValue2 = remainder;
        }
        return tempValue1;
    }

    /**
     * Checks if a given number is prime.
     *
     * @param numberToCheck The number to check
     * @return true if prime, false otherwise
     */
    public static boolean isPrime(int numberToCheck) {
        if (numberToCheck <= 1) {
            return false;
        }
        if (numberToCheck <= 3) {
            return true;
        }
        if (numberToCheck % 2 == 0 || numberToCheck % 3 == 0) {
            return false;
        }
        for (int index = 5; index * index <= numberToCheck; index += 6) {
            if (numberToCheck % index == 0 || numberToCheck % (index + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}