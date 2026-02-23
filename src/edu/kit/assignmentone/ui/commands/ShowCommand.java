package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;

import java.util.Optional;

/**
 * Command to show the current state of the game board.
 *
 * @author uXXXXX
 * @version 1.0
 */
public class ShowCommand extends Command {

    /**
     * Creates a new show command.
     * @param game The game to execute the command on
     */
    public ShowCommand(Game game) {
        super(StringConstants.CMD_SHOW, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }

        Game game = this.getGame();
        Position selected = game.getSelectedPosition();

        if (selected == null) {
            System.out.println(StringConstants.NO_UNIT);
            return;
        }

        Optional<PlacedUnit> unitOpt = game.getBoard().getUnitAt(selected);
        if (unitOpt.isPresent()) {
            System.out.println(unitOpt.get().formatInfo());
        } else {
            System.out.println(StringConstants.NO_UNIT);
        }
    }
}