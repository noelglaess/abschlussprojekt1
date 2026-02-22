package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;

import java.util.Optional;

public class ShowCommand extends Command {

    private static final String COMMAND_NAME = "show";
    private static final String ERROR_NO_ARGS_EXPECTED = "The show command does not take any arguments.";
    private static final String UNIT_INFO_FORMAT = "%s (Team %s)%nATK: %d%nDEF: %d";

    public ShowCommand(Game game) {
        super(COMMAND_NAME, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(ERROR_NO_ARGS_EXPECTED);
        }

        Position selected = this.getGame().getSelectedPosition();
        if (selected == null) {
            System.out.println("<no unit>");
            return;
        }

        Optional<PlacedUnit> unitOpt = this.getGame().getBoard().getUnitAt(selected);
        if (unitOpt.isPresent()) {
            PlacedUnit pu = unitOpt.get();
            System.out.printf(UNIT_INFO_FORMAT,
                    pu.getUnit().name(),
                    pu.getOwner().getDisplayName(),
                    pu.getUnit().attack(),
                    pu.getUnit().defense());
            System.out.println();
        } else {
            System.out.println("<no unit>");
        }
    }
}