package entity;

import game.Console;
import event.Battle;
import item.Weapon;

public class Enemy extends Entity {
	private static final double CHANCE_OF_HEALING = 0.8;

	public Enemy(final String name, final String specialAtt, final Stat stat, final Weapon weapon) {
		super(name, specialAtt, stat, weapon);
	}

	public boolean doAction(final String action, final Battle battle) {
		final int hp = super.getHp();
		final int hp_max = super.getStat().getHp();
		final boolean offensive = this.isOffensive(action);
		final Opponent enemy = Battle.getPlayer();
		if (hp < hp_max * 0.2) {
			if (!offensive) {
				return this.flee(battle);
			}
			if (Math.random() > 0.2) {
				return this.heal();
			}
		} else if (hp < hp_max * 0.7) {
			if (offensive) {
				if (Math.random() > 0.7) {
					return this.heal();
				}
				return this.castSpecialAtt(enemy);
			} else if (Math.random() > 0.5) {
				return this.castSpecialAtt(enemy);
			}
		} else if (offensive) {
			return this.castSpecialAtt(enemy);
		}
		return this.attack(enemy);
	}

	@Override
	public boolean heal() {
		Console.printText(String.valueOf(super.getName()) + " try to heal their wond");
		if (Math.random() > Enemy.CHANCE_OF_HEALING) {
			final int hp = (int) (this.getStat().getHp() * 0.2);
			Console.printText("They healed of " + hp + " health point");
			this.setHp(this.getHp() + hp);
			return true;
		}
		Console.printText("They failed");
		return true;
	}

	private boolean isOffensive(final String action) {
		return action.equals(Battle.ACTION_FIGHT[0]) || action.equals(Battle.ACTION_FIGHT[1]);
	}
}
