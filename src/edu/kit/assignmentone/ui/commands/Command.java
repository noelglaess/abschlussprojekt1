package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;

/**
 * Abstract base class for all commands.
 */
public abstract class Command {

    private final Game game;
    private final String commandRegex;

    protected Command(String commandRegex, Game game) {
        this.commandRegex = commandRegex;
        this.game = game;
    }

    public String getCommandRegex() {
        return commandRegex;
    }

    protected Game getGame() {
        return this.game;
    }

    public abstract void execute(String[] arguments);
}