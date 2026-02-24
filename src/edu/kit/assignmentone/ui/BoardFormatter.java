package edu.kit.assignmentone.ui;

import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.PlayerType;

/**
 * Utility class to format the game board for console output.
 *
 * @author uXXXXX
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

    public static String formatBoard(Board board, Position highlightPos) {
        StringBuilder builder = new StringBuilder();

        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            builder.append(String.format(StringConstants.FMT_ROW, row + 1));

            for (int col = 0; col < BOARD_SIZE; col++) {
                Position currentPos = new Position(col, row);
                // FIX: Wir holen den Wert aus dem Optional oder nutzen null, falls leer
                PlacedUnit unit = board.getUnitAt(currentPos).orElse(null);

                String cellContent = getCellContent(unit);
                String prefix = currentPos.equals(highlightPos) ? SYMBOL_HIGHLIGHT : SYMBOL_EMPTY;

                builder.append(String.format(StringConstants.FMT_CELL, prefix, cellContent));
            }
            builder.append(System.lineSeparator());
        }
        builder.append(StringConstants.COL_FOOTER).append(System.lineSeparator());
        return builder.toString();
    }

    /**
     * Determines the display string for a cell based on the unit present.
     * * @param unit the unit at the position, or null if empty
     * @return the formatted content string
     */
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