package action;

import event.Event;
import field.Dungeon;

public class InputSuper
{
    private static Dungeon dungeon;
    
    public static void setDungeon(final Dungeon dungeon) {
        InputSuper.dungeon = dungeon;
    }
    
    public static Dungeon getDungeon() {
        return InputSuper.dungeon;
    }
    
    public void determineAction(final String action) {
    }
    
    public static void endEvent(final Event event) {
        InputSuper.dungeon.getCurrent().getEvents().remove(event);
        Dungeon.getView().updateRoom(InputSuper.dungeon.getCurrent());
        Dungeon.getView().dispView(InputSuper.dungeon.getCurrent().getId());
        InputSuper.dungeon.doEvent();
    }
}