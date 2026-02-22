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
 * The main game logic class.
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

    public boolean isRunning() {
        return this.isRunning;
    }

    public void quit() {
        this.isRunning = false;
    }

    public Random getRandom() {
        return this.random;
    }

    public Player getHumanPlayer() {
        return this.humanPlayer;
    }

    public Player getEnemyPlayer() {
        return this.enemyPlayer;
    }

    public Board getBoard() {
        return this.board;
    }

    public Position getSelectedPosition() {
        return this.selectedPosition;
    }

    public void setSelectedPosition(Position position) {
        this.selectedPosition = position;
    }

    public Player getActivePlayerObject() {
        return this.activePlayer == PlayerType.PLAYER ? this.humanPlayer : this.enemyPlayer;
    }

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
            System.out.printf("%s has no cards left in the deck!%n", next.getType().getDisplayName());
            PlayerType winner = next.getType() == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
            System.out.printf("%s wins!%n", winner.getDisplayName());
            quit();
            return;
        }
        System.out.printf("It is %s's turn!%n", next.getType().getDisplayName());

        if (this.activePlayer == PlayerType.ENEMY) {
            AIEngine.playTurn(this);
        }
    }
}