package edu.kit.assignmentone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Utility class for weighted random selections as specified in the assignment.
 *
 * @author Programmieren-Team
 */
public final class RandomUtils {

    private RandomUtils() {
        throw new UnsupportedOperationException("Utility classes cannot be instantiated");
    }

    public static int weightedRandom(List<Integer> weights, RandomGenerator rnd) {
        int totalSum = 0;
        int[] sums = new int[weights.size()];

        for (int i = 0; i < weights.size(); i++) {
            int w = Math.max(0, weights.get(i));
            totalSum += w;
            sums[i] = totalSum;
        }

        if (totalSum == 0) return 0;
        int r = rnd.nextInt(totalSum) + 1;

        for (int i = 0; i < sums.length; i++) {
            if (r <= sums[i]) {
                return i;
            }
        }
        return weights.size() - 1;
    }

    public static int reverseWeightedRandom(List<Integer> weights, RandomGenerator rnd) {
        int max = 0;
        for (int w : weights) {
            if (w > max) max = w;
        }
        List<Integer> reverseWeights = new ArrayList<>();
        for (int w : weights) {
            reverseWeights.add(Math.max(0, max - w));
        }
        return weightedRandom(reverseWeights, rnd);
    }
}