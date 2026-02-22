package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.Optional;

/**
 * Command to select a position on the board and show its details.
 *
 * @author Programmieren-Team
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
        this.getGame().setSelectedPosition(pos);

        System.out.print(BoardFormatter.formatBoard(this.getGame().getBoard(), pos));

        Optional<PlacedUnit> unitOpt = this.getGame().getBoard().getUnitAt(pos);
        unitOpt.ifPresent(placedUnit -> System.out.println(placedUnit.formatSelectInfo()));
    }
}