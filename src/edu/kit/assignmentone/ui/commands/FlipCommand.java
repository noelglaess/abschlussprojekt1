package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.StringConstants;
import edu.kit.assignmentone.model.board.Board;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.ui.BoardFormatter;

/**
 * Command to flip the selected unit.
 *
 * @author uqhkm
 * @version 1.0
 */
public class FlipCommand extends Command {

    /**
     * Creates a new flip command.
     * @param game The game instance
     */
    public FlipCommand(Game game) {
        super(StringConstants.CMD_FLIP, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringConstants.ERR_NO_ARGS);
        }
        Game game = this.getGame();
        Board board = game.getBoard();
        Position sel = game.getSelectedPosition();

        if (sel == null || board.isEmpty(sel)) {
            throw new IllegalStateException(StringConstants.ERR_NO_SELECTION);
        }

        PlacedUnit unit = board.getUnitAt(sel).orElseThrow();
        unit.requireValidMove(0, unit);

        if (unit.flipIfCovered()) {
            unit.setMoved(true);
            System.out.printf(StringConstants.FMT_FLIPPED, unit.getName(), unit.getAttack(), unit.getDefense(), sel);
            System.out.print(BoardFormatter.formatBoard(board, sel));
            System.out.println(unit.formatInfo(game));
        }
    }
}