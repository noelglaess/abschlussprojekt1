package edu.kit.assignmentone.model;

/**
 * Utility class containing all strings to avoid duplicate or hardcoded string literals.
 *
 * @author uqhkm
 * @version 1.0
 */
public final class StringConstants {

    private static final char SLASH = 47;

    public static final String KING_NAME = "Farmer King";
    public static final String SUCCESS_MSG = "Success!";
    public static final String FMT_UNION_FAILED = "Union failed. %s was eliminated.%n";
    public static final String FMT_JOIN_FORCES = "%s and %s on %s join forces!%n";
    public static final String FMT_MOVES_TO = "%s moves to %s.%n";
    public static final String FMT_ELIMINATED = "%s was eliminated!%n";
    public static final String FMT_BLOCKS = "%s (%s) blocks!%n";
    public static final String FMT_NO_LONGER_BLOCKS = "%s no longer blocks.%n";

    public static final String FMT_CELL = "%s%-2s|";

    public static final String FMT_ROW = "%d |";
    public static final String COL_FOOTER = "    A   B   C   D   E   F   G";
    public static final String EMPTY = "";
    public static final String NO_UNIT = "<no unit>";
    public static final String ERROR_PREFIX = "Error, ";

    public static final String REGEX_WHITESPACE = " +";
    public static final String REGEX_SELECT = "select [a-zA-Z][0-9]";
    public static final String REGEX_MOVE = "move [a-zA-Z][0-9]";
    public static final String REGEX_PLACE = "place( [0-9]+)+";
    public static final String REGEX_YIELD = "yield( +[0-9]+)?";

    public static final String UNIT_INFO_FORMAT = "%s (Team %s)%nATK: %d%nDEF: %d";
    public static final String FMT_STATS = " (%d" + SLASH + "%d)";
    public static final String FMT_STATE = "%s%n%d" + SLASH + "%d LP%nDC: %d" + SLASH + "%d%nBC: %d" + SLASH + "%d";
    public static final String FMT_HAND_CARD = "[%d] %s (%d" + SLASH + "%d)%n";
    public static final String FMT_ATTACKS = "%s %s attacks %s%s on %s!%n";
    public static final String FMT_DAMAGE = "%s takes %d damage!%n";
    public static final String FMT_DROPPED_ZERO = "%s's life points dropped to 0!%n";
    public static final String FMT_WINS = "%s wins!%n";
    public static final String FMT_FLIPPED = "%s (%d" + SLASH + "%d) was flipped on %s!%n";

    public static final String FMT_PLACES = "%s places %s on %s.%n";
    public static final String FMT_DISCARDED = "%s discarded %s (%d" + SLASH + "%d).%n";
    public static final String FMT_TURN = "It is %s's turn!%n";
    public static final String FMT_NO_CARDS = "%s has no cards left in the deck!%n";

    public static final String FMT_KING_NAME = "%s's %s";
    public static final String FMT_HIDDEN_UNIT = "??? (Team %s)%nATK: ???%nDEF: ???";

    public static final String CMD_QUIT = "quit";
    public static final String CMD_SHOW = "show";
    public static final String CMD_STATE = "state";
    public static final String CMD_HAND = "hand";
    public static final String CMD_BOARD = "board";

    public static final String MSG_HELP = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";

    public static final String ARG_SEED = "seed";
    public static final String ARG_DECK = "deck";
    public static final String ARG_VERBOSITY = "verbosity";
    public static final String ARG_UNITS = "units";
    public static final String ARG_SEPARATOR = "=";
    public static final String ERR_ARG_FORMAT = "Invalid command line arguments.";

    public static final String ERR_MOVE_DIST = "Invalid move distance. Only 1 step horizontally" + SLASH + "vertically or en place allowed.";
    public static final String ERR_NO_SELECTION = "No field selected or selected field is empty.";
    public static final String ERR_ALREADY_MOVED = "This unit has already moved this turn.";
    public static final String ERR_KING_MOVE = "Invalid king move. Kings cannot attack or be attacked by own king.";
    public static final String ERR_NO_SEL_PLACE = "No field selected.";
    public static final String ERR_ALREADY_PLACED = "You have already placed units this turn.";
    public static final String ERR_INV_IDX = "One or more provided hand indices are invalid.";
    public static final String ERR_DUP_IDX = "An index cannot be provided multiple times.";
    public static final String ERR_OCC_ENEMY = "Cannot place on a field occupied by the enemy.";
    public static final String ERR_MUST_DISC = "You hold 5 units and must discard one using 'yield <idx>'.";
    public static final String ERR_CANT_DISC = "You hold less than 5 units and cannot discard.";
    public static final String ERR_NO_ARGS = "This command does not take any arguments.";

    private StringConstants() {
    }
}