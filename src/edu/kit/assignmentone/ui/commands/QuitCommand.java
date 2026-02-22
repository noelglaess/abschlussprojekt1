package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;

/**
 * Command to quit the game.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class QuitCommand extends Command {

    private static final String QUIT = "quit";

    /**
     * Creates a new quit command.
     * @param game The game to execute the command on
     */
    public QuitCommand(Game game) {
        super(QUIT, game);
    }

    @Override
    public void execute(String[] arguments) {
        this.getGame().quit();
    }
}