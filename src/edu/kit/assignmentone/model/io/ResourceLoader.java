package edu.kit.assignmentone.model.io;

import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.units.UnitType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to load game resources from files.
 *
 * @author uqhkm
 * @version 1.0
 */
public final class ResourceLoader {

    private static final String INVALID_UNIT_FORMAT_ERROR = "Invalid unit format in file.";
    private static final String INVALID_DECK_FORMAT_ERROR = "Invalid deck format in file.";
    private static final String UNIT_DELIMITER = ";";
    private static final int UNIT_PARTS_COUNT = 4;

    private ResourceLoader() { }

    /**
     * Loads the units from the specified file.
     *
     * @param filePath The path to the units file
     * @return A list of loaded units
     * @throws IOException If reading the file fails or the format is invalid
     */
    public static List<Unit> loadUnits(String filePath) throws IOException {
        List<Unit> unitsList = new ArrayList<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(filePath))) {
            while (true) {
                String lineString = bufferedReader.readLine();
                if (lineString == null) {
                    break;
                }

                if (lineString.trim().isEmpty()) {
                    continue;
                }
                String[] partsArray = lineString.split(UNIT_DELIMITER);
                if (partsArray.length != UNIT_PARTS_COUNT) {
                    throw new IOException(INVALID_UNIT_FORMAT_ERROR);
                }

                String qualifier = partsArray[0].trim();
                String roleString = partsArray[1].trim();
                UnitType type = UnitType.fromString(roleString);
                int attack = Integer.parseInt(partsArray[2].trim());
                int defense = Integer.parseInt(partsArray[3].trim());
                unitsList.add(new Unit(qualifier, roleString, type, attack, defense));
            }
        } catch (NumberFormatException exception) {
            throw new RuntimeException(exception);
        } catch (IllegalArgumentException exception) {
            throw new IOException(INVALID_UNIT_FORMAT_ERROR, exception);
        }
        return unitsList;
    }

    /**
     * Loads the deck configuration from the specified file.
     *
     * @param filePath The path to the deck file
     * @return A list of integers representing the deck configuration
     * @throws IOException If reading the file fails or the format is invalid
     */
    public static List<Integer> loadDeck(String filePath) throws IOException {
        List<Integer> deckList = new ArrayList<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(filePath))) {
            while (true) {
                String lineString = bufferedReader.readLine();
                if (lineString == null) {
                    break;
                }

                if (lineString.trim().isEmpty()) {
                    continue;
                }
                deckList.add(Integer.parseInt(lineString.trim()));
            }
        } catch (NumberFormatException exception) {
            throw new RuntimeException(exception);
        } catch (IllegalArgumentException exception) {
            throw new IOException(INVALID_DECK_FORMAT_ERROR, exception);
        }
        return deckList;
    }
}