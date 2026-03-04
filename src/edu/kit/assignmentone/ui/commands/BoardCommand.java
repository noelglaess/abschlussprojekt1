package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.ui.BoardFormatter;

/**
 * Command to print the current board.
 *
 * @author uqhkm
 * @version 1.0
 */
public class BoardCommand extends Command {

    /**
     * Creates a new board command.
     * @param game The game instance
     */
    public BoardCommand(Game game) {
        super(StringConstants.COMMAND_BOARD, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERROR_NO_ARGUMENTS);
        }
        System.out.print(BoardFormatter.formatBoard(this.getGame().getBoard(), this.getGame().getSelectedPosition()));
    }
}