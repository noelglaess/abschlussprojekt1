package edu.kit.assignmentone.model;

import edu.kit.assignmentone.model.player.PlayerType;

/**
 * Represents the outcome of a duel between two units.
 *
 * @param damage             The amount of damage dealt
 * @param victim             The player who took the damage
 * @param defenderEliminated True if the defending unit was eliminated
 * @param attackerEliminated True if the attacking unit was eliminated
 * @param movesToDefenderPosition True if the attacker moves to the defender's position
 *
 * @author uqhkm
 * @version 1.0
 */
public record DuelResult(int damage, PlayerType victim, boolean defenderEliminated, boolean attackerEliminated, boolean movesToDefenderPosition) {
}