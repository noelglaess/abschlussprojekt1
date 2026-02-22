package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;

/**
 * Abstract base class for all commands.
 *
 * @author Programmieren-Team
 */
public abstract class Command {

    /** The game instance the command operates on. */
    protected final Game game;
    private final String commandName;
    private final String commandRegex;

    /**
     * Creates a new Command.
     *
     * @param commandName The name of the command
     * @param commandRegex The regular expression to match the command and its arguments
     * @param game The game model
     */
    protected Command(String commandName, String commandRegex, Game game) {
        this.commandName = commandName;
        this.commandRegex = commandRegex;
        this.game = game;
    }

    /**
     * Returns the regex pattern for this command.
     *
     * @return The regex string
     */
    public String getCommandRegex() {
        return commandRegex;
    }

    /**
     * Executes the command.
     *
     * @param arguments The command arguments
     * @throws Exception If an error occurs during execution
     */
    public abstract void execute(String[] arguments) throws Exception;
}