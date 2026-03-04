package edu.kit.assignmentone.ui;

import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.PlayerType;

/**
 * Utility class to format the game board for console output.
 *
 * @author uqhkm
 * @version 1.0
 */
public final class BoardFormatter {

    private static final int BOARD_SIZE = 7;
    private static final String SYMBOL_PLAYER = "x";
    private static final String SYMBOL_ENEMY = "y";
    private static final String SYMBOL_PLAYER_KING = "X";
    private static final String SYMBOL_ENEMY_KING = "Y";
    private static final String SYMBOL_HIGHLIGHT = "*";
    private static final String SYMBOL_EMPTY = " ";
    private static final String SYMBOL_BLOCK = "b";

    private BoardFormatter() { }

    /**
     * Formats the game board into a string representation.
     * @param board The game board
     * @param highlightPosition The currently selected position to highlight
     * @return The formatted board string
     */
    public static String formatBoard(Board board, Position highlightPosition) {
        StringBuilder builder = new StringBuilder();

        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            builder.append(String.format(StringConstants.FORMAT_ROW, row + 1));

            for (int column = 0; column < BOARD_SIZE; column++) {
                Position currentPosition = new Position(column, row);
                PlacedUnit unit = board.getUnitAt(currentPosition).orElse(null);

                String cellContent = getCellContent(unit);
                String prefix = currentPosition.equals(highlightPosition) ? SYMBOL_HIGHLIGHT : SYMBOL_EMPTY;

                builder.append(String.format(StringConstants.FORMAT_CELL, prefix, cellContent));
            }
            builder.append(System.lineSeparator());
        }
        builder.append(StringConstants.COLUMN_FOOTER).append(System.lineSeparator());
        return builder.toString();
    }

    private static String getCellContent(PlacedUnit unit) {
        if (unit == null) {
            return SYMBOL_EMPTY;
        }

        String cellContent;
        boolean isPlayer = unit.getOwner() == PlayerType.PLAYER;

        if (unit.isKing()) {
            cellContent = isPlayer ? SYMBOL_PLAYER_KING : SYMBOL_ENEMY_KING;
        } else {
            cellContent = isPlayer ? SYMBOL_PLAYER : SYMBOL_ENEMY;
        }

        if (unit.isBlocking()) {
            cellContent += SYMBOL_BLOCK;
        }

        return cellContent;
    }
}