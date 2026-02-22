package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;

import java.util.List;

/**
 * Command to show the current hand of the active player.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class HandCommand extends Command {

    private static final String HAND = "hand";

    /**
     * Creates a new hand command.
     * @param game The game to execute the command on
     */
    public HandCommand(Game game) {
        super(HAND, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }

        Player active = this.getGame().getActivePlayerObject();
        List<Unit> hand = active.getHand();

        for (int i = 0; i < hand.size(); i++) {
            Unit unit = hand.get(i);
            System.out.printf(StringConstants.FMT_HAND_CARD, i + 1, unit.name(), unit.attack(), unit.defense());
        }
    }
}