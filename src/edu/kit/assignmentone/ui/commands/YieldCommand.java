package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;

public class YieldCommand extends Command {

    private static final String COMMAND_REGEX = "yield(\\s+\\d+)?";
    private static final String ERROR_MUST_DISCARD = "You hold 5 units and must discard one using 'yield <idx>'.";
    private static final String ERROR_CANNOT_DISCARD = "You hold less than 5 units and cannot discard.";
    private static final String ERROR_INVALID_INDEX = "The provided index is invalid.";
    private static final int FULL_HAND_SIZE = 5;

    public YieldCommand(Game game) {
        super(COMMAND_REGEX, game);
    }

    @Override
    public void execute(String[] arguments) {
        Player active = this.getGame().getActivePlayerObject();
        boolean handIsFull = active.getHand().size() == FULL_HAND_SIZE;

        if (handIsFull && arguments.length == 0) {
            throw new IllegalStateException(ERROR_MUST_DISCARD);
        }
        if (!handIsFull && arguments.length > 0) {
            throw new IllegalStateException(ERROR_CANNOT_DISCARD);
        }

        if (arguments.length > 0) {
            int idx = Integer.parseInt(arguments[0]) - 1;
            if (idx < 0 || idx >= active.getHand().size()) {
                throw new IllegalArgumentException(ERROR_INVALID_INDEX);
            }
            Unit discarded = active.removeCardFromHand(idx);
            System.out.printf("%s discarded %s (%d/%d).%n", active.getType().getDisplayName(), discarded.name(), discarded.attack(), discarded.defense());
        }

        this.getGame().switchTurn();
    }
}