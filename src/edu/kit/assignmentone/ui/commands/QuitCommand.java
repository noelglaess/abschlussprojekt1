package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;

/**
 * Command to quit the game.
 *
 * @author Programmieren-Team
 */
public class QuitCommand extends Command {

    private static final String COMMAND_NAME = "quit";

    /**
     * Creates a new quit command.
     *
     * @param game The game to execute the command on
     */
    public QuitCommand(Game game) {
        super(COMMAND_NAME, COMMAND_NAME, game);
    }

    @Override
    public void execute(String[] arguments) {
        this.game.quit();
    }
}