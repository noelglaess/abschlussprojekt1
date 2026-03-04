package edu.kit.assignmentone.model.board;

import edu.kit.assignmentone.model.StringConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a position on the game board.
 *
 * @param column The column index (0 to 6, where 0 = 'A')
 * @param row The row index (0 to 6, where 0 = '1')
 * @author uqhkm
 * @version 1.0
 */
public record Position(int column, int row) {

    private static final int MINIMUM_INDEX = 0;
    private static final int MAXIMUM_INDEX = 6;
    private static final char COLUMN_OFFSET = 'A';
    private static final char ROW_OFFSET = '1';
    private static final int EXPECTED_LENGTH = 2;

    /**
     * Parses a string representation into a Position.
     * @param positionString The string representation of the position
     * @return The parsed Position
     * @throws IllegalArgumentException If the string format or bounds are invalid
     */
    public static Position fromString(String positionString) {
        if (positionString == null || positionString.length() != EXPECTED_LENGTH) {
            throw new IllegalArgumentException(StringConstants.ERROR_POSITION_FORMAT);
        }

        char columnCharacter = Character.toUpperCase(positionString.charAt(0));
        char rowCharacter = positionString.charAt(1);

        int parsedColumn = columnCharacter - COLUMN_OFFSET;
        int parsedRow = rowCharacter - ROW_OFFSET;

        if (!isValid(parsedColumn, parsedRow)) {
            throw new IllegalArgumentException(String.format(StringConstants.ERROR_POSITION_OUT_OF_BOUNDS, positionString));
        }

        return new Position(parsedColumn, parsedRow);
    }

    /**
     * Checks if the given column and row are within bounds.
     * @param column The column index
     * @param row The row index
     * @return true if valid
     */
    public static boolean isValid(int column, int row) {
        return column >= MINIMUM_INDEX && column <= MAXIMUM_INDEX && row >= MINIMUM_INDEX && row <= MAXIMUM_INDEX;
    }

    /**
     * Calculates the Manhattan distance.
     * @param otherPosition The other position
     * @return The distance in steps
     */
    public int distanceTo(Position otherPosition) {
        return Math.abs(this.column - otherPosition.column()) + Math.abs(this.row - otherPosition.row());
    }

    /**
     * Translates the position by given offsets.
     * @param deltaColumn Column offset
     * @param deltaRow Row offset
     * @return New Position or null if invalid
     */
    public Position translate(int deltaColumn, int deltaRow) {
        int newColumn = this.column + deltaColumn;
        int newRow = this.row + deltaRow;
        return isValid(newColumn, newRow) ? new Position(newColumn, newRow) : null;
    }

    /**
     * Returns adjacent positions and center.
     * @return List of positions
     */
    public List<Position> getAdjacentAndCenter() {
        List<Position> positionList = new ArrayList<>();
        Position positionUp = translate(0, 1);
        if (positionUp != null) {
            positionList.add(positionUp);
        }
        Position positionRight = translate(1, 0);
        if (positionRight != null) {
            positionList.add(positionRight);
        }
        Position positionDown = translate(0, -1);
        if (positionDown != null) {
            positionList.add(positionDown);
        }
        Position positionLeft = translate(-1, 0);
        if (positionLeft != null) {
            positionList.add(positionLeft);
        }
        positionList.add(this);
        return positionList;
    }

    @Override
    public String toString() {
        return "" + (char) (column + COLUMN_OFFSET) + (char) (row + ROW_OFFSET);
    }
}