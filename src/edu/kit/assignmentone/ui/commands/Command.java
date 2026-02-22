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
    private final String commandRegex;

    /**
     * Creates a new Command.
     *
     * @param commandName The name of the command (unused but kept for constructor consistency)
     * @param commandRegex The regular expression to match the command and its arguments
     * @param game The game model
     */
    protected Command(String commandName, String commandRegex, Game game) {
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
     */
    public abstract void execute(String[] arguments);
}