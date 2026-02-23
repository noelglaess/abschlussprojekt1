package edu.kit.assignmentone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for weighted random selections.
 *
 * @author uXXXXX
 * @version 1.0
 */
public final class RandomUtils {

    private RandomUtils() { }

    /**
     * Performs a weighted random selection.
     * @param weights The list of weights
     * @param rnd The global random instance
     * @return The index
     */
    public static int weightedRandom(List<Integer> weights, Random rnd) {
        int result = weights.size() - 1;
        int totalSum = 0;
        int[] sums = new int[weights.size()];

        for (int index = 0; index < weights.size(); index++) {
            int weight = Math.max(0, weights.get(index));
            totalSum += weight;
            sums[index] = totalSum;
        }

        if (totalSum > 0) {
            int randomValue = rnd.nextInt(totalSum) + 1;
            for (int index = 0; index < sums.length; index++) {
                if (randomValue <= sums[index]) {
                    result = index;
                    break;
                }
            }
        } else {
            result = 0;
        }
        return result;
    }

    /**
     * Performs a reverse weighted random selection.
     * @param weights The original weights
     * @param rnd The global random instance
     * @return The index
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