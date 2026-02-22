package edu.kit.assignmentone.ui;

import edu.kit.assignmentone.Main;
import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.AIEngine;
import edu.kit.assignmentone.model.player.PlayerType;
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
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class CommandHandler {
    private static final String ERROR_PREFIX = Main.ERROR_ARGUMENT_FORMAT;
    private static final String COMMAND_NOT_FOUND_ERROR = "Command not recognised.";
    private static final String USE_ONE_OF_THE_FOLLOWING_COMMANDS_SELECT_BOARD_MOVE_FLIP_BLOCK_HAND_PLACE_SHOW_YIELD_STATE_QUIT = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";
    private static final String REGEX = "\\s+";

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
    }

    /**
     * Starts the input loop.
     */
    public void handleUserInput() {
        System.out.println(USE_ONE_OF_THE_FOLLOWING_COMMANDS_SELECT_BOARD_MOVE_FLIP_BLOCK_HAND_PLACE_SHOW_YIELD_STATE_QUIT);
        try (Scanner scanner = new Scanner(System.in)) {
            while (this.game.isRunning()) {
                if (this.game.getActivePlayer() == PlayerType.ENEMY) {
                    AIEngine.playTurn(this.game);
                    continue;
                }

                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) {
                        continue;
                    }
                    executeCommand(input);
                } else {
                    break;
                }
            }
        }
    }

    private void executeCommand(String input) {
        for (Command command : this.commands) {
            if (input.matches(command.getCommandRegex())) {
                String[] arguments = input.split(REGEX);
                String[] argsOnly = new String[arguments.length - 1];
                System.arraycopy(arguments, 1, argsOnly, 0, arguments.length - 1);

                try {
                    command.execute(argsOnly);
                } catch (IllegalStateException | IllegalArgumentException e) {
                    System.err.println(ERROR_PREFIX + e.getMessage());
                }
                return;
            }
        }
        System.err.println(ERROR_PREFIX + COMMAND_NOT_FOUND_ERROR);
    }
}