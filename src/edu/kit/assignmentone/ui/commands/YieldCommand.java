package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;

/**
 * Command to end the current turn and optionally discard a card.
 *
 * @author uXXXXX
 * @version 1.0
 */
public class YieldCommand extends Command {

    /**
     * Creates a new yield command.
     * @param game The game to execute the command on
     */
    public YieldCommand(Game game) {
        super(StringConstants.REGEX_YIELD, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game game = this.getGame();
        Player active = game.getActivePlayerObject();
        Unit discarded = active.processYield(arguments);
        if (discarded != null) {
            System.out.print(discarded.formatDiscardInfo(active.getType()));
        }
        game.switchTurn();
    }
}