package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Command to place one or more units from the hand onto the board.
 *
 * @author Programmieren-Team
 * @version 1.0
 */
public class PlaceCommand extends Command {

    private static final String ERROR_NO_SELECTION = "No field selected.";
    private static final String ERROR_ALREADY_PLACED = "You have already placed units this turn.";
    private static final String ERROR_INVALID_INDEX = "One or more provided hand indices are invalid.";
    private static final String ERROR_DUPLICATE_INDEX = "An index cannot be provided multiple times.";
    private static final String ERROR_OCCUPIED_BY_ENEMY = "Cannot place on a field occupied by the enemy.";

    /**
     * Creates a new place command.
     * @param game The game instance
     */
    public PlaceCommand(Game game) {
        super(StringConstants.REGEX_PLACE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Board board = this.getGame().getBoard();
        Position targetPos = this.getGame().getSelectedPosition();

        if (targetPos == null) {
            throw new IllegalStateException(ERROR_NO_SELECTION);
        }

        Player active = this.getGame().getActivePlayerObject();
        if (active.isPlacedThisTurn()) {
            throw new IllegalStateException(ERROR_ALREADY_PLACED);
        }

        List<Integer> parsedIndices = parseAndValidateIndices(arguments, active.getHand().size());
        boolean targetIsEmpty = board.isEmpty(targetPos);

        if (!targetIsEmpty && board.getUnitAt(targetPos).get().getOwner() != active.getType()) {
            throw new IllegalStateException(ERROR_OCCUPIED_BY_ENEMY);
        }

        List<Unit> unitsToPlace = extractUnits(active, parsedIndices);
        active.setPlacedThisTurn(true);

        boolean firstWasPlaced = placeFirstCardIfEmpty(board, active, targetPos, targetIsEmpty, unitsToPlace);
        performUnions(board, active, targetPos, unitsToPlace, firstWasPlaced);

        System.out.print(BoardFormatter.formatBoard(board, targetPos));
    }

    private List<Unit> extractUnits(Player active, List<Integer> parsedIndices) {
        List<Unit> unitsToPlace = new ArrayList<>();
        for (int idx : parsedIndices) {
            unitsToPlace.add(active.getHand().get(idx));
        }

        List<Integer> sortedIndicesDesc = new ArrayList<>(parsedIndices);
        sortedIndicesDesc.sort(Comparator.reverseOrder());
        for (int idx : sortedIndicesDesc) {
            active.removeCardFromHand(idx);
        }
        return unitsToPlace;
    }

    private boolean placeFirstCardIfEmpty(Board board, Player active, Position targetPos, boolean targetIsEmpty, List<Unit> unitsToPlace) {
        if (targetIsEmpty) {
            Unit firstToPlace = unitsToPlace.removeFirst();
            active.incrementBoardCount();
            board.placeUnit(targetPos, new PlacedUnit(firstToPlace, active.getType()));
            System.out.printf(StringConstants.FMT_PLACES, active.getType().getDisplayName(), firstToPlace.name(), targetPos);
            return true;
        }
        return false;
    }

    private void performUnions(Board board, Player active, Position targetPos, List<Unit> unitsToPlace, boolean firstWasPlaced) {
        PlacedUnit targetUnit = board.getUnitAt(targetPos).orElseThrow();
        boolean suppressFirstPrint = firstWasPlaced;

        for (Unit unit : unitsToPlace) {
            if (!suppressFirstPrint) {
                System.out.printf(StringConstants.FMT_PLACES, active.getType().getDisplayName(), unit.name(), targetPos);
            }
            suppressFirstPrint = false;

            System.out.printf(StringConstants.FMT_JOIN_FORCES, unit.name(), targetUnit.getName(), targetPos);
            Optional<Unit> combinedOpt = unit.combineWith(targetUnit.getUnit());

            if (combinedOpt.isPresent()) {
                System.out.println(StringConstants.SUCCESS_MSG);
                targetUnit.setUnit(combinedOpt.get());
            } else {
                System.out.printf(StringConstants.FMT_UNION_FAILED, targetUnit.getName());
                board.removeUnit(targetPos);
                active.decrementBoardCount();
                break;
            }
        }
    }

    private List<Integer> parseAndValidateIndices(String[] arguments, int handSize) {
        List<Integer> parsedIndices = new ArrayList<>();
        for (String arg : arguments) {
            int idx = Integer.parseInt(arg) - 1;
            if (idx < 0 || idx >= handSize) {
                throw new IllegalArgumentException(ERROR_INVALID_INDEX);
            }
            if (parsedIndices.contains(idx)) {
                throw new IllegalArgumentException(ERROR_DUPLICATE_INDEX);
            }
            parsedIndices.add(idx);
        }
        return parsedIndices;
    }
}