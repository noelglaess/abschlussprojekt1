package edu.kit.assignmentone.model.board;

import edu.kit.assignmentone.model.DuelResult;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;

/**
 * Represents a unit that is currently placed on the board, including its owner and state.
 *
 * @author uXXXXX
 * @version 1.0
 */
public class PlacedUnit {

    private Unit unit;
    private final PlayerType owner;
    private boolean flipped;
    private boolean moved;
    private boolean blocking;

    /**
     * Creates a new placed unit.
     * @param unit The unit stats
     * @param owner The player owner
     */
    public PlacedUnit(Unit unit, PlayerType owner) {
        this.unit = unit;
        this.owner = owner;
        this.flipped = false;
        this.moved = false;
        this.blocking = false;
    }

    /** @return the unit */
    public Unit getUnit() { return this.unit; }
    /** @param unit the unit to set */
    public void setUnit(Unit unit) { this.unit = unit; }
    /** @return the owner */
    public PlayerType getOwner() { return this.owner; }

    /** @return true if flipped */
    public boolean isFlipped() { return this.flipped; }
    /** @param flipped true if flipped */
    public void setFlipped(boolean flipped) { this.flipped = flipped; }

    /** @param moved true if moved */
    public void setMoved(boolean moved) { this.moved = moved; }

    /** @return true if blocking */
    public boolean isBlocking() { return this.blocking; }

    /** @return true if unit is king */
    public boolean isKing() { return this.unit.name().equals(StringConstants.KING_NAME); }
    /** @return the attack value */
    public int getAttack() { return this.unit.attack(); }
    /** @return the defense value */
    public int getDefense() { return this.unit.defense(); }
    /** @return the unit name */
    public String getName() { return this.unit.name(); }

    /**
     * Unblocks the unit if it was blocking.
     * @return true if it was unblocked
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
     * Flips the unit if it was covered.
     * @return true if it was flipped
     */
    public boolean flipIfCovered() {
        boolean flippedNow = false;
        if (!this.flipped && !this.isKing()) {
            this.flipped = true;
            flippedNow = true;
        }
        return flippedNow;
    }

    /** Sets the unit to blocking state. */
    public void block() {
        this.blocking = true;
        this.moved = true;
    }

    /**
     * Checks if this unit is an unmoved enemy.
     * @return true if unmoved enemy
     */
    public boolean isUnmovedEnemy() {
        return this.owner == PlayerType.ENEMY && !this.moved && !this.isKing();
    }

    /**
     * Validates movement rules.
     * @param distance Distance
     * @param targetUnit Target unit if present
     */
    public void requireValidMove(int distance, PlacedUnit targetUnit) {
        if (this.moved) {
            throw new IllegalStateException(StringConstants.ERR_ALREADY_MOVED);
        }
        if (distance > 1) {
            throw new IllegalStateException(StringConstants.ERR_MOVE_DIST);
        }
        if (targetUnit != null) {
            if (targetUnit.isKing() && this.owner == targetUnit.owner) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
            if (this.isKing() && this.owner != targetUnit.owner) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
        }
    }

    /**
     * Formats the unit info for the show command.
     * @return The formatted string
     */
    public String formatInfo() {
        return String.format(StringConstants.UNIT_INFO_FORMAT, this.getName(),
                this.owner.getDisplayName(), this.getAttack(), this.getDefense());
    }

    /**
     * Formats the unit info for the select command.
     * @return The formatted string
     */
    public String formatSelectInfo() {
        return String.format(StringConstants.UNIT_SELECT_FORMAT, this.getName(),
                this.unit.type().toString(), this.owner.getDisplayName(),
                this.getAttack(), this.getDefense());
    }

    /**
     * Executes the battle calculation.
     * @param defender Defending unit
     * @return DuelResult
     */
    public DuelResult fightAgainst(PlacedUnit defender) {
        DuelResult result;
        int atk = this.getAttack();
        int def = defender.getDefense();
        int defAtk = defender.getAttack();
        PlayerType defOwner = defender.getOwner();
        PlayerType myOwner = this.getOwner();

        if (defender.isKing()) {
            result = new DuelResult(defOwner, atk, false, false, false);
        } else if (defender.isBlocking()) {
            if (atk > def) {
                result = new DuelResult(null, 0, false, true, true);
            } else if (atk < def) {
                result = new DuelResult(myOwner, def - atk, false, false, false);
            } else {
                result = new DuelResult(null, 0, false, false, false);
            }
        } else {
            if (atk > defAtk) {
                result = new DuelResult(defOwner, atk - defAtk, false, true, true);
            } else if (atk < defAtk) {
                result = new DuelResult(myOwner, defAtk - atk, true, false, false);
            } else {
                result = new DuelResult(null, 0, true, true, false);
            }
        }
        return result;
    }
}