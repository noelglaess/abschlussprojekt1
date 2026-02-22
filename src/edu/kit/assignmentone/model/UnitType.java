package edu.kit.assignmentone.model.units;

/**
 * Represents the different types a unit can have.
 * * @author Programmieren-Team
 */
public enum UnitType {
    /** Farmer type unit. */ FARMER("Farmer"),
    /** Maid type unit. */ MAID("Maid"),
    /** Spreader type unit. */ SPREADER("Spreader"),
    /** Builder type unit. */ BUILDER("Builder"),
    /** Guard type unit. */ GUARD("Guard"),
    /** Sorceress type unit. */ SORCERESS("Sorceress"),
    /** Architect type unit. */ ARCHITECT("Architect"),
    /** Operator type unit. */ OPERATOR("Operator");

    private final String identifier;

    UnitType(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Parses a string to the corresponding UnitType.
     * @param typeString The string representation of the unit type
     * @return The UnitType
     * @throws IllegalArgumentException if the type is unknown
     */
    public static UnitType fromString(String typeString) {
        for (UnitType type : values()) {
            if (type.identifier.equalsIgnoreCase(typeString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown unit type: " + typeString);
    }
}