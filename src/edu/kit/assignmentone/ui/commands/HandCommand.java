package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;

import java.util.List;

/**
 * Command to show the current hand of the active player.
 *
 * @author uqhkm
 * @version 1.0
 */
public class HandCommand extends Command {

    /**
     * Creates a new hand command.
     * @param game The game to execute the command on
     */
    public HandCommand(Game game) {
        super(StringConstants.COMMAND_HAND, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERROR_NO_ARGUMENTS);
        }

        Player activePlayer = this.getGame().getActivePlayerObject();
        List<Unit> handUnits = activePlayer.getHand();
        int handSize = handUnits.size();

        for (int index = 0; index < handSize; index++) {
            System.out.print(handUnits.get(index).formatHandInfo(index + 1));
        }
    }
}