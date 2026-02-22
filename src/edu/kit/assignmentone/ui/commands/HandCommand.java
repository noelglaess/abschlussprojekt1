package edu.kit.assignmentone.ui.commands;

import edu.kit.assignmentone.model.Game;
import edu.kit.assignmentone.model.player.Player;
import edu.kit.assignmentone.model.units.Unit;

import java.util.List;

public class HandCommand extends Command {

    private static final String COMMAND_NAME = "hand";

    public HandCommand(Game game) {
        super(COMMAND_NAME, game);
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException("The hand command does not take any arguments.");
        }

        Player active = this.getGame().getActivePlayerObject();
        List<Unit> hand = active.getHand();

        for (int i = 0; i < hand.size(); i++) {
            Unit u = hand.get(i);
            System.out.printf("[%d] %s%n(%d/%d)%n", i + 1, u.name(), u.attack(), u.defense());
        }
    }
}