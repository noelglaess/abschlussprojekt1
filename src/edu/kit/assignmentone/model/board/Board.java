package edu.kit.assignmentone.model.board;

import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Represents the 7x7 game board.
 *
 * @author uqhkm
 * @version 1.0
 */
public class Board {

    private static final int PENALTY_FLIPPED = 500;
    private static final int[][] DIRECTIONS_FOUR = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static final int[][] DIRECTIONS_EIGHT = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
    private static final int BOARD_SIZE = 7;
    private final PlacedUnit[][] grid;

    /** Creates a new, empty game board. */
    public Board() {
        this.grid = new PlacedUnit[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Checks if empty.
     * @param position The position
     * @return true if empty
     */
    public boolean isEmpty(Position position) {
        return this.grid[position.column()][position.row()] == null;
    }

    private boolean isOccupiedBy(Position position, PlayerType type) {
        return !isEmpty(position) && this.grid[position.column()][position.row()].getOwner() == type;
    }

    /**
     * Places a unit.
     * @param position The position
     * @param unit The unit
     */
    public void placeUnit(Position position, PlacedUnit unit) {
        if (!isEmpty(position)) {
            throw new IllegalStateException(StringConstants.ERROR_POSITION_OCCUPIED);
        }
        this.grid[position.column()][position.row()] = unit;
    }

    /**
     * Removes a unit.
     * @param position The position
     * @return The removed unit
     */
    public PlacedUnit removeUnit(Position position) {
        int column = position.column();
        int row = position.row();
        PlacedUnit unit = this.grid[column][row];
        this.grid[column][row] = null;
        return unit;
    }

    /**
     * Gets the unit.
     * @param position The position
     * @return An Optional
     */
    public Optional<PlacedUnit> getUnitAt(Position position) {
        return Optional.ofNullable(this.grid[position.column()][position.row()]);
    }

    /**
     * Moves a unit.
     * @param sourcePosition source
     * @param targetPosition target
     */
    public void moveUnit(Position sourcePosition, Position targetPosition) {
        if (isEmpty(sourcePosition)) {
            throw new IllegalStateException(StringConstants.ERROR_NO_UNIT_SOURCE);
        }
        if (!isEmpty(targetPosition)) {
            throw new IllegalStateException(StringConstants.ERROR_TARGET_OCCUPIED);
        }
        PlacedUnit unit = removeUnit(sourcePosition);
        placeUnit(targetPosition, unit);
    }

    /**
     * Finds a unit by name and owner.
     * @param name Name
     * @param owner Owner
     * @return Position or null
     */
    public Position findUnit(String name, PlayerType owner) {
        Position found = null;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                Position position = new Position(column, row);
                Optional<PlacedUnit> unitOptional = getUnitAt(position);
                if (unitOptional.isPresent() && unitOptional.get().getOwner() == owner && unitOptional.get().getName().equals(name)) {
                    found = position;
                    break;
                }
            }
            if (found != null) {
                break;
            }
        }
        return found;
    }

