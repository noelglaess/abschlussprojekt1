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
        super(StringConstants.CMD_HAND, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }

        Player active = this.getGame().getActivePlayerObject();
        List<Unit> hand = active.getHand();
        int size = hand.size();

        for (int i = 0; i < size; i++) {
            System.out.print(hand.get(i).formatHandInfo(i + 1));
        }
    }
}