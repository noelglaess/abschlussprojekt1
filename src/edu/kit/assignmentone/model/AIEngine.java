package edu.kit.assignmentone.model;

import edu.kit.assignmentone.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.units.UnitCombiner;
import edu.kit.assignmentone.ui.BoardFormatter;
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

        List<Position> options = kingPos.getAdjacentAndCenter();
        Position chosen = evaluateKingMoves(board, kingPos, options, game);

        game.setSelectedPosition(kingPos);
        executeCommandSilently(new MoveCommand(game), new String[]{chosen.toString()});
    }

    private static Position evaluateKingMoves(Board board, Position kingPos, List<Position> options, Game game) {
        int maxScore = Integer.MIN_VALUE;
        List<Position> bestOptions = new ArrayList<>();

        for (Position pos : options) {
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

        if (bestOptions.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestOptions.size(); i++) weights.add(WEIGHT_DEFAULT);
            return bestOptions.get(RandomUtils.weightedRandom(weights, game.getRandom()));
        }
        return bestOptions.getFirst();
    }

    private static void placeUnit(Game game) {
        Board board = game.getBoard();
        Player enemy = game.getEnemyPlayer();
        if (enemy.getHand().isEmpty() || enemy.getBoardCount() >= enemy.getMaxBoardCapacity()) return;

        Position kingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.ENEMY);
        Position playerKingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);
        if (kingPos == null || playerKingPos == null) return;

        List<Position> validFields = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        evaluatePlacementFields(board, kingPos, playerKingPos, validFields, scores);

        if (validFields.isEmpty()) return;

        Position chosenField = selectBestPlacementField(validFields, scores, game);
        int chosenUnitIdx = selectUnitToPlace(enemy, game);

        game.setSelectedPosition(chosenField);
        executeCommandSilently(new PlaceCommand(game), new String[]{String.valueOf(chosenUnitIdx + 1)});
    }

    private static void evaluatePlacementFields(Board board, Position kingPos, Position playerKingPos, List<Position> validFields, List<Integer> scores) {
        for (int[] dir : DIR_8) {
            Position pos = new Position(kingPos.col() + dir[0], kingPos.row() + dir[1]);
            if (Position.isValid(pos.col(), pos.row())) {
                Optional<PlacedUnit> unitOpt = board.getUnitAt(pos);
                if (unitOpt.isEmpty() || unitOpt.get().getOwner() == PlayerType.ENEMY) {
                    int steps = pos.distanceTo(playerKingPos);
                    int enemies = board.countUnits(pos, DIR_4, PlayerType.PLAYER, null);
                    int fellows = board.countUnits(pos, DIR_4, PlayerType.ENEMY, null);
                    int score = -steps + ENEMY_MULTIPLIER * enemies - fellows;

                    validFields.add(pos);
                    scores.add(score);
                }
            }
        }
    }

    private static Position selectBestPlacementField(List<Position> validFields, List<Integer> scores, Game game) {
        int maxScore = Integer.MIN_VALUE;
        List<Position> bestFields = new ArrayList<>();
        for (int i = 0; i < validFields.size(); i++) {
            if (scores.get(i) > maxScore) {
                maxScore = scores.get(i);
                bestFields.clear();
                bestFields.add(validFields.get(i));
            } else if (scores.get(i) == maxScore) {
                bestFields.add(validFields.get(i));
            }
        }

        if (bestFields.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestFields.size(); i++) weights.add(WEIGHT_DEFAULT);
            return bestFields.get(RandomUtils.weightedRandom(weights, game.getRandom()));
        }
        return bestFields.getFirst();
    }

    private static int selectUnitToPlace(Player enemy, Game game) {
        List<Integer> handWeights = new ArrayList<>();
        for (Unit u : enemy.getHand()) {
            handWeights.add(u.attack());
        }
        return RandomUtils.weightedRandom(handWeights, game.getRandom());
    }

    private static void moveUnits(Game game) {
        Board board = game.getBoard();
        Position playerKingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);
        if (playerKingPos == null) return;

        while (true) {
            List<Position> unmovedUnits = getUnmovedEnemyUnits(board);
            if (unmovedUnits.isEmpty()) break;
            executeBestUnitAction(game, board, unmovedUnits, playerKingPos);
        }
    }

    private static List<Position> getUnmovedEnemyUnits(Board board) {
        List<Position> unmovedUnits = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(col, row);
                Optional<PlacedUnit> opt = board.getUnitAt(pos);
                if (opt.isPresent() && opt.get().getOwner() == PlayerType.ENEMY
                        && !opt.get().hasMoved() && !opt.get().isKing()) {
                    unmovedUnits.add(pos);
                }
            }
        }
        return unmovedUnits;
    }

    private static void executeBestUnitAction(Game game, Board board, List<Position> unmovedUnits, Position playerKingPos) {
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

    private static UnitScoreResult evaluateUnitOptions(Board board, PlacedUnit unit, Position pos, Position playerKingPos) {
        List<Integer> optionScores = new ArrayList<>();
        List<Integer> validIndices = new ArrayList<>();
        int totalScore = 0;

        for (int dirIdx = 0; dirIdx < 4; dirIdx++) {
            Position target = new Position(pos.col() + DIR_4[dirIdx][0], pos.row() + DIR_4[dirIdx][1]);
            if (!Position.isValid(target.col(), target.row())) continue;

            Optional<PlacedUnit> targetOpt = board.getUnitAt(target);
            if (targetOpt.isPresent() && targetOpt.get().isKing() && targetOpt.get().getOwner() == PlayerType.ENEMY) {
                continue;
            }

            int score = calculateTargetScore(unit, targetOpt, target, playerKingPos, board);
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

    private static int calculateTargetScore(PlacedUnit unit, Optional<PlacedUnit> targetOpt, Position target, Position playerKingPos, Board board) {
        if (targetOpt.isEmpty()) {
            int steps = target.distanceTo(playerKingPos);
            int enemies = board.countUnits(target, DIR_4, PlayerType.PLAYER, null);
            return BASE_SCORE - steps - enemies;
        }

        PlacedUnit targetUnit = targetOpt.get();
        if (targetUnit.getOwner() == PlayerType.ENEMY) {
            Optional<Unit> combined = UnitCombiner.tryCombine(unit.getUnit(), targetUnit.getUnit());
            if (combined.isPresent()) {
                return combined.get().attack() + combined.get().defense() - unit.getAttack() - unit.getDefense();
            }
            return -targetUnit.getAttack() - targetUnit.getDefense();
        }

        if (targetUnit.isKing()) {
            return unit.getAttack();
        } else if (!targetUnit.isFlipped()) {
            return unit.getAttack() - FLIPPED_PENALTY;
        } else if (targetUnit.isBlocking()) {
            return unit.getAttack() - targetUnit.getDefense();
        }
        return ENEMY_MULTIPLIER * (unit.getAttack() - targetUnit.getAttack());
    }

    private static void performChosenAction(Game game, Board board, Position bestUnitPos, List<Integer> scores, List<Integer> validIndices) {
        PlacedUnit activeUnit = board.getUnitAt(bestUnitPos).orElseThrow();
        boolean hasPositive = false;
        for (int score : scores) {
            if (score > 0) hasPositive = true;
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

    private static void executeCommandSilently(edu.kit.assignmentone.ui.commands.Command command, String[] args) {
        try {
            command.execute(args);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
            // Internal execution ignores format exceptions
        }
    }

    private record UnitScoreResult(int totalScore, List<Integer> optionScores, List<Integer> validIndices) {}
}