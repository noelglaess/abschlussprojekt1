package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.ui.BoardFormatter;

/**
 * Command to select a position on the board and show its details.
 *
 * @author uqhkm
 * @version 1.0
 */
public class SelectCommand extends Command {

    /**
     * Creates a new select command.
     * @param game The game to execute the command on
     */
    public SelectCommand(Game game) {
        super(StringConstants.REGEX_SELECT, game);
    }

    @Override
    public void execute(String[] arguments) {
        Position pos = Position.fromString(arguments[0]);
        Game curGame = this.getGame();
        Board board = curGame.getBoard();

        curGame.setSelectedPosition(pos);
        System.out.print(BoardFormatter.formatBoard(board, pos));

        board.getUnitAt(pos).ifPresent(unitItem -> System.out.println(unitItem.formatSelectInfo()));
    }
}