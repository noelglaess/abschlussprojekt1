package edu.kit.assignmentone.model.units;

import edu.kit.assignmentone.model.MathUtils;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.PlayerType;
import java.util.Optional;

/**
 * Represents a single unit configuration as read from the units file.
 *
 * @param name The name of the unit
 * @param type The type of the unit
 * @param attack The attack value
 * @param defense The defense value
 * @author uqhkm
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
        Optional<Unit> result = Optional.empty();

        if (!this.name.equals(other.name())) {
            String newName = generateCombinedName(this.name, other.name());
            UnitType newType = other.type();

            int tAtk = this.attack;
            int tDef = this.defense;
            int oAtk = other.attack();
            int oDef = other.defense();

            if (tAtk > oAtk && tAtk == oDef && oAtk == tDef) {
                result = Optional.of(new Unit(newName, newType, tAtk, oDef));
            } else {
                int maxGcd = Math.max(MathUtils.gcd(tAtk, oAtk), MathUtils.gcd(tDef, oDef));
                if (maxGcd > 100) {
                    result = Optional.of(new Unit(newName, newType,
                            tAtk + oAtk - maxGcd, tDef + oDef - maxGcd));
                } else if (maxGcd == 100 && (MathUtils.hasPrime(tAtk / 100, oAtk / 100)
                        || MathUtils.hasPrime(tDef / 100, oDef / 100))) {
                    result = Optional.of(new Unit(newName, newType, tAtk + oAtk, tDef + oDef));
                }
            }
        }
        return result;
    }

    private String generateCombinedName(String nameA, String nameB) {
        String[] partsA = nameA.split(" ", 2);
        String[] partsB = nameB.split(" ", 2);
        String qualA = partsA.length > 0 ? partsA[0] : nameA;
        String qualB = partsB.length > 0 ? partsB[0] : nameB;
        String roleB = partsB.length > 1 ? partsB[1] : StringConstants.EMPTY;
        return (qualB + " " + qualA + " " + roleB).trim();
    }

    /**
     * Formats the unit for the hand display.
     * @param index The index
     * @return Formatted string
     */
    public String formatHandInfo(int index) {
        return String.format(StringConstants.FMT_HAND_CARD, index, this.name, this.attack, this.defense);
    }

    /**
     * Formats the unit for the discard display.
     * @param type Player type
     * @return Formatted string
     */
    public String formatDiscardInfo(PlayerType type) {
        return String.format(StringConstants.FMT_DISCARDED, type.getDisplayName(),
                this.name, this.attack, this.defense);
    }
}