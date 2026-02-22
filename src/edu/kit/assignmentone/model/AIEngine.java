package edu.kit.assignmentone.model;

import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.ui.BoardFormatter;
import edu.kit.assignmentone.ui.commands.Command;
import edu.kit.assignmentone.ui.commands.MoveCommand;
import edu.kit.assignmentone.ui.commands.PlaceCommand;
import edu.kit.assignmentone.ui.commands.YieldCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the fully automated turn for the AI Enemy.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public final class AIEngine {

    private static final int[][] DIR_4 = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static final int[][] DIR_8 = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};

    private static final int HUNDRED = 100;
    private static final int FLIPPED_PENALTY = 500;
    private static final int BASE_SCORE = 10;
    private static final int ENEMY_MULTIPLIER = 2;
    private static final int FELLOW_MULTIPLIER = 3;
    private static final int BOARD_SIZE = 7;
    private static final int MAX_HAND_SIZE = 5;
    private static final int WEIGHT_DEFAULT = 1;
    private static final int ACTION_BLOCK = 4;
    private static final int ACTION_IN_PLACE = 5;

    private AIEngine() {
        throw new UnsupportedOperationException(StringConstants.UTILITY_CLASS_ERROR);
    }

    /**
     * Executes all phases of the AI turn.
     *
     * @param game The game instance
     */
    public static void playTurn(Game game) {
        if (!game.isRunning()) return;
        moveKing(game);
        if (!game.isRunning()) return;
        placeUnit(game);
        moveUnits(game);
        if (game.isRunning()) endTurn(game);
    }

    private static void moveKing(Game game) {
        Board board = game.getBoard();
        Position kingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.ENEMY);
        if (kingPos == null) return;

        int maxScore = Integer.MIN_VALUE;
        List<Position> bestOptions = new ArrayList<>();

        for (Position pos : kingPos.getAdjacentAndCenter()) {
            if (!Position.isValid(pos.col(), pos.row())) continue;

            Optional<PlacedUnit> unitOpt = board.getUnitAt(pos);
            if (unitOpt.isPresent() && unitOpt.get().getOwner() == PlayerType.PLAYER) continue;

            int fellows = board.countUnits(pos, DIR_8, PlayerType.ENEMY, kingPos);
            int enemies = board.countUnits(pos, DIR_8, PlayerType.PLAYER, null);
            int distance = kingPos.distanceTo(pos);
            int fellowPresent = (unitOpt.isPresent() && unitOpt.get().getOwner() == PlayerType.ENEMY && !pos.equals(kingPos)) ? 1 : 0;

            int score = fellows - ENEMY_MULTIPLIER * enemies - distance - FELLOW_MULTIPLIER * fellowPresent;

            if (score > maxScore) {
                maxScore = score;
                bestOptions.clear();
                bestOptions.add(pos);
            } else if (score == maxScore) {
                bestOptions.add(pos);
            }
        }

        Position chosen = bestOptions.getFirst();
        if (bestOptions.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestOptions.size(); i++) weights.add(WEIGHT_DEFAULT);
            chosen = bestOptions.get(RandomUtils.weightedRandom(weights, game.getRandom()));
        }

        game.setSelectedPosition(kingPos);
        executeCommandSilently(new MoveCommand(game), new String[]{chosen.toString()});
    }

    private static void placeUnit(Game game) {
        Board board = game.getBoard();
        Player enemy = game.getEnemyPlayer();
        if (enemy.getHand().isEmpty() || enemy.getBoardCount() >= enemy.getMaxBoardCapacity()) return;

        Position kingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.ENEMY);
        Position playerKingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);
        if (kingPos == null || playerKingPos == null) return;

        int maxScore = Integer.MIN_VALUE;
        List<Position> bestFields = new ArrayList<>();

        for (int[] dir : DIR_8) {
            Position pos = new Position(kingPos.col() + dir[0], kingPos.row() + dir[1]);
            if (Position.isValid(pos.col(), pos.row()) && (board.isEmpty(pos) || board.getUnitAt(pos).get().getOwner() == PlayerType.ENEMY)) {
                int steps = pos.distanceTo(playerKingPos);
                int enemies = board.countUnits(pos, DIR_4, PlayerType.PLAYER, null);
                int fellows = board.countUnits(pos, DIR_4, PlayerType.ENEMY, null);
                int score = -steps + ENEMY_MULTIPLIER * enemies - fellows;

                if (score > maxScore) {
                    maxScore = score;
                    bestFields.clear();
                    bestFields.add(pos);
                } else if (score == maxScore) {
                    bestFields.add(pos);
                }
            }
        }

        if (bestFields.isEmpty()) return;

        Position chosenField = bestFields.getFirst();
        if (bestFields.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestFields.size(); i++) weights.add(WEIGHT_DEFAULT);
            chosenField = bestFields.get(RandomUtils.weightedRandom(weights, game.getRandom()));
        }

        List<Integer> handWeights = new ArrayList<>();
        for (Unit u : enemy.getHand()) handWeights.add(u.attack());
        int chosenUnitIdx = RandomUtils.weightedRandom(handWeights, game.getRandom());

        game.setSelectedPosition(chosenField);
        executeCommandSilently(new PlaceCommand(game), new String[]{String.valueOf(chosenUnitIdx + 1)});
    }

    private static void moveUnits(Game game) {
        Board board = game.getBoard();
        Position playerKingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);
        if (playerKingPos == null) return;

        while (true) {
            List<Position> unmovedUnits = new ArrayList<>();
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Position pos = new Position(col, row);
                    Optional<PlacedUnit> opt = board.getUnitAt(pos);
                    if (opt.isPresent() && opt.get().getOwner() == PlayerType.ENEMY
                            && !opt.get().isMoved() && !opt.get().isKing()) {
                        unmovedUnits.add(pos);
                    }
                }
            }

            if (unmovedUnits.isEmpty()) break;

            Position bestUnitPos = null;
            int bestUnitTotalScore = Integer.MIN_VALUE;
            List<Integer> bestUnitOptionScores = new ArrayList<>();
            List<Integer> bestUnitValidIndices = new ArrayList<>();

            for (Position pos : unmovedUnits) {
                PlacedUnit unit = board.getUnitAt(pos).orElseThrow();
                UnitScoreResult result = evaluateUnitOptions(board, unit, pos, playerKingPos);

                if (result.totalScore() > bestUnitTotalScore) {
                    bestUnitTotalScore = result.totalScore();
                    bestUnitPos = pos;
                    bestUnitOptionScores = result.optionScores();
                    bestUnitValidIndices = result.validIndices();
                }
            }

            if (bestUnitPos != null) {
                game.setSelectedPosition(bestUnitPos);
                performChosenAction(game, board, bestUnitPos, bestUnitOptionScores, bestUnitValidIndices);
            }
        }
    }

    private static UnitScoreResult evaluateUnitOptions(Board board, PlacedUnit unit, Position pos, Position playerKingPos) {
        List<Integer> optionScores = new ArrayList<>();
        List<Integer> validIndices = new ArrayList<>();
        int totalScore = 0;

        for (int dirIdx = 0; dirIdx < 4; dirIdx++) {
            Position target = new Position(pos.col() + DIR_4[dirIdx][0], pos.row() + DIR_4[dirIdx][1]);
            if (!Position.isValid(target.col(), target.row())) continue;

            PlacedUnit targetUnit = board.getUnitAt(target).orElse(null);
            if (targetUnit != null && targetUnit.isKing() && targetUnit.getOwner() == PlayerType.ENEMY) continue;

            int score = calculateTargetScore(unit, targetUnit, target, playerKingPos, board);
            optionScores.add(score);
            validIndices.add(dirIdx);
            totalScore += score;
        }

        int maxEnemyAtk = board.getMaxSurroundingEnemyAtk(pos);
        int blockScore = Math.max(1, (unit.getDefense() - maxEnemyAtk) / HUNDRED);
        optionScores.add(blockScore);
        validIndices.add(ACTION_BLOCK);
        totalScore += blockScore;

        int inPlaceScore = Math.max(0, (unit.getAttack() - maxEnemyAtk) / HUNDRED);
        optionScores.add(inPlaceScore);
        validIndices.add(ACTION_IN_PLACE);
        totalScore += inPlaceScore;

        return new UnitScoreResult(totalScore, optionScores, validIndices);
    }

    private static int calculateTargetScore(PlacedUnit unit, PlacedUnit targetUnit, Position target, Position playerKingPos, Board board) {
        int score;
        if (targetUnit == null) {
            int steps = target.distanceTo(playerKingPos);
            int enemies = board.countUnits(target, DIR_4, PlayerType.PLAYER, null);
            score = BASE_SCORE - steps - enemies;
        } else if (targetUnit.getOwner() == PlayerType.ENEMY) {
            Optional<Unit> combined = unit.getUnit().combineWith(targetUnit.getUnit());
            if (combined.isPresent()) {
                score = combined.get().attack() + combined.get().defense() - unit.getAttack() - unit.getDefense();
            } else {
                score = -targetUnit.getAttack() - targetUnit.getDefense();
            }
        } else {
            if (targetUnit.isKing()) {
                score = unit.getAttack();
            } else if (!targetUnit.isFlipped()) {
                score = unit.getAttack() - FLIPPED_PENALTY;
            } else if (targetUnit.isBlocking()) {
                score = unit.getAttack() - targetUnit.getDefense();
            } else {
                score = ENEMY_MULTIPLIER * (unit.getAttack() - targetUnit.getAttack());
            }
        }
        return score;
    }

    private static void performChosenAction(Game game, Board board, Position bestUnitPos, List<Integer> scores, List<Integer> validIndices) {
        PlacedUnit activeUnit = board.getUnitAt(bestUnitPos).orElseThrow();
        boolean hasPositive = false;
        for (int score : scores) {
            if (score > 0) {
                hasPositive = true;
                break;
            }
        }

        if (!hasPositive) {
            blockUnit(board, activeUnit, bestUnitPos);
        } else {
            int chosenActionIdx = RandomUtils.weightedRandom(scores, game.getRandom());
            int actualAction = validIndices.get(chosenActionIdx);

            if (actualAction < ACTION_BLOCK) {
                Position target = new Position(bestUnitPos.col() + DIR_4[actualAction][0], bestUnitPos.row() + DIR_4[actualAction][1]);
                executeCommandSilently(new MoveCommand(game), new String[]{target.toString()});
            } else if (actualAction == ACTION_BLOCK) {
                blockUnit(board, activeUnit, bestUnitPos);
            } else if (actualAction == ACTION_IN_PLACE) {
                executeCommandSilently(new MoveCommand(game), new String[]{bestUnitPos.toString()});
            }
        }
    }

    private static void blockUnit(Board board, PlacedUnit unit, Position pos) {
        unit.setBlocking(true);
        unit.setMoved(true);
        System.out.printf(StringConstants.FMT_BLOCKS, unit.getName(), pos);
        System.out.print(BoardFormatter.formatBoard(board, pos));
    }

    private static void endTurn(Game game) {
        Player enemy = game.getEnemyPlayer();
        if (enemy.getHand().size() == MAX_HAND_SIZE) {
            List<Integer> weights = new ArrayList<>();
            for (Unit u : enemy.getHand()) {
                weights.add(u.attack() + u.defense());
            }
            int discardIdx = RandomUtils.reverseWeightedRandom(weights, game.getRandom());
            executeCommandSilently(new YieldCommand(game), new String[]{String.valueOf(discardIdx + 1)});
        } else {
            executeCommandSilently(new YieldCommand(game), new String[0]);
        }
    }

    private static void executeCommandSilently(Command command, String[] args) {
        try {
            command.execute(args);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
            // Internal execution ignores format exceptions
        }
    }

    private record UnitScoreResult(int totalScore, List<Integer> optionScores, List<Integer> validIndices) {}
}