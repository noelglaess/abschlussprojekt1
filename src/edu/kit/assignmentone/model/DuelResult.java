package edu.kit.assignmentone.model;

import edu.kit.assignmentone.model.player.PlayerType;

/**
 * Record holding the outcome of a duel.
 *
 * @param victim The player taking damage, or null if none
 * @param damage The amount of damage dealt
 * @param atkEliminated True if the attacker is destroyed
 * @param defEliminated True if the defender is destroyed
 * @param moves True if the attacker successfully takes the defender's spot
 * @author Programmieren-Team
 * @version 1.0
 */
public record DuelResult(PlayerType victim, int damage, boolean atkEliminated, boolean defEliminated, boolean moves) {
}