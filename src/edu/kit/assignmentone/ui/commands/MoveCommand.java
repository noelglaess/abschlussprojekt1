package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.DuelResult;
import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.Optional;

/**
 * Command to move the selected unit on the board.
 *
 * @author uqhkm
 * @version 1.0
 */
public class MoveCommand extends Command {

    /**
     * Creates a new move command.
     * @param game The game instance
     */
    public MoveCommand(Game game) {
        super(StringConstants.PATTERN_MOVE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game currentGame = this.getGame();
        Board board = currentGame.getBoard();
        Position sourcePosition = currentGame.getSelectedPosition();

        if (sourcePosition == null || board.isEmpty(sourcePosition)) {
            throw new IllegalStateException(StringConstants.ERROR_NO_SELECTION);
        }

        Position targetPosition = Position.fromString(arguments[0]);
        PlacedUnit movingUnit = board.getUnitAt(sourcePosition).orElseThrow();
        PlacedUnit targetUnit = board.getUnitAt(targetPosition).orElse(null);

        movingUnit.requireValidMove(sourcePosition.distanceTo(targetPosition), targetUnit);

        if (movingUnit.unblockIfBlocking()) {
            System.out.printf(StringConstants.FORMAT_NO_LONGER_BLOCKS, movingUnit.getName());
        }

        if (sourcePosition.distanceTo(targetPosition) == 0) {
            movingUnit.setMoved(true);
            System.out.printf(StringConstants.FORMAT_MOVES_TO, movingUnit.getName(), targetPosition);
        } else {
            executeMovement(board, movingUnit, targetUnit, sourcePosition, targetPosition);
        }

        if (currentGame.isRunning()) {
            Position selectedPosition = currentGame.getSelectedPosition();
            System.out.print(BoardFormatter.formatBoard(board, selectedPosition));
            if (selectedPosition != null) {
                Optional<PlacedUnit> unitOptional = board.getUnitAt(selectedPosition);
                if (unitOptional.isPresent()) {
                    System.out.println(unitOptional.get().formatInfo(currentGame));
                } else {
                    System.out.println(StringConstants.NO_UNIT_SELECTED);
                }
            } else {
                System.out.println(StringConstants.NO_UNIT_SELECTED);
            }
        }
    }

    private void executeMovement(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePosition, Position targetPosition) {
        Game currentGame = this.getGame();
        if (targetUnit == null) {
            board.moveUnit(sourcePosition, targetPosition);
            currentGame.setSelectedPosition(targetPosition);
            System.out.printf(StringConstants.FORMAT_MOVES_TO, movingUnit.getName(), targetPosition);
        } else if (movingUnit.getOwner() == targetUnit.getOwner()) {
            handleUnion(board, movingUnit, targetUnit, sourcePosition, targetPosition);
        } else {
            handleDuel(board, movingUnit, targetUnit, sourcePosition, targetPosition);
        }
        movingUnit.setMoved(true);
    }

    private void handleUnion(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePosition, Position targetPosition) {
        Game currentGame = this.getGame();
        String movingUnitName = movingUnit.getName();
        String targetUnitName = targetUnit.getName();
        System.out.printf(StringConstants.FORMAT_MOVES_TO, movingUnitName, targetPosition);
        System.out.printf(StringConstants.FORMAT_JOIN_FORCES, movingUnitName, targetUnitName, targetPosition);

        Optional<Unit> combinedOptional = movingUnit.getUnit().combineWith(targetUnit.getUnit());

        if (combinedOptional.isPresent()) {
            System.out.println(StringConstants.SUCCESS_MESSAGE);
            board.removeUnit(sourcePosition);
            targetUnit.setUnit(combinedOptional.get());
            targetUnit.setFlipped(movingUnit.isFlipped() && targetUnit.isFlipped());
            currentGame.setSelectedPosition(targetPosition);
        } else {
            System.out.printf(StringConstants.FORMAT_UNION_FAILED, targetUnitName);
            board.removeUnit(targetPosition);
            board.moveUnit(sourcePosition, targetPosition);
            getPlayer(targetUnit.getOwner()).decrementBoardCount();
            currentGame.setSelectedPosition(targetPosition);
        }
    }

    private void handleDuel(Board board, PlacedUnit attacker, PlacedUnit defender, Position sourcePosition, Position targetPosition) {
        printAttackInitiation(attacker, defender, targetPosition);
        handleFlipping(attacker, defender, sourcePosition, targetPosition);

        DuelResult duelResult = attacker.fightAgainst(defender);

        resolveEliminations(board, attacker, defender, sourcePosition, targetPosition, duelResult);
        handleDamage(duelResult);
        resolveMovement(board, attacker.getName(), duelResult, sourcePosition, targetPosition);
    }

    private void printAttackInitiation(PlacedUnit attacker, PlacedUnit defender, Position targetPosition) {
        boolean defenderHidden = !defender.isFlipped() && !defender.isKing() && defender.getOwner() != this.getGame().getActivePlayerObject().getType();

        String attackerName = attacker.getName();
        String defenderName = defenderHidden ? StringConstants.NAME_HIDDEN_UNIT : defender.getName();

        String attackerStats = String.format(StringConstants.FORMAT_STATS, attacker.getAttack(), attacker.getDefense());
        String defenderStats = defenderHidden || defender.isKing() ? StringConstants.EMPTY_STRING :
                String.format(StringConstants.FORMAT_STATS, defender.getAttack(), defender.getDefense());

        System.out.printf(StringConstants.FORMAT_ATTACKS, attackerName, attackerStats, defenderName, defenderStats, targetPosition);
    }

    private void handleFlipping(PlacedUnit attacker, PlacedUnit defender, Position sourcePosition, Position targetPosition) {
        if (attacker.flipIfCovered()) {
            System.out.printf(StringConstants.FORMAT_FLIPPED, attacker.getName(), attacker.getAttack(), attacker.getDefense(), sourcePosition);
        }
        if (defender.flipIfCovered()) {
            System.out.printf(StringConstants.FORMAT_FLIPPED, defender.getName(), defender.getAttack(), defender.getDefense(), targetPosition);
        }
    }

    private void resolveEliminations(Board board, PlacedUnit attacker, PlacedUnit defender, Position sourcePosition, Position targetPosition, DuelResult duelResult) {
        if (duelResult.defenderEliminated()) {
            System.out.printf(StringConstants.FORMAT_ELIMINATED, defender.getName());
            board.removeUnit(targetPosition);
            getPlayer(defender.getOwner()).decrementBoardCount();
        }
        if (duelResult.attackerEliminated()) {
            System.out.printf(StringConstants.FORMAT_ELIMINATED, attacker.getName());
            board.removeUnit(sourcePosition);
            getPlayer(attacker.getOwner()).decrementBoardCount();
            this.getGame().setSelectedPosition(null);
        }
    }

    private void handleDamage(DuelResult duelResult) {
        int damageValue = duelResult.damage();
        if (damageValue > 0) {
            PlayerType victimType = duelResult.victim();
            Player victimPlayer = getPlayer(victimType);
            String victimName = victimType.getDisplayName();
            System.out.printf(StringConstants.FORMAT_DAMAGE, victimName, damageValue);

            if (victimPlayer.takeDamageAndCheckDefeat(damageValue)) {
                System.out.printf(StringConstants.FORMAT_DROPPED_ZERO, victimName);
                PlayerType winnerType = victimType == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
                System.out.printf(StringConstants.FORMAT_WINS, winnerType.getDisplayName());
                this.getGame().quit();
            }
        }
    }

    private void resolveMovement(Board board, String attackerName, DuelResult duelResult, Position sourcePosition, Position targetPosition) {
        if (this.getGame().isRunning()) {
            if (duelResult.movesToDefenderPosition() && !duelResult.attackerEliminated()) {
                System.out.printf(StringConstants.FORMAT_MOVES_TO, attackerName, targetPosition);
                board.moveUnit(sourcePosition, targetPosition);
                this.getGame().setSelectedPosition(targetPosition);
            } else if (!duelResult.attackerEliminated()) {
                this.getGame().setSelectedPosition(sourcePosition);
            }
        }
    }

    private Player getPlayer(PlayerType type) {
        Game currentGame = this.getGame();
        return type == PlayerType.PLAYER ? currentGame.getHumanPlayer() : currentGame.getEnemyPlayer();
    }
}