package game;

import field.DepthPath;
import field.Generation;
import java.io.FileWriter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import field.Dungeon;
import item.Weapon;
import entity.Stat;
import entity.Entity;
import java.util.List;
import event.Battle;
import entity.Enemy;
import event.Chest;
import event.Trap;
import event.Shortcut;
import event.Event;
import field.Room;
import item.Item;
import item.Key;
import item.Potion;
import entity.Player;
import org.jdom2.Element;
import javafx.scene.text.Text;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom2.input.DOMBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.ArrayList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import java.io.File;
import org.jdom2.Document;
import java.io.InputStream;

public class ExternalFile implements DataLoader, DataSaver {
	public final InputStream SAVE_BASE;
	private Document game;
	private Exception e;

	public ExternalFile() {
		this.SAVE_BASE = this.getClass().getResourceAsStream("Save_base.xml");
	}

	@Override
	public File chooseLoadDestination(final String title) {
		final FileChooser fileChooser = this.setFileChooser(title);
		return fileChooser.showOpenDialog(new Stage());
	}

	@Override
	public File chooseSaveDestination(final String title) {
		final FileChooser fileChooser = this.setFileChooser(title);
		fileChooser.setInitialFileName("Save");
		return fileChooser.showSaveDialog(new Stage());
	}

	private FileChooser setFileChooser(final String title) {
		final ArrayList<FileChooser.ExtensionFilter> extensions = new ArrayList<FileChooser.ExtensionFilter>();
		extensions.add(new FileChooser.ExtensionFilter("Game save", new String[] { "*.save" }));
		extensions.add(new FileChooser.ExtensionFilter("XML file", new String[] { "*.xml" }));
		extensions.add(new FileChooser.ExtensionFilter("All Files", new String[] { "*.*" }));
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll((Collection<? extends ExtensionFilter>) extensions);
		return fileChooser;
	}

