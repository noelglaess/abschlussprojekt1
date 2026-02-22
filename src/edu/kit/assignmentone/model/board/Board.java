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

    /** Creates a new, empty game board. */
    public Board() {
        this.grid = new PlacedUnit[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Checks if empty.
     * @param position The position
     * @return true if empty
     */
    public boolean isEmpty(Position position) {
        return this.grid[position.col()][position.row()] == null;
    }

    /**
     * Places a unit.
     * @param position The position
     * @param unit The unit
     */
    public void placeUnit(Position position, PlacedUnit unit) {
        if (!isEmpty(position)) throw new IllegalStateException("Position is already occupied.");
        this.grid[position.col()][position.row()] = unit;
    }

    /**
     * Removes a unit.
     * @param position The position
     * @return The removed unit
     */
    public PlacedUnit removeUnit(Position position) {
        int col = position.col();
        int row = position.row();
        PlacedUnit unit = this.grid[col][row];
        this.grid[col][row] = null;
        return unit;
    }

    /**
     * Gets the unit.
     * @param position The position
     * @return An Optional
     */
    public Optional<PlacedUnit> getUnitAt(Position position) {
        return Optional.ofNullable(this.grid[position.col()][position.row()]);
    }

    /**
     * Moves a unit.
     * @param from source
     * @param to target
     */
    public void moveUnit(Position from, Position to) {
        if (isEmpty(from)) throw new IllegalStateException("No unit at source.");
        if (!isEmpty(to)) throw new IllegalStateException("Target occupied.");
        PlacedUnit unit = removeUnit(from);
        placeUnit(to, unit);
    }

    /**
     * Finds a unit by name and owner.
     * @param name Name
     * @param owner Owner
     * @return Position or null
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
     * @param pos center
     * @param dirs directions
     * @param type type
     * @param exclude exclude
     * @return amount
     */
    public int countUnits(Position pos, int[][] dirs, PlayerType type, Position exclude) {
        int count = 0;
        int pCol = pos.col();
        int pRow = pos.row();
        for (int[] dir : dirs) {
            int tCol = pCol + dir[0];
            int tRow = pRow + dir[1];
            if (Position.isValid(tCol, tRow)) {
                Position check = new Position(tCol, tRow);
                if (!check.equals(exclude)) {
                    Optional<PlacedUnit> opt = getUnitAt(check);
                    if (opt.isPresent() && opt.get().getOwner() == type) count++;
                }
            }
        }
        return count;
    }

    /**
     * Gets the highest attack value.
     * @param pos center
     * @return max attack
     */
    public int getMaxSurroundingEnemyAtk(Position pos) {
        int maxAtk = 0;
        int pCol = pos.col();
        int pRow = pos.row();
        for (int[] dir : DIR_4) {
            int tCol = pCol + dir[0];
            int tRow = pRow + dir[1];
            if (Position.isValid(tCol, tRow)) {
                Optional<PlacedUnit> opt = getUnitAt(new Position(tCol, tRow));
                if (opt.isPresent() && opt.get().getOwner() == PlayerType.PLAYER) {
                    if (opt.get().getAttack() > maxAtk) maxAtk = opt.get().getAttack();
                }
            }
        }
        return maxAtk;
    }
}