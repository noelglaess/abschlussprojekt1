package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;

/**
 * Command to end the current turn and optionally discard a card.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class YieldCommand extends Command {

    private static final int FULL_HAND_SIZE = 5;

    /**
     * Creates a new yield command.
     * @param game The game to execute the command on
     */
    public YieldCommand(Game game) {
        super(StringConstants.REGEX_YIELD, game);
    }

    @Override
    public void execute(String[] arguments) {
        Player active = this.getGame().getActivePlayerObject();
        boolean handIsFull = active.getHand().size() == FULL_HAND_SIZE;

        if (handIsFull && arguments.length == 0) {
            throw new IllegalStateException(StringConstants.ERR_MUST_DISC);
        }
        if (!handIsFull && arguments.length > 0) {
            throw new IllegalStateException(StringConstants.ERR_CANT_DISC);
        }

        if (arguments.length > 0) {
            int idx = Integer.parseInt(arguments[0]) - 1;
            if (idx < 0 || idx >= active.getHand().size()) {
                throw new IllegalArgumentException(StringConstants.ERR_INV_IDX);
            }
            Unit discarded = active.removeCardFromHand(idx);
            System.out.printf(StringConstants.FMT_DISCARDED, active.getType().getDisplayName(), discarded.name(), discarded.attack(), discarded.defense());
        }

        this.getGame().switchTurn();
    }
}