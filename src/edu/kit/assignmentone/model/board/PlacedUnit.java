package edu.kit.assignmentone.model.board;

import edu.kit.assignmentone.model.DuelResult;
import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;

/**
 * Represents a unit that is currently placed on the board.
 *
 * @author uqhkm
 * @version 1.0
 */
public class PlacedUnit {

    private Unit unit;
    private final PlayerType owner;
    private boolean flipped;
    private boolean moved;
    private boolean blocking;

    /**
     * Creates a newly placed unit.
     * @param unit  The logical unit definition
     * @param owner The player who owns this unit
     */
    public PlacedUnit(Unit unit, PlayerType owner) {
        this.unit = unit;
        this.owner = owner;
        this.flipped = false;
        this.moved = false;
        this.blocking = false;
    }

    /** @return the internal unit */
    public Unit getUnit() { return this.unit; }
    /** @param unit the internal unit to set */
    public void setUnit(Unit unit) { this.unit = unit; }
    /** @return the unit owner */
    public PlayerType getOwner() { return this.owner; }

    /** @return true if the unit is flipped */
    public boolean isFlipped() { return this.flipped; }
    /** @param flipped true to flip the unit */
    public void setFlipped(boolean flipped) { this.flipped = flipped; }

    /** @param moved true to mark the unit as moved this turn */
    public void setMoved(boolean moved) { this.moved = moved; }
    /** @return true if the unit is blocking */
    public boolean isBlocking() { return this.blocking; }

    /** @return true if this unit is a king */
    public boolean isKing() { return this.unit.fullName().equals(StringConstants.KING_NAME); }
    /** @return the attack value */
    public int getAttack() { return this.unit.attack(); }
    /** @return the defense value */
    public int getDefense() { return this.unit.defense(); }
    /** @return the full name of the unit */
    public String getName() { return this.unit.fullName(); }

    /**
     * Unblocks the unit if it is currently blocking.
     * @return true if the unit was unblocked
     */
    public boolean unblockIfBlocking() {
        boolean unblocked = false;
        if (this.blocking) {
            this.blocking = false;
            unblocked = true;
        }
        return unblocked;
    }

    /**
     * Flips the unit if it is currently covered (not flipped).
     * @return true if the unit was flipped by this call
     */
    public boolean flipIfCovered() {
        boolean flippedNow = false;
        if (!this.flipped && !this.isKing()) {
            this.flipped = true;
            flippedNow = true;
        }
        return flippedNow;
    }

    /**
     * Sets the unit to block and marks it as moved.
     */
    public void block() {
        this.blocking = true;
        this.moved = true;
    }

    /**
     * Checks if this unit is an unmoved enemy unit.
     * @return true if unmoved enemy
     */
    public boolean isUnmovedEnemy() {
        return this.owner == PlayerType.ENEMY && !this.moved && !this.isKing();
    }

    /**
     * Validates if a move is allowed.
     * @param distance   The distance to move
     * @param targetUnit The unit on the target square, if any
     * @throws IllegalStateException If the move is invalid
     */
    public void requireValidMove(int distance, PlacedUnit targetUnit) {
        if (this.moved) {
            throw new IllegalStateException(StringConstants.ERR_ALREADY_MOVED);
        }
        if (distance > 1) {
            throw new IllegalStateException(StringConstants.ERR_MOVE_DIST);
        }
        if (targetUnit != null && targetUnit != this) {
            if (targetUnit.isKing() && this.owner == targetUnit.owner) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
            if (this.isKing() && this.owner != targetUnit.owner) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
        }
    }

    /**
     * Formats the unit information for console output.
     * @param game The current game instance
     * @return The formatted info string
     */
    public String formatInfo(Game game) {
        if (this.isKing()) {
            return String.format(StringConstants.FMT_KING_NAME, this.owner.getDisplayName(), StringConstants.KING_NAME);
        }
        boolean isActivePlayer = game.getActivePlayerObject().getType() == this.owner;
        if (!this.flipped && !isActivePlayer) {
            return String.format(StringConstants.FMT_HIDDEN_UNIT, this.owner.getDisplayName());
        }
        return String.format(StringConstants.UNIT_INFO_FORMAT, this.getName(),
                this.owner.getDisplayName(), this.getAttack(), this.getDefense());
    }

    /**
     * Executes combat calculations against a defending unit.
     * @param defender The unit being attacked
     * @return The result of the duel
     */
    public DuelResult fightAgainst(PlacedUnit defender) {
        DuelResult result;
        int attackerAttack = this.getAttack();
        int defenderDefense = defender.getDefense();
        int defenderAttack = defender.getAttack();
        PlayerType defenderOwner = defender.getOwner();
        PlayerType attackerOwner = this.getOwner();

        if (defender.isKing()) {
            result = new DuelResult(attackerAttack, defenderOwner, false, false, false);
        } else if (defender.isBlocking()) {
            if (attackerAttack > defenderDefense) {
                result = new DuelResult(0, null, true, false, false);
            } else if (attackerAttack < defenderDefense) {
                result = new DuelResult(defenderDefense - attackerAttack, attackerOwner, false, false, false);
            } else {
                result = new DuelResult(0, null, false, false, false);
            }
        } else {
            if (attackerAttack > defenderAttack) {
                result = new DuelResult(attackerAttack - defenderAttack, defenderOwner, true, false, true);
            } else if (attackerAttack < defenderAttack) {
                result = new DuelResult(defenderAttack - attackerAttack, attackerOwner, false, true, false);
            } else {
                result = new DuelResult(0, null, true, true, false);
            }
        }
        return result;
    }
}