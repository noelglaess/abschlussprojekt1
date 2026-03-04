package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.player.PlayerType;
import edu.kit.assignmentone.model.units.Unit;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Command to place one or more units from the hand onto the board.
 *
 * @author uqhkm
 * @version 1.0
 */
public class PlaceCommand extends Command {

    /**
     * Creates a new place command.
     * @param game The game instance
     */
    public PlaceCommand(Game game) {
        super(StringConstants.PATTERN_PLACE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game currentGame = this.getGame();
        Board board = currentGame.getBoard();
        Position targetPosition = currentGame.getSelectedPosition();

        if (targetPosition == null) {
            throw new IllegalStateException(StringConstants.ERROR_NO_SELECTION_PLACE);
        }

        Player activePlayer = currentGame.getActivePlayerObject();
        PlayerType activePlayerType = activePlayer.getType();

        List<Integer> parsedIndices = parseIndices(arguments, activePlayer.getHandSize());
        boolean isFieldEmpty = board.isEmpty(targetPosition);

        if (!isFieldEmpty && board.getUnitAt(targetPosition).orElseThrow().getOwner() != activePlayerType) {
            throw new IllegalStateException(StringConstants.ERROR_OCCUPIED_BY_ENEMY);
        }

        List<Unit> unitsToPlace = activePlayer.preparePlacement(parsedIndices, isFieldEmpty ? null : activePlayerType);
        boolean isFirstPlaced = placeFirstIfEmpty(board, activePlayer, activePlayerType, targetPosition, isFieldEmpty, unitsToPlace);
        performUnions(board, activePlayer, activePlayerType, targetPosition, unitsToPlace, isFirstPlaced);

        System.out.print(BoardFormatter.formatBoard(board, targetPosition));

        Optional<PlacedUnit> unitOptional = board.getUnitAt(targetPosition);
        if (unitOptional.isPresent()) {
            System.out.println(unitOptional.get().formatInfo(currentGame));
        } else {
            System.out.println(StringConstants.NO_UNIT_SELECTED);
        }
    }

    private boolean placeFirstIfEmpty(Board board, Player activePlayer, PlayerType playerType, Position targetPosition, boolean isFieldEmpty, List<Unit> unitsToPlace) {
        boolean isPlaced = false;
        if (isFieldEmpty && !unitsToPlace.isEmpty()) {
            Unit firstUnit = unitsToPlace.removeFirst();
            activePlayer.incrementBoardCount();
            board.placeUnit(targetPosition, new PlacedUnit(firstUnit, playerType));
            System.out.printf(StringConstants.FORMAT_PLACES, playerType.getDisplayName(), firstUnit.fullName(), targetPosition);
            isPlaced = true;
        }
        return isPlaced;
    }

    private void performUnions(Board board, Player activePlayer, PlayerType activePlayerType, Position targetPosition, List<Unit> unitsToPlace, boolean suppressFirstMessage) {
        if (unitsToPlace.isEmpty()) {
            return;
        }
        PlacedUnit targetUnit = board.getUnitAt(targetPosition).orElseThrow();
        String targetUnitName = targetUnit.getName();
        String activePlayerName = activePlayerType.getDisplayName();
        boolean suppressMessage = suppressFirstMessage;

        for (Unit unitObject : unitsToPlace) {
            if (!suppressMessage) {
                System.out.printf(StringConstants.FORMAT_PLACES, activePlayerName, unitObject.fullName(), targetPosition);
            }
            suppressMessage = false;

            System.out.printf(StringConstants.FORMAT_JOIN_FORCES, unitObject.fullName(), targetUnitName, targetPosition);
            Optional<Unit> combinedOptional = unitObject.combineWith(targetUnit.getUnit());

            if (combinedOptional.isPresent()) {
                System.out.println(StringConstants.SUCCESS_MESSAGE);
                targetUnit.setUnit(combinedOptional.get());
                targetUnitName = targetUnit.getName();
            } else {
                System.out.printf(StringConstants.FORMAT_UNION_FAILED, targetUnitName);
                board.removeUnit(targetPosition);
                activePlayer.decrementBoardCount();
                break;
            }
        }
    }

    private List<Integer> parseIndices(String[] arguments, int handSize) {
        List<Integer> parsedIndices = new ArrayList<>();
        for (String argument : arguments) {
            int index = Integer.parseInt(argument) - 1;
            if (index < 0 || index >= handSize) {
                throw new IllegalArgumentException(StringConstants.ERROR_INVALID_INDEX);
            }
            if (parsedIndices.contains(index)) {
                throw new IllegalArgumentException(StringConstants.ERROR_DUPLICATE_INDEX);
            }
            parsedIndices.add(index);
        }
        return parsedIndices;
    }
}