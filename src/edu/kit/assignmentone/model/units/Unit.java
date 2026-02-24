package edu.kit.assignmentone.model.units;

import edu.kit.assignmentone.model.MathUtils;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.PlayerType;
import java.util.Optional;

/**
 * Represents a single unit configuration as read from the units file.
 *
 * @author uXXXXX
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

    public Optional<Unit> combineWith(Unit other) {
        Optional<Unit> result = Optional.empty();

        if (!this.fullName().equals(other.fullName())) {
            String newQualifier = other.qualifier() + " " + this.qualifier();
            String newRoleString = other.roleString();
            UnitType newType = other.type();

            int thisAttack = this.attack;
            int thisDefense = this.defense;
            int otherAttack = other.attack();
            int otherDefense = other.defense();

            if (thisAttack > otherAttack && thisAttack == otherDefense && otherAttack == thisDefense) {
                result = Optional.of(new Unit(newQualifier, newRoleString, newType, thisAttack, otherDefense));
            } else {
                int maxGcd = Math.max(MathUtils.gcd(thisAttack, otherAttack), MathUtils.gcd(thisDefense, otherDefense));
                if (maxGcd > 100) {
                    result = Optional.of(new Unit(newQualifier, newRoleString, newType,
                            thisAttack + otherAttack - maxGcd, thisDefense + otherDefense - maxGcd));
                } else if (maxGcd == 100 && (MathUtils.hasPrime(thisAttack / 100, otherAttack / 100)
                        || MathUtils.hasPrime(thisDefense / 100, otherDefense / 100))) {
                    result = Optional.of(new Unit(newQualifier, newRoleString, newType, thisAttack + otherAttack, thisDefense + otherDefense));
                }
            }
        }
        return result;
    }

    public String formatHandInfo(int index) {
        return String.format(StringConstants.FMT_HAND_CARD, index, this.fullName(), this.attack, this.defense);
    }

    public String formatDiscardInfo(PlayerType playerType) {
        return String.format(StringConstants.FMT_DISCARDED, playerType.getDisplayName(),
                this.fullName(), this.attack, this.defense);
    }
}