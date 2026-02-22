package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.board.PlacedUnit;
import edu.kit.assignmentone.model.board.Position;
import edu.kit.assignmentone.ui.BoardFormatter;

import java.util.Optional;

/**
 * Command to select a position on the board and show its details.
 *
 * @author Programmieren-Team
 */
public class SelectCommand extends Command {

    private static final String COMMAND_NAME = "select";
    private static final String COMMAND_REGEX = "select [a-zA-Z]\\d";
    private static final String UNIT_INFO_FORMAT = "%s %s (Team %s)%nATK: %d%nDEF: %d";

    /**
     * Creates a new select command.
     *
     * @param game The game to execute the command on
     */
    public SelectCommand(Game game) {
        super(COMMAND_NAME, COMMAND_REGEX, game);
    }

    @Override
    public void execute(String[] arguments) {
        Position pos = Position.fromString(arguments[0]);
        this.game.setSelectedPosition(pos);

        System.out.println(BoardFormatter.formatBoard(this.game.getBoard(), pos));

        Optional<PlacedUnit> unitOpt = this.game.getBoard().getUnitAt(pos);
        if (unitOpt.isPresent()) {
            PlacedUnit pu = unitOpt.get();
            String info = String.format(UNIT_INFO_FORMAT,
                    pu.getUnit().name(),
                    pu.getUnit().type().toString(),
                    pu.getOwner().getDisplayName(),
                    pu.getUnit().attack(),
                    pu.getUnit().defense());
            System.out.println(info);
        }
    }
}