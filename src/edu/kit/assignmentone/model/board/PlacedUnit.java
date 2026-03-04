package edu.kit.assignmentone.model.board;

import edu.kit.assignmentone.model.DuelResult;
import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;

/**
 * Represents a unit that is currently placed on the board.
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

    public PlacedUnit(Unit unit, PlayerType owner) {
        this.unit = unit;
        this.owner = owner;
        this.flipped = false;
        this.moved = false;
        this.blocking = false;
    }

    public Unit getUnit() { return this.unit; }
    public void setUnit(Unit unit) { this.unit = unit; }
    public PlayerType getOwner() { return this.owner; }

    public boolean isFlipped() { return this.flipped; }
    public void setFlipped(boolean flipped) { this.flipped = flipped; }

    public void setMoved(boolean moved) { this.moved = moved; }
    public boolean isBlocking() { return this.blocking; }

    public boolean isKing() { return this.unit.fullName().equals(StringConstants.KING_NAME); }
    public int getAttack() { return this.unit.attack(); }
    public int getDefense() { return this.unit.defense(); }
    public String getName() { return this.unit.fullName(); }

    public boolean unblockIfBlocking() {
        boolean unblocked = false;
        if (this.blocking) {
            this.blocking = false;
            unblocked = true;
        }
        return unblocked;
    }

    public boolean flipIfCovered() {
        boolean flippedNow = false;
        if (!this.flipped && !this.isKing()) {
            this.flipped = true;
            flippedNow = true;
        }
        return flippedNow;
    }

    public void block() {
        this.blocking = true;
        this.moved = true;
    }

    public boolean isUnmovedEnemy() {
        return this.owner == PlayerType.ENEMY && !this.moved && !this.isKing();
    }

    public void requireValidMove(int distance, PlacedUnit targetUnit) {
        if (this.moved) {
            throw new IllegalStateException(StringConstants.ERR_ALREADY_MOVED);
        }
        if (distance > 1) {
            throw new IllegalStateException(StringConstants.ERR_MOVE_DIST);
        }
        // FIX: targetUnit != this sorgt dafür, dass "en place" Züge nicht blockiert werden!
        if (targetUnit != null && targetUnit != this) {
            if (targetUnit.isKing() && this.owner == targetUnit.owner) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
            if (this.isKing() && this.owner != targetUnit.owner) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
        }
    }

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
                result = new DuelResult(0, null, false, true, true);
            } else if (attackerAttack < defenderDefense) {
                result = new DuelResult(defenderDefense - attackerAttack, attackerOwner, false, false, false);
            } else {
                result = new DuelResult(0, null, false, false, false);
            }
        } else {
            if (attackerAttack > defenderAttack) {
                result = new DuelResult(attackerAttack - defenderAttack, defenderOwner, false, true, true);
            } else if (attackerAttack < defenderAttack) {
                result = new DuelResult(defenderAttack - attackerAttack, attackerOwner, true, false, false);
            } else {
                result = new DuelResult(0, null, true, true, false);
            }
        }
        return result;
    }
}