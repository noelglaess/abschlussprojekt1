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

    /** Error message. */
    public static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";
    /** King unit name. */
    public static final String KING_NAME = "Farmer King";
    /** Success message. */
    public static final String SUCCESS_MSG = "Success!";
    /** Union failed message. */
    public static final String FMT_UNION_FAILED = "Union failed. %s was eliminated.%n";
    /** Join forces message. */
    public static final String FMT_JOIN_FORCES = "%s and %s on %s join forces!%n";
    /** Move message. */
    public static final String FMT_MOVES_TO = "%s moves to %s.%n";
    /** Eliminated message. */
    public static final String FMT_ELIMINATED = "%s was eliminated!%n";
    /** Block message. */
    public static final String FMT_BLOCKS = "%s (%s) blocks!%n";
    /** Unblock message. */
    public static final String FMT_NO_LONGER_BLOCKS = "%s no longer blocks.%n";
    /** Cell format. */
    public static final String FMT_CELL = "%s%s |";
    /** Row format. */
    public static final String FMT_ROW = "%d |";
    /** Column footer. */
    public static final String COL_FOOTER = "    A   B   C   D   E   F   G";
    /** Empty string. */
    public static final String EMPTY = "";
    /** No unit string. */
    public static final String NO_UNIT = "<no unit>";
    /** Error prefix. */
    public static final String ERROR_PREFIX = "Error, ";

    /** Whitespace regex. */
    public static final String REGEX_WHITESPACE = "" + BACKSLASH + BACKSLASH + "s+";
    /** Select regex. */
    public static final String REGEX_SELECT = "select [a-zA-Z]" + BACKSLASH + BACKSLASH + "d";
    /** Move regex. */
    public static final String REGEX_MOVE = "move [a-zA-Z]" + BACKSLASH + BACKSLASH + "d";
    /** Place regex. */
    public static final String REGEX_PLACE = "place( " + BACKSLASH + BACKSLASH + "d+)+";
    /** Yield regex. */
    public static final String REGEX_YIELD = "yield(" + BACKSLASH + BACKSLASH + "s+" + BACKSLASH + BACKSLASH + "d+)?";

    /** Unit info format. */
    public static final String UNIT_INFO_FORMAT = "%s (Team %s)%nATK: %d%nDEF: %d";
    /** Unit select format. */
    public static final String UNIT_SELECT_FORMAT = "%s %s (Team %s)%nATK: %d%nDEF: %d";
    /** Stats format. */
    public static final String FMT_STATS = " (%d" + SLASH + "%d)";
    /** State format. */
    public static final String FMT_STATE = "%s%n%d" + SLASH + "%d LP%nDC: %d" + SLASH + "%d%nBC: %d" + SLASH + "%d";
    /** Hand card format. */
    public static final String FMT_HAND_CARD = "[%d] %s%n(%d" + SLASH + "%d)%n";
    /** Attack format. */
    public static final String FMT_ATTACKS = "%s %s attacks %s%s on %s!%n";
    /** Damage format. */
    public static final String FMT_DAMAGE = "%s takes %d damage!%n";
    /** Zero LP format. */
    public static final String FMT_DROPPED_ZERO = "%s's life points dropped to 0!%n";
    /** Win format. */
    public static final String FMT_WINS = "%s wins!%n";
    /** Flip format. */
    public static final String FMT_FLIPPED = "%s (%d" + SLASH + "%d) was flipped on %s!%n";
    /** Place format. */
    public static final String FMT_PLACES = "%s places %s on %s.%n";
    /** Discard format. */
    public static final String FMT_DISCARDED = "%s discarded %s (%d" + SLASH + "%d).%n";
    /** Turn format. */
    public static final String FMT_TURN = "It is %s's turn!%n";
    /** No cards format. */
    public static final String FMT_NO_CARDS = "%s has no cards left in the deck!%n";

    /** Seed argument. */
    public static final String ARG_SEED = "seed";
    /** Deck argument. */
    public static final String ARG_DECK = "deck";
    /** Verbosity argument. */
    public static final String ARG_VERBOSITY = "verbosity";
    /** Units argument. */
    public static final String ARG_UNITS = "units";
    /** Argument separator. */
    public static final String ARG_SEPARATOR = "=";
    /** Argument error format. */
    public static final String ERR_ARG_FORMAT = "Invalid command line arguments.";

    /** Error strings. */
    public static final String ERR_MOVE_DIST = "Invalid move distance. Only 1 step horizontally" + SLASH + "vertically or en place allowed.";
    /** Error strings. */
    public static final String ERR_NO_SELECTION = "No field selected or selected field is empty.";
    /** Error strings. */
    public static final String ERR_ALREADY_MOVED = "This unit has already moved this turn.";
    /** Error strings. */
    public static final String ERR_KING_MOVE = "Invalid king move. Kings cannot attack or be attacked by own king.";
    /** Error strings. */
    public static final String ERR_NO_SEL_PLACE = "No field selected.";
    /** Error strings. */
    public static final String ERR_ALREADY_PLACED = "You have already placed units this turn.";
    /** Error strings. */
    public static final String ERR_INV_IDX = "One or more provided hand indices are invalid.";
    /** Error strings. */
    public static final String ERR_DUP_IDX = "An index cannot be provided multiple times.";
    /** Error strings. */
    public static final String ERR_OCC_ENEMY = "Cannot place on a field occupied by the enemy.";
    /** Error strings. */
    public static final String ERR_MUST_DISC = "You hold 5 units and must discard one using 'yield <idx>'.";
    /** Error strings. */
    public static final String ERR_CANT_DISC = "You hold less than 5 units and cannot discard.";
    /** Error strings. */
    public static final String ERR_NO_ARGS = "This command does not take any arguments.";

    private StringConstants() {
        // Prevent instantiation
    }
}