package controller;

import action.InputChest;
import event.Chest;
import action.InputBattle;
import event.Battle;
import action.InputDirection;
import event.Shortcut;
import field.Room;
import action.InputEvent;
import event.Event;
import java.io.InputStream;
import game.Console;
import action.InputSuper;
import field.Dungeon;

public class InputAction
{
    private static Dungeon dungeon;
    private InputSuper in_action;
    
    public InputAction(final Dungeon dungeon) {
        InputSuper.setDungeon(InputAction.dungeon = dungeon);
    }
    
    protected void dispInventory() {
        for (int i = 0; i < InputAction.dungeon.getPlayer().getInventory().size(); ++i) {
            Console.printText(InputAction.dungeon.getPlayer().getInventory().get(i));
        }
    }
    
    protected void restart(final PossibleAction checker, final InputStream filename) {
        Dungeon.resetView();
        (InputAction.dungeon = new Dungeon(filename)).setChecker(checker);
        InputSuper.setDungeon(InputAction.dungeon);
        Dungeon.getView().addRoom(InputAction.dungeon.getStart());
        InputAction.dungeon.setCurrent(InputAction.dungeon.getStart());
        InputAction.dungeon.setPreviousFromXml();
        Dungeon.getView().dispView(InputAction.dungeon.getCurrent().getId());
        InputAction.dungeon.doEvent();
    }
    
    protected static Dungeon getDungeon() {
        return InputAction.dungeon;
    }
    
    public void determineAction(final String action) {
        if (this.isIn(Event.EVENT_COMMANDE, action)) {
            this.in_action = new InputEvent();
        }
        else if (this.isIn(Room.DIRECTION, action) || action.equals(Shortcut.COMMAND[0])) {
            this.in_action = new InputDirection();
        }
        else if (this.isIn(Battle.ACTION_FIGHT, action)) {
            this.in_action = new InputBattle();
        }
        else if (this.isIn(((Chest)InputAction.dungeon.getCurrent().getEvent(new Chest())).getActions(), action)) {
            this.in_action = new InputChest();
        }
        if (this.in_action != null) {
            this.in_action.determineAction(action);
        }
    }
    
    protected boolean isIn(final String[] groupe, final String action) {
        for (int i = 0; i < groupe.length; ++i) {
            if (groupe[i].equals(action)) {
                return true;
            }
        }
        return false;
    }
}