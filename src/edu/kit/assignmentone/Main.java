package edu.kit.assignmentone;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.ui.CommandHandler;

import java.io.IOException;

/**
 * The main entry point for the application.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class Main {

    public static final String ERROR_PREFIX = "Error, ";
    private static final String ERROR_ARGUMENT_FORMAT = "Invalid command line arguments.";
    private static final int REQUIRED_ARGS_COUNT = 4;
    private static final String SEED = "seed";
    private static final String DECK = "deck";
    private static final String VERBOSITY = "verbosity";
    private static final String UNITS = "units";

    private Main() {
        throw new UnsupportedOperationException(StringConstants.UTILITY_CLASS_ERROR);
    }

    /**
     * The main entry point for the program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != REQUIRED_ARGS_COUNT) {
            System.err.println(ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
            return;
        }

        long seed = 0;
        String deckPath = StringConstants.EMPTY;
        String unitsPath = StringConstants.EMPTY;

        try {
            for (String arg : args) {
                String[] parts = arg.split(StringConstants.ARG_SEPARATOR, 2);
                if (parts.length != 2) {
                    System.err.println(ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
                    return;
                }

                String key = parts[0];
                String value = parts[1];

                switch (key) {
                    case SEED -> seed = Long.parseLong(value);
                    case DECK -> deckPath = value;
                    case VERBOSITY -> {
                        // Parsed but intentionally ignored to fix unused assignment warning
                    }
                    case UNITS -> unitsPath = value;
                    case null, default -> {
                        System.err.println(ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
                        return;
                    }
                }
            }
            Game game = new Game(seed, deckPath, unitsPath);
            CommandHandler handler = new CommandHandler(game);
            handler.handleUserInput();
        } catch (IllegalStateException |
                 IllegalArgumentException | IOException e) {
            System.err.println(ERROR_PREFIX + e.getMessage());
        }
    }
}