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
 * @author uqhkm
 * @version 1.0
 */
public class Deck {

    private final List<Unit> units;

    public Deck() {
        this.units = new ArrayList<>();
    }

    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    /**
     * Shuffles the deck using the given random generator.
     *
     * @param randomGenerator The global random instance
     */
    public void shuffle(Random randomGenerator) {
        Collections.shuffle(this.units, randomGenerator);
    }

    public Optional<Unit> drawTopUnit() {
        if (this.units.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.units.removeFirst());
    }

    public int size() {
        return this.units.size();
    }
}