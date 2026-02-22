package edu.kit.assignmentone.model;

import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.io.ResourceLoader;
import edu.kit.assignmentone.model.player.Deck;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * The main game logic class (Facade).
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class Game {

    private final Random random;
    private final List<Unit> allUnits;
    private final List<Integer> deckBlueprint;

    private final Board board;
    private final Player humanPlayer;
    private final Player enemyPlayer;

    private boolean isRunning;
    private Position selectedPosition;
    private PlayerType activePlayer;

    /**
     * Initializes a new Game.
     *
     * @param seed The random seed
     * @param deckPath Path to the deck file
     * @param unitsPath Path to the units file
     * @throws IOException If files cannot be read
     */
    public Game(long seed, String deckPath, String unitsPath) throws IOException {
        this.random = new Random(seed);
        this.isRunning = true;

        this.allUnits = ResourceLoader.loadUnits(unitsPath);
        this.deckBlueprint = ResourceLoader.loadDeck(deckPath);

        this.board = new Board();

        Deck humanDeck = createDeckFromBlueprint();
        Deck enemyDeck = createDeckFromBlueprint();

        humanDeck.shuffle(seed);
        enemyDeck.shuffle(seed);

        this.humanPlayer = new Player(PlayerType.PLAYER, humanDeck);
        this.enemyPlayer = new Player(PlayerType.ENEMY, enemyDeck);

        this.humanPlayer.drawInitialHand();
        this.enemyPlayer.drawInitialHand();

        this.activePlayer = PlayerType.PLAYER;
        this.humanPlayer.drawCard();
    }

    private Deck createDeckFromBlueprint() {
        Deck deck = new Deck();
        for (Integer unitIndex : this.deckBlueprint) {
            int listIndex = unitIndex - 1;
            if (listIndex >= 0 && listIndex < this.allUnits.size()) {
                deck.addUnit(this.allUnits.get(listIndex).copy());
            }
        }
        return deck;
    }

    /**
     * Checks if the game is still running.
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    /**
     * Stops the game.
     */
    public void quit() {
        this.isRunning = false;
    }

    /**
     * Gets the game's random instance.
     * @return the random instance
     */
    public Random getRandom() {
        return this.random;
    }

    /**
     * Gets the human player.
     * @return the human player
     */
    public Player getHumanPlayer() {
        return this.humanPlayer;
    }

    /**
     * Gets the enemy player.
     * @return the enemy player
     */
    public Player getEnemyPlayer() {
        return this.enemyPlayer;
    }

    /**
     * Gets the game board.
     * @return the board
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Gets the currently selected position.
     * @return the selected position, or null if none
     */
    public Position getSelectedPosition() {
        return this.selectedPosition;
    }

    /**
     * Sets the currently selected position.
     * @param position the position to select
     */
    public void setSelectedPosition(Position position) {
        this.selectedPosition = position;
    }

    /**
     * Gets the player type currently taking their turn.
     * @return the active player type
     */
    public PlayerType getActivePlayer() {
        return this.activePlayer;
    }

    /**
     * Gets the player object currently taking their turn.
     * @return the active player object
     */
    public Player getActivePlayerObject() {
        return this.activePlayer == PlayerType.PLAYER ? this.humanPlayer : this.enemyPlayer;
    }

    /**
     * Switches the turn to the other player and handles drawing.
     */
    public void switchTurn() {
        this.selectedPosition = null;

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                this.board.getUnitAt(new Position(col, row)).ifPresent(placedUnit -> placedUnit.setMoved(false));
            }
        }

        this.activePlayer = (this.activePlayer == PlayerType.PLAYER) ? PlayerType.ENEMY : PlayerType.PLAYER;
        Player next = getActivePlayerObject();
        next.setPlacedThisTurn(false);

        boolean canDraw = next.drawCard();
        if (!canDraw) {
            System.out.printf(StringConstants.EMPTY + StringConstants.FMT_NO_CARDS, next.getType().getDisplayName());
            PlayerType winner = next.getType() == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
            System.out.printf(StringConstants.EMPTY + "%s wins!%n", winner.getDisplayName());
            quit();
            return;
        }
        System.out.printf(StringConstants.EMPTY + "It is %s's turn!%n", next.getType().getDisplayName());
    }
}