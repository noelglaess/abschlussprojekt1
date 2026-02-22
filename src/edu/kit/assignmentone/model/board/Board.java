package edu.kit.assignmentone.model.board;

import java.util.Optional;

/**
 * Represents the 7x7 game board.
 *
 * @author Programmieren-Team
 */
public class Board {

    private static final int BOARD_SIZE = 7;
    private final PlacedUnit[][] grid;

    /**
     * Creates a new, empty game board.
     */
    public Board() {
        this.grid = new PlacedUnit[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Checks if a specific position on the board is empty.
     *
     * @param position The position to check
     * @return true if empty, false if occupied
     */
    public boolean isEmpty(Position position) {
        return this.grid[position.col()][position.row()] == null;
    }

    /**
     * Places a unit at the given position.
     *
     * @param position The position to place the unit
     * @param unit The placed unit including its owner
     * @throws IllegalStateException If the position is already occupied
     */
    public void placeUnit(Position position, PlacedUnit unit) {
        if (!isEmpty(position)) {
            throw new IllegalStateException("Position " + position.toString() + " is already occupied.");
        }
        this.grid[position.col()][position.row()] = unit;
    }

    /**
     * Removes and returns a unit from the given position.
     *
     * @param position The position to remove the unit from
     * @return The removed unit, or null if empty
     */
    public PlacedUnit removeUnit(Position position) {
        PlacedUnit unit = this.grid[position.col()][position.row()];
        this.grid[position.col()][position.row()] = null;
        return unit;
    }

    /**
     * Gets the unit at the given position safely using Optional.
     *
     * @param position The position to check
     * @return An Optional containing the placed unit if present
     */
    public Optional<PlacedUnit> getUnitAt(Position position) {
        return Optional.ofNullable(this.grid[position.col()][position.row()]);
    }

    /**
     * Moves a unit from a source position to a target position.
     *
     * @param from The current position
     * @param to The target position
     * @throws IllegalStateException If the source is empty or the target is occupied
     */
    public void moveUnit(Position from, Position to) {
        if (isEmpty(from)) {
            throw new IllegalStateException("No unit at source position " + from.toString());
        }
        if (!isEmpty(to)) {
            throw new IllegalStateException("Target position " + to.toString() + " is occupied.");
        }
        PlacedUnit unit = removeUnit(from);
        placeUnit(to, unit);
    }
}