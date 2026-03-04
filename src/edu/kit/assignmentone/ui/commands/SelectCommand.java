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
 * @author uqhkm
 * @version 1.0
 */
public class SelectCommand extends Command {

    public SelectCommand(Game game) {
        super(StringConstants.PATTERN_SELECT, game);
    }

    @Override
    public void execute(String[] arguments) {
        Position position = Position.fromString(arguments[0]);
        Game currentGame = this.getGame();
        Board board = currentGame.getBoard();

        currentGame.setSelectedPosition(position);
        System.out.print(BoardFormatter.formatBoard(board, position));

        Optional<PlacedUnit> unitOptional = board.getUnitAt(position);
        if (unitOptional.isPresent()) {
            System.out.println(unitOptional.get().formatInfo(currentGame));
        } else {
            System.out.println(StringConstants.NO_UNIT_SELECTED);
        }
    }
}