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
 */
public class Deck {

    private final List<Unit> units;

    public Deck() {
        this.units = new ArrayList<>();
    }

    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    public void shuffle(long seed) {
        Random random = new Random(seed);
        Collections.shuffle(this.units, random);
    }

    public Optional<Unit> drawTopUnit() {
        if (this.units.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.units.remove(0));
    }

    public int size() {
        return this.units.size();
    }
}