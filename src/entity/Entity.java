package entity;

import action.InputSuper;
import event.Battle;
import game.Console;
import item.Weapon;

public abstract class Entity implements Opponent {
	private String name;
	private String secialAtt;
	private int hp;
	private Stat stat;
	private Weapon weapon;

	public Entity(final String name, String specialAtt, final Stat stat, final Weapon weapon) {
		this.name = name;
		if (specialAtt == null || specialAtt.equals("")) {
			specialAtt = "do a special attack";
		}
		this.secialAtt = specialAtt;
		this.stat = stat;
		this.hp = stat.getHp();
		this.setWeapon(weapon);
	}

	@Override
	public String toString() {
		return "Entity [" + this.name + ", special=\"" + this.secialAtt + "\", hp=" + this.hp + "/" + this.stat.getHp()
				+ "]\n Stat " + this.stat + "\n Weapon" + this.weapon;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getSecialAtt() {
		if (this.getWeapon() != null) {
			final String spe = this.getWeapon().getSpecialAtt();
			if (spe != null && !spe.equals("")) {
				return this.getWeapon().getSpecialAtt();
			}
		}
		return this.secialAtt;
	}

	@Override
	public int getHp() {
		return this.hp;
	}

	public void setHp(int hp) {
		if (hp > this.stat.getHp()) {
			hp = this.stat.getHp();
		}
		this.hp = hp;
		if (this.hp < 0) {
			this.hp = 0;
		}
	}

	@Override
	public Stat getStat() {
		return this.stat;
	}

	public Weapon getWeapon() {
		return this.weapon;
	}

	public void setWeapon(final Weapon weapon) {
		this.weapon = weapon;
	}

	@Override
	public boolean attack(final Opponent enemy) {
		Console.printText(String.valueOf(this.getName()) + " do a basic attack");
		return this.infligateDamage(enemy, 1.0);
	}

	@Override
	public boolean castSpecialAtt(final Opponent enemy) {
		Console.printText(String.valueOf(this.getName()) + " " + this.getSecialAtt());
		return this.infligateDamage(enemy, 1.0 + Math.random());
	}

	private boolean infligateDamage(final Opponent enemy, final double multDamage) {
		if (enemy.canEscape()) {
			final int hp = (int) (this.attackDealt(enemy) * multDamage);
			enemy.hurted(hp);
			Console.printText(String.valueOf(enemy.getName()) + " took " + hp + " hp of damage!");
			return true;
		}
		Console.printText(String.valueOf(this.getName()) + " missed!");
		return false;
	}

	private int attackDealt(final Opponent ennemy) {
		int sup_dam = 0;
		if (this.getWeapon() != null) {
			sup_dam = this.getWeapon().getAttack();
		}
		return (int) (Math.random() + this.getStat().getAtt() + sup_dam) * 10 / ennemy.getStat().getDef();
	}

	@Override
	public void defend() {
		Console.printText(String.valueOf(this.name) + " have their defense increase for a turn");
		this.getStat().setDefUp((int) (this.getStat().getDef() * (1.5 + Math.random())));
	}

	@Override
	public void resetDef() {
		this.getStat().setDefUp(this.getStat().getDef());
	}

	@Override
	public void hurted(final int hp) {
		this.setHp(this.getHp() - hp);
	}

	@Override
	public boolean flee(final Battle battle) {
		Console.printText(String.valueOf(this.name) + " try to escape the battle");
		if (this.canEscape()) {
			Console.printText(String.valueOf(this.name) + " escaped succesfuly!");
			battle.resetBattle();
			((Player) Battle.getPlayer()).resetSpec_point();
			InputSuper.endEvent(battle);
			return true;
		}
		Console.printText(String.valueOf(this.name) + " failed to escape");
		return false;
	}

	@Override
	public boolean canEscape() {
		return Math.random() * 50.0 > this.getStat().getSpeed();
	}
}