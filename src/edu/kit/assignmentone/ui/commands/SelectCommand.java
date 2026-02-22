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
 * @version 1.0
 */
public class SelectCommand extends Command {

    private static final String COMMAND_REGEX = "select [a-zA-Z]\\d";
    private static final String UNIT_INFO_FORMAT = "%s %s (Team %s)%nATK: %d%nDEF: %d";

    /**
     * Creates a new select command.
     *
     * @param game The game to execute the command on
     */
    public SelectCommand(Game game) {
        super(COMMAND_REGEX, game);
    }

    @Override
    public void execute(String[] arguments) {
        Position pos = Position.fromString(arguments[0]);
        this.getGame().setSelectedPosition(pos);

        System.out.print(BoardFormatter.formatBoard(this.getGame().getBoard(), pos));

        Optional<PlacedUnit> unitOpt = this.getGame().getBoard().getUnitAt(pos);
        if (unitOpt.isPresent()) {
            PlacedUnit unit = unitOpt.get();
            String info = String.format(UNIT_INFO_FORMAT,
                    unit.getName(),
                    unit.getUnit().type().toString(),
                    unit.getOwner().getDisplayName(),
                    unit.getAttack(),
                    unit.getDefense());
            System.out.println(info);
        }
    }
}