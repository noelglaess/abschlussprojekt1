package edu.kit.assignmentone;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.ui.CommandHandler;

import java.io.IOException;

/**
 * The main entry point for the application.
 *
 * @author uqhkm
 * @version 1.0
 */
public final class Main {

    private static final int REQUIRED_ARGUMENTS_COUNT = 4;

    private Main() {
        // Prevent instantiation
    }

    /**
     * The main entry point for the program.
     *
     * @param arguments the command line arguments
     */
    public static void main(String[] arguments) {
        if (arguments.length != REQUIRED_ARGUMENTS_COUNT) {
            System.err.println(StringConstants.ERROR_PREFIX + StringConstants.ERROR_ARGUMENT_FORMAT);
            return;
        }

        long randomSeed = 0;
        String deckFilePath = StringConstants.EMPTY_STRING;
        String unitsFilePath = StringConstants.EMPTY_STRING;

        try {
            for (String argument : arguments) {
                String[] partsArray = argument.split(StringConstants.ARGUMENT_SEPARATOR, 2);
                if (partsArray.length != 2) {
                    System.err.println(StringConstants.ERROR_PREFIX + StringConstants.ERROR_ARGUMENT_FORMAT);
                    return;
                }

                String key = partsArray[0];
                String value = partsArray[1];

                switch (key) {
                    case StringConstants.ARGUMENT_SEED -> randomSeed = Long.parseLong(value);
                    case StringConstants.ARGUMENT_DECK -> deckFilePath = value;
                    case StringConstants.ARGUMENT_VERBOSITY -> {
                        // Parsed but intentionally ignored
                    }
                    case StringConstants.ARGUMENT_UNITS -> unitsFilePath = value;
                    case null, default -> {
                        System.err.println(StringConstants.ERROR_PREFIX + StringConstants.ERROR_ARGUMENT_FORMAT);
                        return;
                    }
                }
            }
            Game gameInstance = new Game(randomSeed, deckFilePath, unitsFilePath);
            CommandHandler handler = new CommandHandler(gameInstance);
            handler.handleUserInput();
        } catch (NumberFormatException exception) {
            System.err.println(StringConstants.ERROR_PREFIX + StringConstants.ERROR_ARGUMENT_FORMAT);
        } catch (IllegalArgumentException | IllegalStateException | IOException exception) {
            System.err.println(StringConstants.ERROR_PREFIX + exception.getMessage());
        }
    }
}