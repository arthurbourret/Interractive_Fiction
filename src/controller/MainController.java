package controller;

import javafx.application.Platform;
import java.io.File;
import game.DataSaver;
import java.io.IOException;
import game.DataLoader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import game.ExternalFile;
import game.Console;
import entity.Stat;
import entity.Player;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.Node;
import view.FieldView;
import javafx.stage.Screen;
import event.Battle;
import game.Log;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import field.Dungeon;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class MainController {
	public static final String[] POSSIBLE_INPUT;
	private InputChecker checker;
	private InputAction actionHandler;
	@FXML
	private VBox container;
	@FXML
	private TextField input;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Canvas myCanvas;
	@FXML
	private VBox playerInfo;
	@FXML
	private VBox enemyInfo;

	static {
		POSSIBLE_INPUT = new String[] { "inv", "help", "restart", "exit" };
	}

	public void initialize(final Dungeon dungeon) {
		this.input.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent event) {
				switch (event.getCode()) {
				case ENTER: {
					MainController.this.valid();
					break;
				}
				case UP: {
					MainController.this.inputUp();
					break;
				}
				case DOWN: {
					MainController.this.inputDown();
					break;
				}
				default: {
					Log.reset();
					break;
				}
				}
			}
		});
		Log.initialize();
		this.checker = new InputChecker();
		this.actionHandler = new InputAction(dungeon);
		dungeon.setChecker(this.checker);
		Battle.setEnemyInfo(this.enemyInfo);
		this.myCanvas.setWidth(Screen.getPrimary().getBounds().getWidth() * 0.55);
		this.myCanvas.setHeight(Screen.getPrimary().getBounds().getHeight() * 0.85);
		Dungeon.setView(new FieldView(this.myCanvas.getGraphicsContext2D(), this.myCanvas.getWidth(),
				this.myCanvas.getHeight(), dungeon.getMap()));
		dungeon.setCurrent(dungeon.getStart());
		this.dispPlayerInfo();
		for (final Node node : this.playerInfo.getChildren()) {
			final String id = node.getId();
			if (id != null) {
				if (id.equals("")) {
					continue;
				}
				if (id.equals("name")) {
					((Text) node).setText(dungeon.getPlayer().getName());
				}
				if (!id.equals("speed")) {
					continue;
				}
				((Text) node).setText("Speed : \t\t" + dungeon.getPlayer().getStat().getSpeed());
			}
		}
		dungeon.doEvent();
	}

	private void inputUp() {
		this.input.setText(Log.upPos());
	}

	private void inputDown() {
		this.input.setText(Log.downPos());
	}

	private void dispPlayerInfo() {
		final Player play = InputAction.getDungeon().getPlayer();
		final Stat stat = play.getStat();
		for (final Node node : this.playerInfo.getChildren()) {
			final String id = node.getId();
			if (id != null) {
				if (id.equals("")) {
					continue;
				}
				if (id.equals("hp")) {
					final float hp_percent = play.getHp() / (float) stat.getHp();
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
				}
				if (id.equals("mp")) {
					((ProgressBar) node).setProgress(play.getMp() / (float) stat.getMp());
				}
				if (id.equals("att")) {
					((Text) node).setText("Attack :\t\t"
							+ (stat.getAtt() + ((play.getWeapon() != null) ? play.getWeapon().getAttack() : 0)));
				}
				if (id.equals("def")) {
					((Text) node).setText("Defence :\t\t" + stat.getDefUp());
				}
				if (!id.equals("weapon")) {
					continue;
				}
				if (play.getWeapon() != null) {
					((Text) node).setText(new StringBuilder().append(play.getWeapon()).toString());
				} else {
					((Text) node).setText("");
				}
			}
		}
	}

	public void valid() {
		final String attempt = this.input.getText();
		this.input.setText("");
		if (!attempt.equals("")) {
			this.checkText(attempt);
		} else {
			Console.printText("empty input", "error");
		}
	}

	public void checkText(final String attempt) {
		Log.reset();
		Log.addLog(attempt);
		if (this.checker.isSaisieValid(attempt.trim().toLowerCase())) {
			Console.printText("> " + attempt, "input");
			this.determineAction(attempt.trim().toLowerCase());
		} else {
			Console.printText("invalid input", "error");
		}
		this.scrollPane.setVvalue(this.scrollPane.getVmax());
		this.dispPlayerInfo();
	}

	public void load() {
		final DataLoader loader = new ExternalFile();
		FileInputStream filename = null;
		try {
			filename = new FileInputStream(loader.chooseLoadDestination("Load file"));
			this.container.getChildren().clear();
			this.actionHandler.restart(this.checker, filename);
			Console.printText("Game loaded");
		} catch (FileNotFoundException e) {
			Console.printText("could not load", "error");
			e.printStackTrace();
		}
	}

	public void save() {
		if (!this.checker.isSaisieValid(Battle.ACTION_FIGHT[0])) {
			final DataSaver save = new ExternalFile();
			try {
				final File filename = save.chooseSaveDestination("Save file");
				if (filename != null) {
					save.saveGame(filename, InputAction.getDungeon());
					Console.printText("Game saved at " + filename + "|" + " ");
				}
			} catch (IOException e) {
				Console.printText("probleme in saving", "error");
				e.printStackTrace();
			}
		} else {
			Console.printText("can not save in battle", "error");
		}
	}

	private void determineAction(final String action) {
		if (this.actionHandler.isIn(MainController.POSSIBLE_INPUT, action)) {
			if (action.equals(MainController.POSSIBLE_INPUT[0])) {
				this.actionHandler.dispInventory();
			}
			if (action.equals(MainController.POSSIBLE_INPUT[1])) {
				Console.printText(this.checker.getPossible_input().toString());
			}
			if (action.equals(MainController.POSSIBLE_INPUT[2])) {
				this.container.getChildren().clear();
				this.actionHandler.restart(this.checker, new ExternalFile().SAVE_BASE);
			}
			if (action.equals(MainController.POSSIBLE_INPUT[3])) {
				Platform.exit();
				System.exit(0);
			}
		} else {
			this.actionHandler.determineAction(action);
		}
		Console.printText(" ");
	}

	public void dispText(final Text txt) {
		this.container.getChildren().add(txt);
	}
}
