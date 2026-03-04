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
    public static final String SUCCESS_MESSAGE = "Success!";
    public static final String FORMAT_UNION_FAILED = "Union failed. %s was eliminated.%n";
    public static final String FORMAT_JOIN_FORCES = "%s and %s on %s join forces!%n";
    public static final String FORMAT_MOVES_TO = "%s moves to %s.%n";
    public static final String FORMAT_ELIMINATED = "%s was eliminated!%n";
    public static final String FORMAT_BLOCKS = "%s (%s) blocks!%n";
    public static final String FORMAT_NO_LONGER_BLOCKS = "%s no longer blocks.%n";

    public static final String FORMAT_CELL = "%s%-2s|";
    public static final String FORMAT_ROW = "%d |";
    public static final String COLUMN_FOOTER = "    A   B   C   D   E   F   G";
    public static final String EMPTY_STRING = "";
    public static final String NO_UNIT_SELECTED = "<no unit>";
    public static final String ERROR_PREFIX = "Error, ";

    public static final String PATTERN_WHITESPACE = " +";
    public static final String PATTERN_SELECT = "select [a-zA-Z][0-9]";
    public static final String PATTERN_MOVE = "move [a-zA-Z][0-9]";
    public static final String PATTERN_PLACE = "place( [0-9]+)+";
    public static final String PATTERN_YIELD = "yield( +[0-9]+)?";

    public static final String FORMAT_UNIT_INFO = "%s (Team %s)%nATK: %d%nDEF: %d";
    public static final String FORMAT_STATS = " (%d" + SLASH + "%d)";
    public static final String FORMAT_STATE = "%s%n%d" + SLASH + "%d LP%nDC: %d" + SLASH + "%d%nBC: %d" + SLASH + "%d";
    public static final String FORMAT_HAND_CARD = "[%d] %s (%d" + SLASH + "%d)%n";

    public static final String FORMAT_ATTACKS = "%s%s attacks %s%s on %s!%n";
    public static final String FORMAT_DAMAGE = "%s takes %d damage!%n";
    public static final String FORMAT_DROPPED_ZERO = "%s's life points dropped to 0!%n";
    public static final String FORMAT_WINS = "%s wins!%n";
    public static final String FORMAT_FLIPPED = "%s (%d" + SLASH + "%d) was flipped on %s!%n";

    public static final String FORMAT_PLACES = "%s places %s on %s.%n";
    public static final String FORMAT_DISCARDED = "%s discarded %s (%d" + SLASH + "%d).%n";
    public static final String FORMAT_TURN = "It is %s's turn!%n";
    public static final String FORMAT_NO_CARDS = "%s has no cards left in the deck!%n";

    public static final String FORMAT_KING_NAME = "%s's %s";
    public static final String FORMAT_HIDDEN_UNIT = "??? (Team %s)%nATK: ???%nDEF: ???";
    public static final String NAME_HIDDEN_UNIT = "???";

    public static final String COMMAND_QUIT = "quit";
    public static final String COMMAND_SHOW = "show";
    public static final String COMMAND_STATE = "state";
    public static final String COMMAND_HAND = "hand";
    public static final String COMMAND_BOARD = "board";
    public static final String COMMAND_BLOCK = "block";
    public static final String COMMAND_FLIP = "flip";

    public static final String MESSAGE_HELP = "Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit.";

    public static final String ARGUMENT_SEED = "seed";
    public static final String ARGUMENT_DECK = "deck";
    public static final String ARGUMENT_VERBOSITY = "verbosity";
    public static final String ARGUMENT_UNITS = "units";
    public static final String ARGUMENT_SEPARATOR = "=";

    public static final String ERROR_ARGUMENT_FORMAT = "Invalid command line arguments.";
    public static final String ERROR_MOVE_DISTANCE = "Invalid move distance. Only 1 step horizontally" + SLASH + "vertically or en place allowed.";
    public static final String ERROR_NO_SELECTION = "No field selected or selected field is empty.";
    public static final String ERROR_ALREADY_MOVED = "This unit has already moved this turn.";
    public static final String ERROR_KING_MOVE = "Invalid king move. Kings cannot attack or be attacked by own king.";
    public static final String ERROR_NO_SELECTION_PLACE = "No field selected.";
    public static final String ERROR_ALREADY_PLACED = "You have already placed units this turn.";
    public static final String ERROR_INVALID_INDEX = "One or more provided hand indices are invalid.";
    public static final String ERROR_DUPLICATE_INDEX = "An index cannot be provided multiple times.";
    public static final String ERROR_OCCUPIED_BY_ENEMY = "Cannot place on a field occupied by the enemy.";
    public static final String ERROR_MUST_DISCARD = "You hold 5 units and must discard one using 'yield <idx>'.";
    public static final String ERROR_CANNOT_DISCARD = "You hold less than 5 units and cannot discard.";
    public static final String ERROR_NO_ARGUMENTS = "This command does not take any arguments.";

    public static final String ERROR_DECK_ROWS = "Deck rows do not match unit rows.";
    public static final String ERROR_DECK_SIZE = "Deck must contain exactly 40 cards.";
    public static final String ERROR_NEGATIVE_DAMAGE = "Damage cannot be negative.";
    public static final String ERROR_MAXIMUM_BOARD_CAPACITY = "Maximum board capacity reached.";
    public static final String ERROR_BOARD_COUNT_ZERO = "Board count is already zero.";
    public static final String ERROR_POSITION_OCCUPIED = "Position is already occupied.";
    public static final String ERROR_NO_UNIT_SOURCE = "No unit at source.";
    public static final String ERROR_TARGET_OCCUPIED = "Target occupied.";
    public static final String ERROR_POSITION_FORMAT = "Position must be exactly 2 characters long.";
    public static final String ERROR_POSITION_OUT_OF_BOUNDS = "Position %s is out of bounds.";

    private StringConstants() {
    }
}