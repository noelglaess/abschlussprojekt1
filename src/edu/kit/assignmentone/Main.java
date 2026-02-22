package edu.kit.assignmentone;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.ui.CommandHandler;

import java.io.IOException;

/**
 * The main entry point for the application.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class Main {

    public static final String ERROR_ARGUMENT_FORMAT = "Invalid command line arguments.";
    private static final int REQUIRED_ARGS_COUNT = 4;

    private Main() {
    }

    /**
     * The main entry point for the program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != REQUIRED_ARGS_COUNT) {
            System.err.println(StringConstants.ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
            return;
        }

        long seed = 0;
        String deckPath = StringConstants.EMPTY;
        String unitsPath = StringConstants.EMPTY;

        try {
            for (String arg : args) {
                String[] parts = arg.split("=", 2);
                if (parts.length != 2) {
                    System.err.println(StringConstants.ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
                    return;
                }

                String key = parts[0];
                String value = parts[1];

                if (StringConstants.ARG_SEED.equals(key)) {
                    seed = Long.parseLong(value);
                } else if (StringConstants.ARG_DECK.equals(key)) {
                    deckPath = value;
                } else if (StringConstants.ARG_VERBOSITY.equals(key)) {
                    // Parsed but intentionally ignored
                } else if (StringConstants.ARG_UNITS.equals(key)) {
                    unitsPath = value;
                } else {
                    System.err.println(StringConstants.ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
                    return;
                }
            }
            Game game = new Game(seed, deckPath, unitsPath);
            CommandHandler handler = new CommandHandler(game);
            handler.handleUserInput();
        } catch (NumberFormatException | IllegalArgumentException | IllegalStateException | IOException e) {
            System.err.println(StringConstants.ERROR_PREFIX + e.getMessage());
        }
    }
}