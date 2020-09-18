package action;

import field.Room;
import game.Console;
import event.Shortcut;

public class InputDirection extends InputSuper {
    @Override
    public void determineAction(final String action) {
        if (action.equals(Shortcut.COMMAND[0])) {
            this.shortcut();
        }
        else {
            InputSuper.getDungeon().setCurrent(InputSuper.getDungeon().getCurrent().getDirection(this.getPosIn(action)));
        }
        InputSuper.getDungeon().doEvent();
    }
    
    private void shortcut() {
        final Shortcut tmp = (Shortcut)InputSuper.getDungeon().getCurrent().getEvent(new Shortcut());
        if (tmp.getRoom() != null) {
            InputSuper.getDungeon().setCurrent(tmp.getRoom());
        }
        else {
            Console.printText("shortcut unreachable", "error");
        }
    }
    
    private int getPosIn(final String action) {
        for (int i = 0; i < Room.DIRECTION.length; ++i) {
            if (Room.DIRECTION[i].equals(action)) {
                return i;
            }
        }
        return -1;
    }
}