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
 * @author uXXXXX
 * @version 1.0
 */
public class PlaceCommand extends Command {

    /**
     * Creates a new place command.
     * @param game The game instance
     */
    public PlaceCommand(Game game) {
        super(StringConstants.REGEX_PLACE, game);
    }

    @Override
    public void execute(String[] arguments) {
        Game game = this.getGame();
        Board board = game.getBoard();
        Position targetPos = game.getSelectedPosition();

        if (targetPos == null) {
            throw new IllegalStateException(StringConstants.ERR_NO_SEL_PLACE);
        }

        Player active = game.getActivePlayerObject();
        PlayerType activeType = active.getType();

        List<Integer> parsedIndices = parseIndices(arguments, active.getHandSize());
        boolean empty = board.isEmpty(targetPos);

        if (!empty && board.getUnitAt(targetPos).orElseThrow().getOwner() != activeType) {
            throw new IllegalStateException(StringConstants.ERR_OCC_ENEMY);
        }

        List<Unit> unitsToPlace = active.preparePlacement(parsedIndices, empty ? null : activeType);
        boolean firstPlaced = placeFirstIfEmpty(board, active, activeType, targetPos, empty, unitsToPlace);
        performUnions(board, active, activeType, targetPos, unitsToPlace, firstPlaced);

        System.out.print(BoardFormatter.formatBoard(board, targetPos));
    }

    private boolean placeFirstIfEmpty(Board board, Player act, PlayerType type, Position target, boolean empty, List<Unit> units) {
        boolean placed = false;
        if (empty && !units.isEmpty()) {
            Unit first = units.removeFirst();
            act.incrementBoardCount();
            board.placeUnit(target, new PlacedUnit(first, type));
            System.out.printf(StringConstants.FMT_PLACES, type.getDisplayName(), first.name(), target);
            placed = true;
        }
        return placed;
    }

    private void performUnions(Board board, Player active, PlayerType aType, Position targetPos, List<Unit> units, boolean suppressFirst) {
        if (units.isEmpty()) {
            return;
        }
        PlacedUnit targetUnit = board.getUnitAt(targetPos).orElseThrow();
        String tName = targetUnit.getName();
        String aName = aType.getDisplayName();
        boolean suppress = suppressFirst;

        for (Unit unit : units) {
            if (!suppress) {
                System.out.printf(StringConstants.FMT_PLACES, aName, unit.name(), targetPos);
            }
            suppress = false;

            System.out.printf(StringConstants.FMT_JOIN_FORCES, unit.name(), tName, targetPos);
            Optional<Unit> combinedOpt = unit.combineWith(targetUnit.getUnit());

            if (combinedOpt.isPresent()) {
                System.out.println(StringConstants.SUCCESS_MSG);
                targetUnit.setUnit(combinedOpt.get());
                tName = targetUnit.getName();
            } else {
                System.out.printf(StringConstants.FMT_UNION_FAILED, tName);
                board.removeUnit(targetPos);
                active.decrementBoardCount();
                break;
            }
        }
    }

    private List<Integer> parseIndices(String[] arguments, int handSize) {
        List<Integer> parsedIndices = new ArrayList<>();
        for (String arg : arguments) {
            int idx = Integer.parseInt(arg) - 1;
            if (idx < 0 || idx >= handSize) {
                throw new IllegalArgumentException(StringConstants.ERR_INV_IDX);
            }
            if (parsedIndices.contains(idx)) {
                throw new IllegalArgumentException(StringConstants.ERR_DUP_IDX);
            }
            parsedIndices.add(idx);
        }
        return parsedIndices;
    }
}