	@Override
	public void unZip(final String filename) {
		try {
			final byte[] buffer = new byte[1024];
			final ZipInputStream zis = new ZipInputStream(new FileInputStream(filename));
			for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
				final File newFile = newFile(new File(""), zipEntry);
				final FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File newFile(final File destinationDir, final ZipEntry zipEntry) throws IOException {
		final File destFile = new File(destinationDir, zipEntry.getName());
		final String destDirPath = destinationDir.getCanonicalPath();
		final String destFilePath = destFile.getCanonicalPath();
		if (!destFilePath.startsWith(String.valueOf(destDirPath) + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}
		return destFile;
	}

	private Document openXmlIntern(final String filename) {
		return this.openXml(this.getClass().getResourceAsStream(filename));
	}

	private Document openXml(final InputStream filename) {
		Document document = null;
		try {
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final org.w3c.dom.Document reader = dBuilder.parse(filename);
			final DOMBuilder domBuilder = new DOMBuilder();
			document = domBuilder.build(reader);
		} catch (ParserConfigurationException | SAXException | IOException ex2) {
			System.out.println("error on " + filename);
			e.printStackTrace();
		}
		return document;
	}

	@Override
	public ArrayList<Text> loadFileText(final String filename) {
		final ArrayList<Text> text = new ArrayList<Text>();
		final Document reader = this.openXmlIntern(filename);
		final Element root = reader.getRootElement();
		final Text title = new Text(this.getContent(root, "title"));
		title.getStyleClass().add("title");
		text.add(title);
		text.add(new Text(" "));
		final Element content = root.getChild("content");
		for (final Element partie : content.getChildren()) {
			Text temp = null;
			if (partie.getName().equals("enquiry")) {
				temp = new Text(String.valueOf(this.getContent(partie, "line")) + " --> "
						+ this.getContent(partie, "commande"));
				temp.getStyleClass().add("command");
				text.add(temp);
			}
			if (partie.getName().equals("paragraphe")) {
				for (final Element line : partie.getChildren("line")) {
					text.add(new Text(line.getText()));
				}
				text.add(new Text(" "));
			}
		}
		text.add(new Text(" "));
		return text;
	}

	@Override
	public void setGame(final InputStream filename) {
		this.game = this.openXml(filename);
	}

	@Override
	public int getGenerationAttribute(final String name) {
		int attribute = -1;
		if (this.game != null) {
			final Document reader = this.game;
			attribute = this.getInt(reader.getRootElement().getChild("generation"), name);
		}
		return attribute;
	}

	@Override
	public String gameGameRoom(final String option) {
		String id = null;
		if (this.game != null) {
			final Document reader = this.game;
			id = reader.getRootElement().getAttributeValue(option);
		}
		return id;
	}

	@Override
	public Player loadPlayer() {
		Player player = null;
		if (this.game != null) {
			final Document reader = this.game;
			final Element root = reader.getRootElement().getChild("player");
			final int currentHp = this.getInt(root, "hp");
			player = (Player) this.getEntity(root);
			player.setHp(currentHp);
			for (final Element elem : root.getChildren()) {
				Item item = null;
				if (elem.getName().equals("potion")) {
					item = new Potion(this.getInt(elem, "heal"));
				}
				if (elem.getName().equals("key")) {
					item = new Key(this.getInt(elem, "level"));
				}
				player.addItem(item);
			}
		}
		return player;
	}

	@Override
	public ArrayList<Room> loadRooms() {
		final ArrayList<Room> all_room = new ArrayList<Room>();
		if (this.game != null) {
			final Document reader = this.game;
			final List<Element> array = reader.getRootElement().getChild("generation").getChildren();
			for (final Element room : array) {
				final Element event = room.getChild("event");
				final ArrayList<Event> events = new ArrayList<Event>();
				final Element shortcut = this.getElement(event, "shortcut");
				if (shortcut != null) {
					final String desc_short = this.getContent(shortcut, "description");
					final String room_dir = shortcut.getAttributeValue("room");
					events.add(new Shortcut(desc_short, room_dir));
				}
				final Element trap = this.getElement(event, "trap");
				if (trap != null) {
					final String desc_trap = this.getContent(trap, "description");
					final int damage = this.getInt(trap, "damage");
					final double disarm = this.getInt(trap, "disarm") * 0.1;
					final double activate = this.getInt(trap, "activate") * 0.1;
					events.add(new Trap(desc_trap, damage, disarm, activate));
				}
				final Element chest = this.getElement(event, "chest");
				if (chest != null) {
					final String desc_chest = this.getContent(chest, "description");
					final int level = this.getInt(chest, "level");
					final ArrayList<Item> content = new ArrayList<Item>();
					for (final Element item : chest.getChildren()) {
						if (item.getName().equals("potion")) {
							final int heal = this.getInt(item, "heal");
							content.add(new Potion(heal));
						}
						if (item.getName().equals("key")) {
							final int key_level = this.getInt(item, "level");
							content.add(new Key(key_level));
						}
						if (item.getName().equals("weapon")) {
							content.add(this.getWeapon(item));
						}
					}
					events.add(new Chest(desc_chest, level, content));
				}
				final Element battle = this.getElement(event, "battle");
				if (battle != null) {
					final String desc_battle = this.getContent(battle, "description");
					final double escape = this.getInt(battle, "escape") * 0.1;
					final Element enemy = this.getElement(battle, "enemy");
					Enemy enemy_from_xlm = null;
					if (enemy != null) {
						enemy_from_xlm = (Enemy) this.getEntity(enemy);
					}
					final Battle tmp = new Battle(desc_battle, escape, enemy_from_xlm);
					events.add(tmp);
				}
				final String room_id = room.getAttributeValue("id");
				final String room_desc = this.getContent(room, "description");
				final String[] room_dir2 = { room.getAttributeValue("north"), room.getAttributeValue("east"),
						room.getAttributeValue("south"), room.getAttributeValue("west") };
				final boolean visited = Boolean.parseBoolean(room.getAttributeValue("visited"));
				all_room.add(new Room(room_id, room_desc, room_dir2, events, visited));
			}
		}
		return all_room;
	}

	private Element getElement(final Element elem, final String name) {
		if (elem != null && elem.getChild(name) != null) {
			return elem.getChild(name);
		}
		return null;
	}

	private String getContent(final Element elem, final String name) {
		if (elem != null) {
			final Element child = this.getElement(elem, name);
			if (child != null) {
				return child.getText();
			}
		}
		return null;
	}

	private int getInt(final Element elem, final String attribute) {
		return Integer.parseInt(elem.getAttributeValue(attribute));
	}

	private Entity getEntity(final Element elem) {
		final String name = elem.getAttributeValue("name");
		final String speAtt = this.getContent(elem, "description");
		final Weapon weapon = this.getWeapon(elem.getChild("weapon"));
		final Element stat = this.getElement(elem, "stat");
		final int hp = this.getInt(stat, "hp");
		final int att = this.getInt(stat, "att");
		final int def = this.getInt(stat, "def");
		final int speed = this.getInt(stat, "speed");
		if (elem.getName().equals("player")) {
			return new Player(name, speAtt, new Stat(hp, this.getInt(stat, "mp"), att, def, speed), weapon);
		}
		return new Enemy(name, speAtt, new Stat(hp, att, def, speed), weapon);
	}

	private Weapon getWeapon(final Element elem) {
		if (elem != null) {
			final String name = elem.getAttributeValue("name");
			final String specAtt = this.getContent(elem, "description");
			final int damage = this.getInt(elem, "damage");
			return new Weapon(name, specAtt, damage);
		}
		return null;
	}

	@Override
	public void saveGame(final File filename, final Dungeon dungeon) throws IOException {
		final Element root = new Element("game");
		String room_id = dungeon.getCurrent().getId();
		root.setAttribute("position", room_id);
		if (dungeon.getPrevious() != null) {
			room_id = dungeon.getPrevious().getId();
		}
		root.setAttribute("previous", room_id);
		final Player play = dungeon.getPlayer();
		final Element player = new Element("player");
		player.setAttribute("hp", new StringBuilder(String.valueOf(play.getHp())).toString());
		this.addEntity(play, player);
		for (final Potion pot : play.getPotions()) {
			this.addPotion(pot, player);
		}
		for (final Key k : play.getKeychain()) {
			this.addKey(k, player);
		}
		root.addContent(player);
		final Generation gen = dungeon.getMap();
		final Element generation = new Element("generation");
		generation.setAttribute("column", new StringBuilder(String.valueOf(gen.getColumn())).toString());
		generation.setAttribute("line", new StringBuilder(String.valueOf(gen.getLine())).toString());
		generation.setAttribute("type", "0");
		for (final Room r : this.getGeneration(dungeon)) {
			final Element room = new Element("room");
			room.setAttribute("id", r.getId());
			this.setDirAttribute("north", r.getDirection(0), room);
			this.setDirAttribute("east", r.getDirection(1), room);
			this.setDirAttribute("south", r.getDirection(2), room);
			this.setDirAttribute("west", r.getDirection(3), room);
			room.setAttribute("visited", new StringBuilder(String.valueOf(r.isVisited())).toString());
			this.addDesc(r.getDescription(), room);
			final Element event = new Element("event");
			final Shortcut sho = (Shortcut) r.getEvent(new Shortcut());
			if (sho != null && sho.getRoom() != null) {
				final Element shortcut = new Element("shortcut");
				shortcut.setAttribute("room", sho.getRoom().getId());
				this.addDesc(sho.getDesription(), shortcut);
				event.addContent(shortcut);
			}
			final Trap tr = (Trap) r.getEvent(new Trap());
			if (tr != null) {
				final Element trap = new Element("trap");
				trap.setAttribute("activate",
						new StringBuilder(String.valueOf((int) (tr.getActivating_chance() * 10.0))).toString());
				trap.setAttribute("disarm",
						new StringBuilder(String.valueOf((int) (tr.getDisarming_chance() * 10.0))).toString());
				trap.setAttribute("damage", new StringBuilder(String.valueOf(tr.getDamage())).toString());
				this.addDesc(tr.getDesription(), trap);
				event.addContent(trap);
			}
			final Chest ch = (Chest) r.getEvent(new Chest());
			if (ch != null) {
				final Element chest = new Element("chest");
				chest.setAttribute("level", new StringBuilder(String.valueOf(ch.getLevel())).toString());
				this.addDesc(ch.getDesription(), chest);
				for (final Item item : ch.getContent()) {
					if (item instanceof Potion) {
						this.addPotion((Potion) item, chest);
					}
					if (item instanceof Key) {
						this.addKey((Key) item, chest);
					}
					if (item instanceof Weapon) {
						this.addWeapon((Weapon) item, chest);
					}
				}
				event.addContent(chest);
			}
			final Battle bat = (Battle) r.getEvent(new Battle());
			if (bat != null) {
				final Element battle = new Element("battle");
				battle.setAttribute("escape",
						new StringBuilder(String.valueOf((int) (bat.getEscape_chance() * 10.0))).toString());
				this.addDesc(bat.getDesription(), battle);
				final Element enemy = new Element("enemy");
				this.addEntity((Entity) bat.getEnnemi(), enemy);
				battle.addContent(enemy);
				event.addContent(battle);
			}
			if (event.getChildren().size() > 0) {
				room.addContent(event);
			}
			generation.addContent(room);
		}
		root.addContent(generation);
		final Document doc = new Document(root);
		final XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileWriter(filename));
	}

	private ArrayList<Room> getGeneration(final Dungeon dungeon) {
		final DepthPath algo = new DepthPath(dungeon.getMap().getColumn() * dungeon.getMap().getLine() + 2);
		return algo.getGeneration(dungeon.getEnd());
	}

	private void addDesc(final String desc, final Element elem) {
		if (desc != null && !desc.equals("")) {
			final Element description = new Element("description");
			description.setText(desc);
			elem.addContent(description);
		}
	}

	private void setDirAttribute(final String nom, final Room dir, final Element room) {
		if (dir != null && room != null) {
			room.setAttribute(nom, dir.getId());
		}
	}

	private void addPotion(final Potion pot, final Element elem) {
		if (pot != null) {
			final Element potion = new Element("potion");
			potion.setAttribute("heal", new StringBuilder(String.valueOf(pot.getHeal())).toString());
			elem.addContent(potion);
		}
	}

	private void addKey(final Key k, final Element elem) {
		if (k != null) {
			final Element key = new Element("key");
			key.setAttribute("level", new StringBuilder(String.valueOf(k.getLevel())).toString());
			elem.addContent(key);
		}
	}

	private void addWeapon(final Weapon weap, final Element elem) {
		if (weap != null) {
			final Element weapon = new Element("weapon");
			weapon.setAttribute("name", weap.getName());
			weapon.setAttribute("damage", new StringBuilder(String.valueOf(weap.getAttack())).toString());
			this.addDesc(weap.getSpecialAtt(), weapon);
			elem.addContent(weapon);
		}
	}

	private void addEntity(final Entity ent, final Element elem) {
		elem.setAttribute("name", ent.getName());
		this.addDesc(ent.getSecialAtt(), elem);
		final Element stat = new Element("stat");
		stat.setAttribute("hp", new StringBuilder(String.valueOf(ent.getStat().getHp())).toString());
		if (ent.getStat().getMp() > 0) {
			stat.setAttribute("mp", new StringBuilder(String.valueOf(ent.getStat().getMp())).toString());
		}
		stat.setAttribute("att", new StringBuilder(String.valueOf(ent.getStat().getAtt())).toString());
		stat.setAttribute("def", new StringBuilder(String.valueOf(ent.getStat().getDef())).toString());
		stat.setAttribute("speed", new StringBuilder(String.valueOf(ent.getStat().getSpeed())).toString());
		elem.addContent(stat);
		this.addWeapon(ent.getWeapon(), elem);
	}
}
