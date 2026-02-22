package edu.kit.assignmentone.model.units;

import edu.kit.assignmentone.StringConstants;
import edu.kit.assignmentone.model.MathUtils;

import java.util.Optional;

/**
 * Utility class to handle the combination of two units.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class UnitCombiner {

    private static final int HUNDRED = 100;

    private UnitCombiner() {
        throw new UnsupportedOperationException(StringConstants.UTILITY_CLASS_ERROR);
    }

    /**
     * Attempts to combine unit A and unit B.
     *
     * @param unitA The moving/placed unit
     * @param unitB The unit already on the field
     * @return An Optional containing the new combined unit, or empty if incompatible
     */
    public static Optional<Unit> tryCombine(Unit unitA, Unit unitB) {
        if (unitA.name().equals(unitB.name())) {
            return Optional.empty();
        }

        String newName = generateCombinedName(unitA.name(), unitB.name());
        UnitType newType = unitB.type();

        if (isSymbiosis(unitA, unitB)) {
            return Optional.of(new Unit(newName, newType, unitA.attack(), unitB.defense()));
        }

        int gcdAtk = MathUtils.gcd(unitA.attack(), unitB.attack());
        int gcdDef = MathUtils.gcd(unitA.defense(), unitB.defense());
        int maxGcd = Math.max(gcdAtk, gcdDef);

        if (maxGcd > HUNDRED) {
            int newAtk = unitA.attack() + unitB.attack() - maxGcd;
            int newDef = unitA.defense() + unitB.defense() - maxGcd;
            return Optional.of(new Unit(newName, newType, newAtk, newDef));
        }

        if (maxGcd == HUNDRED && isPrimeCompatible(unitA, unitB)) {
            int newAtk = unitA.attack() + unitB.attack();
            int newDef = unitA.defense() + unitB.defense();
            return Optional.of(new Unit(newName, newType, newAtk, newDef));
        }

        return Optional.empty();
    }

    private static String generateCombinedName(String nameA, String nameB) {
        String[] partsA = nameA.split(" ", 2);
        String[] partsB = nameB.split(" ", 2);
        String qualA = partsA.length > 0 ? partsA[0] : nameA;
        String qualB = partsB.length > 0 ? partsB[0] : nameB;
        String roleB = partsB.length > 1 ? partsB[1] : StringConstants.EMPTY;
        return (qualB + " " + qualA + " " + roleB).trim();
    }

    private static boolean isSymbiosis(Unit unitA, Unit unitB) {
        return unitA.attack() > unitB.attack()
                && unitA.attack() == unitB.defense()
                && unitB.attack() == unitA.defense();
    }

    private static boolean isPrimeCompatible(Unit unitA, Unit unitB) {
        boolean atkPrime = MathUtils.isPrime(unitA.attack() / HUNDRED) && MathUtils.isPrime(unitB.attack() / HUNDRED);
        boolean defPrime = MathUtils.isPrime(unitA.defense() / HUNDRED) && MathUtils.isPrime(unitB.defense() / HUNDRED);
        return atkPrime || defPrime;
    }
}