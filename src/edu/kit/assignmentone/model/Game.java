package edu.kit.assignmentone.model;

import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.io.ResourceLoader;
import edu.kit.assignmentone.model.player.Deck;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.units.UnitType;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * The main game logic class (Facade).
 *
 * @author uqhkm
 * @version 1.0
 */
public class Game {

    private static final int REQUIRED_DECK_SIZE = 40;
    private static final int BOARD_SIZE = 7;

    private final Random random;
    private final List<Unit> allUnits;
    private final List<Integer> deckBlueprint;

    private final Board board;
    private final Player humanPlayer;
    private final Player enemyPlayer;

    private boolean running;
    private Position selectedPosition;
    private PlayerType activePlayer;

    /**
     * Constructs a new game instance.
     *
     * @param seed      The random seed
     * @param deckPath  Path to the deck configuration file
     * @param unitsPath Path to the units configuration file
     * @throws IOException              If reading the files fails
     * @throws IllegalStateException    If the deck configuration is invalid
     */
    public Game(long seed, String deckPath, String unitsPath) throws IOException {
        this.random = new Random(seed);
        this.running = true;

        this.allUnits = ResourceLoader.loadUnits(unitsPath);
        this.deckBlueprint = ResourceLoader.loadDeck(deckPath);

        if (this.allUnits.size() != this.deckBlueprint.size()) {
            throw new IllegalStateException(StringConstants.ERR_DECK_ROWS);
        }
        int totalCards = 0;
        for (int count : this.deckBlueprint) {
            totalCards += count;
        }
        if (totalCards != REQUIRED_DECK_SIZE) {
            throw new IllegalStateException(StringConstants.ERR_DECK_SIZE);
        }

        this.board = new Board();

        Deck humanDeck = createDeckFromBlueprint();
        Deck enemyDeck = createDeckFromBlueprint();

        humanDeck.shuffle(this.random);
        enemyDeck.shuffle(this.random);

        this.humanPlayer = new Player(PlayerType.PLAYER, humanDeck);
        this.enemyPlayer = new Player(PlayerType.ENEMY, enemyDeck);

        initPlayer(this.humanPlayer, true);
        initPlayer(this.enemyPlayer, false);

        Unit kingUnit = new Unit(StringConstants.EMPTY, StringConstants.KING_NAME, UnitType.FARMER, 0, 0);
        this.board.placeUnit(new Position(3, 0), new PlacedUnit(kingUnit, PlayerType.PLAYER));
        this.board.placeUnit(new Position(3, 6), new PlacedUnit(kingUnit, PlayerType.ENEMY));

        this.activePlayer = PlayerType.PLAYER;
    }

    private void initPlayer(Player player, boolean drawExtra) {
        player.drawInitialHand();
        if (drawExtra) {
            player.drawCard();
        }
    }

    private Deck createDeckFromBlueprint() {
        Deck deck = new Deck();
        for (int i = 0; i < this.allUnits.size(); i++) {
            int count = this.deckBlueprint.get(i);
            for (int j = 0; j < count; j++) {
                deck.addUnit(this.allUnits.get(i).copy());
            }
        }
        return deck;
    }

    /**
     * Checks if the game is still running.
     *
     * @return True if running, false otherwise
     */
    public boolean isRunning() { return this.running; }

    /** Quits the game. */
    public void quit() { this.running = false; }

    /**
     * Checks if it is the enemy's turn.
     *
     * @return True if enemy turn
     */
    public boolean isEnemyTurn() { return this.activePlayer == PlayerType.ENEMY; }

    /**
     * Gets the game's random instance.
     *
     * @return The random generator
     */
    public Random getRandom() { return this.random; }

    /**
     * Gets the human player.
     *
     * @return The human player
     */
    public Player getHumanPlayer() { return this.humanPlayer; }

    /**
     * Gets the enemy player.
     *
     * @return The enemy player
     */
    public Player getEnemyPlayer() { return this.enemyPlayer; }

    /**
     * Gets the game board.
     *
     * @return The board
     */
    public Board getBoard() { return this.board; }

    /**
     * Gets the currently selected position.
     *
     * @return The selected position, or null
     */
    public Position getSelectedPosition() { return this.selectedPosition; }

    /**
     * Sets the currently selected position.
     *
     * @param position The position to select
     */
    public void setSelectedPosition(Position position) { this.selectedPosition = position; }

    /**
     * Gets the active player object.
     *
     * @return The active player
     */
    public Player getActivePlayerObject() {
        return this.activePlayer == PlayerType.PLAYER ? this.humanPlayer : this.enemyPlayer;
    }

    /**
     * Switches the turn to the other player and executes turn preparation.
     */
    public void switchTurn() {
        this.selectedPosition = null;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                this.board.getUnitAt(new Position(col, row)).ifPresent(unitObj -> unitObj.setMoved(false));
            }
        }

        PlayerType previousPlayer = this.activePlayer;
        this.activePlayer = previousPlayer.next();

        Player nextPlayer = getActivePlayerObject();
        nextPlayer.setPlacedThisTurn(false);

        String activeName = this.activePlayer.getDisplayName();

        if (!nextPlayer.drawCard()) {
            System.out.printf(StringConstants.FMT_NO_CARDS, activeName);
            System.out.printf(StringConstants.FMT_WINS, previousPlayer.getDisplayName());
            quit();
        } else {
            System.out.printf(StringConstants.FMT_TURN, activeName);
        }
    }
}