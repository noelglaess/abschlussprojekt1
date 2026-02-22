package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;

/**
 * Abstract base class for all commands.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public abstract class Command {

    private final Game game;
    private final String commandRegex;

    /**
     * Creates a new Command.
     *
     * @param commandRegex The regular expression to match the command
     * @param game The game model
     */
    protected Command(String commandRegex, Game game) {
        this.commandRegex = commandRegex;
        this.game = game;
    }

    /**
     * Returns the regex pattern for this command.
     *
     * @return The regex string
     */
    public String getCommandRegex() {
        return this.commandRegex;
    }

    /**
     * Executes the command.
     *
     * @param arguments The command arguments
     */
    public abstract void execute(String[] arguments);

    /** The game instance the command operates on. */
    public Game getGame() {
        return game;
    }
}