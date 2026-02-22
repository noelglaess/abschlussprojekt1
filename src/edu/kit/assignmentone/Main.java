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

    private static final int REQUIRED_ARGS_COUNT = 4;

    private Main() {
        // Prevent instantiation
    }

    /**
     * The main entry point for the program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != REQUIRED_ARGS_COUNT) {
            System.err.println(StringConstants.ERROR_PREFIX + StringConstants.ERR_ARG_FORMAT);
            return;
        }

        long seed = 0;
        String deckPath = StringConstants.EMPTY;
        String unitsPath = StringConstants.EMPTY;

        try {
            for (String arg : args) {
                String[] parts = arg.split(StringConstants.ARG_SEPARATOR, 2);
                if (parts.length != 2) {
                    System.err.println(StringConstants.ERROR_PREFIX + StringConstants.ERR_ARG_FORMAT);
                    return;
                }

                String key = parts[0];
                String value = parts[1];

                switch (key) {
                    case StringConstants.ARG_SEED -> seed = Long.parseLong(value);
                    case StringConstants.ARG_DECK -> deckPath = value;
                    case StringConstants.ARG_VERBOSITY -> {
                        // Parsed but intentionally ignored
                    }
                    case StringConstants.ARG_UNITS -> unitsPath = value;
                    case null, default -> {
                        System.err.println(StringConstants.ERROR_PREFIX + StringConstants.ERR_ARG_FORMAT);
                        return;
                    }
                }
            }
            Game game = new Game(seed, deckPath, unitsPath);
            CommandHandler handler = new CommandHandler(game);
            handler.handleUserInput();
        } catch (NumberFormatException | IOException e) {
            // Wir fangen hier exakt nur die beiden Exceptions,
            // die in der Setup-Phase explizit fliegen können.
            System.err.println(StringConstants.ERROR_PREFIX + e.getMessage());
        }
    }
}