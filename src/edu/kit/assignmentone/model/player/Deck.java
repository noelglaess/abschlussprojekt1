package edu.kit.assignmentone.model.player;

import edu.kit.assignmentone.model.units.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Represents a deck of units for a player.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class Deck {

    private final List<Unit> units;

    /**
     * Creates a new, empty deck.
     */
    public Deck() {
        this.units = new ArrayList<>();
    }

    /**
     * Adds a unit to the bottom of the deck.
     *
     * @param unit The unit to add
     */
    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    /**
     * Shuffles the deck using the given seed.
     *
     * @param seed The random seed to use for shuffling
     */
    public void shuffle(long seed) {
        Random random = new Random(seed);
        Collections.shuffle(this.units, random);
    }

    /**
     * Draws the top unit from the deck.
     *
     * @return An Optional containing the drawn unit, or empty if the deck is empty
     */
    public Optional<Unit> drawTopUnit() {
        if (this.units.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.units.removeFirst());
    }

    /**
     * Gets the current number of units remaining in the deck.
     *
     * @return The size of the deck
     */
    public int size() {
        return this.units.size();
    }
}