package edu.kit.assignmentone.model.player;

import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Represents a player in the game.
 *
 * @author uXXXXX
 * @version 1.0
 */
public class Player {

    private static final int MAX_LIFE_POINTS = 8000;
    private static final int MAX_BOARD_CAPACITY = 5;
    private static final int MAX_DECK_CAPACITY = 40;
    private static final int INITIAL_HAND_SIZE = 4;
    private static final int FULL_HAND_SIZE = 5;

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

    /**
     * Takes damage and checks for defeat.
     * @param amount damage amount
     * @return true if player is defeated
     */
    public boolean takeDamageAndCheckDefeat(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative.");
        }
        this.lifePoints = Math.max(0, this.lifePoints - amount);
        return this.lifePoints == 0;
    }

    /** @return unmodifiable hand */
    public List<Unit> getHand() {
        return Collections.unmodifiableList(this.hand);
    }

    /** @return current size of hand */
    public int getHandSize() {
        return this.hand.size();
    }

    /** @return true if hand is full */
    public boolean hasFullHand() {
        return this.hand.size() == FULL_HAND_SIZE;
    }

    /**
     * Discards a card from hand.
     * @param arguments user args
     * @return The discarded unit
     */
    public Unit processYield(String[] arguments) {
        Unit discarded = null;
        boolean full = this.hand.size() == FULL_HAND_SIZE;
        if (full && arguments.length == 0) {
            throw new IllegalStateException(StringConstants.ERR_MUST_DISC);
        } else if (!full && arguments.length > 0) {
            throw new IllegalStateException(StringConstants.ERR_CANT_DISC);
        } else if (arguments.length > 0) {
            int idx = Integer.parseInt(arguments[0]) - 1;
            if (idx < 0 || idx >= this.hand.size()) {
                throw new IllegalArgumentException(StringConstants.ERR_INV_IDX);
            }
            discarded = this.hand.remove(idx);
        }
        return discarded;
    }

    /**
     * Prepares units for placement.
     * @param indices List of indices
     * @param targetOwner Owner of target
     * @return List of units
     */
    public List<Unit> preparePlacement(List<Integer> indices, PlayerType targetOwner) {
        if (this.placedThisTurn) {
            throw new IllegalStateException(StringConstants.ERR_ALREADY_PLACED);
        }
        if (targetOwner != null && targetOwner != this.type) {
            throw new IllegalStateException(StringConstants.ERR_OCC_ENEMY);
        }
        for (int idx : indices) {
            if (idx < 0 || idx >= this.hand.size()) {
                throw new IllegalArgumentException(StringConstants.ERR_INV_IDX);
            }
        }

        List<Unit> pulled = new ArrayList<>();
        for (int idx : indices) {
            pulled.add(this.hand.get(idx));
        }

        List<Integer> sorted = new ArrayList<>(indices);
        sorted.sort(Collections.reverseOrder());
        for (int idx : sorted) {
            this.hand.remove(idx);
        }

        this.placedThisTurn = true;
        return pulled;
    }

    /**
     * AI method to pick unit to place.
     * @param rnd random generator
     * @return unit index
     */
    public int pickUnitToPlace(Random rnd) {
        List<Integer> weights = new ArrayList<>();
        for (Unit u : this.hand) {
            weights.add(u.attack());
        }
        return RandomUtils.weightedRandom(weights, rnd);
    }

    /**
     * AI method to pick unit to discard.
     * @param rnd random generator
     * @return unit index
     */
    public int pickUnitToDiscard(Random rnd) {
        List<Integer> weights = new ArrayList<>();
        for (Unit u : this.hand) {
            weights.add(u.attack() + u.defense());
        }
        return RandomUtils.reverseWeightedRandom(weights, rnd);
    }

    /** Draws the initial hand. */
    public void drawInitialHand() {
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) drawCard();
    }

    /**
     * Draws a card.
     * @return true if successful
     */
    public boolean drawCard() {
        boolean success = false;
        Optional<Unit> drawn = this.deck.drawTopUnit();
        if (drawn.isPresent()) {
            this.hand.add(drawn.get());
            success = true;
        }
        return success;
    }

    /** @return board count */
    public int getBoardCount() { return this.boardCount; }

    /** Increments board count. */
    public void incrementBoardCount() {
        if (this.boardCount >= MAX_BOARD_CAPACITY) {
            throw new IllegalStateException("Maximum board capacity reached.");
        }
        this.boardCount++;
    }

    /** Decrements board count. */
    public void decrementBoardCount() {
        if (this.boardCount <= 0) {
            throw new IllegalStateException("Board count is already zero.");
        }
        this.boardCount--;
    }

    /** @return max capacity */
    public int getMaxBoardCapacity() { return MAX_BOARD_CAPACITY; }

    /** @param placed bool value */
    public void setPlacedThisTurn(boolean placed) { this.placedThisTurn = placed; }

    /**
     * Formats the player state as a string.
     * @return the formatted state
     */
    public String formatState() {
        return String.format(StringConstants.FMT_STATE, this.type.getDisplayName(),
                this.lifePoints, MAX_LIFE_POINTS, this.deck.size(),
                MAX_DECK_CAPACITY, this.boardCount, MAX_BOARD_CAPACITY);
    }
}