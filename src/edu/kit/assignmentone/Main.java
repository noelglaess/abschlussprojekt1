package edu.kit.assignmentone;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.ui.CommandHandler;

/**
 * The main entry point for the application.
 *
 * @author Programmieren-Team
 */
public final class Main {

    private static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";
    private static final String ERROR_PREFIX = "Error, ";
    private static final String ERROR_ARGUMENT_FORMAT = "Invalid command line arguments.";
    private static final int REQUIRED_ARGS_COUNT = 4;

    private Main() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
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
        String deckPath = "";
        String verbosity = "";
        String unitsPath = "";

        try {
            for (String arg : args) {
                String[] parts = arg.split("=", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException();
                }
                switch (parts[0]) {
                    case "seed" -> seed = Long.parseLong(parts[1]);
                    case "deck" -> deckPath = parts[1];
                    case "verbosity" -> verbosity = parts[1];
                    case "units" -> unitsPath = parts[1];
                    default -> throw new IllegalArgumentException();
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
            return;
        }

        try {
            Game game = new Game(seed, deckPath, verbosity, unitsPath);
            CommandHandler handler = new CommandHandler(game);
            handler.handleUserInput();
        } catch (Exception e) {
            System.err.println(ERROR_PREFIX + e.getMessage());
        }
    }
}