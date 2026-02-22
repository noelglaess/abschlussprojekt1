package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.Player;

/**
 * Command to show the current state (LP, DC, BC) of both players.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class StateCommand extends Command {

    private static final String COMMAND_NAME = "state";

    /**
     * Creates a new state command.
     *
     * @param game The game to execute the command on
     */
    public StateCommand(Game game) {
        super(COMMAND_NAME, COMMAND_NAME, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException("The state command does not take any arguments.");
        }

        printPlayerState(this.getGame().getHumanPlayer());
        printPlayerState(this.getGame().getEnemyPlayer());
    }

    private void printPlayerState(Player player) {
        String output = String.format(StringConstants.FMT_STATE,
                player.getType().getDisplayName(),
                player.getLifePoints(), player.getMaxLifePoints(),
                player.getDeck().size(), player.getMaxDeckCapacity(),
                player.getBoardCount(), player.getMaxBoardCapacity()
        );
        System.out.println(output);
    }
}