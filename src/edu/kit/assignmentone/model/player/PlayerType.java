package edu.kit.assignmentone.model.player;

/**
 * Represents the two types of players in the game.
 *
 * @author uqhkm
 * @version 1.0
 */
public enum PlayerType {

    /** The human player. */
    PLAYER("Player"),

    /** The AI enemy. */
    ENEMY("Enemy");

    private final String displayName;

    PlayerType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the formatted display name.
     * @return The name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns the opposite player type.
     * @return The other player type
     */
    public PlayerType next() {
        return this == PLAYER ? ENEMY : PLAYER;
    }
}