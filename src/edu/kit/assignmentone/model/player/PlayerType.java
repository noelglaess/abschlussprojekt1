package edu.kit.assignmentone.model.player;

/**
 * Represents the type of a player in the game.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public enum PlayerType {
    /** The human player. */ PLAYER("Player"),
    /** The AI enemy. */ ENEMY("Enemy");

    private final String displayName;

    PlayerType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of the player type.
     * @return the display name
     */
    public String getDisplayName() {
        return this.displayName;
    }
}