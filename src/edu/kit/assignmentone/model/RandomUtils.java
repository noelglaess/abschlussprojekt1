package edu.kit.assignmentone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for weighted random selections as specified in the assignment.
 *
 * @author Programmieren-Team
 */
public final class RandomUtils {

    private static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";

    private RandomUtils() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
    }

    /**
     * Performs a weighted random selection based on the given weights.
     * Negative weights are treated as 0.
     *
     * @param weights The list of weights
     * @param rnd The global random instance
     * @return The index of the selected option
     */
    public static int weightedRandom(List<Integer> weights, Random rnd) {
        int totalSum = 0;
        int[] sums = new int[weights.size()];

        for (int i = 0; i < weights.size(); i++) {
            int w = Math.max(0, weights.get(i));
            totalSum += w;
            sums[i] = totalSum;
        }

        if (totalSum == 0) {
            return 0; // Fallback
        }

        int r = rnd.nextInt(totalSum) + 1; // Zieht zwischen 1 und totalSum (inklusive)

        for (int i = 0; i < sums.length; i++) {
            if (r <= sums[i]) {
                return i;
            }
        }
        return weights.size() - 1;
    }

    /**
     * Performs a reverse weighted random selection.
     *
     * @param weights The original weights
     * @param rnd The global random instance
     * @return The index of the selected option
     */
    public static int reverseWeightedRandom(List<Integer> weights, Random rnd) {
        int max = 0;
        for (int w : weights) {
            if (w > max) {
                max = w;
            }
        }

        List<Integer> reverseWeights = new ArrayList<>();
        for (int w : weights) {
            reverseWeights.add(Math.max(0, max - w));
        }

        return weightedRandom(reverseWeights, rnd);
    }
}