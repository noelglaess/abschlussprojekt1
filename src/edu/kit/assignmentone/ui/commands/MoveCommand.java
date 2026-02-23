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
 * @author uXXXXX
 * @version 1.0
 */
public class MoveCommand extends Command {

    /**
     * Creates a new move command.
     * @param game The game
     */
    public MoveCommand(Game game) {
        super(StringConstants.REGEX_MOVE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game game = this.getGame();
        Board board = game.getBoard();
        Position sourcePos = game.getSelectedPosition();

        if (sourcePos == null || board.isEmpty(sourcePos)) {
            throw new IllegalStateException(StringConstants.ERR_NO_SELECTION);
        }

        Position targetPos = Position.fromString(arguments[0]);
        PlacedUnit movingUnit = board.getUnitAt(sourcePos).orElseThrow();
        PlacedUnit targetUnit = board.getUnitAt(targetPos).orElse(null);

        movingUnit.requireValidMove(sourcePos.distanceTo(targetPos), targetUnit);

        if (movingUnit.unblockIfBlocking()) {
            System.out.printf(StringConstants.FMT_NO_LONGER_BLOCKS, movingUnit.getName());
        }

        if (sourcePos.distanceTo(targetPos) == 0) {
            movingUnit.setMoved(true);
            System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), targetPos);
        } else {
            executeMovement(board, movingUnit, targetUnit, sourcePos, targetPos);
        }

        if (game.isRunning()) {
            System.out.print(BoardFormatter.formatBoard(board, game.getSelectedPosition()));
        }
    }

    private void executeMovement(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position source, Position target) {
        Game game = this.getGame();
        if (targetUnit == null) {
            board.moveUnit(source, target);
            game.setSelectedPosition(target);
            System.out.printf(StringConstants.FMT_MOVES_TO, movingUnit.getName(), target);
        } else if (movingUnit.getOwner() == targetUnit.getOwner()) {
            handleUnion(board, movingUnit, targetUnit, source, target);
        } else {
            handleDuel(board, movingUnit, targetUnit, source, target);
        }
        movingUnit.setMoved(true);
    }

    private void handleUnion(Board board, PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePos, Position targetPos) {
        Game game = this.getGame();
        String mName = movingUnit.getName();
        String tName = targetUnit.getName();
        System.out.printf(StringConstants.FMT_MOVES_TO, mName, targetPos);
        System.out.printf(StringConstants.FMT_JOIN_FORCES, mName, tName, targetPos);

        Optional<Unit> combinedOpt = movingUnit.getUnit().combineWith(targetUnit.getUnit());

        if (combinedOpt.isPresent()) {
            System.out.println(StringConstants.SUCCESS_MSG);
            board.removeUnit(sourcePos);
            targetUnit.setUnit(combinedOpt.get());
            targetUnit.setFlipped(movingUnit.isFlipped() && targetUnit.isFlipped());
            game.setSelectedPosition(targetPos);
        } else {
            System.out.printf(StringConstants.FMT_UNION_FAILED, tName);
            board.removeUnit(targetPos);
            board.moveUnit(sourcePos, targetPos);
            getPlayer(targetUnit.getOwner()).decrementBoardCount();
            game.setSelectedPosition(targetPos);
        }
    }

    private void handleDuel(Board board, PlacedUnit attacker, PlacedUnit defender, Position source, Position target) {
        String atkName = attacker.getName();
        int atkAtk = attacker.getAttack();
        int atkDef = attacker.getDefense();
        String defName = defender.getName();
        int defAtk = defender.getAttack();
        int defDef = defender.getDefense();

        String atkStats = String.format(StringConstants.FMT_STATS, atkAtk, atkDef);
        String defStats = defender.isKing() ? StringConstants.EMPTY :
                String.format(StringConstants.FMT_STATS, defAtk, defDef);

        System.out.printf(StringConstants.FMT_ATTACKS, atkName, atkStats, defName, defStats, target);

        if (attacker.flipIfCovered()) {
            System.out.printf(StringConstants.FMT_FLIPPED, atkName, atkAtk, atkDef, source);
        }
        if (defender.flipIfCovered()) {
            System.out.printf(StringConstants.FMT_FLIPPED, defName, defAtk, defDef, target);
        }

        DuelResult result = attacker.fightAgainst(defender);
        int dmg = result.damage();
        PlayerType victimType = result.victim();

        if (dmg > 0) {
            Player victim = getPlayer(victimType);
            String vName = victimType.getDisplayName();
            System.out.printf(StringConstants.FMT_DAMAGE, vName, dmg);
            if (victim.takeDamageAndCheckDefeat(dmg)) {
                System.out.printf(StringConstants.FMT_DROPPED_ZERO, vName);
                PlayerType winner = victimType == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
                System.out.printf(StringConstants.FMT_WINS, winner.getDisplayName());
                this.getGame().quit();
            }
        }

        resolveOutcome(board, atkName, defName, defender.getOwner(), attacker.getOwner(), source, target, result);
    }

    private void resolveOutcome(Board board, String aName, String dName, PlayerType dOwner, PlayerType aOwner, Position src, Position tgt, DuelResult result) {
        Game game = this.getGame();
        boolean dElim = result.defEliminated();
        boolean aElim = result.atkEliminated();

        if (dElim) {
            System.out.printf(StringConstants.FMT_ELIMINATED, dName);
            board.removeUnit(tgt);
            getPlayer(dOwner).decrementBoardCount();
        }
        if (aElim) {
            System.out.printf(StringConstants.FMT_ELIMINATED, aName);
            board.removeUnit(src);
            getPlayer(aOwner).decrementBoardCount();
            game.setSelectedPosition(null);
        }
        if (result.moves() && !aElim) {
            System.out.printf(StringConstants.FMT_MOVES_TO, aName, tgt);
            board.moveUnit(src, tgt);
            game.setSelectedPosition(tgt);
        } else if (!aElim) {
            game.setSelectedPosition(src);
        }
    }

    private Player getPlayer(PlayerType type) {
        Game game = this.getGame();
        return type == PlayerType.PLAYER ? game.getHumanPlayer() : game.getEnemyPlayer();
    }
}