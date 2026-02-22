package edu.kit.assignmentone.model.player;

import edu.kit.assignmentone.model.units.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a player in the game.
 *
 * @author Programmieren-Team
 * @version 1.0
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
    private boolean placedThisTurn;

    /**
     * Creates a new player.
     * @param type player type
     * @param deck player deck
     */
    public Player(PlayerType type, Deck deck) {
        this.type = type;
        this.deck = deck;
        this.hand = new ArrayList<>();
        this.lifePoints = MAX_LIFE_POINTS;
        this.boardCount = 0;
        this.placedThisTurn = false;
    }

    /** @return player type */
    public PlayerType getType() { return this.type; }
    /** @return current life points */
    public int getLifePoints() { return this.lifePoints; }
    /** @return max life points */
    public int getMaxLifePoints() { return MAX_LIFE_POINTS; }

    /**
     * Takes damage.
     * @param amount damage amount
     */
    public void takeDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Damage cannot be negative.");
        this.lifePoints = Math.max(0, this.lifePoints - amount);
    }

    /** @return true if defeated */
    public boolean isDefeated() { return this.lifePoints <= 0; }
    /** @return the deck */
    public Deck getDeck() { return this.deck; }

    /** @return unmodifiable hand */
    public List<Unit> getHand() {
        return Collections.unmodifiableList(this.hand);
    }

    /** Draws the initial hand. */
    public void drawInitialHand() {
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            drawCard();
        }
    }

    /**
     * Draws a card.
     * @return true if successful
     */
    public boolean drawCard() {
        Optional<Unit> drawn = this.deck.drawTopUnit();
        if (drawn.isPresent()) {
            this.hand.add(drawn.get());
            return true;
        }
        return false;
    }

    /**
     * Removes card from hand.
     * @param index index
     * @return removed unit
     */
    public Unit removeCardFromHand(int index) {
        return this.hand.remove(index);
    }

    /** @return board count */
    public int getBoardCount() { return this.boardCount; }

    /** Increments board count. */
    public void incrementBoardCount() {
        if (this.boardCount >= MAX_BOARD_CAPACITY) throw new IllegalStateException("Maximum board capacity reached.");
        this.boardCount++;
    }

    /** Decrements board count. */
    public void decrementBoardCount() {
        if (this.boardCount <= 0) throw new IllegalStateException("Board count is already zero.");
        this.boardCount--;
    }

    /** @return max deck */
    public int getMaxDeckCapacity() { return MAX_DECK_CAPACITY; }
    /** @return max board */
    public int getMaxBoardCapacity() { return MAX_BOARD_CAPACITY; }
    /** @return true if placed */
    public boolean isPlacedThisTurn() { return this.placedThisTurn; }
    /** @param placed bool value */
    public void setPlacedThisTurn(boolean placed) { this.placedThisTurn = placed; }
}