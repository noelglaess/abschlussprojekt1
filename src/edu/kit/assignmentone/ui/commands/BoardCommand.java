package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.ui.BoardFormatter;

/**
 * Command to print the current board.
 *
 * @author uXXXXX
 * @version 1.0
 */
public class BoardCommand extends Command {

    public BoardCommand(Game game) {
        super(StringConstants.CMD_BOARD, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }
        System.out.print(BoardFormatter.formatBoard(this.getGame().getBoard(), this.getGame().getSelectedPosition()));
    }
}