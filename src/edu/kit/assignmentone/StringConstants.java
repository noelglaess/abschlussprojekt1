package edu.kit.assignmentone;

/**
 * Utility class containing all strings to avoid duplicate or hardcoded string literals.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class StringConstants {

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
    public static final String ARG_SEPARATOR = "=";
    public static final String NO_UNIT = "<no unit>";
    public static final String FMT_STATS = " (%d/%d)";
    public static final String FMT_STATE = "%s%n%d/%d LP%nDC: %d/%d%nBC: %d/%d";
    public static final String FMT_ATTACKS = "%s %s attacks %s%s on %s!%n";
    public static final String FMT_DAMAGE = "%s takes %d damage!%n";
    public static final String FMT_DROPPED_ZERO = "%s's life points dropped to 0!%n";
    public static final String FMT_WINS = "%s wins!%n";
    public static final String FMT_FLIPPED = "%s (%d/%d) was flipped on %s!%n";
    public static final String FMT_PLACES = "%s places %s on %s.%n";
    public static final String FMT_DISCARDED = "%s discarded %s (%d/%d).%n";
    public static final String FMT_NO_CARDS = "%s has no cards left in the deck!%n";
    public static final String FMT_HAND_CARD = "[%d] %s%n(%d/%d)%n";

    private StringConstants() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
    }
}