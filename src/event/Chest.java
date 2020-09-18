package event;

import game.Console;
import item.Weapon;
import item.Item;
import java.util.ArrayList;

public class Chest extends Event {
	protected static final String[] COMMAND;
	public static final String COMMAND_CLOSE = "close";
	private static final String[] FULL_COMMAND;
	private int level;
	ArrayList<Item> content;
	private String[] actions;

	static {
		COMMAND = new String[] { "open" };
		FULL_COMMAND = new String[] { "Open the chest" };
	}

	public Chest() {
	}

	public Chest(final String description, final int level, final ArrayList<Item> content) {
		super(description, Chest.COMMAND, Chest.FULL_COMMAND);
		this.level = level;
		this.content = content;
		this.actions = new String[0];
	}

	public ArrayList<String> getPossibleAction() {
		final ArrayList<String> possible_action = new ArrayList<String>();
		for (int i = 0; i < this.content.size(); ++i) {
			if (this.content.get(i) instanceof Weapon) {
				final String item_name = this.content.get(i).getName();
				possible_action.add(item_name.toLowerCase());
				Console.printText("Equip " + item_name + "--> " + item_name, "command");
			}
		}
		possible_action.add("close".toLowerCase());
		Console.printText("Close the chest --> close", "command");
		this.actions = new String[possible_action.size()];
		for (int i = 0; i < possible_action.size(); ++i) {
			this.actions[i] = possible_action.get(i);
		}
		return possible_action;
	}

	public String[] getActions() {
		return this.actions;
	}

	public ArrayList<Item> getContent() {
		return this.content;
	}

	public Weapon getWeapon(final String name) {
		for (int i = 0; i < this.content.size(); ++i) {
			if (this.content.get(i) instanceof Weapon) {
				final Weapon current_weapon = (Weapon) this.content.get(i);
				if (current_weapon.getName().toLowerCase().equals(name)) {
					this.content.remove(current_weapon);
					return current_weapon;
				}
			}
		}
		return null;
	}

	public int getLevel() {
		return this.level;
	}
}
