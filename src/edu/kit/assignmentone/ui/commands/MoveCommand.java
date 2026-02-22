package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.units.UnitCombiner;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.Optional;

public class MoveCommand extends Command {

    private static final String COMMAND_REGEX = "move [a-zA-Z]\\d";
    private static final String ERROR_NO_SELECTION = "No field selected or selected field is empty.";
    private static final String ERROR_ALREADY_MOVED = "This unit has already moved this turn.";
    private static final String ERROR_INVALID_DISTANCE = "Invalid move distance. Only 1 step horizontally/vertically or en place allowed.";
    private static final String ERROR_KING_MOVE = "Invalid king move. Kings cannot attack or be attacked by own king.";
    private static final String KING_NAME = "Farmer King";

    public MoveCommand(Game game) {
        super(COMMAND_REGEX, game);
    }

    @Override
    public void execute(String[] arguments) {
        Position sourcePos = this.getGame().getSelectedPosition();
        if (sourcePos == null || this.getGame().getBoard().isEmpty(sourcePos)) {
            throw new IllegalStateException(ERROR_NO_SELECTION);
        }

        Position targetPos = Position.fromString(arguments[0]);
        PlacedUnit movingUnit = this.getGame().getBoard().getUnitAt(sourcePos).get();

        validateMovementRules(movingUnit, sourcePos, targetPos);

        if (movingUnit.isBlocking()) {
            movingUnit.setBlocking(false);
            System.out.println(movingUnit.getUnit().name() + " no longer blocks.");
        }

        Optional<PlacedUnit> targetUnitOpt = this.getGame().getBoard().getUnitAt(targetPos);
        int distance = sourcePos.distanceTo(targetPos);

        if (distance == 0) {
            movingUnit.setMoved(true);
            System.out.println(movingUnit.getUnit().name() + " moves to " + targetPos + ".");
            printBoard(targetPos);
            return;
        }

        if (targetUnitOpt.isEmpty()) {
            this.getGame().getBoard().moveUnit(sourcePos, targetPos);
            movingUnit.setMoved(true);
            this.getGame().setSelectedPosition(targetPos);
            System.out.println(movingUnit.getUnit().name() + " moves to " + targetPos + ".");
        } else {
            PlacedUnit targetUnit = targetUnitOpt.get();
            if (movingUnit.getOwner() == targetUnit.getOwner()) {
                handleUnion(movingUnit, targetUnit, sourcePos, targetPos);
            } else {
                handleDuel(movingUnit, targetUnit, sourcePos, targetPos);
            }
            movingUnit.setMoved(true);
        }

        printBoard(this.getGame().getSelectedPosition());
    }

    private void validateMovementRules(PlacedUnit movingUnit, Position sourcePos, Position targetPos) {
        if (movingUnit.hasMoved()) {
            throw new IllegalStateException(ERROR_ALREADY_MOVED);
        }
        int distance = sourcePos.distanceTo(targetPos);
        if (distance > 1) {
            throw new IllegalStateException(ERROR_INVALID_DISTANCE);
        }
        Optional<PlacedUnit> targetUnitOpt = this.getGame().getBoard().getUnitAt(targetPos);
        if (targetUnitOpt.isPresent()) {
            PlacedUnit targetUnit = targetUnitOpt.get();
            boolean isMovingKing = movingUnit.getUnit().name().equals(KING_NAME);
            boolean isTargetKing = targetUnit.getUnit().name().equals(KING_NAME);

            if (isTargetKing && movingUnit.getOwner() == targetUnit.getOwner()) {
                throw new IllegalStateException(ERROR_KING_MOVE);
            }
            if (isMovingKing && movingUnit.getOwner() != targetUnit.getOwner()) {
                throw new IllegalStateException(ERROR_KING_MOVE);
            }
        }
    }

    private void handleUnion(PlacedUnit movingUnit, PlacedUnit targetUnit, Position sourcePos, Position targetPos) {
        System.out.println(movingUnit.getUnit().name() + " moves to " + targetPos + ".");
        System.out.println(movingUnit.getUnit().name() + " and " + targetUnit.getUnit().name() + " on " + targetPos + " join forces!");

        Optional<Unit> combinedOpt = UnitCombiner.tryCombine(movingUnit.getUnit(), targetUnit.getUnit());

        if (combinedOpt.isPresent()) {
            System.out.println("Success!");
            this.getGame().getBoard().removeUnit(sourcePos);
            targetUnit.setUnit(combinedOpt.get());
            targetUnit.setFlipped(movingUnit.isFlipped() && targetUnit.isFlipped());
            this.getGame().setSelectedPosition(targetPos);
        } else {
            System.out.println("Union failed. " + targetUnit.getUnit().name() + " was eliminated.");
            this.getGame().getBoard().removeUnit(targetPos);
            this.getGame().getBoard().moveUnit(sourcePos, targetPos);
            getPlayer(targetUnit.getOwner()).decrementBoardCount();
            this.getGame().setSelectedPosition(targetPos);
        }
    }

