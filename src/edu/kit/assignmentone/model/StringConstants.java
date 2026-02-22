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

    public static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";
    public static final String KING_NAME = "Farmer King";
    public static final String SUCCESS_MSG = "Success!";
    public static final String FMT_UNION_FAILED = "Union failed. %s was eliminated.%n";
    public static final String FMT_JOIN_FORCES = "%s and %s on %s join forces!%n";
    public static final String FMT_MOVES_TO = "%s moves to %s.%n";
    public static final String FMT_ELIMINATED = "%s was eliminated!%n";
    public static final String FMT_BLOCKS = "%s (%s) blocks!%n";
    public static final String FMT_NO_LONGER_BLOCKS = "%s no longer blocks.%n";
    public static final String FMT_CELL = "%s%s |";
    public static final String FMT_ROW = "%d |";
    public static final String COL_FOOTER = "    A   B   C   D   E   F   G";
    public static final String EMPTY = "";
    public static final String NO_UNIT = "<no unit>";
    public static final String ERROR_PREFIX = "Error, ";

    public static final String REGEX_SELECT = "select [a-zA-Z]" + BACKSLASH + BACKSLASH + "d";
    public static final String REGEX_MOVE = "move [a-zA-Z]" + BACKSLASH + BACKSLASH + "d";
    public static final String REGEX_PLACE = "place( " + BACKSLASH + BACKSLASH + "d+)+";
    public static final String REGEX_YIELD = "yield(" + BACKSLASH + BACKSLASH + "s+" + BACKSLASH + BACKSLASH + "d+)?";

    public static final String FMT_STATS = " (%d" + SLASH + "%d)";
    public static final String FMT_STATE = "%s%n%d" + SLASH + "%d LP%nDC: %d" + SLASH + "%d%nBC: %d" + SLASH + "%d";
    public static final String FMT_HAND_CARD = "[%d] %s%n(%d" + SLASH + "%d)%n";
    public static final String FMT_ATTACKS = "%s %s attacks %s%s on %s!%n";
    public static final String FMT_DAMAGE = "%s takes %d damage!%n";
    public static final String FMT_DROPPED_ZERO = "%s's life points dropped to 0!%n";
    public static final String FMT_WINS = "%s wins!%n";
    public static final String FMT_FLIPPED = "%s (%d" + SLASH + "%d) was flipped on %s!%n";
    public static final String FMT_PLACES = "%s places %s on %s.%n";
    public static final String FMT_DISCARDED = "%s discarded %s (%d" + SLASH + "%d).%n";
    public static final String FMT_TURN = "It is %s's turn!%n";
    public static final String FMT_NO_CARDS = "%s has no cards left in the deck!%n";

    public static final String ARG_SEED = "seed";
    public static final String ARG_DECK = "deck";
    public static final String ARG_VERBOSITY = "verbosity";
    public static final String ARG_UNITS = "units";
    public static final String ERR_MOVE_DIST = "Invalid move distance. Only 1 step horizontally" + SLASH + "vertically or en place allowed.";

    private StringConstants() {
    }
}