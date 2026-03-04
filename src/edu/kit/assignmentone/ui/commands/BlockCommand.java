package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.ui.BoardFormatter;

/**
 * Command to block with the selected unit.
 *
 * @author uqhkm
 * @version 1.0
 */
public class BlockCommand extends Command {

    /**
     * Creates a new block command.
     * @param game The game instance
     */
    public BlockCommand(Game game) {
        super(StringConstants.COMMAND_BLOCK, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERROR_NO_ARGUMENTS);
        }
        Game currentGame = this.getGame();
        Board board = currentGame.getBoard();
        Position selectedPosition = currentGame.getSelectedPosition();

        if (selectedPosition == null || board.isEmpty(selectedPosition)) {
            throw new IllegalStateException(StringConstants.ERROR_NO_SELECTION);
        }

        PlacedUnit unit = board.getUnitAt(selectedPosition).orElseThrow();
        unit.requireValidMove(0, unit);

        unit.block();
        System.out.printf(StringConstants.FORMAT_BLOCKS, unit.getName(), selectedPosition);
        System.out.print(BoardFormatter.formatBoard(board, selectedPosition));
        System.out.println(unit.formatInfo(currentGame));
    }
}