package edu.kit.assignmentone.model.player;

import edu.kit.assignmentone.model.units.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a player in the game, keeping track of their health, deck, hand, and board limits.
 *
 * @author Programmieren-Team
 */
public class Player {

    private static final int MAX_LIFE_POINTS = 8000;
    private static final int MAX_BOARD_CAPACITY = 5;
    private static final int MAX_DECK_CAPACITY = 40;
    private static final int INITIAL_HAND_SIZE = 4;

    private final PlayerType type;
    private final Deck deck;
    private final List<Unit> hand;
    private int lifePoints;
    private int boardCount;
    private boolean hasPlacedThisTurn;

    /**
     * Creates a new player.
     *
     * @param type The type of the player (e.g., PLAYER or ENEMY)
     * @param deck The player's assigned deck
     */
    public Player(PlayerType type, Deck deck) {
        this.type = type;
        this.deck = deck;
        this.hand = new ArrayList<>();
        this.lifePoints = MAX_LIFE_POINTS;
        this.boardCount = 0;
        this.hasPlacedThisTurn = false;
    }

    public PlayerType getType() {
        return this.type;
    }

    public int getLifePoints() {
        return this.lifePoints;
    }

    public int getMaxLifePoints() {
        return MAX_LIFE_POINTS;
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative.");
        }
        this.lifePoints = Math.max(0, this.lifePoints - amount);
    }

    public boolean isDefeated() {
        return this.lifePoints <= 0;
    }

    public Deck getDeck() {
        return this.deck;
    }

    public List<Unit> getHand() {
        return this.hand;
    }

    public void drawInitialHand() {
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            drawCard();
        }
    }

    public boolean drawCard() {
        Optional<Unit> drawn = this.deck.drawTopUnit();
        if (drawn.isPresent()) {
            this.hand.add(drawn.get());
            return true;
        }
        return false;
    }

    public Unit removeCardFromHand(int index) {
        return this.hand.remove(index);
    }

    public int getBoardCount() {
        return this.boardCount;
    }

    public void incrementBoardCount() {
        if (this.boardCount >= MAX_BOARD_CAPACITY) {
            throw new IllegalStateException("Maximum board capacity reached.");
        }
        this.boardCount++;
    }

    public void decrementBoardCount() {
        if (this.boardCount <= 0) {
            throw new IllegalStateException("Board count is already zero.");
        }
        this.boardCount--;
    }

    public int getMaxDeckCapacity() {
        return MAX_DECK_CAPACITY;
    }

    public int getMaxBoardCapacity() {
        return MAX_BOARD_CAPACITY;
    }

    public boolean hasPlacedThisTurn() {
        return this.hasPlacedThisTurn;
    }

    public void setPlacedThisTurn(boolean placed) {
        this.hasPlacedThisTurn = placed;
    }
}