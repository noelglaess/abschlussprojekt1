package edu.kit.assignmentone.model;

import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.ui.BoardFormatter;
import edu.kit.assignmentone.ui.commands.Command;
import edu.kit.assignmentone.ui.commands.MoveCommand;
import edu.kit.assignmentone.ui.commands.PlaceCommand;
import edu.kit.assignmentone.ui.commands.YieldCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the fully automated turn for the AI Enemy.
 *
 * @author uXXXXX
 * @version 1.0
 */
public final class AIEngine {

    private static final int HUNDRED = 100;
    private static final int DIR_COUNT = 4;
    private static final int[][] DIR_4 = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    private AIEngine() { }

    /**
     * Executes all phases of the AI turn.
     *
     * @param game The game instance
     */
    public static void playTurn(Game game) {
        if (game.isRunning()) {
            moveKing(game);
        }
        if (game.isRunning()) {
            placeUnit(game);
        }
        if (game.isRunning()) {
            moveUnits(game);
        }
        if (game.isRunning()) {
            endTurn(game);
        }
    }

    private static void moveKing(Game game) {
        Board board = game.getBoard();
        Position kingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.ENEMY);
        if (kingPos != null) {
            Position chosen = board.findBestKingMove(kingPos, game.getRandom());
            game.setSelectedPosition(kingPos);
            executeSilently(new MoveCommand(game), new String[]{chosen.toString()});
        }
    }

    private static void placeUnit(Game game) {
        Board board = game.getBoard();
        Player enemy = game.getEnemyPlayer();

        if (!enemy.getHand().isEmpty() && enemy.getBoardCount() < enemy.getMaxBoardCapacity()) {
            Position kingPos = board.findUnit(StringConstants.KING_NAME, PlayerType.ENEMY);
            Position pKing = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);

            if (kingPos != null && pKing != null) {
                Position chosenField = board.findBestPlacement(kingPos, pKing, game.getRandom());
                if (chosenField != null) {
                    int chosenIdx = enemy.pickUnitToPlace(game.getRandom());
                    game.setSelectedPosition(chosenField);
                    executeSilently(new PlaceCommand(game), new String[]{String.valueOf(chosenIdx + 1)});
                }
            }
        }
    }

    private static void moveUnits(Game game) {
        Board board = game.getBoard();
        Position pKing = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);
        if (pKing != null) {
            while (true) {
                List<Position> unmoved = board.getUnmovedEnemyUnits();
                if (unmoved.isEmpty()) {
                    break;
                }

                Position bestPos = null;
                int bestTotal = Integer.MIN_VALUE;
                List<Integer> bestScores = new ArrayList<>();
                List<Integer> bestIndices = new ArrayList<>();

                for (Position pos : unmoved) {
                    UnitScoreResult res = evaluateUnit(board, pos, pKing);
                    if (res.totalScore() > bestTotal) {
                        bestTotal = res.totalScore();
                        bestPos = pos;
                        bestScores = res.optionScores();
                        bestIndices = res.validIndices();
                    }
                }
                if (bestPos != null) {
                    game.setSelectedPosition(bestPos);
                    performAction(game, board, bestPos, bestScores, bestIndices);
                }
            }
        }
    }

    private static UnitScoreResult evaluateUnit(Board board, Position pos, Position pKing) {
        PlacedUnit unit = board.getUnitAt(pos).orElseThrow();
        List<Integer> scores = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int total = 0;

        for (int i = 0; i < DIR_COUNT; i++) {
            Position target = pos.translate(DIR_4[i][0], DIR_4[i][1]);
            if (target != null) {
                PlacedUnit tUnit = board.getUnitAt(target).orElse(null);
                if (tUnit == null || !tUnit.isKing() || tUnit.getOwner() != PlayerType.ENEMY) {
                    int scoreVal = board.evaluateTargetScore(pos, target, pKing);
                    scores.add(scoreVal);
                    indices.add(i);
                    total += scoreVal;
                }
            }
        }

        int maxAtk = board.getMaxSurroundingEnemyAtk(pos);
        int blockScore = Math.max(1, (unit.getDefense() - maxAtk) / HUNDRED);
        scores.add(blockScore);
        indices.add(4);
        total += blockScore;

        int inPlaceScore = Math.max(0, (unit.getAttack() - maxAtk) / HUNDRED);
        scores.add(inPlaceScore);
        indices.add(5);
        total += inPlaceScore;

        return new UnitScoreResult(total, scores, indices);
    }

    private static void performAction(Game game, Board board, Position pos, List<Integer> scores, List<Integer> idxs) {
        PlacedUnit unit = board.getUnitAt(pos).orElseThrow();
        boolean hasPos = false;
        for (int scoreVal : scores) {
            if (scoreVal > 0) {
                hasPos = true;
                break;
            }
        }

        if (!hasPos) {
            blockUnit(board, unit, pos);
        } else {
            int chosenIdx = RandomUtils.weightedRandom(scores, game.getRandom());
            int act = idxs.get(chosenIdx);
            if (act < DIR_COUNT) {
                Position target = pos.translate(DIR_4[act][0], DIR_4[act][1]);
                if (target != null) {
                    executeSilently(new MoveCommand(game), new String[]{target.toString()});
                }
            } else if (act == DIR_COUNT) {
                blockUnit(board, unit, pos);
            } else {
                executeSilently(new MoveCommand(game), new String[]{pos.toString()});
            }
        }
    }

    private static void blockUnit(Board board, PlacedUnit unit, Position pos) {
        unit.block();
        System.out.printf(StringConstants.FMT_BLOCKS, unit.getName(), pos);
        System.out.print(BoardFormatter.formatBoard(board, pos));
    }

    private static void endTurn(Game game) {
        Player enemy = game.getEnemyPlayer();
        if (enemy.hasFullHand()) {
            int dropIdx = enemy.pickUnitToDiscard(game.getRandom());
            executeSilently(new YieldCommand(game), new String[]{String.valueOf(dropIdx + 1)});
        } else {
            executeSilently(new YieldCommand(game), new String[0]);
        }
    }

    private static void executeSilently(Command cmd, String[] args) {
        try {
            cmd.execute(args);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
            // Ignored silently for AI
        }
    }

    private record UnitScoreResult(int totalScore, List<Integer> optionScores, List<Integer> validIndices) {}
}