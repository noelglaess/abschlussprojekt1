package edu.kit.assignmentone.model;

import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.units.UnitCombiner;
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
 */
public final class AIEngine {

    private static final String KING_NAME = "Farmer King";
    private static final int[][] DIR_4 = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static final int[][] DIR_8 = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
    private static final int HUNDRED = 100;

    private static final int FLIPPED_PENALTY = 500;
    private static final int BASE_SCORE = 10;
    private static final int ENEMY_MULTIPLIER = 2;
    private static final int FELLOW_MULTIPLIER = 3;
    private static final int BOARD_SIZE = 7;
    private static final int MAX_HAND_SIZE = 5;

    private AIEngine() {
        throw new UnsupportedOperationException("Utility classes cannot be instantiated.");
    }

    public static void playTurn(Game game) {
        if (!game.isRunning()) {
            return;
        }
        moveKing(game);
        if (!game.isRunning()) {
            return;
        }
        placeUnit(game);
        moveUnits(game);
        if (game.isRunning()) {
            endTurn(game);
        }
    }

    private static void moveKing(Game game) {
        Board board = game.getBoard();
        Position kingPos = findUnit(board, PlayerType.ENEMY);
        if (kingPos == null) {
            return;
        }

        List<Position> options = new ArrayList<>();
        options.add(new Position(kingPos.col(), kingPos.row() + 1));
        options.add(new Position(kingPos.col() + 1, kingPos.row()));
        options.add(new Position(kingPos.col(), kingPos.row() - 1));
        options.add(new Position(kingPos.col() - 1, kingPos.row()));
        options.add(kingPos);

        int maxScore = Integer.MIN_VALUE;
        List<Position> bestOptions = new ArrayList<>();

        for (Position pos : options) {
            if (!Position.isValid(pos.col(), pos.row())) {
                continue;
            }
            Optional<PlacedUnit> unitOpt = board.getUnitAt(pos);
            if (unitOpt.isPresent() && unitOpt.get().getOwner() == PlayerType.PLAYER) {
                continue;
            }

            int fellows = countUnits(board, pos, DIR_8, PlayerType.ENEMY, kingPos);
            int enemies = countUnits(board, pos, DIR_8, PlayerType.PLAYER, null);
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

        Position chosen = bestOptions.get(0);
        if (bestOptions.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestOptions.size(); i++) {
                weights.add(1);
            }
            int chosenIdx = RandomUtils.weightedRandom(weights, game.getRandom());
            chosen = bestOptions.get(chosenIdx);
        }

        game.setSelectedPosition(kingPos);
        executeCommandSilently(new MoveCommand(game), new String[]{chosen.toString()});
    }

    private static void placeUnit(Game game) {
        Board board = game.getBoard();
        Player enemy = game.getEnemyPlayer();
        if (enemy.getHand().isEmpty() || enemy.getBoardCount() >= enemy.getMaxBoardCapacity()) {
            return;
        }

        Position kingPos = findUnit(board, PlayerType.ENEMY);
        Position playerKingPos = findUnit(board, PlayerType.PLAYER);

        List<Position> validFields = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();

        for (int[] dir : DIR_8) {
            Position pos = new Position(kingPos.col() + dir[0], kingPos.row() + dir[1]);
            if (Position.isValid(pos.col(), pos.row())) {
                Optional<PlacedUnit> unitOpt = board.getUnitAt(pos);
                if (unitOpt.isEmpty() || unitOpt.get().getOwner() == PlayerType.ENEMY) {
                    int steps = pos.distanceTo(playerKingPos);
                    int enemies = countUnits(board, pos, DIR_4, PlayerType.PLAYER, null);
                    int fellows = countUnits(board, pos, DIR_4, PlayerType.ENEMY, null);
                    int score = -steps + ENEMY_MULTIPLIER * enemies - fellows;

                    validFields.add(pos);
                    scores.add(score);
                }
            }
        }

        if (validFields.isEmpty()) {
            return;
        }

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

        Position chosenField = bestFields.get(0);
        if (bestFields.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestFields.size(); i++) {
                weights.add(1);
            }
            chosenField = bestFields.get(RandomUtils.weightedRandom(weights, game.getRandom()));
        }

