package edu.kit.assignmentone.model.units;

import edu.kit.assignmentone.model.MathUtils;
import edu.kit.assignmentone.model.StringConstants;
import java.util.Optional;

/**
 * Represents a single unit configuration as read from the units file.
 *
 * @param name The name of the unit
 * @param type The type of the unit
 * @param attack The attack value
 * @param defense The defense value
 * @author Programmieren-Team
 * @version 1.0
 */
public record Unit(String name, UnitType type, int attack, int defense) {

    /**
     * Creates a deep copy of this unit.
     * @return A new Unit instance.
     */
    public Unit copy() {
        return new Unit(this.name, this.type, this.attack, this.defense);
    }

    /**
     * Attempts to combine this unit with another unit on the board.
     *
     * @param other The unit already on the field
     * @return An Optional containing the combined unit, or empty if incompatible
     */
    public Optional<Unit> combineWith(Unit other) {
        if (this.name.equals(other.name())) {
            return Optional.empty();
        }

        String newName = generateCombinedName(this.name, other.name());
        UnitType newType = other.type();

        if (this.attack > other.attack() && this.attack == other.defense() && other.attack() == this.defense) {
            return Optional.of(new Unit(newName, newType, this.attack, other.defense()));
        }

        int gcdAtk = MathUtils.gcd(this.attack, other.attack());
        int gcdDef = MathUtils.gcd(this.defense, other.defense());
        int maxGcd = Math.max(gcdAtk, gcdDef);

        if (maxGcd > 100) {
            return Optional.of(new Unit(newName, newType, this.attack + other.attack() - maxGcd, this.defense + other.defense() - maxGcd));
        }

        if (maxGcd == 100 && (MathUtils.isPrime(this.attack / 100) && MathUtils.isPrime(other.attack() / 100)
                || MathUtils.isPrime(this.defense / 100) && MathUtils.isPrime(other.defense() / 100))) {
            return Optional.of(new Unit(newName, newType, this.attack + other.attack(), this.defense + other.defense()));
        }

        return Optional.empty();
    }

    private String generateCombinedName(String nameA, String nameB) {
        String[] partsA = nameA.split(" ", 2);
        String[] partsB = nameB.split(" ", 2);
        String qualA = partsA.length > 0 ? partsA[0] : nameA;
        String qualB = partsB.length > 0 ? partsB[0] : nameB;
        String roleB = partsB.length > 1 ? partsB[1] : StringConstants.EMPTY;
        return (qualB + " " + qualA + " " + roleB).trim();
    }
}