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
 * Handles movement, unblocking, unions, and duels.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class MoveCommand extends Command {

    /**
     * Creates a new move command.
     * @param game The game to execute the command on
     */
    public MoveCommand(Game game) {
        super(StringConstants.REGEX_MOVE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Board board = this.getGame().getBoard();
        Position sourcePos = this.getGame().getSelectedPosition();

        if (sourcePos == null || board.isEmpty(sourcePos)) {
            throw new IllegalStateException(StringConstants.ERR_NO_SELECTION);
        }

        Position targetPos = Position.fromString(arguments[0]);
        PlacedUnit movingUnit = board.getUnitAt(sourcePos).orElseThrow();

        validateMovementRules(board, movingUnit, sourcePos, targetPos);

        if (movingUnit.unblockIfBlocking()) {
            System.out.printf(StringConstants.FMT_NO_LONGER_BLOCKS, movingUnit.getName());
        }

        if (sourcePos.distanceTo(targetPos) == 0) {
            movingUnit.setMoved(true);
            System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), targetPos);
            printBoard(targetPos);
            return;
        }

        executeMovement(board, movingUnit, sourcePos, targetPos);
        printBoard(this.getGame().getSelectedPosition());
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
        if (movingUnit.isMoved()) {
            throw new IllegalStateException(StringConstants.ERR_ALREADY_MOVED);
        }
        if (sourcePos.distanceTo(targetPos) > 1) {
            throw new IllegalStateException(StringConstants.ERR_MOVE_DIST);
        }
        Optional<PlacedUnit> targetUnitOpt = board.getUnitAt(targetPos);
        if (targetUnitOpt.isPresent()) {
            PlacedUnit targetUnit = targetUnitOpt.get();
            if (targetUnit.isKing() && movingUnit.getOwner() == targetUnit.getOwner()) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
            if (movingUnit.isKing() && movingUnit.getOwner() != targetUnit.getOwner()) {
                throw new IllegalStateException(StringConstants.ERR_KING_MOVE);
            }
        }
    }

    private void handleUnion(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePos, Position targetPos) {
        System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), targetPos);
        System.out.printf(StringConstants.FMT_JOIN_FORCES, movingUnit.getName(), targetUnit.getName(), targetPos);

        Optional<Unit> combinedOpt = movingUnit.getUnit().combineWith(targetUnit.getUnit());

        if (combinedOpt.isPresent()) {
            System.out.println(StringConstants.SUCCESS_MSG);
            board.removeUnit(sourcePos);
            targetUnit.setUnit(combinedOpt.get());
            targetUnit.setFlipped(movingUnit.isFlipped() && targetUnit.isFlipped());
            this.getGame().setSelectedPosition(targetPos);
        } else {
            System.out.printf(StringConstants.FMT_UNION_FAILED, targetUnit.getName());
            board.removeUnit(targetPos);
            board.moveUnit(sourcePos, targetPos);
            getPlayer(targetUnit.getOwner()).decrementBoardCount();
            this.getGame().setSelectedPosition(targetPos);
        }
    }

    private void handleDuel(Board board, PlacedUnit attacker, PlacedUnit defender, Position source, Position target) {
        String atkStats = String.format(StringConstants.FMT_STATS, attacker.getAttack(), attacker.getDefense());
        String defStats = defender.isKing() ? StringConstants.EMPTY : String.format(StringConstants.FMT_STATS, defender.getAttack(), defender.getDefense());

        System.out.printf(StringConstants.FMT_ATTACKS, attacker.getName(), atkStats, defender.getName(), defStats, target);

        if (attacker.flipIfCovered()) {
            System.out.printf(StringConstants.FMT_FLIPPED, attacker.getName(), attacker.getAttack(), attacker.getDefense(), source);
        }
        if (defender.flipIfCovered()) {
            System.out.printf(StringConstants.FMT_FLIPPED, defender.getName(), defender.getAttack(), defender.getDefense(), target);
        }

        DuelResult result = attacker.fightAgainst(defender);
        if (result.damage() > 0) {
            Player victim = getPlayer(result.victim());
            System.out.printf(StringConstants.FMT_DAMAGE, result.victim().getDisplayName(), result.damage());
            if (victim.takeDamageAndCheckDefeat(result.damage())) {
                System.out.printf(StringConstants.FMT_DROPPED_ZERO, result.victim().getDisplayName());
                PlayerType winner = result.victim() == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
                System.out.printf(StringConstants.FMT_WINS, winner.getDisplayName());
                this.getGame().quit();
            }
        }

        resolveDuelOutcome(board, attacker, defender, source, target, result);
    }

    private void resolveDuelOutcome(Board board, PlacedUnit attacker, PlacedUnit defender, Position source, Position target, DuelResult result) {
        if (result.defEliminated()) {
            System.out.printf(StringConstants.FMT_ELIMINATED, defender.getName());
            board.removeUnit(target);
            getPlayer(defender.getOwner()).decrementBoardCount();
        }
        if (result.atkEliminated()) {
            System.out.printf(StringConstants.FMT_ELIMINATED, attacker.getName());
            board.removeUnit(source);
            getPlayer(attacker.getOwner()).decrementBoardCount();
            this.getGame().setSelectedPosition(null);
        }
        if (result.moves() && !result.atkEliminated()) {
            System.out.printf(StringConstants.FMT_MOVES_TO, attacker.getName(), target);
            board.moveUnit(source, target);
            this.getGame().setSelectedPosition(target);
        } else if (!result.atkEliminated()) {
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
}