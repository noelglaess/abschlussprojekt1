package edu.kit.assignmentone.ui;

import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.PlayerType;

import java.util.Optional;

/**
 * Utility class to format the game board for console output.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class BoardFormatter {

    private static final int BOARD_SIZE = 7;
    private static final String SYMBOL_PLAYER = "X";
    private static final String SYMBOL_ENEMY = "Y";
    private static final String SYMBOL_HIGHLIGHT = "*";
    private static final String SYMBOL_EMPTY = " ";

    private BoardFormatter() {
        throw new UnsupportedOperationException(StringConstants.UTILITY_CLASS_ERROR);
    }

    /**
     * Formats the board into a string representation.
     *
     * @param board The board to format
     * @param highlightPos The position to highlight, or null
     * @return The formatted board string
     */
    public static String formatBoard(Board board, Position highlightPos) {
        StringBuilder builder = new StringBuilder();

        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            builder.append(String.format(StringConstants.FMT_ROW, row + 1));

            for (int col = 0; col < BOARD_SIZE; col++) {
                Position currentPos = new Position(col, row);
                Optional<PlacedUnit> unitAtPos = board.getUnitAt(currentPos);

                String cellContent = SYMBOL_EMPTY;
                if (unitAtPos.isPresent()) {
                    cellContent = unitAtPos.get().getOwner() == PlayerType.PLAYER ? SYMBOL_PLAYER : SYMBOL_ENEMY;
                }

                String prefix = currentPos.equals(highlightPos) ? SYMBOL_HIGHLIGHT : SYMBOL_EMPTY;
                builder.append(String.format(StringConstants.FMT_CELL, prefix, cellContent));
            }
            builder.append(System.lineSeparator());
        }
        builder.append(StringConstants.COL_FOOTER).append(System.lineSeparator());
        return builder.toString();
    }
}