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

    /** Penalty applied when attacking unflipped units. */
    private static final int FLIPPED_PENALTY = 500;

    private static final int[][] DIR_4 = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static final int[][] DIR_8 = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
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
        return this.grid[position.col()][position.row()] == null;
    }

    /**
     * Checks if the position is occupied by a specific player.
     * @param position The position
     * @param type The player type
     * @return true if occupied by the player
     */
    private boolean isOccupiedBy(Position position, PlayerType type) {
        return !isEmpty(position) && this.grid[position.col()][position.row()].getOwner() == type;
    }

    /**
     * Places a unit.
     * @param position The position
     * @param unit The unit
     */
    public void placeUnit(Position position, PlacedUnit unit) {
        if (!isEmpty(position)) {
            throw new IllegalStateException(StringConstants.ERR_POS_OCCUPIED);
        }
        this.grid[position.col()][position.row()] = unit;
    }

    /**
     * Removes a unit.
     * @param position The position
     * @return The removed unit
     */
    public PlacedUnit removeUnit(Position position) {
        int col = position.col();
        int row = position.row();
        PlacedUnit unit = this.grid[col][row];
        this.grid[col][row] = null;
        return unit;
    }

    /**
     * Gets the unit.
     * @param position The position
     * @return An Optional
     */
    public Optional<PlacedUnit> getUnitAt(Position position) {
        return Optional.ofNullable(this.grid[position.col()][position.row()]);
    }

    /**
     * Moves a unit.
     * @param from source
     * @param to target
     */
    public void moveUnit(Position from, Position to) {
        if (isEmpty(from)) {
            throw new IllegalStateException(StringConstants.ERR_NO_UNIT_SOURCE);
        }
        if (!isEmpty(to)) {
            throw new IllegalStateException(StringConstants.ERR_TARGET_OCCUPIED);
        }
        PlacedUnit unit = removeUnit(from);
        placeUnit(to, unit);
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
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(col, row);
                Optional<PlacedUnit> opt = getUnitAt(pos);
                if (opt.isPresent() && opt.get().getOwner() == owner && opt.get().getName().equals(name)) {
                    found = pos;
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
        List<Position> unmoved = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                PlacedUnit unit = this.grid[col][row];
                if (unit != null && unit.isUnmovedEnemy()) {
                    unmoved.add(new Position(col, row));
                }
            }
        }
        return unmoved;
    }

    /**
     * Counts specific units around a position.
     * @param pos center
     * @param eightDirs true if 8 directions
     * @param type type
     * @param exclude exclude
     * @return amount
     */
    private int countUnits(Position pos, boolean eightDirs, PlayerType type, Position exclude) {
        int count = 0;
        int[][] dirs = eightDirs ? DIR_8 : DIR_4;
        for (int[] dir : dirs) {
            Position check = pos.translate(dir[0], dir[1]);
            if (check != null && !check.equals(exclude) && isOccupiedBy(check, type)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the highest attack value around.
     * @param pos center
     * @return max attack
     */
    public int getMaxSurroundingEnemyAtk(Position pos) {
        int maxAtk = 0;
        for (int[] dir : DIR_4) {
            Position check = pos.translate(dir[0], dir[1]);
            if (check != null) {
                Optional<PlacedUnit> opt = getUnitAt(check);
                if (opt.isPresent() && opt.get().getOwner() == PlayerType.PLAYER) {
                    maxAtk = Math.max(maxAtk, opt.get().getAttack());
                }
            }
        }
        return maxAtk;
    }

    /**
     * Calculates the king score for AI.
     * @param pos Evaluation position
     * @param kingPos Current king pos
     * @return The score
     */
    private int getKingScore(Position pos, Position kingPos) {
        int fellows = countUnits(pos, true, PlayerType.ENEMY, kingPos);
        int enemies = countUnits(pos, true, PlayerType.PLAYER, null);
        int dist = kingPos.distanceTo(pos);
        int present = (isOccupiedBy(pos, PlayerType.ENEMY) && !pos.equals(kingPos)) ? 1 : 0;
        return fellows - 2 * enemies - dist - 3 * present;
    }

    /**
     * Calculates placement score for AI.
     * @param pos The position
     * @param playerKingPos Target king pos
     * @return The score
     */
    private int getPlacementScore(Position pos, Position playerKingPos) {
        int steps = pos.distanceTo(playerKingPos);
        int enemies = countUnits(pos, false, PlayerType.PLAYER, null);
        int fellows = countUnits(pos, false, PlayerType.ENEMY, null);
        return -steps + 2 * enemies - fellows;
    }

    /**
     * Evaluates AI target score safely.
     * @param sourcePos Source pos
     * @param targetPos Target pos (must not be null)
     * @param pKing Player king
     * @return the score
     */
    public int evaluateTargetScore(Position sourcePos, Position targetPos, Position pKing) {
        int resultScore;
        PlacedUnit unit = getUnitAt(sourcePos).orElseThrow();
        PlacedUnit targetUnit = getUnitAt(targetPos).orElse(null);

        if (targetUnit == null) {
            int steps = targetPos.distanceTo(pKing);
            int enemies = countUnits(targetPos, false, PlayerType.PLAYER, null);
            resultScore = 10 - steps - enemies;
        } else {
            PlayerType tOwner = targetUnit.getOwner();
            int tAtk = targetUnit.getAttack();
            int tDef = targetUnit.getDefense();
            int uAtk = unit.getAttack();

            if (tOwner == PlayerType.ENEMY) {
                Optional<Unit> combined = unit.getUnit().combineWith(targetUnit.getUnit());
                resultScore = combined.map(combinedUnit -> combinedUnit.attack() + combinedUnit.defense() - uAtk - unit.getDefense())
                        .orElse(-tAtk - tDef);
            } else if (targetUnit.isKing()) {
                resultScore = uAtk;
            } else if (!targetUnit.isFlipped()) {
                resultScore = uAtk - FLIPPED_PENALTY;
            } else if (targetUnit.isBlocking()) {
                resultScore = uAtk - tDef;
            } else {
                resultScore = 2 * (uAtk - tAtk);
            }
        }
        return resultScore;
    }

    /**
     * Finds the best King move for AI.
     * @param kingPos the king
     * @param rnd random
     * @return The position
     */
    public Position findBestKingMove(Position kingPos, Random rnd) {
        Position bestResult = kingPos;
        int maxScore = Integer.MIN_VALUE;
        List<Position> bestOpts = new ArrayList<>();

        for (Position pos : kingPos.getAdjacentAndCenter()) {
            if (Position.isValid(pos.col(), pos.row()) && !isOccupiedBy(pos, PlayerType.PLAYER)) {
                int scoreVal = getKingScore(pos, kingPos);
                if (scoreVal > maxScore) {
                    maxScore = scoreVal;
                    bestOpts.clear();
                    bestOpts.add(pos);
                } else if (scoreVal == maxScore) {
                    bestOpts.add(pos);
                }
            }
        }

        if (bestOpts.size() == 1) {
            bestResult = bestOpts.getFirst();
        } else if (bestOpts.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestOpts.size(); i++) {
                weights.add(1);
            }
            bestResult = bestOpts.get(RandomUtils.weightedRandom(weights, rnd));
        }
        return bestResult;
    }

    /**
     * Finds best placement for AI.
     * @param kingPos King
     * @param pKing Player king
     * @param rnd Random
     * @return Position or null
     */
    public Position findBestPlacement(Position kingPos, Position pKing, Random rnd) {
        Position bestResult = null;
        int maxScore = Integer.MIN_VALUE;
        List<Position> bestFields = new ArrayList<>();

        for (int[] dir : DIR_8) {
            Position pos = kingPos.translate(dir[0], dir[1]);
            if (pos != null && !isOccupiedBy(pos, PlayerType.PLAYER)) {
                int scoreVal = getPlacementScore(pos, pKing);
                if (scoreVal > maxScore) {
                    maxScore = scoreVal;
                    bestFields.clear();
                    bestFields.add(pos);
                } else if (scoreVal == maxScore) {
                    bestFields.add(pos);
                }
            }
        }

        if (bestFields.size() == 1) {
            bestResult = bestFields.getFirst();
        } else if (bestFields.size() > 1) {
            List<Integer> weights = new ArrayList<>();
            for (int i = 0; i < bestFields.size(); i++) {
                weights.add(1);
            }
            bestResult = bestFields.get(RandomUtils.weightedRandom(weights, rnd));
        }
        return bestResult;
    }
}