        List<Integer> handWeights = new ArrayList<>();
        for (Unit u : enemy.getHand()) {
            handWeights.add(u.attack());
        }
        int chosenUnitIdx = RandomUtils.weightedRandom(handWeights, game.getRandom());

        game.setSelectedPosition(chosenField);
        executeCommandSilently(new PlaceCommand(game), new String[]{String.valueOf(chosenUnitIdx + 1)});
    }

    private static void moveUnits(Game game) {
        Board board = game.getBoard();
        Position playerKingPos = findUnit(board, PlayerType.PLAYER);

        while (true) {
            List<Position> unmovedUnits = new ArrayList<>();
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Position p = new Position(col, row);
                    Optional<PlacedUnit> opt = board.getUnitAt(p);
                    if (opt.isPresent() && opt.get().getOwner() == PlayerType.ENEMY
                            && !opt.get().hasMoved() && !opt.get().getUnit().name().equals(KING_NAME)) {
                        unmovedUnits.add(p);
                    }
                }
            }

            if (unmovedUnits.isEmpty()) {
                break;
            }

            Position bestUnitPos = null;
            int bestUnitTotalScore = Integer.MIN_VALUE;
            List<Integer> bestUnitOptionScores = new ArrayList<>();
            List<Integer> bestUnitValidIndices = new ArrayList<>();

            for (Position p : unmovedUnits) {
                PlacedUnit unit = board.getUnitAt(p).get();
                List<Integer> optionScores = new ArrayList<>();
                List<Integer> validIndices = new ArrayList<>();
                int totalScore = 0;

                for (int i = 0; i < 4; i++) {
                    Position target = new Position(p.col() + DIR_4[i][0], p.row() + DIR_4[i][1]);
                    if (!Position.isValid(target.col(), target.row())) {
                        continue;
                    }
                    Optional<PlacedUnit> targetOpt = board.getUnitAt(target);
                    if (targetOpt.isPresent() && targetOpt.get().getUnit().name().equals(KING_NAME) && targetOpt.get().getOwner() == PlayerType.ENEMY) {
                        continue;
                    }

                    int score = 0;
                    if (targetOpt.isEmpty()) {
                        int steps = target.distanceTo(playerKingPos);
                        int enemies = countUnits(board, target, DIR_4, PlayerType.PLAYER, null);
                        score = BASE_SCORE - steps - enemies;
                    } else {
                        PlacedUnit tUnit = targetOpt.get();
                        if (tUnit.getOwner() == PlayerType.ENEMY) {
                            Optional<Unit> combined = UnitCombiner.tryCombine(unit.getUnit(), tUnit.getUnit());
                            if (combined.isPresent()) {
                                score = combined.get().attack() + combined.get().defense() - unit.getUnit().attack() - unit.getUnit().defense();
                            } else {
                                score = -tUnit.getUnit().attack() - tUnit.getUnit().defense();
                            }
                        } else {
                            if (tUnit.getUnit().name().equals(KING_NAME)) {
                                score = unit.getUnit().attack();
                            } else if (!tUnit.isFlipped()) {
                                score = unit.getUnit().attack() - FLIPPED_PENALTY;
                            } else if (tUnit.isBlocking()) {
                                score = unit.getUnit().attack() - tUnit.getUnit().defense();
                            } else {
                                score = ENEMY_MULTIPLIER * (unit.getUnit().attack() - tUnit.getUnit().attack());
                            }
                        }
                    }
                    optionScores.add(score);
                    validIndices.add(i);
                    totalScore += score;
                }

                int maxEnemyAtk = getMaxSurroundingEnemyAtk(board, p);

                int blockScore = Math.max(1, (unit.getUnit().defense() - maxEnemyAtk) / HUNDRED);
                optionScores.add(blockScore);
                validIndices.add(4);
                totalScore += blockScore;

                int inPlaceScore = Math.max(0, (unit.getUnit().attack() - maxEnemyAtk) / HUNDRED);
                optionScores.add(inPlaceScore);
                validIndices.add(5);
                totalScore += inPlaceScore;

                if (totalScore > bestUnitTotalScore) {
                    bestUnitTotalScore = totalScore;
                    bestUnitPos = p;
                    bestUnitOptionScores = optionScores;
                    bestUnitValidIndices = validIndices;
                }
            }

            game.setSelectedPosition(bestUnitPos);
            PlacedUnit activeUnit = board.getUnitAt(bestUnitPos).get();

            boolean hasPositive = false;
            for (int s : bestUnitOptionScores) {
                if (s > 0) {
                    hasPositive = true;
                }
            }

            if (!hasPositive) {
                activeUnit.setBlocking(true);
                activeUnit.setMoved(true);
                System.out.printf("%s (%s) blocks!%n", activeUnit.getUnit().name(), bestUnitPos);
                System.out.print(BoardFormatter.formatBoard(board, bestUnitPos));
            } else {
                int chosenActionIdx = RandomUtils.weightedRandom(bestUnitOptionScores, game.getRandom());
                int actualAction = bestUnitValidIndices.get(chosenActionIdx);

                if (actualAction < 4) {
                    int targetCol = bestUnitPos.col() + DIR_4[actualAction][0];
                    int targetRow = bestUnitPos.row() + DIR_4[actualAction][1];
                    String targetStr = new Position(targetCol, targetRow).toString();
                    executeCommandSilently(new MoveCommand(game), new String[]{targetStr});
                } else if (actualAction == 4) {
                    activeUnit.setBlocking(true);
                    activeUnit.setMoved(true);
                    System.out.printf("%s (%s) blocks!%n", activeUnit.getUnit().name(), bestUnitPos);
                    System.out.print(BoardFormatter.formatBoard(board, bestUnitPos));
                } else if (actualAction == 5) {
                    executeCommandSilently(new MoveCommand(game), new String[]{bestUnitPos.toString()});
                }
            }
        }
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

    private static Position findUnit(Board board, PlayerType owner) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Optional<PlacedUnit> opt = board.getUnitAt(new Position(col, row));
                if (opt.isPresent() && opt.get().getOwner() == owner && opt.get().getUnit().name().equals(KING_NAME)) {
                    return new Position(col, row);
                }
            }
        }
        return null;
    }

    private static int countUnits(Board board, Position pos, int[][] dirs, PlayerType type, Position exclude) {
        int count = 0;
        for (int[] d : dirs) {
            Position check = new Position(pos.col() + d[0], pos.row() + d[1]);
            if (Position.isValid(check.col(), check.row()) && !check.equals(exclude)) {
                Optional<PlacedUnit> opt = board.getUnitAt(check);
                if (opt.isPresent() && opt.get().getOwner() == type) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int getMaxSurroundingEnemyAtk(Board board, Position pos) {
        int maxAtk = 0;
        for (int[] d : DIR_4) {
            Position check = new Position(pos.col() + d[0], pos.row() + d[1]);
            if (Position.isValid(check.col(), check.row())) {
                Optional<PlacedUnit> opt = board.getUnitAt(check);
                if (opt.isPresent() && opt.get().getOwner() == PlayerType.PLAYER) {
                    if (opt.get().getUnit().attack() > maxAtk) {
                        maxAtk = opt.get().getUnit().attack();
                    }
                }
            }
        }
        return maxAtk;
    }

    private static void executeCommandSilently(Command command, String[] args) {
        try {
            command.execute(args);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
            // Error falls back safely
        }
    }
}