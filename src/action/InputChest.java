package action;

import event.Chest;

public class InputChest extends InputSuper
{
    @Override
    public void determineAction(final String action) {
        final Chest chest = (Chest)InputSuper.getDungeon().getCurrent().getEvent(new Chest());
        final String[] chest_actions = chest.getActions();
        for (int i = 0; i < chest_actions.length - 1; ++i) {
            if (action.equals(chest_actions[i])) {
                InputSuper.getDungeon().getPlayer().setWeapon(chest.getWeapon(action));
                InputSuper.getDungeon().getChecker().setPossible_input(chest.getPossibleAction().toArray(new String[0]));
            }
        }
        if (action.equals("close")) {
            InputSuper.endEvent(chest);
        }
    }
}