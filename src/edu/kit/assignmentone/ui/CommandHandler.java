package edu.kit.assignmentone.ui;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.AIEngine;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.ui.commands.Command;
import edu.kit.assignmentone.ui.commands.HandCommand;
import edu.kit.assignmentone.ui.commands.MoveCommand;
import edu.kit.assignmentone.ui.commands.PlaceCommand;
import edu.kit.assignmentone.ui.commands.QuitCommand;
import edu.kit.assignmentone.ui.commands.SelectCommand;
import edu.kit.assignmentone.ui.commands.ShowCommand;
import edu.kit.assignmentone.ui.commands.StateCommand;
import edu.kit.assignmentone.ui.commands.YieldCommand;
import edu.kit.assignmentone.ui.commands.BoardCommand;
import edu.kit.assignmentone.ui.commands.BlockCommand;
import edu.kit.assignmentone.ui.commands.FlipCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the user input and executes the corresponding commands.
 *
 * @author uqhkm
 * @version 1.0
 */
public class CommandHandler {

    private static final String COMMAND_NOT_FOUND_ERROR = "Command not recognised.";

    private final Game game;
    private final List<Command> commandsList;

    /**
     * Creates a new CommandHandler.
     *
     * @param game The game model
     */
    public CommandHandler(Game game) {
        this.game = game;
        this.commandsList = new ArrayList<>();
        initializeCommands();
    }

    private void initializeCommands() {
        this.commandsList.add(new QuitCommand(this.game));
        this.commandsList.add(new StateCommand(this.game));
        this.commandsList.add(new ShowCommand(this.game));
        this.commandsList.add(new SelectCommand(this.game));
        this.commandsList.add(new PlaceCommand(this.game));
        this.commandsList.add(new MoveCommand(this.game));
        this.commandsList.add(new HandCommand(this.game));
        this.commandsList.add(new YieldCommand(this.game));
        this.commandsList.add(new BoardCommand(this.game));
        this.commandsList.add(new BlockCommand(this.game));
        this.commandsList.add(new FlipCommand(this.game));
    }

    /**
     * Starts the input loop.
     */
    public void handleUserInput() {
        System.out.println(StringConstants.MESSAGE_HELP);
        try (Scanner scanner = new Scanner(System.in)) {
            while (this.game.isRunning()) {
                if (this.game.isEnemyTurn()) {
                    AIEngine.playTurn(this.game);
                } else if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    if (!input.isEmpty()) {
                        executeCommand(input);
                    }
                } else {
                    this.game.quit();
                }
            }
        }
    }

    private void executeCommand(String input) {
        boolean commandFound = false;
        for (Command command : this.commandsList) {
            if (input.matches(command.getCommandRegularExpression())) {
                commandFound = true;
                String[] arguments = input.split(StringConstants.PATTERN_WHITESPACE);
                String[] argumentsOnly = new String[arguments.length - 1];
                System.arraycopy(arguments, 1, argumentsOnly, 0, arguments.length - 1);

                try {
                    command.execute(argumentsOnly);
                } catch (IllegalStateException | IllegalArgumentException exception) {
                    System.err.println(StringConstants.ERROR_PREFIX + exception.getMessage());
                }
                break;
            }
        }
        if (!commandFound) {
            System.err.println(StringConstants.ERROR_PREFIX + COMMAND_NOT_FOUND_ERROR);
        }
    }
}