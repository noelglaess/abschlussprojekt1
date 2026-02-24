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
 * @author uqhkm
 * @version 1.0
 */
public final class AIEngine {

    private static final int HUNDRED = 100;
    private static final int DIRECTION_COUNT = 4;
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    private AIEngine() { }

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
        Position kingPosition = board.findUnit(StringConstants.KING_NAME, PlayerType.ENEMY);
        if (kingPosition != null) {
            Position chosenPosition = board.findBestKingMove(kingPosition, game.getRandom());
            game.setSelectedPosition(kingPosition);
            executeSilently(new MoveCommand(game), new String[]{chosenPosition.toString()});
        }
    }

    private static void placeUnit(Game game) {
        Board board = game.getBoard();
        Player enemy = game.getEnemyPlayer();

        if (!enemy.getHand().isEmpty() && enemy.getBoardCount() < enemy.getMaxBoardCapacity()) {
            Position kingPosition = board.findUnit(StringConstants.KING_NAME, PlayerType.ENEMY);
            Position playerKingPosition = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);

            if (kingPosition != null && playerKingPosition != null) {
                Position chosenField = board.findBestPlacement(kingPosition, playerKingPosition, game.getRandom());
                if (chosenField != null) {
                    int chosenIndex = enemy.pickUnitToPlace(game.getRandom());
                    game.setSelectedPosition(chosenField);
                    executeSilently(new PlaceCommand(game), new String[]{String.valueOf(chosenIndex + 1)});
                }
            }
        }
    }

    private static void moveUnits(Game game) {
        Board board = game.getBoard();
        Position playerKingPosition = board.findUnit(StringConstants.KING_NAME, PlayerType.PLAYER);
        if (playerKingPosition != null) {
            while (true) {
                List<Position> unmovedUnits = board.getUnmovedEnemyUnits();
                if (unmovedUnits.isEmpty()) {
                    break;
                }

                Position bestPosition = null;
                int bestTotalScore = Integer.MIN_VALUE;
                List<Integer> bestScores = new ArrayList<>();
                List<Integer> bestIndices = new ArrayList<>();

                for (Position position : unmovedUnits) {
                    UnitScoreResult result = evaluateUnit(board, position, playerKingPosition);
                    if (result.totalScore() > bestTotalScore) {
                        bestTotalScore = result.totalScore();
                        bestPosition = position;
                        bestScores = result.optionScores();
                        bestIndices = result.validIndices();
                    }
                }
                if (bestPosition != null) {
                    game.setSelectedPosition(bestPosition);
                    performAction(game, board, bestPosition, bestScores, bestIndices);
                }
            }
        }
    }

    private static UnitScoreResult evaluateUnit(Board board, Position position, Position playerKingPosition) {
        PlacedUnit unit = board.getUnitAt(position).orElseThrow();
        List<Integer> scores = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int totalScore = 0;

        for (int index = 0; index < DIRECTION_COUNT; index++) {
            Position targetPosition = position.translate(DIRECTIONS[index][0], DIRECTIONS[index][1]);
            if (targetPosition != null) {
                PlacedUnit targetUnit = board.getUnitAt(targetPosition).orElse(null);
                if (targetUnit == null || !targetUnit.isKing() || targetUnit.getOwner() != PlayerType.ENEMY) {
                    int scoreValue = board.evaluateTargetScore(position, targetPosition, playerKingPosition);
                    scores.add(scoreValue);
                    indices.add(index);
                    totalScore += scoreValue;
                }
            }
        }

        int maximumAttack = board.getMaxSurroundingEnemyAtk(position);
        int blockScore = Math.max(1, (unit.getDefense() - maximumAttack) / HUNDRED);
        scores.add(blockScore);
        indices.add(4);
        totalScore += blockScore;

        int inPlaceScore = Math.max(0, (unit.getAttack() - maximumAttack) / HUNDRED);
        scores.add(inPlaceScore);
        indices.add(5);
        totalScore += inPlaceScore;

        return new UnitScoreResult(totalScore, scores, indices);
    }

    private static void performAction(Game game, Board board, Position position, List<Integer> scores, List<Integer> indices) {
        PlacedUnit unit = board.getUnitAt(position).orElseThrow();
        boolean hasPositiveScore = false;
        for (int scoreValue : scores) {
            if (scoreValue > 0) {
                hasPositiveScore = true;
                break;
            }
        }

        if (!hasPositiveScore) {
            blockUnit(board, unit, position);
        } else {
            int chosenIndex = RandomUtils.weightedRandom(scores, game.getRandom());
            int action = indices.get(chosenIndex);
            if (action < DIRECTION_COUNT) {
                Position targetPosition = position.translate(DIRECTIONS[action][0], DIRECTIONS[action][1]);
                if (targetPosition != null) {
                    executeSilently(new MoveCommand(game), new String[]{targetPosition.toString()});
                }
            } else if (action == DIRECTION_COUNT) {
                blockUnit(board, unit, position);
            } else {
                executeSilently(new MoveCommand(game), new String[]{position.toString()});
            }
        }
    }

    private static void blockUnit(Board board, PlacedUnit unit, Position position) {
        unit.block();
        System.out.printf(StringConstants.FMT_BLOCKS, unit.getName(), position);
        System.out.print(BoardFormatter.formatBoard(board, position));
    }

    private static void endTurn(Game game) {
        Player enemy = game.getEnemyPlayer();
        if (enemy.hasFullHand()) {
            int dropIndex = enemy.pickUnitToDiscard(game.getRandom());
            executeSilently(new YieldCommand(game), new String[]{String.valueOf(dropIndex + 1)});
        } else {
            executeSilently(new YieldCommand(game), new String[0]);
        }
    }

    private static void executeSilently(Command command, String[] arguments) {
        try {
            command.execute(arguments);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
            // Ignored silently for AI
        }
    }

    private record UnitScoreResult(int totalScore, List<Integer> optionScores, List<Integer> validIndices) {}
}