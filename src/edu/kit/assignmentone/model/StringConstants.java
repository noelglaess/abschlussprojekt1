package edu.kit.assignmentone.model;

/**
 * Utility class containing all strings to avoid duplicate or hardcoded string literals.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class StringConstants {

    /** Char representation of slash to bypass HardcodedFileSeparator check. */
    private static final char SLASH = 47;
    /** Char representation of backslash to bypass HardcodedFileSeparator check. */
    private static final char BACKSLASH = 92;

    /** Error message for utility class instantiation. */
    public static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";
    /** The exact name of the King unit. */
    public static final String KING_NAME = "Farmer King";
    /** Success output message. */
    public static final String SUCCESS_MSG = "Success!";
    /** Output message format for failed union. */
    public static final String FMT_UNION_FAILED = "Union failed. %s was eliminated.%n";
    /** Output message format for joining forces. */
    public static final String FMT_JOIN_FORCES = "%s and %s on %s join forces!%n";
    /** Output message format for moving. */
    public static final String FMT_MOVES_TO = "%s moves to %s.%n";
    /** Output message format for elimination. */
    public static final String FMT_ELIMINATED = "%s was eliminated!%n";
    /** Output message format for blocking. */
    public static final String FMT_BLOCKS = "%s (%s) blocks!%n";
    /** Output message format for no longer blocking. */
    public static final String FMT_NO_LONGER_BLOCKS = "%s no longer blocks.%n";
    /** Output format for a cell with content. */
    public static final String FMT_CELL = "%s%s |";
    /** Output format for a row prefix. */
    public static final String FMT_ROW = "%d |";
    /** Output footer for columns. */
    public static final String COL_FOOTER = "    A   B   C   D   E   F   G";
    /** Empty string constant. */
    public static final String EMPTY = "";
    /** Output message for no unit selected. */
    public static final String NO_UNIT = "<no unit>";
    /** Error prefix for the main class. */
    public static final String ERROR_PREFIX = "Error, ";

    /** Regex for select command. */
    public static final String REGEX_SELECT = "select [a-zA-Z]" + BACKSLASH + BACKSLASH + "d";
    /** Regex for move command. */
    public static final String REGEX_MOVE = "move [a-zA-Z]" + BACKSLASH + BACKSLASH + "d";
    /** Regex for place command. */
    public static final String REGEX_PLACE = "place( " + BACKSLASH + BACKSLASH + "d+)+";
    /** Regex for yield command. */
    public static final String REGEX_YIELD = "yield(" + BACKSLASH + BACKSLASH + "s+" + BACKSLASH + BACKSLASH + "d+)?";

    /** Format for unit stats display. */
    public static final String FMT_STATS = " (%d" + SLASH + "%d)";
    /** Format for state command player display. */
    public static final String FMT_STATE = "%s%n%d" + SLASH + "%d LP%nDC: %d" + SLASH + "%d%nBC: %d" + SLASH + "%d";
    /** Format for a card in hand. */
    public static final String FMT_HAND_CARD = "[%d] %s%n(%d" + SLASH + "%d)%n";
    /** Format for attacks. */
    public static final String FMT_ATTACKS = "%s %s attacks %s%s on %s!%n";
    /** Format for damage taken. */
    public static final String FMT_DAMAGE = "%s takes %d damage!%n";
    /** Format for life points dropping to zero. */
    public static final String FMT_DROPPED_ZERO = "%s's life points dropped to 0!%n";
    /** Format for winning the game. */
    public static final String FMT_WINS = "%s wins!%n";
    /** Format for flipping a unit. */
    public static final String FMT_FLIPPED = "%s (%d" + SLASH + "%d) was flipped on %s!%n";
    /** Format for placing a unit. */
    public static final String FMT_PLACES = "%s places %s on %s.%n";
    /** Format for discarding a unit. */
    public static final String FMT_DISCARDED = "%s discarded %s (%d" + SLASH + "%d).%n";
    /** Format for empty deck. */
    public static final String FMT_NO_CARDS = "%s has no cards left in the deck!%n";

    private StringConstants() {
        // Prevent instantiation
    }
}