package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;

import java.util.Optional;

/**
 * Command to show the current state of the game board.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class ShowCommand extends Command {

    private static final String SHOW = "show";

    /**
     * Creates a new show command.
     * @param game The game to execute the command on
     */
    public ShowCommand(Game game) {
        super(SHOW, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }

        Position selected = this.getGame().getSelectedPosition();
        if (selected == null) {
            System.out.println(StringConstants.NO_UNIT);
            return;
        }

        Optional<PlacedUnit> unitOpt = this.getGame().getBoard().getUnitAt(selected);
        if (unitOpt.isPresent()) {
            System.out.println(unitOpt.get().formatInfo());
        } else {
            System.out.println(StringConstants.NO_UNIT);
        }
    }
}