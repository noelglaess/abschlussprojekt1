package edu.kit.assignmentone.ui;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.ui.commands.Command;
import edu.kit.assignmentone.ui.commands.HandCommand;
import edu.kit.assignmentone.ui.commands.MoveCommand;
import edu.kit.assignmentone.ui.commands.PlaceCommand;
import edu.kit.assignmentone.ui.commands.QuitCommand;
import edu.kit.assignmentone.ui.commands.SelectCommand;
import edu.kit.assignmentone.ui.commands.ShowCommand;
import edu.kit.assignmentone.ui.commands.StateCommand;
import edu.kit.assignmentone.ui.commands.YieldCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the user input and executes the corresponding commands.
 */
public class CommandHandler {
    private static final String ERROR_PREFIX = "Error, ";
    private static final String COMMAND_NOT_FOUND_ERROR = "Command not recognised.";

    private final Game game;
    private final List<Command> commands;

    public CommandHandler(Game game) {
        this.game = game;
        this.commands = new ArrayList<>();
        initCommands();
    }

    private void initCommands() {
        this.commands.add(new QuitCommand(this.game));
        this.commands.add(new StateCommand(this.game));
        this.commands.add(new ShowCommand(this.game));
        this.commands.add(new SelectCommand(this.game));
        this.commands.add(new PlaceCommand(this.game));
        this.commands.add(new MoveCommand(this.game));
        this.commands.add(new HandCommand(this.game));
        this.commands.add(new YieldCommand(this.game));
    }

    public void handleUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (this.game.isRunning() && scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                executeCommand(input);
            }
        }
    }

    private void executeCommand(String input) {
        for (Command command : this.commands) {
            if (input.matches(command.getCommandRegex())) {
                String[] arguments = input.split("\\s+");
                String[] argsOnly = new String[arguments.length - 1];
                System.arraycopy(arguments, 1, argsOnly, 0, arguments.length - 1);

                try {
                    command.execute(argsOnly);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.err.println(ERROR_PREFIX + e.getMessage());
                }
                return;
            }
        }
        System.err.println(ERROR_PREFIX + COMMAND_NOT_FOUND_ERROR);
    }
}