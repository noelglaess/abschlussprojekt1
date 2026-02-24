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

    public PlaceCommand(Game game) {
        super(StringConstants.REGEX_PLACE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game game = this.getGame();
        Board board = game.getBoard();
        Position targetPosition = game.getSelectedPosition();

        if (targetPosition == null) {
            throw new IllegalStateException(StringConstants.ERR_NO_SEL_PLACE);
        }

        Player activePlayer = game.getActivePlayerObject();
        PlayerType activePlayerType = activePlayer.getType();

        List<Integer> parsedIndices = parseIndices(arguments, activePlayer.getHandSize());
        boolean isEmpty = board.isEmpty(targetPosition);

        if (!isEmpty && board.getUnitAt(targetPosition).orElseThrow().getOwner() != activePlayerType) {
            throw new IllegalStateException(StringConstants.ERR_OCC_ENEMY);
        }

        List<Unit> unitsToPlace = activePlayer.preparePlacement(parsedIndices, isEmpty ? null : activePlayerType);
        boolean isFirstPlaced = placeFirstIfEmpty(board, activePlayer, activePlayerType, targetPosition, isEmpty, unitsToPlace);
        performUnions(board, activePlayer, activePlayerType, targetPosition, unitsToPlace, isFirstPlaced);

        System.out.print(BoardFormatter.formatBoard(board, targetPosition));

        // FIX: Druckt jetzt auch die Karten-Info mit aus!
        Optional<PlacedUnit> unitOpt = board.getUnitAt(targetPosition);
        if (unitOpt.isPresent()) {
            System.out.println(unitOpt.get().formatInfo(game));
        } else {
            System.out.println(StringConstants.NO_UNIT);
        }
    }

    private boolean placeFirstIfEmpty(Board board, Player activePlayer, PlayerType playerType, Position targetPosition, boolean isEmpty, List<Unit> unitsToPlace) {
        boolean isPlaced = false;
        if (isEmpty && !unitsToPlace.isEmpty()) {
            Unit firstUnit = unitsToPlace.removeFirst();
            activePlayer.incrementBoardCount();
            board.placeUnit(targetPosition, new PlacedUnit(firstUnit, playerType));
            System.out.printf(StringConstants.FMT_PLACES, playerType.getDisplayName(), firstUnit.fullName(), targetPosition);
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
                System.out.printf(StringConstants.FMT_PLACES, activePlayerName, unitObject.fullName(), targetPosition);
            }
            suppressMessage = false;

            System.out.printf(StringConstants.FMT_JOIN_FORCES, unitObject.fullName(), targetUnitName, targetPosition);
            Optional<Unit> combinedOptional = unitObject.combineWith(targetUnit.getUnit());

            if (combinedOptional.isPresent()) {
                System.out.println(StringConstants.SUCCESS_MSG);
                targetUnit.setUnit(combinedOptional.get());
                targetUnitName = targetUnit.getName();
            } else {
                System.out.printf(StringConstants.FMT_UNION_FAILED, targetUnitName);
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
                throw new IllegalArgumentException(StringConstants.ERR_INV_IDX);
            }
            if (parsedIndices.contains(index)) {
                throw new IllegalArgumentException(StringConstants.ERR_DUP_IDX);
            }
            parsedIndices.add(index);
        }
        return parsedIndices;
    }
}