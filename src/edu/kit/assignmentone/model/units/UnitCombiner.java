package edu.kit.assignmentone.model.units;

import edu.kit.assignmentone.model.MathUtils;
import java.util.Optional;

/**
 * Utility class to handle the combination ("Zusammenschluss") of two units.
 *
 * @author Programmieren-Team
 */
public final class UnitCombiner {

    private static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";
    private static final int HUNDRED = 100;

    private UnitCombiner() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
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

        // Neuer Name: Qualifikator B + Qualifikator A + Rolle B
        String[] partsA = unitA.name().split(" ", 2);
        String[] partsB = unitB.name().split(" ", 2);

        // Fallback falls der Name nicht aus genau 2 Teilen besteht
        String qualA = partsA.length > 0 ? partsA[0] : unitA.name();
        String qualB = partsB.length > 0 ? partsB[0] : unitB.name();
        String roleB = partsB.length > 1 ? partsB[1] : "";

        String newName = (qualB + " " + qualA + " " + roleB).trim();
        UnitType newType = unitB.type();

        // 1. Symbiose
        if (unitA.attack() > unitB.attack()
                && unitA.attack() == unitB.defense()
                && unitB.attack() == unitA.defense()) {
            return Optional.of(new Unit(newName, newType, unitA.attack(), unitB.defense()));
        }

        int g3t = Math.max(MathUtils.gcd(unitA.attack(), unitB.attack()),
                MathUtils.gcd(unitA.defense(), unitB.defense()));

        // 2. Gleichgesinntheit
        if (g3t > HUNDRED) {
            int newAtk = unitA.attack() + unitB.attack() - g3t;
            int newDef = unitA.defense() + unitB.defense() - g3t;
            return Optional.of(new Unit(newName, newType, newAtk, newDef));
        }

        // 3. Primkompatibilität
        if (g3t == HUNDRED) {
            boolean atkPrime = MathUtils.isPrime(unitA.attack() / HUNDRED) && MathUtils.isPrime(unitB.attack() / HUNDRED);
            boolean defPrime = MathUtils.isPrime(unitA.defense() / HUNDRED) && MathUtils.isPrime(unitB.defense() / HUNDRED);

            if (atkPrime || defPrime) {
                int newAtk = unitA.attack() + unitB.attack();
                int newDef = unitA.defense() + unitB.defense();
                return Optional.of(new Unit(newName, newType, newAtk, newDef));
            }
        }

        return Optional.empty(); // Inkompatibel
    }
}