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

    private static final String COMMAND_NAME = "show";
    private static final String ERROR_NO_ARGS_EXPECTED = "The show command does not take any arguments.";
    private static final String UNIT_INFO_FORMAT = "%s (Team %s)%nATK: %d%nDEF: %d";

    /**
     * Creates a new show command.
     * @param game The game to execute the command on
     */
    public ShowCommand(Game game) {
        super(COMMAND_NAME, COMMAND_NAME, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(ERROR_NO_ARGS_EXPECTED);
        }

        Position selected = this.getGame().getSelectedPosition();
        if (selected == null) {
            System.out.println(StringConstants.NO_UNIT);
            return;
        }

        Optional<PlacedUnit> unitOpt = this.getGame().getBoard().getUnitAt(selected);
        if (unitOpt.isPresent()) {
            PlacedUnit unit = unitOpt.get();
            System.out.printf(UNIT_INFO_FORMAT,
                    unit.getName(),
                    unit.getOwner().getDisplayName(),
                    unit.getAttack(),
                    unit.getDefense());
            System.out.println();
        } else {
            System.out.println(StringConstants.NO_UNIT);
        }
    }
}