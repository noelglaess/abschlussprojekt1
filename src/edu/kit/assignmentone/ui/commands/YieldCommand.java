package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;

/**
 * Command to end the current turn and optionally discard a card.
 *
 * @author uqhkm
 * @version 1.0
 */
public class YieldCommand extends Command {

    /**
     * Creates a new yield command.
     * @param game The game to execute the command on
     */
    public YieldCommand(Game game) {
        super(StringConstants.PATTERN_YIELD, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game currentGame = this.getGame();
        Player activePlayer = currentGame.getActivePlayerObject();
        Unit discardedUnit = activePlayer.processYield(arguments);
        if (discardedUnit != null) {
            System.out.print(discardedUnit.formatDiscardInformation(activePlayer.getType()));
        }
        currentGame.switchTurn();
    }
}