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
 * @author Programmieren-Team
 */
public final class ResourceLoader {

    private static final String INVALID_UNIT_FORMAT_ERROR = "Invalid unit format in file.";
    private static final String INVALID_DECK_FORMAT_ERROR = "Invalid deck format in file.";
    private static final String UNIT_DELIMITER = ";";
    private static final int UNIT_PARTS_COUNT = 4;

    private ResourceLoader() {
        throw new UnsupportedOperationException("Utility classes cannot be instantiated");
    }

    public static List<Unit> loadUnits(String filePath) throws IOException {
        List<Unit> units = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(UNIT_DELIMITER);
                if (parts.length != UNIT_PARTS_COUNT) throw new IOException(INVALID_UNIT_FORMAT_ERROR);

                String name = parts[0].trim();
                UnitType type = UnitType.fromString(parts[1].trim());
                int attack = Integer.parseInt(parts[2].trim());
                int defense = Integer.parseInt(parts[3].trim());
                units.add(new Unit(name, type, attack, defense));
            }
        } catch (NumberFormatException | IllegalArgumentException e) {
            throw new IOException(INVALID_UNIT_FORMAT_ERROR);
        }
        return units;
    }

    public static List<Integer> loadDeck(String filePath) throws IOException {
        List<Integer> deck = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (line.trim().isEmpty()) continue;
                deck.add(Integer.parseInt(line.trim()));
            }
        } catch (NumberFormatException e) {
            throw new IOException(INVALID_DECK_FORMAT_ERROR);
        }
        return deck;
    }
}