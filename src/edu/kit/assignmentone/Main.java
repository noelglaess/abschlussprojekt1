package edu.kit.assignmentone;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.ui.CommandHandler;

import java.io.IOException;

/**
 * The main entry point for the application.
 *
 * @author Programmieren-Team
 */
public final class Main {

    private static final String ERROR_PREFIX = "Error, ";
    private static final String ERROR_ARGUMENT_FORMAT = "Invalid command line arguments.";
    private static final int REQUIRED_ARGS_COUNT = 4;

    private Main() {
        throw new UnsupportedOperationException("Utility classes cannot be instantiated");
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
        String unitsPath = "";

        try {
            for (String arg : args) {
                String[] parts = arg.split("=", 2);
                if (parts.length != 2) {
                    System.err.println(ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
                    return;
                }
                switch (parts[0]) {
                    case "seed" -> seed = Long.parseLong(parts[1]);
                    case "deck" -> deckPath = parts[1];
                    case "verbosity" -> { /* Parsed but internally ignored as per inspection */ }
                    case "units" -> unitsPath = parts[1];
                    default -> {
                        System.err.println(ERROR_PREFIX + ERROR_ARGUMENT_FORMAT);
                        return;
                    }
                }
            }
            Game game = new Game(seed, deckPath, unitsPath);
            CommandHandler handler = new CommandHandler(game);
            handler.handleUserInput();
        } catch (NumberFormatException | IllegalStateException | IllegalArgumentException | IOException e) {
            System.err.println(ERROR_PREFIX + e.getMessage());
        }
    }
}