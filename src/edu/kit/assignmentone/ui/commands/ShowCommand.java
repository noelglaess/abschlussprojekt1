package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;

import java.util.Optional;

/**
 * Command to show the current state of the game board.
 *
 * @author uqhkm
 * @version 1.0
 */
public class ShowCommand extends Command {

    public ShowCommand(Game game) {
        super(StringConstants.COMMAND_SHOW, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERROR_NO_ARGUMENTS);
        }

        Game currentGame = this.getGame();
        Position selectedPosition = currentGame.getSelectedPosition();

        if (selectedPosition == null) {
            System.out.println(StringConstants.NO_UNIT_SELECTED);
        } else {
            Optional<PlacedUnit> unitOptional = currentGame.getBoard().getUnitAt(selectedPosition);
            if (unitOptional.isPresent()) {
                System.out.println(unitOptional.get().formatInformation(currentGame));
            } else {
                System.out.println(StringConstants.NO_UNIT_SELECTED);
            }
        }
    }
}