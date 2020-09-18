package view;

import javafx.scene.text.Font;
import event.Battle;
import event.Chest;
import event.Trap;
import event.Shortcut;
import field.Room;
import field.Generation;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FieldView {
	private static final Color[] EVENT_C;
	private static int r;
	private static int room_r;
	private static int play_r;
	private static int event_r;
	private static int long_l;
	private static int larg_l;
	private GraphicsContext canvas;
	private int width;
	private int height;
	private int depX;
	private int depY;
	private ArrayList<RoomView> generation;

	static {
		EVENT_C = new Color[] { Color.rgb(0, 128, 255), Color.rgb(255, 128, 0), Color.rgb(128, 0, 255),
				Color.rgb(128, 255, 0) };
		FieldView.room_r = 80;
		FieldView.play_r = 10;
		FieldView.event_r = 20;
		FieldView.long_l = 50;
		FieldView.larg_l = 5;
	}

	public FieldView(final GraphicsContext canvas, final double width, final double height, final Generation map) {
		this.canvas = canvas;
		this.width = (int) width;
		this.height = (int) height;
		final int column = map.getColumn() + 2;
		final int line = map.getLine() + 2;
		final double reduction = 0.65;
		if (height < width) {
			FieldView.room_r = (int) (height / (line + (line - 1) * reduction) * 0.8);
		} else {
			FieldView.room_r = (int) (width / (column + (column - 1) * reduction) * 0.8);
		}
		FieldView.event_r = (int) (FieldView.room_r * 0.25);
		FieldView.play_r = (int) (FieldView.event_r * 0.7);
		FieldView.long_l = (int) (FieldView.room_r * reduction);
		FieldView.larg_l = (int) (FieldView.long_l * 0.1);
		FieldView.r = FieldView.room_r + FieldView.long_l;
		this.depX = (int) ((width - FieldView.r * column + FieldView.long_l) * 0.5);
		this.depY = (int) ((height - FieldView.r * line + FieldView.long_l) * 0.5);
		this.generation = new ArrayList<RoomView>();
	}

	public void addRoom(final Room room) {
		if (room != null) {
			room.setVisitedOn();
			final String id = room.getId();
			int posX = -1;
			int posY = -1;
			final boolean[] dir = new boolean[4];
			if (!id.equals("start") && !id.equals("end")) {
				posX = this.getXFromRoom(room);
				posY = this.getYFromRoom(room);
				for (int i = 0; i < room.getDirection().length; ++i) {
					dir[i] = (room.getDirection(i) != null);
				}
			} else if (id.equals("start") || id.equals("end")) {
				final Room[] room_dir = room.getDirection();
				for (int j = 0; j < room_dir.length; ++j) {
					if (room_dir[j] != null) {
						posX = this.getXFromRoom(room_dir[j]) + ((j == 1) ? -1 : 0) + ((j == 3) ? 1 : 0);
						posY = this.getYFromRoom(room_dir[j]) + ((j == 0) ? 1 : 0) + ((j == 2) ? -1 : 0);
						dir[j] = true;
						break;
					}
				}
			}
			this.generation.add(new RoomView(id, posX, posY, dir, this.getEventInRoom(room)));
		}
	}

	public void updateRoom(final Room room) {
		final String id = room.getId();
		for (int i = 0; i < this.generation.size(); ++i) {
			if (id.equals(this.generation.get(i).id)) {
				this.generation.get(i).setEvent(this.getEventInRoom(room));
				break;
			}
		}
	}

	public ArrayList<RoomView> getGeneration() {
		return this.generation;
	}

	private int getXFromRoom(final Room room) {
		return room.getId().charAt(1) - '0';
	}

	private int getYFromRoom(final Room room) {
		return room.getId().charAt(0) - '0';
	}

	private boolean[] getEventInRoom(final Room room) {
		final boolean[] events = { room.getEvent(new Shortcut()) != null, room.getEvent(new Trap()) != null,
				room.getEvent(new Chest()) != null, room.getEvent(new Battle()) != null };
		return events;
	}

	public void dispView(final String idPosP) {
		this.resetScreen();
		this.dispBorder();
		this.dispLegend();
		for (final RoomView room : this.generation) {
			final int x = (room.x() + 1) * FieldView.r;
			final int y = (room.y() + 1) * FieldView.r;
			this.dispRoom(x, y);
			this.dispLinks(room.getDir(), x, y);
			this.dispEvents(room.getEvent(), x, y);
			if (idPosP.equals(room.getId())) {
				this.dispPlayer(x, y);
			}
		}
	}

	private void resetScreen() {
		this.canvas.clearRect(0.0, 0.0, this.width, this.height);
	}

	private void dispBorder() {
		this.canvas.setFill(Color.BLACK);
		this.border(0, 0, this.width, this.height, 5);
	}

	private void dispLegend() {
		this.canvas.setFill(Color.BLACK);
		final int start = 20;
		this.border(start, start, 270, 100, 2);
		this.canvas.setFont(new Font(20.0));
		this.canvas.fillText("Legend", start + 10, start + 25);
		final int event_height = 20;
		this.canvas.setFont(new Font(18.0));
		final String[] legend = { "shortcut", "trap", "chest", "battle" };
		for (int i = 0; i < 4; ++i) {
			final int x = start + 20 + ((i > 1) ? 120 : 0);
			final int y = (int) (start + 40 + event_height * 1.2 * ((i > 1) ? (i - 2) : i));
			this.canvas.setFill(FieldView.EVENT_C[i]);
			this.rectLegend(x, y, event_height);
			this.canvas.setFill(Color.BLACK);
			this.canvas.fillText(": " + legend[i], x + 10 + event_height, y + event_height * 0.75);
		}
	}

	private void rectLegend(final int x, final int y, final int width) {
		this.rect(x - this.depX, y - this.depY, width, width);
	}

	private void dispRoom(final int x, final int y) {
		this.canvas.setFill(Color.gray(0.8));
		this.rect(x, y, FieldView.room_r);
	}

	private void dispLinks(final boolean[] dir, final int x, final int y) {
		this.canvas.setFill(Color.gray(0.5));
		for (int i = 0; i < dir.length; ++i) {
			if (dir[i]) {
				final int depA = (i > 0 && i < 3) ? FieldView.room_r : 0;
				final int signe = (i > 0 && i < 3) ? 1 : -1;
				if (i % 2 == 0) {
					this.rect(x + (FieldView.room_r - FieldView.larg_l) / 2, y + depA, FieldView.larg_l,
							signe * FieldView.long_l);
				} else {
					this.rect(x + depA, y + (FieldView.room_r - FieldView.larg_l) / 2, signe * FieldView.long_l,
							FieldView.larg_l);
				}
			}
		}
	}

	private void dispEvents(final boolean[] events, int x, int y) {
		x += (FieldView.room_r / 2 - FieldView.event_r) / 2;
		y += (FieldView.room_r / 2 - FieldView.event_r) / 2;
		for (int i = 0; i < events.length; ++i) {
			if (events[i]) {
				this.canvas.setFill(FieldView.EVENT_C[i]);
				final int depX = (i > 0 && i < 3) ? (FieldView.room_r / 2) : 0;
				final int depY = (i > 1) ? 0 : (FieldView.room_r / 2);
				this.rect(x + depX, y + depY, FieldView.event_r);
			}
		}
	}

	private void dispPlayer(final int x, final int y) {
		this.canvas.setFill(Color.RED);
		this.rect(x - (FieldView.play_r - FieldView.room_r) / 2, y - (FieldView.play_r - FieldView.room_r) / 2,
				FieldView.play_r);
	}

	private void rect(final int x, final int y, final int width) {
		this.rect(x, y, width, width);
	}

	private void rect(int x, int y, final int width, final int height) {
		if (width < 0) {
			x += width;
		}
		if (height < 0) {
			y += height;
		}
		this.canvas.fillRect(this.depX + x, this.depY + y, Math.abs(width), Math.abs(height));
	}

	private void border(final int x, final int y, final int width, final int height, final int weight) {
		this.canvas.fillRect(x, y, width, weight);
		this.canvas.fillRect(x, y, weight, height);
		this.canvas.fillRect(x, y + height - weight, width, weight);
		this.canvas.fillRect(x + width - weight, y, weight, height);
	}
}