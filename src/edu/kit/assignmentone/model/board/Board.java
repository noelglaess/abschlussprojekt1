package edu.kit.assignmentone.model.board;

import edu.kit.assignmentone.model.player.PlayerType;

import java.util.Optional;

/**
 * Represents the 7x7 game board.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class Board {

    private static final int BOARD_SIZE = 7;
    private static final int[][] DIR_4 = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
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
     */
    public void placeUnit(Position position, PlacedUnit unit) {
        if (!isEmpty(position)) {
            throw new IllegalStateException("Position is already occupied.");
        }
        this.grid[position.col()][position.row()] = unit;
    }

    /**
     * Removes a unit from the given position.
     *
     * @param position The position to remove the unit from
     * @return The removed unit
     */
    public PlacedUnit removeUnit(Position position) {
        PlacedUnit unit = this.grid[position.col()][position.row()];
        this.grid[position.col()][position.row()] = null;
        return unit;
    }

    /**
     * Gets the unit at the given position.
     *
     * @param position The position to check
     * @return An Optional containing the unit if present
     */
    public Optional<PlacedUnit> getUnitAt(Position position) {
        return Optional.ofNullable(this.grid[position.col()][position.row()]);
    }

    /**
     * Moves a unit from a source to a target position.
     *
     * @param from The source position
     * @param to The target position
     */
    public void moveUnit(Position from, Position to) {
        if (isEmpty(from)) throw new IllegalStateException("No unit at source position.");
        if (!isEmpty(to)) throw new IllegalStateException("Target position is occupied.");
        PlacedUnit unit = removeUnit(from);
        placeUnit(to, unit);
    }

    /**
     * Finds a unit by name and owner.
     *
     * @param name The name of the unit
     * @param owner The owner of the unit
     * @return The position or null
     */
    public Position findUnit(String name, PlayerType owner) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(col, row);
                Optional<PlacedUnit> opt = getUnitAt(pos);
                if (opt.isPresent() && opt.get().getOwner() == owner && opt.get().getName().equals(name)) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Counts specific units around a position.
     *
     * @param pos The center position
     * @param dirs The directions to check
     * @param type The owner type to count
     * @param exclude A position to exclude from counting
     * @return The amount of matching units
     */
    public int countUnits(Position pos, int[][] dirs, PlayerType type, Position exclude) {
        int count = 0;
        for (int[] dir : dirs) {
            Position check = new Position(pos.col() + dir[0], pos.row() + dir[1]);
            if (Position.isValid(check.col(), check.row()) && !check.equals(exclude)) {
                Optional<PlacedUnit> opt = getUnitAt(check);
                if (opt.isPresent() && opt.get().getOwner() == type) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Gets the highest attack value of surrounding enemy units.
     *
     * @param pos The center position
     * @return The maximum attack value found
     */
    public int getMaxSurroundingEnemyAtk(Position pos) {
        int maxAtk = 0;
        for (int[] dir : DIR_4) {
            Position check = new Position(pos.col() + dir[0], pos.row() + dir[1]);
            if (Position.isValid(check.col(), check.row())) {
                Optional<PlacedUnit> opt = getUnitAt(check);
                if (opt.isPresent() && opt.get().getOwner() == PlayerType.PLAYER) {
                    if (opt.get().getAttack() > maxAtk) {
                        maxAtk = opt.get().getAttack();
                    }
                }
            }
        }
        return maxAtk;
    }
}