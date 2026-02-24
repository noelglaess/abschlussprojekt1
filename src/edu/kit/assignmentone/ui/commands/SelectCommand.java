package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.Optional;

/**
 * Command to select a position on the board and show its details.
 *
 * @author uXXXXX
 * @version 1.0
 */
public class SelectCommand extends Command {

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

        Optional<PlacedUnit> unitOpt = board.getUnitAt(pos);
        if (unitOpt.isPresent()) {
            System.out.println(unitOpt.get().formatInfo(curGame));
        } else {
            System.out.println(StringConstants.NO_UNIT);
        }
    }
}