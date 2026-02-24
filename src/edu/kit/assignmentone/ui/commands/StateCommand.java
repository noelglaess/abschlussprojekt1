package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;

/**
 * Command to show the current state (LP, DC, BC) of both players.
 *
 * @author uqhkm
 * @version 1.0
 */
public class StateCommand extends Command {

    /**
     * Creates a new state command.
     * @param game The game to execute the command on
     */
    public StateCommand(Game game) {
        super(StringConstants.CMD_STATE, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }

        Game game = this.getGame();
        System.out.println(game.getHumanPlayer().formatState());
        System.out.println(game.getEnemyPlayer().formatState());
    }
}