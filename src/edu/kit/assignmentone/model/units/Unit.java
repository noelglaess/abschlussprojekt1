package edu.kit.assignmentone.model.units;

/**
 * Represents a single unit configuration as read from the units file.
 * * @param name The name of the unit (e.g., "Daisy")
 * @param type The type of the unit (e.g., FARMER)
 * @param attack The attack value
 * @param defense The defense value
 * * @author Programmieren-Team
 */
public record Unit(String name, edu.kit.assignmentone.model.units.UnitType type, int attack, int defense) {

    /**
     * Creates a deep copy of this unit.
     * @return A new Unit instance with the same properties.
     */
    public Unit copy() {
        return new Unit(this.name, this.type, this.attack, this.defense);
    }
}