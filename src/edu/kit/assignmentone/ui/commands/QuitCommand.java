package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;

public class QuitCommand extends Command {

    private static final String COMMAND_NAME = "quit";

    public QuitCommand(Game game) {
        super(COMMAND_NAME, game);
    }

    @Override
    public void execute(String[] arguments) {
        this.getGame().quit();
    }
}