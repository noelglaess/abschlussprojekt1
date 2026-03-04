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
 * @author uqhkm
 * @version 1.0
 */
public class Player {

    /** The maximum number of units allowed on the board per player. */
    public static final int MAXIMUM_BOARD_CAPACITY = 5;

    private static final int MAXIMUM_LIFE_POINTS = 8000;
    private static final int MAXIMUM_DECK_CAPACITY = 40;
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
     *
     * @param type The player type
     * @param deck The player's deck
     */
    public Player(PlayerType type, Deck deck) {
        this.type = type;
        this.deck = deck;
        this.hand = new ArrayList<>();
        this.lifePoints = MAXIMUM_LIFE_POINTS;
        this.boardCount = 0;
        this.placedThisTurn = false;
    }

    /**
     * Gets the player type.
     * @return The type
     */
    public PlayerType getType() { return this.type; }

    /**
     * Subtracts damage from the player's life points and checks for defeat.
     *
     * @param amount The damage amount
     * @return True if the player is defeated (LP dropped to 0)
     * @throws IllegalArgumentException If the damage is negative
     */
    public boolean takeDamageAndCheckDefeat(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(StringConstants.ERROR_NEGATIVE_DAMAGE);
        }
        this.lifePoints = Math.max(0, this.lifePoints - amount);
        return this.lifePoints == 0;
    }

    /**
     * Gets the player's current hand.
     * @return An unmodifiable list of units
     */
    public List<Unit> getHand() {
        return Collections.unmodifiableList(this.hand);
    }

    /**
     * Gets the current hand size.
     * @return The size
     */
    public int getHandSize() {
        return this.hand.size();
    }

    /**
     * Checks if the hand is full.
     * @return True if full
     */
    public boolean isHandFull() {
        return this.hand.size() == FULL_HAND_SIZE;
    }

    /**
     * Processes the yield command logic for the player.
     *
     * @param arguments The command arguments
     * @return The discarded unit, or null if none
     */
    public Unit processYield(String[] arguments) {
        Unit discardedUnit = null;
        boolean hasMaximumCards = this.hand.size() == FULL_HAND_SIZE;
        if (hasMaximumCards && arguments.length == 0) {
            throw new IllegalStateException(StringConstants.ERROR_MUST_DISCARD);
        } else if (!hasMaximumCards && arguments.length > 0) {
            throw new IllegalStateException(StringConstants.ERROR_CANNOT_DISCARD);
        } else if (arguments.length > 0) {
            int index = Integer.parseInt(arguments[0]) - 1;
            if (index < 0 || index >= this.hand.size()) {
                throw new IllegalArgumentException(StringConstants.ERROR_INVALID_INDEX);
            }
            discardedUnit = this.hand.remove(index);
        }
        return discardedUnit;
    }

    /**
     * Prepares units for placement.
     *
     * @param indices     The indices of the units to place
     * @param targetOwner The owner of the target field
     * @return The list of units to place
     */
    public List<Unit> preparePlacement(List<Integer> indices, PlayerType targetOwner) {
        if (this.placedThisTurn) {
            throw new IllegalStateException(StringConstants.ERROR_ALREADY_PLACED);
        }
        if (targetOwner != null && targetOwner != this.type) {
            throw new IllegalStateException(StringConstants.ERROR_OCCUPIED_BY_ENEMY);
        }
        for (int index : indices) {
            if (index < 0 || index >= this.hand.size()) {
                throw new IllegalArgumentException(StringConstants.ERROR_INVALID_INDEX);
            }
        }

        List<Unit> pulledUnits = new ArrayList<>();
        for (int index : indices) {
            pulledUnits.add(this.hand.get(index));
        }

        List<Integer> sortedIndices = new ArrayList<>(indices);
        sortedIndices.sort(Collections.reverseOrder());
        for (int index : sortedIndices) {
            this.hand.remove(index);
        }

        this.placedThisTurn = true;
        return pulledUnits;
    }

    /**
     * Selects a unit index to place based on AI logic.
     *
     * @param randomGenerator The random generator
     * @return The chosen index
     */
    public int pickUnitToPlace(Random randomGenerator) {
        List<Integer> weights = new ArrayList<>();
        for (Unit unitObject : this.hand) {
            weights.add(unitObject.attack());
        }
        return RandomUtils.weightedRandom(weights, randomGenerator);
    }

    /**
     * Selects a unit index to discard based on AI logic.
     *
     * @param randomGenerator The random generator
     * @return The chosen index
     */
    public int pickUnitToDiscard(Random randomGenerator) {
        List<Integer> weights = new ArrayList<>();
        for (Unit unitObject : this.hand) {
            weights.add(unitObject.attack() + unitObject.defense());
        }
        return RandomUtils.reverseWeightedRandom(weights, randomGenerator);
    }

    /**
     * Draws the initial hand of units.
     */
    public void drawInitialHand() {
        for (int index = 0; index < INITIAL_HAND_SIZE; index++) {
            drawCard();
        }
    }

    /**
     * Draws a single card from the deck.
     *
     * @return True if successful
     */
    public boolean drawCard() {
        boolean isSuccessful = false;
        Optional<Unit> drawnUnit = this.deck.drawTopUnit();
        if (drawnUnit.isPresent()) {
            this.hand.add(drawnUnit.get());
            isSuccessful = true;
        }
        return isSuccessful;
    }

    /**
     * Gets the current amount of units on the board.
     * @return The count
     */
    public int getBoardCount() { return this.boardCount; }

    /** Increments the board unit counter. */
    public void incrementBoardCount() {
        if (this.boardCount >= MAXIMUM_BOARD_CAPACITY) {
            throw new IllegalStateException(StringConstants.ERROR_MAXIMUM_BOARD_CAPACITY);
        }
        this.boardCount++;
    }

    /** Decrements the board unit counter. */
    public void decrementBoardCount() {
        if (this.boardCount <= 0) {
            throw new IllegalStateException(StringConstants.ERROR_BOARD_COUNT_ZERO);
        }
        this.boardCount--;
    }

    /**
     * Sets whether the player has placed units this turn.
     * @param hasPlaced True if placed
     */
    public void setPlacedThisTurn(boolean hasPlaced) { this.placedThisTurn = hasPlaced; }

    /**
     * Formats the current state of the player.
     * @return The formatted string
     */
    public String formatStateInformation() {
        return String.format(StringConstants.FORMAT_STATE, this.type.getDisplayName(),
                this.lifePoints, MAXIMUM_LIFE_POINTS, this.deck.size(),
                MAXIMUM_DECK_CAPACITY, this.boardCount, MAXIMUM_BOARD_CAPACITY);
    }
}