    private void handleDuel(PlacedUnit attacker, PlacedUnit defender, Position sourcePos, Position targetPos) {
        boolean isDefenderKing = defender.getUnit().name().equals(KING_NAME);

        String atkStats = String.format("(%d/%d)", attacker.getUnit().attack(), attacker.getUnit().defense());
        String defStats = isDefenderKing ? "" : String.format(" (%d/%d)", defender.getUnit().attack(), defender.getUnit().defense());

        System.out.println(attacker.getUnit().name() + " " + atkStats + " attacks " + defender.getUnit().name() + defStats + " on " + targetPos + "!");

        flipUnitIfCovered(attacker, sourcePos);
        flipUnitIfCovered(defender, targetPos);

        int damage;
        boolean attackerEliminated = false;
        boolean defenderEliminated = false;
        boolean attackerMovesToTarget = false;

        if (isDefenderKing) {
            damage = attacker.getUnit().attack();
            applyDamage(defender.getOwner(), damage);
        } else if (defender.isBlocking()) {
            if (attacker.getUnit().attack() > defender.getUnit().defense()) {
                defenderEliminated = true;
                attackerMovesToTarget = true;
            } else if (attacker.getUnit().attack() < defender.getUnit().defense()) {
                damage = defender.getUnit().defense() - attacker.getUnit().attack();
                applyDamage(attacker.getOwner(), damage);
            }
        } else {
            if (attacker.getUnit().attack() > defender.getUnit().attack()) {
                damage = attacker.getUnit().attack() - defender.getUnit().attack();
                defenderEliminated = true;
                attackerMovesToTarget = true;
                applyDamage(defender.getOwner(), damage);
            } else if (attacker.getUnit().attack() < defender.getUnit().attack()) {
                damage = defender.getUnit().attack() - attacker.getUnit().attack();
                attackerEliminated = true;
                applyDamage(attacker.getOwner(), damage);
            } else {
                attackerEliminated = true;
                defenderEliminated = true;
            }
        }

        resolveDuelOutcome(attacker, defender, sourcePos, targetPos, attackerEliminated, defenderEliminated, attackerMovesToTarget);
    }

    private void flipUnitIfCovered(PlacedUnit unit, Position pos) {
        if (!unit.isFlipped() && !unit.getUnit().name().equals(KING_NAME)) {
            unit.setFlipped(true);
            System.out.printf("%s (%d/%d) was flipped on %s!%n", unit.getUnit().name(), unit.getUnit().attack(), unit.getUnit().defense(), pos);
        }
    }

    private void applyDamage(PlayerType victimType, int damage) {
        if (damage > 0) {
            Player victim = getPlayer(victimType);
            victim.takeDamage(damage);
            System.out.printf("%s takes %d damage!%n", victimType.getDisplayName(), damage);
            if (victim.isDefeated()) {
                System.out.printf("%s's life points dropped to 0!%n", victimType.getDisplayName());
                PlayerType winner = victimType == PlayerType.PLAYER ? PlayerType.ENEMY : PlayerType.PLAYER;
                System.out.printf("%s wins!%n", winner.getDisplayName());
                this.getGame().quit();
            }
        }
    }

    private void resolveDuelOutcome(PlacedUnit attacker, PlacedUnit defender, Position sourcePos, Position targetPos, boolean atkElim, boolean defElim, boolean moves) {
        if (defElim) {
            System.out.println(defender.getUnit().name() + " was eliminated!");
            this.getGame().getBoard().removeUnit(targetPos);
            getPlayer(defender.getOwner()).decrementBoardCount();
        }
        if (atkElim) {
            System.out.println(attacker.getUnit().name() + " was eliminated!");
            this.getGame().getBoard().removeUnit(sourcePos);
            getPlayer(attacker.getOwner()).decrementBoardCount();
            this.getGame().setSelectedPosition(null);
        }
        if (moves && !atkElim) {
            System.out.println(attacker.getUnit().name() + " moves to " + targetPos + ".");
            this.getGame().getBoard().moveUnit(sourcePos, targetPos);
            this.getGame().setSelectedPosition(targetPos);
        } else if (!atkElim) {
            this.getGame().setSelectedPosition(sourcePos);
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