    /**
     * Gets a list of all unmoved enemy units.
     * @return List of positions
     */
    public List<Position> getUnmovedEnemyUnits() {
        List<Position> unmovedList = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                PlacedUnit unit = this.grid[column][row];
                if (unit != null && unit.isUnmovedEnemy()) {
                    unmovedList.add(new Position(column, row));
                }
            }
        }
        return unmovedList;
    }

    private int countUnits(Position position, boolean useEightDirections, PlayerType type, Position excludePosition) {
        int count = 0;
        int[][] directions = useEightDirections ? DIRECTIONS_EIGHT : DIRECTIONS_FOUR;
        for (int[] directionArray : directions) {
            Position positionToCheck = position.translate(directionArray[0], directionArray[1]);
            if (positionToCheck != null && !positionToCheck.equals(excludePosition) && isOccupiedBy(positionToCheck, type)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the highest attack value around.
     * @param position center
     * @return maximum attack
     */
    public int getMaximumSurroundingEnemyAttack(Position position) {
        int maximumAttack = 0;
        for (int[] directionArray : DIRECTIONS_FOUR) {
            Position positionToCheck = position.translate(directionArray[0], directionArray[1]);
            if (positionToCheck != null) {
                Optional<PlacedUnit> unitOptional = getUnitAt(positionToCheck);
                if (unitOptional.isPresent() && unitOptional.get().getOwner() == PlayerType.PLAYER) {
                    maximumAttack = Math.max(maximumAttack, unitOptional.get().getAttack());
                }
            }
        }
        return maximumAttack;
    }

    private int calculateKingScore(Position positionToEvaluate, Position kingPosition) {
        int fellows = countUnits(positionToEvaluate, true, PlayerType.ENEMY, kingPosition);
        int enemies = countUnits(positionToEvaluate, true, PlayerType.PLAYER, null);
        int distance = kingPosition.distanceTo(positionToEvaluate);
        int present = (isOccupiedBy(positionToEvaluate, PlayerType.ENEMY) && !positionToEvaluate.equals(kingPosition)) ? 1 : 0;
        return fellows - 2 * enemies - distance - 3 * present;
    }

    private int calculatePlacementScore(Position positionToEvaluate, Position playerKingPosition) {
        int distance = positionToEvaluate.distanceTo(playerKingPosition);
        int enemies = countUnits(positionToEvaluate, false, PlayerType.PLAYER, null);
        int fellows = countUnits(positionToEvaluate, false, PlayerType.ENEMY, null);
        return -distance + 2 * enemies - fellows;
    }

    /**
     * Evaluates AI target score safely.
     * @param sourcePosition Source
     * @param targetPosition Target (must not be null)
     * @param playerKingPosition Player king
     * @return the score
     */
    public int evaluateTargetScore(Position sourcePosition, Position targetPosition, Position playerKingPosition) {
        int resultScore;
        PlacedUnit unit = getUnitAt(sourcePosition).orElseThrow();
        PlacedUnit targetUnit = getUnitAt(targetPosition).orElse(null);

        if (targetUnit == null) {
            int distance = targetPosition.distanceTo(playerKingPosition);
            int enemies = countUnits(targetPosition, false, PlayerType.PLAYER, null);
            resultScore = 10 - distance - enemies;
        } else {
            PlayerType targetOwner = targetUnit.getOwner();
            int targetAttack = targetUnit.getAttack();
            int targetDefense = targetUnit.getDefense();
            int unitAttack = unit.getAttack();

            if (targetOwner == PlayerType.ENEMY) {
                Optional<Unit> combinedUnitOptional = unit.getUnit().combineWith(targetUnit.getUnit());
                resultScore = combinedUnitOptional.map(combinedUnit -> combinedUnit.attack() + combinedUnit.defense() - unitAttack - unit.getDefense())
                        .orElse(-targetAttack - targetDefense);
            } else if (targetUnit.isKing()) {
                resultScore = unitAttack;
            } else if (!targetUnit.isFlipped()) {
                resultScore = unitAttack - PENALTY_FLIPPED;
            } else if (targetUnit.isBlocking()) {
                resultScore = unitAttack - targetDefense;
            } else {
                resultScore = 2 * (unitAttack - targetAttack);
            }
        }
        return resultScore;
    }

    /**
     * Finds the best King move for AI.
     * @param kingPosition the king
     * @param randomGenerator random
     * @return The position
     */
    public Position findBestKingMove(Position kingPosition, Random randomGenerator) {
        Position bestResult = kingPosition;
        int maximumScore = Integer.MIN_VALUE;
        List<Position> bestOptions = new ArrayList<>();

        for (Position position : kingPosition.getAdjacentAndCenter()) {
            if (Position.isValid(position.column(), position.row()) && !isOccupiedBy(position, PlayerType.PLAYER)) {
                int scoreValue = calculateKingScore(position, kingPosition);
                if (scoreValue > maximumScore) {
                    maximumScore = scoreValue;
                    bestOptions.clear();
                    bestOptions.add(position);
                } else if (scoreValue == maximumScore) {
                    bestOptions.add(position);
                }
            }
        }

        if (bestOptions.size() == 1) {
            bestResult = bestOptions.getFirst();
        } else if (bestOptions.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int index = 0; index < bestOptions.size(); index++) {
                weights.add(1);
            }
            bestResult = bestOptions.get(RandomUtils.weightedRandom(weights, randomGenerator));
        }
        return bestResult;
    }

    /**
     * Finds best placement for AI.
     * @param kingPosition King
     * @param playerKingPosition Player king
     * @param randomGenerator Random
     * @return Position or null
     */
    public Position findBestPlacement(Position kingPosition, Position playerKingPosition, Random randomGenerator) {
        Position bestResult = null;
        int maximumScore = Integer.MIN_VALUE;
        List<Position> bestFields = new ArrayList<>();

        for (int[] directionArray : DIRECTIONS_EIGHT) {
            Position position = kingPosition.translate(directionArray[0], directionArray[1]);
            if (position != null && !isOccupiedBy(position, PlayerType.PLAYER)) {
                int scoreValue = calculatePlacementScore(position, playerKingPosition);
                if (scoreValue > maximumScore) {
                    maximumScore = scoreValue;
                    bestFields.clear();
                    bestFields.add(position);
                } else if (scoreValue == maximumScore) {
                    bestFields.add(position);
                }
            }
        }

        if (bestFields.size() == 1) {
            bestResult = bestFields.getFirst();
        } else if (bestFields.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int index = 0; index < bestFields.size(); index++) {
                weights.add(1);
            }
            bestResult = bestFields.get(RandomUtils.weightedRandom(weights, randomGenerator));
        }
        return bestResult;
    }
}