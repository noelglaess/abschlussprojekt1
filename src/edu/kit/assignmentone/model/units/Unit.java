package edu.kit.assignmentone.model.units;

import edu.kit.assignmentone.model.MathUtils;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.PlayerType;
import java.util.Optional;

/**
 * Represents a single unit configuration as read from the units file.
 *
 * @author uqhkm
 * @version 1.0
 */
public record Unit(String qualifier, String roleString, UnitType type, int attack, int defense) {

    public Unit copy() {
        return new Unit(this.qualifier, this.roleString, this.type, this.attack, this.defense);
    }

    /**
     * @return The full name consisting of qualifier and role.
     */
    public String fullName() {
        return (this.qualifier + " " + this.roleString).trim();
    }

    public Optional<Unit> combineWith(Unit otherUnit) {
        Optional<Unit> resultOptional = Optional.empty();

        if (!this.fullName().equals(otherUnit.fullName())) {
            String newQualifier = otherUnit.qualifier() + " " + this.qualifier();
            String newRoleString = otherUnit.roleString();
            UnitType newType = otherUnit.type();

            int thisAttack = this.attack;
            int thisDefense = this.defense;
            int otherAttack = otherUnit.attack();
            int otherDefense = otherUnit.defense();

            if (thisAttack > otherAttack && thisAttack == otherDefense && otherAttack == thisDefense) {
                resultOptional = Optional.of(new Unit(newQualifier, newRoleString, newType, thisAttack, otherDefense));
            } else {
                int maximumGreatestCommonDivisor = Math.max(MathUtils.calculateGreatestCommonDivisor(thisAttack, otherAttack), MathUtils.calculateGreatestCommonDivisor(thisDefense, otherDefense));
                if (maximumGreatestCommonDivisor > 100) {
                    resultOptional = Optional.of(new Unit(newQualifier, newRoleString, newType,
                            thisAttack + otherAttack - maximumGreatestCommonDivisor, thisDefense + otherDefense - maximumGreatestCommonDivisor));
                } else if (maximumGreatestCommonDivisor == 100 && (MathUtils.hasPrime(thisAttack / 100, otherAttack / 100)
                        || MathUtils.hasPrime(thisDefense / 100, otherDefense / 100))) {
                    resultOptional = Optional.of(new Unit(newQualifier, newRoleString, newType, thisAttack + otherAttack, thisDefense + otherDefense));
                }
            }
        }
        return resultOptional;
    }

    public String formatHandInfo(int index) {
        return String.format(StringConstants.FORMAT_HAND_CARD, index, this.fullName(), this.attack, this.defense);
    }

    public String formatDiscardInfo(PlayerType playerType) {
        return String.format(StringConstants.FORMAT_DISCARDED, playerType.getDisplayName(),
                this.fullName(), this.attack, this.defense);
    }
}