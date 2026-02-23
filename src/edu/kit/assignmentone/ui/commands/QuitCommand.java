package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;

/**
 * Command to quit the game.
 *
 * @author uXXXXX
 * @version 1.0
 */
public class QuitCommand extends Command {

    /**
     * Creates a new quit command.
     * @param game The game to execute the command on
     */
    public QuitCommand(Game game) {
        super(StringConstants.CMD_QUIT, game);
    }

    @Override
    public void execute(String[] arguments) {
        this.getGame().quit();
    }
}