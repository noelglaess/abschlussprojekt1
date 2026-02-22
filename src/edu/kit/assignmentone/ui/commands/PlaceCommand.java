package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.model.units.UnitCombiner;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Command to place one or more units from the hand onto the board.
 *
 * @author Programmieren-Team
 */
public class PlaceCommand extends Command {

    private static final String COMMAND_NAME = "place";
    private static final String COMMAND_REGEX = "place( \\d+)+";
    private static final String ERROR_NO_SELECTION = "No field selected.";
    private static final String ERROR_ALREADY_PLACED = "You have already placed units this turn.";
    private static final String ERROR_INVALID_INDEX = "One or more provided hand indices are invalid.";
    private static final String ERROR_DUPLICATE_INDEX = "An index cannot be provided multiple times.";
    private static final String ERROR_OCCUPIED_BY_ENEMY = "Cannot place on a field occupied by the enemy.";

    public PlaceCommand(Game game) {
        super(COMMAND_NAME, COMMAND_REGEX, game);
    }

    @Override
    public void execute(String[] arguments) {
        Board board = this.game.getBoard();
        Position targetPos = this.game.getSelectedPosition();

        if (targetPos == null) {
            throw new IllegalStateException(ERROR_NO_SELECTION);
        }

        Player active = this.game.getActivePlayerObject();
        if (active.hasPlacedThisTurn()) {
            throw new IllegalStateException(ERROR_ALREADY_PLACED);
        }

        List<Integer> parsedIndices = parseAndValidateIndices(arguments, active.getHand().size());

        Optional<PlacedUnit> existingUnitOpt = board.getUnitAt(targetPos);
        if (existingUnitOpt.isPresent() && existingUnitOpt.get().getOwner() != active.getType()) {
            throw new IllegalStateException(ERROR_OCCUPIED_BY_ENEMY);
        }

        List<Unit> unitsToPlace = new ArrayList<>();
        for (int idx : parsedIndices) {
            unitsToPlace.add(active.getHand().get(idx));
        }

        List<Integer> sortedIndicesDesc = new ArrayList<>(parsedIndices);
        sortedIndicesDesc.sort((a, b) -> b.compareTo(a));
        for (int idx : sortedIndicesDesc) {
            active.removeCardFromHand(idx);
        }

        active.setPlacedThisTurn(true);

        boolean firstCardIsPlaced = false;
        if (existingUnitOpt.isEmpty()) {
            Unit firstToPlace = unitsToPlace.remove(0);
            active.incrementBoardCount();
            board.placeUnit(targetPos, new PlacedUnit(firstToPlace, active.getType()));
            System.out.printf("%s places %s on %s.%n", active.getType().getDisplayName(), firstToPlace.name(), targetPos);
            firstCardIsPlaced = true;
        }

        PlacedUnit targetUnit = board.getUnitAt(targetPos).get();
        for (Unit u : unitsToPlace) {
            if (!firstCardIsPlaced) {
                System.out.printf("%s places %s on %s.%n", active.getType().getDisplayName(), u.name(), targetPos);
            }
            firstCardIsPlaced = false;

            System.out.printf("%s and %s on %s join forces!%n", u.name(), targetUnit.getUnit().name(), targetPos);
            Optional<Unit> combinedOpt = UnitCombiner.tryCombine(u, targetUnit.getUnit());

            if (combinedOpt.isPresent()) {
                System.out.println("Success!");
                targetUnit.setUnit(combinedOpt.get());
            } else {
                System.out.printf("Union failed. %s was eliminated.%n", targetUnit.getUnit().name());
                board.removeUnit(targetPos);
                active.decrementBoardCount();
                break;
            }
        }

        System.out.print(BoardFormatter.formatBoard(board, targetPos));
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