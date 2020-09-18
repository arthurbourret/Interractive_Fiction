package event;

import game.Console;
import entity.Player;
import java.util.ArrayList;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import entity.Opponent;

public class Battle extends Event {
	protected static final String[] COMMAND;
	private static final String[] FULL_COMMAND;
	public static final String[] ACTION_FIGHT;
	private static final String[] COMMANDE_FIGHT;
	private double escape_chance;
	private Opponent ennemi;
	private static Opponent player;
	@FXML
	private static VBox enemyInfo;

	static {
		COMMAND = new String[] { "escape", "fight" };
		FULL_COMMAND = new String[] { "Escape the enemy", "Fight the enemy" };
		ACTION_FIGHT = new String[] { "att", "spec", "def", "heal", "flee" };
		COMMANDE_FIGHT = new String[] { "Attack the ennemi", "Do a special attack", "Defend for a turn",
				"Take a potion", "Escape the fight" };
	}

	public Battle() {
	}

	public Battle(final String description, final double escape, final Opponent ennemi) {
		super(description, Battle.COMMAND, Battle.FULL_COMMAND);
		this.escape_chance = escape;
		this.ennemi = ennemi;
	}

	public double getEscape_chance() {
		return this.escape_chance;
	}

	public Opponent getEnnemi() {
		return this.ennemi;
	}

	public static Opponent getPlayer() {
		return Battle.player;
	}

	public static void setPlayer(final Opponent player) {
		Battle.player = player;
	}

	public boolean isOver() {
		return Battle.player.getHp() < 1 || this.ennemi.getHp() < 1;
	}

	public static void setEnemyInfo(final VBox enemyInfo) {
		Battle.enemyInfo = enemyInfo;
	}

	public void dispEnemyInfo() {
		Battle.enemyInfo.setVisible(true);
		for (final Node node : Battle.enemyInfo.getChildren()) {
			final String id = node.getId();
			if (id != null) {
				if (id.equals("")) {
					continue;
				}
				if (id.equals("name")) {
					((Text) node).setText(this.getEnnemi().getName());
				}
				if (!id.equals("hp")) {
					continue;
				}
				float hp_percent = this.getEnnemi().getHp() / (float) this.getEnnemi().getStat().getHp();
				final ProgressBar hp_prog = (ProgressBar) node;
				hp_prog.getStyleClass().removeAll("hp-high", "hp-medium", "hp-low");
				if (hp_percent > 0.66) {
					hp_prog.getStyleClass().add("hp-high");
				} else if (hp_percent > 0.33) {
					hp_prog.getStyleClass().add("hp-medium");
				} else {
					hp_prog.getStyleClass().add("hp-low");
				}
				hp_prog.setProgress(hp_percent);
				if (hp_percent < 0.0f) {
					hp_percent = 0.0f;
				}
				((ProgressBar) node).setProgress(hp_percent);
			}
		}
	}

	public void resetBattle() {
		Battle.player.resetDef();
		this.ennemi.resetDef();
		Battle.enemyInfo.setVisible(false);
	}

	public static ArrayList<String> getPossibleFight() {
		final ArrayList<String> possible_action = new ArrayList<String>();
		final boolean hasPotion = ((Player) getPlayer()).hasPotions();
		for (int i = 0; i < Battle.ACTION_FIGHT.length; ++i) {
			if (i != 3 || hasPotion) {
				possible_action.add(Battle.ACTION_FIGHT[i].toLowerCase());
				Console.printText(String.valueOf(Battle.COMMANDE_FIGHT[i]) + " --> " + Battle.ACTION_FIGHT[i],
						"command");
			}
		}
		return possible_action;
	}
}