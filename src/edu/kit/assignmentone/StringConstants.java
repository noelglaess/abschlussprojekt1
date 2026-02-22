package edu.kit.assignmentone;

/**
 * Utility class containing all strings to avoid duplicate or hardcoded string literals.
 *
 * @author Programmieren-Team
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
    public static final String FMT_CELL = "%s%s |";
    public static final String FMT_ROW = "%d |";
    public static final String COL_FOOTER = "    A   B   C   D   E   F   G";
    public static final String REGEX_WHITESPACE = "\\s+";
    public static final String EMPTY = "";

    private StringConstants() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
    }
}