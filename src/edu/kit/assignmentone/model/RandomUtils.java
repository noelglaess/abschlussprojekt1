package edu.kit.assignmentone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for weighted random selections.
 *
 * @author uqhkm
 * @version 1.0
 */
public final class RandomUtils {

    private RandomUtils() { }

    /**
     * Performs a weighted random selection.
     * @param weights The list of weights
     * @param randomGenerator The global random instance
     * @return The chosen index
     */
    public static int weightedRandom(List<Integer> weights, Random randomGenerator) {
        int resultIndex = weights.size() - 1;
        int totalSum = 0;
        int[] cumulativeSums = new int[weights.size()];

        for (int index = 0; index < weights.size(); index++) {
            int weight = Math.max(0, weights.get(index));
            totalSum += weight;
            cumulativeSums[index] = totalSum;
        }

        if (totalSum > 0) {
            int randomValue = randomGenerator.nextInt(totalSum) + 1;
            for (int index = 0; index < cumulativeSums.length; index++) {
                if (randomValue <= cumulativeSums[index]) {
                    resultIndex = index;
                    break;
                }
            }
        } else {
            resultIndex = 0;
        }
        return resultIndex;
    }

    /**
     * Performs a reverse weighted random selection.
     * @param weights The original weights
     * @param randomGenerator The global random instance
     * @return The chosen index
     */
    public static int reverseWeightedRandom(List<Integer> weights, Random randomGenerator) {
        int maximumWeight = 0;
        for (int weight : weights) {
            if (weight > maximumWeight) {
                maximumWeight = weight;
            }
        }

        List<Integer> reverseWeights = new ArrayList<>();
        for (int weight : weights) {
            reverseWeights.add(Math.max(0, maximumWeight - weight));
        }
        return weightedRandom(reverseWeights, randomGenerator);
    }
}