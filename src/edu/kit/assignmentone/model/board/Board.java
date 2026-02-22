package edu.kit.assignmentone.model.board;

import java.util.Optional;

/**
 * Represents the 7x7 game board.
 */
public class Board {

    private static final int BOARD_SIZE = 7;
    private final PlacedUnit[][] grid;

    public Board() {
        this.grid = new PlacedUnit[BOARD_SIZE][BOARD_SIZE];
    }

    public boolean isEmpty(Position position) {
        return this.grid[position.col()][position.row()] == null;
    }

    public void placeUnit(Position position, PlacedUnit unit) {
        if (!isEmpty(position)) {
            throw new IllegalStateException("Position " + position.toString() + " is already occupied.");
        }
        this.grid[position.col()][position.row()] = unit;
    }

    public PlacedUnit removeUnit(Position position) {
        int col = position.col();
        int row = position.row();
        PlacedUnit unit = this.grid[col][row];
        this.grid[col][row] = null;
        return unit;
    }

    public Optional<PlacedUnit> getUnitAt(Position position) {
        return Optional.ofNullable(this.grid[position.col()][position.row()]);
    }

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