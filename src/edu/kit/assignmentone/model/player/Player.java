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

    public Player(PlayerType type, Deck deck) {
        this.type = type;
        this.deck = deck;
        this.hand = new ArrayList<>();
        this.lifePoints = MAXIMUM_LIFE_POINTS;
        this.boardCount = 0;
        this.placedThisTurn = false;
    }

    public PlayerType getType() { return this.type; }

    public boolean takeDamageAndCheckDefeat(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative.");
        }
        this.lifePoints = Math.max(0, this.lifePoints - amount);
        return this.lifePoints == 0;
    }

    public List<Unit> getHand() {
        return Collections.unmodifiableList(this.hand);
    }

    public int getHandSize() {
        return this.hand.size();
    }

    public boolean hasFullHand() {
        return this.hand.size() == FULL_HAND_SIZE;
    }

    public Unit processYield(String[] arguments) {
        Unit discardedUnit = null;
        boolean isFull = this.hand.size() == FULL_HAND_SIZE;
        if (isFull && arguments.length == 0) {
            throw new IllegalStateException(StringConstants.ERR_MUST_DISC);
        } else if (!isFull && arguments.length > 0) {
            throw new IllegalStateException(StringConstants.ERR_CANT_DISC);
        } else if (arguments.length > 0) {
            int index = Integer.parseInt(arguments[0]) - 1;
            if (index < 0 || index >= this.hand.size()) {
                throw new IllegalArgumentException(StringConstants.ERR_INV_IDX);
            }
            discardedUnit = this.hand.remove(index);
        }
        return discardedUnit;
    }

    public List<Unit> preparePlacement(List<Integer> indices, PlayerType targetOwner) {
        if (this.placedThisTurn) {
            throw new IllegalStateException(StringConstants.ERR_ALREADY_PLACED);
        }
        if (targetOwner != null && targetOwner != this.type) {
            throw new IllegalStateException(StringConstants.ERR_OCC_ENEMY);
        }
        for (int index : indices) {
            if (index < 0 || index >= this.hand.size()) {
                throw new IllegalArgumentException(StringConstants.ERR_INV_IDX);
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

    public int pickUnitToPlace(Random randomGenerator) {
        List<Integer> weights = new ArrayList<>();
        for (Unit unitObject : this.hand) {
            weights.add(unitObject.attack());
        }
        return RandomUtils.weightedRandom(weights, randomGenerator);
    }

    public int pickUnitToDiscard(Random randomGenerator) {
        List<Integer> weights = new ArrayList<>();
        for (Unit unitObject : this.hand) {
            weights.add(unitObject.attack() + unitObject.defense());
        }
        return RandomUtils.reverseWeightedRandom(weights, randomGenerator);
    }

    public void drawInitialHand() {
        for (int index = 0; index < INITIAL_HAND_SIZE; index++) {
            drawCard();
        }
    }

    public boolean drawCard() {
        boolean isSuccessful = false;
        Optional<Unit> drawnUnit = this.deck.drawTopUnit();
        if (drawnUnit.isPresent()) {
            this.hand.add(drawnUnit.get());
            isSuccessful = true;
        }
        return isSuccessful;
    }

    public int getBoardCount() { return this.boardCount; }

    public void incrementBoardCount() {
        if (this.boardCount >= MAXIMUM_BOARD_CAPACITY) {
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

    public void setPlacedThisTurn(boolean hasPlaced) { this.placedThisTurn = hasPlaced; }

    public String formatState() {
        return String.format(StringConstants.FMT_STATE, this.type.getDisplayName(),
                this.lifePoints, MAXIMUM_LIFE_POINTS, this.deck.size(),
                MAXIMUM_DECK_CAPACITY, this.boardCount, MAXIMUM_BOARD_CAPACITY);
    }
}