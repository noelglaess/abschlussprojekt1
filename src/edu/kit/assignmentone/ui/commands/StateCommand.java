package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;

/**
 * Command to show the current state (LP, DC, BC) of both players.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class StateCommand extends Command {

    private static final String STATE = "state";

    /**
     * Creates a new state command.
     * @param game The game to execute the command on
     */
    public StateCommand(Game game) {
        super(STATE, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }

        System.out.println(this.getGame().getHumanPlayer().formatState());
        System.out.println(this.getGame().getEnemyPlayer().formatState());
    }
}