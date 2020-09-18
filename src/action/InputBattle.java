package action;

import entity.Enemy;
import game.Console;
import event.Battle;

public class InputBattle extends InputSuper
{
    @Override
    public void determineAction(final String action) {
        final Battle battle = (Battle)InputSuper.getDungeon().getCurrent().getEvent(new Battle());
        battle.resetBattle();
        if (action.equals(Battle.ACTION_FIGHT[0])) {
            Battle.getPlayer().attack(battle.getEnnemi());
        }
        if (action.equals(Battle.ACTION_FIGHT[1])) {
            Battle.getPlayer().castSpecialAtt(battle.getEnnemi());
        }
        if (action.equals(Battle.ACTION_FIGHT[2])) {
            Battle.getPlayer().defend();
        }
        if (action.equals(Battle.ACTION_FIGHT[3])) {
            Battle.getPlayer().heal();
        }
        if (action.equals(Battle.ACTION_FIGHT[4])) {
            Battle.getPlayer().flee(battle);
        }
        if (battle.isOver()) {
            if (Battle.getPlayer().getHp() > 0) {
                Console.printText("You won!");
            }
            else {
                Console.printText("You lost!");
                InputSuper.getDungeon().getChecker().setPossible_input(new String[0]);
            }
            battle.resetBattle();
            InputSuper.endEvent(battle);
            InputSuper.getDungeon().getPlayer().resetSpec_point();
        }
        else if (InputSuper.getDungeon().getCurrent().getEvent(new Battle()) != null) {
            battle.dispEnemyInfo();
            if (!((Enemy)battle.getEnnemi()).doAction(action, battle)) {
                Console.printText(" ");
                battle.getEnnemi().defend();
            }
        }
    }
}

