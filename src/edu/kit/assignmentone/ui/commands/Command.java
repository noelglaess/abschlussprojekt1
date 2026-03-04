package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;

/**
 * Abstract base class for all commands.
 *
 * @author uqhkm
 * @version 1.0
 */
public abstract class Command {

    private final Game game;
    private final String commandRegularExpression;

    /**
     * Creates a new Command.
     *
     * @param commandRegularExpression The regular expression to match the command
     * @param game The game model
     */
    protected Command(String commandRegularExpression, Game game) {
        this.commandRegularExpression = commandRegularExpression;
        this.game = game;
    }

    /**
     * Returns the regex pattern for this command.
     *
     * @return The regex string
     */
    public String getCommandRegularExpression() {
        return this.commandRegularExpression;
    }

    /**
     * Returns the game instance the command operates on.
     *
     * @return The game model
     */
    protected Game getGame() {
        return this.game;
    }

    /**
     * Executes the command.
     *
     * @param arguments The command arguments
     */
    public abstract void execute(String[] arguments);
}