package edu.kit.assignmentone.ui;

import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.PlayerType;

import java.util.Optional;

/**
 * Utility class to format the game board for console output.
 *
 * @author Programmieren-Team
 */
public final class BoardFormatter {

    private static final int BOARD_SIZE = 7;
    private static final String ROW_PREFIX_FORMAT = "%d |";
    private static final String COLUMN_FOOTER = "    A   B   C   D   E   F   G";
    private static final String SYMBOL_PLAYER = "X";
    private static final String SYMBOL_ENEMY = "Y";
    private static final String SYMBOL_HIGHLIGHT = "*";

    private BoardFormatter() {
        throw new UnsupportedOperationException("Utility classes cannot be instantiated");
    }

    public static String formatBoard(Board board, Position highlightPos) {
        StringBuilder builder = new StringBuilder();

        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            builder.append(String.format(ROW_PREFIX_FORMAT, row + 1));

            for (int col = 0; col < BOARD_SIZE; col++) {
                Position currentPos = new Position(col, row);
                Optional<PlacedUnit> unitAtPos = board.getUnitAt(currentPos);

                String cellContent = " ";
                if (unitAtPos.isPresent()) {
                    cellContent = unitAtPos.get().getOwner() == PlayerType.PLAYER ? SYMBOL_PLAYER : SYMBOL_ENEMY;
                }

                String prefix = currentPos.equals(highlightPos) ? SYMBOL_HIGHLIGHT : " ";
                builder.append(String.format("%s%s |", prefix, cellContent));
            }
            builder.append(System.lineSeparator());
        }
        builder.append(COLUMN_FOOTER);
        return builder.toString();
    }
}