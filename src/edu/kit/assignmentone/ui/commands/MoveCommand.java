package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.StringConstants;
import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.units.UnitCombiner;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.Optional;

/**
 * Command to move the selected unit on the board.
 * Handles movement, unblocking, unions, and duels.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class MoveCommand extends Command {

    private static final String COMMAND_NAME = "move";
    private static final String COMMAND_REGEX = "move [a-zA-Z]\\d";
    private static final String ERROR_NO_SELECTION = "No field selected or selected field is empty.";
    private static final String ERROR_ALREADY_MOVED = "This unit has already moved this turn.";
    private static final String ERROR_INVALID_DISTANCE = "Invalid move distance. Only 1 step horizontally/vertically or en place allowed.";
    private static final String ERROR_KING_MOVE = "Invalid king move. Kings cannot attack or be attacked by own king.";

    /**
     * Creates a new move command.
     * @param game The game to execute the command on
     */
    public MoveCommand(Game game) {
        super(COMMAND_REGEX, game);
    }

    @Override
    public void execute(String[] arguments) {
        Board board = this.getGame().getBoard();
        Position sourcePos = this.getGame().getSelectedPosition();

        if (sourcePos == null || board.isEmpty(sourcePos)) {
            throw new IllegalStateException(ERROR_NO_SELECTION);
        }

        Position targetPos = Position.fromString(arguments[0]);
        PlacedUnit movingUnit = board.getUnitAt(sourcePos).orElseThrow();

        validateMovementRules(board, movingUnit, sourcePos, targetPos);
        unblockIfNecessary(movingUnit);

        int distance = sourcePos.distanceTo(targetPos);
        if (distance == 0) {
            movingUnit.setMoved(true);
            System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), targetPos);
            printBoard(targetPos);
            return;
        }

        executeMovement(board, movingUnit, sourcePos, targetPos);
        printBoard(this.getGame().getSelectedPosition());
    }

    private void unblockIfNecessary(PlacedUnit unit) {
        if (unit.isBlocking()) {
            unit.setBlocking(false);
            System.out.printf(StringConstants.FMT_NO_LONGER_BLOCKS, unit.getName());
        }
    }

    private void executeMovement(Board board, PlacedUnit movingUnit, Position source, Position target) {
        Optional<PlacedUnit> targetUnitOpt = board.getUnitAt(target);
        if (targetUnitOpt.isEmpty()) {
            board.moveUnit(source, target);
            this.getGame().setSelectedPosition(target);
            System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), target);
        } else {
            PlacedUnit targetUnit = targetUnitOpt.get();
            if (movingUnit.getOwner() == targetUnit.getOwner()) {
                handleUnion(board, movingUnit, targetUnit, source, target);
            } else {
                handleDuel(board, movingUnit, targetUnit, source, target);
            }
        }
        movingUnit.setMoved(true);
    }

    private void validateMovementRules(Board board, PlacedUnit movingUnit, Position sourcePos, Position targetPos) {
        if (movingUnit.hasMoved()) {
            throw new IllegalStateException(ERROR_ALREADY_MOVED);
        }
        if (sourcePos.distanceTo(targetPos) > 1) {
            throw new IllegalStateException(ERROR_INVALID_DISTANCE);
        }
        Optional<PlacedUnit> targetUnitOpt = board.getUnitAt(targetPos);
        if (targetUnitOpt.isPresent()) {
            PlacedUnit targetUnit = targetUnitOpt.get();
            boolean isMovingKing = movingUnit.isKing();
            boolean isTargetKing = targetUnit.isKing();

            if (isTargetKing && movingUnit.getOwner() == targetUnit.getOwner()) {
                throw new IllegalStateException(ERROR_KING_MOVE);
            }
            if (isMovingKing && movingUnit.getOwner() != targetUnit.getOwner()) {
                throw new IllegalStateException(ERROR_KING_MOVE);
            }
        }
    }

    private void handleUnion(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePos, Position targetPos) {
        System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), targetPos);
        System.out.printf(StringConstants.FMT_JOIN_FORCES, movingUnit.getName(), targetUnit.getName(), targetPos);

        Optional<Unit> combinedOpt = UnitCombiner.tryCombine(movingUnit.getUnit(), targetUnit.getUnit());

        if (combinedOpt.isPresent()) {
            System.out.println(StringConstants.SUCCESS_MSG);
            board.removeUnit(sourcePos);
            targetUnit.setUnit(combinedOpt.get());
            targetUnit.setFlipped(movingUnit.isFlipped() && targetUnit.isFlipped());
            this.getGame().setSelectedPosition(targetPos);
        } else {
            System.out.printf(StringConstants.FMT_UNION_FAILED, targetUnit.getName());
            board.removeAndMove(sourcePos, targetPos);
            getPlayer(targetUnit.getOwner()).decrementBoardCount();
            this.getGame().setSelectedPosition(targetPos);
        }
    }

    private void handleDuel(Board board, PlacedUnit attacker, PlacedUnit defender, Position source, Position target) {
        String atkStats = String.format(StringConstants.FMT_STATS, attacker.getAttack(), attacker.getDefense());
        String defStats = defender.isKing() ? StringConstants.EMPTY : String.format(StringConstants.FMT_STATS, defender.getAttack(), defender.getDefense());

        System.out.printf(StringConstants.FMT_ATTACKS, attacker.getName(), atkStats, defender.getName(), defStats, target);

        flipUnitIfCovered(attacker, source);
        flipUnitIfCovered(defender, target);

        DuelResult result = calculateDuelDamage(attacker, defender);
        if (result.damage() > 0) {
            applyDamage(result.victim(), result.damage());
        }

        resolveDuelOutcome(board, attacker, defender, source, target, result.atkEliminated(), result.defEliminated(), result.moves());
    }

    private DuelResult calculateDuelDamage(PlacedUnit attacker, PlacedUnit defender) {
        if (defender.isKing()) {
            return new DuelResult(defender.getOwner(), attacker.getAttack(), false, false, false);
        }

        if (defender.isBlocking()) {
            if (attacker.getAttack() > defender.getDefense()) {
                return new DuelResult(null, 0, false, true, true);
            } else if (attacker.getAttack() < defender.getDefense()) {
                return new DuelResult(attacker.getOwner(), defender.getDefense() - attacker.getAttack(), false, false, false);
            }
            return new DuelResult(null, 0, false, false, false);
        }

        if (attacker.getAttack() > defender.getAttack()) {
            return new DuelResult(defender.getOwner(), attacker.getAttack() - defender.getAttack(), false, true, true);
        } else if (attacker.getAttack() < defender.getAttack()) {
            return new DuelResult(attacker.getOwner(), defender.getAttack() - attacker.getAttack(), true, false, false);
        }
        return new DuelResult(null, 0, true, true, false);
    }

    private void flipUnitIfCovered(PlacedUnit unit, Position pos) {
        if (!unit.isFlipped() && !unit.isKing()) {
            unit.setFlipped(true);
            System.out.printf(StringConstants.FMT_FLIPPED, unit.getName(), unit.getAttack(), unit.getDefense(), pos);
        }
    }

    private void applyDamage(PlayerType victimType, int damage) {
        Player victim = getPlayer(victimType);
        victim.takeDamage(damage);
        System.out.printf(StringConstants.FMT_DAMAGE, victimType.getDisplayName(), damage);
        if (victim.isDefeated()) {
            System.out.printf(StringConstants.FMT_DROPPED_ZERO, victimType.getDisplayName());
            PlayerType winner = victimType == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
            System.out.printf(StringConstants.FMT_WINS, winner.getDisplayName());
            this.getGame().quit();
        }
    }

    private void resolveDuelOutcome(Board board, PlacedUnit attacker, PlacedUnit defender, Position source, Position target, boolean atkElim, boolean defElim, boolean moves) {
        if (defElim) {
            System.out.printf(StringConstants.FMT_ELIMINATED, defender.getName());
            board.removeUnit(target);
            getPlayer(defender.getOwner()).decrementBoardCount();
        }
        if (atkElim) {
            System.out.printf(StringConstants.FMT_ELIMINATED, attacker.getName());
            board.removeUnit(source);
            getPlayer(attacker.getOwner()).decrementBoardCount();
            this.getGame().setSelectedPosition(null);
        }
        if (moves && !atkElim) {
            System.out.printf(StringConstants.FMT_MOVES_TO, attacker.getName(), target);
            board.moveUnit(source, target);
            this.getGame().setSelectedPosition(target);
        } else if (!atkElim) {
            this.getGame().setSelectedPosition(source);
        }
    }

    private Player getPlayer(PlayerType type) {
        return type == PlayerType.PLAYER ? this.getGame().getHumanPlayer() : this.getGame().getEnemyPlayer();
    }

    private void printBoard(Position highlight) {
        if (this.getGame().isRunning()) {
            System.out.print(BoardFormatter.formatBoard(this.getGame().getBoard(), highlight));
        }
    }

    private record DuelResult(PlayerType victim, int damage, boolean atkEliminated, boolean defEliminated, boolean moves) {}
}