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

    private static final String CMD_NOT_FOUND_ERROR = "Command not recognised.";

    private final Game game;
    private final List<Command> commands;

    /**
     * Creates a new CommandHandler.
     *
     * @param game The game model
     */
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
        this.commands.add(new BoardCommand(this.game));
        this.commands.add(new BlockCommand(this.game));
        this.commands.add(new FlipCommand(this.game));
    }

    /**
     * Starts the input loop.
     */
    public void handleUserInput() {
        System.out.println(StringConstants.MSG_HELP);
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
        boolean found = false;
        for (Command command : this.commands) {
            if (input.matches(command.getCommandRegex())) {
                found = true;
                String[] arguments = input.split(StringConstants.REGEX_WHITESPACE);
                String[] argsOnly = new String[arguments.length - 1];
                System.arraycopy(arguments, 1, argsOnly, 0, arguments.length - 1);

                try {
                    command.execute(argsOnly);
                } catch (IllegalStateException | IllegalArgumentException e) {
                    System.err.println(StringConstants.ERROR_PREFIX + e.getMessage());
                }
                break;
            }
        }
        if (!found) {
            System.err.println(StringConstants.ERROR_PREFIX + CMD_NOT_FOUND_ERROR);
        }
    }
}