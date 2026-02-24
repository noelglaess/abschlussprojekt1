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

    public MoveCommand(Game game) {
        super(StringConstants.REGEX_MOVE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game game = this.getGame();
        Board board = game.getBoard();
        Position sourcePosition = game.getSelectedPosition();

        if (sourcePosition == null || board.isEmpty(sourcePosition)) {
            throw new IllegalStateException(StringConstants.ERR_NO_SELECTION);
        }

        Position targetPosition = Position.fromString(arguments[0]);
        PlacedUnit movingUnit = board.getUnitAt(sourcePosition).orElseThrow();
        PlacedUnit targetUnit = board.getUnitAt(targetPosition).orElse(null);

        movingUnit.requireValidMove(sourcePosition.distanceTo(targetPosition), targetUnit);

        if (movingUnit.unblockIfBlocking()) {
            System.out.printf(StringConstants.FMT_NO_LONGER_BLOCKS, movingUnit.getName());
        }

        if (sourcePosition.distanceTo(targetPosition) == 0) {
            movingUnit.setMoved(true);
            System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), targetPosition);
        } else {
            executeMovement(board, movingUnit, targetUnit, sourcePosition, targetPosition);
        }

        if (game.isRunning()) {
            Position sel = game.getSelectedPosition();
            System.out.print(BoardFormatter.formatBoard(board, sel));
            // FIX: Druckt jetzt auch hier nach jedem Move die Einheiten-Info aus!
            if (sel != null) {
                Optional<PlacedUnit> unitOpt = board.getUnitAt(sel);
                if (unitOpt.isPresent()) {
                    System.out.println(unitOpt.get().formatInfo(game));
                } else {
                    System.out.println(StringConstants.NO_UNIT);
                }
            } else {
                System.out.println(StringConstants.NO_UNIT);
            }
        }
    }

    private void executeMovement(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePosition, Position targetPosition) {
        Game game = this.getGame();
        if (targetUnit == null) {
            board.moveUnit(sourcePosition, targetPosition);
            game.setSelectedPosition(targetPosition);
            System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), targetPosition);
        } else if (movingUnit.getOwner() == targetUnit.getOwner()) {
            handleUnion(board, movingUnit, targetUnit, sourcePosition, targetPosition);
        } else {
            handleDuel(board, movingUnit, targetUnit, sourcePosition, targetPosition);
        }
        movingUnit.setMoved(true);
    }

    private void handleUnion(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePosition, Position targetPosition) {
        Game game = this.getGame();
        String movingUnitName = movingUnit.getName();
        String targetUnitName = targetUnit.getName();
        System.out.printf(StringConstants.FMT_MOVES_TO, movingUnitName, targetPosition);
        System.out.printf(StringConstants.FMT_JOIN_FORCES, movingUnitName, targetUnitName, targetPosition);

        Optional<Unit> combinedOptional = movingUnit.getUnit().combineWith(targetUnit.getUnit());

        if (combinedOptional.isPresent()) {
            System.out.println(StringConstants.SUCCESS_MSG);
            board.removeUnit(sourcePosition);
            targetUnit.setUnit(combinedOptional.get());
            targetUnit.setFlipped(movingUnit.isFlipped() && targetUnit.isFlipped());
            game.setSelectedPosition(targetPosition);
        } else {
            System.out.printf(StringConstants.FMT_UNION_FAILED, targetUnitName);
            board.removeUnit(targetPosition);
            board.moveUnit(sourcePosition, targetPosition);
            getPlayer(targetUnit.getOwner()).decrementBoardCount();
            game.setSelectedPosition(targetPosition);
        }
    }

    private void handleDuel(Board board, PlacedUnit attacker, PlacedUnit defender, Position sourcePosition, Position targetPosition) {
        String attackerName = attacker.getName();
        int attackerAttack = attacker.getAttack();
        int attackerDefense = attacker.getDefense();
        String defenderName = defender.getName();
        int defenderAttack = defender.getAttack();
        int defenderDefense = defender.getDefense();

        String attackerStats = String.format(StringConstants.FMT_STATS, attackerAttack, attackerDefense);
        String defenderStats = defender.isKing() ? StringConstants.EMPTY :
                String.format(StringConstants.FMT_STATS, defenderAttack, defenderDefense);

        System.out.printf(StringConstants.FMT_ATTACKS, attackerName, attackerStats, defenderName, defenderStats, targetPosition);

        if (attacker.flipIfCovered()) {
            System.out.printf(StringConstants.FMT_FLIPPED, attackerName, attackerAttack, attackerDefense, sourcePosition);
        }
        if (defender.flipIfCovered()) {
            System.out.printf(StringConstants.FMT_FLIPPED, defenderName, defenderAttack, defenderDefense, targetPosition);
        }

        DuelResult result = attacker.fightAgainst(defender);
        int damage = result.damage();
        PlayerType victimType = result.victim();

        if (damage > 0) {
            Player victim = getPlayer(victimType);
            String victimName = victimType.getDisplayName();
            System.out.printf(StringConstants.FMT_DAMAGE, victimName, damage);
            if (victim.takeDamageAndCheckDefeat(damage)) {
                System.out.printf(StringConstants.FMT_DROPPED_ZERO, victimName);
                PlayerType winner = victimType == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
                System.out.printf(StringConstants.FMT_WINS, winner.getDisplayName());
                this.getGame().quit();
            }
        }

        resolveOutcome(board, attackerName, defenderName, defender.getOwner(), attacker.getOwner(), sourcePosition, targetPosition, result);
    }

    private void resolveOutcome(Board board, String attackerName, String defenderName, PlayerType defenderOwner, PlayerType attackerOwner, Position sourcePosition, Position targetPosition, DuelResult result) {
        Game game = this.getGame();
        boolean defenderEliminated = result.defenderEliminated();
        boolean attackerEliminated = result.attackerEliminated();

        if (defenderEliminated) {
            System.out.printf(StringConstants.FMT_ELIMINATED, defenderName);
            board.removeUnit(targetPosition);
            getPlayer(defenderOwner).decrementBoardCount();
        }
        if (attackerEliminated) {
            System.out.printf(StringConstants.FMT_ELIMINATED, attackerName);
            board.removeUnit(sourcePosition);
            getPlayer(attackerOwner).decrementBoardCount();
            game.setSelectedPosition(null);
        }
        if (result.moves() && !attackerEliminated) {
            System.out.printf(StringConstants.FMT_MOVES_TO, attackerName, targetPosition);
            board.moveUnit(sourcePosition, targetPosition);
            game.setSelectedPosition(targetPosition);
        } else if (!attackerEliminated) {
            game.setSelectedPosition(sourcePosition);
        }
    }

    private Player getPlayer(PlayerType type) {
        Game game = this.getGame();
        return type == PlayerType.PLAYER ? game.getHumanPlayer() : game.getEnemyPlayer();
    }
}