package edu.kit.assignmentone.model.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a position on the game board.
 * The board uses columns A-G and rows 1-7.
 *
 * @param col The column index (0 to 6, where 0 = 'A')
 * @param row The row index (0 to 6, where 0 = '1')
 * @author Programmieren-Team
 * @version 1.0
 */
public record Position(int col, int row) {

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 6;
    private static final char COL_OFFSET = 'A';
    private static final char ROW_OFFSET = '1';
    private static final int EXPECTED_LENGTH = 2;

    /**
     * Parses a string representation (e.g., "D5") into a Position.
     *
     * @param posString The string representation of the position
     * @return The parsed Position
     * @throws IllegalArgumentException If the string format or bounds are invalid
     */
    public static Position fromString(String posString) {
        if (posString == null || posString.length() != EXPECTED_LENGTH) {
            throw new IllegalArgumentException("Position must be exactly 2 characters long.");
        }

        char colChar = Character.toUpperCase(posString.charAt(0));
        char rowChar = posString.charAt(1);

        int col = colChar - COL_OFFSET;
        int row = rowChar - ROW_OFFSET;

        if (!isValid(col, row)) {
            throw new IllegalArgumentException("Position " + posString + " is out of bounds.");
        }

        return new Position(col, row);
    }

    /**
     * Checks if the given column and row are within the board's bounds.
     *
     * @param col The column index
     * @param row The row index
     * @return true if valid, false otherwise
     */
    public static boolean isValid(int col, int row) {
        return col >= MIN_INDEX && col <= MAX_INDEX && row >= MIN_INDEX && row <= MAX_INDEX;
    }

    /**
     * Calculates the Manhattan distance between this position and another one.
     *
     * @param other The other position
     * @return The distance in steps
     */
    public int distanceTo(Position other) {
        return Math.abs(this.col - other.col()) + Math.abs(this.row - other.row());
    }

    /**
     * Returns a list of adjacent positions (up, right, down, left) including the current position itself.
     *
     * @return List of positions
     */
    public List<Position> getAdjacentAndCenter() {
        List<Position> list = new ArrayList<>();
        list.add(new Position(this.col, this.row + 1));
        list.add(new Position(this.col + 1, this.row));
        list.add(new Position(this.col, this.row - 1));
        list.add(new Position(this.col - 1, this.row));
        list.add(this);
        return list;
    }

    @Override
    public String toString() {
        return "" + (char) (col + COL_OFFSET) + (char) (row + ROW_OFFSET);
    }
}