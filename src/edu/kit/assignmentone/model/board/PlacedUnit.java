package edu.kit.assignmentone.model.board;

import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;

/**
 * Represents a unit that is currently placed on the board, including its owner and state.
 *
 * @author Programmieren-Team
 */
public class PlacedUnit {

    private Unit unit;
    private final PlayerType owner;
    private boolean isFlipped;
    private boolean hasMoved;
    private boolean isBlocking;

    /**
     * Creates a new placed unit.
     *
     * @param unit The base unit statistics
     * @param owner The player who owns this unit
     */
    public PlacedUnit(Unit unit, PlayerType owner) {
        this.unit = unit;
        this.owner = owner;
        this.isFlipped = false;
        this.hasMoved = false;
        this.isBlocking = false;
    }

    public Unit getUnit() {
        return this.unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public PlayerType getOwner() {
        return this.owner;
    }

    public boolean isFlipped() {
        return this.isFlipped;
    }

    public void setFlipped(boolean flipped) {
        this.isFlipped = flipped;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public void setMoved(boolean moved) {
        this.hasMoved = moved;
    }

    public boolean isBlocking() {
        return this.isBlocking;
    }

    public void setBlocking(boolean blocking) {
        this.isBlocking = blocking;
    }
}