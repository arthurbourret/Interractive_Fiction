package action;

import item.Weapon;
import game.Console;
import event.Chest;
import event.Battle;
import event.Trap;
import event.Event;

public class InputEvent extends InputSuper
{
    @Override
    public void determineAction(final String action) {
        if (action.equals(Event.EVENT_COMMANDE[4])) {
            this.battle();
        }
        else if (action.equals(Event.EVENT_COMMANDE[0])) {
            this.chest();
        }
        else {
            Event to_remove = null;
            if (action.equals(Event.EVENT_COMMANDE[1])) {
                this.trap();
                to_remove = InputSuper.getDungeon().getCurrent().getEvent(new Trap());
            }
            if (action.equals(Event.EVENT_COMMANDE[2])) {
                this.activateTrap();
                to_remove = InputSuper.getDungeon().getCurrent().getEvent(new Trap());
            }
            if (action.equals(Event.EVENT_COMMANDE[3])) {
                this.escape();
                to_remove = InputSuper.getDungeon().getCurrent().getEvent(new Battle());
            }
            InputSuper.endEvent(to_remove);
        }
    }
    
    private void chest() {
        final Chest chest = (Chest)InputSuper.getDungeon().getCurrent().getEvent(new Chest());
        if (InputSuper.getDungeon().getPlayer().canOpenChest(chest.getLevel())) {
            Console.printText("You open the chest| ");
            for (int i = 0; i < chest.getContent().size(); ++i) {
                Console.printText("You found " + chest.getContent().get(i).getDescription());
                if (!(chest.getContent().get(i) instanceof Weapon)) {
                    InputSuper.getDungeon().getPlayer().addItem(chest.getContent().get(i));
                }
            }
            InputSuper.getDungeon().getChecker().setPossible_input(chest.getPossibleAction());
        }
        else {
            Console.printText("You couldn't open the locker of the chest");
        }
    }
    
    private void trap() {
        final Trap tmp = (Trap)InputSuper.getDungeon().getCurrent().getEvent(new Trap());
        tmp.setActive(false);
        if (Math.random() > tmp.getDisarming_chance()) {
            Console.printText("The trap has been succesfuly disarmed");
        }
        else {
            Console.printText("You failed to disarm the trap");
            Console.printText("You took " + tmp.getDamage() + " hp of damage from the trap");
            InputSuper.getDungeon().getPlayer().hurted(tmp.getDamage());
        }
    }
    
    private void activateTrap() {
        final Trap tmp = (Trap)InputSuper.getDungeon().getCurrent().getEvent(new Trap());
        if (tmp.isActive()) {
            Console.printText("You took " + tmp.getDamage() + " of damage from the trap");
            InputSuper.getDungeon().getPlayer().hurted(tmp.getDamage());
        }
        Console.printText("You eventually managed to escap it");
    }
    
    private void escape() {
        final Battle tmp = (Battle)InputSuper.getDungeon().getCurrent().getEvent(new Battle());
        if (Math.random() > tmp.getEscape_chance()) {
            Console.printText("You managed to escape to the room you came from");
            InputSuper.getDungeon().setCurrent(InputSuper.getDungeon().getPrevious());
        }
        else {
            Console.printText("You failed to escape");
            Console.printText("The enemy attack| ");
            Console.printText("You took " + tmp.getEnnemi().getStat().getAtt() * 10 + " hp of damage");
            InputSuper.getDungeon().getPlayer().hurted(tmp.getEnnemi().getStat().getAtt() * 10);
            Console.printText("and the enemy disapear somewhere");
        }
    }
    
    private void battle() {
        Console.printText("You decide to attack the ennemi before it does");
        Console.printText(" ");
        InputSuper.getDungeon().getChecker().setPossible_input(Battle.getPossibleFight());
        ((Battle)InputSuper.getDungeon().getCurrent().getEvent(new Battle())).dispEnemyInfo();
    }
}