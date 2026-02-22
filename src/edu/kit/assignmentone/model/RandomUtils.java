package edu.kit.assignmentone.model;

import edu.kit.assignmentone.StringConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for weighted random selections as specified in the assignment.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class RandomUtils {

    private RandomUtils() {
        throw new UnsupportedOperationException(StringConstants.UTILITY_CLASS_ERROR);
    }

    /**
     * Performs a weighted random selection based on the given weights.
     *
     * @param weights The list of weights
     * @param rnd The global random instance
     * @return The index of the selected option
     */
    public static int weightedRandom(List<Integer> weights, Random rnd) {
        int totalSum = 0;
        int[] sums = new int[weights.size()];

        for (int index = 0; index < weights.size(); index++) {
            int weight = Math.max(0, weights.get(index));
            totalSum += weight;
            sums[index] = totalSum;
        }

        if (totalSum == 0) {
            return 0;
        }

        int randomValue = rnd.nextInt(totalSum) + 1;

        for (int index = 0; index < sums.length; index++) {
            if (randomValue <= sums[index]) {
                return index;
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
        int maxWeight = 0;
        for (int weight : weights) {
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }

        List<Integer> reverseWeights = new ArrayList<>();
        for (int weight : weights) {
            reverseWeights.add(Math.max(0, maxWeight - weight));
        }

        return weightedRandom(reverseWeights, rnd);
    }
}